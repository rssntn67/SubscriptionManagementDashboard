package it.arsinfo.smd.vaadin.ui;

import java.util.List;

import com.vaadin.data.Binder;
import com.vaadin.ui.ComboBox;

import it.arsinfo.smd.entity.CampagnaItem;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.repository.CampagnaItemDao;
import it.arsinfo.smd.vaadin.model.SmdEditor;

public class CampagnaItemEditor
        extends SmdEditor<CampagnaItem> {

    private final ComboBox<Pubblicazione> pubblicazione = new ComboBox<Pubblicazione>("Pubblicazioni");

    public CampagnaItemEditor(
            CampagnaItemDao campagnaItemDao,
            List<Pubblicazione> pubblicazioni) {

        super(campagnaItemDao, new Binder<>(CampagnaItem.class) );
        pubblicazione.setEmptySelectionAllowed(false);
        pubblicazione.setPlaceholder("Pubblicazione");
        pubblicazione.setItems(pubblicazioni);
        pubblicazione.setItemCaptionGenerator(Pubblicazione::getCaption);


        setComponents(getActions(), pubblicazione);
 
        getBinder()
            .forField(pubblicazione)
            .asRequired()
            .withValidator(p -> p != null, "Pubblicazione deve essere selezionata")
            .bind(CampagnaItem::getPubblicazione,CampagnaItem::setPubblicazione);

    }

    @Override
    public void focus(boolean persisted, CampagnaItem obj) {
        getSave().setEnabled(!persisted);
        getCancel().setEnabled(!persisted);
        getDelete().setEnabled(!persisted);
        pubblicazione.focus();        
    }

}
