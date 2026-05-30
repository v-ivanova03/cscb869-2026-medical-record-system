package org.example.medical_record.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

// @ControllerAdvice (не @RestControllerAdvice) хваща грешки от
// ВСИЧКИ контролери — и REST (връща JSON) и MVC (връща HTML)
// Разграничаваме по Accept header — ако клиентът иска JSON, даваме JSON
@ControllerAdvice
public class GlobalExceptionHandler {

    // ---------------------------------------------------------------
    // NoSuchElementException → 404
    // ---------------------------------------------------------------
    @ExceptionHandler(NoSuchElementException.class)
    public Object handleNotFound(NoSuchElementException ex,
                                 HttpServletRequest request) {
        if (isApiRequest(request)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(errorBody(HttpStatus.NOT_FOUND, ex.getMessage()));
        }
        return mvcError(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    // ---------------------------------------------------------------
    // Валидационни грешки (@Valid) → 400  — само за REST
    // MVC формите ги обработват сами чрез BindingResult
    // ---------------------------------------------------------------
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(
            MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(fe.getField(), fe.getDefaultMessage());
        }
        Map<String, Object> body = errorBody(HttpStatus.BAD_REQUEST, "Грешка при валидация");
        body.put("fields", fieldErrors);
        return ResponseEntity.badRequest().body(body);
    }

    // ---------------------------------------------------------------
    // AccessDeniedException → 403
    // ---------------------------------------------------------------
    @ExceptionHandler(AccessDeniedException.class)
    public Object handleForbidden(AccessDeniedException ex,
                                  HttpServletRequest request) {
        if (isApiRequest(request)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(errorBody(HttpStatus.FORBIDDEN, "Нямате достъп до този ресурс"));
        }
        return mvcError(HttpStatus.FORBIDDEN, "Нямате право да достъпите тази страница");
    }

    // ---------------------------------------------------------------
    // IllegalArgumentException → 400
    // ---------------------------------------------------------------
    @ExceptionHandler(IllegalArgumentException.class)
    public Object handleBadRequest(IllegalArgumentException ex,
                                   HttpServletRequest request) {
        if (isApiRequest(request)) {
            return ResponseEntity.badRequest()
                    .body(errorBody(HttpStatus.BAD_REQUEST, ex.getMessage()));
        }
        return mvcError(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    // ---------------------------------------------------------------
    // Всичко останало → 500
    // ---------------------------------------------------------------
    @ExceptionHandler(Exception.class)
    public Object handleGeneral(Exception ex, HttpServletRequest request) {
        if (isApiRequest(request)) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorBody(HttpStatus.INTERNAL_SERVER_ERROR,
                            "Възникна неочаквана грешка. Свържете се с администратора."));
        }
        return mvcError(HttpStatus.INTERNAL_SERVER_ERROR,
                "Възникна неочаквана грешка. Свържете се с администратора.");
    }

    // ---------------------------------------------------------------
    // Помощни методи
    // ---------------------------------------------------------------

    // Проверява дали заявката е към REST API (/api/**) или към MVC
    private boolean isApiRequest(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String accept = request.getHeader("Accept");
        return uri.startsWith("/api/") ||
                (accept != null && accept.contains("application/json"));
    }

    // JSON тяло за REST грешки
    private Map<String, Object> errorBody(HttpStatus status, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status",    status.value());
        body.put("error",     status.getReasonPhrase());
        body.put("message",   message);
        return body;
    }

    // ModelAndView за HTML грешки — препраща към templates/error.html
    private ModelAndView mvcError(HttpStatus status, String message) {
        ModelAndView mv = new ModelAndView("error");
        mv.addObject("status",  status.value());
        mv.addObject("error",   status.getReasonPhrase());
        mv.addObject("message", message);
        mv.setStatus(status);
        return mv;
    }
}
