package it.arsinfo.smd.vaadin.ui;

import com.vaadin.annotations.Title;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;

import it.arsinfo.smd.vaadin.model.SmdButton;
import it.arsinfo.smd.vaadin.model.SmdUI;
import it.arsinfo.smd.vaadin.model.SmdUIHelper;

@SpringUI(path = SmdUIHelper.URL_PROSPETTI)
@Title("Prospetti ADP")
public class ProspettiUI extends SmdUI {

    /**
     * 
     */
    private static final long serialVersionUID = -4970387092690412856L;

    @Override
    protected void init(VaadinRequest request) {
        super.init(request,"Prospetti");
        
        SmdButton tipografo = new SmdButton("Tipografo", VaadinIcons.ENVELOPES);
        SmdButton insolventi = new SmdButton("Insolventi", VaadinIcons.ENVELOPES);
        SmdButton spedizioni = new SmdButton("Spedizioni", VaadinIcons.ENVELOPES);
        SmdButton spedizioniSede = new SmdButton("Spedizioni Sede", VaadinIcons.ENVELOPES);
        
        addSmdComponents(tipografo,insolventi,spedizioni,spedizioniSede);
     }

}
