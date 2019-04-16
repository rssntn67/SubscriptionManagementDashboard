package it.arsinfo.smd.ui.vaadin;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.util.StringUtils;

import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;

import it.arsinfo.smd.entity.Nota;
import it.arsinfo.smd.entity.Storico;
import it.arsinfo.smd.repository.NotaDao;

public class NotaSearch extends SmdSearch<Nota> {

    private String searchText;
    private Storico storico;

    public NotaSearch(NotaDao notaDao, List<Storico> storici) {
        super(notaDao);
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
        if (StringUtils.isEmpty(searchText) && storico == null) {
            return findAll();
        }
        if (StringUtils.isEmpty(searchText)) {
            return ((NotaDao) getRepo()).findByStorico(storico);
        }
        if (storico == null) {
            return ((NotaDao) getRepo()).findByDescriptionContainingIgnoreCase(searchText);
        }
        return ((NotaDao) getRepo()).findByDescriptionContainingIgnoreCase(searchText)
                .stream()
                .filter(n -> 
                n.getStorico().getId() 
                        == storico.getId())
                .collect(Collectors.toList());
    }

}
