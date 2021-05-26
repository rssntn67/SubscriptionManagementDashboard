package it.arsinfo.smd.ui.abbonamento;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import it.arsinfo.smd.dao.AbbonamentoDao;
import it.arsinfo.smd.dao.RivistaAbbonamentoDao;
import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.RivistaAbbonamento;
import it.arsinfo.smd.ui.MainLayout;
import it.arsinfo.smd.ui.entity.EntityGridView;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * A sample Vaadin view class.
 * <p>
 * To implement a Vaadin view just extend any Vaadin component and
 * use @Route annotation to announce it in a URL as a Spring managed
 * bean.
 * Use the @PWA annotation make the application installable on phones,
 * tablets and some desktop browsers.
 * <p>
 * A new instance of this class is created for every new user and every
 * browser tab/window.
 */

@Route(value="adp/abbonamento", layout = MainLayout.class)
@PageTitle("Abbonamento | ADP Portale")
public class AbbonamentoView extends EntityGridView<RivistaAbbonamento> {

    @Autowired
    private AbbonamentoDao abbonamentoDao;

    @Autowired
    private RivistaAbbonamentoDao rivistaAbbonamentoDao;

    private Abbonamento abbonamento;

    public AbbonamentoView() {
        super();
    }

    @Override
    public List<RivistaAbbonamento> filter() {
            return rivistaAbbonamentoDao.findByAbbonamento(abbonamento);
    }

    @PostConstruct
    public void init() {
        init(new Grid<>(RivistaAbbonamento.class));
        configureGrid("beneficiario");
        setColumnCaption("pubblicazione.nome","Pubblicazione");
        setColumnCaption("inizio","Inizio");
        setColumnCaption("fine","Fine");
        setColumnCaption("tipoAbbonamentoRivista","Abbonamento");
        setColumnCaption("statoRivista","Stato");
        setColumnCaption("numero","Quan.tà");
        //FIXME EURO Converter
        setColumnCaption("importo", "Importo");


        HorizontalLayout toolbar =  getToolBar();
        abbonamento = abbonamentoDao.findByIntestatarioAndAnno(getUserSession().getLoggedInIntestatario(), Anno
                .getAnnoCorrente()).iterator().next();
        toolbar.add(new H2("Riviste in Abbonamento: " + getUserSession().getLoggedInIntestatario().getCaption()));
        Div importi = new Div();
//        abbonamento.getIncassato();
//        abbonamento.getSpese();
//        abbonamento.getImporto();
//        abbonamento.getResiduo();
        importi.setText("Importi: da pagare. Stato Abbonamento" + abbonamento.getStatoAbbonamento());
        toolbar.add(importi);

        add(toolbar,getContent(getGrid()));
        updateList();

    }
}
