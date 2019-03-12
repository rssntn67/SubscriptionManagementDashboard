package it.arsinfo.smd.vaadin.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import it.arsinfo.smd.entity.SmdEntity;

public abstract class SmdSearchKey<T extends SmdEntity, K extends SmdEntity>
        extends SmdChangeHandler {

    private final JpaRepository<T, Long> repo;

    public SmdSearchKey(JpaRepository<T, Long> repo) {
        this.repo=repo;

    }
 
    public abstract List<T> findByKey(K key);

    public JpaRepository<T, Long> getRepo() {
        return repo;
    }
    
}
