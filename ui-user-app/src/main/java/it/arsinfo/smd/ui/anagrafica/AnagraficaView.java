package it.arsinfo.smd.ui.anagrafica;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.service.api.AnagraficaService;
import it.arsinfo.smd.ui.MainLayout;
import it.arsinfo.smd.ui.entity.EntityView;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Route(value="adp/anagrafica", layout = MainLayout.class)
@PageTitle("Anagrafica | ADP Portale")
public class AnagraficaView extends EntityView<Anagrafica> {
    private final AnagraficaService service;

    public AnagraficaView(@Autowired AnagraficaService service) {
        super(service,new Grid<>(Anagrafica.class),new AnagraficaForm(new BeanValidationBinder<>(Anagrafica.class)));
        this.service=service;
        ComboBox<Anagrafica> filterAnagrafica = new ComboBox<>();
        filterAnagrafica.setItems(service.findAll());
        filterAnagrafica.setItemLabelGenerator(Anagrafica::getCaption);

        configureGrid("titolo","denominazione", "nome", "diocesi.details",
                "citta", "provincia","cap", "paese.nome");

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
        HorizontalLayout toolbar = getToolBar();
        toolbar.addAndExpand(filterAnagrafica);
        add(toolbar,getContent(getGrid(),getForm()));
        updateList();
        closeEditor();

    }

    @Override
    public List<Anagrafica> filter() {
        return service.searchByDefault();
    }
}
