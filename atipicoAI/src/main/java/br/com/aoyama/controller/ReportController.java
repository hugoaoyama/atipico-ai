package br.com.aoyama.controller;

import br.com.aoyama.dto.SpecialistSummaryDTO;
import br.com.aoyama.service.ReportService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<byte[]> baixarRelatorioPaciente(@PathVariable Long patientId) {
        byte[] pdfBytes = reportService.gerarRelatorioUltimosResumos(patientId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "relatorio-clinico-paciente.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }

    @GetMapping("/patient/{patientId}/preview")
    public ResponseEntity<List<SpecialistSummaryDTO>> visualizarResumosPaciente(@PathVariable Long patientId) {
        List<SpecialistSummaryDTO> resumos = reportService.obterResumosParaVisualizacao(patientId);
        return ResponseEntity.ok(resumos);
    }
}