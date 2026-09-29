package br.com.aoyama.service;

import br.com.aoyama.dto.SpecialistSummaryDTO;
import br.com.aoyama.exception.ReportGenerationException;
import br.com.aoyama.exception.ResourceNotFoundException;
import br.com.aoyama.model.MedicalDocument;
import br.com.aoyama.model.Patient;
import br.com.aoyama.repository.MedicalDocumentRepository;
import br.com.aoyama.repository.PatientRepository;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ReportService {

    private final PatientRepository patientRepository;
    private final MedicalDocumentRepository documentRepository;

    public ReportService(PatientRepository patientRepository, MedicalDocumentRepository documentRepository) {
        this.patientRepository = patientRepository;
        this.documentRepository = documentRepository;
    }

    public byte[] gerarRelatorioUltimosResumos(Long patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente não encontrado."));

        // Busca os últimos documentos de cada especialista para este paciente
        List<MedicalDocument> ultimosDocumentos = documentRepository.findLatestDocumentsPerSpecialist(patientId);

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            Document document = new Document(PageSize.A4, 36, 36, 54, 54);
            PdfWriter.getInstance(document, out);
            document.open();

            // Fontes
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.DARK_GRAY);
            Font subtitleFont = FontFactory.getFont(FontFactory.HELVETICA, 12, Color.GRAY);
            Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.BLUE);
            Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.BLACK);

            // Cabeçalho do Relatório
            Paragraph title = new Paragraph("AtípicoAI - Relatório Clínico Consolidado", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            Paragraph subtitle = new Paragraph("Paciente: " + patient.getName() + " | Gerado em: " + java.time.LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), subtitleFont);
            subtitle.setAlignment(Element.ALIGN_CENTER);
            subtitle.setSpacingAfter(20);
            document.add(subtitle);

            document.add(new Chunk(new com.lowagie.text.pdf.draw.LineSeparator()));

            // Seção de resumos por especialidade
            for (MedicalDocument doc : ultimosDocumentos) {
                Paragraph specHeader = new Paragraph("Especialista: " + doc.getSpecialist().getName() + " (" + doc.getSpecialist().getCategory() + ")", sectionFont);
                specHeader.setSpacingBefore(15);
                specHeader.setSpacingAfter(5);
                document.add(specHeader);

                Paragraph docInfo = new Paragraph("Documento: " + doc.getTitle() + " - Tipo: " + doc.getDocumentType(), bodyFont);
                document.add(docInfo);

                Paragraph summary = new Paragraph("Resumo Clínico (IA):\n" + (doc.getAiSummary() != null ? doc.getAiSummary() : "Sem resumo disponível."), bodyFont);
                summary.setSpacingBefore(5);
                summary.setSpacingAfter(10);
                document.add(summary);

                document.add(new Chunk(new com.lowagie.text.pdf.draw.DottedLineSeparator()));
            }

            document.close();
        } catch (DocumentException e) {
            throw new ReportGenerationException("Erro ao gerar PDF do relatório consolidado: " + e.getMessage(), e);
        }

        return out.toByteArray();
    }

    public List<SpecialistSummaryDTO> obterResumosParaVisualizacao(Long patientId) {
        List<MedicalDocument> ultimosDocumentos = documentRepository.findLatestDocumentsPerSpecialist(patientId);

        return ultimosDocumentos.stream().map(doc -> new SpecialistSummaryDTO(
                doc.getSpecialist().getName(),
                doc.getSpecialist().getCategory(),
                doc.getTitle(),
                doc.getDocumentType(),
                doc.getAiSummary() != null ? doc.getAiSummary() : "Sem resumo clínico disponível.",
                doc.getDocumentDate() != null ? doc.getDocumentDate().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")) : ""
        )).toList();
    }
}