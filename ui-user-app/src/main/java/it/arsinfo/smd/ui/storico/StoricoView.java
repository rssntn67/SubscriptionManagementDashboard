package it.arsinfo.smd.ui.storico;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.TipoAbbonamentoRivista;
import it.arsinfo.smd.entity.Storico;
import it.arsinfo.smd.service.api.StoricoService;
import it.arsinfo.smd.ui.entity.EntityView;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public abstract class StoricoView extends EntityView<Storico> {

    private final StoricoService service;
    public StoricoView(@Autowired StoricoService service) {
        super(service);
        this.service=service;
    }

    public void init(Anno anno) {
        Grid<Storico> grid =new Grid<>(Storico.class);
        StoricoForm form =
                new StoricoForm(new BeanValidationBinder<>(Storico.class),getUserSession().getAnagraficaStorico(),service.findPubblicazioni());
        super.init(grid,form);
        configureGrid(
                "intestazione",
                "beneficiario",
                "tipoAbbonamentoRivista");
        setColumnCaption("numero", "Numero");
        setColumnCaption("captionPubblicazione", "Pubblicazione");
        setColumnCaption("invioSpedizione", "Sped.");
        setColumnCaption("statoStorico", "Stato");
        setColumnCaption("contrassegno", "Contrassegno");

        getForm().addListener(StoricoForm.SaveEvent.class, e -> {
            try {
                e.getEntity().addItem(service.getNotaOnSave(e.getEntity(),getUserSession().getUser().getEmail()));
                save(e.getEntity());
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });
        getForm().addListener(StoricoForm.DeleteEvent.class, e -> {
            try {
                e.getEntity().setNumero(0);
                e.getEntity().addItem(service.getNotaOnSave(e.getEntity(),getUserSession().getUser().getEmail()));
                save(e.getEntity());
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });
        getForm().addListener(StoricoForm.CloseEvent.class, e -> closeEditor());
        HorizontalLayout toolbar = getToolBar();
        toolbar.add(getAddButton(), new H2(anno.getAnnoAsString()));
        add(toolbar,getContent(getGrid(),getForm()));
        updateList();
        closeEditor();

    }

    @Override
    public void edit(Storico t) {
        assert t != null;
        if (t.getId() == null) {
            t.setIntestatario(getUserSession().getLoggedInIntestatario());
            t.setDestinatario(getUserSession().getLoggedInIntestatario());
            t.setTipoAbbonamentoRivista(TipoAbbonamentoRivista.Ordinario);
        }
        super.edit(t);
        if (t.getId() == null) {
            getForm().isNew();
            return;
        }
        switch (t.getTipoAbbonamentoRivista()) {
            case OmaggioCuriaDiocesiana:
            case OmaggioEditore:
            case OmaggioCuriaGeneralizia:
            case OmaggioDirettoreAdp:
            case OmaggioGesuiti:
            case Scontato:
                getForm().setReadOnly(true);
                break;
            default:
                getForm().setReadOnly(!t.getIntestatario().equals(getUserSession().getLoggedInIntestatario()));
        }

    }

    @Override
    public List<Storico> filter() {
        try {
            return service.searchBy(getUserSession().getLoggedInIntestatario());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
}
