package br.com.controlefinaceiro.financeiroapi.movement.entity;

import br.com.controlefinaceiro.financeiroapi.user.entity.User;
import br.com.controlefinaceiro.financeiroapi.utils.constant.TypeCashFlow;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "Movement")
@Table(name = "movement")
public class Movement {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "Descrição obrigatoria")
    @Column(nullable = false)
    private String description;

    @NotNull(message = "Valor obrigatorio")
    @Column
    private BigDecimal value;

    @NotNull(message = "Dia de pagamento obrigatorio")
    @Column
    private Date payDay;

    @NotNull(message = "Dia de vencimento obrigatorio")
    @Column
    private Date dueDate;

    @NotNull(message = "Usuario obrigatorio")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_user")
    private User user;


    @Enumerated(EnumType.STRING)
    @NotNull(message = "Fluxo de caixa e obrigatorio. Os valores permitidos para 'typeCashFlow' sao Receita ou Despesa.")
    @Column(name = "type_cash_flow")
    private TypeCashFlow typeCashFlow;
}
