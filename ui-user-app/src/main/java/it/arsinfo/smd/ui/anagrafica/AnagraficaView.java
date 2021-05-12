package it.arsinfo.smd.ui.anagrafica;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.service.api.AnagraficaService;
import it.arsinfo.smd.ui.entity.EntityView;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


public class AnagraficaView extends EntityView<Anagrafica> {
    private final AnagraficaService service;

    public AnagraficaView(@Autowired AnagraficaService service) {
        super(service,new Grid<>(Anagrafica.class),new AnagraficaForm());
        this.service=service;
        ComboBox<Anagrafica> filterAnagrafica = new ComboBox<>();
        filterAnagrafica.setItems(service.findAll());
        filterAnagrafica.setItemLabelGenerator(Anagrafica::getCaption);

        configureGrid("denominazione");

        getForm().addListener(AnagraficaForm.SaveEvent.class, e -> {
            try {
                save(e.getEntity());
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });
        getForm().addListener(AnagraficaForm.DeleteEvent.class, e -> {
            try {
                delete(e.getEntity());
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });
        getForm().addListener(AnagraficaForm.CloseEvent.class, e -> closeEditor());

        add(getToolBar(getAddButton()),getContent());
        updateList();
        closeEditor();

    }

    @Override
    public List<Anagrafica> filter() {
        return service.searchByDefault();
    }
}
