package it.arsinfo.smd.ui.spedizione;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.renderer.NumberRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import it.arsinfo.smd.dto.Indirizzo;
import it.arsinfo.smd.entity.*;
import it.arsinfo.smd.service.api.SpedizioneService;
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
    private final IndirizzoForm form = new IndirizzoForm(new BeanValidationBinder<>(Indirizzo.class));

    public SpedizioneView(@Autowired SpedizioneService service) {
        this.service=service;
    }
    @PostConstruct
    public void init() {
        super.init(new Grid<>(SpedizioneItem.class));
        configureGrid("statoSpedizione","numero");
        setColumnCaption("pubbCaption","Pubblicazione");
        setColumnCaption("spedizione.destinazione","Destinatario");
        setColumnCaption("spedizione.pesoStimato","Peso");
        setColumnCaption("spedizione.invioSpedizione","Invio");
        getGrid().addColumn(new NumberRenderer<>(item-> item.getSpedizione().getSpesePostali(), SmdEntity.getEuroCurrency())).setHeader("Spese Postali");


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
