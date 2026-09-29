package br.com.aoyama.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SpecialistRequestDTO {

    @NotBlank(message = "O nome do especialista é obrigatório.")
    private String name;
    @NotBlank(message = "O categoria ou especialidade é obrigatório.")
    private String category;
    private String contactInfo;
}