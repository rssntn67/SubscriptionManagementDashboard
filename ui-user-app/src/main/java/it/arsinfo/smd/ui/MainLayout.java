package it.arsinfo.smd.ui;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.HighlightConditions;
import com.vaadin.flow.router.RouterLink;
import it.arsinfo.smd.ui.home.HomeView;

@CssImport("./styles/shared-styles.css")
public class MainLayout extends AppLayout {

    public MainLayout() {
        createHeader();
        createDrawer();
    }

    private void createHeader() {
        H1 logo = new H1("Portale Riviste ADP");
        logo.addClassName("logo");
        Anchor logout = new Anchor("logout", "Log out");

        HorizontalLayout header = new HorizontalLayout(new DrawerToggle(), logo,logout);
        header.expand(logo);
        header.setDefaultVerticalComponentAlignment(
                FlexComponent.Alignment.CENTER);
        header.setWidth("50%");
        header.addClassName("header");

        addToNavbar(header);

    }

    private void createDrawer() {
        VerticalLayout menu = new VerticalLayout();

        RouterLink homeLink = new RouterLink("Home", HomeView.class);
        homeLink.setHighlightCondition(HighlightConditions.sameLocation());

        menu.add(homeLink);

        addToDrawer(menu);
    }
}