package it.arsinfo.smd.ui.vaadin;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.util.StringUtils;

import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;

import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.Mese;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.entity.SpedizioneItem;
import it.arsinfo.smd.repository.SpedizioneItemDao;

public class SpedizioneItemSearch extends SmdSearch<SpedizioneItem> {

    private Pubblicazione pubblicazione;
    private final ComboBox<Anno> filterAnno = new ComboBox<Anno>("Anno", EnumSet.allOf(Anno.class));
    private final ComboBox<Mese> filterMese = new ComboBox<Mese>("Mese", EnumSet.allOf(Mese.class));
    private String numero;
        
    public SpedizioneItemSearch(SpedizioneItemDao spedizioneItemDao, List<Pubblicazione> pubblicazioni) {
        super(spedizioneItemDao);
        ComboBox<Pubblicazione> filterPubblicazione = new ComboBox<Pubblicazione>();

        TextField filterNumero = new TextField("Inserire Quantità");
        filterNumero.setValueChangeMode(ValueChangeMode.LAZY);

        setComponents(new HorizontalLayout(filterPubblicazione,filterAnno,filterMese,filterNumero));

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

        filterMese.setPlaceholder("Seleziona Mese");
        filterMese.setItemCaptionGenerator(Mese::getNomeBreve);
        filterMese.addSelectionListener(e ->onChange());

        filterNumero.addValueChangeListener(e -> {
            numero = e.getValue();
            onChange();
        });

    }

    @Override
    public List<SpedizioneItem> find() {
        if (StringUtils.isEmpty(numero) && pubblicazione == null) {
            return filterAll(findAll());            
        }
        if (!StringUtils.isEmpty(numero)) {
            try {
                Integer.parseInt(numero);
            } catch (NumberFormatException e) {
                return new ArrayList<>();
            }
        }
        if ( StringUtils.isEmpty(numero) ) {
            return filterAll(((SpedizioneItemDao) getRepo()).findByPubblicazione(pubblicazione));
        }
        if (pubblicazione == null) {
            return filterAll(((SpedizioneItemDao) getRepo()).findByNumero(Integer.parseInt(numero)));
        }
        return filterAll(((SpedizioneItemDao) getRepo()).findByPubblicazioneAndNumero(pubblicazione, Integer.parseInt(numero)));
    }

    private List<SpedizioneItem> filterAll(List<SpedizioneItem> spedizioni) {
        if (filterAnno.getValue() != null) {
            spedizioni = spedizioni.stream().filter( s -> s.getAnnoPubblicazione() == filterAnno.getValue()).collect(Collectors.toList());
        }
        if (filterMese.getValue() != null) {
            spedizioni = spedizioni.stream().filter( s -> s.getMesePubblicazione() == filterMese.getValue()).collect(Collectors.toList());
        }        
        return spedizioni;
    }

}
