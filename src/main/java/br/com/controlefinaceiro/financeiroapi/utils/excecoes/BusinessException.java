package br.com.controlefinaceiro.financeiroapi.utils.excecoes;

public class BusinessException extends RuntimeException {
    public BusinessException(String mensagemError) {
        super(mensagemError);
    }
}
