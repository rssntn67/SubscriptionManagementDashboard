package it.arsinfo.smd.ui.vaadin;

import java.util.List;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;

import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.repository.EstrattoContoDao;
import it.arsinfo.smd.repository.OperazioneDao;

public class OperazioneGenera extends SmdChangeHandler {

    private final HorizontalLayout buttons = new HorizontalLayout();
    private final Button genera;
    private final Button indietro;
    public OperazioneGenera(String caption, VaadinIcons icon,OperazioneDao operazioneDao, EstrattoContoDao abbonamentoDao, List<Pubblicazione> pubblicazioni) {
        indietro = new Button("indietro");
        indietro.addClickListener(click -> onChange());
        genera = new Button(caption,icon);
        genera.addClickListener(click -> {
//        Smd.generaOperazioni(pubblicazioni, abbonamentoDao.findAll()).forEach(op -> operazioneDao.save(op));
        onChange();
        });
        
        buttons.addComponent(genera);
        buttons.addComponent(indietro);

        setComponents(buttons);
    }

    public void edit() {
        buttons.setVisible(true);
        genera.setVisible(true);
        indietro.setVisible(true);
    }
    
}
