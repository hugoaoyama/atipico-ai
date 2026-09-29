package br.com.aoyama.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentDetailsResponseDTO {
    private Long id;
    private String specialistName;
    private String title;
    private String fileName;
    private String documentType;
    private LocalDate documentDate;
    private LocalDate expirationDate;
    private String googleDriveUrl;
    private String aiSummary;
    private LocalDateTime createdAt;
}
