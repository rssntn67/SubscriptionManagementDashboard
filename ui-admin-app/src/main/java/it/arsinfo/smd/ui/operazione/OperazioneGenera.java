package it.arsinfo.smd.ui.operazione;

import java.util.EnumSet;
import java.util.List;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;

import it.arsinfo.smd.entity.Anno;
import it.arsinfo.smd.entity.Mese;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.ui.vaadin.SmdChangeHandler;

public class OperazioneGenera extends SmdChangeHandler {

    private Pubblicazione pubblicazione=null;
    private Anno anno=Anno.getAnnoCorrente();
    private Mese mese=Mese.getMeseCorrente();

    public OperazioneGenera(String caption, VaadinIcons icon, List<Pubblicazione> pubblicazioni) {

        HorizontalLayout buttons = new HorizontalLayout();
        HorizontalLayout selections = new HorizontalLayout();
        pubblicazione=pubblicazioni.get(0);
        ComboBox<Anno> annocb = new ComboBox<Anno>("Anno", EnumSet.allOf(Anno.class));
        annocb.setSelectedItem(anno);
        annocb.setEmptySelectionAllowed(false);

        annocb.addSelectionListener(a -> {
            anno = a.getValue();            
        });
        
        ComboBox<Mese> mesecb = new ComboBox<Mese>("Mese", EnumSet.allOf(Mese.class));
        mesecb.setSelectedItem(mese);
        mesecb.setEmptySelectionAllowed(false);

        mesecb.addSelectionListener(a -> {
            mese = a.getValue();            
        });


        ComboBox<Pubblicazione> pcb = new ComboBox<Pubblicazione>("Pubblicazione", pubblicazioni);
        pcb.setEmptySelectionAllowed(false);
        pcb.setSelectedItem(pubblicazione);
        pcb.setItemCaptionGenerator(Pubblicazione::getNome);
        pcb.setEmptySelectionAllowed(false);

        pcb.addSelectionListener(a -> {
            pubblicazione = a.getValue();            
        });


        Button indietro = new Button("indietro");
        indietro.addClickListener(click -> {
            onChange();   
        });
        
        Button genera = new Button("Genera", icon);
        genera.addClickListener(click -> {
            onChange();
        });
                
        buttons.addComponent(genera);
        buttons.addComponent(indietro);
        
        selections.addComponents(pcb,mesecb,annocb);
        

        setComponents(buttons,selections);
    }
   
    public Anno getAnno() {
        return anno;
    }

    public Mese getMese() {
        return mese;
    }

	public Pubblicazione getPubblicazione() {
		return pubblicazione;
	}
    
    
}
