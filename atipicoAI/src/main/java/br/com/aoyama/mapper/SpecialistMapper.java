package br.com.aoyama.mapper;

import br.com.aoyama.dto.SpecialistRequestDTO;
import br.com.aoyama.dto.SpecialistResponseDTO;
import br.com.aoyama.model.Specialist;
import br.com.aoyama.model.User;
import org.springframework.stereotype.Component;

@Component
public class SpecialistMapper {

    public Specialist toEntity(SpecialistRequestDTO dto, User user) {
        if (dto == null) {
            return null;
        }

        return Specialist.builder()
                .user(user)
                .name(dto.getName())
                .category(dto.getCategory())
                .contactInfo(dto.getContactInfo())
                .build();
    }

    public SpecialistResponseDTO toResponseDTO(Specialist specialist) {
        if (specialist == null) {
            return null;
        }

        return SpecialistResponseDTO.builder()
                .id(specialist.getId())
                .name(specialist.getName())
                .category(specialist.getCategory())
                .contactInfo(specialist.getContactInfo())
                .build();
    }
}