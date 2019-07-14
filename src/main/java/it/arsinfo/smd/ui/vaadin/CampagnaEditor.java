package it.arsinfo.smd.ui.vaadin;

import java.util.EnumSet;

import com.vaadin.data.Binder;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;

import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.StatoCampagna;
import it.arsinfo.smd.entity.Campagna;
import it.arsinfo.smd.repository.CampagnaDao;

public class CampagnaEditor extends SmdEditor<Campagna> {

    private final ComboBox<Anno> anno = new ComboBox<Anno>("Anno",
                                                           EnumSet.allOf(Anno.class));

    private final ComboBox<StatoCampagna> statoCampagna = new ComboBox<StatoCampagna>("Stato",
            EnumSet.allOf(StatoCampagna.class));

    private HorizontalLayout pri = new HorizontalLayout(anno,statoCampagna);

    public CampagnaEditor(CampagnaDao repo) {

        super(repo, new Binder<>(Campagna.class));
        setComponents(getActions(),pri);

        anno.setItemCaptionGenerator(Anno::getAnnoAsString);

        statoCampagna.setReadOnly(true);
        getBinder().bindInstanceFields(this);

        setVisible(false);

    }

    @Override
    public void focus(boolean persisted, Campagna campagna) {

        anno.setReadOnly(persisted);
        getSave().setEnabled(!persisted);
        getCancel().setEnabled(!persisted);
        getDelete().setEnabled(!persisted || (campagna.getStatoCampagna() == StatoCampagna.Generata && campagna.getAnno().getAnno() > Anno.getAnnoCorrente().getAnno())
                );
        anno.focus();

    }
}
