package it.arsinfo.smd.ui.campagna;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import it.arsinfo.smd.entity.CampagnaItem;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.ui.vaadin.SmdCheckBoxArrayMapper;
import it.arsinfo.smd.ui.vaadin.SmdChangeHandler;

public class CampagnaItemsEditor extends SmdChangeHandler {

    private final SmdCheckBoxArrayMapper<Pubblicazione, CampagnaItem> mapper;
    public CampagnaItemsEditor(List<Pubblicazione> pubblicazioni) {
        mapper = new SmdCheckBoxArrayMapper<Pubblicazione, CampagnaItem>(pubblicazioni) {
            
            @Override
            public Set<Long> match(List<CampagnaItem> items) {
                return items.stream().map(item -> item.getPubblicazione().getId()).collect(Collectors.toSet());
            }

            @Override
            public boolean getReadOnly(Pubblicazione t, boolean persisted) {
                return true;
            }

            @Override
            public String getBoxCaption(Pubblicazione t) {
                return t.getNome();
            }

        };
        
        setComponents(mapper.getLayout());
        
    }
    public SmdCheckBoxArrayMapper<Pubblicazione, CampagnaItem> getMapper() {
        return mapper;
    }

    public List<Pubblicazione> getSelected() {
        return mapper.getSelected();
    }

    public void edit(List<CampagnaItem> items, boolean persisted) {
        mapper.edit(items, persisted);
    }
    
}
