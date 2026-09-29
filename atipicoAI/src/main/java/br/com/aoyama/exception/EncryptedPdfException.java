package br.com.aoyama.exception;

public class EncryptedPdfException extends RuntimeException {
    public EncryptedPdfException(String message) {
        super(message);
    }
}