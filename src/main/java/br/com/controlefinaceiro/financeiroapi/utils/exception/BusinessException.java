package br.com.controlefinaceiro.financeiroapi.utils.exception;

public class BusinessException extends RuntimeException {
    public BusinessException(String mensagemError) {
        super(mensagemError);
    }
}
