package br.com.aoyama.service;

import br.com.aoyama.exception.GoogleDriveAuthenticationException;
import br.com.aoyama.exception.GoogleDriveUploadException;
import br.com.aoyama.model.User;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.UserCredentials;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.Date;

@Service
public class GoogleDriveService {

    private static final String FOLDER_NAME = "AtipicoAI";

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;

    /**
     * Faz o upload de um arquivo PDF para o Google Drive usando o Token OAuth da usuária.
     *
     * @O accessToken Token de acesso OAuth obtido no login da usuária.
     * @param multipartFile O arquivo PDF enviado via requisição web.
     * @return Objeto File contendo o ID e o link do arquivo no Drive.
     */
    public File enviarPdfParaDrive(User user, MultipartFile multipartFile) {
        try {
            // 1. Cria o AccessToken a partir do usuário
            AccessToken accessTokenObj = new AccessToken(
                    user.getGoogleAccessToken(), new Date(System.currentTimeMillis() + 3600 * 1000));

            // 2. Configura o UserCredentials completo com suporte a refresh token
            UserCredentials credentials = UserCredentials.newBuilder()
                    .setClientId(clientId)
                    .setClientSecret(clientSecret)
                    .setAccessToken(accessTokenObj)
                    .setRefreshToken(user.getGoogleRefreshToken())
                    .build();

            // 3. Constrói o cliente oficial da API do Google Drive v3
            Drive driveService = new Drive.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    GsonFactory.getDefaultInstance(),
                    new HttpCredentialsAdapter(credentials))
                    .setApplicationName("AtipicoAI")
                    .build();

            // 2. Garante que a pasta "AtipicoAI" existe (busca ou cria)
            String folderId = obterOuCriarPastaAtipicoAI(driveService);

            // 3. Define os metadados do arquivo (Nome que ele terá no Google Drive)
            File fileMetadata = new File();
            fileMetadata.setName(multipartFile.getOriginalFilename());
            fileMetadata.setParents(Collections.singletonList(folderId)); // Organiza dentro da pasta AtipicoAI

            // 4. Prepara o conteúdo do arquivo vindo do MultipartFile
            InputStreamContent mediaContent = new InputStreamContent(
                    multipartFile.getContentType(),
                    new ByteArrayInputStream(multipartFile.getBytes())
            );

            // 5. Executa o upload para o Google Drive da usuária

            return driveService.files().create(fileMetadata, mediaContent)
                    .setFields("id, webViewLink, name")
                    .execute();

        } catch (IOException e) {
            // Verifica se o erro está relacionado a credenciais inválidas ou expiradas
            if (e.getMessage() != null && (e.getMessage().contains("invalid_grant") || e.getMessage().contains("Token"))) {
                throw new GoogleDriveAuthenticationException("Sessão do Google expirada ou inválida. Faça login novamente.", e);
            }
            // Demais erros de rede ou upload
            throw new GoogleDriveUploadException("Erro ao comunicar com o Google Drive durante o upload: " + e.getMessage(), e);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException("Erro de segurança ao configurar transporte HTTP para o Google.", e);
        }
    }

    private String obterOuCriarPastaAtipicoAI(Drive driveService) throws IOException {
        String query = "mimeType = 'application/vnd.google-apps.folder' and name = '" + FOLDER_NAME + "' and trashed = false";

        FileList result = driveService.files().list()
                .setQ(query)
                .setSpaces("drive")
                .setFields("files(id, name)")
                .execute();

        // Se a pasta já existir, retorna o ID dela
        if (result.getFiles() != null && !result.getFiles().isEmpty()) {
            return result.getFiles().get(0).getId();
        }

        // Se não existir, cria a pasta "AtipicoAI" na raiz do Drive
        File folderMetadata = new File();
        folderMetadata.setName(FOLDER_NAME);
        folderMetadata.setMimeType("application/vnd.google-apps.folder");

        File folder = driveService.files().create(folderMetadata)
                .setFields("id")
                .execute();

        return folder.getId();
    }
}