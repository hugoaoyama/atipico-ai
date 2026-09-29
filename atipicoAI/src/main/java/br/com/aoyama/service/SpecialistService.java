package br.com.aoyama.service;

import br.com.aoyama.dto.SpecialistRequestDTO;
import br.com.aoyama.dto.SpecialistResponseDTO;
import br.com.aoyama.mapper.SpecialistMapper;
import br.com.aoyama.model.Specialist;
import br.com.aoyama.model.User;
import br.com.aoyama.repository.SpecialistRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SpecialistService {
    private final SpecialistRepository specialistRepository;
    private final UserService userService;
    private final SpecialistMapper specialistMapper;

    public SpecialistService(SpecialistRepository specialistRepository,
                             UserService userService,
                             SpecialistMapper specialistMapper) {
        this.specialistRepository = specialistRepository;
        this.userService = userService;
        this.specialistMapper = specialistMapper;
    }

    public SpecialistResponseDTO cadastrarEspecialista(String emailUser, SpecialistRequestDTO dto) {
        User user = userService.buscarPorEmail(emailUser);

        Specialist specialist = Specialist.builder()
                .user(user)
                .name(dto.getName())
                .category(dto.getCategory())
                .contactInfo(dto.getContactInfo())
                .build();
        Specialist saved = specialistRepository.save(specialist);

        // Delega a conversão para DTO de resposta para o Mapper
        return specialistMapper.toResponseDTO(saved);
    }

    public List<SpecialistResponseDTO> listarEspecialistasDoUsuario(String emailUser) {
        User user = userService.buscarPorEmail(emailUser);
        List<Specialist> specialists = specialistRepository.findByUserId(user.getId());

        return specialists.stream()
                .map(specialistMapper::toResponseDTO)
                .toList();
    }

}
