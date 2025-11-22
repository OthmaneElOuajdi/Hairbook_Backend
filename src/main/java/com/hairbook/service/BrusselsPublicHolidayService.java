package com.hairbook.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.util.*;

/**
 * Service pour calculer les jours fériés officiels de la Région de Bruxelles-Capitale.
 * <p>
 * Jours fériés légaux en Belgique (applicables à Bruxelles) :
 * <ul>
 *   <li>1er janvier - Jour de l'An</li>
 *   <li>Lundi de Pâques (mobile)</li>
 *   <li>1er mai - Fête du Travail</li>
 *   <li>Jeudi de l'Ascension (mobile, 39 jours après Pâques)</li>
 *   <li>Lundi de Pentecôte (mobile, 50 jours après Pâques)</li>
 *   <li>21 juillet - Fête nationale belge</li>
 *   <li>15 août - Assomption</li>
 *   <li>1er novembre - Toussaint</li>
 *   <li>11 novembre - Armistice</li>
 *   <li>25 décembre - Noël</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BrusselsPublicHolidayService {

    /**
     * Calcule tous les jours fériés de Bruxelles pour une année donnée.
     *
     * @param year année
     * @return Map avec la date comme clé et le nom du jour férié comme valeur
     */
    public Map<LocalDate, String> getPublicHolidaysForYear(int year) {
        Map<LocalDate, String> holidays = new LinkedHashMap<>();

        // Jours fériés fixes
        holidays.put(LocalDate.of(year, Month.JANUARY, 1), "Jour de l'An");
        holidays.put(LocalDate.of(year, Month.MAY, 1), "Fête du Travail");
        holidays.put(LocalDate.of(year, Month.JULY, 21), "Fête nationale belge");
        holidays.put(LocalDate.of(year, Month.AUGUST, 15), "Assomption");
        holidays.put(LocalDate.of(year, Month.NOVEMBER, 1), "Toussaint");
        holidays.put(LocalDate.of(year, Month.NOVEMBER, 11), "Armistice");
        holidays.put(LocalDate.of(year, Month.DECEMBER, 25), "Noël");

        // Jours fériés mobiles basés sur Pâques
        LocalDate easter = calculateEaster(year);
        holidays.put(easter.plusDays(1), "Lundi de Pâques");
        holidays.put(easter.plusDays(39), "Ascension");
        holidays.put(easter.plusDays(50), "Lundi de Pentecôte");

        log.info("Calculated {} public holidays for Brussels in year {}", holidays.size(), year);
        return holidays;
    }

    /**
     * Vérifie si une date est un jour férié à Bruxelles.
     *
     * @param date date à vérifier
     * @return true si c'est un jour férié
     */
    public boolean isPublicHoliday(LocalDate date) {
        Map<LocalDate, String> holidays = getPublicHolidaysForYear(date.getYear());
        return holidays.containsKey(date);
    }

    /**
     * Récupère le nom du jour férié pour une date donnée.
     *
     * @param date date à vérifier
     * @return nom du jour férié ou null si ce n'est pas un jour férié
     */
    public String getPublicHolidayName(LocalDate date) {
        Map<LocalDate, String> holidays = getPublicHolidaysForYear(date.getYear());
        return holidays.get(date);
    }

    /**
     * Récupère tous les jours fériés entre deux dates.
     *
     * @param startDate date de début
     * @param endDate   date de fin
     * @return Map des jours fériés dans la période
     */
    public Map<LocalDate, String> getPublicHolidaysBetween(LocalDate startDate, LocalDate endDate) {
        Map<LocalDate, String> result = new LinkedHashMap<>();

        for (int year = startDate.getYear(); year <= endDate.getYear(); year++) {
            Map<LocalDate, String> yearHolidays = getPublicHolidaysForYear(year);
            yearHolidays.entrySet().stream()
                    .filter(entry -> !entry.getKey().isBefore(startDate) && !entry.getKey().isAfter(endDate))
                    .forEach(entry -> result.put(entry.getKey(), entry.getValue()));
        }

        return result;
    }

    /**
     * Calcule la date de Pâques pour une année donnée.
     * Utilise l'algorithme de Meeus/Jones/Butcher.
     *
     * @param year année
     * @return date de Pâques
     */
    private LocalDate calculateEaster(int year) {
        int a = year % 19;
        int b = year / 100;
        int c = year % 100;
        int d = b / 4;
        int e = b % 4;
        int f = (b + 8) / 25;
        int g = (b - f + 1) / 3;
        int h = (19 * a + b - d - g + 15) % 30;
        int i = c / 4;
        int k = c % 4;
        int l = (32 + 2 * e + 2 * i - h - k) % 7;
        int m = (a + 11 * h + 22 * l) / 451;
        int month = (h + l - 7 * m + 114) / 31;
        int day = ((h + l - 7 * m + 114) % 31) + 1;

        return LocalDate.of(year, month, day);
    }

    /**
     * Récupère les prochains jours fériés à partir d'aujourd'hui.
     *
     * @param count nombre de jours fériés à récupérer
     * @return Liste des prochains jours fériés avec leur nom
     */
    public List<Map.Entry<LocalDate, String>> getUpcomingPublicHolidays(int count) {
        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusYears(2); // Chercher sur 2 ans

        return getPublicHolidaysBetween(today, endDate).entrySet().stream()
                .filter(entry -> !entry.getKey().isBefore(today))
                .limit(count)
                .toList();
    }
}
