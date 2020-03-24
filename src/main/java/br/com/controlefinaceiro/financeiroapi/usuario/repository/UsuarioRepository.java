package br.com.controlefinaceiro.financeiroapi.usuario.repository;

import br.com.controlefinaceiro.financeiroapi.usuario.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    boolean existsByEmail(String email);
}
