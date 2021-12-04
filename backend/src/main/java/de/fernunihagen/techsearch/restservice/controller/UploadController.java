package de.fernunihagen.techsearch.restservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import de.fernunihagen.techsearch.restservice.ApiResponse;
import de.fernunihagen.techsearch.restservice.dtos.UploadDataDto;
import de.fernunihagen.techsearch.restservice.dtos.UploadResponseDto;
import de.fernunihagen.techsearch.restservice.services.UploadService;

@RestController
public class UploadController {

	@Autowired
	private UploadService uploadService;
	

	@PostMapping("/api/upload/upload-file")
    public ApiResponse<UploadResponseDto> uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("uploadData") UploadDataDto uploadData) {
	    try {
	    	uploadService.upload(file, uploadData);
	    	var uploadResponseDto = new UploadResponseDto();
	    	uploadResponseDto.setSaved(true);
	    	return new ApiResponse<UploadResponseDto>(uploadResponseDto);
	    } catch (Exception e) {
	        var response = new ApiResponse<UploadResponseDto>();
	        response.setError(true);
	        response.setErrorMessage(e.toString());
	        return response;
	    }
	}
}
