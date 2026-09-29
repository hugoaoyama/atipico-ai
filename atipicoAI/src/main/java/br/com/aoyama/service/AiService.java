package br.com.aoyama.service;

import br.com.aoyama.exception.AiServiceException;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class AiService {

    private final ChatClient chatClient;

    // O Spring AI injeta o ChatClient configurado automaticamente
    public AiService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    public String gerarResumoClinico(String textoExtraidoDoPdf) {
        if (textoExtraidoDoPdf == null || textoExtraidoDoPdf.isBlank()) {
            return "Não foi possível extrair texto suficiente para gerar um resumo.";
        }

        String prompt = "Você é um assistente médico especializado em apoiar mães de crianças atípicas (como TEA). " +
                "Analise o texto clínico extraído de um documento médico abaixo e elabore um resumo objetivo e estruturado contendo: " +
                "1. Diagnósticos ou hipóteses mencionadas. " +
                "2. Principais evoluções ou marcos apresentados. " +
                "3. Recomendações de terapias ou condutas sugeridas pelo profissional. " +
                "\n\nTexto do documento:\n" + textoExtraidoDoPdf;

        try {
            return this.chatClient.prompt()
                    .user(prompt)
                    .call()
                    .content();
        } catch (Exception e) {
            // Lança a exceção para que o GlobalExceptionHandler trate e informe o cliente adequadamente
            throw new AiServiceException("Erro ao comunicar com o serviço de Inteligência Artificial: " + e.getMessage(), e);
        }
    }
}