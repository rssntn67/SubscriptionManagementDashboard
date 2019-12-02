    package it.arsinfo.smd.ui.vaadin;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.server.ExternalResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.MenuItem;
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
    private MenuBar menu = new MenuBar();
    private Label header = new Label("");
    public static final String HOME = "/";
    public final static String URL_LOGIN = "/login.html";
    public final static String URL_LOGIN_PROCESSING = "/login";
    public final static String URL_LOGIN_FAILURE = "/login.html?error";
    public final static String URL_LOGOUT = "/logout";
    public final static String URL_REDIRECT_LOGOUT = "/login.html?logout";
    public final static String URL_ANAGRAFICA = "/anagrafica";
    public final static String URL_STORICO = "/storico";
    public final static String URL_PUBBLICAZIONI = "/pubblicazioni";
    public final static String URL_SPESESPEDIZIONE = "/spesespedizione";
    public final static String URL_ABBONAMENTI = "/abbonamenti";
    public final static String URL_SPEDIZIONI = "/spedizioni";
    public final static String URL_CAMPAGNA = "/campagna";
    public final static String URL_INCASSI = "/incassi";
    public final static String URL_VERSAMENTI = "/versamenti";
    public final static String URL_TIPOGRAFIA = "/tipografo";
    public final static String URL_SPEDIZIONERE = "/spedizioniere";
    public final static String URL_NOTE = "/note";
    public final static String URL_USER = "/user";
    public final static String URL_RESET = "/reset";

    private UserInfo loggedInUser;
    protected void init(VaadinRequest request, String head) {

        loggedInUser = SecurityUtils.getCurrentUser(userInfoDao);
        header.setValue(head);
        layout.addComponent(menu);
        layout.addComponent(header);
        setContent(layout);
        
        menu.addItem("Home",new MenuBar.Command() {
            private static final long serialVersionUID = 1L;
            
            public void menuSelected(MenuItem selectedItem) {
                getUI().getPage().setLocation(HOME);;
            }
        });

        menu.addItem("Anagrafica",new MenuBar.Command() {
            private static final long serialVersionUID = 1L;
            
            public void menuSelected(MenuItem selectedItem) {
                getUI().getPage().setLocation(URL_ANAGRAFICA);
            }
        });

        
        menu.addItem("Pubblicazioni",new MenuBar.Command() {
            private static final long serialVersionUID = 1L;
            
            public void menuSelected(MenuItem selectedItem) {
                getUI().getPage().setLocation(URL_PUBBLICAZIONI);
            }
        });

        menu.addItem("Spese Spedizione",new MenuBar.Command() {
            private static final long serialVersionUID = 1L;
            
            public void menuSelected(MenuItem selectedItem) {
                getUI().getPage().setLocation(URL_SPESESPEDIZIONE);
            }
        });

        MenuItem campagne = menu.addItem("Gestione Campagne",null);
        
        campagne.addItem("Campagna",new MenuBar.Command() {
            private static final long serialVersionUID = 1L;
            
            public void menuSelected(MenuItem selectedItem) {
                getUI().getPage().setLocation(URL_CAMPAGNA);
            }
        } );
        campagne.addItem("Storico",new MenuBar.Command() {
            private static final long serialVersionUID = 1L;
            
            public void menuSelected(MenuItem selectedItem) {
                getUI().getPage().setLocation(URL_STORICO);
            }
        } );
        campagne.addItem("Note",new MenuBar.Command() {
            private static final long serialVersionUID = 1L;
            
            public void menuSelected(MenuItem selectedItem) {
                getUI().getPage().setLocation(URL_NOTE);
            }
        } );
        
        MenuItem abbonamenti = menu.addItem("Gestione Abbonamenti",null);
        abbonamenti.addItem("Abbonamento",new MenuBar.Command() {
            private static final long serialVersionUID = 1L;
            
            public void menuSelected(MenuItem selectedItem) {
                getUI().getPage().setLocation(URL_ABBONAMENTI);
            }
        } );

        abbonamenti.addItem("Spedizioni",new MenuBar.Command() {
            private static final long serialVersionUID = 1L;
            
            public void menuSelected(MenuItem selectedItem) {
                getUI().getPage().setLocation(URL_SPEDIZIONI);
            }
        } );

        MenuItem incassi = menu.addItem("Gestione Incassi",null);
        incassi.addItem("Incassi",new MenuBar.Command() {
            private static final long serialVersionUID = 1L;
            
            public void menuSelected(MenuItem selectedItem) {
                getUI().getPage().setLocation(URL_INCASSI);
            }
        } );
        incassi.addItem("Versamenti",new MenuBar.Command() {
            private static final long serialVersionUID = 1L;
            
            public void menuSelected(MenuItem selectedItem) {
                getUI().getPage().setLocation(URL_VERSAMENTI);
            }
        } );

        MenuItem ordini = menu.addItem("Ordini",null);
        ordini.addItem("Tipografo" ,new MenuBar.Command() {
            
            private static final long serialVersionUID = 1L;
            
            public void menuSelected(MenuItem selectedItem) {
                getUI().getPage().setLocation(URL_TIPOGRAFIA);
            }
        });

        ordini.addItem("Spedizioniere" ,new MenuBar.Command() {
            
            private static final long serialVersionUID = 1L;
            
            public void menuSelected(MenuItem selectedItem) {
                getUI().getPage().setLocation(URL_SPEDIZIONERE);
            }
        });

        MenuItem user = menu.addItem("User", null);
        user.addItem("Logout: "+ loggedInUser.getUsername(),new MenuBar.Command() {
            private static final long serialVersionUID = 1L;
            
            public void menuSelected(MenuItem selectedItem) {
                getUI().getPage().setLocation(URL_LOGOUT);
            }
        } );
       if (loggedInUser.getRole() == Role.ADMIN ) {
           user.addItem("Amministrazione Utenti",new MenuBar.Command() {
                private static final long serialVersionUID = 1L;
                
                public void menuSelected(MenuItem selectedItem) {
                    getUI().getPage().setLocation(URL_USER);
                }
            } );
        } 
        if (!(loggedInUser.getRole() == Role.LOCKED)) {
            user.addItem("Reset Password",new MenuBar.Command() {
                private static final long serialVersionUID = 1L;
                
                public void menuSelected(MenuItem selectedItem) {
                    getUI().getPage().setLocation(URL_RESET);
                }
            } );
        }

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
    
    protected void addComponents(Component...components) {
        layout.addComponents(components);
    }
    
    public Link getHomePageLink() {
        return new Link("Home",new ExternalResource(HOME));        
    }
    
    public Link getAnagraficaLink() {
        return new Link("Anagrafica", new ExternalResource(URL_ANAGRAFICA));
        
    }

    public Link[] getOrdiniLinks() {
        List<Link> links = new ArrayList<>();
        links.add(new Link("Tipografo", new ExternalResource(URL_TIPOGRAFIA)));
        links.add(new Link("Spedizioniere", new ExternalResource(URL_SPEDIZIONERE)));
        return links.toArray(new Link[links.size()]);
    }

    public Link getPubblicazioneLink() {
        return new Link("Pubblicazioni",new ExternalResource(URL_PUBBLICAZIONI));    
    }

    public Link getSpeseSpedizioneLink() {
        return new Link("Spese Spedizione",new ExternalResource(URL_SPESESPEDIZIONE));    
    }

    public Link[] getCampagnaLinks() {
        List<Link> links = new ArrayList<>();
        links.add(new Link("Campagna",   new ExternalResource(URL_CAMPAGNA)));
        links.add(new Link("Storico",   new ExternalResource(URL_STORICO)));
        links.add(new Link("Note", new ExternalResource(URL_NOTE)));
        return links.toArray((new Link[links.size()]));
    }

    public Link[] getAbbonamentoLinks() {
        List<Link> links = new ArrayList<>();
        links.add(new Link("Abbonamenti",  new ExternalResource(URL_ABBONAMENTI)));
        links.add(new Link("Spedizioni",  new ExternalResource(URL_SPEDIZIONI)));
        return links.toArray((new Link[links.size()]));
    }

    public Link[] getIncassoLinks() {
        List<Link> links = new ArrayList<>();
        links.add(new Link("Incassi", new ExternalResource(URL_INCASSI)));
        links.add(new Link("Versamenti", new ExternalResource(URL_VERSAMENTI)));
        return links.toArray((new Link[links.size()]));
    }

    public Link[] getUserLinks() {
        List<Link> links = new ArrayList<>();
        UserInfo loggedInUser = SecurityUtils.getCurrentUser(userInfoDao);
        if (loggedInUser.getRole() == Role.ADMIN ) {
            links.add(new Link("Amministrazione Utenti", new ExternalResource(URL_USER)));
        } 
        if (!(loggedInUser.getRole() == Role.LOCKED)) {
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
