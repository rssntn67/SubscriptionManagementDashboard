package it.arsinfo.smd.ui.storico;

import com.vaadin.data.Binder;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextArea;

import it.arsinfo.smd.entity.Nota;
import it.arsinfo.smd.ui.vaadin.SmdItemEditor;

public class NotaEditor extends SmdItemEditor<Nota> {

    private final TextArea description = new TextArea("Descrizione");

    public NotaEditor() {

        super(new Binder<>(Nota.class));
        HorizontalLayout val = new HorizontalLayout();
        val.addComponentsAndExpand(description);

        setComponents(val);
        description.setWordWrap(false);
        description.setSizeFull();

        getBinder().forField(description).bind(Nota::getDescription,
                                          Nota::setDescription);
        
    }

    @Override
    public void focus(boolean persisted, Nota obj) {
        description.setVisible(!persisted);
        description.focus();
    }

}
