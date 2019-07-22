package it.arsinfo.smd.ui.vaadin;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;

import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.Invio;
import it.arsinfo.smd.data.InvioSpedizione;
import it.arsinfo.smd.data.Mese;
import it.arsinfo.smd.data.StatoSpedizione;
import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Spedizione;
import it.arsinfo.smd.repository.AbbonamentoDao;
import it.arsinfo.smd.repository.SpedizioneDao;

public class SpedizioneSearch extends SmdSearch<Spedizione> {

    private Anagrafica destinatario;
    private final ComboBox<Anno> filterAnno = new ComboBox<Anno>("Anno", EnumSet.allOf(Anno.class));
    private final ComboBox<Mese> filterMese = new ComboBox<Mese>("Mese", EnumSet.allOf(Mese.class));
    private final ComboBox<StatoSpedizione> filterStatoSpedizione = new ComboBox<StatoSpedizione>("Stato Sped", EnumSet.allOf(StatoSpedizione.class));
    private final ComboBox<Invio> filterInvio = new ComboBox<Invio>("Invio", EnumSet.allOf(Invio.class));
    private final ComboBox<InvioSpedizione> filterInvioSpedizione = new ComboBox<InvioSpedizione>("Invio Sped", EnumSet.allOf(InvioSpedizione.class));
            
    private final Map<Long,Abbonamento> abbMap = new HashMap<>(); 

    public SpedizioneSearch(SpedizioneDao spedizioneDao,
            List<Abbonamento> abbonamenti,
            List<Anagrafica> anagrafica) {
        super(spedizioneDao);
        abbonamenti.forEach( abb -> abbMap.put(abb.getId(),abb));

        ComboBox<Anagrafica> filterDestinatario = new ComboBox<Anagrafica>();


        setComponents(new HorizontalLayout(filterDestinatario,filterAnno,filterMese,filterInvio,filterStatoSpedizione,filterInvioSpedizione));

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


        filterAnno.setPlaceholder("Seleziona Anno");
        filterAnno.setItemCaptionGenerator(Anno::getAnnoAsString);
        filterAnno.addSelectionListener(e ->onChange());

        filterMese.setPlaceholder("Seleziona Mese");
        filterMese.setItemCaptionGenerator(Mese::getNomeBreve);
        filterMese.addSelectionListener(e ->onChange());

        filterInvioSpedizione.setPlaceholder("Seleziona Invio Sped");
        filterInvioSpedizione.addSelectionListener(e ->onChange());

        filterInvio.setPlaceholder("Seleziona Invio");
        filterInvio.addSelectionListener(e ->onChange());        

        filterStatoSpedizione.setPlaceholder("Seleziona Stato");
        filterStatoSpedizione.addSelectionListener(e ->onChange());        

    }

    @Override
    public List<Spedizione> find() {
        if (destinatario == null) {
            return filterAll(findAll());            
        }
        return filterAll(((SpedizioneDao) getRepo()).findByDestinatario(destinatario));
    }

    private List<Spedizione> filterAll(List<Spedizione> spedizioni) {
        if (filterAnno.getValue() != null) {
            spedizioni = spedizioni.stream().filter( s -> s.getAbbonamento().getAnno() == filterAnno.getValue()).collect(Collectors.toList());
        }
        if (filterMese.getValue() != null) {
            spedizioni=spedizioni.stream().filter(s -> s.getMeseSpedizione() == filterMese.getValue()).collect(Collectors.toList());      
        }
        if (filterInvio.getValue() != null) {
            spedizioni=spedizioni.stream().filter(s -> s.getInvio() == filterInvio.getValue()).collect(Collectors.toList());      
        }
        if (filterInvioSpedizione.getValue() != null) {
            spedizioni=spedizioni.stream().filter(s -> s.getInvioSpedizione() == filterInvioSpedizione.getValue()).collect(Collectors.toList());      
        }
        if (filterStatoSpedizione.getValue() != null) {
            spedizioni=spedizioni.stream().filter(s -> s.getStatoSpedizione() == filterStatoSpedizione.getValue()).collect(Collectors.toList());      
        }
        for (Spedizione spedizione: spedizioni) {
            spedizione.setAbbonamento(abbMap.get(spedizione.getAbbonamento().getId()));
        }
        return spedizioni;
    }

}
