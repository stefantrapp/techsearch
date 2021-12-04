package de.fernunihagen.techsearch.restservice.services;

import de.fernunihagen.techsearch.data.BulkImportJob;

public interface EpoImportService {

	void importEpoFile(String filename, BulkImportJob bulkImportJob, EpoImportCallback epoImportCallback);

	boolean test();
	
}
