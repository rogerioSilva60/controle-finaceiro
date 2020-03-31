package br.com.controlefinaceiro.financeiroapi.goal.service.impl;

import br.com.controlefinaceiro.financeiroapi.goal.entity.Goal;
import br.com.controlefinaceiro.financeiroapi.goal.repository.GoalRepository;
import br.com.controlefinaceiro.financeiroapi.goal.service.GoalService;
import br.com.controlefinaceiro.financeiroapi.utils.exception.BusinessException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class GoalServiceImpl implements GoalService {

    GoalRepository repository;

    public GoalServiceImpl(GoalRepository repository) {
        this.repository = repository;
    }

    @Override
    public Goal save(Goal goal) {
        if(repository.existsByUserAndMonthAndYear(goal.getUser(),goal.getMonth(), goal.getYear())){
            throw new BusinessException("Meta ja cadastrada.");
        }
        return repository.save(goal);
    }

    @Override
    public Optional<Goal> getById(long id) {
        return Optional.empty();
    }

    @Override
    public Goal update(Goal goal) {
        return null;
    }

    @Override
    public void delete(Goal goal) {

    }

    public BigDecimal sumByUserMonthAndYear(long idUser, long month, long year){
        return repository.sumByUserMonthAndYear(idUser, month, year);
    }

    @Override
    public Optional<Goal> getById(long idUser, long month, long year) {
        return repository.getByIdUserMonthAndYear(idUser, month, year);
    }

}
