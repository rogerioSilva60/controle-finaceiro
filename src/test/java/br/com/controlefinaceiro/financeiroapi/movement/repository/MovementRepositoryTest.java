package br.com.controlefinaceiro.financeiroapi.movement.repository;

import br.com.controlefinaceiro.financeiroapi.movement.entity.Movement;
import br.com.controlefinaceiro.financeiroapi.user.entity.User;
import br.com.controlefinaceiro.financeiroapi.user.repository.UserRepository;
import br.com.controlefinaceiro.financeiroapi.utils.DateTime;
import br.com.controlefinaceiro.financeiroapi.utils.constant.TypeCashFlow;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class MovementRepositoryTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    MovementRepository repository;

    @Autowired
    UserRepository userRepository;

    @Test
    @DisplayName("Deve salvar uma movimentacao.")
    public void saveMovementTest(){
        //cenario
        Movement movement = getMovement();
        User user = getUsuario();

        //execucao
        User userSaved = userRepository.save(user);
        movement.setUser(userSaved);
        Movement movementSaved = repository.save(movement);

        //Verificacao
        assertThat(userSaved.getId()).isNotNull();
        assertThat(movementSaved.getId()).isNotNull();
        assertThat(movementSaved.getTypeCashFlow()).isEqualTo(TypeCashFlow.EXPENCE);
    }

    private Movement getMovement() {
        return Movement.builder()
                .description("Energia")
                .value(new BigDecimal(150))
                .dueDate(DateTime.create(26,03,2020))
                .payDay(DateTime.create(26,03,2020))
                .user(getUsuario())
                .typeCashFlow(TypeCashFlow.fromValue("DESPESA"))
                .build();
    }

    private User getUsuario() {
        return User.builder().name("Fulano").phone("558500000000")
                .email("fulano@gmail.com").birthDate(DateTime.create(30,3,1987)).build();
    }
}
