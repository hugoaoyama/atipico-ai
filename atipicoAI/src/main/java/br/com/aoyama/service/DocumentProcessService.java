package br.com.aoyama.service;

import br.com.aoyama.dto.DocumentDetailsResponseDTO;
import br.com.aoyama.dto.DocumentResponseDTO;
import br.com.aoyama.dto.DocumentUploadRequestDTO;
import br.com.aoyama.exception.GoogleDriveAuthenticationException;
import br.com.aoyama.exception.ResourceNotFoundException;
import br.com.aoyama.mapper.MedicalDocumentMapper;
import br.com.aoyama.model.MedicalDocument;
import br.com.aoyama.model.Patient;
import br.com.aoyama.model.Specialist;
import br.com.aoyama.model.User;
import br.com.aoyama.repository.MedicalDocumentRepository;
import br.com.aoyama.repository.PatientRepository;
import br.com.aoyama.repository.SpecialistRepository;
import br.com.aoyama.specification.MedicalDocumentSpecification;
import com.google.api.services.drive.model.File;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
public class DocumentProcessService {

    private final UserService userService;
    private final GoogleDriveService googleDriveService;
    private final PdfService pdfService;
    private final AiService aiService;
    private final MedicalDocumentRepository documentRepository;
    private final PatientRepository patientRepository;
    private final SpecialistRepository specialistRepository;
    private final MedicalDocumentMapper documentMapper;

    public DocumentProcessService(UserService userService,
                                  GoogleDriveService googleDriveService,
                                  PdfService pdfService,
                                  AiService aiService,
                                  MedicalDocumentRepository documentRepository,
                                  PatientRepository patientRepository,
                                  SpecialistRepository specialistRepository,
                                  MedicalDocumentMapper documentMapper) {
        this.userService = userService;
        this.googleDriveService = googleDriveService;
        this.pdfService = pdfService;
        this.aiService = aiService;
        this.documentRepository = documentRepository;
        this.patientRepository = patientRepository;
        this.specialistRepository = specialistRepository;
        this.documentMapper = documentMapper;
    }

    public DocumentResponseDTO processarUpload(String emailUser, DocumentUploadRequestDTO dto, MultipartFile file) {
        User user = userService.buscarPorEmail(emailUser);

        // Substituído por exceção específica para retornar 401 Unauthorized
        if (user.getGoogleAccessToken() == null) {
            throw new GoogleDriveAuthenticationException("Token do Google Drive não encontrado. Faça login novamente.", null);
        }
        log.info("patient="+dto.getPatientId());
        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Paciente não encontrado."));

        log.info("patient="+patient.getName());
        log.info("especialista="+dto.getSpecialistId());

        Specialist specialist = specialistRepository.findById(dto.getSpecialistId())
                .orElseThrow(()-> new ResourceNotFoundException("Especialista não encontrado"));
        log.info("especialista="+specialist.getName());

        log.info("Envia o PDF para o Google Drive");
        // 1. Envia o PDF para o Google Drive
        File driveFile = googleDriveService.enviarPdfParaDrive(user, file);
        log.info("drive ="+driveFile);

        // 2. Extrai o texto do PDF via PDFBox
        String textoExtraido = pdfService.extrairTextoDeMultipartFile(file);
        log.info("textoExtraido" + textoExtraido);

        if(textoExtraido.isBlank()){
            textoExtraido = pdfService.extractTextFromScannedPdf(file);
            log.info("textoExtraido da imagem" + textoExtraido);
        }

        // 3. Gera o resumo clínico inteligente usando Spring AI
        String resumoAi = aiService.gerarResumoClinico(textoExtraido);
        log.info("resumoAi ="+resumoAi);

        // 4. Constrói e persiste o documento com o texto e o resumo da IA
        MedicalDocument document = MedicalDocument.builder()
                    .patient(patient)
                    .specialist(specialist)
                    .title(dto.getTitle() != null ? dto.getTitle() : driveFile.getName())
                    .fileName(driveFile.getName())
                    .documentType(dto.getDocumentType() != null ? dto.getDocumentType() : "Laudo")
                    .googleDriveFileId(driveFile.getId())
                    .googleDriveUrl(driveFile.getWebViewLink())
//                    .extractedText(textoExtraido)
                    .aiSummary(resumoAi)// Salvando o resumo gerado pela IA
                    .documentDate(dto.getDocumentDate())
                    .expirationDate(dto.getExpirationDate())
                    .build();
        // Em vez de: log.info("document =" + document);
        log.info("Documento construído com sucesso para o paciente ID: {}", patient.getId());
        documentRepository.save(document);
        log.info("depois do save =");
        return new DocumentResponseDTO(
                    driveFile.getId(),
                    driveFile.getName(),
                    driveFile.getWebViewLink(),
                    "Documento enviado ao Drive e salvo com sucesso para o paciente!"
        );

    }

    public List<DocumentDetailsResponseDTO> listarComFiltros(Long patientId, String documentType, LocalDate startDate, LocalDate endDate) {
        Specification<MedicalDocument> spec = Specification
                .where(MedicalDocumentSpecification.belongsToPatient(patientId))
                .and(MedicalDocumentSpecification.hasDocumentType(documentType))
                .and(MedicalDocumentSpecification.isBetweenDates(startDate, endDate));

        List<MedicalDocument> documents = documentRepository.findAll(spec);

        // Delega o mapeamento para o Mapper dedicado
        return documents.stream()
                .map(documentMapper::toDetailsResponseDTO)
                .toList();
    }

    public void excluirDocumento(Long id) {
        MedicalDocument document = documentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Documento não encontrado."));

        // Opcional: Remover do Google Drive antes de apagar do banco
        // googleDriveService.deletarArquivo(document.getGoogleDriveFileId());

        documentRepository.delete(document);
    }
}
