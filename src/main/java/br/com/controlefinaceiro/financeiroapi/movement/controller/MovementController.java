package br.com.controlefinaceiro.financeiroapi.movement.controller;

import br.com.controlefinaceiro.financeiroapi.movement.dto.MovementDto;
import br.com.controlefinaceiro.financeiroapi.movement.entity.Movement;
import br.com.controlefinaceiro.financeiroapi.movement.service.MovementService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/movimentacao")
public class MovementController {


    ModelMapper modelMapper;
    MovementService service;

    public MovementController(ModelMapper modelMapper, MovementService service) {
        this.modelMapper = modelMapper;
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MovementDto create(@RequestBody @Valid MovementDto dto){
        Movement movement = modelMapper.map(dto, Movement.class);
        movement = service.save(movement);
        return modelMapper.map(movement, MovementDto.class);
    }

}
