package it.arsinfo.smd.ui.vaadin;

import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;

import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.Invio;
import it.arsinfo.smd.data.Omaggio;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.entity.Spedizione;
import it.arsinfo.smd.repository.SpedizioneDao;

public class SpedizioneSearch extends SmdSearch<Spedizione> {

    private Anagrafica intestatario;
    private Anagrafica destinatario;
    private Pubblicazione pubblicazione;
    private final ComboBox<Anno> filterAnno = new ComboBox<Anno>("Anno", EnumSet.allOf(Anno.class));
    private final ComboBox<Omaggio> filterOmaggio = new ComboBox<Omaggio>("Omaggio", EnumSet.allOf(Omaggio.class));
    private final ComboBox<Invio> filterInvio = new ComboBox<Invio>("Invio", EnumSet.allOf(Invio.class));
    private final CheckBox filterSospesa = new CheckBox("Sospesa");
    private final CheckBox filterNonSospesa = new CheckBox("Attiva");
        
    public SpedizioneSearch(SpedizioneDao spedizioneDao,
            List<Anagrafica> anagrafica, List<Pubblicazione> pubblicazioni) {
        super(spedizioneDao);
        ComboBox<Anagrafica> filterIntestatario = new ComboBox<Anagrafica>();
        ComboBox<Anagrafica> filterDestinatario = new ComboBox<Anagrafica>();
        ComboBox<Pubblicazione> filterPubblicazione = new ComboBox<Pubblicazione>();

        setComponents(new HorizontalLayout(filterIntestatario,filterDestinatario,filterPubblicazione),
                      new HorizontalLayout(filterAnno,filterOmaggio,filterInvio),
                      new HorizontalLayout(filterSospesa,filterNonSospesa));

        filterIntestatario.setEmptySelectionAllowed(true);
        filterIntestatario.setPlaceholder("Cerca per Intestatario");
        filterIntestatario.setItems(anagrafica);
        filterIntestatario.setItemCaptionGenerator(Anagrafica::getCaption);
        filterIntestatario.addSelectionListener(e -> {
            if (e.getValue() == null) {
                intestatario = null;
            } else {
                intestatario = e.getSelectedItem().get();
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

        filterPubblicazione.setEmptySelectionAllowed(true);
        filterPubblicazione.setPlaceholder("Cerca per Pubblicazioni");
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

        filterAnno.setPlaceholder("Seleziona Anno");
        filterAnno.setItemCaptionGenerator(Anno::getAnnoAsString);
        filterAnno.addSelectionListener(e ->onChange());
        filterOmaggio.setPlaceholder("Seleziona Omaggio");
        filterOmaggio.addSelectionListener(e ->onChange());
        filterInvio.setPlaceholder("Seleziona Invio");
        filterInvio.addSelectionListener(e ->onChange());
        filterSospesa.addValueChangeListener(e ->onChange());
        filterNonSospesa.addValueChangeListener(e ->onChange());

        

    }

    @Override
    public List<Spedizione> find() {
        if (destinatario == null && pubblicazione == null) {
            return filterAll(findAll());            
        }
        if (destinatario == null ) {
            return filterAll(((SpedizioneDao) getRepo()).findByPubblicazione(pubblicazione));
        }
        if (pubblicazione == null) {
            return filterAll(((SpedizioneDao) getRepo()).findByDestinatario(destinatario));
        }
        return filterAll(((SpedizioneDao) getRepo()).findByDestinatarioAndPubblicazione(destinatario, pubblicazione));
    }

    private List<Spedizione> filterAll(List<Spedizione> spedizioni) {
        if (intestatario != null) {
            spedizioni = spedizioni.stream().filter( s -> s.getAbbonamento().getIntestatario().getId() == intestatario.getId()).collect(Collectors.toList());
        }
        if (filterAnno.getValue() != null) {
            spedizioni = spedizioni.stream().filter( s -> s.getAbbonamento().getAnno() == filterAnno.getValue()).collect(Collectors.toList());
        }
        if (filterOmaggio.getValue() != null) {
            spedizioni=spedizioni.stream().filter(s -> s.getOmaggio() == filterOmaggio.getValue()).collect(Collectors.toList());      
        }
        if (filterInvio.getValue() != null) {
            spedizioni=spedizioni.stream().filter(s -> s.getInvio() == filterInvio.getValue()).collect(Collectors.toList());      
        }
        if (filterSospesa.getValue()) {
            spedizioni=spedizioni.stream().filter(s -> s.isSospesa()).collect(Collectors.toList());      
        }
        if (filterNonSospesa.getValue()) {
            spedizioni=spedizioni.stream().filter(s -> !s.isSospesa()).collect(Collectors.toList());      
        }
        
        return spedizioni;
    }

}
