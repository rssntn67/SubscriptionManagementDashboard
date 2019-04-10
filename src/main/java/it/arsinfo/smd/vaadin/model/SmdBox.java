
package it.arsinfo.smd.vaadin.model;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;

import it.arsinfo.smd.entity.SmdEntity;

public abstract class SmdBox<T extends SmdEntity>
    extends SmdChangeHandler {

    private HorizontalLayout layout= new HorizontalLayout();
    private List<T> selected = new ArrayList<>();
    private final List<T> provided;

    public SmdBox(List<T> provided) {
        this.provided=provided;
        setComponents(layout);
    }

    public List<T> getSelected() {
        return selected;
    }

    public List<T> getProvided() {
        return provided;
    }
    
    public CheckBox generaBox(T t, boolean value, boolean persisted) {
        CheckBox cbx = new CheckBox(getBoxCaption(t));
        cbx.setValue(value);
        cbx.addValueChangeListener( e -> {
            if (e.getValue()) {
                selected.add(t);
            } else {
                selected.remove(t);
            }
            onChange();
        });
        cbx.setReadOnly(getReadOnly(t, persisted));
        return cbx;

    }

    public void edit(boolean persisted) {
        layout.removeAllComponents();
        selected.clear();

        provided.stream().forEach(t -> {
            layout.addComponent(generaBox(t,false,persisted));                
        });
    }

    public abstract boolean getReadOnly(T t, boolean persisted);
    public abstract String getBoxCaption(T t);

    public HorizontalLayout getLayout() {
        return layout;
    }

    public void setLayout(HorizontalLayout layout) {
        this.layout = layout;
    }
    
}
