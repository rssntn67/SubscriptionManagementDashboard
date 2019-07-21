package it.arsinfo.smd.ui.vaadin;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;

import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.entity.Campagna;
import it.arsinfo.smd.repository.CampagnaDao;

public class CampagnaSearch extends SmdSearch<Campagna> {

    Anno anno;

    public CampagnaSearch(CampagnaDao repo) {
        super(repo);
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
        if (anno != null) {
            Campagna campagna = ((CampagnaDao) getRepo()).findByAnno(anno);
            List<Campagna> campagne = new ArrayList<>();
            campagne.add(campagna);
            return campagne;
        }
        return findAll();
    }

}
