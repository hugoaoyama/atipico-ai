package br.com.aoyama.repository;

import br.com.aoyama.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    // Lista todas as crianças associadas a uma mãe específica
    List<Patient> findByUserId(Long userId);
}
