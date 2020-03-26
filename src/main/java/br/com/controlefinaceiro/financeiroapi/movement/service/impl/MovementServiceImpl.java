package br.com.controlefinaceiro.financeiroapi.movement.service.impl;

import br.com.controlefinaceiro.financeiroapi.movement.entity.Movement;
import br.com.controlefinaceiro.financeiroapi.movement.repository.MovementRepository;
import br.com.controlefinaceiro.financeiroapi.movement.service.MovementService;
import br.com.controlefinaceiro.financeiroapi.utils.exception.BusinessException;
import org.springframework.stereotype.Service;

@Service
public class MovementServiceImpl implements MovementService {

    private MovementRepository repository;

    public MovementServiceImpl(MovementRepository repository) {
        this.repository = repository;
    }

    @Override
    public Movement save(Movement movement) {
        if(repository.existsById(movement.getId())){
            throw new BusinessException("Movimentacao ja cadastrada.");
        }
        return repository.save(movement);
    }
}
