package de.fernunihagen.techsearch;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

/**
 * Startklasse für die Sptring-Boot-Anwendung
 */

@SpringBootApplication
public class TechSearchApplication {

    private static ConfigurableApplicationContext context;
    
	public static void main(String[] args) {
	    checkEncoding();
	    
	    SpringApplication app = new SpringApplication(TechSearchApplication.class);
    
	    context = app.run(args);
	}
	
	private static void checkEncoding() {
	    /* Damit kann das Encoding geprüft wernde, das wirklich beim Start der JVM gesetzt war und
	     * nicht evtl. Nachträglich geändert wurde. Nachträgliche Änderungen greifen nicht überall. */
	    byte [] byteArray = {' '}; 
	    InputStream inputStream = new ByteArrayInputStream(byteArray); 
	    InputStreamReader reader = new InputStreamReader(inputStream); 
	    String defaultEncoding = reader.getEncoding();
        
	    if (!defaultEncoding.toLowerCase().equals("utf8")) {
	        throw new RuntimeException("Bitte das Encoding beim Starten der JVM auf UTF-8 setzen. Es ist auf " + defaultEncoding + " gesetzt. Die JVM mit folgenden Parameter starten: -Dfile.encoding=UTF-8");
	    }
    }

    @Bean
	public SchedulerFactoryBean schedulerFactory() {
	  SchedulerFactoryBean factoryBean = new SchedulerFactoryBean();
			
	  return factoryBean;
	}
	
	public static void restart() {
        ApplicationArguments args = context.getBean(ApplicationArguments.class);

        Thread thread = new Thread(() -> {
            context.close();
            context = SpringApplication.run(TechSearchApplication.class, args.getSourceArgs());
        });

        thread.setDaemon(false);
        thread.start();
    }
}
