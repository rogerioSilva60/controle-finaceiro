package br.com.controlefinaceiro.financeiroapi.utils.excecoes.api;

import br.com.controlefinaceiro.financeiroapi.utils.excecoes.BusinessException;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ApiErros {

    public List<String> errors;

    public ApiErros(BindingResult bindingResult) {
      this.errors = new ArrayList<String>();
      bindingResult.getAllErrors().forEach(error -> this.errors.add(error.getDefaultMessage()));
    }

    public ApiErros(BusinessException ex) {
        this.errors = Arrays.asList(ex.getMessage());
    }

    public List<String> getErrors() {
        return errors;
    }
}
