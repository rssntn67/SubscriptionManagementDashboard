package it.arsinfo.smd.ui.entity;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import it.arsinfo.smd.data.UserSession;
import it.arsinfo.smd.entity.SmdEntity;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public abstract class EntityGridView<T extends SmdEntity> extends VerticalLayout {
    private Grid<T> grid;

    @Autowired
    private UserSession userSession;

    public UserSession getUserSession() {
        return userSession;
    }

    public EntityGridView() {
    }

    public void init(Grid<T> grid) {
        this.grid=grid;
        addClassName("gc-view");
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


    public void configureGrid(String...columns) {
        grid.addClassName("grid");
        grid.setColumns(columns);
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
    }

    public void setColumnCaption(String column,String header) {
        grid.addColumn(column).setHeader(header);
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
    }

    public void updateList() {
        grid.setItems(filter());
    }

    public abstract List<T> filter();

    public Grid<T> getGrid() {
        return grid;
    }

}
