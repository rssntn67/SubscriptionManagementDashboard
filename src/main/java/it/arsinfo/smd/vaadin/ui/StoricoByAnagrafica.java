package it.arsinfo.smd.vaadin.ui;

import java.util.List;
import java.util.stream.Collectors;

import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Storico;
import it.arsinfo.smd.repository.StoricoDao;
import it.arsinfo.smd.vaadin.model.SmdSearchKey;


public class StoricoByAnagrafica extends SmdSearchKey<Storico,Anagrafica> {

    public StoricoByAnagrafica(StoricoDao storicoDao) {
        super(storicoDao);
        setComponents(new HorizontalLayout(new Label("Storico")));
    }
    
    @Override
    public List<Storico> findByKey(Anagrafica customer) {
        List<Storico> list = ((StoricoDao)getRepo()).findByIntestatario(customer);
        list.addAll(((StoricoDao)getRepo()).findByDestinatario(customer)
                    .stream()
                    .filter(ap -> customer.getId() != ap.getIntestatario().getId())
                    .collect(Collectors.toList()));
        return list;
    }

}
