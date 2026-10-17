package br.org.itinerario.api;

import java.util.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class ApiErrors {

    @ExceptionHandler(br.org.itinerario.application.BusinessException.class)
    ResponseEntity<?> business(br.org.itinerario.application.BusinessException error) {
        return ResponseEntity.status(error.status).body(Map.of("error", error.getMessage()));
    }

    @ExceptionHandler(org.springframework.dao.ConcurrencyFailureException.class)
    ResponseEntity<?> concurrentUpdate() {
        return ResponseEntity.status(409).body(
            Map.of("error", "Registro alterado por outra operação. Atualize e tente novamente.")
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<?> validation(MethodArgumentNotValidException error) {
        Map<String, String> fields = new LinkedHashMap<>();
        error
            .getBindingResult()
            .getFieldErrors()
            .forEach(field -> fields.putIfAbsent(field.getField(), field.getDefaultMessage()));
        return ResponseEntity.badRequest().body(
            Map.of("error", "Revise os campos informados.", "fields", fields)
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<?> malformed() {
        return ResponseEntity.badRequest().body(
            Map.of("error", "Dados inválidos ou campos não permitidos.")
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    ResponseEntity<?> conflict() {
        return ResponseEntity.status(409).body(
            Map.of("error", "A operação conflita com um registro existente.")
        );
    }
}
