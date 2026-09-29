package br.com.aoyama.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "patients")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String name;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    // Nova lista para armazenar múltiplos CIDs (Ex: F84.0, F90.0)
    @ElementCollection
    @CollectionTable(name = "patient_cids", joinColumns = @JoinColumn(name = "patient_id"))
    @Column(name = "cid_code")
    private List<String> cids;

    @Column(columnDefinition = "TEXT")
    private String notes; // Observações gerais sobre a criança

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MedicalDocument> documents;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
