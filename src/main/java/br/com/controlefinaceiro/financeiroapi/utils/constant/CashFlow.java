package br.com.controlefinaceiro.financeiroapi.utils.constant;

import java.util.Arrays;

public enum CashFlow {

    RECIPE("Receita"),
    EXPENCE("Despesa");

    public String value;
    CashFlow(String value) {
        this.value = value;
    }

    public static CashFlow fromValue(String value) {
        if(value != null){
            for (CashFlow type : values()) {
                if (type.value.equalsIgnoreCase(value)) {
                    return type;
                }
            }
        }
        throw new IllegalArgumentException(
                "Tipo de enumeração desconhecido " + value + ", Os valores permitidos são " + Arrays.toString(values()));
    }
}
