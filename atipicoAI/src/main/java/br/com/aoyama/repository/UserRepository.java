package br.com.aoyama.repository;

import br.com.aoyama.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Útil para buscar o usuário pelo e-mail (geralmente usado no login/autenticação)
    Optional<User> findByEmail(String email);
}