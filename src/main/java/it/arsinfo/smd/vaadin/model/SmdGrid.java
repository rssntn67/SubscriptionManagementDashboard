package it.arsinfo.smd.vaadin.model;

import java.util.List;

import com.vaadin.ui.Grid;

import it.arsinfo.smd.entity.SmdEntity;

public abstract class SmdGrid<T extends SmdEntity>
        extends SmdChangeHandler {

    private final Grid<T> grid;
    private T selected;

    public SmdGrid(Grid<T> grid) {
        this.grid = grid;
        this.grid.setWidth("80%");

        this.grid.asSingleSelect().addValueChangeListener(e -> {
            selected = e.getValue();
            onChange();
        });
        setComponents(this.grid);
    }

    public void setColumns(String...columnIds) {
        grid.setColumns(columnIds);
    }

    public void setColumnCamptio(String columnId, String caption) {
        if (grid.getColumn(columnId) == null) {
            return;
        }
        grid.getColumn(columnId).setCaption(caption);
    }
    
    public void populate(List<T> items) {
        grid.setItems(items);
    }

    public T getSelected() {
        return selected;
    }
    
}
