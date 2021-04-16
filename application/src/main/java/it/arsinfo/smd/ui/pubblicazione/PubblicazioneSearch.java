package it.arsinfo.smd.ui.pubblicazione;

import java.util.EnumSet;
import java.util.List;

import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;

import it.arsinfo.smd.service.api.PubblicazioneService;
import it.arsinfo.smd.data.TipoPubblicazione;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.ui.vaadin.SmdSearch;

public class PubblicazioneSearch extends SmdSearch<Pubblicazione> {

    private String searchNome;
    private TipoPubblicazione tipoPubblicazione;
    
    private final PubblicazioneService dao;

    public PubblicazioneSearch(PubblicazioneService dao) {
        super(dao);
        this.dao=dao;
        TextField filterNome = new TextField();

        ComboBox<TipoPubblicazione> filterTipo = new ComboBox<TipoPubblicazione>(null,
                                                                                 EnumSet.allOf(TipoPubblicazione.class));
        setComponents(new HorizontalLayout(filterTipo,
                                           filterNome));

        filterTipo.setEmptySelectionAllowed(true);
        filterTipo.setPlaceholder("Cerca per Tipo");

        filterNome.setPlaceholder("Cerca per Nome");

        filterTipo.addSelectionListener(e -> {
            if (e.getValue() == null) {
                tipoPubblicazione = null;
            } else {
                tipoPubblicazione = e.getSelectedItem().get();
            }
            onChange();
        });

        filterNome.setValueChangeMode(ValueChangeMode.EAGER);
        filterNome.addValueChangeListener(e -> {
            searchNome = e.getValue();
            onChange();
        });

    }

    @Override
    public List<Pubblicazione> find() {
    	return dao.searchBy(searchNome, tipoPubblicazione);
    }

}
