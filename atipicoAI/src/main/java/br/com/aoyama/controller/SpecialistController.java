package br.com.aoyama.controller;

import br.com.aoyama.dto.SpecialistRequestDTO;
import br.com.aoyama.dto.SpecialistResponseDTO;
import br.com.aoyama.service.SpecialistService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/specialists")
public class SpecialistController {

    private final SpecialistService specialistService;

    public SpecialistController(SpecialistService specialistService) {
        this.specialistService = specialistService;
    }

    @PostMapping
    public ResponseEntity<SpecialistResponseDTO> cadastrarEspecialista(
            @AuthenticationPrincipal OAuth2User oauth2User,
            @Valid @RequestBody SpecialistRequestDTO dto) {

        String email = oauth2User.getAttribute("email");
        SpecialistResponseDTO response = specialistService.cadastrarEspecialista(email, dto);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<SpecialistResponseDTO>> listarEspecialistas(
            @AuthenticationPrincipal OAuth2User oauth2User) {

        String email = oauth2User.getAttribute("email");
        List<SpecialistResponseDTO> response = specialistService.listarEspecialistasDoUsuario(email);

        return ResponseEntity.ok(response);
    }
}