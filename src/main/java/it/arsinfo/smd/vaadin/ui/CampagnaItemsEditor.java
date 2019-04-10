package it.arsinfo.smd.vaadin.ui;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import it.arsinfo.smd.entity.CampagnaItem;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.vaadin.model.SmdBoxMapper;

public class CampagnaItemsEditor
        extends SmdBoxMapper<Pubblicazione, CampagnaItem> {

    public CampagnaItemsEditor(List<Pubblicazione> pubblicazioni) {
        super(pubblicazioni);
    }
    
    public Set<Long> match(List<CampagnaItem> items) {
        return items.stream().map(item -> item.getId()).collect(Collectors.toSet());
    }

    @Override
    public boolean getReadOnly(Pubblicazione t, boolean persisted) {
        return persisted;
    }

    @Override
    public String getBoxCaption(Pubblicazione t) {
        return t.getNome();
    }

    
    
}
