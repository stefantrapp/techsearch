package de.fernunihagen.techsearch.data;

import java.util.HashMap;

import de.fernunihagen.techsearch.StartupApplicationListener;

/**
 * Enum für die verschiedenen Jobs innerhalb der Anwendung.
 */

public class ApplicationJobEnum {

    private static HashMap<String, ApplicationJobEnum> enums = new HashMap<String, ApplicationJobEnum>();

    /** Wenn hier neue Instanzen angelegt werden, diese in der Klasse {@link StartupApplicationListener} analog zu den Vorhandenen nachtragen.  */
    
    public static ApplicationJobEnum REBUILD_SEARCH_INDEX = new ApplicationJobEnum(ApplicationJobName.REBUILD_SEARCH_INDEX.toString());
    public static ApplicationJobEnum REBUILD_WORD2VEC_MODEL = new ApplicationJobEnum(ApplicationJobName.REBUILD_WORD2VEC_MODEL.toString());
    public static ApplicationJobEnum REBUILD_DOC2VEC_MODEL = new ApplicationJobEnum(ApplicationJobName.REBUILD_DOC2VEC_MODEL.toString());
    public static ApplicationJobEnum REBUILD_SENT2VEC_MODEL = new ApplicationJobEnum(ApplicationJobName.REBUILD_SENT2VEC_MODEL.toString());
    public static ApplicationJobEnum PREPROCESS_DOCUMENTS = new ApplicationJobEnum(ApplicationJobName.PREPROCESS_DOCUMENTS.toString());
    public static ApplicationJobEnum PROCESS_CRAWL_JOBS = new ApplicationJobEnum(ApplicationJobName.PROCESS_CRAWL_JOBS.toString());
    public static ApplicationJobEnum BULK_IMPORT_BATCH = new ApplicationJobEnum(ApplicationJobName.BULK_IMPORT_BATCH.toString());
    
    private String name;
    
    private ApplicationJobEnum(String name) {
        this.name = name;
        enums.put(name, this);
    }
    
    public String getName() {
        return name;
    }
    
    public static ApplicationJobEnum fromName(String name) {
        return enums.get(name);
    }
    
    @Override
    public int hashCode() {
        return super.hashCode();
    }
    
    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof ApplicationJobEnum))
            return false;
        ApplicationJobEnum other = (ApplicationJobEnum)o;
        
        return this.name.equals(other.name);
    }
    
    @Override
    public String toString() {
        return super.toString() + " " + name;
    }
    
}
