package com.lifeos.be.core.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Global Exception Handler (Inbound Adapter — Error Handling).
 * <p>
 * Mengubah seluruh exception yang terlempar dari lapisan Application dan Domain
 * menjadi respons HTTP yang terstruktur dan konsisten menggunakan format RFC 9457
 * {@link ProblemDetail}.
 * <p>
 * Format respons JSON yang dikembalikan ke client:
 * <pre>
 * {
 *   "type": "https://lifeos.com/errors/validation-failed",
 *   "title": "Validation Failed",
 *   "status": 400,
 *   "detail": "Request body contains invalid field values.",
 *   "errors": { "email": "must not be blank" }
 * }
 * </pre>
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Menangkap pelanggaran aturan bisnis domain (Illegal State).
     * Contoh: konflik waktu activity, rule bisnis dilanggar.
     */
    @ExceptionHandler(IllegalStateException.class)
    public ProblemDetail handleIllegalStateException(IllegalStateException ex) {
        log.warn("Business rule violation: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problem.setTitle("Business Rule Violation");
        problem.setType(URI.create("https://lifeos.com/errors/business-rule-violation"));
        return problem;
    }

    /**
     * Menangkap argumen yang tidak valid (misalnya: data tidak ditemukan di repository).
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgumentException(IllegalArgumentException ex) {
        log.warn("Invalid argument: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problem.setTitle("Invalid Request");
        problem.setType(URI.create("https://lifeos.com/errors/invalid-argument"));
        return problem;
    }

    /**
     * Menangkap percobaan akses tidak sah (ownership violation dari domain entity).
     * Contoh: user mencoba toggle/delete activity milik user lain.
     */
    @ExceptionHandler(SecurityException.class)
    public ProblemDetail handleSecurityException(SecurityException ex) {
        log.warn("Unauthorized resource access attempt: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ex.getMessage());
        problem.setTitle("Access Denied");
        problem.setType(URI.create("https://lifeos.com/errors/access-denied"));
        return problem;
    }

    /**
     * Menangkap user yang tidak ditemukan di database.
     */
    @ExceptionHandler(UsernameNotFoundException.class)
    public ProblemDetail handleUsernameNotFoundException(UsernameNotFoundException ex) {
        log.warn("User not found: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("User Not Found");
        problem.setType(URI.create("https://lifeos.com/errors/user-not-found"));
        return problem;
    }

    /**
     * Menangkap kegagalan validasi request body (annotation @Valid / @NotBlank / @NotNull, dll.).
     * Mengembalikan daftar semua field yang gagal beserta alasannya.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fieldError -> fieldError.getDefaultMessage() != null
                                ? fieldError.getDefaultMessage()
                                : "Invalid value",
                        (first, second) -> first // jika ada duplikat field, ambil yang pertama
                ));

        log.warn("Validation failed: {}", errors);

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "Request body contains invalid field values."
        );
        problem.setTitle("Validation Failed");
        problem.setType(URI.create("https://lifeos.com/errors/validation-failed"));
        problem.setProperty("errors", errors);
        return problem;
    }

    /**
     * Fallback handler untuk semua exception yang tidak tertangani.
     * Memastikan tidak ada stack trace yang bocor ke client.
     */
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGenericException(Exception ex) {
        log.error("Unexpected error occurred: {}", ex.getMessage(), ex);
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred. Please try again later."
        );
        problem.setTitle("Internal Server Error");
        problem.setType(URI.create("https://lifeos.com/errors/internal-server-error"));
        return problem;
    }
}
