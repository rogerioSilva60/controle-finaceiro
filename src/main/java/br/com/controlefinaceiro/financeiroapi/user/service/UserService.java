package br.com.controlefinaceiro.financeiroapi.user.service;

import br.com.controlefinaceiro.financeiroapi.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;


public interface UserService {

    User save(User user);

    Optional<User> getById(long idUser);

    void delete(User user);

    User update(User user);

    Page<User> find(User user, Pageable pageable);

}
