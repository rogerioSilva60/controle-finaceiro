package br.com.controlefinaceiro.financeiroapi.user.controller;

import br.com.controlefinaceiro.financeiroapi.user.dto.UserDto;
import br.com.controlefinaceiro.financeiroapi.user.entity.User;
import br.com.controlefinaceiro.financeiroapi.user.service.UserService;
import br.com.controlefinaceiro.financeiroapi.utils.DateTime;
import br.com.controlefinaceiro.financeiroapi.utils.exception.BusinessException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.Optional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc
public class UserControllerTest {

    private static final String USER_API = "/api/usuario";

    @Autowired
    MockMvc mvc;
    @MockBean
    UserService service;

    @Test
    @DisplayName("Deve criar um usuário com sucesso.")
    public void createUserTest() throws Exception {

        //Cenario
        UserDto dto = getUsuarioDto();
        String dataFormatada = DateTime.formattedDate(dto.getBirthDate(),"yyyy-MM-dd");

        User userFake = User.builder().id(1l).name(dto.getName()).phone(dto.getPhone())
                .email(dto.getEmail()).birthDate(dto.getBirthDate()).build();

        //Simulando a resposta ao criar o usuario.
        BDDMockito.given(service.save(Mockito.any(User.class)))
                .willReturn(userFake);

        String json = new ObjectMapper().writeValueAsString(dto);
        System.out.println(json);

        //Execucao
        //Simulando o envio pro controller.
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(USER_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        //Verificacao
             mvc
                .perform(request)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").isNotEmpty())
                     .andExpect(jsonPath("id").value(userFake.getId()))
                .andExpect(jsonPath("name").value(dto.getName()))
                .andExpect(jsonPath("email").value(dto.getEmail()))
                .andExpect(jsonPath("phone").value(dto.getPhone()))
                .andExpect(jsonPath("birthDate").value(dataFormatada));
    }

    @Test
    @DisplayName("Deve lancar erro de validacao quando nao houver dados suficiente para criacao de usuario.")
    public void createUserNotValidTest() throws Exception{

        //Cenario
        String json = new ObjectMapper().writeValueAsString(new UserDto());

        //Excecucao
        //Simulando o envio pro controller.
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(USER_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        //Verificacao
        mvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", Matchers.hasSize(3)));
    }

    @Test
    @DisplayName("Deve lancar erro de validacao ao tentar criar usuario com o email duplicado.")
    public void createUserEmailDuplicateTest() throws Exception{
        //Cenario
        UserDto dto = getUsuarioDto();
        String json = new ObjectMapper().writeValueAsString(dto);

        //Simulando a resposta ao criar o usuario.
        String mensagemError = "Email ja cadastrado.";
        BDDMockito.given(service.save(Mockito.any(User.class)))
                .willThrow(new BusinessException(mensagemError));

        //Execucao
        //Simulando o envio pro controller.
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(USER_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        //Verificacao
        mvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", Matchers.hasSize(1)))
                .andExpect(jsonPath("errors[0]").value(mensagemError));
    }

    @Test
    @DisplayName("Deve obter o usuario por id.")
    public void getUserByIdTest() throws Exception{
        //Cenario
        long idUser = 1l;

        //Simulando o envio do controller
        User user = User.builder()
                        .id(idUser)
                        .name(getUsuarioDto().getName())
                        .email(getUsuarioDto().getEmail())
                        .phone(getUsuarioDto().getPhone())
                        .birthDate(getUsuarioDto().getBirthDate())
                        .build();
        String formattedDate = DateTime.formattedDate(user.getBirthDate(),"yyyy-MM-dd");
        BDDMockito.given(service.getById(idUser)).willReturn(Optional.of(user));

        //Execucao
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(USER_API.concat("/") + idUser)
                .accept(MediaType.APPLICATION_JSON);


        //Verificacao
        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").isNotEmpty())
                .andExpect(jsonPath("id").value(idUser))
                .andExpect(jsonPath("name").value(user.getName()))
                .andExpect(jsonPath("email").value(user.getEmail()))
                .andExpect(jsonPath("phone").value(user.getPhone()))
                .andExpect(jsonPath("birthDate").value(formattedDate));
    }

    @Test
    @DisplayName("Deve retornar not found.")
    public void notFountUserTest() throws Exception{
        //Cenario
        BDDMockito.given(service.getById(Mockito.anyLong())).willReturn(Optional.empty());

        //Execucao
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(USER_API.concat("/") + 1l)
                .accept(MediaType.APPLICATION_JSON);

        //Verificacao
        mvc.perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve deletar usuario.")
    public void deleteUser() throws Exception{
        //Cenario
        Long idUser = 1l;
        //Simulando retorno.
        BDDMockito.given(service.getById(Mockito.anyLong())).willReturn(Optional.of(User.builder().id(idUser).build()));

        //Execucao
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(USER_API.concat("/") + 1l);

        //Verificacao
        mvc.perform(request)
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Deve retornar not found ao deletar usuario nao encontrado.")
    public void deleteUserNotFound() throws Exception{
        //Cenario
        //Simulando retorno.
        BDDMockito.given(service.getById(Mockito.anyLong())).willReturn(Optional.empty());

        //Execucao
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(USER_API.concat("/") + 1l);

        //Verificacao
        mvc.perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve atualizar o usuario")
    public void updateUser() throws Exception{
        //Cenario
        Long idUser = 1l;
        User userFake = User.builder()
                .id(idUser)
                .name(getUsuarioDto().getName())
                .email(getUsuarioDto().getEmail())
                .phone(getUsuarioDto().getPhone())
                .birthDate(getUsuarioDto().getBirthDate())
                .build();
        String formattedDate = DateTime.formattedDate(userFake.getBirthDate(),"yyyy-MM-dd");

        //Simulando retorno.
        BDDMockito.given(service.getById(idUser)).willReturn(Optional.of(userFake));
        BDDMockito.given(service.update(Mockito.any(User.class)))
                .willReturn(userFake);

        String json = new ObjectMapper().writeValueAsString(getUsuarioDto());

        //Execucao
        //Simulando o envio pro controller.
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .put(USER_API.concat("/") + idUser)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        //verificacao
        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").isNotEmpty())
                .andExpect(jsonPath("id").value(idUser))
                .andExpect(jsonPath("name").value(userFake.getName()))
                .andExpect(jsonPath("email").value(userFake.getEmail()))
                .andExpect(jsonPath("phone").value(userFake.getPhone()))
                .andExpect(jsonPath("birthDate").value(formattedDate));
    }

    @Test
    @DisplayName("Deve lancar not found caso nao encontre o  usuario para atualizar.")
    public void updateUserNotFound() throws Exception{
        //Cenario
        Long idUser = 1l;
        //Simulando retorno.
        BDDMockito.given(service.getById(Mockito.anyLong())).willReturn(Optional.empty());

        String json = new ObjectMapper().writeValueAsString(getUsuarioDto());

        //Execucao
        //Simulando o envio pro controller.
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .put(USER_API.concat("/") + idUser)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve retornar uma lista de usuarios paginado.")
    public void listUserPaginatedTest() throws Exception{
        //cenario
        User user = User.builder()
                            .id(1l)
                            .name(getUsuarioDto().getName())
                            .email(getUsuarioDto().getEmail())
                            .phone(getUsuarioDto().getPhone())
                            .birthDate(getUsuarioDto().getBirthDate())
                            .build();

        BDDMockito.given( service.find(Mockito.any(User.class), Mockito.any(Pageable.class)))
                .willReturn(new PageImpl<User>(Arrays.asList(user), PageRequest.of(0, 100), 1));

        String queryString = String.format("?nome=%s&page=0&size=100", user.getName());

        //execucao
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(USER_API.concat(queryString))
                .accept(MediaType.APPLICATION_JSON);

        //verificacao
        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("content", Matchers.hasSize(1)))
                .andExpect(jsonPath("totalElements").value(1))
                .andExpect(jsonPath("pageable.pageSize").value(100))
                .andExpect(jsonPath("pageable.pageNumber").value( 0));
    }

    public UserDto getUsuarioDto() {
        return UserDto.builder().name("Fulano").phone("558500000000")
                .email("fulano@gmail.com").birthDate(DateTime.create(30,3,1987)).build();
    }
}
