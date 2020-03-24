package br.com.controlefinaceiro.financeiroapi.usuario.service;

import br.com.controlefinaceiro.financeiroapi.usuario.entity.Usuario;
import org.springframework.stereotype.Service;


public interface UsuarioService {

    Usuario save(Usuario usuario);
}
