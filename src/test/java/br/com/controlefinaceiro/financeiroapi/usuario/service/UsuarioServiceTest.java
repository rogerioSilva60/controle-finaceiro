package br.com.controlefinaceiro.financeiroapi.usuario.service;

import br.com.controlefinaceiro.financeiroapi.usuario.entity.Usuario;
import br.com.controlefinaceiro.financeiroapi.usuario.repository.UsuarioRepository;
import br.com.controlefinaceiro.financeiroapi.usuario.service.impl.UsuarioServiceImpl;
import br.com.controlefinaceiro.financeiroapi.utils.DataHora;
import br.com.controlefinaceiro.financeiroapi.utils.excecoes.BusinessException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class UsuarioServiceTest {

    UsuarioService service;

    @MockBean
    UsuarioRepository repository;

    //Executa antes de cada metodo de teste
    @BeforeEach
    public void setUp(){
        this.service = new UsuarioServiceImpl(repository);
    }

    @Test
    @DisplayName("Deve salvar um usuario.")
    public void salvarUsuarioTest(){
        //cenario
        Usuario usuario = getUsuario();
        Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(false);
        //Simula um objeto criado no banco de dados.
        Mockito.when(repository.save(usuario)).thenReturn(
                        Usuario.builder().id(1l)
                        .nome(usuario.getNome())
                        .email(usuario.getEmail())
                        .telefone(usuario.getTelefone())
                        .dataNascimento(usuario.getDataNascimento())
                        .build()
        );

        //execucao
        Usuario usuarioSalvo = service.save(usuario);

        //verificacao
        assertThat(usuarioSalvo.getId()).isNotNull();
        assertThat(usuarioSalvo.getNome()).isEqualTo(usuario.getNome());
        assertThat(usuarioSalvo.getTelefone()).isEqualTo(usuario.getTelefone());
        assertThat(usuarioSalvo.getDataNascimento()).isEqualTo(usuario.getDataNascimento());
    }

    @Test
    @DisplayName("Deve lancar erro de negocio ao salvar usuario com email duplicado.")
    public void erroAoSalvarUsuarioComEmialDuplicado(){

        //cenario
        Usuario usuario = getUsuario();
        Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(true);

        //execucao
        Throwable exception = Assertions.catchThrowable(() -> service.save(usuario));

        //verificacao
        assertThat(exception)
                .isInstanceOf(BusinessException.class)
                .hasMessage("Email ja cadastrado.");

        Mockito.verify(repository,Mockito.never()).save(usuario);
    }

    private Usuario getUsuario() {
        return Usuario.builder().nome("Fulano").telefone("5585000000")
                .email("fulano@gmail.com").dataNascimento(DataHora.criar(30,3,1987)).build();
    }
}
