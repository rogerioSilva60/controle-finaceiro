package br.com.controlefinaceiro.financeiroapi.utils.constant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

public enum TypeCashFlow {

    RECIPE("Receita"),
    EXPENCE("Despesa");

    private String key;

    TypeCashFlow(String key) {
        this.key = key;
    }

    @JsonValue
    public String getKey() { return this.key; }

    @JsonCreator
    public static TypeCashFlow fromValue(String value) {
        if(value != null){
            for (TypeCashFlow type : values()) {
                if (type.key.equalsIgnoreCase(value)) {
                    return type;
                }
            }
        }
        throw new IllegalArgumentException(
                "Tipo de enumeração desconhecido '" + value + "', Os valores permitidos para 'typeCashFlow' são " + Arrays.toString(values()));
    }

    @Override
    public String toString() { return key; }
}
