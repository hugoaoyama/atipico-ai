package br.com.aoyama.specification;

import br.com.aoyama.model.MedicalDocument;
import org.springframework.data.jpa.domain.Specification;
import java.time.LocalDate;

public class MedicalDocumentSpecification {

    public static Specification<MedicalDocument> belongsToPatient(Long patientId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("patient").get("id"), patientId);
    }

    public static Specification<MedicalDocument> hasDocumentType(String documentType) {
        return (root, query, criteriaBuilder) -> {
            if (documentType == null || documentType.isBlank()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(criteriaBuilder.lower(root.get("documentType")), documentType.toLowerCase());
        };
    }

    public static Specification<MedicalDocument> isBetweenDates(LocalDate startDate, LocalDate endDate) {
        return (root, query, criteriaBuilder) -> {
            if (startDate == null && endDate == null) {
                return criteriaBuilder.conjunction();
            }
            if (startDate != null && endDate != null) {
                return criteriaBuilder.between(root.get("documentDate"), startDate, endDate);
            }
            if (startDate != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("documentDate"), startDate);
            }
            return criteriaBuilder.lessThanOrEqualTo(root.get("documentDate"), endDate);
        };
    }
}