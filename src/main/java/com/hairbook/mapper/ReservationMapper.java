package com.hairbook.mapper;

import com.hairbook.dto.reservation.ReservationResponseDTO;
import com.hairbook.entity.Reservation;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper responsable de la conversion des entités {@link Reservation}
 * vers des objets {@link ReservationResponseDTO}.
 * <p>
 * Cette classe permet de séparer la logique métier (entités JPA) 
 * de la couche API (DTO exposés via les contrôleurs REST).
 */
@Component
public class ReservationMapper {

    /**
     * Convertit une entité {@link Reservation} en {@link ReservationResponseDTO}.
     * <p>
     * Les informations principales de la réservation, ainsi que les
     * données de l'utilisateur et du service associé, sont mappées
     * dans l'objet DTO. Les valeurs nulles sont gérées afin d'éviter
     * les NullPointerException.
     *
     * @param reservation l'entité {@link Reservation} à convertir
     * @return une instance de {@link ReservationResponseDTO} contenant
     *         les données de la réservation
     */
    public ReservationResponseDTO toResponseDTO(Reservation reservation) {
        ReservationResponseDTO dto = new ReservationResponseDTO();
        
        dto.setId(reservation.getId());
        dto.setReservationDateTime(reservation.getStartTime());
        dto.setStatus(reservation.getStatus());
        dto.setNotes(reservation.getNotes());
        dto.setCreatedAt(reservation.getCreatedAt());
        dto.setUpdatedAt(reservation.getUpdatedAt());
        
        // Données utilisateur
        if (reservation.getUser() != null) {
            dto.setUserId(reservation.getUser().getId());
            dto.setUserName(reservation.getUser().getFullName());
        }
        
        // Données service
        if (reservation.getServiceItem() != null) {
            dto.setServiceItemId(reservation.getServiceItem().getId());
            dto.setServiceName(reservation.getServiceItem().getName());
            dto.setServicePrice(BigDecimal.valueOf(reservation.getServiceItem().getPrice()));
            dto.setServiceDuration(reservation.getServiceItem().getDuration());
        }
        
        return dto;
    }

    /**
     * Convertit une liste d'entités {@link Reservation} en une liste
     * de {@link ReservationResponseDTO}.
     *
     * @param reservations la liste d'entités {@link Reservation} à convertir
     * @return une liste de {@link ReservationResponseDTO}
     */
    public List<ReservationResponseDTO> toResponseDTOList(List<Reservation> reservations) {
        return reservations.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }
}
