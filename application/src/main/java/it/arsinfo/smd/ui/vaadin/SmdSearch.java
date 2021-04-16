
package it.arsinfo.smd.ui.vaadin;

import java.util.List;

import it.arsinfo.smd.dao.SmdServiceDao;
import it.arsinfo.smd.entity.SmdEntity;

public abstract class SmdSearch<T extends SmdEntity>
        extends SmdChangeHandler {

    private final SmdServiceDao<T> dao;
        
    public SmdSearch(SmdServiceDao<T> dao) {
        this.dao=dao;

    }

    public abstract List<T> find();
        
    public List<T> findAll() {
        return dao.findAll();
    }

}
