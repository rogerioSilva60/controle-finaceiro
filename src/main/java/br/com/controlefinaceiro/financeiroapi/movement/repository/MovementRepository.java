package br.com.controlefinaceiro.financeiroapi.movement.repository;

import br.com.controlefinaceiro.financeiroapi.movement.entity.Movement;
import br.com.controlefinaceiro.financeiroapi.utils.constant.TypeCashFlow;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Date;

public interface MovementRepository extends JpaRepository<Movement, Long> {

    @Query(value = "SELECT m FROM Movement m WHERE m.user.id= ?1 AND m.dueDate>= ?2 AND m.dueDate<= ?3",
    countQuery = "SELECT COUNT(m.id) FROM Movement m WHERE m.user.id= ?1 AND m.dueDate>= ?2 AND m.dueDate<= ?3")
    Page<Movement> findByExpirationDate(Long idUser, Date dueDateInitial, Date dueDateEnd, Pageable pageable);

    @Query(value = "SELECT SUM(value) FROM movement " +
            "WHERE id_user= :idUser AND EXTRACT(MONTH FROM due_date)= :month " +
            "AND EXTRACT(YEAR FROM due_date)= :year AND type_cash_flow= :typeCashFlow"
            , nativeQuery = true)
    BigDecimal sumMovementTypeCashFlow(@Param("idUser") long idUser,@Param("month") long month,
                                       @Param("year") long year, @Param("typeCashFlow") String typeCashFlow);
}
