package com.vts.domain;

import com.vts.domain.Enums.Format;
import com.vts.domain.Enums.Resolution;

public class AttributePair {

	private Format format;
	private Resolution resolution;
	
	public AttributePair(Format format, Resolution resolution) {
		this.format = format;
		this.resolution = resolution;
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
	
	
	
	
}
