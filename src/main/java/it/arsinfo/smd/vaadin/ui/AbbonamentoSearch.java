package it.arsinfo.smd.vaadin.ui;

import java.util.List;

import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;

import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.repository.AbbonamentoDao;
import it.arsinfo.smd.vaadin.model.SmdSearch;

public class AbbonamentoSearch extends SmdSearch<Abbonamento> {

    private Anagrafica customer;

    public AbbonamentoSearch(AbbonamentoDao abbonamentoDao,
            List<Anagrafica> anagrafica) {
        super(abbonamentoDao);

        ComboBox<Anagrafica> filterAnagrafica = new ComboBox<Anagrafica>();

        setComponents(new HorizontalLayout(filterAnagrafica));

        filterAnagrafica.setEmptySelectionAllowed(false);
        filterAnagrafica.setPlaceholder("Cerca per Cliente");
        filterAnagrafica.setItems(anagrafica);
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
    public List<Abbonamento> find() {
        if (customer != null) {
            return ((AbbonamentoDao) getRepo()).findByIntestatario(customer);
        }
        return findAll();
    }

}
