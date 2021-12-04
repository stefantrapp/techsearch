package de.fernunihagen.techsearch.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Klasse, die das Cross-Origin Resource Sharing (CORS) so festlegt, dass die API auch 
 * von einem anderen Port aus augerufen werden kann. Das wird für die Entwicklung
 * benötigt, wenn die Angular-Anwendung auf einem anderen Port als die
 * Sptring-Boot-Anwendung läuft. 
 * 
 */

@Configuration
public class CorsConfiguration implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        /* Dadurch kann auf die API auch von anderen URL bzw. Ports zugegriffen werden. */
        registry.addMapping("/api/**").allowedMethods("GET", "POST");
    }
}