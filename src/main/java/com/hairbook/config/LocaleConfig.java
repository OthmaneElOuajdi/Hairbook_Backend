package com.hairbook.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Configuration pour le support multilingue (français, néerlandais, anglais).
 * <p>
 * Gère la détection de la locale via l'en-tête `Accept-Language` et permet de
 * la
 * changer via un paramètre d'URL (`?lang=...`).
 * Les locales supportées sont centralisées dans la classe interne
 * {@link SupportedLocales}.
 */
@Configuration
public class LocaleConfig implements WebMvcConfigurer {

    /**
     * Centralise les locales supportées par l'application.
     */
    public static final class SupportedLocales {
        public static final Locale DEFAULT = Locale.forLanguageTag("fr-BE");
        public static final List<Locale> ALL = Arrays.asList(
                DEFAULT, // Français (Belgique)
                Locale.forLanguageTag("nl-BE"), // Néerlandais (Belgique)
                Locale.forLanguageTag("en-US"), // Anglais (US)
                Locale.FRENCH, // Français (générique)
                Locale.forLanguageTag("nl"), // Néerlandais (générique)
                Locale.ENGLISH // Anglais (générique)
        );

        private SupportedLocales() {
        }
    }

    /**
     * Résolveur de locale basé sur l'en-tête Accept-Language.
     */
    @Bean
    public LocaleResolver localeResolver() {
        AcceptHeaderLocaleResolver localeResolver = new AcceptHeaderLocaleResolver();
        localeResolver.setDefaultLocale(SupportedLocales.DEFAULT);
        localeResolver.setSupportedLocales(SupportedLocales.ALL);
        return localeResolver;
    }

    /**
     * Source des messages multilingues (i18n).
     * Charge les fichiers `messages_xx.properties` depuis le classpath.
     */
    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:messages");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setCacheSeconds(3600); // Cache de 1 heure
        messageSource.setFallbackToSystemLocale(false);
        messageSource.setDefaultLocale(SupportedLocales.DEFAULT);
        return messageSource;
    }

    /**
     * Intercepteur pour changer la langue via paramètre URL
     * Exemple : /api/users?lang=nl ou /api/users?lang=en
     */
    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();

        interceptor.setParamName("lang");

        interceptor.setIgnoreInvalidLocale(true);

        return interceptor;
    }

    /**
     * Enregistrement de l'intercepteur de changement de locale
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
    }
}
