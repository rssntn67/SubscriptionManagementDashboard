package it.arsinfo.smd.ui.nota;

import java.util.List;

import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;

import it.arsinfo.smd.entity.Nota;
import it.arsinfo.smd.entity.Storico;
import it.arsinfo.smd.service.dao.NotaServiceDao;
import it.arsinfo.smd.ui.vaadin.SmdSearch;

public class NotaSearch extends SmdSearch<Nota> {

    private String searchText;
    private Storico storico;
    private NotaServiceDao dao;

    public NotaSearch(NotaServiceDao dao, List<Storico> storici) {
        super(dao);
        this.dao=dao;
        TextField filter = new TextField();
        filter.setPlaceholder("Cerca per Descrizione");
        filter.setValueChangeMode(ValueChangeMode.EAGER);
        filter.addValueChangeListener(e -> {
            searchText = e.getValue();
            onChange();
        });
        ComboBox<Storico> filterStorico = new ComboBox<Storico>();
        filterStorico.setPlaceholder("Cerca per Storico");
        filterStorico.setItems(storici);
        filterStorico.setItemCaptionGenerator(Storico::getCaption);

        filterStorico.addSelectionListener(e -> {
            if (e.getValue() == null) {
                storico = null;
            } else {
                storico = e.getSelectedItem().get();
            }
            onChange();
        });
        
        HorizontalLayout layout = new HorizontalLayout();
        layout.addComponent(filter);
        layout.addComponentsAndExpand(filterStorico);

        setComponents(layout);
    }

    @Override
    public List<Nota> find() {
    	return dao.searchBy(searchText, storico);
    }

}
