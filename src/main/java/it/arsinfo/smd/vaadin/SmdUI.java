package it.arsinfo.smd.vaadin;



import com.vaadin.annotations.Title;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@SpringUI
@Title("Gestione Abbonamenti ADP")
public class SmdUI extends UI {

	public final static String URL_ANAGRAFICA="anagrafica";
	public final static String URL_PUBBLICAZIONI="pubblicazioni";
	public final static String URL_ABBONAMENTI="abbonamenti";
	public final static String URL_CAMPAGNA="campagna";
	public final static String URL_INCASSI="incassi";
	public final static String URL_PROSPETTI="prospetti";
	public final static String URL_NOTE="note";
	/**
	 * 
	 */
	private static final long serialVersionUID = 7884064928998716106L;

	public static Link[] getPageLinks() {
	    Link[] links= {
                    new Link("Anagrafica", new ExternalResource(URL_ANAGRAFICA)),
                    new Link("Pubblicazioni", new ExternalResource(URL_PUBBLICAZIONI)),
                    new Link("Abbonamenti", new ExternalResource(URL_ABBONAMENTI)),                         
                    new Link("Campagna", new ExternalResource(URL_CAMPAGNA)),                               
                    new Link("Note", new ExternalResource(URL_NOTE)),                               
                    new Link("Incassi", new ExternalResource(URL_INCASSI)),
                    new Link("Prospetti", new ExternalResource(URL_PROSPETTI))	            
	    };
	    return links;
	}
	@Override
	protected void init(VaadinRequest request) {
		VerticalLayout layout = new VerticalLayout();
		layout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
		layout.addComponents(new Label("Benvenuti nel programma gestione abbonamenti ADP")
		);
		layout.addComponents(getPageLinks());
		setContent(layout);

	}

}
