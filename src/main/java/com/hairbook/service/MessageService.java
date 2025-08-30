package com.hairbook.service;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.util.Locale;

/**
 * Service utilitaire pour la gestion des messages multilingues
 * Permet de récupérer facilement les messages traduits dans les services métier
 */
@Service
public class MessageService {

    private final MessageSource messageSource;

    public MessageService(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    /**
     * Récupère un message traduit selon la locale courante
     * 
     * @param key Clé du message dans les fichiers properties
     * @return Message traduit
     */
    public String getMessage(String key) {
        return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
    }

    /**
     * Récupère un message traduit avec des paramètres
     * 
     * @param key  Clé du message
     * @param args Arguments à injecter dans le message
     * @return Message traduit avec paramètres
     */
    public String getMessage(String key, Object... args) {
        return messageSource.getMessage(key, args, LocaleContextHolder.getLocale());
    }

    /**
     * Récupère un message traduit avec une locale spécifique
     * 
     * @param key    Clé du message
     * @param locale Locale spécifique
     * @return Message traduit
     */
    public String getMessage(String key, Locale locale) {
        return messageSource.getMessage(key, null, locale);
    }

    /**
     * Récupère un message traduit avec paramètres et locale spécifique
     * 
     * @param key    Clé du message
     * @param args   Arguments à injecter
     * @param locale Locale spécifique
     * @return Message traduit avec paramètres
     */
    public String getMessage(String key, Object[] args, Locale locale) {
        return messageSource.getMessage(key, args, locale);
    }

    /**
     * Récupère un message avec fallback si la clé n'existe pas
     * 
     * @param key            Clé du message
     * @param defaultMessage Message par défaut si clé introuvable
     * @return Message traduit ou message par défaut
     */
    public String getMessageWithDefault(String key, String defaultMessage) {
        return messageSource.getMessage(key, null, defaultMessage, LocaleContextHolder.getLocale());
    }

    /**
     * Récupère la locale courante
     * 
     * @return Locale courante du thread
     */
    public Locale getCurrentLocale() {
        return LocaleContextHolder.getLocale();
    }

    /**
     * Vérifie si la locale courante est le français
     * 
     * @return true si français (fr ou fr-BE)
     */
    public boolean isFrench() {
        Locale locale = getCurrentLocale();
        return "fr".equals(locale.getLanguage());
    }

    /**
     * Vérifie si la locale courante est le néerlandais
     * 
     * @return true si néerlandais (nl ou nl-BE)
     */
    public boolean isDutch() {
        Locale locale = getCurrentLocale();
        return "nl".equals(locale.getLanguage());
    }

    /**
     * Vérifie si la locale courante est l'anglais
     * 
     * @return true si anglais (en ou en-US)
     */
    public boolean isEnglish() {
        Locale locale = getCurrentLocale();
        return "en".equals(locale.getLanguage());
    }
}
