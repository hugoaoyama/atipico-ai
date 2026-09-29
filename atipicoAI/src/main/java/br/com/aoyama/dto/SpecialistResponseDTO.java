package br.com.aoyama.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SpecialistResponseDTO {
    private Long id;
    private String name;
    private String category;
    private String contactInfo;
}
