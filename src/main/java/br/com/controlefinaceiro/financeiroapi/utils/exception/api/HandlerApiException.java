package br.com.controlefinaceiro.financeiroapi.utils.exception.api;

import br.com.controlefinaceiro.financeiroapi.utils.exception.BusinessException;
import com.fasterxml.jackson.databind.exc.ValueInstantiationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class HandlerApiException {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity handleValidationException(MethodArgumentNotValidException ex){
        BindingResult bindingResult = ex.getBindingResult();
        ApiErrors apiErrors = new ApiErrors(bindingResult);
        Map<String, Object> body = getStringObjectMapException(apiErrors);
        return new ResponseEntity(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity handleBusinessException(BusinessException ex){
        ApiErrors apiErrors = new ApiErrors(ex);
        Map<String, Object> body = getStringObjectMapException(apiErrors);
        return new ResponseEntity(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity handleResponseStatusException(ResponseStatusException ex){
        ApiErrors apiErrors = new ApiErrors(ex);
        Map<String, Object> body = getStringObjectMapException(apiErrors);
        return new ResponseEntity(body, ex.getStatus());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity handleIllegalArgumentException(IllegalArgumentException ex){
        ApiErrors apiErrors = new ApiErrors(ex);
        Map<String, Object> body = getStringObjectMapException(apiErrors);
        return new ResponseEntity(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ValueInstantiationException.class)
    public ResponseEntity handleHttpMessageConversionException(ValueInstantiationException ex){
        ApiErrors apiErrors = new ApiErrors(ex);
        Map<String, Object> body = getStringObjectMapException(apiErrors);
        return new ResponseEntity(body, HttpStatus.BAD_REQUEST);
    }

    private Map<String, Object> getStringObjectMapException(ApiErrors errors) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", errors);
        return body;
    }
}
