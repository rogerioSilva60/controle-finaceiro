package br.com.controlefinaceiro.financeiroapi.usuario.service.impl;

import br.com.controlefinaceiro.financeiroapi.usuario.entity.User;
import br.com.controlefinaceiro.financeiroapi.usuario.repository.UserRepository;
import br.com.controlefinaceiro.financeiroapi.usuario.service.UserService;
import br.com.controlefinaceiro.financeiroapi.utils.excecoes.BusinessException;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository repository;

    public UserServiceImpl(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public User save(User user) {
        if(repository.existsByEmail(user.getEmail())){
            throw new BusinessException("Email ja cadastrado.");
        }
        return repository.save(user);
    }

    @Override
    public Optional getById(long idUser) {
        return repository.findById(idUser);
    }

    @Override
    public void delete(User user) {
        if(user == null || user.getId() == null){
            throw new IllegalArgumentException("Obrigatorio usuario para deletar.");
        }
        repository.delete(user);
    }

    @Override
    public User update(User user) {
        if(user == null || user.getId() == null){
            throw new IllegalArgumentException("Obrigatorio usuario para atualizar.");
        }
        return repository.save(user);
    }

    @Override
    public Page find(User user, Pageable pageable) {
        Example<User> example = Example.of(user,
                ExampleMatcher
                        .matching()
                        .withIgnoreCase()
                        .withIgnoreNullValues()
                        .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
        );
        return repository.findAll(example, pageable);
    }

}
