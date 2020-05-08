package it.arsinfo.smd.ui.pubblicazione;

import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.util.StringUtils;

import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;

import it.arsinfo.smd.dao.repository.PubblicazioneDao;
import it.arsinfo.smd.data.TipoPubblicazione;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.ui.vaadin.SmdSearch;

public class PubblicazioneSearch extends SmdSearch<Pubblicazione> {

    private String searchNome;
    private TipoPubblicazione searchTipo;

    public PubblicazioneSearch(PubblicazioneDao pubblicazioneDao) {
        super(pubblicazioneDao);
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
                searchTipo = null;
            } else {
                searchTipo = e.getSelectedItem().get();
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
        if (StringUtils.isEmpty(searchNome) && searchTipo == null) {
            return findAll();
        }

        if (searchTipo == null) {
            return ((PubblicazioneDao)getRepo()).findByNomeStartsWithIgnoreCase(searchNome);
        }
        if (StringUtils.isEmpty(searchNome)) {
            return ((PubblicazioneDao)getRepo()).findByTipo(searchTipo);
        }
        return ((PubblicazioneDao)getRepo()).findByNomeStartsWithIgnoreCase(searchNome).stream().filter(p -> p.getTipo().equals(searchTipo)).collect(Collectors.toList());
    }

}