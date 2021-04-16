package it.arsinfo.smd.ui.campagna;

import java.util.EnumSet;
import java.util.List;

import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;

import it.arsinfo.smd.service.api.CampagnaService;
import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.entity.Campagna;
import it.arsinfo.smd.ui.vaadin.SmdSearch;

public class CampagnaSearch extends SmdSearch<Campagna> {

    private Anno anno;
    
    private final CampagnaService dao;

    public CampagnaSearch(CampagnaService dao) {
        super(dao);
        this.dao=dao;
        ComboBox<Anno> filterAnno = new ComboBox<Anno>("Selezionare Anno",
                                                       EnumSet.allOf(Anno.class));

        setComponents(new HorizontalLayout(filterAnno));

        filterAnno.setEmptySelectionAllowed(true);
        filterAnno.setItemCaptionGenerator(Anno::getAnnoAsString);
        filterAnno.setPlaceholder("Cerca per Anno");

        filterAnno.addSelectionListener(e -> {
            if (e.getValue() == null) {
                anno=null;
            } else {
                anno = e.getSelectedItem().get();
            }
            onChange();
        });

    }

    @Override
    public List<Campagna> find() {
        return dao.searchBy(anno);
    }

}
