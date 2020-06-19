package com.github.supermoonie.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.format.support.FormattingConversionService;

import java.time.format.DateTimeFormatter;

/**
 * @author supermoonie
 * @date 2020-06-11
 */
@Configuration
public class DateTimeConfig {

    /**
     * <a>https://www.baeldung.com/spring-date-parameters</a>
     */
    @Bean
    public FormattingConversionService conversionService() {
        DefaultFormattingConversionService conversionService =
                new DefaultFormattingConversionService(false);
        DateTimeFormatterRegistrar registrar = new DateTimeFormatterRegistrar();
        registrar.setDateFormatter(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        registrar.setDateTimeFormatter(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        registrar.registerFormatters(conversionService);
        return conversionService;
    }
}
