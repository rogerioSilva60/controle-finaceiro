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

import java.util.Optional;

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
    public void retorneVerdadeiroCasoExistaTest(){

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
    public void retorneFalsoCasoExistaTest(){

        //cenario
        String email = "fulano@gmail.com";

        //excucao
        boolean existe = repository.existsByEmail(email);

        //verificacao
        assertThat(existe).isFalse();
    }

    @Test
    @DisplayName("Deve buscar usuario por id.")
    public void buscarUsuarioTest(){
        //cenario
        Usuario usuario = getUsuario();
        entityManager.persist(usuario);

        //execucao
        Optional<Usuario> usuarioOptional = repository.findById(usuario.getId());

        //verificacao
        assertThat(usuarioOptional.isPresent()).isTrue();
    }

    @Test
    @DisplayName("Deve salvar usuario.")
    public void salvarUsuarioTest(){
        //cenario
        Usuario usuario = getUsuario();

        //execucao
        Usuario usuarioSalvo = repository.save(usuario);

        //Verificacao
        assertThat(usuarioSalvo.getId()).isNotNull();
    }

    @Test
    @DisplayName("Deve deletar um usuario.")
    public void deletUsuarioTest(){
        //cenario
        Usuario usuarioASalvar = getUsuario();
        entityManager.persist(usuarioASalvar);
        Usuario usuario = entityManager.find(Usuario.class, usuarioASalvar.getId());

        //execucao
        repository.delete(usuario);
        Usuario usuarioDeletado = entityManager.find(Usuario.class, usuarioASalvar.getId());

        //verificacao
        assertThat(usuarioDeletado).isNull();
    }

    private Usuario getUsuario() {
        return Usuario.builder().nome("Fulano").telefone("5585000000")
                .email("fulano@gmail.com").dataNascimento(DataHora.criar(30,3,1987)).build();
    }
}
