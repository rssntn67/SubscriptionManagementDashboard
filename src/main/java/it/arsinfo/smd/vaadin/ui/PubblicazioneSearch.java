package it.arsinfo.smd.vaadin.ui;

import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.util.StringUtils;

import com.vaadin.annotations.Title;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;

import it.arsinfo.smd.data.TipoPubblicazione;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.repository.PubblicazioneDao;
import it.arsinfo.smd.vaadin.model.SmdSearch;
import it.arsinfo.smd.vaadin.model.SmdUIHelper;

@SpringUI(path = SmdUIHelper.URL_PUBBLICAZIONI)
@Title("Anagrafica Pubblicazioni ADP")
public class PubblicazioneSearch extends SmdSearch<Pubblicazione> {

    /**
     * 
     */
    private static final long serialVersionUID = 7884064928998716106L;

    PubblicazioneDao pubblicazioneDao;

    private String searchNome;
    private TipoPubblicazione searchTipo;

    public PubblicazioneSearch(PubblicazioneDao pubblicazioneDao) {
        super(new Grid<>(Pubblicazione.class));
        this.pubblicazioneDao = pubblicazioneDao;
        TextField filterNome = new TextField();

        ComboBox<TipoPubblicazione> filterTipo = new ComboBox<TipoPubblicazione>(null,
                                                                                 EnumSet.allOf(TipoPubblicazione.class));
        HorizontalLayout actions = new HorizontalLayout(filterTipo,
                                                        filterNome);
        addComponents(actions, getGrid());

        filterTipo.setEmptySelectionAllowed(false);
        filterTipo.setPlaceholder("Cerca per Tipo");

        filterNome.setPlaceholder("Cerca per Nome");

        setColumns("id", "nome", "tipo", "costoUnitario", "costoScontato",
                   "primaPubblicazione");

        filterTipo.addSelectionListener(e -> {
            if (e.getValue() == null) {
                searchTipo = null;
            } else {
                searchTipo = e.getSelectedItem().get();
            }
            onSearch();
            ;
        });

        filterNome.setValueChangeMode(ValueChangeMode.EAGER);
        filterNome.addValueChangeListener(e -> {
            searchNome = e.getValue();
            onSearch();
        });

        onSearch();

    }

    @Override
    public Pubblicazione generate() {
        return new Pubblicazione();
    }

    @Override
    public List<Pubblicazione> search() {
        if (StringUtils.isEmpty(searchNome) && searchTipo == null) {
            return pubblicazioneDao.findAll();
        }

        if (searchTipo == null) {
            return pubblicazioneDao.findByNomeStartsWithIgnoreCase(searchNome);
        }
        if (StringUtils.isEmpty(searchNome)) {
            return pubblicazioneDao.findByTipo(searchTipo);
        }
        return pubblicazioneDao.findByNomeStartsWithIgnoreCase(searchNome).stream().filter(p -> p.getTipo().equals(searchTipo)).collect(Collectors.toList());
    }

}
