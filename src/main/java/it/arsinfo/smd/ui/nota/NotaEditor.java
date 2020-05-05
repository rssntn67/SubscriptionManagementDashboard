package it.arsinfo.smd.ui.nota;

import java.util.List;

import com.vaadin.data.Binder;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;

import it.arsinfo.smd.dao.NotaDao;
import it.arsinfo.smd.entity.Nota;
import it.arsinfo.smd.entity.Storico;
import it.arsinfo.smd.ui.vaadin.SmdEditor;

public class NotaEditor extends SmdEditor<Nota> {

    private final TextArea description = new TextArea("Descrizione");
    private final ComboBox<Storico> storico = new ComboBox<Storico>("Selezionare lo storico");
    private final TextField  user = new TextField("User");

    public NotaEditor(NotaDao notaDao, List<Storico> storici) {

        super(notaDao, new Binder<>(Nota.class));
        HorizontalLayout pri = new HorizontalLayout();
        pri.addComponentsAndExpand(storico);
        HorizontalLayout val = new HorizontalLayout();
        val.addComponent(user);
        val.addComponentsAndExpand(description);
        setComponents(getActions(),pri,val);

        description.setWordWrap(false);
        description.setSizeFull();
        storico.setItems(storici);
        storico.setItemCaptionGenerator(Storico::getCaption);
        storico.setEmptySelectionAllowed(false);

        user.setReadOnly(true);

        getBinder().forField(storico).asRequired().withValidator(an -> an != null,
                                                               "Scegliere uno storico").bind(Nota::getStorico,
                                                                                            Nota::setStorico);
        getBinder().forField(description).bind(Nota::getDescription,
                                          Nota::setDescription);
        
        getBinder().forField(user).bind(Nota::getOperatore,
                                               Nota::setOperatore);

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
