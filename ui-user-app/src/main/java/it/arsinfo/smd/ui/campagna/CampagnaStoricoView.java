package it.arsinfo.smd.ui.campagna;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import it.arsinfo.smd.dao.RivistaAbbonamentoDao;
import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.StatoCampagna;
import it.arsinfo.smd.data.TipoAbbonamentoRivista;
import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.Campagna;
import it.arsinfo.smd.entity.RivistaAbbonamento;
import it.arsinfo.smd.entity.Storico;
import it.arsinfo.smd.service.api.StoricoService;
import it.arsinfo.smd.ui.MainLayout;
import it.arsinfo.smd.ui.abbonamento.AbbonamentoGrid;
import it.arsinfo.smd.ui.abbonamento.RivistaAbbonamentoGrid;
import it.arsinfo.smd.ui.entity.EntityView;
import it.arsinfo.smd.ui.storico.StoricoForm;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Route(value="adp/campagna/edit", layout = MainLayout.class)
@PageTitle("Campagna | ADP Portale")
public class CampagnaStoricoView extends EntityView<Storico> {

    private final StoricoService service;

    private Campagna campagna;
    @Autowired
    private RivistaAbbonamentoDao raDao;

    public CampagnaStoricoView(@Autowired StoricoService service) {
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
        });
        getForm().addListener(StoricoForm.DeleteEvent.class, e -> {
                e.getEntity().setNumero(0);
                e.getEntity().addItem(service.getNotaOnSave(e.getEntity(),getUserSession().getUser().getEmail()));
                save(e.getEntity());
        });
        getForm().addListener(StoricoForm.CloseEvent.class, e -> closeEditor());
        HorizontalLayout toolbar = getToolBar();
        campagna = service.getByAnno(Anno.getAnnoProssimo());
        Div helper = new Div();
        helper.setText("Per modificare gli ordinativi selezionare la riga nella seguente tabella");
        Button paga = new Button("Paga -> https://retepreghierapapa.it/pagamento");
        if (campagna != null ) {
            if (campagna.getStatoCampagna() == StatoCampagna.Generata || campagna.getStatoCampagna() == StatoCampagna.Inviata) {
                toolbar.add(getAddButton());
            } else {
                helper.setText("Non è possibile modificare gli ordinativi");
                paga.setEnabled(false);
            }
            final List<Abbonamento> abbonamenti = service.findAbbonamento(campagna, getUserSession().getLoggedInIntestatario(), Anno.getAnnoProssimo());
            AbbonamentoGrid abbgrid = new AbbonamentoGrid() {

                @Override
                public List<Abbonamento> filter() {
                    setFooter(abbonamenti);
                    return abbonamenti;
                }
            };
            abbgrid.init(new Grid<>(Abbonamento.class));
            abbgrid.getGrid().setHeightByRows(true);

            RivistaAbbonamentoGrid raGrid = new RivistaAbbonamentoGrid() {

                @Override
                public List<RivistaAbbonamento> filter() {
                    List<RivistaAbbonamento> list = new ArrayList<>();
                    for (Abbonamento abb:abbonamenti) {
                        list.addAll(raDao.findByAbbonamento(abb));
                    }
                    return list;
                }
            };
            raGrid.init(new Grid<>(RivistaAbbonamento.class));
            raGrid.getGrid().setHeightByRows(true);
            abbgrid.updateList();
            raGrid.updateList();

            add(
                    toolbar,
                    new H2(" Campagna Abbonamenti"  + campagna.getHeader()),
                    new H5("Ordini"),
                    helper,
                    getContent(getGrid(),getForm()),
                    new H5("Abbonamenti"),
                    getContent(abbgrid.getGrid()),
                    paga,
                    new H5("Riviste in Abbonamento"),
                    getContent(raGrid.getGrid())
            );
        }
        getGrid().setHeightByRows(true);
        closeEditor();

    }


    @Override
    public void save(Storico entity) {
        try {
                service.aggiornaCampagna(campagna,entity,getUserSession().getUser().getEmail());
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
