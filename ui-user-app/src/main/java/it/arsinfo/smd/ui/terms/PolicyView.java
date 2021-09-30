package it.arsinfo.smd.ui.terms;

import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.IFrame;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import it.arsinfo.smd.ui.MainLayout;

@Route(value="privacy-policy", layout = MainLayout.class)
@PageTitle("Privacy Policy | ADP Portale")
public class PolicyView extends VerticalLayout {

    public PolicyView() {
        H2 header = new H2("Privacy Policy di areariservata.apostolatodellapreghiera.it");
        Anchor anchor = new Anchor("https://www.retepreghierapapa.it/privacy-policy/","Le policy corrispondono a quelle del sito retepreghierapapa.it");
        anchor.getElement().setAttribute("router-ignore", true);
        Label info = new Label("E' possibile visualizzare i termini scorrendo il seguente frame");
        IFrame external = new IFrame("https://www.retepreghierapapa.it/privacy-policy/");
        external.setWidth("90%");
        external.setHeight(20, Unit.CM);
        add(header,anchor,info,external);
    }
}
