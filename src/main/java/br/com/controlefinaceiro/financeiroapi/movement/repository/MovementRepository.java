package br.com.controlefinaceiro.financeiroapi.movement.repository;

import br.com.controlefinaceiro.financeiroapi.movement.entity.Movement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.Date;

public interface MovementRepository extends JpaRepository<Movement, Long> {

    @Query(value = "SELECT m FROM Movement m WHERE m.user.id= ?1 AND m.dueDate>= ?2 AND m.dueDate<= ?3",
    countQuery = "SELECT COUNT(m.id) FROM Movement m WHERE m.user.id= ?1 AND m.dueDate>= ?2 AND m.dueDate<= ?3")
    Page<Movement> findByExpirationDate(Long idUser, Date dueDateInitial, Date dueDateEnd, Pageable pageable);
}
