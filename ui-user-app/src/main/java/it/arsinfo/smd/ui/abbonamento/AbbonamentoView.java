package it.arsinfo.smd.ui.abbonamento;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import it.arsinfo.smd.dao.RivistaAbbonamentoDao;
import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.Campagna;
import it.arsinfo.smd.entity.RivistaAbbonamento;
import it.arsinfo.smd.service.api.StoricoService;
import it.arsinfo.smd.ui.MainLayout;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Route(value="adp/abbonamento", layout = MainLayout.class)
@PageTitle("Campagna Anno Corrente| ADP Portale")
public class AbbonamentoView extends AbbonamentoGrid {

    private final StoricoService service;

    private Campagna campagna;
    @Autowired
    private RivistaAbbonamentoDao raDao;

    public AbbonamentoView(@Autowired StoricoService service)  {
        this.service=service;
    }

    @PostConstruct
    public void init() {
        super.init(new Grid<>(Abbonamento.class));
        campagna = service.getByAnno(Anno.getAnnoCorrente());
        List<Abbonamento> abbonamentoList = filter();
        RivistaAbbonamentoGrid raGrid = new RivistaAbbonamentoGrid() {

            @Override
            public List<RivistaAbbonamento> filter() {
                List<RivistaAbbonamento> list = new ArrayList<>();
                for (Abbonamento abb: abbonamentoList) {
                    list.addAll(raDao.findByAbbonamento(abb));
                }
                return list;
            }
        };
        raGrid.init(new Grid<>(RivistaAbbonamento.class));
        raGrid.getGrid().setHeightByRows(true);

        add(
            new H3(campagna.getHeader()),
            new H2("Abbonamenti"),
            getContent(getGrid()),
            new H2("Riviste in Abbonamento"),
            getContent(raGrid.getGrid())
        );
        getGrid().setHeightByRows(true);

        getGrid().setItems(abbonamentoList);
        raGrid.updateList();

    }



    @Override
    public List<Abbonamento> filter() {
        try {
            return service.findAbbonamento(campagna,getUserSession().getLoggedInIntestatario(),Anno.getAnnoCorrente());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
}
