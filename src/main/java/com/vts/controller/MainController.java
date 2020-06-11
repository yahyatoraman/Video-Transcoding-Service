package com.vts.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.vts.domain.ChunkDto;
import com.vts.service.TranscodingService;

@Controller
public class MainController {
	
	@Autowired TranscodingService transcodingService;
	
	@GetMapping("/")
	public String mainPage(Model model) {

		ChunkDto chunkDto = new ChunkDto();
		model.addAttribute("chunkDto", chunkDto);
		
		return "main-page";
		
	}
	
	@PostMapping("/")
	public String postDetails(@ModelAttribute("chunkDto") ChunkDto chunkDto) throws Exception {
		
		transcodingService.transcodeAndUploadToS3(chunkDto);
		
		return "final";
	}

}
