package it.arsinfo.smd.ui.vaadin;

import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;

import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.TipoEstrattoConto;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.EstrattoConto;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.repository.EstrattoContoDao;

public class EstrattoContoSearch extends SmdSearch<EstrattoConto> {

    private Anagrafica intestatario;
    private Pubblicazione pubblicazione;
    private final ComboBox<Anno> filterAnno = new ComboBox<Anno>("Anno", EnumSet.allOf(Anno.class));
    private final ComboBox<TipoEstrattoConto> filterTipoEstrattoConto = new ComboBox<TipoEstrattoConto>("Tipo Estratto Conto", EnumSet.allOf(TipoEstrattoConto.class));
            
    public EstrattoContoSearch(EstrattoContoDao spedizioneDao,
            List<Anagrafica> anagrafica, List<Pubblicazione> pubblicazioni) {
        super(spedizioneDao);
        ComboBox<Anagrafica> filterIntestatario = new ComboBox<Anagrafica>();
        ComboBox<Pubblicazione> filterPubblicazione = new ComboBox<Pubblicazione>();

        setComponents(new HorizontalLayout(filterIntestatario,filterPubblicazione,filterAnno,filterTipoEstrattoConto));

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
        filterTipoEstrattoConto.setPlaceholder("Seleziona Omaggio");
        filterTipoEstrattoConto.addSelectionListener(e ->onChange());

    }

    @Override
    public List<EstrattoConto> find() {
        if (pubblicazione != null) {
            return filterAll(((EstrattoContoDao) getRepo()).findByPubblicazione(pubblicazione));
        }
        return filterAll(((EstrattoContoDao) getRepo()).findAll());
    }

    private List<EstrattoConto> filterAll(List<EstrattoConto> estrattiConto) {
        if (intestatario != null) {
            estrattiConto = estrattiConto.stream().filter( s -> s.getAbbonamento().getIntestatario().getId() == intestatario.getId()).collect(Collectors.toList());
        }
        if (filterAnno.getValue() != null) {
            estrattiConto = estrattiConto.stream().filter( s -> s.getAbbonamento().getAnno() == filterAnno.getValue()).collect(Collectors.toList());
        }
        if (filterTipoEstrattoConto.getValue() != null) {
            estrattiConto=estrattiConto.stream().filter(s -> s.getTipoEstrattoConto() == filterTipoEstrattoConto.getValue()).collect(Collectors.toList());      
        }
        
        return estrattiConto;
    }

}
