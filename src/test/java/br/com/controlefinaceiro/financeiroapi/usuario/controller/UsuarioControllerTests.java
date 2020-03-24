package br.com.controlefinaceiro.financeiroapi.usuario.controller;

import br.com.controlefinaceiro.financeiroapi.usuario.dto.UsuarioDto;
import br.com.controlefinaceiro.financeiroapi.usuario.entity.Usuario;
import br.com.controlefinaceiro.financeiroapi.usuario.service.UsuarioService;
import br.com.controlefinaceiro.financeiroapi.utils.DataHora;
import br.com.controlefinaceiro.financeiroapi.utils.excecoes.BusinessException;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Optional;

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
    public void criarUsuarioTelefoneDuplicadoTest() throws Exception{
        //Cenario
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

    @Test
    @DisplayName("Deve obter o usuario.")
    public void obterUsuarioTest() throws Exception{
        //Cenario
        long idUsuario = 1l;

        //Simulando o envio do controller
        Usuario usuario = Usuario.builder()
                        .id(idUsuario)
                        .nome(getUsuarioDto().getNome())
                        .email(getUsuarioDto().getEmail())
                        .telefone(getUsuarioDto().getTelefone())
                        .dataNascimento(getUsuarioDto().getDataNascimento())
                        .build();
        String dataFormatada = DataHora.dataFormatada(usuario.getDataNascimento(),"yyyy-MM-dd");
        BDDMockito.given(usuarioService.getById(idUsuario)).willReturn(Optional.of(usuario));

        //Execucao
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(USUARIO_API.concat("/") + idUsuario)
                .accept(MediaType.APPLICATION_JSON);


        //Verificacao
        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").isNotEmpty())
                .andExpect(jsonPath("id").value(idUsuario))
                .andExpect(jsonPath("nome").value(usuario.getNome()))
                .andExpect(jsonPath("email").value(usuario.getEmail()))
                .andExpect(jsonPath("telefone").value(usuario.getTelefone()))
                .andExpect(jsonPath("dataNascimento").value(dataFormatada));
    }

    @Test
    @DisplayName("Deve retornar not found.")
    public void notFountUsuarioTest() throws Exception{
        //Cenario
        BDDMockito.given(usuarioService.getById(Mockito.anyLong())).willReturn(Optional.empty());

        //Execucao
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(USUARIO_API.concat("/") + 1l)
                .accept(MediaType.APPLICATION_JSON);

        //Verificacao
        mvc.perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve deletar usuario.")
    public void deletarUsuario() throws Exception{
        //Cenario
        Long idUsuario = 1l;
        //Simulando retorno.
        BDDMockito.given(usuarioService.getById(Mockito.anyLong())).willReturn(Optional.of(Usuario.builder().id(idUsuario).build()));

        //Execucao
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(USUARIO_API.concat("/") + 1l);

        //Verificacao
        mvc.perform(request)
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Deve retornar not found ao deletar usuario nao encontrado.")
    public void deletarUsuarioInexistente() throws Exception{
        //Cenario
        //Simulando retorno.
        BDDMockito.given(usuarioService.getById(Mockito.anyLong())).willReturn(Optional.empty());

        //Execucao
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(USUARIO_API.concat("/") + 1l);

        //Verificacao
        mvc.perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve atualizar o usuario")
    public void atualizarUsuario() throws Exception{
        //Cenario
        Long idUsuario = 1l;
        Usuario usuarioFake = Usuario.builder()
                .id(idUsuario)
                .nome(getUsuarioDto().getNome())
                .email(getUsuarioDto().getEmail())
                .telefone(getUsuarioDto().getTelefone())
                .dataNascimento(getUsuarioDto().getDataNascimento())
                .build();
        String dataFormatada = DataHora.dataFormatada(usuarioFake.getDataNascimento(),"yyyy-MM-dd");

        //Simulando retorno.
        BDDMockito.given(usuarioService.getById(idUsuario)).willReturn(Optional.of(usuarioFake));
        BDDMockito.given(usuarioService.update(Mockito.any(Usuario.class)))
                .willReturn(usuarioFake);

        String json = new ObjectMapper().writeValueAsString(getUsuarioDto());

        //Execucao
        //Simulando o envio pro controller.
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .put(USUARIO_API.concat("/") + idUsuario)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").isNotEmpty())
                .andExpect(jsonPath("id").isNotEmpty())
                .andExpect(jsonPath("id").value(idUsuario))
                .andExpect(jsonPath("nome").value(usuarioFake.getNome()))
                .andExpect(jsonPath("email").value(usuarioFake.getEmail()))
                .andExpect(jsonPath("telefone").value(usuarioFake.getTelefone()))
                .andExpect(jsonPath("dataNascimento").value(dataFormatada));
    }

    @Test
    @DisplayName("Deve lanacar not found caso nao encontre o  usuario para atualizar.")
    public void atualizarUsuarioInexistente() throws Exception{
        //Cenario
        Long idUsuario = 1l;
        //Simulando retorno.
        BDDMockito.given(usuarioService.getById(Mockito.anyLong())).willReturn(Optional.empty());

        String json = new ObjectMapper().writeValueAsString(getUsuarioDto());

        //Execucao
        //Simulando o envio pro controller.
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .put(USUARIO_API.concat("/") + idUsuario)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(status().isNotFound());
    }

    public UsuarioDto getUsuarioDto() {
        return UsuarioDto.builder().nome("Fulano").telefone("558500000000")
                .email("fulano@gmail.com").dataNascimento(DataHora.criar(30,3,1987)).build();
    }
}
