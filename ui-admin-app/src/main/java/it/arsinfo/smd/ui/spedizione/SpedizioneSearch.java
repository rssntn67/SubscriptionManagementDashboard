package it.arsinfo.smd.ui.spedizione;

import java.util.EnumSet;
import java.util.List;

import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;

import it.arsinfo.smd.service.api.SpedizioneService;
import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.InvioSpedizione;
import it.arsinfo.smd.data.Mese;
import it.arsinfo.smd.data.StatoSpedizione;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.entity.Spedizione;
import it.arsinfo.smd.ui.vaadin.SmdSearch;

public class SpedizioneSearch extends SmdSearch<Spedizione> {

    private Anagrafica destinatario;
    private Pubblicazione pubblicazione;

    private final ComboBox<Anno> filterAnno = new ComboBox<Anno>();
    private final ComboBox<Mese> filterMese = new ComboBox<Mese>();
    private final ComboBox<StatoSpedizione> filterStatoSpedizione = new ComboBox<StatoSpedizione>();
    private final ComboBox<InvioSpedizione> filterInvioSpedizione = new ComboBox<InvioSpedizione>();
    
    private final SpedizioneService dao;
    public SpedizioneSearch(SpedizioneService dao,
                            List<Anagrafica> anagrafica,
                            List<Pubblicazione> pubblicazioni) {
        super(dao);
        this.dao=dao;
        ComboBox<Anagrafica> filterDestinatario = new ComboBox<Anagrafica>();
        ComboBox<Pubblicazione> filterPubblicazione = new ComboBox<Pubblicazione>();

        HorizontalLayout anag = new HorizontalLayout(filterPubblicazione);
        anag.addComponentsAndExpand(filterDestinatario);

        HorizontalLayout tipo = new HorizontalLayout(filterStatoSpedizione,filterAnno,filterMese);
        tipo.addComponentsAndExpand(filterInvioSpedizione);
        setComponents(anag,tipo);

        filterPubblicazione.setEmptySelectionAllowed(true);
        filterPubblicazione.setPlaceholder("Cerca per Pubblicazione");
        filterPubblicazione.setItems(pubblicazioni);
        filterPubblicazione.setItemCaptionGenerator(Pubblicazione::getNome);
        filterPubblicazione.addSelectionListener(e -> {
            if (e.getValue() == null) {
                pubblicazione = null;
            } else {
                pubblicazione = e.getSelectedItem().get();
            }
            onChange();
        });

        filterDestinatario.setEmptySelectionAllowed(true);
        filterDestinatario.setPlaceholder("Cerca per Destinatario");
        filterDestinatario.setItems(anagrafica);
        filterDestinatario.setItemCaptionGenerator(Anagrafica::getCaption);
        filterDestinatario.addSelectionListener(e -> {
            if (e.getValue() == null) {
                destinatario = null;
            } else {
                destinatario = e.getSelectedItem().get();
            }
            onChange();
        });


        filterAnno.setPlaceholder("Cerca per Anno");
        filterAnno.setItems(EnumSet.allOf(Anno.class));
        filterAnno.setItemCaptionGenerator(Anno::getAnnoAsString);
        filterAnno.addSelectionListener(e ->onChange());

        filterMese.setPlaceholder("Cerca per Mese");
        filterMese.setItems(EnumSet.allOf(Mese.class));
        filterMese.setItemCaptionGenerator(Mese::getNomeBreve);
        filterMese.addSelectionListener(e ->onChange());

        filterInvioSpedizione.setPlaceholder("Cerca per Invio");
        filterInvioSpedizione.setItems(EnumSet.allOf(InvioSpedizione.class));
        filterInvioSpedizione.addSelectionListener(e ->onChange());

        filterStatoSpedizione.setPlaceholder("Cerca per Stato");
        filterStatoSpedizione.setItems(EnumSet.allOf(StatoSpedizione.class));
        filterStatoSpedizione.addSelectionListener(e ->onChange());        

    }

    @Override
    public List<Spedizione> find() {
    	return dao.searchBy(
    			destinatario,
    			filterAnno.getValue(),
    			filterMese.getValue(),
    			filterInvioSpedizione.getValue(),
    			pubblicazione, 
    			filterStatoSpedizione.getValue());
    }

}
