package br.com.aoyama.exception;

public class GoogleDriveAuthenticationException extends RuntimeException {
    public GoogleDriveAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
