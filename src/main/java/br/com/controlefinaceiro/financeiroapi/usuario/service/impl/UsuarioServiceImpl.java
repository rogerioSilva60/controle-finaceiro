package br.com.controlefinaceiro.financeiroapi.usuario.service.impl;

import br.com.controlefinaceiro.financeiroapi.usuario.entity.Usuario;
import br.com.controlefinaceiro.financeiroapi.usuario.repository.UsuarioRepository;
import br.com.controlefinaceiro.financeiroapi.usuario.service.UsuarioService;
import br.com.controlefinaceiro.financeiroapi.utils.excecoes.BusinessException;
import org.springframework.stereotype.Service;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private UsuarioRepository repository;

    public UsuarioServiceImpl(UsuarioRepository repository) {
        this.repository = repository;
    }

    @Override
    public Usuario save(Usuario usuario) {
        if(repository.existsByEmail(usuario.getEmail())){
            throw new BusinessException("Email ja cadastrado.");
        }
        return repository.save(usuario);
    }

}
