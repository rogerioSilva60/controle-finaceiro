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
        prepareMovement(movement);
        return repository.save(movement);
    }

    private void prepareMovement(Movement movement) {
        if(movement == null){
            throw new BusinessException("Movimentacao nao pode salvar vazio.");
        } else if(movement.getId() != null){
            if(repository.existsById(movement.getId()))
                throw new BusinessException("Movimentacao ja cadastrada.");
        } else if(movement.getTypeCashFlow() == null){
            throw new BusinessException("Fluxo do caixa da movimentacao nao pode salvar vazio.");
        }
    }
}
