package br.com.controlefinaceiro.financeiroapi.goal.controller;

import br.com.controlefinaceiro.financeiroapi.goal.dto.GoalDto;
import br.com.controlefinaceiro.financeiroapi.goal.entity.Goal;
import br.com.controlefinaceiro.financeiroapi.goal.service.GoalService;
import br.com.controlefinaceiro.financeiroapi.user.dto.UserDto;
import br.com.controlefinaceiro.financeiroapi.user.entity.User;
import br.com.controlefinaceiro.financeiroapi.utils.DateTime;
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

import java.math.BigDecimal;
import java.util.Optional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = GoalController.class)
@AutoConfigureMockMvc
public class GoalControllerTest {

    private static final String GOAL_API = "/api/meta";

    @Autowired
    MockMvc mvc;
    @MockBean
    GoalService service;

    @Test
    @DisplayName("Deve criar meta")
    public void createGoalTest() throws Exception{

        GoalDto goalDto = getGoalDto();

        User userFake = User.builder()
                .phone(goalDto.getUser().getPhone())
                .name(goalDto.getUser().getName())
                .email(goalDto.getUser().getEmail())
                .birthDate(goalDto.getUser().getBirthDate())
                .id(1l)
                .build();

        Goal goalFake = Goal.builder()
                .id(1l)
                .month(goalDto.getMonth())
                .value(goalDto.getValue())
                .year(goalDto.getYear())
                .user(userFake)
                .build();
        //Simulando a resposta ao criar a meta.
        BDDMockito.given(service.save(Mockito.any(Goal.class)))
                .willReturn(goalFake);

        String json = new ObjectMapper().writeValueAsString(goalDto);
        //Simulando o envio pro controller.
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(GOAL_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        //Verificacao
        mvc
                .perform(request)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("data.id").isNotEmpty())
                .andExpect(jsonPath("data.id").value(1))
                .andExpect(jsonPath("data.month").value(3))
                .andExpect(jsonPath("data.year").value(2020))
                .andExpect(jsonPath("data.value").value(200))
                .andExpect(jsonPath("data.user").isNotEmpty())
                .andExpect(jsonPath("data.user.id").value(1));

    }

    @Test
    @DisplayName("Deve lancar erro de validacao quando nao houver dados suficiente para criacao de meta.")
    public void createGoalNotValidTest() throws Exception{

        //Cenario
        String json = new ObjectMapper().writeValueAsString(new GoalDto());

        //Excecucao
        //Simulando o envio pro controller.
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(GOAL_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        //Verificacao
        mvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", Matchers.hasSize(4)));
    }

    @Test
    @DisplayName("Deve lancar erro de validacao quando mes nao estiver entre 1 a 12 para criacao de meta.")
    public void createGoalNotValidMonthTest() throws Exception{
        GoalDto goalDto = getGoalDto();
        goalDto.setMonth(13l);
        //Cenario
        String json = new ObjectMapper().writeValueAsString(goalDto);

        //Excecucao
        //Simulando o envio pro controller.
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(GOAL_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        //Verificacao
        mvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", Matchers.hasSize(1)));
    }

    @Test
    @DisplayName("Deve lancar erro de validacao quando o ano nao estiver com valor minimo permitido para criacao de meta.")
    public void createGoalNotValidYearTest() throws Exception{
        GoalDto goalDto = getGoalDto();
        goalDto.setYear(2000l);
        //Cenario
        String json = new ObjectMapper().writeValueAsString(goalDto);

        //Excecucao
        //Simulando o envio pro controller.
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(GOAL_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        //Verificacao
        mvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", Matchers.hasSize(1)));
    }

    @Test
    @DisplayName("Deve atualizar meta")
    public void updateGoalTest() throws Exception{
        //cenario
        GoalDto goalDto = getGoalDto();

        User userFake = User.builder()
                .phone(goalDto.getUser().getPhone())
                .name(goalDto.getUser().getName())
                .email(goalDto.getUser().getEmail())
                .birthDate(goalDto.getUser().getBirthDate())
                .id(1l)
                .build();

        Goal goalFake = Goal.builder()
                .id(1l)
                .month(goalDto.getMonth())
                .value(goalDto.getValue())
                .year(goalDto.getYear())
                .user(userFake)
                .build();

        //Simulando retorno.
        BDDMockito.given(service.getById(goalFake.getId())).willReturn(Optional.of(goalFake));
        BDDMockito.given(service.update(Mockito.any(Goal.class)))
                .willReturn(goalFake);

        String json = new ObjectMapper().writeValueAsString(goalDto);

        //Execucao
        //Simulando o envio pro controller.
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .put(GOAL_API.concat("/") + goalFake.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        //verificacao
        mvc
                .perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("data.id").isNotEmpty())
                .andExpect(jsonPath("data.id").value(1))
                .andExpect(jsonPath("data.month").value(3))
                .andExpect(jsonPath("data.year").value(2020))
                .andExpect(jsonPath("data.value").value(200))
                .andExpect(jsonPath("data.user").isNotEmpty())
                .andExpect(jsonPath("data.user.id").value(1));
    }

    @Test
    @DisplayName("Deve lancar not found caso nao encontre a meta para atualizar.")
    public void updateGoalNotFoundTest() throws Exception{
        //Cenario
        //Simulando retorno.
        BDDMockito.given(service.getById(Mockito.anyLong())).willReturn(Optional.empty());

        String json = new ObjectMapper().writeValueAsString(getGoalDto());

        //Execucao
        //Simulando o envio pro controller.
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .put(GOAL_API.concat("/") + 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve deletar usuario.")
    public void deleteGoalTest() throws Exception{
        //Cenario
        Long idGoal = 1l;
        //Simulando retorno.
        BDDMockito.given(service.getById(Mockito.anyLong())).willReturn(Optional.of(Goal.builder().id(idGoal).build()));

        //Execucao
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(GOAL_API.concat("/") + idGoal);

        //Verificacao
        mvc.perform(request)
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Deve retornar not found ao deletar meta nao encontrado.")
    public void deleteGoalNotFoundTest() throws Exception{
        //Cenario
        Long idGoal = 1l;
        //Simulando retorno.
        BDDMockito.given(service.getById(Mockito.anyLong())).willReturn(Optional.empty());

        //Execucao
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(GOAL_API.concat("/") + idGoal);

        //Verificacao
        mvc.perform(request)
                .andExpect(status().isNotFound());
    }



    private GoalDto getGoalDto() {
        return GoalDto.builder()
                .month(3l)
                .year(2020l)
                .value(new BigDecimal(200))
                .user(getUserDto())
                .build();
    }

    public UserDto getUserDto() {
        return UserDto.builder().name("Fulano").phone("558500000000")
                .email("fulano@gmail.com").birthDate(DateTime.create(30,3,1987)).build();
    }
}
