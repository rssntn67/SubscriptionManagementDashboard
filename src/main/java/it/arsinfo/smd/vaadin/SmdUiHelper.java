package it.arsinfo.smd.vaadin;



import com.vaadin.server.ExternalResource;
import com.vaadin.ui.Link;

public class SmdUiHelper  {

	public final static String URL_ANAGRAFICA="anagrafica";
	public final static String URL_PUBBLICAZIONI="pubblicazioni";
	public final static String URL_ABBONAMENTI="abbonamenti";
	public final static String URL_CAMPAGNA="campagna";
	public final static String URL_INCASSI="incassi";
        public final static String URL_VERSAMENTI="versamenti";
	public final static String URL_PROSPETTI="prospetti";
	public final static String URL_NOTE="note";

	public static Link[] getPageLinks() {
	    Link[] links= {
                    new Link("Anagrafica", new ExternalResource(URL_ANAGRAFICA)),
                    new Link("Pubblicazioni", new ExternalResource(URL_PUBBLICAZIONI)),
                    new Link("Abbonamenti", new ExternalResource(URL_ABBONAMENTI)),                    
                    new Link("Campagna", new ExternalResource(URL_CAMPAGNA)),
                    new Link("Note", new ExternalResource(URL_NOTE)),
                    new Link("Incassi", new ExternalResource(URL_INCASSI)),
                    new Link("Versamenti", new ExternalResource(URL_VERSAMENTI)),
                    new Link("Prospetti", new ExternalResource(URL_PROSPETTI))
	    };
	    return links;
	}

}
