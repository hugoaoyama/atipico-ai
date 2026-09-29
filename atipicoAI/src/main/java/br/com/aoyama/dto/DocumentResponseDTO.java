package br.com.aoyama.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentResponseDTO {
    private String fileId;
    private String fileName;
    private String webViewLink;
    private String message;
}
