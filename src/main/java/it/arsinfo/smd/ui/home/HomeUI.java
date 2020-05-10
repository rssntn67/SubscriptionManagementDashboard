package it.arsinfo.smd.ui.home;



import com.vaadin.annotations.Title;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import it.arsinfo.smd.ui.SmdUI;

@SpringUI(path=SmdUI.HOME)
@Title("Gestione Abbonamenti ADP")
public class HomeUI extends SmdUI {

    /**
     * 
     */
    private static final long serialVersionUID = 7884064928998716106L;

    @Override
    protected void init(VaadinRequest request) {
        HorizontalSplitPanel layout = new HorizontalSplitPanel();
        AbsoluteLayout left = new AbsoluteLayout();
        left.setWidth("1600px");
        left.setHeight("1000px");        
        left.addComponent(new Label("Benvenuto nel programma gestione abbonamenti ADP"), "left: 150px; top: 200px;");
        VerticalLayout right = new VerticalLayout();
        right.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);
        HorizontalSplitPanel GA = new HorizontalSplitPanel();        
        GA.addComponents(new Label("Gestione Anagrafiche:"),new VerticalLayout(getAnagraficaLink()));
        right.addComponents(GA);
        HorizontalSplitPanel GB = new HorizontalSplitPanel();        
        GB.addComponents(new Label("Gestione Abbonamenti:"),new VerticalLayout(getAbbonamentoLinks()));
        right.addComponents(GB);
        HorizontalSplitPanel GI = new HorizontalSplitPanel();        
        GI.addComponents(new Label("Gestione Incassi:"),new VerticalLayout(getIncassoLinks()));
        right.addComponents(GI);
        HorizontalSplitPanel GO = new HorizontalSplitPanel();        
        GO.addComponents(new Label("Gestione ordini :"),new VerticalLayout(getOrdiniLinks()));
        right.addComponents(GO);
        
        layout.addComponents(left,
                             right);
        setContent(layout);

    }

}
