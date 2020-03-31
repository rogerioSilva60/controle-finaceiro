package br.com.controlefinaceiro.financeiroapi.movement.service;

import br.com.controlefinaceiro.financeiroapi.movement.dto.FinancialAnalysisDto;
import br.com.controlefinaceiro.financeiroapi.movement.entity.Movement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.Optional;

public interface MovementService {

    Movement save(Movement movement);

    Page<Movement> findByExpirationDate(long idUser, Date dueDateInitial, Date dueDateEnd, Pageable pageable);

    Movement update(Movement movement);

    Optional<Movement> getById(long id);

    void delete(Movement movement);

    FinancialAnalysisDto personalFinancialAnalysis (long idUser, long month, long year);
}
