package it.arsinfo.smd.vaadin.ui;

import java.util.EnumSet;

import com.vaadin.data.Binder;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;

import it.arsinfo.smd.data.Cassa;
import it.arsinfo.smd.data.Ccp;
import it.arsinfo.smd.data.Cuas;
import it.arsinfo.smd.entity.Incasso;
import it.arsinfo.smd.repository.IncassoDao;
import it.arsinfo.smd.vaadin.model.SmdEditor;

public class IncassoEditor extends SmdEditor<Incasso> {
    
    private final ComboBox<Cassa> cassa = new ComboBox<Cassa>("Cassa",EnumSet.allOf(Cassa.class));
    private final ComboBox<Cuas> cuas = new ComboBox<Cuas>("Cuas",EnumSet.allOf(Cuas.class));
    private final ComboBox<Ccp> ccp = new ComboBox<Ccp>("Ccp",EnumSet.allOf(Ccp.class));
    private final TextField operazione = new TextField("operazione");


    public IncassoEditor(IncassoDao incassoDao) {
        super(incassoDao, new Binder<>(Incasso.class));

        setComponents(getActions(), new HorizontalLayout(cassa,cuas,ccp,operazione));
        
        getBinder().bindInstanceFields(this);
    }


    @Override
    public void focus(boolean persisted, Incasso incasso) {
        cassa.setReadOnly(persisted);
        cuas.setReadOnly(persisted);
        ccp.setReadOnly(persisted);
        operazione.setReadOnly(persisted);
    }

}
