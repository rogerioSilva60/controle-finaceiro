package br.com.controlefinaceiro.financeiroapi.goal.controller;

import br.com.controlefinaceiro.financeiroapi.goal.dto.GoalDto;
import br.com.controlefinaceiro.financeiroapi.goal.entity.Goal;
import br.com.controlefinaceiro.financeiroapi.goal.service.GoalService;
import br.com.controlefinaceiro.financeiroapi.response.Response;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/meta")
public class GoalController {

    private ModelMapper modelMapper;
    private GoalService service;

    public GoalController(ModelMapper modelMapper, GoalService service) {
        this.modelMapper = modelMapper;
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Response<GoalDto>> create(@RequestBody @Valid GoalDto dto){
        Response<GoalDto> response = new Response<>();
        Goal goal = modelMapper.map(dto, Goal.class);
        goal = service.save(goal);
        GoalDto goalDto = modelMapper.map(goal, GoalDto.class);
        response.setData(goalDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("{id}")
    public ResponseEntity<Response<GoalDto>> update(@PathVariable Long id, @RequestBody @Valid GoalDto dto){
        Response<GoalDto> response = new Response<>();
        response.setData(
                service.getById(id)
                    .map(goal -> {
                    goal.setMonth(dto.getMonth());
                    goal.setYear(dto.getYear());
                    goal.setValue(dto.getValue());
                    return modelMapper.map(goal, GoalDto.class);
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)));
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id){
        Goal goal = service.getById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        service.delete(goal);
    }
}
