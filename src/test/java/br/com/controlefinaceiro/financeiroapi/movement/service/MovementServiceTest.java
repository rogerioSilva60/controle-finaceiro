package br.com.controlefinaceiro.financeiroapi.movement.service;

import br.com.controlefinaceiro.financeiroapi.movement.entity.Movement;
import br.com.controlefinaceiro.financeiroapi.movement.repository.MovementRepository;
import br.com.controlefinaceiro.financeiroapi.movement.service.impl.MovementServiceImpl;
import br.com.controlefinaceiro.financeiroapi.user.entity.User;
import br.com.controlefinaceiro.financeiroapi.utils.DateTime;
import br.com.controlefinaceiro.financeiroapi.utils.constant.CashFlow;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class MovementServiceTest {

    MovementService service;

    @MockBean
    MovementRepository repository;

    //Executa antes de cada metodo de teste
    @BeforeEach
    public void setUp(){
        this.service = new MovementServiceImpl(repository);
    }

    @Test
    @DisplayName("Deve salvar um usuario.")
    public void saveMovementTest() throws Exception{
        //cenario
        Movement movement = getMovement();
        Mockito.when(repository.existsById(Mockito.anyLong())).thenReturn(false);
        //Simula um objeto criado no banco de dados.
        User user = movement.getUser();
        user.setId(1l);
        CashFlow cashFlow = CashFlow.fromValue("DESPESA");
        Mockito.when(repository.save(movement)).thenReturn(
                Movement.builder().id(1l)
                        .description(movement.getDescription())
                        .value(movement.getValue())
                        .dueDate(movement.getDueDate())
                        .payDay(movement.getPayDay())
                        .user(user)
                        .cashFlow(cashFlow)
                        .build()
        );

        //execucao
        Movement movementSave = service.save(movement);

        //verificacao
        assertThat(movementSave.getId()).isNotNull();
        assertThat(movementSave.getDescription()).isEqualTo(movement.getDescription());
        assertThat(movementSave.getValue()).isEqualTo(movement.getValue());
        assertThat(movementSave.getUser()).isNotNull();
        assertThat(movementSave.getUser().getId()).isNotNull();
        assertThat(movementSave.getCashFlow()).isNotNull();
        assertThat(movementSave.getCashFlow()).isEqualTo(cashFlow);
    }

    private Movement getMovement() {
        CashFlow cashFlow = CashFlow.fromValue("DESPESA");
        return Movement.builder()
                .description("Energia")
                .value(new BigDecimal(150))
                .dueDate(DateTime.create(26,03,2020))
                .payDay(DateTime.create(26,03,2020))
                .user(getUsuario())
                .cashFlow(cashFlow)
                .build();
    }

    private User getUsuario() {
        return User.builder().name("Fulano").phone("558500000000")
                .email("fulano@gmail.com").birthDate(DateTime.create(30,3,1987)).build();
    }
}
