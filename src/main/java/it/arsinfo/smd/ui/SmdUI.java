    package it.arsinfo.smd.ui;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.annotations.Push;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import it.arsinfo.smd.dao.repository.UserInfoDao;
import it.arsinfo.smd.entity.UserInfo;
import it.arsinfo.smd.entity.UserInfo.Role;
import it.arsinfo.smd.ui.security.SecurityUtils;
import it.arsinfo.smd.ui.vaadin.SmdChangeHandler;

@Push
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

    public final static String URL_ANAGRAFICA = "/anagrafica";
    public final static String URL_PUBBLICAZIONI = "/pubblicazioni";
    public final static String URL_SPESESPEDIZIONE = "/spese/spedizione";

    public final static String URL_STORICO = "/storico";
    public final static String URL_ABBONAMENTI = "/abbonamenti";
    public final static String URL_OFFERTE = "/offerte";
    public final static String URL_DDT = "/ddt";
    public final static String URL_SPEDIZIONI = "/spedizioni";
    public final static String URL_CAMPAGNA = "/campagna";
    public final static String URL_NOTE = "/note";
    public final static String URL_RIEPILOGO = "/riepilogo";

    public final static String URL_DISTINTA_VERSAMENTI = "/distinta";
    public final static String URL_UPLOAD_POSTE = "/upload/poste";
    public final static String URL_INCASSA_CODELINE = "/incassa/codeline";
    public final static String URL_INCASSA_VERSAMENTI = "/versamenti";
    public final static String URL_INCASSA_ABBONAMENTI = "/incassa/abbonamenti";
    public final static String URL_INCASSA_OFFERTA = "/incassa/offerte";
    public final static String URL_INCASSA_DDT = "/incassa/ddt";
    public final static String URL_VERSAMENTI_COMMITTENTI = "/versamenti/committenti";
    
    public final static String URL_TIPOGRAFIA = "/tipografo";
    public final static String URL_SPEDIZIONERE = "/spedizioniere";
    public final static String URL_ADPSEDE = "/adpsede";
    public final static String URL_ADMIN_USER = "/admin/user";
    public final static String URL_RESET_PASS = "/reset/pass";

    public static final String TITLE_HOME = "Home";
    public final static String TITLE_ANAGRAFICA = "Anagrafica";
    public final static String TITLE_PUBBLICAZIONI = "Pubblicazioni";
    
    public final static String TITLE_SPESESPEDIZIONE = "Spese Spedizione";
    public final static String TITLE_ABBONAMENTI = "Abbonamenti";
    public final static String TITLE_OFFERTE = "Offerte";
    public final static String TITLE_DDT = "DDT";
    public final static String TITLE_STORICO = "Storici";
    public final static String TITLE_SPEDIZIONI = "Spedizioni";
    public final static String TITLE_CAMPAGNA = "Campagne";
    public final static String TITLE_NOTE = "Note";
    public final static String TITLE_RIEPILOGO = "Riepilogo";

    public final static String TITLE_DISTINTA_VERSAMENTI = "Distinta Versamenti";
    public final static String TITLE_INCASSA_VERSAMENTI = "Incassa Versamenti";
    public final static String TITLE_VERSAMENTI_COMMITTENTI = "Committenti Versamenti";
    public final static String TITLE_INCASSA_ABBONAMENTI = "Incassa Abbonamenti";
    public final static String TITLE_INCASSA_OFFERTA = "Incassa Offerte";
    public final static String TITLE_INCASSA_DDT = "Incassa DDT";
    public final static String TITLE_UPLOAD_POSTE = "Importa ccp";
    public final static String TITLE_INCASSA_CODELINE = "Incassa Codeline";
    
    public final static String TITLE_TIPOGRAFIA = "Tipografo";
    public final static String TITLE_SPEDIZIONERE = "Spedizioniere";
    public final static String TITLE_ADPSEDE = "Sede";
    public final static String TITLE_ADMIN_USER = "Amministrazione Utenti";
    public final static String TITLE_RESET_PASS = "Reset Password";

    private UserInfo loggedInUser;
    protected void init(VaadinRequest request, String head) {

        loggedInUser = SecurityUtils.getCurrentUser(userInfoDao);
        header.setValue(head);
        layout.addComponent(menu);
        layout.addComponent(header);
        setContent(layout);
        
        menu.addItem(TITLE_HOME,new MenuBar.Command() {
            private static final long serialVersionUID = 1L;
            
            public void menuSelected(MenuItem selectedItem) {
                getUI().getPage().setLocation(HOME);;
            }
        });

        MenuItem anagrafiche = menu.addItem("Gestione Anagrafiche",null);
        anagrafiche.addItem(TITLE_ANAGRAFICA,new MenuBar.Command() {
            private static final long serialVersionUID = 1L;
            
            public void menuSelected(MenuItem selectedItem) {
                getUI().getPage().setLocation(URL_ANAGRAFICA);
            }
        });
        anagrafiche.addItem(TITLE_PUBBLICAZIONI,new MenuBar.Command() {
            private static final long serialVersionUID = 1L;
            
            public void menuSelected(MenuItem selectedItem) {
                getUI().getPage().setLocation(URL_PUBBLICAZIONI);
            }
        });
        anagrafiche.addItem(TITLE_SPESESPEDIZIONE,new MenuBar.Command() {
            private static final long serialVersionUID = 1L;
            
            public void menuSelected(MenuItem selectedItem) {
                getUI().getPage().setLocation(URL_SPESESPEDIZIONE);
            }
        });

        MenuItem abbonamenti = menu.addItem("Gestione Abbonamento",null);
        abbonamenti.addItem(TITLE_ABBONAMENTI,new MenuBar.Command() {
            private static final long serialVersionUID = 1L;
            
            public void menuSelected(MenuItem selectedItem) {
                getUI().getPage().setLocation(URL_ABBONAMENTI);
            }
        } );
        abbonamenti.addItem(TITLE_OFFERTE,new MenuBar.Command() {
            private static final long serialVersionUID = 1L;
            
            public void menuSelected(MenuItem selectedItem) {
                getUI().getPage().setLocation(URL_OFFERTE);
            }
        } );
        abbonamenti.addItem(TITLE_DDT,new MenuBar.Command() {
            private static final long serialVersionUID = 1L;
            
            public void menuSelected(MenuItem selectedItem) {
                getUI().getPage().setLocation(URL_DDT);
            }
        } );
        abbonamenti.addItem(TITLE_SPEDIZIONI,new MenuBar.Command() {
            private static final long serialVersionUID = 1L;
            
            public void menuSelected(MenuItem selectedItem) {
                getUI().getPage().setLocation(URL_SPEDIZIONI);
            }
        } );
        abbonamenti.addItem(TITLE_CAMPAGNA,new MenuBar.Command() {
            private static final long serialVersionUID = 1L;
            
            public void menuSelected(MenuItem selectedItem) {
                getUI().getPage().setLocation(URL_CAMPAGNA);
            }
        } );
        abbonamenti.addItem(TITLE_STORICO,new MenuBar.Command() {
            private static final long serialVersionUID = 1L;
            
            public void menuSelected(MenuItem selectedItem) {
                getUI().getPage().setLocation(URL_STORICO);
            }
        } );
        abbonamenti.addItem(TITLE_NOTE,new MenuBar.Command() {
            private static final long serialVersionUID = 1L;
            
            public void menuSelected(MenuItem selectedItem) {
                getUI().getPage().setLocation(URL_NOTE);
            }
        } );
        abbonamenti.addItem(TITLE_RIEPILOGO,new MenuBar.Command() {
            private static final long serialVersionUID = 1L;
            
            public void menuSelected(MenuItem selectedItem) {
                getUI().getPage().setLocation(URL_RIEPILOGO);
            }
        } );
        
        MenuItem incassi = menu.addItem("Gestione Incassi",null);
        incassi.addItem(TITLE_DISTINTA_VERSAMENTI,new MenuBar.Command() {
            private static final long serialVersionUID = 1L;
            
            public void menuSelected(MenuItem selectedItem) {
                getUI().getPage().setLocation(URL_DISTINTA_VERSAMENTI);
            }
        } );
        incassi.addItem(TITLE_UPLOAD_POSTE,new MenuBar.Command() {
            private static final long serialVersionUID = 1L;
            
            public void menuSelected(MenuItem selectedItem) {
                getUI().getPage().setLocation(URL_UPLOAD_POSTE);
            }
        } );
        incassi.addItem(TITLE_INCASSA_CODELINE,new MenuBar.Command() {
            private static final long serialVersionUID = 1L;
            
            public void menuSelected(MenuItem selectedItem) {
                getUI().getPage().setLocation(URL_INCASSA_CODELINE);
            }
        } );
        incassi.addItem(TITLE_INCASSA_VERSAMENTI,new MenuBar.Command() {
            private static final long serialVersionUID = 1L;
            
            public void menuSelected(MenuItem selectedItem) {
                getUI().getPage().setLocation(URL_INCASSA_VERSAMENTI);
            }
        } );
        incassi.addItem(TITLE_INCASSA_ABBONAMENTI,new MenuBar.Command() {
            private static final long serialVersionUID = 1L;
            
            public void menuSelected(MenuItem selectedItem) {
                getUI().getPage().setLocation(URL_INCASSA_ABBONAMENTI);
            }
        } );
        incassi.addItem(TITLE_INCASSA_OFFERTA,new MenuBar.Command() {
            private static final long serialVersionUID = 1L;
            
            public void menuSelected(MenuItem selectedItem) {
                getUI().getPage().setLocation(URL_INCASSA_OFFERTA);
            }
        } );
        incassi.addItem(TITLE_INCASSA_DDT,new MenuBar.Command() {
            private static final long serialVersionUID = 1L;
            
            public void menuSelected(MenuItem selectedItem) {
                getUI().getPage().setLocation(URL_INCASSA_DDT);
            }
        } );
        incassi.addItem(TITLE_VERSAMENTI_COMMITTENTI,new MenuBar.Command() {
            private static final long serialVersionUID = 1L;
            
            public void menuSelected(MenuItem selectedItem) {
                getUI().getPage().setLocation(URL_VERSAMENTI_COMMITTENTI);
            }
        } );

        MenuItem ordini = menu.addItem("Gestione Ordini",null);
        ordini.addItem(TITLE_TIPOGRAFIA ,new MenuBar.Command() {
            
            private static final long serialVersionUID = 1L;
            
            public void menuSelected(MenuItem selectedItem) {
                getUI().getPage().setLocation(URL_TIPOGRAFIA);
            }
        });

        ordini.addItem(TITLE_SPEDIZIONERE ,new MenuBar.Command() {
            
            private static final long serialVersionUID = 1L;
            
            public void menuSelected(MenuItem selectedItem) {
                getUI().getPage().setLocation(URL_SPEDIZIONERE);
            }
        });

        ordini.addItem(TITLE_ADPSEDE ,new MenuBar.Command() {
            
            private static final long serialVersionUID = 1L;
            
            public void menuSelected(MenuItem selectedItem) {
                getUI().getPage().setLocation(URL_ADPSEDE);
            }
        });

        menu.addItem("Logout: "+ loggedInUser.getUsername(),new MenuBar.Command() {
            private static final long serialVersionUID = 1L;
            
            public void menuSelected(MenuItem selectedItem) {
                getUI().getPage().setLocation(URL_LOGOUT);
            }
        } );
       if (loggedInUser.getRole() == Role.ADMIN ) {
           menu.addItem(TITLE_ADMIN_USER,new MenuBar.Command() {
                private static final long serialVersionUID = 1L;
                
                public void menuSelected(MenuItem selectedItem) {
                    getUI().getPage().setLocation(URL_ADMIN_USER);
                }
            } );
        } 
        if (!(loggedInUser.getRole() == Role.LOCKED)) {
            menu.addItem(TITLE_RESET_PASS,new MenuBar.Command() {
                private static final long serialVersionUID = 1L;
                
                public void menuSelected(MenuItem selectedItem) {
                    getUI().getPage().setLocation(URL_RESET_PASS);
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
    
    public static Link getHomePageLink() {
        return new Link(TITLE_HOME,new ExternalResource(HOME));        
    }
    
    public static Link[] getAnagraficaLink() {
    	List<Link> links = new ArrayList<>();
    	links.add(new Link(TITLE_ANAGRAFICA, new ExternalResource(URL_ANAGRAFICA)));
    	links.add(new Link(TITLE_PUBBLICAZIONI,new ExternalResource(URL_PUBBLICAZIONI)));    
    	links.add(new Link(TITLE_SPESESPEDIZIONE,new ExternalResource(URL_SPESESPEDIZIONE)));    
    	return links.toArray(new Link[links.size()]);
    }

    public static Link[] getOrdiniLinks() {
        List<Link> links = new ArrayList<>();
        links.add(new Link(TITLE_TIPOGRAFIA, new ExternalResource(URL_TIPOGRAFIA)));
        links.add(new Link(TITLE_SPEDIZIONERE, new ExternalResource(URL_SPEDIZIONERE)));
        links.add(new Link(TITLE_ADPSEDE, new ExternalResource(URL_ADPSEDE)));
        return links.toArray(new Link[links.size()]);
    }

    public static Link[] getAbbonamentoLinks() {
        List<Link> links = new ArrayList<>();
        links.add(new Link(TITLE_ABBONAMENTI,  new ExternalResource(URL_ABBONAMENTI)));
        links.add(new Link(TITLE_OFFERTE,  new ExternalResource(URL_OFFERTE)));
        links.add(new Link(TITLE_SPEDIZIONI,  new ExternalResource(URL_SPEDIZIONI)));
        links.add(new Link(TITLE_CAMPAGNA,   new ExternalResource(URL_CAMPAGNA)));
        links.add(new Link(TITLE_STORICO,   new ExternalResource(URL_STORICO)));
        links.add(new Link(TITLE_NOTE, new ExternalResource(URL_NOTE)));
        links.add(new Link(TITLE_RIEPILOGO, new ExternalResource(URL_RIEPILOGO)));
        return links.toArray((new Link[links.size()]));
    }

    public static Link[] getIncassoLinks() {
        List<Link> links = new ArrayList<>();
        links.add(new Link(TITLE_DISTINTA_VERSAMENTI, new ExternalResource(URL_DISTINTA_VERSAMENTI)));
        links.add(new Link(TITLE_UPLOAD_POSTE, new ExternalResource(URL_UPLOAD_POSTE)));
        links.add(new Link(TITLE_INCASSA_CODELINE, new ExternalResource(URL_INCASSA_CODELINE)));
        links.add(new Link(TITLE_INCASSA_VERSAMENTI, new ExternalResource(URL_INCASSA_VERSAMENTI)));
        links.add(new Link(TITLE_INCASSA_ABBONAMENTI, new ExternalResource(URL_INCASSA_ABBONAMENTI)));
        links.add(new Link(TITLE_INCASSA_OFFERTA, new ExternalResource(URL_INCASSA_OFFERTA)));
        links.add(new Link(TITLE_VERSAMENTI_COMMITTENTI, new ExternalResource(URL_VERSAMENTI_COMMITTENTI)));
        return links.toArray((new Link[links.size()]));
    }

    public Link getLogoutLink() {
    	return new Link("Logout: "+ loggedInUser.getUsername(), new ExternalResource(URL_LOGOUT));
    }
    public UserInfo getLoggedInUser() {
        return loggedInUser;
    }

    public void setLoggedInUser(UserInfo loggedInUser) {
        this.loggedInUser = loggedInUser;
    }

}
