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
import it.arsinfo.smd.data.UserSession;
import it.arsinfo.smd.entity.Anno;
import it.arsinfo.smd.entity.Campagna;
import it.arsinfo.smd.entity.StatoCampagna;
import it.arsinfo.smd.service.api.StoricoService;
import it.arsinfo.smd.ui.abbonamento.AbbonamentoView;
import it.arsinfo.smd.ui.anagrafica.AnagraficaView;
import it.arsinfo.smd.ui.campagna.CampagnaStoricoView;
import it.arsinfo.smd.ui.campagna.CampagnaView;
import it.arsinfo.smd.ui.home.HomeView;
import it.arsinfo.smd.ui.offerta.OffertaView;
import it.arsinfo.smd.ui.spedizione.SpedizioneView;
import it.arsinfo.smd.ui.storico.StoricoView;
import it.arsinfo.smd.ui.terms.PolicyView;
import it.arsinfo.smd.ui.terms.TermsView;
import it.arsinfo.smd.ui.versamento.VersamentoView;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@CssImport("./styles/shared-styles.css")
public class MainLayout extends AppLayout implements BeforeEnterObserver {

    @Autowired
    private UserSession userSession;

    @Autowired
    private StoricoService storicoService;

    private boolean first=true;

    private final RouterLink homeLink = new RouterLink("Home", HomeView.class);
    private final RouterLink termsLink = new RouterLink("Termini e Condizioni di Vendita", TermsView.class);
    private final RouterLink privacyLink = new RouterLink("Privacy Policy", PolicyView.class);

    private final RouterLink anagraficaLink = new RouterLink("Anagrafica", AnagraficaView.class);
    private final RouterLink campagnaStoricoLink= new RouterLink("Campagna " + Anno.getAnnoProssimo().getAnnoAsString(), CampagnaStoricoView.class);
    private final RouterLink campagnaLink = new RouterLink("Campagna " + Anno.getAnnoProssimo().getAnnoAsString(), CampagnaView.class);
    private final RouterLink abbonamentoLink = new RouterLink("Campagna " + Anno.getAnnoCorrente().getAnnoAsString(), AbbonamentoView.class);
    private final RouterLink storicoLink = new RouterLink("Campagna " + Anno.getAnnoProssimo().getAnnoAsString(), StoricoView.class);
    private final RouterLink offertaLink = new RouterLink("Offerte", OffertaView.class);
    private final RouterLink spedizioneLink = new RouterLink("Spedizioni", SpedizioneView.class);
    private final RouterLink versamentoLink = new RouterLink("Versamenti", VersamentoView.class);

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        VerticalLayout menu = new VerticalLayout();
        if (first) {
            createHeader();
            menu.add(homeLink);
            menu.add(anagraficaLink);
            menu.add(createMenuCampagna());
            menu.add(spedizioneLink);
            menu.add(versamentoLink);
            menu.add(offertaLink);
            menu.add(termsLink);
            menu.add(privacyLink);

            homeLink.setHighlightCondition(HighlightConditions.sameLocation());
            termsLink.setHighlightCondition(HighlightConditions.sameLocation());
            privacyLink.setHighlightCondition(HighlightConditions.sameLocation());

            anagraficaLink.setHighlightCondition(HighlightConditions.sameLocation());
            campagnaStoricoLink.setHighlightCondition(HighlightConditions.sameLocation());
            campagnaLink.setHighlightCondition(HighlightConditions.sameLocation());
            storicoLink.setHighlightCondition(HighlightConditions.sameLocation());
            abbonamentoLink.setHighlightCondition(HighlightConditions.sameLocation());
            spedizioneLink.setHighlightCondition(HighlightConditions.sameLocation());
            versamentoLink.setHighlightCondition(HighlightConditions.sameLocation());
            offertaLink.setHighlightCondition(HighlightConditions.sameLocation());
            addToDrawer(menu);
            first=false;
        }

        if (userSession.getLoggedIn() == null) {
            anagraficaLink.setVisible(false);
            campagnaStoricoLink.setVisible(false);
            campagnaLink.setVisible(false);
            abbonamentoLink.setVisible(false);
            storicoLink.setVisible(false);
            offertaLink.setVisible(false);
            spedizioneLink.setVisible(false);
            versamentoLink.setVisible(false);
        } else {
            anagraficaLink.setVisible(true);
            campagnaStoricoLink.setVisible(true);
            campagnaLink.setVisible(true);
            abbonamentoLink.setVisible(true);
            storicoLink.setVisible(true);
            offertaLink.setVisible(true);
            spedizioneLink.setVisible(true);
            versamentoLink.setVisible(true);
        }
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

    private RouterLink[] createMenuCampagna() {
        List<RouterLink> components = new ArrayList<>();
        int i = 0;

        Campagna futureC = storicoService.getByAnno(Anno.getAnnoProssimo());
        if (futureC != null) {
            if (futureC.getStatoCampagna() == StatoCampagna.Generata || futureC.getStatoCampagna() == StatoCampagna.Inviata) {
                components.add(i++,campagnaStoricoLink);
            } else {
                components.add(i++, campagnaLink);
            }
        } else {
            components.add(i++,storicoLink);
        }

        if (storicoService.getByAnno(Anno.getAnnoCorrente()) != null) {
            components.add(i,abbonamentoLink);
        }
        return components.toArray(new RouterLink[0]);
    }

}