package br.com.controlefinaceiro.financeiroapi.utils;

import java.math.BigDecimal;

public abstract class Calculator {

    public static BigDecimal percentageValue(BigDecimal valueInitial, BigDecimal valueTotal){
        if(valueInitial.doubleValue() == 0 || valueTotal.doubleValue() == 0){
            return new BigDecimal(0);
        }
        return valueInitial.multiply(new BigDecimal(100)).divide(valueTotal);
    }
}
