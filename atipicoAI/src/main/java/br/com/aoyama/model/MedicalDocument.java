package br.com.aoyama.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "medical_documents")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicalDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "specialist_id")
    private Specialist specialist; // Opcional, caso queira vincular ao profissional

    @Column(nullable = false)
    private String title; // Ex: "Relatório Trimestral - Fono"

    @Column(name = "file_name")
    private String fileName; // Nome original do PDF enviado

    @Column(name = "document_type", nullable = false)
    private String documentType; // Ex: Laudo, Relatório de Evolução, Receita, Exame

    @Column(name = "document_date")
    private LocalDate documentDate; // Data em que o documento foi emitido

    @Column(name = "expiration_date")
    private LocalDate expirationDate; // Útil para alertas de laudos vencendo

    @Column(name = "google_drive_file_id", nullable = false)
    private String googleDriveFileId; // ID essencial para buscar o PDF no Drive

    @Column(name = "google_drive_url", columnDefinition = "TEXT")
    private String googleDriveUrl; // Link direto para visualização

//    @Column(name = "extracted_text", columnDefinition = "TEXT")
//    private String extractedText; // Texto bruto extraído pelo PDFBox para a IA

    @Column(name = "ai_summary", columnDefinition = "TEXT")
    private String aiSummary; // Resumo clínico estruturado gerado pela IA

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}