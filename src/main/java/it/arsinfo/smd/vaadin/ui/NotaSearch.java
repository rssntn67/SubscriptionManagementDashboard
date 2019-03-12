package it.arsinfo.smd.vaadin.ui;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.util.StringUtils;

import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;

import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Nota;
import it.arsinfo.smd.repository.AnagraficaDao;
import it.arsinfo.smd.repository.NotaDao;
import it.arsinfo.smd.vaadin.model.SmdSearch;

public class NotaSearch extends SmdSearch<Nota> {

    private String searchText;
    private Anagrafica customer;

    public NotaSearch(NotaDao notaDao, AnagraficaDao anagraficaDao) {
        super(notaDao);
        TextField filter = new TextField();
        ComboBox<Anagrafica> filterAnagrafica = new ComboBox<Anagrafica>("Selezionare Cliente");

        setComponents(new HorizontalLayout(filterAnagrafica, filter));

        filter.setPlaceholder("Cerca per Descrizione");
        filter.setValueChangeMode(ValueChangeMode.EAGER);
        filter.addValueChangeListener(e -> {
            searchText = e.getValue();
            onChange();
        });

        filterAnagrafica.setEmptySelectionAllowed(false);
        filterAnagrafica.setPlaceholder("Cerca per Cliente");
        filterAnagrafica.setItems(anagraficaDao.findAll());
        filterAnagrafica.setItemCaptionGenerator(Anagrafica::getCaption);

        filterAnagrafica.addSelectionListener(e -> {
            if (e.getValue() == null) {
                customer = null;
            } else {
                customer = e.getSelectedItem().get();
            }
            onChange();
        });

    }

    @Override
    public List<Nota> find() {
        if (StringUtils.isEmpty(searchText) && customer == null) {
            return findAll();
        }
        if (StringUtils.isEmpty(searchText)) {
            return ((NotaDao) getRepo()).findByAnagrafica(customer);
        }
        if (customer == null) {
            ((NotaDao) getRepo()).findByDescriptionStartsWithIgnoreCase(searchText);
        }
        return ((NotaDao) getRepo()).findByDescriptionStartsWithIgnoreCase(searchText).stream().filter(n -> n.getAnagrafica().getId() == customer.getId()).collect(Collectors.toList());
    }

}
