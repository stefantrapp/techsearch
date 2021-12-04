package de.fernunihagen.techsearch.data;

/**
 * Analog zu {@link ApplicationJobEnum}. Notwendig für die API, weil ApplicationJobEnum eine Klasse und kein echter Enum ist
 */
public enum ApplicationJobName {
    REBUILD_SEARCH_INDEX, 
    REBUILD_WORD2VEC_MODEL, 
    REBUILD_DOC2VEC_MODEL, 
    REBUILD_SENT2VEC_MODEL, 
    PREPROCESS_DOCUMENTS, 
    PROCESS_CRAWL_JOBS,
    BULK_IMPORT_BATCH
}
