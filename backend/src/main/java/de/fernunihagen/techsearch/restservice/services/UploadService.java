package de.fernunihagen.techsearch.restservice.services;

import org.springframework.web.multipart.MultipartFile;

import de.fernunihagen.techsearch.restservice.dtos.UploadDataDto;

public interface UploadService {
	public void upload(MultipartFile file, UploadDataDto uploadData);
}
