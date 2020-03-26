package br.com.controlefinaceiro.financeiroapi.movement.service;

import br.com.controlefinaceiro.financeiroapi.movement.entity.Movement;
import br.com.controlefinaceiro.financeiroapi.movement.repository.MovementRepository;
import br.com.controlefinaceiro.financeiroapi.movement.service.impl.MovementServiceImpl;
import br.com.controlefinaceiro.financeiroapi.user.entity.User;
import br.com.controlefinaceiro.financeiroapi.utils.DateTime;
import br.com.controlefinaceiro.financeiroapi.utils.constant.TypeCashFlow;
import br.com.controlefinaceiro.financeiroapi.utils.exception.BusinessException;
import org.assertj.core.api.Assertions;
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
    @DisplayName("Deve salvar uma movimentacao.")
    public void saveMovementTest() throws Exception{
        //cenario
        Movement movement = getMovement();
        Mockito.when(repository.existsById(Mockito.anyLong())).thenReturn(false);
        //Simula um objeto criado no banco de dados.
        User user = movement.getUser();
        user.setId(1l);
        TypeCashFlow cashFlow = TypeCashFlow.fromValue("DESPESA");
        Mockito.when(repository.save(movement)).thenReturn(
                Movement.builder().id(1l)
                        .description(movement.getDescription())
                        .value(movement.getValue())
                        .dueDate(movement.getDueDate())
                        .payDay(movement.getPayDay())
                        .user(user)
                        .typeCashFlow(cashFlow)
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
        assertThat(movementSave.getTypeCashFlow()).isNotNull();
        assertThat(movementSave.getTypeCashFlow()).isEqualTo(cashFlow);
    }

    @Test
    @DisplayName("Deve lancar erro de negocio ao tentar salvar movement vazio.")
    public void errorToSaveMovementWithEmptyTest(){

        //cenario
        Movement movement = null;
        Mockito.when(repository.existsById(Mockito.anyLong())).thenReturn(true);

        //execucao
        Throwable exception = Assertions.catchThrowable(() -> service.save(movement));

        //verificacao
        assertThat(exception)
                .isInstanceOf(BusinessException.class)
                .hasMessage("Movimentacao nao pode salvar vazio.");

        Mockito.verify(repository,Mockito.never()).save(movement);
    }

    @Test
    @DisplayName("Deve lancar erro de negocio ao tentar salvar o fluxo de caixa vazio.")
    public void errorToSaveMovementWithCashFlowEmptyTest(){

        //cenario
        Movement movement = getMovement();
        movement.setTypeCashFlow(null);
        Mockito.when(repository.existsById(Mockito.anyLong())).thenReturn(true);

        //execucao
        Throwable exception = Assertions.catchThrowable(() -> service.save(movement));

        //verificacao
        assertThat(exception)
                .isInstanceOf(BusinessException.class)
                .hasMessage("Fluxo do caixa da movimentacao nao pode salvar vazio.");

        Mockito.verify(repository,Mockito.never()).save(movement);
    }

    private Movement getMovement() {
        TypeCashFlow cashFlow = TypeCashFlow.fromValue("DESPESA");
        return Movement.builder()
                .description("Energia")
                .value(new BigDecimal(150))
                .dueDate(DateTime.create(26,03,2020))
                .payDay(DateTime.create(26,03,2020))
                .user(getUsuario())
                .typeCashFlow(cashFlow)
                .build();
    }

    private User getUsuario() {
        return User.builder().name("Fulano").phone("558500000000")
                .email("fulano@gmail.com").birthDate(DateTime.create(30,3,1987)).build();
    }
}
