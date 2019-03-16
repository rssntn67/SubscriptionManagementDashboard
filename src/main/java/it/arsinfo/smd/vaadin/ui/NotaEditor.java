package it.arsinfo.smd.vaadin.ui;

import java.util.List;

import com.vaadin.data.Binder;
import com.vaadin.data.converter.LocalDateToDateConverter;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextArea;

import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Nota;
import it.arsinfo.smd.repository.NotaDao;
import it.arsinfo.smd.vaadin.model.SmdEditor;

public class NotaEditor extends SmdEditor<Nota> {

    private final DateField data = new DateField("data");
    private final TextArea description = new TextArea("Descrizione");
    private final ComboBox<Anagrafica> anagrafica = new ComboBox<Anagrafica>("Selezionare il cliente");

    public NotaEditor(NotaDao notaDao, List<Anagrafica> anagrafiche) {

        super(notaDao, new Binder<>(Nota.class));
        HorizontalLayout pri = new HorizontalLayout();
        pri.addComponent(anagrafica);
        pri.addComponent(data);
        pri.addComponentsAndExpand(description);
        setComponents(pri, getActions());

        description.setWordWrap(false);
        description.setSizeFull();
        anagrafica.setItems(anagrafiche);
        anagrafica.setItemCaptionGenerator(Anagrafica::getCaption);
        anagrafica.setEmptySelectionAllowed(false);

        getBinder().forField(anagrafica).asRequired().withValidator(an -> an != null,
                                                               "Scegliere un Cliente").bind(Nota::getAnagrafica,
                                                                                            Nota::setAnagrafica);
        getBinder().forField(description).bind(Nota::getDescription,
                                          Nota::setDescription);
        getBinder().forField(data).withConverter(new LocalDateToDateConverter()).bind("data");


    }

    @Override
    public void focus(boolean persisted, Nota obj) {
        getSave().setEnabled(!persisted);
        getCancel().setEnabled(false);
        data.setReadOnly(persisted);
        data.setVisible(!persisted);
        anagrafica.setReadOnly(persisted);
        description.setReadOnly(persisted);
        anagrafica.focus();

    }

}
