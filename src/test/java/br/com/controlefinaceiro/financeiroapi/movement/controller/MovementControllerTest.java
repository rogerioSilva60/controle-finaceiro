package br.com.controlefinaceiro.financeiroapi.movement.controller;

import br.com.controlefinaceiro.financeiroapi.movement.dto.FinancialAnalysisDto;
import br.com.controlefinaceiro.financeiroapi.movement.dto.MovementDto;
import br.com.controlefinaceiro.financeiroapi.movement.entity.Movement;
import br.com.controlefinaceiro.financeiroapi.movement.service.MovementService;
import br.com.controlefinaceiro.financeiroapi.user.dto.UserDto;
import br.com.controlefinaceiro.financeiroapi.user.entity.User;
import br.com.controlefinaceiro.financeiroapi.utils.Calculator;
import br.com.controlefinaceiro.financeiroapi.utils.DateTime;
import br.com.controlefinaceiro.financeiroapi.utils.constant.TypeCashFlow;
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
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = MovementController.class)
@AutoConfigureMockMvc
public class MovementControllerTest {

    private static final String MOVEMENT_API = "/api/movimentacao";

    @Autowired
    MockMvc mvc;
    @MockBean
    MovementService service;

    @Test
    @DisplayName("Deve criar uma movimentacao finaceira com sucesso.")
    public void createMovimententTest() throws Exception {
        //cenario
        Long idUser = 1l;
        MovementDto dto = getMovementDto();
        dto.getUser().setId(idUser);

        String dueDateformatted = DateTime.formattedDate(dto.getDueDate(),"yyyy-MM-dd");
        String payDayformatted = DateTime.formattedDate(dto.getPayDay(),"yyyy-MM-dd");

        Movement movementFake = Movement.builder().id(1l).description(dto.getDescription()).value(dto.getValue())
                .dueDate(dto.getDueDate()).payDay(dto.getPayDay())
                .user(User.builder().id(idUser).name(getUsuarioDto().getName()).email(getUsuarioDto().getEmail())
                .birthDate(getUsuarioDto().getBirthDate()).build()).typeCashFlow(TypeCashFlow.EXPENCE).build();

        //Simulando a resposta ao criar o movimentacao.
        BDDMockito.given(service.save(Mockito.any(Movement.class)))
                .willReturn(movementFake);
        String json = new ObjectMapper().writeValueAsString(dto);
        System.out.println(json);
        //execucao
        //Simulando o envio pro controller.
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(MOVEMENT_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        //verificacao
        mvc
                .perform(request)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("data").isNotEmpty())
                .andExpect(jsonPath("data.id").isNotEmpty())
                .andExpect(jsonPath("data.id").value(movementFake.getId()))
                .andExpect(jsonPath("data.description").value(dto.getDescription()))
                .andExpect(jsonPath("data.value").value(dto.getValue()))
                .andExpect(jsonPath("data.dueDate").value(dueDateformatted))
                .andExpect(jsonPath("data.payDay").value(payDayformatted))
                .andExpect(jsonPath("data.user.id").isNotEmpty())
                .andExpect(jsonPath("data.user.id").value(movementFake.getUser().getId()));
    }

    @Test
    @DisplayName("Deve lancar erro de validacao quando nao houver dados suficiente para criacao de movimentacao financeira.")
    public void createMovementNotValidTest() throws Exception{
        //Cenario
        String json = new ObjectMapper().writeValueAsString(new MovementDto());

        //Excecucao
        //Simulando o envio pro controller.
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(MOVEMENT_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        //Verificacao
        mvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", Matchers.hasSize(6)));
    }

    @Test
    @DisplayName("Deve lancar erro de validacao quando o fluxo de caixa nao for os respectivos existentes para criacao de usuario.")
    public void createMovimententNotCashFlowInvalidTest() throws Exception {
        //cenario
        MovementDto dto = getMovementDto();
        dto.setTypeCashFlow(null);

        String json = new ObjectMapper().writeValueAsString(dto);
        //execucao
        //Simulando o envio pro controller.
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(MOVEMENT_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        //verificacao
        mvc
                .perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", Matchers.hasSize(1)));
    }


    @Test
    @DisplayName("Deve obter a movimentacao por periodo de vencimento e id do usuario paginado.")
    public void getMovementByIdUserAndDueDateInitialAndDueDateEndPagedTest() throws Exception{
        //cenario
        Long idUser = 1l;
        MovementDto dto = getMovementDto();
        dto.getUser().setId(idUser);

        Date dueDateInitial = DateTime.create(1,3,2020);
        Date dueDateEnd = DateTime.createDateLast(27,3,2020);
        String dueDateInitialFormated = DateTime.formattedDate(dueDateInitial, "yyyy-MM-dd");
        String dueDateEndFormated = DateTime.formattedDate(dueDateEnd, "yyyy-MM-dd");

        User user = User.builder()
                .id(idUser)
                .name(getUsuarioDto().getName())
                .email(getUsuarioDto().getEmail())
                .birthDate(getUsuarioDto().getBirthDate())
                .build();
        Movement movementFake = Movement.builder()
                .id(1l).description(dto.getDescription())
                .value(dto.getValue())
                .dueDate(dto.getDueDate())
                .payDay(dto.getPayDay())
                .user(user)
                .typeCashFlow(TypeCashFlow.EXPENCE)
                .build();
        Pageable pageable = PageRequest.of(0, 10, Sort.Direction.DESC, "dueDate");

        //Simulando a resposta da movimentacao papginada.
        BDDMockito.given(service.findByExpirationDate(Mockito.anyLong(), Mockito.any(Date.class),
                Mockito.any(Date.class), Mockito.any(Pageable.class)))
                .willReturn(new PageImpl<Movement>(Arrays.asList(movementFake), pageable, 1));

        String queryString = String.format("?idUser=%s&dueDateInitial=%s&dueDateEnd=%s&page=0&size=10",
                idUser, dueDateInitialFormated, dueDateEndFormated);

        //execucao
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(MOVEMENT_API.concat(queryString))
                .accept(MediaType.APPLICATION_JSON);

        //verificacao
        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("data").isNotEmpty())
                .andExpect(jsonPath("data.content", Matchers.hasSize(1)))
                .andExpect(jsonPath("data.totalElements").value(1))
                .andExpect(jsonPath("data.pageable.pageSize").value(10))
                .andExpect(jsonPath("data.pageable.pageNumber").value( 0));
    }

    @Test
    @DisplayName("Deve atualizar a movimentacao")
    public void updateMovement() throws Exception{
        //cenario
        Long idUser = 1l;
        Long idMovement = 1l;
        MovementDto dto = getMovementDto();
        dto.getUser().setId(idUser);
        User user = User.builder()
                .id(idUser)
                .name(getUsuarioDto().getName())
                .email(getUsuarioDto().getEmail())
                .birthDate(getUsuarioDto().getBirthDate())
                .build();
        Movement movementFake = Movement.builder()
                .id(idMovement).description(dto.getDescription())
                .value(dto.getValue())
                .dueDate(dto.getDueDate())
                .payDay(dto.getPayDay())
                .user(user)
                .typeCashFlow(TypeCashFlow.EXPENCE)
                .build();
        BDDMockito.given(service.getById(Mockito.anyLong())).willReturn(Optional.of(movementFake));
        BDDMockito.given(service.update(Mockito.any(Movement.class)))
                .willReturn(movementFake);

        String json = new ObjectMapper().writeValueAsString(dto);

        //execucao
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .put(MOVEMENT_API.concat("/") + idMovement)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        //verificacao
        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("data.id").isNotEmpty());
    }

    @Test
    @DisplayName("Deve lancar not found caso nao encontre a movimentacao para atualizar.")
    public void updateMovementNotFound() throws Exception{
        //cenario
        Long idUser = 1l;
        Long idMovement = 1l;
        MovementDto dto = getMovementDto();
        dto.getUser().setId(idUser);
        BDDMockito.given(service.getById(Mockito.anyLong())).willReturn(Optional.empty());

        String json = new ObjectMapper().writeValueAsString(dto);

        //execucao
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .put(MOVEMENT_API.concat("/") + idMovement)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        //verificacao
        mvc.perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve deletar uma movimentacao")
    public void deleteMovement() throws Exception{
        //Cenario
        Long idMovement = 1l;
        //Simulando retorno.
        BDDMockito.given(service.getById(Mockito.anyLong())).willReturn(Optional.of(Movement.builder().id(idMovement).build()));

        //Execucao
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(MOVEMENT_API.concat("/") + idMovement);

        //Verificacao
        mvc.perform(request)
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Deve retornar not found ao deletar uma movimentacao nao encontrada.")
    public void deleteNotFoundMovement() throws Exception{
        //Cenario
        //Simulando retorno.
        BDDMockito.given(service.getById(Mockito.anyLong())).willReturn(Optional.empty());

        //Execucao
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(MOVEMENT_API.concat("/") + 1l);

        //Verificacao
        mvc.perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve retorna a analise financeira pessoal do mes escolhido.")
    public void personalFinancialAnalysisTest() throws Exception{
        //cenario
        long idUser = 1l;
        long month = 1l;
        long year = 2020;
        FinancialAnalysisDto dto = FinancialAnalysisDto.builder()
                .valueGoal(new BigDecimal(800))
                .valueTotalRecipe(new BigDecimal(1000))
                .valueTotalExpence(new BigDecimal(900))
                .build();

        dto.setValueTotalBalance(dto.getValueTotalRecipe().subtract(dto.getValueTotalExpence()));
        BigDecimal resultPercentGoal = Calculator.percentageValue(dto.getValueGoal(), dto.getValueTotalRecipe());
        BigDecimal resultPercentExpence = Calculator.percentageValue(dto.getValueTotalExpence(), dto.getValueTotalRecipe());
        BigDecimal resultPercentMonthlySpend = resultPercentExpence.subtract(resultPercentGoal);
        dto.setValueMonthlySpend(dto.getValueTotalExpence().subtract(dto.getValueGoal()));

        BDDMockito.given(service.personalFinancialAnalysis(Mockito.anyLong(),Mockito.anyLong(), Mockito.anyLong()))
        .willReturn(dto);
        //execucao
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(MOVEMENT_API.concat("/analise-financeiro-pessoal" + String.format("?idUser=%s&month=%s&year=%s", idUser, month, year)))
                .accept(MediaType.APPLICATION_JSON);

        //verificacao
        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("data").isNotEmpty())
                .andExpect(jsonPath("data.valueGoal").value(dto.getValueGoal()))
                .andExpect(jsonPath("data.valueTotalRecipe").value(dto.getValueTotalRecipe()))
                .andExpect(jsonPath("data.valueTotalExpence").value(dto.getValueTotalExpence()))
                .andExpect(jsonPath("data.valueTotalBalance").value(dto.getValueTotalBalance()));
    }

    private MovementDto getMovementDto() {
        return MovementDto.builder()
                .description("Energia")
                .value(new BigDecimal(150))
                .dueDate(DateTime.create(26,03,2020))
                .payDay(DateTime.create(26,03,2020))
                .user(getUsuarioDto())
                .typeCashFlow(TypeCashFlow.fromValue("DESPESA"))
                .build();
    }

    private UserDto getUsuarioDto() {
        return UserDto.builder().name("Fulano").phone("558500000000")
                .email("fulano@gmail.com").birthDate(DateTime.create(30,3,1987)).build();
    }
}
