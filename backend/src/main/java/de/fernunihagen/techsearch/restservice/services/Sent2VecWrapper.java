package de.fernunihagen.techsearch.restservice.services;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import de.fernunihagen.techsearch.data.Language;

public class Sent2VecWrapper {

    private String fastTextExecutablePath;
    private String modelPath;

    private OutputStreamWriter outStream;

    private int maxSecWaitTime = 10;
    private int maxBufferLength = 1000000;
    
    private String lastOutputLines = "";
    private Process queryProcess;
    private Thread inputStreamReaderThread;
    
    @Autowired
    private Logger log;
    
	private String sentencesPath;
	private Process createProcess;

	private StringBuilder inputStreamBuffer;
	private Language language;
	
	public Sent2VecWrapper(Sent2VecConfig sent2VecConfig, Language language) {
		this.language = language;
		this.fastTextExecutablePath = sent2VecConfig.getExecutablePath();
        this.modelPath = sent2VecConfig.getModelPath();
        this.sentencesPath = sent2VecConfig.getSentencesPath();
    }

	private boolean loadModel() {
        log.info("Sent2Vec-Model wird geladen.");
    	List<String> commands = new ArrayList<String>();
        commands.add(fastTextExecutablePath);
        commands.add("nnSent");
        
        var modelPathCmd = modelPath;
        if (!modelPath.toLowerCase().endsWith(".bin")) {
        	/* Beim Erzeugen ergänzt sent2vec .bin automatisch. Deshalb muss man es hier anhängen. */
        	modelPathCmd = modelPath + ".bin";
        }
        
        commands.add(getLocalizedPath(modelPathCmd)); 
        commands.add(getLocalizedPath(sentencesPath));
        
        ProcessBuilder pb = new ProcessBuilder(commands);
        
        log.debug("Kommandozeile für sent2vec: " + pb.command());
        
        pb.redirectErrorStream(true);
        
        try {
            queryProcess = pb.start();
            
            inputStreamReaderThread = createInputStreamReaderThread(queryProcess);
            outStream = new OutputStreamWriter(queryProcess.getOutputStream());
            
            var readResult = waitForReady();
            if (readResult.isReady()) {
            	log.info("Sent2Vec-Model bereit.");
            	return true;
            } else {
            	var status = readResult.getOutput();
            	
            	log.warn("Sent2Vec-Model konnte nicht geladen werden." + (status != null ? " Ausgabe: " + status : ""));
            	return false;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
	
	private String getLocalizedPath(String path) {
		return path.replace("{@Lang}", language.toString());
	}
	
	public void startCreateModel() {
        log.info("Sent2Vec-Model wird erzeugt.");
        List<String> commands = new ArrayList<String>();
        commands.add(fastTextExecutablePath);
        commands.add("sent2vec");
        commands.add("-input");
        commands.add(getLocalizedPath(this.sentencesPath));
        commands.add("-output");
        
        var modelPathCmd = this.modelPath;
        if (modelPathCmd.toLowerCase().endsWith(".bin")) {
        	modelPathCmd = modelPathCmd.substring(modelPathCmd.length() - 4);
        }
        
        commands.add(getLocalizedPath(modelPathCmd));
        
        //var parts = "-minCount 8 -dim 70 -epoch 9 -lr 0.2 -wordNgrams 2 -loss ns -neg 10 -thread 20 -t 0.000005 -dropoutK 4 -minCountLabel 20 -bucket 4000000 -maxVocabSize 750000 -numCheckPoints 10".split(" ");
        //var parts = "-thread 20 -t 0.000005 -dropoutK 4 -minCountLabel 20 -bucket 4000000 -maxVocabSize 750000 -numCheckPoints 10".split(" ");
        //for (var part : parts) {
        //	commands.add(part);
        //}
        
        ProcessBuilder pb = new ProcessBuilder(commands);
        
        log.debug("Kommandozeile für sent2vec: " + pb.command());
        
        pb.redirectErrorStream(true);
        
        try {
            createProcess = pb.start();
            
            inputStreamReaderThread = createInputStreamReaderThread(createProcess);
            outStream = new OutputStreamWriter(createProcess.getOutputStream());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
	
	public String getCreateModelStatus() {
		var result = "";
		if (inputStreamBuffer != null) {
			result = inputStreamBuffer.toString();
		
			if (result.length() == 0) {
				result = lastOutputLines;
			}
		}
		
		return result;
	}
	
	public void clearCreateModelStatus() {
		inputStreamBuffer.setLength(0);
	}
	
	public CreateModelStatus isCreateModelFinished() {
		var isProcessAlive = createProcess.isAlive();
		Integer exitCode = null;
		if (!isProcessAlive) {
			exitCode = createProcess.exitValue();
		}
		
		var isinputStreamReaderThreadAlice = inputStreamReaderThread.isAlive();
		
		boolean finished = !isProcessAlive && !isinputStreamReaderThreadAlice;
		
		return new CreateModelStatus(finished, exitCode);
	}
    
	public static class CreateModelStatus {
		private boolean finished;
		private Integer exitCode;
		
		CreateModelStatus(boolean finished, Integer exitCode) {
			this.finished = finished;
			this.exitCode = exitCode;
			
		}

		public boolean isFinished() {
			return finished;
		}

		public int getExitCode() {
			return exitCode;
		}
	}
	
    public void quit() {
        inputStreamReaderThread.interrupt();
        
        var processes = new ArrayList<Process>();
        
        if (queryProcess != null) {
            processes.add(queryProcess);
        }
        
        if (createProcess != null) {
            processes.add(createProcess);
        }
        
        for (var process : processes) {
        
            if (SystemUtils.IS_OS_LINUX) {
                var pid = process.pid();
                
                var killCommand = "kill -S SIGINT " + pid;
                
                try {
        			Runtime.getRuntime().exec(killCommand);
        		} catch (IOException e) {
        		    process.destroy();
        		}
            } else {
                if (outStream != null) {
                    try {
                        outStream.write("@\r\n");
                        outStream.flush();
                    } catch (IOException e) {
                        log.warn("Fehler beim Beenden von sent2vec: " + e);
                    }
                }
            }
            
            try {
                process.waitFor(1, TimeUnit.SECONDS);
    		} catch (InterruptedException e) {
    			/* Dann eben nicht warten... */
    		}
            
            if (process.isAlive()) {
                process.destroy();
            }
        }
    }
    
    private Thread createInputStreamReaderThread(final Process process) {
    	var inputStream = process.getInputStream();
    	
    	inputStreamReaderThread = new Thread(new Runnable() {
			public void run() {
                try {
                    int c;
                
                    inputStreamBuffer = new StringBuilder(); 
                    
                    while ((c = inputStream.read()) != -1) {
                    	if (c == 10) { /* Neue Zeile */
                            if (lastOutputLines == null) {
                            	lastOutputLines = "";
                            }
                        	lastOutputLines = lastOutputLines += inputStreamBuffer.toString() + "\n";
                        	
                            inputStreamBuffer.setLength(0);
                        } else if (c == 13) { 
                        	/* auch eine Neue Zeile, es reicht wenn 10 ausgewertet wird */
                        } else if (c > 31) { // Keine Steuerzeichen o. ä.
                            inputStreamBuffer.append((char) c);
                            
                            if (inputStreamBuffer.length() > maxBufferLength) { /* Damit der Puffer nicht unendlich groß wird. */
                            	inputStreamBuffer.delete(0, maxBufferLength - 2000);
                            }
                        }
                    }
                    
                    if (lastOutputLines == null) {
                    	lastOutputLines = "";
                    }
                    
                	lastOutputLines = lastOutputLines += inputStreamBuffer.toString();
                } catch (IOException e) {
                	log.error("Fehler beim Lesen der Ausgabe von Sent2Vec", e);
                }
            }
        });
        inputStreamReaderThread.start();
        return inputStreamReaderThread;
    }

    public String search(String query) {
        if (queryProcess == null) {
        	if (!loadModel()) {
        		return null;
        	}
        }
    	
    	try {
            while (lastOutputLines != null && !lastOutputLines.trim().endsWith("Query sentence?")) {
                System.out.println("lastOutput: " + lastOutputLines);
                Thread.sleep(500);
            }
            
            lastOutputLines = "";
            
            log.debug("Stelle Anfrage an Sent2Vec: " + query);
            outStream.write(query + "\r\n");
            outStream.flush();
            
            var result = waitForReady();
            
            log.debug("Ergebnis von Anfrage '" + query + "':\n" + result.getOutput());
            
            return result.getOutput();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    private WaitResult waitForReady() throws InterruptedException {
    	var start = System.currentTimeMillis();
        
        var maxMillisWaitTime = maxSecWaitTime * 1000;
    	
        var result = new WaitResult();
        boolean ready = false;
        
    	while (lastOutputLines == null || !(ready = lastOutputLines.trim().endsWith("Query sentence?"))) {
            Thread.sleep(500);
            var now =  System.currentTimeMillis();
            
            if (now - start > maxMillisWaitTime) {
            	log.warn("Nach " + maxSecWaitTime + " Sekunden keine Antwort erhalten. Es wird nicht mehr weiter gewartet.");
            	break;
            }
        }
    	
    	result.setReady(ready);
    	
    	result.setOutput(lastOutputLines);
		 
    	return result;
    }
    
    private static class WaitResult {
    	private boolean ready;
    	
    	private String output;
    	
    	public WaitResult() {
		}

		public boolean isReady() {
			return ready;
		}

		public String getOutput() {
			return output;
		}

		public void setReady(boolean ready) {
			this.ready = ready;
		}

		public void setOutput(String output) {
			this.output = output;
		}
		
		
    }
}
