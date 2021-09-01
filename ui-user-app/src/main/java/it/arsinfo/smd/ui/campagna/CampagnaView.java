package it.arsinfo.smd.ui.campagna;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import it.arsinfo.smd.dao.RivistaAbbonamentoDao;
import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.Campagna;
import it.arsinfo.smd.entity.RivistaAbbonamento;
import it.arsinfo.smd.service.api.StoricoService;
import it.arsinfo.smd.ui.MainLayout;
import it.arsinfo.smd.ui.abbonamento.AbbonamentoGrid;
import it.arsinfo.smd.ui.abbonamento.RivistaAbbonamentoGrid;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Route(value="adp/campagna", layout = MainLayout.class)
@PageTitle("Campagna | ADP Portale")
public class CampagnaView extends AbbonamentoGrid {

    private final StoricoService service;
    private List<Abbonamento> abbonamenti = new ArrayList<>();
    private Campagna campagna;
    @Autowired
    private RivistaAbbonamentoDao raDao;
    RivistaAbbonamentoGrid raGrid;


    public CampagnaView(@Autowired StoricoService service) {
        this.service=service;
    }

    @PostConstruct
    public void init() {
        campagna = service.getByAnno(Anno.getAnnoProssimo());
        Grid<Abbonamento> grid = new Grid<>(Abbonamento.class);
        super.init(grid);
        grid.setHeightByRows(true);
        HorizontalLayout toolbar = getToolBar();
        Button paga = new Button("Paga -> https://retepreghierapapa.it/pagamento");
        toolbar.add(paga);

        raGrid = new RivistaAbbonamentoGrid() {
            @Override
            public List<RivistaAbbonamento> filter() {
                List<RivistaAbbonamento> list = new ArrayList<>();
                for (Abbonamento abb: abbonamenti) {
                    list.addAll(raDao.findByAbbonamento(abb));
                }
                return list;
            }
        };
        raGrid.init(new Grid<>(RivistaAbbonamento.class));
        raGrid.getGrid().setHeightByRows(true);
        add(
                toolbar,
                new H2(" Campagna Abbonamenti "  + campagna.getHeader()),
                new H5("Abbonamenti"),
                getContent(getGrid()),
                new H5("Riviste in Abbonamento"),
                getContent(raGrid.getGrid())
        );
        updateList();
    }

    @Override
    public void updateList() {
        abbonamenti = service.findAbbonamento(campagna, getUserSession().getLoggedInIntestatario(), Anno.getAnnoProssimo());
        raGrid.updateList();
        super.updateList();
    }

    @Override
    public List<Abbonamento> filter() {
        try {
            setFooter(abbonamenti);
            return abbonamenti;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
}
