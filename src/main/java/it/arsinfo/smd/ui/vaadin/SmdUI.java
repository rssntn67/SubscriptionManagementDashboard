package it.arsinfo.smd.ui.vaadin;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.server.ExternalResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import it.arsinfo.smd.entity.UserInfo;
import it.arsinfo.smd.entity.UserInfo.Role;
import it.arsinfo.smd.repository.UserInfoDao;
import it.arsinfo.smd.ui.security.SecurityUtils;

public abstract class SmdUI extends UI {

    @Autowired
    private UserInfoDao userInfoDao;
    /**
     * 
     */
    private static final long serialVersionUID = 7884064928998716106L;
    private VerticalLayout layout = new VerticalLayout();
    private HorizontalLayout menu = new HorizontalLayout();
    private Label header = new Label("");
    public static final String APP_URL = "/";
    public final static String URL_LOGIN = "/login.html";
    public final static String URL_LOGIN_PROCESSING = "/login";
    public final static String URL_LOGIN_FAILURE = "/login.html?error";
    public final static String URL_LOGOUT = "/login.html?logout";
    public final static String URL_ANAGRAFICA = "/anagrafica";
    public final static String URL_STORICO = "/storico";
    public final static String URL_PUBBLICAZIONI = "/pubblicazioni";
    public final static String URL_ABBONAMENTI = "/abbonamenti";
    public final static String URL_SPEDIZIONI = "/spedizioni";
    public final static String URL_CAMPAGNA = "/campagna";
    public final static String URL_INCASSI = "/incassi";
    public final static String URL_VERSAMENTI = "/versamenti";
    public final static String URL_OPERAZIONI = "/operazioni";
    public final static String URL_PROSPETTI = "/prospetti";
    public final static String URL_NOTE = "/note";
    public final static String URL_USER = "/user";
    public final static String URL_RESET = "/reset";

    private UserInfo loggedInUser;
    protected void init(VaadinRequest request, String head) {

        loggedInUser = SecurityUtils.getCurrentUser(userInfoDao);
        menu.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        menu.addComponents(getPageLinks(SecurityUtils.getUsername()));
        header.setValue(head);
        layout.addComponent(menu);
        layout.addComponent(header);
        setContent(layout);

    }

    protected void setExpandRatio(Component component, float ratio) {
        layout.setExpandRatio(component, ratio);
    }

    protected void hideMenu() {
        menu.setVisible(false);
    }

    protected void showMenu() {
        menu.setVisible(true);
    }

    protected void setHeader(String head) {
        header.setValue(head);
    }

    protected void addSmdComponents(SmdChangeHandler ... smdChangeHandlers) {
        for (SmdChangeHandler smdChangeHandler: smdChangeHandlers) {
            layout.addComponents(smdChangeHandler.getComponents());
        }
    }

    public Link[] getPageLinks(String username) {
        List<Link> links = new ArrayList<>();
        UserInfo loggedInUser = SecurityUtils.getCurrentUser(userInfoDao);
        links.add(new Link("Pubblicazioni",new ExternalResource(URL_PUBBLICAZIONI)));
        links.add(new Link("Campagna", new ExternalResource(URL_CAMPAGNA)));
        links.add(new Link("Abbonamenti",  new ExternalResource(URL_ABBONAMENTI)));
        links.add(new Link("Spedizioni",  new ExternalResource(URL_SPEDIZIONI)));
        links.add(new Link("Anagrafica",   new ExternalResource(URL_ANAGRAFICA)));
        links.add(new Link("Storico",   new ExternalResource(URL_STORICO)));
        links.add(new Link("Operazioni", new ExternalResource(URL_OPERAZIONI)));
        links.add(new Link("Prospetti", new ExternalResource(URL_PROSPETTI)));
        links.add(new Link("Versamenti", new ExternalResource(URL_VERSAMENTI)));
        links.add(new Link("Incassi", new ExternalResource(URL_INCASSI)));
        links.add(new Link("Note", new ExternalResource(URL_NOTE)));
        if (loggedInUser.getRole() == Role.ADMIN ) {
            links.add(new Link("Amministrazione Utenti", new ExternalResource(URL_USER)));
        } 
        if (!loggedInUser.isLocked()) {
            links.add(new Link("Reset Password", new ExternalResource(URL_RESET)));
        }
        links.add(new Link(String.format("Logout: %s",loggedInUser.getUsername()),
                         new ExternalResource(URL_LOGOUT)));
        return links.toArray((new Link[links.size()]));
    }

    public UserInfo getLoggedInUser() {
        return loggedInUser;
    }

    public void setLoggedInUser(UserInfo loggedInUser) {
        this.loggedInUser = loggedInUser;
    }

}
