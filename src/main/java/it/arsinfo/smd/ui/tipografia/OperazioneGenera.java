package it.arsinfo.smd.ui.tipografia;

import java.util.EnumSet;
import java.util.List;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;

import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.Mese;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.ui.vaadin.SmdChangeHandler;

public class OperazioneGenera extends SmdChangeHandler {

    private boolean isGeneraA=false;
    private boolean isGenera=false;
    private Pubblicazione pubblicazione=null;
    private Anno anno=Anno.getAnnoCorrente();
    public OperazioneGenera(String caption, VaadinIcons icon, List<Pubblicazione> pubblicazioni) {

        HorizontalLayout buttons = new HorizontalLayout();
        ComboBox<Anno> annocb = new ComboBox<Anno>("Anno", EnumSet.allOf(Anno.class));
        annocb.setSelectedItem(anno);
        annocb.setEmptySelectionAllowed(false);

        annocb.addSelectionListener(a -> {
            anno = a.getValue();            
        });

        ComboBox<Pubblicazione> pcb = new ComboBox<Pubblicazione>("Pubblicazione", pubblicazioni);
        pcb.setEmptySelectionAllowed(false);
        pcb.setItemCaptionGenerator(Pubblicazione::getNome);

        pcb.addSelectionListener(a -> {
            pubblicazione = a.getValue();            
        });


        Button indietro = new Button("indietro");
        indietro.addClickListener(click -> {
            isGeneraA=false;
            isGenera=false;
            onChange();   
        });
        
        Button genera = new Button("Genera" + Mese.getMeseCorrente().getNomeBreve() + Anno.getAnnoCorrente().getAnnoAsString(),icon);
        genera.addClickListener(click -> {
            isGeneraA=false;
            isGenera=true;
            onChange();
        });

        Button generaA = new Button(caption,icon);
        generaA.addClickListener(click -> {
            isGeneraA=true;
            isGenera=false;
            onChange();
        });
                
        buttons.addComponent(generaA);
        buttons.addComponent(genera);
        buttons.addComponent(indietro);
        

        setComponents(buttons,annocb,pcb);
    }
   
    public boolean isGenera() {
        return isGenera;
    }

    public boolean isGeneraA() {
        return isGeneraA;
    }

    public Anno getAnno() {
        return anno;
    }

	public Pubblicazione getPubblicazione() {
		return pubblicazione;
	}
    
    
}
