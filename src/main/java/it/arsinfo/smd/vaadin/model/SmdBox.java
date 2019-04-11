
package it.arsinfo.smd.vaadin.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;

public abstract class SmdBox<T> {

    private HorizontalLayout layout= new HorizontalLayout();
    private List<T> selected = new ArrayList<>();
    private final Collection<T> provided;

    public SmdBox(Collection<T> provided) {
        this.provided=provided;
    }

    public List<T> getSelected() {
        return selected;
    }

    public Collection<T> getProvided() {
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
        layout.setVisible(true);
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
