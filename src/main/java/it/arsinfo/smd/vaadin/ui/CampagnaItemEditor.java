package it.arsinfo.smd.vaadin.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;

import it.arsinfo.smd.entity.CampagnaItem;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.vaadin.model.SmdChangeHandler;

public class CampagnaItemEditor
        extends SmdChangeHandler {

    private HorizontalLayout layout= new HorizontalLayout();
    private List<Pubblicazione> selected = new ArrayList<>();
    private Map<Long,Pubblicazione> pubblicazioni;

    public CampagnaItemEditor(List<Pubblicazione> pubblicazioni) {
        this.pubblicazioni=pubblicazioni.stream().collect(Collectors.toMap(p->p.getId(), p->p));
        setComponents(layout);
    }

    public List<Pubblicazione> getSelected() {
        return selected;
    }
        
    public void edit(List<CampagnaItem> items, boolean persisted) {
        layout.removeAllComponents();
        Map<Long,CampagnaItem> persisteditemmap = 
                items.stream().collect(Collectors.toMap(p->p.getPubblicazione().getId(), p->p));
        pubblicazioni.values().stream().forEach(p -> {
                CheckBox cbx = new CheckBox(p.getNome());
                cbx.setValue(persisteditemmap.containsKey(p.getId()));
                cbx.addValueChangeListener( e -> {
                    if (e.getValue()) {
                        selected.add(p);
                    } else {
                        selected.remove(p);
                    }
                });
                cbx.setReadOnly(persisted);
                cbx.setVisible(true);
                layout.addComponent(cbx);
                
        });
        setVisible(true);
    }
    
}
