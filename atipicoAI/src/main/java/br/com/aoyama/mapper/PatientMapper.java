package br.com.aoyama.mapper;

import br.com.aoyama.dto.PatientRequestDTO;
import br.com.aoyama.dto.PatientResponseDTO;
import br.com.aoyama.model.Patient;
import br.com.aoyama.model.User;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class PatientMapper {

    // Converte RequestDTO para Entidade Patient (na hora de cadastrar)
    public Patient toEntity(PatientRequestDTO dto, User user) {
        if (dto == null) {
            return null;
        }

        return Patient.builder()
                .name(dto.getName())
                .birthDate(dto.getBirthDate())
                .cids(dto.getCids() != null ? dto.getCids() : Collections.emptyList())
                .notes(dto.getNotes())
                .user(user)
                .build();
    }

    // Converte Entidade Patient para ResponseDTO (na hora de retornar para o front)
    public PatientResponseDTO toResponseDTO(Patient patient) {
        if (patient == null) {
            return null;
        }

        return PatientResponseDTO.builder()
                .id(patient.getId())
                .name(patient.getName())
                .birthDate(patient.getBirthDate())
                .cids(patient.getCids())
                .notes(patient.getNotes())
                .createdAt(patient.getCreatedAt())
                .build();
    }
}