package it.arsinfo.smd.ui.storico;

import com.vaadin.data.Binder;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;

import it.arsinfo.smd.entity.Nota;
import it.arsinfo.smd.ui.vaadin.SmdItemEditor;

public class NotaEditor extends SmdItemEditor<Nota> {

    private final TextArea description = new TextArea("Descrizione");
    private final TextField  user = new TextField("User");

    public NotaEditor() {

        super(new Binder<>(Nota.class));
        HorizontalLayout val = new HorizontalLayout();
        val.addComponent(user);
        val.addComponentsAndExpand(description);

        description.setWordWrap(false);
        description.setSizeFull();

        user.setReadOnly(true);

        getBinder().forField(description).bind(Nota::getDescription,
                                          Nota::setDescription);
        
        getBinder().forField(user).bind(Nota::getOperatore,
                                               Nota::setOperatore);

    }

    @Override
    public void focus(boolean persisted, Nota obj) {
        description.setReadOnly(persisted);
        description.focus();
    }

}
