package br.com.controlefinaceiro.financeiroapi.goal.entity;

import br.com.controlefinaceiro.financeiroapi.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "Goal")
@Table(name = "goal")
public class Goal {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Mes obrigatorio")
    @Column
    private Long month;

    @NotNull(message = "Ano obrigatorio")
    @Column
    private Long year;

    @NotNull(message = "Valor obrigatorio")
    @Column
    private BigDecimal value;

    @NotNull(message = "Usuario obrigatorio")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_user")
    private User user;
}
