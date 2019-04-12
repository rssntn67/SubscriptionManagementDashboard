package it.arsinfo.smd.vaadin.model;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.server.ExternalResource;
import com.vaadin.ui.Link;

import it.arsinfo.smd.entity.UserInfo.Role;
import it.arsinfo.smd.security.SecurityUtils;

public class SmdUIHelper {

    public static final String APP_URL = "/";
    public final static String URL_LOGIN = "/login.html";
    public final static String URL_LOGIN_PROCESSING = "/login";
    public final static String URL_LOGIN_FAILURE = "/login.html?error";
    public final static String URL_LOGOUT = "/login.html?logout";
    public final static String URL_ANAGRAFICA = "/anagrafica";
    public final static String URL_PUBBLICAZIONI = "/pubblicazioni";
    public final static String URL_ABBONAMENTI = "/abbonamenti";
    public final static String URL_CAMPAGNA = "/campagna";
    public final static String URL_INCASSI = "/incassi";
    public final static String URL_VERSAMENTI = "/versamenti";
    public final static String URL_OPERAZIONI = "/operazioni";
    public final static String URL_PROSPETTI = "/prospetti";
    public final static String URL_NOTE = "/note";
    public final static String URL_USER = "/user";

    public static Link[] getPageLinks(String username) {
        List<Link> links = new ArrayList<>();
        if (SecurityUtils.isCurrentUserInRole(Role.ADMIN.name())) {
            links.add(new Link("Amministrazione Utenti", new ExternalResource(URL_USER)));
        }
        links.add(new Link("Campagna", new ExternalResource(URL_CAMPAGNA)));
        links.add(new Link("Anagrafica",   new ExternalResource(URL_ANAGRAFICA)));
        links.add(new Link("Pubblicazioni",new ExternalResource(URL_PUBBLICAZIONI)));
        links.add(new Link("Abbonamenti",  new ExternalResource(URL_ABBONAMENTI)));
        links.add(new Link("Operazioni", new ExternalResource(URL_OPERAZIONI)));
        links.add(new Link("Prospetti", new ExternalResource(URL_PROSPETTI)));
        links.add(new Link("Versamenti", new ExternalResource(URL_VERSAMENTI)));
        links.add(new Link("Incassi", new ExternalResource(URL_INCASSI)));
        links.add(new Link("Note", new ExternalResource(URL_NOTE)));
        links.add(new Link(String.format("Logout: %s",SecurityUtils.getUsername()),
                         new ExternalResource(URL_LOGOUT)));
        return links.toArray((new Link[links.size()]));
    }
}
