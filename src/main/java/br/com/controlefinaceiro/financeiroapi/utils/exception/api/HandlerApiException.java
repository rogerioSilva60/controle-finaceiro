package br.com.controlefinaceiro.financeiroapi.utils.exception.api;

import br.com.controlefinaceiro.financeiroapi.response.Response;
import br.com.controlefinaceiro.financeiroapi.utils.exception.BusinessException;
import com.fasterxml.jackson.databind.exc.ValueInstantiationException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;


@RestControllerAdvice
public class HandlerApiException {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity handleValidationException(MethodArgumentNotValidException ex){
        BindingResult bindingResult = ex.getBindingResult();
        return ResponseEntity.badRequest().body(getResponseException(new ApiErrors(bindingResult)));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity handleBusinessException(BusinessException ex){
        return ResponseEntity.badRequest().body(getResponseException(new ApiErrors(ex)));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity handleResponseStatusException(ResponseStatusException ex){
        return ResponseEntity.status(ex.getStatus()).body(getResponseException(new ApiErrors(ex)));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity handleIllegalArgumentException(IllegalArgumentException ex){
        return ResponseEntity.badRequest().body(getResponseException(new ApiErrors(ex)));
    }

    @ExceptionHandler(ValueInstantiationException.class)
    public ResponseEntity handleHttpMessageConversionException(ValueInstantiationException ex){
        return ResponseEntity.badRequest().body(getResponseException(new ApiErrors(ex)));
    }

    private Response getResponseException(ApiErrors apiErrors) {
        Response response = new Response<>();
        response.setErrors(apiErrors.getErrors());
        return response;
    }
}
