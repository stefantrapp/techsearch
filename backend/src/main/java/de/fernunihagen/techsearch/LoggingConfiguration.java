package de.fernunihagen.techsearch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * Klasse um einen Logger in ein Bean zu injecten
 *
 */
@Configuration
public class LoggingConfiguration {

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public Logger logger(final InjectionPoint injectionPoint) {
        
        @SuppressWarnings("rawtypes")
        final Class loggerClass;
        
        var methodParameter = injectionPoint.getMethodParameter();
        
        if (null != methodParameter) { /* Für Konstruktorinjection */
            loggerClass = methodParameter.getContainingClass();
        } else { /* Für Feldinjection */
            loggerClass = injectionPoint.getField().getDeclaringClass();
        }
        
        return LoggerFactory.getLogger(loggerClass);
    }
}
