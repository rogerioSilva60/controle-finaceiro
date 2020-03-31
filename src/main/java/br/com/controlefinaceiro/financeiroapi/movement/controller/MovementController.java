package br.com.controlefinaceiro.financeiroapi.movement.controller;

import br.com.controlefinaceiro.financeiroapi.movement.dto.FinancialAnalysisDto;
import br.com.controlefinaceiro.financeiroapi.movement.dto.MovementDto;
import br.com.controlefinaceiro.financeiroapi.movement.entity.Movement;
import br.com.controlefinaceiro.financeiroapi.movement.service.MovementService;
import br.com.controlefinaceiro.financeiroapi.response.Response;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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
    public ResponseEntity<Response<MovementDto>> create(@RequestBody @Valid MovementDto dto){
        Response<MovementDto> response = new Response<>();
        Movement movement = modelMapper.map(dto, Movement.class);
        movement = service.save(movement);
        MovementDto movementDto = modelMapper.map(movement, MovementDto.class);
        response.setData(movementDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id){
        Movement movement = service.getById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        service.delete(movement);
    }

    @PutMapping("{id}")
    public ResponseEntity<Response<MovementDto>> update(@PathVariable Long id, @RequestBody @Valid MovementDto dto){
        Response<MovementDto> response = new Response<>();
        response.setData(
                service.getById(id)
                        .map(movement -> {
                            movement.setTypeCashFlow(dto.getTypeCashFlow());
                            movement.setDescription(dto.getDescription());
                            movement.setDueDate(dto.getDueDate());
                            movement.setPayDay(dto.getPayDay());
                            movement.setValue(dto.getValue());
                            movement = service.update(movement);
                            return modelMapper.map(movement, MovementDto.class);
                        })
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND))
        );
        return ResponseEntity.ok(response);
    }



    @GetMapping
    public ResponseEntity<Response<PageImpl<MovementDto>>> get( Long idUser,
                        @DateTimeFormat(pattern = "yyyy-MM-dd") Date dueDateInitial,
                        @DateTimeFormat(pattern = "yyyy-MM-dd") Date dueDateEnd,
                        Pageable pageable){
        Response<PageImpl<MovementDto>> response = new Response<>();
        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.Direction.DESC, "dueDate");

        Page<Movement> result = service.findByExpirationDate(idUser, dueDateInitial, dueDateEnd, pageable);
        List<MovementDto> movements = result != null ? result.getContent()
                .stream()
                .map(movement -> modelMapper.map(movement, MovementDto.class))
                .collect(Collectors.toList()) : Arrays.asList();

        response.setData(new PageImpl<MovementDto> (movements, pageable, result != null ? result.getTotalElements() : 0));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/analise-financeiro-pessoal")
    public ResponseEntity<Response<FinancialAnalysisDto>> getPersonalFinancialAnalysis(
             Long idUser, Long month, Long year){
        Response<FinancialAnalysisDto> response = new Response<>();
        response.setData(service.personalFinancialAnalysis(idUser, month, year));

        return ResponseEntity.ok(response);
    }
}
