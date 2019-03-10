package it.arsinfo.smd.vaadin.ui;

import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.util.StringUtils;

import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;

import it.arsinfo.smd.data.Diocesi;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.repository.AnagraficaDao;
import it.arsinfo.smd.vaadin.model.SmdSearch;

public class AnagraficaSearch extends SmdSearch<Anagrafica> {

    /**
     * 
     */
    private static final long serialVersionUID = 7884064928998716106L;

    private Diocesi searchDiocesi;
    private String searchCognome;

    private AnagraficaDao anagraficaDao;

    public AnagraficaSearch(AnagraficaDao anagraficaDao) {
        super(new Grid<>(Anagrafica.class));
        this.anagraficaDao = anagraficaDao;
        TextField filterCognome = new TextField();
        ComboBox<Diocesi> filterDiocesi = new ComboBox<Diocesi>(null,
                                                                EnumSet.allOf(Diocesi.class));

        HorizontalLayout actions = new HorizontalLayout(filterDiocesi,
                                                        filterCognome,
                                                        getAdd());
        addComponents(actions, getGrid());

        filterDiocesi.setEmptySelectionAllowed(false);
        filterDiocesi.setPlaceholder("Cerca per Diocesi");
        filterDiocesi.setItemCaptionGenerator(Diocesi::getDetails);

        filterCognome.setPlaceholder("Cerca per Cognome");

        setColumns("id", "nome", "cognome", "diocesi.details", "indirizzo",
                   "citta", "cap", "paese", "email", "telefono", "inRegola");
        getGrid().getColumn("diocesi.details").setCaption("Diocesi");
        getGrid().getColumn("id").setMaximumWidth(100);

        filterDiocesi.setEmptySelectionAllowed(true);
        filterDiocesi.setItemCaptionGenerator(Diocesi::getDetails);

        filterDiocesi.addSelectionListener(e -> {
            if (e.getValue() == null) {
                searchDiocesi = null;
            } else {
                searchDiocesi = e.getSelectedItem().get();
            }
            onSearch();
        });

        filterCognome.setValueChangeMode(ValueChangeMode.EAGER);
        filterCognome.addValueChangeListener(e -> {
            searchCognome = e.getValue();
            onSearch();
        });
        onSearch();

    }

    @Override
    public List<Anagrafica> search() {
        if (StringUtils.isEmpty(searchCognome) && searchDiocesi == null) {
            return anagraficaDao.findAll();
        } 
        if (searchDiocesi == null) {
            return anagraficaDao.findByCognomeStartsWithIgnoreCase(searchCognome);
        } 
        if (StringUtils.isEmpty(searchCognome)) {
            return anagraficaDao.findByDiocesi(searchDiocesi);
        } 
        
        return anagraficaDao.findByCognomeStartsWithIgnoreCase(searchCognome).
                stream().
                filter(tizio -> tizio.getDiocesi().equals(searchDiocesi)).
                collect(Collectors.toList());
    }

    @Override
    public Anagrafica generate() {
        return new Anagrafica();
    }

}
