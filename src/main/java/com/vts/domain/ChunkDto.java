package com.vts.domain;

import org.springframework.web.multipart.MultipartFile;

public class ChunkDto {
	
	private MultipartFile file;
	private String formats;
	private String resolutions;
	private int interval;
	
	public MultipartFile getFile() {
		return file;
	}
	public void setFile(MultipartFile file) {
		this.file = file;
	}
	public String getFormats() {
		return formats;
	}
	public void setFormats(String formats) {
		this.formats = formats;
	}
	public String getResolutions() {
		return resolutions;
	}
	public void setResolutions(String resolutions) {
		this.resolutions = resolutions;
	}
	public int getInterval() {
		return interval;
	}
	public void setInterval(int interval) {
		this.interval = interval;
	}
	
	
}
