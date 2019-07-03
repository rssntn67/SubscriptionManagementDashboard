package it.arsinfo.smd.ui.vaadin;

import java.util.EnumSet;

import com.vaadin.data.Binder;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;

import it.arsinfo.smd.Smd;
import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.entity.Campagna;
import it.arsinfo.smd.repository.CampagnaDao;

public class CampagnaEditor extends SmdEditor<Campagna> {

    private final ComboBox<Anno> anno = new ComboBox<Anno>("Anno",
                                                           EnumSet.allOf(Anno.class));
 
    private HorizontalLayout pri = new HorizontalLayout(anno);

    public CampagnaEditor(CampagnaDao repo) {

        super(repo, new Binder<>(Campagna.class));
        setComponents(getActions(),pri);

        anno.setItemCaptionGenerator(Anno::getAnnoAsString);

        getBinder().bindInstanceFields(this);

        setVisible(false);

    }

    @Override
    public void focus(boolean persisted, Campagna campagna) {

        anno.setReadOnly(persisted);
        getSave().setEnabled(!persisted);
        getCancel().setEnabled(!persisted);
        getDelete().setEnabled(!persisted || campagna.getAnno().getAnno() > Smd.getAnnoCorrente().getAnno()
                );
        anno.focus();

    }
}
