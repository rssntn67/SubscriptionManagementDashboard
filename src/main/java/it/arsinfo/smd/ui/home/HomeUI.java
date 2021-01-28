package it.arsinfo.smd.ui.home;



import com.vaadin.annotations.Title;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.BrowserWindowOpener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

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
    	super.init(request, "");
        
        BrowserWindowOpener popupOpenerA = new BrowserWindowOpener("/printtestA");
        Button stampaA = new Button("Test Stampa Busta", VaadinIcons.PRINT);
        popupOpenerA.extend(stampaA);

        BrowserWindowOpener popupOpenerB = new BrowserWindowOpener("/printtestB");
        Button stampaB = new Button("Test Stampa Cartoncino", VaadinIcons.PRINT);
        popupOpenerB.extend(stampaB);

        BrowserWindowOpener popupOpenerC = new BrowserWindowOpener("/printtestC");
        Button stampaC = new Button("Test Stampa Busta Piccola", VaadinIcons.PRINT);
        popupOpenerC.extend(stampaC);

        HorizontalLayout print = new HorizontalLayout(stampaA,stampaB,stampaC);
        addComponents(print);

        addComponents(new Label("<hr style=\"height:60px;border-width:0;color:gray;background-color:gray\">",ContentMode.HTML));
        addComponents(new Label("<p style=\"color:black;font-size:20px;face=Arial;\"><b>Benvenuto nel programma gestione abbonamenti ADP</b></p>",ContentMode.HTML));

    }

}
