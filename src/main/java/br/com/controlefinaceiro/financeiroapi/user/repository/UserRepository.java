package br.com.controlefinaceiro.financeiroapi.user.repository;

import br.com.controlefinaceiro.financeiroapi.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);
}
