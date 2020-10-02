package it.arsinfo.smd.ui.spedizione;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;

import it.arsinfo.smd.dao.SpedizioneServiceDao;
import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.InvioSpedizione;
import it.arsinfo.smd.data.Mese;
import it.arsinfo.smd.data.StatoSpedizione;
import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.entity.Spedizione;
import it.arsinfo.smd.ui.vaadin.SmdSearch;

public class SpedizioneSearch extends SmdSearch<Spedizione> {

    private Anagrafica a;
    private Pubblicazione p;

    private final ComboBox<Anno> filterAnno = new ComboBox<Anno>();
    private final ComboBox<Mese> filterMese = new ComboBox<Mese>();
    private final ComboBox<StatoSpedizione> filterStatoSpedizione = new ComboBox<StatoSpedizione>();
    private final ComboBox<InvioSpedizione> filterInvioSpedizione = new ComboBox<InvioSpedizione>();
            
    private final Map<Long,Abbonamento> abbMap;
    
    private final SpedizioneServiceDao dao;
    public SpedizioneSearch(SpedizioneServiceDao dao,
            List<Abbonamento> abbonamenti,
            List<Anagrafica> anagrafica,
            List<Pubblicazione> pubblicazioni) {
        super(dao);
        this.dao=dao;
        abbMap = abbonamenti.stream().collect(Collectors.toMap(Abbonamento::getId, Function.identity()));
        ComboBox<Anagrafica> filterDestinatario = new ComboBox<Anagrafica>();
        ComboBox<Pubblicazione> filterPubblicazione = new ComboBox<Pubblicazione>();

        HorizontalLayout anag = new HorizontalLayout(filterPubblicazione);
        anag.addComponentsAndExpand(filterDestinatario);

        HorizontalLayout tipo = new HorizontalLayout(filterStatoSpedizione,filterAnno,filterMese);
        tipo.addComponentsAndExpand(filterInvioSpedizione);
        setComponents(anag,tipo);

        filterPubblicazione.setEmptySelectionAllowed(true);
        filterPubblicazione.setPlaceholder("Cerca per Pubblicazioni");
        filterPubblicazione.setItems(pubblicazioni);
        filterPubblicazione.setItemCaptionGenerator(Pubblicazione::getNome);
        filterPubblicazione.addSelectionListener(e -> {
            if (e.getValue() == null) {
                p = null;
            } else {
                p = e.getSelectedItem().get();
            }
            onChange();
        });

        filterDestinatario.setEmptySelectionAllowed(true);
        filterDestinatario.setPlaceholder("Cerca per Anagrafica");
        filterDestinatario.setItems(anagrafica);
        filterDestinatario.setItemCaptionGenerator(Anagrafica::getCaption);
        filterDestinatario.addSelectionListener(e -> {
            if (e.getValue() == null) {
                a = null;
            } else {
                a = e.getSelectedItem().get();
            }
            onChange();
        });


        filterAnno.setPlaceholder("Seleziona Anno");
        filterAnno.setItems(EnumSet.allOf(Anno.class));
        filterAnno.setItemCaptionGenerator(Anno::getAnnoAsString);
        filterAnno.addSelectionListener(e ->onChange());

        filterMese.setPlaceholder("Seleziona Mese");
        filterMese.setItems(EnumSet.allOf(Mese.class));
        filterMese.setItemCaptionGenerator(Mese::getNomeBreve);
        filterMese.addSelectionListener(e ->onChange());

        filterInvioSpedizione.setPlaceholder("Seleziona Invio Sped");
        filterInvioSpedizione.setItems(EnumSet.allOf(InvioSpedizione.class));
        filterInvioSpedizione.addSelectionListener(e ->onChange());

        filterStatoSpedizione.setPlaceholder("Seleziona Stato");
        filterStatoSpedizione.setItems(EnumSet.allOf(StatoSpedizione.class));
        filterStatoSpedizione.addSelectionListener(e ->onChange());        

    }

    @Override
    public List<Spedizione> find() {
    	return filterAll(dao.searchBy(p, a,filterStatoSpedizione.getValue()));
    }

    private List<Spedizione> filterAll(List<Spedizione> spedizioni) {
        if (filterAnno.getValue() != null) {
            spedizioni = spedizioni.stream().filter( s -> s.getAnnoSpedizione() == filterAnno.getValue()).collect(Collectors.toList());
        }
        if (filterMese.getValue() != null) {
            spedizioni=spedizioni.stream().filter(s -> s.getMeseSpedizione() == filterMese.getValue()).collect(Collectors.toList());      
        }
        if (filterInvioSpedizione.getValue() != null) {
            spedizioni=spedizioni.stream().filter(s -> s.getInvioSpedizione() == filterInvioSpedizione.getValue()).collect(Collectors.toList());      
        }
        for (Spedizione sped: spedizioni) {
            sped.setAbbonamento(abbMap.get(sped.getAbbonamento().getId()));
        }
        return spedizioni;
    }

}
