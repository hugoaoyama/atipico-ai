package br.com.aoyama.controller;

import br.com.aoyama.dto.PatientRequestDTO;
import br.com.aoyama.dto.PatientResponseDTO;
import br.com.aoyama.model.Patient;
import br.com.aoyama.service.PatientService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/patients")
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @PostMapping
    public ResponseEntity<PatientResponseDTO> cadastrarPaciente(
            @AuthenticationPrincipal OAuth2User oauth2User,
            @Valid @RequestBody PatientRequestDTO dto) {

        String email = oauth2User.getAttribute("email");
        PatientResponseDTO novoPaciente = patientService.criarPaciente(email, dto);

        return ResponseEntity.ok(novoPaciente);
    }

    // Endpoint para listar os pacientes do usuário logado
    @GetMapping
    public ResponseEntity<List<PatientResponseDTO>> listarPacientes(@AuthenticationPrincipal OAuth2User oauth2User) {
        String email = oauth2User.getAttribute("email");
        List<PatientResponseDTO> patients = patientService.listarPorUsuario(email); // Certifique-se de ter esse método no seu Service
        return ResponseEntity.ok(patients);
    }

}