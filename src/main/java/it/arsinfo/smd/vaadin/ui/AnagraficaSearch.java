package it.arsinfo.smd.vaadin.ui;

import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.util.StringUtils;

import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;

import it.arsinfo.smd.data.Diocesi;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.repository.AnagraficaDao;
import it.arsinfo.smd.vaadin.model.SmdSearch;

public class AnagraficaSearch extends SmdSearch<Anagrafica> {

    private Diocesi searchDiocesi;
    private String searchCognome;

    public AnagraficaSearch(AnagraficaDao anagraficaDao) {
        super(anagraficaDao);
        TextField filterCognome = new TextField();
        ComboBox<Diocesi> filterDiocesi = new ComboBox<Diocesi>(null,
                                                                EnumSet.allOf(Diocesi.class));

        setComponents(new HorizontalLayout(filterDiocesi,
                                       filterCognome
                                       ));

        filterDiocesi.setEmptySelectionAllowed(true);
        filterDiocesi.setItemCaptionGenerator(Diocesi::getDetails);
        filterDiocesi.setPlaceholder("Cerca per Diocesi");

        filterDiocesi.addSelectionListener(e -> {
            if (e.getValue() == null) {
                searchDiocesi = null;
            } else {
                searchDiocesi = e.getSelectedItem().get();
            }
            onChange();
        });

        filterCognome.setPlaceholder("Cerca per Cognome");
        filterCognome.setValueChangeMode(ValueChangeMode.EAGER);
        filterCognome.addValueChangeListener(e -> {
            searchCognome = e.getValue();
            onChange();
        });

    }

    @Override
    public List<Anagrafica> find() {
        if (StringUtils.isEmpty(searchCognome) && searchDiocesi == null) {
            return findAll();
        } 
        if (searchDiocesi == null) {
            return ((AnagraficaDao)getRepo()).findByCognomeStartsWithIgnoreCase(searchCognome);
        } 
        if (StringUtils.isEmpty(searchCognome)) {
            return ((AnagraficaDao)getRepo()).findByDiocesi(searchDiocesi);
        } 
        
        return ((AnagraficaDao)getRepo()).findByCognomeStartsWithIgnoreCase(searchCognome).
                stream().
                filter(tizio -> tizio.getDiocesi().equals(searchDiocesi)).
                collect(Collectors.toList());
    }

}
