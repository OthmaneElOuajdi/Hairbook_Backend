package com.hairbook.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import java.util.Locale;

/**
 * Configuration de l'internationalisation (i18n) de l'application.
 * <p>
 * Supporte le français (fr) et l'anglais (en).
 * La langue est déterminée par l'en-tête HTTP Accept-Language.
 */
@Configuration
public class I18nConfig implements WebMvcConfigurer {

    /**
     * Configure le résolveur de locale basé sur l'en-tête Accept-Language.
     * Langue par défaut : français (fr).
     */
    @Bean
    public LocaleResolver localeResolver() {
        AcceptHeaderLocaleResolver localeResolver = new AcceptHeaderLocaleResolver();
        localeResolver.setDefaultLocale(Locale.FRENCH);
        return localeResolver;
    }

    /**
     * Configure la source des messages i18n.
     * Les fichiers de messages sont dans src/main/resources/messages_{locale}.properties
     */
    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setUseCodeAsDefaultMessage(true);
        return messageSource;
    }

    /**
     * Intercepteur permettant de changer la langue via un paramètre de requête.
     * Exemple : ?lang=en
     */
    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
        interceptor.setParamName("lang");
        return interceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
    }
}
