package br.com.controlefinaceiro.financeiroapi.usuario.service;

import br.com.controlefinaceiro.financeiroapi.usuario.entity.Usuario;
import org.springframework.stereotype.Service;

import java.util.Optional;


public interface UsuarioService {

    Usuario save(Usuario usuario);

    Optional<Usuario> getById(long idUsuario);

    void delete(Usuario usuario);

    Usuario update(Usuario usuario);
}
