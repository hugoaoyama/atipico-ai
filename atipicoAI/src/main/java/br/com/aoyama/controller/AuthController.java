package br.com.aoyama.controller;

import br.com.aoyama.model.User;
import br.com.aoyama.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserService userService;
    private final OAuth2AuthorizedClientService authorizedClientService;

    public AuthController(UserService userService, OAuth2AuthorizedClientService authorizedClientService) {
        this.userService = userService;
        this.authorizedClientService = authorizedClientService;
    }

    @GetMapping("/sucesso")
    public ResponseEntity<String> loginSucesso(
            OAuth2AuthenticationToken authenticationToken,
            @AuthenticationPrincipal OAuth2User oauth2User) {

        // Recupera o cliente autorizado (contendo os tokens) usando os serviços do Spring Security
        OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(
                authenticationToken.getAuthorizedClientRegistrationId(),
                authenticationToken.getName());

        // Delega toda a regra de negócio e persistência para o Service
        User usuarioSalvo = userService.processarLoginGoogle(authenticationToken, oauth2User, client);

        return ResponseEntity.ok("Login realizado com sucesso para: " + usuarioSalvo.getName() + " (" + usuarioSalvo.getEmail() + ")");
    }

    @GetMapping("/user")
    public ResponseEntity<Map<String, Object>> getCurrentUser(@AuthenticationPrincipal OAuth2User principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Usuário não autenticado"));
        }

        String name = principal.getAttribute("name");
        String email = principal.getAttribute("email");
        String picture = principal.getAttribute("picture");

        return ResponseEntity.ok(Map.of(
                "name", name != null ? name : "Usuário",
                "email", email != null ? email : "",
                "picture", picture != null ? picture : ""
        ));
    }
}