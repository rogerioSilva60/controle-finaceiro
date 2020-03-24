package br.com.controlefinaceiro.financeiroapi.usuario.repository;

import br.com.controlefinaceiro.financeiroapi.usuario.entity.Usuario;
import br.com.controlefinaceiro.financeiroapi.utils.DataHora;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class UsuarioRepositoryTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    UsuarioRepository repository;

    @Test
    @DisplayName("Deve retornar verdadeiro quando existir usuario na base com email informado.")
    public void retorneVerdadeiroCasoExista(){

        //cenario
        String email = "fulano@gmail.com";
        Usuario usuario = getUsuario();
        entityManager.persist(usuario);

        //excucao
        boolean existe = repository.existsByEmail(email);

        //verificacao
        assertThat(existe).isTrue();
    }

    @Test
    @DisplayName("Deve retornar falso quando existir usuario na base com email informado.")
    public void retorneFalsoCasoExista(){

        //cenario
        String email = "fulano@gmail.com";

        //excucao
        boolean existe = repository.existsByEmail(email);

        //verificacao
        assertThat(existe).isFalse();
    }

    private Usuario getUsuario() {
        return Usuario.builder().nome("Fulano").telefone("5585000000")
                .email("fulano@gmail.com").dataNascimento(DataHora.criar(30,3,1987)).build();
    }
}
