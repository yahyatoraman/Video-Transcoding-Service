package com.vts.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.vts.domain.AttributePair;
import com.vts.domain.Chunk;
import com.vts.domain.ChunkDto;
import com.vts.domain.Enums.Format;
import com.vts.domain.Enums.Resolution;
import com.vts.domain.Interval;

//We want client-side to proceed to next page where the progression is displayed
//If not enabled, process.waitFor functions block the thread
//Therefore client waits for all the operations to end before it receives response

@Service
public class TranscodingService {
	
	@Value("${directory.base}")
	private String BASE;
	
	@Value("${directory.ffmpeg}")
	private String ffmpeg;
	
	@Value("${directory.ffprobe}")
	private String ffprobe;
	
	@Autowired S3Service s3Service;
	
	public void transcodeAndUploadToS3(ChunkDto chunkDto) throws Exception {
		
		MultipartFile file = chunkDto.getFile();
		
		// Create new folder for each request
		String folderName = UUID.randomUUID().toString();
		String outputFolderPath = BASE + folderName + "/";
		Runtime.getRuntime().exec("mkdir " + outputFolderPath);
		
		// Transfer uploaded file to the newly created folder
		String originalVideoFullPath = outputFolderPath + file.getOriginalFilename();
		Files.copy(file.getInputStream(), Paths.get(originalVideoFullPath));
		
		// LOCAL: Create a new inner folder for the chunks
		String innerFolderPath = outputFolderPath + "inner/";
		Runtime.getRuntime().exec("mkdir " + innerFolderPath);
		// S3: Create a folder within the root of the bucket
		s3Service.createFolderWithinBucket(folderName);
		
		// Create video chunks within the inner folder and save to local
		// Chunks are basic objects holding properties -> format, resolution, time intervals
		List<Chunk> chunks = createChunks(chunkDto, originalVideoFullPath);
		for (int i = 0; i < 2; i++) {
			System.out.println(String.format("%s / %s", i+1, chunks.size()));
			
			Chunk chunk = chunks.get(i);
			
			String fileNameWithExt = chunk.getChunkId() + "." + chunk.getFormat();
			String outputFullPath = innerFolderPath + fileNameWithExt;
			List<String> command = createCommand(chunk, outputFullPath, originalVideoFullPath);
			
			ProcessBuilder builder = new ProcessBuilder(command);
			Process process = builder.start();
			process.waitFor();

			s3Service.uploadChunk(folderName, fileNameWithExt, new File(outputFullPath));
			
		}

	}
	

	private List<String> createCommand(Chunk chunk, String outputFullPath, String originalVideoFullPath) {
		
		List<String> command = new ArrayList<String>();
		
		command.add(ffmpeg);
		command.add("-i");
		command.add(originalVideoFullPath);
		command.add("-s");
		command.add(chunk.getResolution().toString().substring(1));
		command.add("-ss");
		command.add(chunk.getStartTimestamp());
		command.add("-to");
		command.add(chunk.getEndTimestamp());
		command.add("-strict");
		command.add("-2");
		command.add(outputFullPath);
		
		return command;
		
	}

	private List<Chunk> createChunks(ChunkDto chunkDto, String originalVideoFullPath) throws Exception {

		List<Interval> intervals = createIntervals(getVideoLength(originalVideoFullPath), chunkDto.getInterval());
		List<AttributePair> attributePairs = createAttributePairs(chunkDto);

		List<Chunk> chunks = new ArrayList<Chunk>();
		for (Interval interval : intervals) {
			for (AttributePair pair : attributePairs) {
				chunks.add(new Chunk(pair.getFormat(), pair.getResolution(), secondsToTimestamp(interval.getStart()),
						secondsToTimestamp(interval.getEnd())));
			}
		}

		return chunks;
	}

	public static List<Interval> createIntervals(int length, int interval) throws Exception {

		if (length <= 0 || interval <= 0 || interval > length) {
			throw new Exception("Intervals are not appropriate");
		}

		List<Interval> intervals = new ArrayList<Interval>();

		int limit;
		if (length % interval == 0) {
			limit = length / interval;
		} else {
			limit = (length / interval) + 1; // get the last bit from video
		}

		for (int i = 0; i < limit; i++) {
			intervals.add(new Interval(i * interval, (i + 1) * interval));
		}

		return intervals;
	}

	private List<AttributePair> createAttributePairs(ChunkDto chunkDto) {
		List<AttributePair> pairs = new ArrayList<AttributePair>();

		for (String format : chunkDto.getFormats().split("/")) {
			for (String resolution : chunkDto.getResolutions().split("/")) {
				pairs.add(new AttributePair(Format.valueOf(format), Resolution.valueOf(resolution)));
			}
		}

		return pairs;
	}

	private String secondsToTimestamp(int seconds) {
		long input = seconds;
		long hh = (input - input % 3600) / 3600;
		long mm = (input % 3600 - input % 3600 % 60) / 60;
		long ss = input % 3600 % 60;
		return hh + ":" + mm + ":" + ss;
	}

	// Returns video length in seconds
	private int getVideoLength(String originalVideoFullPath) throws IOException {
		
		Process process = Runtime.getRuntime().exec(ffprobe + " -v error -show_entries format=duration"
				+ "  -of default=noprint_wrappers=1:nokey=1 " + originalVideoFullPath);
		
		InputStream is = process.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		String output = reader.readLine();
		
		return Integer.valueOf(output.split("\\.")[0]);
	}

}
