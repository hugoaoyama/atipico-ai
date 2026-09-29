package br.com.aoyama.repository;

import br.com.aoyama.model.MedicalDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MedicalDocumentRepository extends JpaRepository<MedicalDocument, Long>, JpaSpecificationExecutor<MedicalDocument> {

    // Lista todos os documentos de um paciente específico (ordenados por data do documento decrescente)
    List<MedicalDocument> findByPatientIdOrderByDocumentDateDesc(Long patientId);

    // Lista documentos por tipo (ex: "Laudo", "Receita") para um paciente
    List<MedicalDocument> findByPatientIdAndDocumentType(Long patientId, String documentType);

    // Útil para a funcionalidade de alertas de validade (buscar documentos que vencem antes de uma data)
    List<MedicalDocument> findByExpirationDateBefore(LocalDate date);

    // Busca todos os documentos vinculados a um paciente específico
    List<MedicalDocument> findByPatientId(Long patientId);

    @Query("SELECT d FROM MedicalDocument d WHERE d.patient.id = :patientId AND d.documentDate IN " +
            "(SELECT MAX(sub.documentDate) FROM MedicalDocument sub WHERE sub.patient.id = :patientId GROUP BY sub.specialist.id)")
    List<MedicalDocument> findLatestDocumentsPerSpecialist(@Param("patientId") Long patientId);
}
