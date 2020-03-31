package br.com.controlefinaceiro.financeiroapi.movement.service.impl;

import br.com.controlefinaceiro.financeiroapi.goal.service.GoalService;
import br.com.controlefinaceiro.financeiroapi.movement.dto.FinancialAnalysisDto;
import br.com.controlefinaceiro.financeiroapi.movement.entity.Movement;
import br.com.controlefinaceiro.financeiroapi.movement.repository.DynamicQuerysMovementRepository;
import br.com.controlefinaceiro.financeiroapi.movement.repository.MovementRepository;
import br.com.controlefinaceiro.financeiroapi.movement.service.MovementService;
import br.com.controlefinaceiro.financeiroapi.user.service.UserService;
import br.com.controlefinaceiro.financeiroapi.utils.Calculator;
import br.com.controlefinaceiro.financeiroapi.utils.constant.TypeCashFlow;
import br.com.controlefinaceiro.financeiroapi.utils.exception.BusinessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;

@Service
public class MovementServiceImpl implements MovementService {

    private MovementRepository repository;
    private DynamicQuerysMovementRepository dynamicQuerysMovementRepository;

    private UserService userService;
    private GoalService goalService;

    public MovementServiceImpl(MovementRepository repository,
                               DynamicQuerysMovementRepository dynamicQuerysMovementRepository,
                               UserService userService, GoalService goalService) {
        this.repository = repository;
        this.dynamicQuerysMovementRepository = dynamicQuerysMovementRepository;
        this.userService = userService;
        this.goalService = goalService;
    }

    @Override
    public Movement save(Movement movement) {
        prepareMovementToSave(movement);
        return repository.save(movement);
    }

    @Override
    public Page<Movement> findByExpirationDate(long idUser, Date dueDateInitial, Date dueDateEnd, Pageable pageable) {

        Page<Movement> result = repository.findByExpirationDate(idUser, dueDateInitial, dueDateEnd, pageable);
        return result;
    }

    @Override
    public Movement update(Movement movement) {

        return null;
    }

    @Override
    public Optional<Movement> getById(long id) {
        return null;
    }

    @Override
    public void delete(Movement movement) {

    }

    @Override
    public FinancialAnalysisDto personalFinancialAnalysis (long idUser, long month, long year){
        BigDecimal sumRecipe = repository.sumMovementTypeCashFlow(idUser, month, year, TypeCashFlow.convert(TypeCashFlow.RECIPE.getKey()));
        BigDecimal sumExpence = repository.sumMovementTypeCashFlow(idUser, month, year, TypeCashFlow.convert(TypeCashFlow.EXPENCE.getKey()));
        BigDecimal resultGoal = goalService.sumByUserMonthAndYear(idUser, month, year);

        FinancialAnalysisDto dto = getFinancialAnalysisDto(sumRecipe, sumExpence, resultGoal);
        return dto;

    }

    private FinancialAnalysisDto getFinancialAnalysisDto(BigDecimal sumRecipe, BigDecimal sumExpence, BigDecimal resultGoal) {
        FinancialAnalysisDto dto = new FinancialAnalysisDto();

        sumRecipe = sumRecipe == null ? new BigDecimal(0) : sumRecipe;
        sumExpence = sumExpence == null ? new BigDecimal(0) : sumExpence;
        dto.setValueTotalRecipe(sumRecipe);
        dto.setValueTotalExpence(sumExpence);

        BigDecimal resultBalance = sumRecipe.subtract(sumExpence);
        dto.setValueTotalBalance(resultBalance);
        dto.setValueGoal(resultGoal == null ? new BigDecimal(0) : resultGoal);
        dto.setValueMonthlySpend(dto.getValueTotalExpence().subtract(dto.getValueGoal()));

        BigDecimal resultPercentGoal = Calculator.percentageValue(dto.getValueGoal(), dto.getValueTotalRecipe());
        BigDecimal resultPercentualExpence = Calculator.percentageValue(dto.getValueTotalExpence(), dto.getValueTotalRecipe());
        BigDecimal resultPercentMonthlySpend = resultPercentualExpence.subtract(resultPercentGoal);

        if(sumRecipe.doubleValue() <= 0){
            throw new BusinessException("Informe sua receita para poder analisar suas financas.");
        }
        dto.setMessage(validationMonthGoalPlanning(resultPercentMonthlySpend, dto.getValueMonthlySpend(), resultBalance));
        return dto;
    }

    private String validationMonthGoalPlanning(BigDecimal percentMonthlySpend, BigDecimal valueMonthlySpend, BigDecimal resultBalance){
        if(percentMonthlySpend == null ){
            return "";
        }
        if(percentMonthlySpend.doubleValue() > 0){
            String message = "Infelizmente voce gastou "+ valueMonthlySpend
                    + "R$ (" + percentMonthlySpend.doubleValue() + "%) a mais do que pretendia para o mes. ";

            if(resultBalance.doubleValue()>=0){
                message += "Mas sua receita ainda esta positiva com um valor restante de " + resultBalance + "R$.";
            } else{
              message += " E sua receita esta negativa com uma valor devedor de " + resultBalance + "R$, reveja suas financas.";
            }

            return message;
        } else if(percentMonthlySpend.doubleValue() == 0){
            return "Ótimo, voce conseguiu gastar o planejado para o mes.";
        } else {
            return "Parabéns, voce esta coseguindo economizar " + valueMonthlySpend
                    + "R$ (" + percentMonthlySpend.doubleValue() + "%) do previsto pro mes, continue assim!";
        }
    }

    private void prepareMovementToSave(Movement movement) {
        if(movement == null){
            throw new BusinessException("Movimentacao nao pode salvar vazio.");
        } else if(movement.getId() != null){
            if(repository.existsById(movement.getId()))
                throw new BusinessException("Movimentacao ja cadastrada.");
        } else if(movement.getTypeCashFlow() == null){
            throw new BusinessException("Fluxo do caixa da movimentacao nao pode salvar vazio.");
        } else if(movement.getUser().getId() == null){
            throw new BusinessException("Id do usuario é obrigatorio.");
        } else if(!userService.getById(movement.getUser().getId()).isPresent()){
            throw new BusinessException("Id do usuario não identificado, " +
                    "Verifique se o mesmo foi cadastrado antes de salvar uma movimentacao.");
        }
    }
}
