package br.com.aoyama.service;

import br.com.aoyama.dto.PatientRequestDTO;
import br.com.aoyama.dto.PatientResponseDTO;
import br.com.aoyama.mapper.PatientMapper;
import br.com.aoyama.model.Patient;
import br.com.aoyama.model.User;
import br.com.aoyama.repository.PatientRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PatientService {
    private final PatientRepository patientRepository;
    private final UserService userService;
    private final PatientMapper patientMapper;

    public PatientService(PatientRepository patientRepository,
                          UserService userService,
                          PatientMapper patientMapper) {
        this.patientRepository = patientRepository;
        this.userService = userService;
        this.patientMapper = patientMapper;
    }

    public PatientResponseDTO criarPaciente(String emailUser, PatientRequestDTO dto) {
        User user = userService.buscarPorEmail(emailUser);

        Patient patient = Patient.builder()
                .name(dto.getName())
                .birthDate(dto.getBirthDate())
                .cids(dto.getCids()) // Adicionando os CIDs informados
                .notes(dto.getNotes())
                .user(user) // Vincula o paciente à mãe logada
                .build();

        Patient saved = patientRepository.save(patient);
        return patientMapper.toResponseDTO(saved);
    }

    public List<PatientResponseDTO> listarPorUsuario(String email) {
        User user = userService.buscarPorEmail(email);

        List<Patient> patients = patientRepository.findByUserId(user.getId());
        return patients.stream()
                .map(patientMapper::toResponseDTO)
                .toList();
    }
}
