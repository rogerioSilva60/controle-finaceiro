package br.com.controlefinaceiro.financeiroapi.movement.repository;

import br.com.controlefinaceiro.financeiroapi.movement.entity.Movement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovementRepository extends JpaRepository<Movement, Long> {


}
