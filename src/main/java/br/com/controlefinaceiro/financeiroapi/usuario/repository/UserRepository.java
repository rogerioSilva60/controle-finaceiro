package br.com.controlefinaceiro.financeiroapi.usuario.repository;

import br.com.controlefinaceiro.financeiroapi.usuario.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);
}
