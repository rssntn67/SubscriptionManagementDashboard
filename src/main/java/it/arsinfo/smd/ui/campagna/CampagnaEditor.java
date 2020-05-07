package it.arsinfo.smd.ui.campagna;

import java.util.EnumSet;

import com.vaadin.data.Binder;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;

import it.arsinfo.smd.dao.CampagnaServiceDao;
import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.StatoCampagna;
import it.arsinfo.smd.entity.Campagna;
import it.arsinfo.smd.ui.vaadin.SmdServiceDaoEditor;

public class CampagnaEditor extends SmdServiceDaoEditor<Campagna> {

    private final ComboBox<Anno> anno = new ComboBox<Anno>("Anno",
                                                           EnumSet.allOf(Anno.class));

    private final ComboBox<StatoCampagna> statoCampagna = new ComboBox<StatoCampagna>("Stato",
            EnumSet.allOf(StatoCampagna.class));

    public CampagnaEditor(CampagnaServiceDao repo) {

        super(repo, new Binder<>(Campagna.class));
        HorizontalLayout pri = new HorizontalLayout(anno,statoCampagna);
        setComponents(getActions(),pri);

        anno.setItemCaptionGenerator(Anno::getAnnoAsString);

        statoCampagna.setReadOnly(true);
        getBinder().bindInstanceFields(this);
    }

    @Override
    public void focus(boolean persisted, Campagna campagna) {

        anno.setReadOnly(persisted);
        getSave().setEnabled(!persisted);
        if (!persisted) {
        	getSave().setCaption("Genera Campagna");
        }  else {
        	getSave().setCaption("Salva");        	
        }
        getCancel().setEnabled(!persisted);
        getDelete().setEnabled( persisted &&
    		campagna.getStatoCampagna() == StatoCampagna.Generata 
         && campagna.getAnno().getAnno() > Anno.getAnnoCorrente().getAnno()
        );
        statoCampagna.setVisible(persisted);
        anno.focus();
    }
}
