package br.com.controlefinaceiro.financeiroapi.user.controller;

import br.com.controlefinaceiro.financeiroapi.user.dto.UserDto;
import br.com.controlefinaceiro.financeiroapi.user.entity.User;
import br.com.controlefinaceiro.financeiroapi.user.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/usuario")
public class UserController {

    private ModelMapper modelMapper;
    private UserService service;

    public UserController(UserService service, ModelMapper modelMapper) {
        this.service = service;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto create(@RequestBody @Valid UserDto dto){
        User user = modelMapper.map(dto, User.class);
        user = service.save(user);
        return modelMapper.map(user, UserDto.class);
    }

    @GetMapping("{id}")
    public UserDto get(@PathVariable Long id){
        return service.getById(id)
                .map(user -> modelMapper.map(user, UserDto.class))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id){
        User user = service.getById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        service.delete(user);
    }

    @PutMapping("{id}")
    public UserDto update(@PathVariable Long id, @RequestBody @Valid UserDto dto){
        return service.getById(id)
                .map(user -> {
                    user.setName(dto.getName());
                    user.setPhone(dto.getPhone());
                    user.setBirthDate(dto.getBirthDate());
                    user = service.update(user);
                    return modelMapper.map(user, UserDto.class);
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    public PageImpl<UserDto> get(UserDto dto, Pageable pageable){
        User user = modelMapper.map(dto, User.class);
        Page<User> result = service.find(user, pageable);
        List<UserDto> users = result.getContent()
                .stream()
                .map(entity -> modelMapper.map(entity, UserDto.class))
                .collect(Collectors.toList());
        return new PageImpl<UserDto>(users, pageable, result.getTotalElements());
    }

}
