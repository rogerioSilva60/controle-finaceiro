package br.com.controlefinaceiro.financeiroapi.movement.service;

import br.com.controlefinaceiro.financeiroapi.goal.repository.GoalRepository;
import br.com.controlefinaceiro.financeiroapi.goal.service.GoalService;
import br.com.controlefinaceiro.financeiroapi.goal.service.impl.GoalServiceImpl;
import br.com.controlefinaceiro.financeiroapi.movement.dto.FinancialAnalysisDto;
import br.com.controlefinaceiro.financeiroapi.movement.entity.Movement;
import br.com.controlefinaceiro.financeiroapi.movement.repository.DynamicQuerysMovementRepository;
import br.com.controlefinaceiro.financeiroapi.movement.repository.MovementRepository;
import br.com.controlefinaceiro.financeiroapi.movement.service.impl.MovementServiceImpl;
import br.com.controlefinaceiro.financeiroapi.user.entity.User;
import br.com.controlefinaceiro.financeiroapi.user.repository.UserRepository;
import br.com.controlefinaceiro.financeiroapi.user.service.UserService;
import br.com.controlefinaceiro.financeiroapi.user.service.impl.UserServiceImpl;
import br.com.controlefinaceiro.financeiroapi.utils.DateTime;
import br.com.controlefinaceiro.financeiroapi.utils.constant.TypeCashFlow;
import br.com.controlefinaceiro.financeiroapi.utils.exception.BusinessException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class MovementServiceTest {

    MovementService service;
    UserService userService;
    GoalService goalService;

    @MockBean
    DynamicQuerysMovementRepository dynamicQuerysMovementRepository;
    @MockBean
    MovementRepository repository;
    @MockBean
    UserRepository userRepository;
    @MockBean
    GoalRepository goalRepository;

    //Executa antes de cada metodo de teste
    @BeforeEach
    public void setUp(){
        this.userService = new UserServiceImpl(userRepository);
        this.goalService = new GoalServiceImpl(goalRepository);
        this.service = new MovementServiceImpl(repository, dynamicQuerysMovementRepository, userService, goalService);

    }

    @Test
    @DisplayName("Deve salvar uma movimentacao.")
    public void saveMovementTest() throws Exception{
        //cenario
        Movement movement = getMovement();

        //Simula um objeto criado no banco de dados.
        User user = movement.getUser();
        user.setId(1l);
        TypeCashFlow cashFlow = TypeCashFlow.fromValue("DESPESA");
        Mockito.when(repository.existsById(Mockito.anyLong())).thenReturn(false);
        Mockito.when(userService.save(getUsuario())).thenReturn(user);
        Mockito.when(userService.getById(Mockito.anyLong())).thenReturn(Optional.of(user));
        Mockito.when(repository.save(movement)).thenReturn(
                Movement.builder().id(1l)
                        .description(movement.getDescription())
                        .value(movement.getValue())
                        .dueDate(movement.getDueDate())
                        .payDay(movement.getPayDay())
                        .user(user)
                        .typeCashFlow(cashFlow)
                        .build()
        );

        //execucao
        Movement movementSave = service.save(movement);

        //verificacao
        assertThat(movementSave.getId()).isNotNull();
        assertThat(movementSave.getDescription()).isEqualTo(movement.getDescription());
        assertThat(movementSave.getValue()).isEqualTo(movement.getValue());
        assertThat(movementSave.getUser()).isNotNull();
        assertThat(movementSave.getUser().getId()).isNotNull();
        assertThat(movementSave.getTypeCashFlow()).isNotNull();
        assertThat(movementSave.getTypeCashFlow()).isEqualTo(cashFlow);
    }

    @Test
    @DisplayName("Deve lancar erro de negocio ao tentar salvar movement vazio.")
    public void errorToSaveMovementWithEmptyTest(){

        //cenario
        Movement movement = null;
        Mockito.when(repository.existsById(Mockito.anyLong())).thenReturn(true);

        //execucao
        Throwable exception = Assertions.catchThrowable(() -> service.save(movement));

        //verificacao
        assertThat(exception)
                .isInstanceOf(BusinessException.class)
                .hasMessage("Movimentacao nao pode salvar vazio.");

        Mockito.verify(repository,Mockito.never()).save(movement);
    }

    @Test
    @DisplayName("Deve lancar erro de negocio ao tentar salvar o fluxo de caixa vazio.")
    public void errorToSaveMovementWithCashFlowEmptyTest(){

        //cenario
        Movement movement = getMovement();
        movement.setTypeCashFlow(null);
        Mockito.when(repository.existsById(Mockito.anyLong())).thenReturn(true);

        //execucao
        Throwable exception = Assertions.catchThrowable(() -> service.save(movement));

        //verificacao
        assertThat(exception)
                .isInstanceOf(BusinessException.class)
                .hasMessage("Fluxo do caixa da movimentacao nao pode salvar vazio.");

        Mockito.verify(repository,Mockito.never()).save(movement);
    }

    @Test
    @DisplayName("Deve obter a movimentacao por periodo de vencimento e id do usuario paginado.")
    public void getMovementByIdUserAndDueDateInitialAndDueDateEndPagedTest(){
        //cenario
        long idUser = 1l;
        Movement movement = getMovement();
        movement.setId(1l);
        movement.getUser().setId(idUser);
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<Movement> movements = Arrays.asList(movement);
        PageImpl<Movement> page = new PageImpl<>(movements, pageRequest, 1);
        Mockito.when(repository.findByExpirationDate(Mockito.anyLong(), Mockito.any(Date.class),
                Mockito.any(Date.class), Mockito.any(PageRequest.class)))
                .thenReturn(page);

        //execucao
        Page<Movement> movementPage = service.findByExpirationDate(idUser, movement.getDueDate(),
                movement.getDueDate(), pageRequest);

        //verificacao
        assertThat(movementPage.getTotalElements()).isEqualTo(1);
        assertThat(movementPage.getContent()).isEqualTo(movements);
        assertThat(movementPage.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(movementPage.getPageable().getPageSize()).isEqualTo(10);
    }

    @Test
    @DisplayName("Deve retornar uma analise financeira pessoal.")
    public void getPersonalFinancialAnalysisTest(){
        //cenario
        BigDecimal valueRecipe = new BigDecimal(1000);
        BigDecimal valueExpence = new BigDecimal(0);
        BigDecimal valueGoal = new BigDecimal(0);

        Mockito.when(repository.sumMovementTypeCashFlow(1, 1,
                2020, TypeCashFlow.convert(TypeCashFlow.RECIPE.getKey())))
                .thenReturn(valueRecipe);
        Mockito.when(repository.sumMovementTypeCashFlow(1, 1,
                2020, TypeCashFlow.convert(TypeCashFlow.EXPENCE.getKey())))
                .thenReturn(valueExpence);
        Mockito.when(goalService.sumByUserMonthAndYear(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(valueGoal);

        //execucao
        FinancialAnalysisDto financialAnalysisDto = service.personalFinancialAnalysis(1, 1, 2020);

        //verificacao
        assertThat(financialAnalysisDto).isNotNull();
        assertThat(financialAnalysisDto.getValueTotalExpence()).isEqualTo(valueExpence);
        assertThat(financialAnalysisDto.getValueTotalRecipe()).isEqualTo(valueRecipe);
        assertThat(financialAnalysisDto.getValueGoal()).isEqualTo(valueGoal);
    }

    @Test
    @DisplayName("Deve retornar um erro de regra de negocio da analise financeira pessoal " +
            "caso a receita esteja menor ou iguala zero.")
    public void getPersonalFinancialAnalysisBusinessExceptionTest(){
        //cenario
        BigDecimal valueRecipe = new BigDecimal(-5);
        BigDecimal valueExpence = new BigDecimal(800);
        BigDecimal valueGoal = new BigDecimal(500);

        Mockito.when(repository.sumMovementTypeCashFlow(1, 1,
                2020, TypeCashFlow.convert(TypeCashFlow.RECIPE.getKey())))
                .thenReturn(valueRecipe);
        Mockito.when(repository.sumMovementTypeCashFlow(1, 1,
                2020, TypeCashFlow.convert(TypeCashFlow.EXPENCE.getKey())))
                .thenReturn(valueExpence);
        Mockito.when(goalService.sumByUserMonthAndYear(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(valueGoal);

        //execucao
        Throwable exception = Assertions.catchThrowable(() -> service.personalFinancialAnalysis(1, 1, 2020));

        //verificacao
        assertThat(exception)
                .isInstanceOf(BusinessException.class)
                .hasMessage("Informe sua receita para poder analisar suas financas.");
    }

    private Movement getMovement() {
        TypeCashFlow cashFlow = TypeCashFlow.fromValue("DESPESA");
        return Movement.builder()
                .description("Energia")
                .value(new BigDecimal(150))
                .dueDate(DateTime.create(26,03,2020))
                .payDay(DateTime.create(26,03,2020))
                .user(getUsuario())
                .typeCashFlow(cashFlow)
                .build();
    }

    private User getUsuario() {
        return User.builder().name("Fulano").phone("558500000000")
                .email("fulano@gmail.com").birthDate(DateTime.create(30,3,1987)).build();
    }
}
