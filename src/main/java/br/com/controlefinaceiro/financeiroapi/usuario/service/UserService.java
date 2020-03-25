package br.com.controlefinaceiro.financeiroapi.usuario.service;

import br.com.controlefinaceiro.financeiroapi.usuario.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;


public interface UserService {

    User save(User user);

    Optional<User> getById(long idUser);

    void delete(User user);

    User update(User user);

    Page<User> find(User user, Pageable pageable);

}
