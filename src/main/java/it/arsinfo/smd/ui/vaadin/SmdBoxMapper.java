
package it.arsinfo.smd.ui.vaadin;

import java.util.List;
import java.util.Set;

import it.arsinfo.smd.entity.SmdEntity;

public abstract class SmdBoxMapper<T extends SmdEntity, K extends SmdEntity>
        extends SmdBox<T> {


    public SmdBoxMapper(List<T> provided) {
        super(provided);
    }
    
    public void edit(List<K> items, boolean persisted) {
        getLayout().removeAllComponents();
        getSelected().clear();
        Set<Long> matchers = match(items);
        getProvided().stream().forEach(t -> {
            getLayout().addComponent(generaBox(t,matchers.contains(t.getId()),persisted));                
        });
        getLayout().setVisible(true);
    }

    public abstract Set<Long> match(List<K> items);
    
}
