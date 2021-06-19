package it.arsinfo.smd.ui.storico;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.TipoAbbonamentoRivista;
import it.arsinfo.smd.entity.Campagna;
import it.arsinfo.smd.entity.Storico;
import it.arsinfo.smd.service.api.StoricoService;
import it.arsinfo.smd.ui.entity.EntityView;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public abstract class StoricoView extends EntityView<Storico> {

    private final StoricoService service;

    private Campagna campagna;
    @Autowired
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
                e.getEntity().addItem(service.getNotaOnSave(e.getEntity(),getUserSession().getUser().getEmail()));
                save(e.getEntity());
        });
        getForm().addListener(StoricoForm.DeleteEvent.class, e -> {
                e.getEntity().setNumero(0);
                e.getEntity().addItem(service.getNotaOnSave(e.getEntity(),getUserSession().getUser().getEmail()));
                save(e.getEntity());
        });
        getForm().addListener(StoricoForm.CloseEvent.class, e -> closeEditor());
        HorizontalLayout toolbar = getToolBar();
        campagna = service.getByAnno(anno);

        toolbar.add(getAddButton());
        Div campagnaDiv = new Div();
        if (campagna != null )
            campagnaDiv.add(campagna.getHeader());
        else
            campagnaDiv.add(anno.getAnnoAsString());
        add(toolbar,campagnaDiv,getContent(getGrid(),getForm()));
        updateList();
        closeEditor();

    }


    @Override
    public void save(Storico entity) {
        try {
            if (campagna == null) {
                super.save(entity);
            } else {
                service.aggiornaCampagna(campagna,entity,getUserSession().getUser().getEmail());
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }

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
