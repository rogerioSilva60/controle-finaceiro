package br.com.controlefinaceiro.financeiroapi.usuario.controller;

import br.com.controlefinaceiro.financeiroapi.usuario.dto.UsuarioDto;
import br.com.controlefinaceiro.financeiroapi.usuario.entity.Usuario;
import br.com.controlefinaceiro.financeiroapi.usuario.service.UsuarioService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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

    @GetMapping("{id}")
    public UsuarioDto obter(@PathVariable Long id){
        return usuarioService.getById(id)
                .map(usuario -> modelMapper.map(usuario, UsuarioDto.class))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable Long id){
        Usuario usuario = usuarioService.getById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        usuarioService.delete(usuario);
    }

    @PutMapping("{id}")
    public UsuarioDto atualizar(@PathVariable Long id, @RequestBody @Valid UsuarioDto dto){
        return usuarioService.getById(id)
                .map(usuario -> {
                    usuario.setNome(dto.getNome());
                    usuario.setTelefone(dto.getTelefone());
                    usuario.setDataNascimento(dto.getDataNascimento());
                    usuario = usuarioService.update(usuario);
                    return modelMapper.map(usuario, UsuarioDto.class);
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

}
