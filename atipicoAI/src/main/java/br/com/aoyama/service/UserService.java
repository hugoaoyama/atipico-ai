package br.com.aoyama.service;

import br.com.aoyama.exception.ResourceNotFoundException;
import br.com.aoyama.model.User;
import br.com.aoyama.repository.UserRepository;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User buscarPorEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado no sistema."));
    }

    /**
     * Processa o login do usuário autenticado via Google, extrai os tokens OAuth
     * e persiste ou atualiza os dados no banco de dados PostgreSQL.
     */
    public User processarLoginGoogle(OAuth2AuthenticationToken authenticationToken, OAuth2User oauth2User, OAuth2AuthorizedClient authorizedClient) {
        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name");

        String accessToken = authorizedClient.getAccessToken().getTokenValue();
        String refreshToken = authorizedClient.getRefreshToken() != null
                ? authorizedClient.getRefreshToken().getTokenValue()
                : null;

        // Busca o usuário existente ou cria um novo
        User user = userRepository.findByEmail(email).orElseGet(User::new);

        user.setName(name);
        user.setEmail(email);
        user.setGoogleAccessToken(accessToken);

        // O Google nem sempre envia o refresh_token no login recorrente, por isso validamos
        if (refreshToken != null) {
            user.setGoogleRefreshToken(refreshToken);
        }

        return userRepository.save(user);
    }
}