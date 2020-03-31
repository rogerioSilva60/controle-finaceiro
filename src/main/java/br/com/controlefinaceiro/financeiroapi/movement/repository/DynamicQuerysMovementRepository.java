package br.com.controlefinaceiro.financeiroapi.movement.repository;

import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class DynamicQuerysMovementRepository {

    @PersistenceContext
    private final EntityManager manager;

    public DynamicQuerysMovementRepository(EntityManager manager) {
        this.manager = manager;
    }

}
