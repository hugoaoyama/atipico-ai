package br.com.aoyama.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class PatientRequestDTO {
    @NotNull(message = "O ID do paciente é obrigatório.")
    private String name;
    private LocalDate birthDate;
    private List<String> cids; // Ex: ["F84.0", "F90.0"]
    private String notes;
}
