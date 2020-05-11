package it.arsinfo.smd.ui.storico;

import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;

import it.arsinfo.smd.dao.repository.StoricoDao;
import it.arsinfo.smd.data.Cassa;
import it.arsinfo.smd.data.Invio;
import it.arsinfo.smd.data.InvioSpedizione;
import it.arsinfo.smd.data.TipoEstrattoConto;
import it.arsinfo.smd.data.StatoStorico;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.entity.Storico;
import it.arsinfo.smd.ui.vaadin.SmdSearch;

public class StoricoSearch extends SmdSearch<Storico> {

    private Anagrafica intestatario;
    private Anagrafica destinatario;
    private Pubblicazione pubblicazione;
    private final ComboBox<TipoEstrattoConto> filterTipoEstrattoConto = new ComboBox<TipoEstrattoConto>();
    private final ComboBox<Cassa> filterCassa = new ComboBox<Cassa>();
    private final ComboBox<Invio> filterInvio = new ComboBox<Invio>();
    private final ComboBox<InvioSpedizione> filterInvioSped = new ComboBox<InvioSpedizione>();
    private final ComboBox<StatoStorico> filterStatoStorico = new ComboBox<StatoStorico>();

    public StoricoSearch(StoricoDao storicoDao,
            List<Anagrafica> anagrafica, List<Pubblicazione> pubblicazioni) {
        super(storicoDao);

        ComboBox<Anagrafica> filterIntestatario = new ComboBox<Anagrafica>();
        ComboBox<Anagrafica> filterDestinatario = new ComboBox<Anagrafica>();
        ComboBox<Pubblicazione> filterPubblicazione = new ComboBox<Pubblicazione>();

        HorizontalLayout anagr = new HorizontalLayout(filterPubblicazione);
        anagr.addComponentsAndExpand(filterIntestatario,filterDestinatario);
        HorizontalLayout stat = new HorizontalLayout(filterCassa,filterStatoStorico,filterInvioSped,filterInvio);
        stat.addComponentsAndExpand(filterTipoEstrattoConto);
        setComponents(anagr,stat);

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

        filterTipoEstrattoConto.setPlaceholder("Seleziona Tipo EC");
        filterTipoEstrattoConto.addSelectionListener(e ->onChange());
        filterTipoEstrattoConto.setItems(EnumSet.allOf(TipoEstrattoConto.class));

        filterCassa.setPlaceholder("Seleziona Cassa");
        filterCassa.addSelectionListener(e ->onChange());
        filterCassa.setItems(EnumSet.allOf(Cassa.class));

        filterInvio.setPlaceholder("Seleziona Invio");
        filterInvio.addSelectionListener(e ->onChange());
        filterInvio.setItems(EnumSet.allOf(Invio.class));
        
        filterInvioSped.setPlaceholder("Seleziona Sped");
        filterInvioSped.addSelectionListener(e ->onChange());
        filterInvioSped.setItems(EnumSet.allOf(InvioSpedizione.class));

        filterStatoStorico.setPlaceholder("Seleziona Stato");
        filterStatoStorico.addSelectionListener(e ->onChange());
        filterStatoStorico.setItems(EnumSet.allOf(StatoStorico.class));

        

    }

    @Override
    public List<Storico> find() {
        if (destinatario == null && intestatario == null && pubblicazione == null) {
            return filterAll(findAll());            
        }
        if (destinatario == null && intestatario == null) {
            return filterAll(((StoricoDao) getRepo()).findByPubblicazione(pubblicazione));
        }
        if (destinatario == null && pubblicazione == null) {
            return filterAll(((StoricoDao) getRepo()).findByIntestatario(intestatario));
        }
        if (intestatario == null && pubblicazione == null) {
            return filterAll(((StoricoDao) getRepo()).findByDestinatario(destinatario));
        }
        if (pubblicazione == null) {
            return filterAll(((StoricoDao) getRepo()).findByIntestatarioAndDestinatario(intestatario,destinatario));
        }
        if (intestatario == null) {
            return filterAll(((StoricoDao) getRepo()).findByDestinatarioAndPubblicazione(destinatario,pubblicazione));
        }
        if (destinatario == null ) {
            return filterAll(((StoricoDao) getRepo()).findByIntestatarioAndPubblicazione(intestatario, pubblicazione));
        }
        return filterAll(((StoricoDao) getRepo()).findByIntestatarioAndDestinatarioAndPubblicazione(intestatario, destinatario, pubblicazione));
    }

    private List<Storico> filterAll(List<Storico> storici) {
        if (filterTipoEstrattoConto.getValue() != null) {
            storici=storici.stream().filter(s -> s.getTipoEstrattoConto() == filterTipoEstrattoConto.getValue()).collect(Collectors.toList());      
        }
        if (filterCassa.getValue() != null) {
            storici=storici.stream().filter(s -> s.getCassa() == filterCassa.getValue()).collect(Collectors.toList());      
        }
        if (filterInvio.getValue() != null) {
            storici=storici.stream().filter(s -> s.getInvio() == filterInvio.getValue()).collect(Collectors.toList());      
        }
        if (filterInvioSped.getValue() != null) {
            storici=storici.stream().filter(s -> s.getInvioSpedizione() == filterInvioSped.getValue()).collect(Collectors.toList());      
        }
        if (filterStatoStorico.getValue() != null) {
            storici=storici.stream().filter(s -> s.getStatoStorico() == filterStatoStorico.getValue()).collect(Collectors.toList());      
        }
        
        return storici;
    }

}
