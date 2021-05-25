package it.arsinfo.smd.ui;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.HighlightConditions;
import com.vaadin.flow.router.RouterLink;
import it.arsinfo.smd.dao.CampagnaDao;
import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.StatoCampagna;
import it.arsinfo.smd.data.UserSession;
import it.arsinfo.smd.entity.Campagna;
import it.arsinfo.smd.ui.anagrafica.AnagraficaView;
import it.arsinfo.smd.ui.campagna.CampagnaView;
import it.arsinfo.smd.ui.home.HomeView;
import it.arsinfo.smd.ui.subscription.SubscriptionView;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

@CssImport("./styles/shared-styles.css")
public class MainLayout extends AppLayout implements BeforeEnterObserver {

    @Autowired
    private UserSession userSession;

    @Autowired
    private CampagnaDao campagnaDao;

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        // implementation omitted
        if ( userSession.getLoggedIn() == null) {
            event.rerouteTo(NoItemsView.class);
        }
    }

    @PostConstruct
    public void init() {
        createHeader();
        createDrawer();
    }

    private void createHeader() {
        H1 logo = new H1("Portale Riviste ADP");
        logo.addClassName("logo");

        HorizontalLayout header = new HorizontalLayout(new DrawerToggle(), logo);
        header.expand(logo);
        header.setDefaultVerticalComponentAlignment(
                FlexComponent.Alignment.CENTER);
        header.setWidth("50%");
        header.addClassName("header");

        Div div = new Div();
        div.setText("Benvenuto " + userSession.getUser().getFirstName());

        // Spring maps the 'logout' url so we should ignore it
        Anchor logout = new Anchor("/logout", "Logout");
        logout.getElement().setAttribute("router-ignore", true);

        header.add(div,logout);
        addToNavbar(header);

    }

    private void createDrawer() {
        VerticalLayout menu = new VerticalLayout();

        RouterLink homeLink = new RouterLink("Home", HomeView.class);
        homeLink.setHighlightCondition(HighlightConditions.sameLocation());

        RouterLink anagraficaLink = new RouterLink("Anagrafica", AnagraficaView.class);
        anagraficaLink.setHighlightCondition(HighlightConditions.sameLocation());

        RouterLink campagnaLink = new RouterLink(getCampagnaLinkText(), CampagnaView.class);
        campagnaLink.setHighlightCondition(HighlightConditions.sameLocation());

        RouterLink subscriptionLink = new RouterLink("Subscription", SubscriptionView.class);
        subscriptionLink.setHighlightCondition(HighlightConditions.sameLocation());

        menu.add(homeLink,anagraficaLink,campagnaLink,subscriptionLink);

        addToDrawer(menu);
    }

    private String getCampagnaLinkText() {
        Campagna campagna = campagnaDao.findByAnno(Anno.getAnnoProssimo());
        if (campagna == null || campagna.getStatoCampagna() == StatoCampagna.Generata) {
            return "Campagna " + Anno.getAnnoProssimo();
        }
        return "Campagna " + Anno.getAnnoSuccessivo(Anno.getAnnoProssimo()).getAnno();
    }
}