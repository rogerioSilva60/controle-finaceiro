package br.com.controlefinaceiro.financeiroapi.movement.controller;

import br.com.controlefinaceiro.financeiroapi.movement.dto.MovementDto;
import br.com.controlefinaceiro.financeiroapi.movement.entity.Movement;
import br.com.controlefinaceiro.financeiroapi.movement.service.MovementService;
import br.com.controlefinaceiro.financeiroapi.user.dto.UserDto;
import br.com.controlefinaceiro.financeiroapi.user.entity.User;
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
                .andExpect(jsonPath("id").isNotEmpty())
                .andExpect(jsonPath("id").value(movementFake.getId()))
                .andExpect(jsonPath("description").value(dto.getDescription()))
                .andExpect(jsonPath("value").value(dto.getValue()))
                .andExpect(jsonPath("dueDate").value(dueDateformatted))
                .andExpect(jsonPath("payDay").value(payDayformatted))
                .andExpect(jsonPath("user.id").isNotEmpty())
                .andExpect(jsonPath("user.id").value(movementFake.getUser().getId()));
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
                .andExpect(jsonPath("message.errors", Matchers.hasSize(6)));
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
                .andExpect(jsonPath("message.errors", Matchers.hasSize(1)));
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
