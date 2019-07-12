
package it.arsinfo.smd.ui.vaadin;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import it.arsinfo.smd.SmdEntity;

public abstract class SmdSearch<T extends SmdEntity>
        extends SmdChangeHandler {

    private final JpaRepository<T, Long> repo;

    public SmdSearch(JpaRepository<T, Long> repo) {
        this.repo=repo;

    }

    public abstract List<T> find();
    
    public List<T> findAll() {
        return repo.findAll();
    }

    public JpaRepository<T, Long> getRepo() {
        return repo;
    }
}
