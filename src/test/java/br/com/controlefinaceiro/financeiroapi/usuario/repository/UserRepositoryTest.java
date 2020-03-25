package br.com.controlefinaceiro.financeiroapi.usuario.repository;

import br.com.controlefinaceiro.financeiroapi.usuario.entity.User;
import br.com.controlefinaceiro.financeiroapi.utils.DateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    UserRepository repository;

    @Test
    @DisplayName("Deve retornar verdadeiro quando existir usuario na base com email informado.")
    public void returnTrueIfAnyTest(){

        //cenario
        String email = "fulano@gmail.com";
        User user = getUser();
        entityManager.persist(user);

        //excucao
        boolean existe = repository.existsByEmail(email);

        //verificacao
        assertThat(existe).isTrue();
    }

    @Test
    @DisplayName("Deve retornar falso quando existir usuario na base com email informado.")
    public void returnFalseIfAnyTest(){

        //cenario
        String email = "fulano@gmail.com";

        //excucao
        boolean existe = repository.existsByEmail(email);

        //verificacao
        assertThat(existe).isFalse();
    }

    @Test
    @DisplayName("Deve buscar usuario por id.")
    public void getUserTest(){
        //cenario
        User user = getUser();
        entityManager.persist(user);

        //execucao
        Optional<User> userOptional = repository.findById(user.getId());

        //verificacao
        assertThat(userOptional.isPresent()).isTrue();
    }

    @Test
    @DisplayName("Deve salvar usuario.")
    public void saveUserTest(){
        //cenario
        User user = getUser();

        //execucao
        User userSalvo = repository.save(user);

        //Verificacao
        assertThat(userSalvo.getId()).isNotNull();
    }

    @Test
    @DisplayName("Deve deletar um usuario.")
    public void deleteUserTest(){
        //cenario
        User userSaving = getUser();
        entityManager.persist(userSaving);
        User userSaved = entityManager.find(User.class, userSaving.getId());

        //execucao
        repository.delete(userSaved);
        User userDeleted = entityManager.find(User.class, userSaving.getId());

        //verificacao
        assertThat(userDeleted).isNull();
    }

    private User getUser() {
        return User.builder().name("Fulano").phone("5585000000")
                .email("fulano@gmail.com").birthDate(DateTime.create(30,3,1987)).build();
    }
}
