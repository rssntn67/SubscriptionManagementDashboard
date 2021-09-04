package it.arsinfo.smd.ui.storico;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import it.arsinfo.smd.entity.Anno;
import it.arsinfo.smd.entity.TipoAbbonamentoRivista;
import it.arsinfo.smd.entity.Storico;
import it.arsinfo.smd.service.api.StoricoService;
import it.arsinfo.smd.ui.MainLayout;
import it.arsinfo.smd.ui.entity.EntityView;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Route(value="adp/storico", layout = MainLayout.class)
@PageTitle("Campagna | ADP Portale")
public class StoricoView extends EntityView<Storico> {

    private final StoricoService service;

    public StoricoView(@Autowired StoricoService service) {
        super(service);
        this.service=service;
    }

    @PostConstruct
    public void init() {
        Grid<Storico> grid =new Grid<>(Storico.class);
        StoricoForm form =
                new StoricoForm(new BeanValidationBinder<>(Storico.class),getUserSession().getAnagraficaStorico(),service.findPubblicazioni());
        super.init(grid,form);
        configureGrid(
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
                closeEditor();
        });
        getForm().addListener(StoricoForm.DeleteEvent.class, e -> {
                e.getEntity().setNumero(0);
                e.getEntity().addItem(service.getNotaOnSave(e.getEntity(),getUserSession().getUser().getEmail()));
                save(e.getEntity());
                closeEditor();
        });
        getForm().addListener(StoricoForm.CloseEvent.class, e -> closeEditor());
        HorizontalLayout toolbar = getToolBar();
        toolbar.add(getAddButton());
        Div helper = new Div();
        helper.setText("Per modificare gli ordinativi selezionare la riga nella seguente tabella");
        add(
            toolbar,
            new H3("La Campagna " + Anno.getAnnoProssimo().getAnnoAsString() +" non è stata ancora generata"),
            helper,
            new H2("ordini"),
            getContent(getGrid(), getForm())
        );
        getGrid().setHeightByRows(true);
        closeEditor();
    }


    @Override
    public void save(Storico entity) {
        try {
                super.save(entity);
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
            getForm().isNew();
        }
        super.edit(t);
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
