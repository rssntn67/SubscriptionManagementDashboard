package it.arsinfo.smd.ui.entity;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import it.arsinfo.smd.data.UserSession;
import it.arsinfo.smd.entity.SmdEntity;
import it.arsinfo.smd.service.api.SmdServiceBase;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.List;

public abstract class EntityView<T extends SmdEntity> extends VerticalLayout {
    private Grid<T> grid;
    final private SmdServiceBase<T> service;
    private EntityForm<T> form;

    @Autowired
    private UserSession userSession;

    public UserSession getUserSession() {
        return userSession;
    }

    public EntityView(@Autowired SmdServiceBase<T> service) {
        this.service=service;
    }

    @PostConstruct
    public void init(Grid<T> grid, EntityForm<T> form) {
        this.form=form;
        this.grid=grid;
        addClassName("gc-view");
        form.addClassName("form");
        setSizeFull();

    }
    public HorizontalLayout getToolBar() {
        HorizontalLayout toolbar = new HorizontalLayout();
        toolbar.addClassName("toolbar");
        return toolbar;
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
        updateList();
        closeEditor();
    }

    public void delete(T entity) throws Exception {
        service.delete(entity);
        updateList();
        closeEditor();
    }

    public void edit(T entity) {
        if (entity == null) {
            closeEditor();
        } else {
            form.setEntity(entity);
            form.setVisible(true);
            grid.setVisible(false);
            addClassName("editing");
        }
    }

    public void closeEditor() {
        form.setEntity(null);
        form.setVisible(false);
        removeClassName("editing");
        grid.setVisible(true);
    }

    public Button getAddButton() {
        Button addButton = new Button("Add");
        addButton.addClickListener(click -> add());
        return addButton;

    }

    public void configureGrid(String...columns) {
        grid.addClassName("grid");
        grid.setColumns(columns);
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
        grid.asSingleSelect().addValueChangeListener(event ->
                edit(event.getValue()));
    }

    public void setColumnCaption(String column,String header) {
        grid.addColumn(column).setHeader(header);
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
    }

    public void updateList() {
        grid.setItems(filter());
    }

    void add() {
        grid.asSingleSelect().clear();
        edit(service.add());
    }

    public abstract List<T> filter();

    public Grid<T> getGrid() {
        return grid;
    }

}
