package it.arsinfo.smd.vaadin.model;



import com.vaadin.server.ExternalResource;
import com.vaadin.ui.Link;

public class SmdUIHelper  {

        public static final String APP_URL = "/";
        public final static String URL_LOGIN="/login.html";
        public final static String URL_LOGIN_PROCESSING="/login";
        public final static String URL_LOGIN_FAILURE="/login.html?error";
        public final static String URL_LOGOUT="/login.html?logout";
	public final static String URL_ANAGRAFICA="anagrafica";
	public final static String URL_PUBBLICAZIONI="pubblicazioni";
	public final static String URL_ABBONAMENTI="abbonamenti";
	public final static String URL_CAMPAGNA="campagna";
	public final static String URL_INCASSI="incassi";
        public final static String URL_VERSAMENTI="versamenti";
        public final static String URL_OPERAZIONI="operazioni";
	public final static String URL_PROSPETTI="prospetti";
	public final static String URL_NOTE="note";
        public final static String URL_USER="user";

	public static Link[] getPageLinks() {
	    Link[] links= {
                    new Link("Anagrafica", new ExternalResource(URL_ANAGRAFICA)),
                    new Link("Pubblicazioni", new ExternalResource(URL_PUBBLICAZIONI)),
                    new Link("Abbonamenti", new ExternalResource(URL_ABBONAMENTI)),                    
                    new Link("Campagna", new ExternalResource(URL_CAMPAGNA)),
                    new Link("Note", new ExternalResource(URL_NOTE)),
                    new Link("Incassi", new ExternalResource(URL_INCASSI)),
                    new Link("Versamenti", new ExternalResource(URL_VERSAMENTI)),
                    new Link("Prospetti", new ExternalResource(URL_PROSPETTI)),
                    new Link("Operazioni", new ExternalResource(URL_OPERAZIONI)),
                    new Link("User", new ExternalResource(URL_USER)),
                    new Link("Logout", new ExternalResource(URL_LOGOUT))
	    };
	    return links;
	}

}
