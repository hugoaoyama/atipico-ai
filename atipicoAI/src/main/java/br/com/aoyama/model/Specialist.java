package br.com.aoyama.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "specialists")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Specialist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String name; // Ex: Dra. Carla / Clínica FonoVida

    @Column(nullable = false)
    private String category; // Ex: Neuropediatra, Fonoaudiólogo, T.O., Psicólogo ABA, Psiquiatra

    @Column(name = "contact_info")
    private String contactInfo; // Telefone, e-mail ou endereço

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}