package br.com.controlefinaceiro.financeiroapi.movement.dto;

import br.com.controlefinaceiro.financeiroapi.user.dto.UserDto;
import br.com.controlefinaceiro.financeiroapi.utils.constant.TypeCashFlow;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovementDto {

    private Long id;

    @NotEmpty(message = "Descrição obrigatória")
    private String description;

    @NotNull(message = "Valor obrigatório")
    private BigDecimal value;

    @NotNull(message = "Dia de pagamento obrigatório")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "America/Fortaleza")
    private Date payDay;

    @NotNull(message = "Dia de vencimento obrigatório")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "America/Fortaleza")
    private Date dueDate;

    @NotNull(message = "Usuário obrigatório.")
    private UserDto user;

    @NotNull(message = "Fluxo de caixa e obrigatorio. Os valores permitidos para 'typeCashFlow' sao Receita ou Despesa.")
    private TypeCashFlow typeCashFlow;

}
