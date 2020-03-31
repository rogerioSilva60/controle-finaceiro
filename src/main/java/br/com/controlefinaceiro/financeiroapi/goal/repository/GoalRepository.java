package br.com.controlefinaceiro.financeiroapi.goal.repository;

import br.com.controlefinaceiro.financeiroapi.goal.entity.Goal;
import br.com.controlefinaceiro.financeiroapi.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.Optional;

public interface GoalRepository extends JpaRepository<Goal,Long> {

    @Query("SELECT SUM(g.value) FROM Goal g WHERE g.user.id= ?1 AND g.month= ?2 AND g.year= ?3")
    BigDecimal sumByUserMonthAndYear(long idUser, long month, long year);

    @Query("SELECT g FROM Goal g WHERE g.user.id= ?1 AND g.month= ?2 AND g.year= ?3")
    Optional<Goal> getByIdUserMonthAndYear(long idUser, long month, long year);

    boolean existsByUserAndMonthAndYear(User user, long month, long year);
}
