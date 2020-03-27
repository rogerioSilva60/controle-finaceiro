package br.com.controlefinaceiro.financeiroapi.movement.service;

import br.com.controlefinaceiro.financeiroapi.movement.entity.Movement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Date;

public interface MovementService {

    Movement save(Movement movement);

    Page<Movement> findByExpirationDate(long idUser, Date dueDateInitial, Date dueDateEnd, Pageable pageable);
}
