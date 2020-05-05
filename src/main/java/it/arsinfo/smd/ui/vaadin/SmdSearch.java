
package it.arsinfo.smd.ui.vaadin;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import it.arsinfo.smd.entity.SmdEntity;
import it.arsinfo.smd.service.SmdService;

public abstract class SmdSearch<T extends SmdEntity>
        extends SmdChangeHandler {

    private final JpaRepository<T, Long> repo;

    private SmdService smdService;
        
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

    public SmdService getSmdService() {
        return smdService;
    }

    public void setSmdService(SmdService smdService) {
        this.smdService = smdService;
    }
}
