package br.com.controlefinaceiro.financeiroapi.utils.excecoes.api;

import br.com.controlefinaceiro.financeiroapi.utils.excecoes.BusinessException;
import org.springframework.validation.BindingResult;

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

    public List<String> getErrors() {
        return errors;
    }
}
