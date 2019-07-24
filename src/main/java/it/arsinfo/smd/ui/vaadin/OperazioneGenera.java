package it.arsinfo.smd.ui.vaadin;

import java.util.EnumSet;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;

import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.Mese;

public class OperazioneGenera extends SmdChangeHandler {

    private boolean isGeneraA=false;
    private boolean isGenera=false;
    private Anno anno=Anno.getAnnoCorrente();
    public OperazioneGenera(String caption, VaadinIcons icon) {

        HorizontalLayout buttons = new HorizontalLayout();
        ComboBox<Anno> annocb = new ComboBox<Anno>("Anno", EnumSet.allOf(Anno.class));
        annocb.setSelectedItem(anno);
        annocb.setEmptySelectionAllowed(false);
        
        annocb.addSelectionListener(a -> {
            anno = a.getValue();            
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
        

        setComponents(buttons,annocb);
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
    
    
}
