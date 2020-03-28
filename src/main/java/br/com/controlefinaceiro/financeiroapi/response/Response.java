package br.com.controlefinaceiro.financeiroapi.response;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Response<T>{

    private T data;
    private List<String> errors = new ArrayList<>();

    public Response() {

    }
}
