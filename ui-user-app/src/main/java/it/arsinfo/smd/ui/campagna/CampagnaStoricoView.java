package it.arsinfo.smd.ui.campagna;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import it.arsinfo.smd.dao.RivistaDao;
import it.arsinfo.smd.entity.Anno;
import it.arsinfo.smd.entity.TipoAbbonamentoRivista;
import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.Campagna;
import it.arsinfo.smd.entity.Rivista;
import it.arsinfo.smd.entity.Storico;
import it.arsinfo.smd.service.api.StoricoService;
import it.arsinfo.smd.service.api.WooCommerceOrderService;
import it.arsinfo.smd.ui.MainLayout;
import it.arsinfo.smd.ui.abbonamento.AbbonamentoGrid;
import it.arsinfo.smd.ui.abbonamento.RivistaDtoGrid;
import it.arsinfo.smd.ui.entity.EntityView;
import it.arsinfo.smd.ui.storico.StoricoForm;
import it.arsinfo.smd.woocommerce.api.WooCommerceApiService;
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
    private RivistaDao raDao;
    @Autowired
    private WooCommerceOrderService wooCommerceOrderService;
    @Autowired
    private WooCommerceApiService wooCommerceApi;

    private RivistaDtoGrid raGrid;
    private AbbonamentoGrid abbgrid;
    private List<Abbonamento> abbonamenti = new ArrayList<>();

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
        toolbar.add(getAddButton());
        abbgrid = new AbbonamentoGrid() {
            @Override
            public List<Abbonamento> filter() {
                setFooter(abbonamenti);
                return abbonamenti;
            }
        };
        abbgrid.init(new Grid<>(Abbonamento.class));
        abbgrid.getGrid()
                .addColumn(new ComponentRenderer<>(
                        abbonamento ->
                                new CampagnaPaga(getUserSession().getLoggedIn(), abbonamento,wooCommerceOrderService,wooCommerceApi)))
                .setHeader("Pagamento");
        abbgrid.getGrid().setHeightByRows(true);


        raGrid = new RivistaDtoGrid() {
            @Override
            public List<Rivista> filter() {
                List<Rivista> list = new ArrayList<>();
                for (Abbonamento abb:abbonamenti) {
                    list.addAll(raDao.findByAbbonamento(abb));
                }
                return list;
            }
        };
        raGrid.init(new Grid<>(Rivista.class));
        raGrid.getGrid().setHeightByRows(true);

        add(
            toolbar,
            new H2(" Campagna Abbonamenti"  + campagna.getHeader()),
            new H5("Ordini - Per modificare gli ordinativi selezionare la riga nella tabella"),
            getContent(getGrid(),getForm()),
            new H5("Abbonamenti - Per pagare online selezionare paga (redirect su Ecommerce ADP)"),
            getContent(abbgrid.getGrid()),
            new H5("Riviste in Abbonamento"),
            getContent(raGrid.getGrid())
        );
        getGrid().setHeightByRows(true);
        closeEditor();

    }

    @Override
    public void updateList() {
        abbonamenti = service.findAbbonamento(campagna, getUserSession().getLoggedInIntestatario(), Anno.getAnnoProssimo());
        abbgrid.updateList();
        raGrid.updateList();
        super.updateList();
    }

    @Override
    public void save(Storico entity) {
        try {
            service.aggiornaCampagna(campagna,entity,getUserSession().getUser().getEmail());
            closeEditor();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void edit(Storico t) {
        super.edit(t);
        if (t == null)
            return;
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
