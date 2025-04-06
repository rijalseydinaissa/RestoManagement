package org.odc.gestionstockapp.Web.Dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class NotificationDto {
    private Long id;
    private String message;
    private Long commandeId;
    private LocalDateTime dateCreation;
    private boolean lue;
}