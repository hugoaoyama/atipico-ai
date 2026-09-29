package br.com.aoyama.repository;

import br.com.aoyama.model.Specialist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpecialistRepository extends JpaRepository<Specialist, Long> {

    // Lista todos os especialistas cadastrados por um usuário
    List<Specialist> findByUserId(Long userId);

    // Opcional: Filtrar especialistas por categoria (ex: Fonoaudiólogo) para um usuário
    List<Specialist> findByUserIdAndCategory(Long userId, String category);
}
