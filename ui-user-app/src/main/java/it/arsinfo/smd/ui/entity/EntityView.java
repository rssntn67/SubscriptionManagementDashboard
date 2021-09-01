package it.arsinfo.smd.ui.entity;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import it.arsinfo.smd.entity.SmdEntity;
import it.arsinfo.smd.service.api.SmdServiceBase;

public abstract class EntityView<T extends SmdEntity> extends EntityGridView<T> {
    private EntityForm<T> form;

    private final SmdServiceBase<T> service;

    public EntityView(SmdServiceBase<T> service) {
        super();
        this.service=service;
    }

    public void init(Grid<T> grid, EntityForm<T> form) {
        this.form=form;
        form.addClassName("form");
        super.init(grid);
    }

    public Div getContent(Component...components) {
        Div content = new Div(components);
        content.addClassName("content");
        content.setSizeFull();
        return content;
    }

    public EntityForm<T> getForm() {
        return form;
    }

    public void save(T entity) throws Exception {
        service.save(entity);
        closeEditor();
    }

    public void delete(T entity) throws Exception {
        service.delete(entity);
        closeEditor();
    }

    public void edit(T entity) {
        if (entity == null) {
            closeEditor();
        } else {
            form.setEntity(entity);
            form.setVisible(true);
            getGrid().setVisible(false);
            addClassName("editing");
        }
    }

    public void closeEditor() {
        form.setEntity(null);
        form.setVisible(false);
        removeClassName("editing");
        getGrid().setVisible(true);
        updateList();
    }

    public Button getAddButton() {
        Button addButton = new Button("Add");
        addButton.addClickListener(click -> add());
        return addButton;

    }

    public void configureGrid(String...columns) {
        super.configureGrid(columns);
        getGrid().asSingleSelect().addValueChangeListener(event ->
                edit(event.getValue()));
    }

    void add() {
        getGrid().asSingleSelect().clear();
        edit(service.add());
    }
   }
