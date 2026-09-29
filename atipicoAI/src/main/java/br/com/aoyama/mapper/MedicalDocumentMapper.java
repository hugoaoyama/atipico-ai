package br.com.aoyama.mapper;

import br.com.aoyama.dto.DocumentDetailsResponseDTO;
import br.com.aoyama.model.MedicalDocument;
import org.springframework.stereotype.Component;

@Component
public class MedicalDocumentMapper {

    public DocumentDetailsResponseDTO toDetailsResponseDTO(MedicalDocument doc) {
        if (doc == null) {
            return null;
        }

        return DocumentDetailsResponseDTO.builder()
                .id(doc.getId())
                .title(doc.getTitle())
                .fileName(doc.getFileName())
                .documentType(doc.getDocumentType())
                .documentDate(doc.getDocumentDate())
                .expirationDate(doc.getExpirationDate())
                .googleDriveUrl(doc.getGoogleDriveUrl())
                .aiSummary(doc.getAiSummary())
                .createdAt(doc.getCreatedAt())
                .specialistName(doc.getSpecialist().getName())
                .build();
    }
}