package it.arsinfo.smd.vaadin.model;

import java.util.List;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;

import it.arsinfo.smd.entity.SmdEntity;

public abstract class SmdSearchKey<T extends SmdEntity, K extends SmdEntity>
        extends SmdChangeHandler {

    /**
     * 
     */
    private static final long serialVersionUID = 7884064928998716106L;

    private final Grid<T> grid;
    private K key;
    private T selected;
    private Button add = new Button("Add", VaadinIcons.PLUS);

    public SmdSearchKey(Grid<T> grid) {
        this.grid = grid;
        this.grid.setWidth("80%");

        this.grid.asSingleSelect().addValueChangeListener(e -> {
            selected = e.getValue();
            onChange();
        });

        add.addClickListener(e -> {
            selected = generate();
            onChange();
        });

    }

    public void onSearch() {
        grid.setItems(searchByKey());
        setVisible(true);
    }

    public abstract T generate();

    public abstract List<T> searchByKey();

    public Grid<T> getGrid() {
        return grid;
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

    
    public T getSelected() {
        return selected;
    }

    public Button getAdd() {
        return add;
    }

    public K getKey() {
        return key;
    }

    public void setKey(K upper) {
        this.key = upper;
    }
    
}
