package it.arsinfo.smd.vaadin;



import com.vaadin.server.ExternalResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Link;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class SmdHeaderUI extends UI {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7884064928998716106L;
        private VerticalLayout layout = new VerticalLayout();

	
	@Override
	protected void init(VaadinRequest request) {
	    HorizontalLayout header = new HorizontalLayout();
	    header.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
	    header.addComponents( 
				new Link("Anagrafica", new ExternalResource(SmdUI.URL_ANAGRAFICA)),
				new Link("Pubblicazioni", new ExternalResource(SmdUI.URL_PUBBLICAZIONI)),
				new Link("Abbonamenti", new ExternalResource(SmdUI.URL_ABBONAMENTI)),				
				new Link("Campagna", new ExternalResource(SmdUI.URL_CAMPAGNA)),				
				new Link("Note", new ExternalResource(SmdUI.URL_NOTE)),				
				new Link("Incassi", new ExternalResource(SmdUI.URL_INCASSI)),
				new Link("Prospetti", new ExternalResource(SmdUI.URL_PROSPETTI))			
		);
		layout.addComponent(header);
		setContent(layout);

	}
		
	protected void addComponents(Component... components ) {
	    layout.addComponents(components);
	}

}
