package br.com.controlefinaceiro.financeiroapi.usuario.controller;

import br.com.controlefinaceiro.financeiroapi.usuario.dto.UsuarioDto;
import br.com.controlefinaceiro.financeiroapi.usuario.entity.Usuario;
import br.com.controlefinaceiro.financeiroapi.usuario.service.UsuarioService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/usuario")
public class UsuarioController {

    private ModelMapper modelMapper;
    private UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService, ModelMapper modelMapper) {
        this.usuarioService = usuarioService;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioDto criar( @RequestBody @Valid  UsuarioDto dto){
        Usuario usuario = modelMapper.map(dto, Usuario.class);
        usuario = usuarioService.save(usuario);
        return modelMapper.map(usuario, UsuarioDto.class);
    }

}
