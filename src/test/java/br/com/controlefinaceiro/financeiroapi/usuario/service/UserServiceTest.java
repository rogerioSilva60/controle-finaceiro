package br.com.controlefinaceiro.financeiroapi.usuario.service;

import br.com.controlefinaceiro.financeiroapi.usuario.entity.User;
import br.com.controlefinaceiro.financeiroapi.usuario.repository.UserRepository;
import br.com.controlefinaceiro.financeiroapi.usuario.service.impl.UserServiceImpl;
import br.com.controlefinaceiro.financeiroapi.utils.DateTime;
import br.com.controlefinaceiro.financeiroapi.utils.excecoes.BusinessException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class UserServiceTest {

    UserService service;

    @MockBean
    UserRepository repository;

    //Executa antes de cada metodo de teste
    @BeforeEach
    public void setUp(){
        this.service = new UserServiceImpl(repository);
    }

    @Test
    @DisplayName("Deve salvar um usuario.")
    public void saveUserTest(){
        //cenario
        User user = getUser();
        Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(false);
        //Simula um objeto criado no banco de dados.
        Mockito.when(repository.save(user)).thenReturn(
                        User.builder().id(1l)
                        .name(user.getName())
                        .email(user.getEmail())
                        .phone(user.getPhone())
                        .birthDate(user.getBirthDate())
                        .build()
        );

        //execucao
        User userSalvo = service.save(user);

        //verificacao
        assertThat(userSalvo.getId()).isNotNull();
        assertThat(userSalvo.getName()).isEqualTo(user.getName());
        assertThat(userSalvo.getPhone()).isEqualTo(user.getPhone());
        assertThat(userSalvo.getBirthDate()).isEqualTo(user.getBirthDate());
    }

    @Test
    @DisplayName("Deve lancar erro de negocio ao tentar salvar usuario com email duplicado.")
    public void errorToSaveUserWithEmailDuplicateTest(){

        //cenario
        User user = getUser();
        Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(true);

        //execucao
        Throwable exception = Assertions.catchThrowable(() -> service.save(user));

        //verificacao
        assertThat(exception)
                .isInstanceOf(BusinessException.class)
                .hasMessage("Email ja cadastrado.");

        Mockito.verify(repository,Mockito.never()).save(user);
    }

    @Test
    @DisplayName("Deve buscar um usuario por id.")
    public void getUserByIdTest(){
        //cenario
        Long id =1l;
        User user = getUser();
        user.setId(id);
        Mockito.when(repository.findById(id)).thenReturn(Optional.of(user));

        //execucao
        Optional<User> usuarioOptional = service.getById(id);

        //verificacao
        assertThat(usuarioOptional.isPresent()).isTrue();
        assertThat(usuarioOptional.get().getId()).isEqualTo(id);
    }

    @Test
    @DisplayName("Deve retornar vazio ao buscar um usuario por id que nao foi encontrado.")
    public void getUserByIdNotFoundTest(){
        //cenario
        Long id =1l;
        Mockito.when(repository.findById(id)).thenReturn(Optional.empty());

        //execucao
        Optional<User> usuarioOptional = service.getById(id);

        //verificacao
        assertThat(usuarioOptional.isPresent()).isFalse();
    }

    @Test
    @DisplayName("Deve deletar usuario caso exista")
    public void deleteUserTest(){
        //cenario
        User user = getUser();
        user.setId(1l);

        //execucao
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> service.delete(user));

        //verificacao
        Mockito.verify(repository, Mockito.times(1)).delete(user);
    }

    @Test
    @DisplayName("Deve retornar erro ao tentar deletar usuario vazio.")
    public void deleteUserIllegalTest(){
        //cenario
        User user = null;
        //execucao
        Throwable exception = Assertions.catchThrowable(() -> service.delete(user));

        //verificacao
        assertThat(exception)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Obrigatorio usuario para deletar.");
        Mockito.verify(repository, Mockito.never()).delete(user);
    }

    @Test
    @DisplayName("Deve deletar usuario caso exista")
    public void updateUserTest(){
        //cenario

        long id = 1l;
        User userUpdating = User.builder().id(id).build();

        User userUpdated = getUser();
        userUpdated.setId(id);
        Mockito.when(repository.save(Mockito.any(User.class))).thenReturn(userUpdated);

        //execucao
        User user = service.update(userUpdating);

        //verificacao
        assertThat(user).isNotEqualTo(null);
        assertThat(user.getId()).isEqualTo(userUpdated.getId());
        assertThat(user.getName()).isEqualTo(userUpdated.getName());
    }

    @Test
    @DisplayName("Deve retornar erro ao tentar deletar usuario vazio.")
    public void updateUserIllegalTest(){
        //cenario
        User user = null;
        //execucao
        Throwable exception = Assertions.catchThrowable(() -> service.update(user));

        //verificacao
        assertThat(exception)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Obrigatorio usuario para atualizar.");
        Mockito.verify(repository, Mockito.never()).save(user);
    }

    @Test
    @DisplayName("Deve retornar uma lista de usuarios paginados.")
    public void getUserPaginatedTest(){
        //cenario
        User user = getUser();

        PageRequest pageRequest = PageRequest.of(0, 10);
        List<User> users = Arrays.asList(user);
        PageImpl<User> page = new PageImpl<>(users, pageRequest, 1);
        Mockito.when(repository.findAll(Mockito.any(Example.class), Mockito.any(PageRequest.class)))
                .thenReturn(page);
        //execucao
        Page<User> userPage = service.find(user, pageRequest);

        //vericacao
        assertThat(userPage.getTotalElements()).isEqualTo(1);
        assertThat(userPage.getContent()).isEqualTo(users);
        assertThat(userPage.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(userPage.getPageable().getPageSize()).isEqualTo(10);
    }

    private User getUser() {
        return User.builder().name("Fulano").phone("5585000000")
                .email("fulano@gmail.com").birthDate(DateTime.create(30,3,1987)).build();
    }
}
