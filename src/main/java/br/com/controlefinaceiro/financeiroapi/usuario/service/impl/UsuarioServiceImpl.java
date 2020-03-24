package br.com.controlefinaceiro.financeiroapi.usuario.service.impl;

import br.com.controlefinaceiro.financeiroapi.usuario.entity.Usuario;
import br.com.controlefinaceiro.financeiroapi.usuario.repository.UsuarioRepository;
import br.com.controlefinaceiro.financeiroapi.usuario.service.UsuarioService;
import br.com.controlefinaceiro.financeiroapi.utils.excecoes.BusinessException;
import org.springframework.stereotype.Service;

import java.util.Optional;

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

    @Override
    public Optional getById(long idUsuario) {
        return repository.findById(idUsuario);
    }

    @Override
    public void delete(Usuario usuario) {
        if(usuario == null || usuario.getId() == null){
            throw new IllegalArgumentException("Obrigatorio usuario para deletar.");
        }
        repository.delete(usuario);
    }

    @Override
    public Usuario update(Usuario usuario) {
        if(usuario == null || usuario.getId() == null){
            throw new IllegalArgumentException("Obrigatorio usuario para atualizar.");
        }
        return repository.save(usuario);
    }

}
