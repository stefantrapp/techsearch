package de.fernunihagen.techsearch;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * Klasse, die beim Starten der Spring-Boot-Anwendung, das Python-Backen startet und beim beenden 
 * der Anwendung auch wieder beendet. 
 *
 */

@Component
public class SecondaryPythonProcess {

    @Autowired
    private Logger log;

    private Process process;
    
    @PostConstruct
    public void start() {
        ProcessBuilder pb = new ProcessBuilder();
        
        var curDir = new File("").getAbsolutePath();
        
        System.out.println(curDir);
        
        pb.directory(new File("..\\backend-python"));
        pb.command("..\\backend-python\\run.cmd");
        try {
            process = pb.start();
            
            createStreamReaderThread(process.getInputStream(), false);
            createStreamReaderThread(process.getErrorStream(), true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    @PreDestroy
    public void stop() {
        if (process != null) {
            log.info("Beende Python-Prozess...");
            URL url;
            try {
                url = new URL("http://localhost:5000/api/shutdown/");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.setConnectTimeout(1000);
                con.setReadTimeout(1000);
                try {
                    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    in.close();
                } catch (SocketTimeoutException ste) {
                    // Das ist ok. Der Prozess beendet sich sofort.
                } catch (SocketException se) {
                    // Das ist ok. Der Prozess beendet sich sofort.
                }

                con.disconnect();
            } catch (Exception e) {
                log.warn("Python-Prozess konnte nicht per API beendet werden. Der Prozess wird hart beendet.", e);
                process.destroy();
            }
            
            if (process.isAlive()) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (process.isAlive()) {
                    process.destroy();
                }
            }
        }
    }
    
    private Thread createStreamReaderThread(InputStream inputStream, boolean errorStream) {
        BufferedReader reader = new BufferedReader (new InputStreamReader(inputStream));
        
        var inputStreamReaderThread = new Thread(new Runnable() {
            public void run() {
                try {
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        if (errorStream) {
                            log.error(line);
                        } else {
                            log.debug(line);
                        }
                    }
                } catch (IOException e) {
                    log.error("Fehler beim Lesen der Ausgabe von Python", e);
                }
            }
        });
        inputStreamReaderThread.start();
        return inputStreamReaderThread;
    }
    
}
