package br.com.controlefinaceiro.financeiroapi.goal.dto;

import br.com.controlefinaceiro.financeiroapi.user.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoalDto {

    private Long id;

    @NotNull(message = "Mes obrigatorio.")
    @Min(value = 1, message = "valor minimo para mes e 1")
    @Max(value = 12, message = "valor maximo para mes e 12")
    private Long month;

    @NotNull(message = "Ano obrigatorio.")
    @Min(value = 2020, message = "valor minimo para ano e 2020")
    private Long year;

    @NotNull(message = "Valor da meta obrigatorio.")
    private BigDecimal value;

    @NotNull(message = "Usuário obrigatório.")
    private UserDto user;
}
