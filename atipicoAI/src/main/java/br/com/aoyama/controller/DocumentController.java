package br.com.aoyama.controller;

import br.com.aoyama.dto.DocumentDetailsResponseDTO;
import br.com.aoyama.dto.DocumentResponseDTO;
import br.com.aoyama.dto.DocumentUploadRequestDTO;
import br.com.aoyama.service.DocumentProcessService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/documents")
public class DocumentController {

    private final DocumentProcessService documentProcessService;

    public DocumentController(DocumentProcessService documentProcessService) {
        this.documentProcessService = documentProcessService;
    }

    @PostMapping("/upload")
    public ResponseEntity<DocumentResponseDTO> uploadDocumento(
            @AuthenticationPrincipal OAuth2User oauth2User,
            @Valid @ModelAttribute DocumentUploadRequestDTO requestDTO,
            @RequestParam("file") MultipartFile file) {

        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body(new DocumentResponseDTO(null, null, null, "O arquivo PDF é obrigatório."));
        }
        // Extrai o e-mail do usuário autenticado via OAuth2
        String email = oauth2User.getAttribute("email");

        DocumentResponseDTO response = documentProcessService.processarUpload(email, requestDTO, file);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<DocumentDetailsResponseDTO>> listarDocumentosDoPaciente(
            @PathVariable Long patientId,
            @RequestParam(required = false) String documentType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        List<DocumentDetailsResponseDTO> documentos = documentProcessService.listarComFiltros(patientId, documentType, startDate, endDate);
        return ResponseEntity.ok(documentos);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirDocumento(@PathVariable Long id){
        documentProcessService.excluirDocumento(id);
        log.info("Documento com ID {} excluído com sucesso.", id);
        return ResponseEntity.noContent().build(); // Retorna 204 No Content em caso de sucesso

    }
}