package br.com.aoyama.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data
public class DocumentUploadRequestDTO {

    @NotNull(message = "O ID do paciente é obrigatório.")
    private Long patientId;
    @NotNull(message = "O ID do especialista é obrigatório.")
    private Long specialistId; // Se o ID do especialista for Long no banco
    @NotBlank(message = "O título do documento é obrigatório.")
    private String title;
    @NotBlank(message = "O tipo de documento é obrigatório.")
    private String documentType;
    @NotNull(message = "A data do documento é obrigatória.")
    private LocalDate documentDate;
    private LocalDate expirationDate;
}