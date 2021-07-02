package it.arsinfo.smd.ui.spedizione;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.renderer.NumberRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import it.arsinfo.smd.dao.AnagraficaDao;
import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.Mese;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Spedizione;
import it.arsinfo.smd.entity.SpedizioneItem;
import it.arsinfo.smd.service.Smd;
import it.arsinfo.smd.service.api.SpedizioneService;
import it.arsinfo.smd.service.dto.Indirizzo;
import it.arsinfo.smd.ui.MainLayout;
import it.arsinfo.smd.ui.entity.EntityGridView;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Route(value="adp/spedizione", layout = MainLayout.class)
@PageTitle("Spedizione | ADP Portale")
public class SpedizioneView extends EntityGridView<SpedizioneItem> {

    private final SpedizioneService service;
    private final AnagraficaDao dao;
    private final IndirizzoForm form = new IndirizzoForm(new BeanValidationBinder<>(Indirizzo.class));

    public SpedizioneView(@Autowired SpedizioneService service, @Autowired AnagraficaDao anagraficaDao) {
        this.service=service;
        this.dao = anagraficaDao;

    }
    @PostConstruct
    public void init() {
        super.init(new Grid<>(SpedizioneItem.class));
        configureGrid("statoSpedizione","numero");
        setColumnCaption("pubbCaption","Pubblicazione");
        setColumnCaption("spedizione.destinazione","Destinatario");
        setColumnCaption("spedizione.pesoStimato","Peso");
        setColumnCaption("spedizione.invioSpedizione","Invio");
        getGrid().addColumn(new NumberRenderer<>(item-> item.getSpedizione().getSpesePostali(), Smd.getEuroCurrency())).setHeader("Spese Postali");


        getGrid().asSingleSelect().addValueChangeListener(event ->
                edit(event.getValue()));
        form.addListener(IndirizzoForm.CloseEvent.class, e -> closeEditor());

        H2 header = new H2("Spedizioni di " + Mese.getMeseCorrente().getNomeBreve() + " " + Anno.getAnnoCorrente().getAnnoAsString());
        Div help = new Div();
        help.setText("Seleziona spedizione per dettaglio indirizzo");
        add(header, getContent(getGrid()),getContent(help),getContent(form));
        form.setVisible(false);
        updateList();
    }

    public void edit(SpedizioneItem entity) {
        Anagrafica destinatario = entity.getSpedizione().getDestinatario();
        Indirizzo indirizzo = Indirizzo.getIndirizzo(destinatario);
        if (destinatario.getCo() != null ) {
            Anagrafica co = dao.findById(destinatario.getCo().getId()).orElse(null);
            Indirizzo.getIndirizzo(destinatario,co);
        }
        form.setEntity(indirizzo);
        form.setVisible(true);
        addClassName("editing");
    }

    public void closeEditor() {
        form.setEntity(null);
        form.setVisible(false);
        removeClassName("editing");
    }

    @Override
    public List<SpedizioneItem> filter() {
        List<SpedizioneItem> items = new ArrayList<>();
        for (Spedizione s: service.searchBy(getUserSession().getLoggedInIntestatario(), Anno.getAnnoCorrente(), Mese.getMeseCorrente())) {
            for (SpedizioneItem item: service.getItems(s)) {
                item.setSpedizione(s);
                items.add(item);
            }
        }
        return items;
    }
}
