package br.com.controlefinaceiro.financeiroapi.goal.service;

import br.com.controlefinaceiro.financeiroapi.goal.entity.Goal;

import java.math.BigDecimal;
import java.util.Optional;

public interface GoalService {

    Goal save(Goal goal);

    Optional<Goal> getById(long id);

    Goal update(Goal goal);

    void delete(Goal goal);

    BigDecimal sumByUserMonthAndYear(long idUser, long month, long year);

    Optional<Goal> getById(long idUser, long month, long year);
}
