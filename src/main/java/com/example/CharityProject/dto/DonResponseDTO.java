package com.example.CharityProject.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class DonResponseDTO {
    private Long id;
    private Double montant;
    private LocalDateTime dateDon;
    private String actionTitre;
    private String donateurEmail;
}