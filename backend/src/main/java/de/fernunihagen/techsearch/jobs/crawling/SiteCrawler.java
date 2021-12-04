package de.fernunihagen.techsearch.jobs.crawling;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fernunihagen.techsearch.data.CrawlJob;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

/**
 * Klasse für einen WebCrawler von crawler4j.
 */
public class SiteCrawler extends WebCrawler {

    private static Logger log = LoggerFactory.getLogger(SiteCrawler.class); 
    
	private Set<String> seedHosts = new HashSet<String>();
    
	public SiteCrawler(CrawlJob crawlJob, CrawlingConfig config) {
		
        storagePath = Path.of(config.getTempStoragePath(), Integer.toString(crawlJob.getId())).normalize().toString();
        
		try {
            for (var url : crawlJob.getUrls()) {
                var seedHost = getHost(url);
                seedHosts.add(seedHost);
            }
		} catch (Exception e) {
            throw new RuntimeException(e);
        }
	}


    protected String getHost(String url) throws MalformedURLException {
        URL seedUrl = new URL(url);
        var seedHost = seedUrl.getHost().toLowerCase() + ":" + seedUrl.getPort();
        return seedHost;
    }

    /**
     * Es werden nur Seiten besucht, die unter- bzw. innerhalb der angegeben Seeds liegen. 
     */

    @Override
    public boolean shouldVisit(Page referringPage, WebURL webUrl) {
	    try {
    	    var host = getHost(webUrl.getURL());
    	    
            boolean visit = seedHosts.contains(host);
            
            return visit;
	    } catch (Exception e) {
	        throw new RuntimeException(e);
	    }
    }
	
	@Override
    public void visit(Page page) {
        WebURL webURL = page.getWebURL();

        if (page.getParseData() instanceof HtmlParseData) {
            HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
            
            String html = htmlParseData.getHtml();
            
            var pagePath = webURL.getPath();
            
            if (pagePath != null) {
                pagePath = pagePath.toLowerCase();
            }
            
            pagePath = ensureValidFilename(pagePath);
            
            if (pagePath.length() == 0) {
                pagePath = "page.html";
            }
        
            if (!pagePath.endsWith(".html") || !pagePath.endsWith(".html")) {
                pagePath = pagePath + ".html";
            }
            
            Path path = null;
            
            int writeRetries = 0;
            
            while (true) {
                while (true) {
                    pagePath = ensureUniqueFilename(storagePath, pagePath);
                        
                    path = Path.of(storagePath, pagePath);
                    
                    if (path.toString().length() > 100) { /* Windows hat mit langen Pfaden Probleme */
                        pagePath = "page.html";
                    } else {
                        break;
                    } 
                }
    
                
                try {
                    Files.writeString(path, html, StandardOpenOption.CREATE_NEW);
                    break;
                } catch (FileAlreadyExistsException e) {
                    /* Die Exception ignorieren. In der Schleife wird nochmal versucht einen neuen Namen zu finden.
                     * Das kann vorkommen, wenn zwei Threads versuchen gleichzeitig eine Datei mit dem gleichen
                     * Namen zu schreiben. */
                    writeRetries++;
                    if (writeRetries > 100) {
                        /* Hier kann etwas nicht stimmen */
                        log.error("Wiederholte Exception beim Schreiben der Datei: " + path + ": " + e);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
	}
	
	private static final String validChars = "abcdefghijklmnoptstuvwxyzäöü0123456789.";
	
	private static Map<String, Integer> fileNameCounter = new ConcurrentHashMap<String, Integer>();

    private String storagePath;
	
	private String ensureUniqueFilename(String directory, String fileName) {
        
	    var path = Path.of(directory, fileName);
	    
	    var newFileName = fileName;
	    
	    if (Files.exists(path)) {
	        var dotIndex = fileName.lastIndexOf(".");
	        
	        var base = fileName.substring(0, dotIndex);
	        var extension = fileName.substring(dotIndex);
	        
	        int counter = 2;
	        
	        var chachedCount = fileNameCounter.get(fileName);
            if (chachedCount != null) {
                counter = chachedCount; 
            }
	        
	        while (true) {
	            newFileName = base + "_" + counter + extension;
	            if (!Files.exists(Path.of(directory, newFileName))) {
	                
	                if (counter > 10) {
	                    /* Manche Seite verwedenen immer die gleiche "Datei", z. B index.html und nur weitere Parameter um die Seiten zu unterscheiden.
	                     * Dann würde immer wieder hochgezählt werden, deshalb die letzten Zähler werken */
	                    fileNameCounter.put(fileName, counter);
	                }
	                
	                break;
	            }
	            counter++;
	        }
	    }
        
        return newFileName;
    }
	
	private String ensureValidFilename(String fileName) {
        if (fileName == null) {
            return "";
        }
        
	    StringBuilder builder = new StringBuilder();
	    for (int i = 0; i < fileName.length(); i++) {
            char c = fileName.charAt(i);
            if (validChars.indexOf(c) != -1) {
                builder.append(c);
            }
        }
        
	    var result = builder.toString();
	    
	    return result;
    }
}
