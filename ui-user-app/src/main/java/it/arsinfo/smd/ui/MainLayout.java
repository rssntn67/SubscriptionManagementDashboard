package it.arsinfo.smd.ui;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.HighlightConditions;
import com.vaadin.flow.router.RouterLink;
import it.arsinfo.smd.data.UserSession;
import it.arsinfo.smd.ui.anagrafica.AnagraficaView;
import it.arsinfo.smd.ui.home.HomeView;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

@CssImport("./styles/shared-styles.css")
public class MainLayout extends AppLayout {

    @Autowired
    UserSession userSession;

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
        div.setText("Hello " + userSession.getUser().getFirstName() + " " + userSession.getUser().getLastName());
        div.getElement().getStyle().set("font-size", "xx-large");

        Image image = new Image(userSession.getUser().getPicture(), "User Image");

        // Spring maps the 'logout' url so we should ignore it
        Anchor logout = new Anchor("/logout", "Logout");
        logout.getElement().setAttribute("router-ignore", true);

        header.add(div,image,logout);
        addToNavbar(header);

    }

    private void createDrawer() {
        VerticalLayout menu = new VerticalLayout();

        RouterLink homeLink = new RouterLink("Home", HomeView.class);
        homeLink.setHighlightCondition(HighlightConditions.sameLocation());

        RouterLink anagraficaLink = new RouterLink("Anagrafica", AnagraficaView.class);
        anagraficaLink.setHighlightCondition(HighlightConditions.sameLocation());

        menu.add(homeLink,anagraficaLink);

        addToDrawer(menu);
    }
}