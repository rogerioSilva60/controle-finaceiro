package br.com.controlefinaceiro.financeiroapi.movement.service.impl;

import br.com.controlefinaceiro.financeiroapi.movement.entity.Movement;
import br.com.controlefinaceiro.financeiroapi.movement.repository.MovementRepository;
import br.com.controlefinaceiro.financeiroapi.movement.service.MovementService;
import br.com.controlefinaceiro.financeiroapi.user.service.UserService;
import br.com.controlefinaceiro.financeiroapi.utils.exception.BusinessException;
import org.springframework.stereotype.Service;

@Service
public class MovementServiceImpl implements MovementService {

    private MovementRepository repository;

    private UserService userService;

    public MovementServiceImpl(MovementRepository repository, UserService userService) {
        this.repository = repository;
        this.userService = userService;
    }

    @Override
    public Movement save(Movement movement) {
        prepareMovementToSave(movement);
        return repository.save(movement);
    }

    private void prepareMovementToSave(Movement movement) {
        if(movement == null){
            throw new BusinessException("Movimentacao nao pode salvar vazio.");
        } else if(movement.getId() != null){
            if(repository.existsById(movement.getId()))
                throw new BusinessException("Movimentacao ja cadastrada.");
        } else if(movement.getTypeCashFlow() == null){
            throw new BusinessException("Fluxo do caixa da movimentacao nao pode salvar vazio.");
        } else if(movement.getUser().getId() == null){
            throw new BusinessException("Id do usuario é obrigatorio.");
        } else if(!userService.getById(movement.getUser().getId()).isPresent()){
            throw new BusinessException("Id do usuario não identificado, " +
                    "Verifique se o mesmo foi cadastrado antes de salvar uma movimentacao.");
        }
    }
}
