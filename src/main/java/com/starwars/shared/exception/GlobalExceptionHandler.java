package com.starwars.shared.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;

import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.context.request.WebRequest;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Métodos auxiliares para reducir duplicación
    private ErrorResponse buildErrorResponse(HttpStatus status, String error, String message, WebRequest request) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(error)
                .message(message)
                .path(extractPath(request))
                .build();
    }

    private ErrorResponse buildErrorResponse(HttpStatus status, String error, String message, WebRequest request, Map<String, String> validationErrors) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(error)
                .message(message)
                .path(extractPath(request))
                .validationErrors(validationErrors)
                .build();
    }

    private String extractPath(WebRequest request) {
        return request.getDescription(false).replace("uri=", "");
    }

    @ExceptionHandler(ResourceAccessException.class)
    public ResponseEntity<ErrorResponse> handleResourceAccessException(ResourceAccessException ex, WebRequest request) {
        log.error("Error de conexión con API externa: ", ex);
        ErrorResponse error = buildErrorResponse(
                HttpStatus.SERVICE_UNAVAILABLE,
                "Servicio No Disponible",
                "La API de Star Wars no está disponible temporalmente. Por favor, inténtalo más tarde.",
                request
        );
        return new ResponseEntity<>(error, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<ErrorResponse> handleHttpClientErrorException(HttpClientErrorException ex, WebRequest request) {
        log.error("Error de cliente en API externa: ", ex);

        HttpStatus status = ex.getStatusCode();
        String message = "Error al comunicarse con la API de Star Wars";

        if (status == HttpStatus.NOT_FOUND) {
            message = "El recurso solicitado no fue encontrado";
        } else if (status == HttpStatus.BAD_REQUEST) {
            message = "Parámetros de solicitud inválidos";
        }

        ErrorResponse error = buildErrorResponse(status, status.getReasonPhrase(), message, request);
        return new ResponseEntity<>(error, status);
    }

    @ExceptionHandler(HttpServerErrorException.class)
    public ResponseEntity<ErrorResponse> handleHttpServerErrorException(HttpServerErrorException ex, WebRequest request) {
        log.error("Error de servidor en API externa: ", ex);

        ErrorResponse error = buildErrorResponse(
                HttpStatus.SERVICE_UNAVAILABLE,
                "Servicio No Disponible",
                "La API de Star Wars está experimentando problemas de servidor. Por favor, inténtalo más tarde.",
                request
        );

        return new ResponseEntity<>(error, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, String> validationErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                validationErrors.put(error.getField(), error.getDefaultMessage()));

        ErrorResponse error = buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Validación Fallida",
                "Los datos de entrada no son válidos",
                request,
                validationErrors
        );

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException ex, WebRequest request) {
        Map<String, String> validationErrors = new HashMap<>();
        ex.getConstraintViolations().forEach(violation ->
                validationErrors.put(violation.getPropertyPath().toString(), violation.getMessage()));

        ErrorResponse error = buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Validación Fallida",
                "Los parámetros de la solicitud no son válidos",
                request,
                validationErrors
        );

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex, WebRequest request) {
        log.error("Error al leer el cuerpo de la solicitud: ", ex);

        ErrorResponse error = buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Solicitud Inválida",
                "El cuerpo de la solicitud no es válido o está mal formateado",
                request
        );

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex, WebRequest request) {
        log.error("Método HTTP no soportado: ", ex);

        ErrorResponse error = buildErrorResponse(
                HttpStatus.METHOD_NOT_ALLOWED,
                "Método No Permitido",
                "El método HTTP '" + ex.getMethod() + "' no está soportado para este endpoint",
                request
        );

        return new ResponseEntity<>(error, HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(BadCredentialsException ex, WebRequest request) {
        log.error("Credenciales inválidas: ", ex);

        ErrorResponse error = buildErrorResponse(
                HttpStatus.UNAUTHORIZED,
                "No Autorizado",
                "Credenciales inválidas. Verifica tu nombre de usuario y contraseña.",
                request
        );

        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
        log.error("Acceso denegado: ", ex);

        ErrorResponse error = buildErrorResponse(
                HttpStatus.FORBIDDEN,
                "Acceso Denegado",
                "No tienes permisos para acceder a este recurso",
                request
        );

        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        log.error("Recurso no encontrado: ", ex);

        ErrorResponse error = buildErrorResponse(
                HttpStatus.NOT_FOUND,
                "No Encontrado",
                ex.getMessage(),
                request
        );

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        log.error("Argumento ilegal: ", ex);

        ErrorResponse error = buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Solicitud Inválida",
                ex.getMessage(),
                request
        );

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, WebRequest request) {
        log.error("Error interno del servidor: ", ex);

        ErrorResponse error = buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Error Interno del Servidor",
                "Ha ocurrido un error inesperado. Por favor, inténtalo más tarde.",
                request
        );

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

