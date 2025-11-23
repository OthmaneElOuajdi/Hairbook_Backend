package com.hairbook.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.util.Locale;

/**
 * Service pour gérer les messages internationalisés.
 */
@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageSource messageSource;

    /**
     * Récupère un message traduit selon la locale courante.
     *
     * @param code clé du message
     * @param args arguments optionnels
     * @return message traduit
     */
    public String getMessage(String code, Object... args) {
        return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
    }

    /**
     * Récupère un message traduit selon une locale spécifique.
     *
     * @param code   clé du message
     * @param locale locale souhaitée
     * @param args   arguments optionnels
     * @return message traduit
     */
    public String getMessage(String code, Locale locale, Object... args) {
        return messageSource.getMessage(code, args, locale);
    }
}
