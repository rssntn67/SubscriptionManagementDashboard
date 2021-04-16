
package it.arsinfo.smd.ui.vaadin;

import java.util.List;

import it.arsinfo.smd.service.api.SmdServiceBase;
import it.arsinfo.smd.entity.SmdEntity;

public abstract class SmdSearch<T extends SmdEntity>
        extends SmdChangeHandler {

    private final SmdServiceBase<T> dao;
        
    public SmdSearch(SmdServiceBase<T> dao) {
        this.dao=dao;

    }

    public abstract List<T> find();
        
    public List<T> findAll() {
        return dao.findAll();
    }

}
