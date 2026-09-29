package br.com.aoyama.service;

import br.com.aoyama.exception.EncryptedPdfException;
import br.com.aoyama.exception.OcrProcessingException;
import br.com.aoyama.exception.PdfProcessingException;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

@Service
public class PdfService {

    /**
     * Extrai o texto de um arquivo MultipartFile enviado via requisição HTTP.
     */
    public String extrairTextoDeMultipartFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("O arquivo PDF enviado está vazio.");
        }

        try (InputStream inputStream = file.getInputStream()) {
            byte[] bytesPdf = inputStream.readAllBytes();
            return extrairTextoDeBytes(bytesPdf);
        } catch (IOException e) {
            throw new PdfProcessingException("Erro ao ler o fluxo de dados do arquivo PDF: " + e.getMessage(), e);
        }
    }

    /**
     * Extrai o texto de um array de bytes utilizando o Loader do PDFBox 3.x.
     */
    public String extrairTextoDeBytes(byte[] pdfBytes) {
        try (PDDocument document = Loader.loadPDF(pdfBytes)) {

            if (document.isEncrypted()) {
                throw new EncryptedPdfException("O PDF está protegido por senha e não pode ser lido automaticamente.");
            }

            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);

            return stripper.getText(document);

        } catch (EncryptedPdfException e) {
            throw e; // Propaga a exceção específica de criptografia
        } catch (IOException e) {
            throw new PdfProcessingException("Erro ao processar e extrair o texto do PDF usando Apache PDFBox: " + e.getMessage(), e);
        }
    }

    /**
     * Realiza OCR em PDFs escaneados (baseados em imagem).
     */
    public String extractTextFromScannedPdf(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("O arquivo PDF enviado está vazio.");
        }

        StringBuilder resultText = new StringBuilder();

        try (InputStream inputStream = file.getInputStream();
             PDDocument document = Loader.loadPDF(inputStream.readAllBytes())) {

            PDFRenderer pdfRenderer = new PDFRenderer(document);
            Tesseract tesseract = new Tesseract();

            String tessDataPath = "src/main/resources/tessdata";
            tesseract.setDatapath(tessDataPath);
            tesseract.setLanguage("por"); // Português

            for (int page = 0; page < document.getNumberOfPages(); page++) {
                BufferedImage bim = pdfRenderer.renderImageWithDPI(page, 300);
                String text = tesseract.doOCR(bim);
                resultText.append(text).append("\n");
            }
        } catch (IOException e) {
            throw new PdfProcessingException("Erro de IO ao renderizar páginas do PDF escaneado: " + e.getMessage(), e);
        } catch (TesseractException e) {
            throw new OcrProcessingException("Erro ao processar o OCR (reconhecimento de texto) na imagem do PDF: " + e.getMessage(), e);
        }

        return resultText.toString();
    }
}