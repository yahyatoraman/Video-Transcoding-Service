package com.vts.domain;

import java.util.UUID;

import com.vts.domain.Enums.Format;
import com.vts.domain.Enums.Resolution;

public class Chunk {

	private String chunkId;
	private Format format;
	private Resolution resolution;
	private String startTimestamp;
	private String endTimestamp;

	public Chunk(Format format, Resolution resolution, String startTimestamp, String endTimestamp) {
		this.chunkId = UUID.randomUUID().toString();
		this.format = format;
		this.resolution = resolution;
		this.startTimestamp = startTimestamp;
		this.endTimestamp = endTimestamp;
	}

	// To achieve cleanliness in the controller
	public Chunk(String format, String resolution, String startTimestamp, String endTimestamp) {
		this.chunkId = UUID.randomUUID().toString();
		this.format = Format.valueOf(format);
		this.resolution = Resolution.valueOf(resolution);
		this.startTimestamp = startTimestamp;
		this.endTimestamp = endTimestamp;
	}

	public String getChunkId() {
		return chunkId;
	}

	public void setChunkId(String chunkId) {
		this.chunkId = chunkId;
	}

	public Format getFormat() {
		return format;
	}

	public void setFormat(Format format) {
		this.format = format;
	}

	public Resolution getResolution() {
		return resolution;
	}

	public void setResolution(Resolution resolution) {
		this.resolution = resolution;
	}

	public String getStartTimestamp() {
		return startTimestamp;
	}

	public void setStartTimestamp(String startTimestamp) {
		this.startTimestamp = startTimestamp;
	}

	public String getEndTimestamp() {
		return endTimestamp;
	}

	public void setEndTimestamp(String endTimestamp) {
		this.endTimestamp = endTimestamp;
	}

	@Override
	public String toString() {
		return "Chunk [chunkId=" + chunkId + ", format=" + format + ", resolution=" + resolution + ", startingSecond="
				+ startTimestamp + ", endingSecond=" + endTimestamp + "]";
	}

}
