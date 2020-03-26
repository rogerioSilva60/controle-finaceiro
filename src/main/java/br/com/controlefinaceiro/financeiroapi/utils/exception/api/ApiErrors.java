package br.com.controlefinaceiro.financeiroapi.utils.exception.api;

import br.com.controlefinaceiro.financeiroapi.utils.exception.BusinessException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.ValueInstantiationException;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.validation.BindingResult;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ApiErrors {

    public List<String> errors;

    public ApiErrors(BindingResult bindingResult) {
      this.errors = new ArrayList<String>();
      bindingResult.getAllErrors().forEach(error -> this.errors.add(error.getDefaultMessage()));
    }

    public ApiErrors(BusinessException ex) {
        this.errors = Arrays.asList(ex.getMessage());
    }

    public ApiErrors(ResponseStatusException ex){
        this.errors = Arrays.asList(ex.getMessage());
    }

    public ApiErrors(IllegalArgumentException ex) {
        this.errors = Arrays.asList(ex.getMessage());
    }

    public ApiErrors(ValueInstantiationException ex) {
        this.errors = Arrays.asList(ex.getMessage());
    }

    public List<String> getErrors() {
        return errors;
    }
}
