package br.com.controlefinaceiro.financeiroapi.usuario.controller;

import br.com.controlefinaceiro.financeiroapi.usuario.dto.UsuarioDto;
import br.com.controlefinaceiro.financeiroapi.usuario.entity.Usuario;
import br.com.controlefinaceiro.financeiroapi.usuario.service.UsuarioService;
import br.com.controlefinaceiro.financeiroapi.utils.DataHora;
import br.com.controlefinaceiro.financeiroapi.utils.excecoes.BusinessException;
import com.fasterxml.jackson.core.JsonProcessingException;
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
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest
@AutoConfigureMockMvc
public class UsuarioControllerTests {

    private static final String USUARIO_API = "/api/usuario";
    @Autowired
    MockMvc mvc;

    @MockBean
    UsuarioService usuarioService;

    @Test
    @DisplayName("Deve criar um usuário com sucesso.")
    public void criarUsuarioTest() throws Exception {

        //Cenario
        UsuarioDto dto = getUsuarioDto();
        String dataFormatada = DataHora.dataFormatada(dto.getDataNascimento(),"yyyy-MM-dd");

        Usuario usuarioFake = Usuario.builder().id(1l).nome(dto.getNome()).telefone(dto.getTelefone())
                .email(dto.getEmail()).dataNascimento(dto.getDataNascimento()).build();

        //Simulando a resposta ao criar o usuario.
        BDDMockito.given(usuarioService.save(Mockito.any(Usuario.class)))
                .willReturn(usuarioFake);

        String json = new ObjectMapper().writeValueAsString(dto);

        //Execucao
        //Simulando o envio pro controller.
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(USUARIO_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        //Verificacao
             mvc
                .perform(request)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").isNotEmpty())
                     .andExpect(jsonPath("id").value(usuarioFake.getId()))
                .andExpect(jsonPath("nome").value(dto.getNome()))
                .andExpect(jsonPath("email").value(dto.getEmail()))
                .andExpect(jsonPath("telefone").value(dto.getTelefone()))
                .andExpect(jsonPath("dataNascimento").value(dataFormatada));
    }



    @Test
    @DisplayName("Deve lancar erro de validacao quando nao houver dados suficiente para criacao de usuario.")
    public void criarUsuarioNaoValidoTest() throws Exception{

        //Cenario
        String json = new ObjectMapper().writeValueAsString(new UsuarioDto());

        //Excecucao
        //Simulando o envio pro controller.
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(USUARIO_API)
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
    public void criarUsuarioTelefoneDuplicado() throws Exception{
        //cenario
        UsuarioDto dto = getUsuarioDto();
        String json = new ObjectMapper().writeValueAsString(dto);

        //Simulando a resposta ao criar o usuario.
        String mensagemError = "Email ja cadastrado.";
        BDDMockito.given(usuarioService.save(Mockito.any(Usuario.class)))
                .willThrow(new BusinessException(mensagemError));

        //Execucao
        //Simulando o envio pro controller.
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(USUARIO_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        //Verificacao
        mvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", Matchers.hasSize(1)))
                .andExpect(jsonPath("errors[0]").value(mensagemError));
    }

    public UsuarioDto getUsuarioDto() {
        return UsuarioDto.builder().nome("Fulano").telefone("558500000000")
                .email("fulano@gmail.com").dataNascimento(DataHora.criar(30,3,1987)).build();
    }
}
