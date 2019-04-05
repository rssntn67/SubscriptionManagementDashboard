package it.arsinfo.smd.vaadin.ui;

import java.util.List;

import com.vaadin.data.Binder;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextArea;

import it.arsinfo.smd.entity.Nota;
import it.arsinfo.smd.entity.Storico;
import it.arsinfo.smd.repository.NotaDao;
import it.arsinfo.smd.vaadin.model.SmdEditor;

public class NotaEditor extends SmdEditor<Nota> {

    private final TextArea description = new TextArea("Descrizione");
    private final ComboBox<Storico> storico = new ComboBox<Storico>("Selezionare lo storico");

    public NotaEditor(NotaDao notaDao, List<Storico> storici) {

        super(notaDao, new Binder<>(Nota.class));
        HorizontalLayout pri = new HorizontalLayout();
        pri.addComponentsAndExpand(storico);
        HorizontalLayout val = new HorizontalLayout();
        val.addComponentsAndExpand(description);
        setComponents(getActions(),pri,val);

        description.setWordWrap(false);
        description.setSizeFull();
        storico.setItems(storici);
        storico.setItemCaptionGenerator(Storico::getCaption);
        storico.setEmptySelectionAllowed(false);

        getBinder().forField(storico).asRequired().withValidator(an -> an != null,
                                                               "Scegliere uno storico").bind(Nota::getStorico,
                                                                                            Nota::setStorico);
        getBinder().forField(description).bind(Nota::getDescription,
                                          Nota::setDescription);

    }

    @Override
    public void focus(boolean persisted, Nota obj) {
        getSave().setEnabled(!persisted);
        getCancel().setEnabled(false);
        storico.setReadOnly(persisted);
        description.setReadOnly(persisted);
        storico.focus();

    }

}
