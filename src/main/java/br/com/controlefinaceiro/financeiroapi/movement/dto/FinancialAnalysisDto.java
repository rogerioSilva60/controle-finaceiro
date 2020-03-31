package br.com.controlefinaceiro.financeiroapi.movement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinancialAnalysisDto {

    private BigDecimal valueTotalRecipe;
    private BigDecimal valueTotalExpence;
    private BigDecimal valueGoal;
    private BigDecimal valueTotalBalance;
    private BigDecimal valueMonthlySpend;
    private String message;

}
