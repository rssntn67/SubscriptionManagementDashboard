package it.arsinfo.smd.ui.vaadin;

import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;

import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.Invio;
import it.arsinfo.smd.data.InvioSpedizione;
import it.arsinfo.smd.data.TipoEstrattoConto;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.entity.EstrattoConto;
import it.arsinfo.smd.repository.EstrattoContoDao;

public class EstrattoContoSearch extends SmdSearch<EstrattoConto> {

    private Anagrafica intestatario;
    private Anagrafica destinatario;
    private Pubblicazione pubblicazione;
    private final ComboBox<Anno> filterAnno = new ComboBox<Anno>("Anno", EnumSet.allOf(Anno.class));
    private final ComboBox<TipoEstrattoConto> filterOmaggio = new ComboBox<TipoEstrattoConto>("Omaggio", EnumSet.allOf(TipoEstrattoConto.class));
    private final ComboBox<Invio> filterInvio = new ComboBox<Invio>("Invio", EnumSet.allOf(Invio.class));
    private final ComboBox<InvioSpedizione> filterInvioSped = new ComboBox<InvioSpedizione>("Sped.", EnumSet.allOf(InvioSpedizione.class));
    private final CheckBox filterSospesa = new CheckBox("Sospesa");
    private final CheckBox filterNonSospesa = new CheckBox("Attiva");
        
    public EstrattoContoSearch(EstrattoContoDao spedizioneDao,
            List<Anagrafica> anagrafica, List<Pubblicazione> pubblicazioni) {
        super(spedizioneDao);
        ComboBox<Anagrafica> filterIntestatario = new ComboBox<Anagrafica>();
        ComboBox<Anagrafica> filterDestinatario = new ComboBox<Anagrafica>();
        ComboBox<Pubblicazione> filterPubblicazione = new ComboBox<Pubblicazione>();

        setComponents(new HorizontalLayout(filterIntestatario,filterDestinatario,filterPubblicazione),
                      new HorizontalLayout(filterAnno,filterOmaggio,filterInvioSped,filterInvio),
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
        filterInvioSped.setPlaceholder("Seleziona Sped.");
        filterInvioSped.addSelectionListener(e ->onChange());
        filterSospesa.addValueChangeListener(e ->onChange());
        filterNonSospesa.addValueChangeListener(e ->onChange());

        

    }

    @Override
    public List<EstrattoConto> find() {
        if (destinatario == null && pubblicazione == null) {
            return filterAll(findAll());            
        }
        if (destinatario == null ) {
            return filterAll(((EstrattoContoDao) getRepo()).findByPubblicazione(pubblicazione));
        }
        if (pubblicazione == null) {
            return filterAll(((EstrattoContoDao) getRepo()).findByDestinatario(destinatario));
        }
        return filterAll(((EstrattoContoDao) getRepo()).findByDestinatarioAndPubblicazione(destinatario, pubblicazione));
    }

    private List<EstrattoConto> filterAll(List<EstrattoConto> spedizioni) {
        if (intestatario != null) {
            spedizioni = spedizioni.stream().filter( s -> s.getAbbonamento().getIntestatario().getId() == intestatario.getId()).collect(Collectors.toList());
        }
        if (filterAnno.getValue() != null) {
            spedizioni = spedizioni.stream().filter( s -> s.getAbbonamento().getAnno() == filterAnno.getValue()).collect(Collectors.toList());
        }
        if (filterOmaggio.getValue() != null) {
            spedizioni=spedizioni.stream().filter(s -> s.getTipoEstrattoConto() == filterOmaggio.getValue()).collect(Collectors.toList());      
        }
        if (filterInvio.getValue() != null) {
            spedizioni=spedizioni.stream().filter(s -> s.getInvio() == filterInvio.getValue()).collect(Collectors.toList());      
        }
        if (filterInvioSped.getValue() != null) {
            spedizioni=spedizioni.stream().filter(s -> s.getInvioSpedizione() == filterInvioSped.getValue()).collect(Collectors.toList());      
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
