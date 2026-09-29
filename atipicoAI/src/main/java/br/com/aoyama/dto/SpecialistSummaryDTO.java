package br.com.aoyama.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpecialistSummaryDTO {
    private String specialistName;
    private String specialty;
    private String documentTitle;
    private String documentType;
    private String aiSummary;
    private String documentDate;
}