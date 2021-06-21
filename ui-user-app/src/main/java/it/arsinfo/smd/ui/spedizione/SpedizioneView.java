package it.arsinfo.smd.ui.spedizione;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.NumberRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.Mese;
import it.arsinfo.smd.entity.Spedizione;
import it.arsinfo.smd.entity.SpedizioneItem;
import it.arsinfo.smd.service.Smd;
import it.arsinfo.smd.service.api.SpedizioneService;
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

@Route(value="adp/spedizione", layout = MainLayout.class)
@PageTitle("Spedizione | ADP Portale")
public class SpedizioneView extends EntityGridView<Spedizione> {

    private final SpedizioneService service;
    public SpedizioneView(@Autowired SpedizioneService service) {

        this.service=service;

    }
    @PostConstruct
    public void init() {
        super.init(new Grid<>(Spedizione.class));
        configureGrid("destinazione","pesoStimato");
        setColumnCaption("invioSpedizione","Invio");
        getGrid().addColumn(new NumberRenderer<>(Spedizione::getSpesePostali, Smd.getEuroCurrency())).setHeader("Spese Postali");

        H2 header = new H2("Spedizioni di " + Mese.getMeseCorrente().getNomeBreve() + " " + Anno.getAnnoCorrente().getAnnoAsString());
        add(header, getContent(getGrid()));

        updateList();
    }

    @Override
    public List<Spedizione> filter() {
        return service.searchBy(getUserSession().getLoggedInIntestatario(), Anno.getAnnoCorrente(), Mese.getMeseCorrente());
    }
}
