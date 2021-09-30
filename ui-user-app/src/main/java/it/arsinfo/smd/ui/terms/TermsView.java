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

@Route(value="termini-econdizioni-vendita", layout = MainLayout.class)
@PageTitle("Termini e Condizioni | ADP Portale")
public class TermsView extends VerticalLayout {
    public TermsView() {
        H2 header = new H2("Termini e condizioni di utilizzo di areariservata.apostolatodellapreghiera.it");
        Anchor anchor = new Anchor("https://www.retepreghierapapa.it/termini-e-condizioni/","I termini corrispondono a quelle del sito retepreghierapapa.it");
        anchor.getElement().setAttribute("router-ignore", true);
        Label info = new Label("E' possibile visualizzare i termini scorrendo il seguente frame");
        IFrame external = new IFrame("https://www.retepreghierapapa.it/termini-e-condizioni/");
        external.setWidth("90%");
        external.setHeight(20, Unit.CM);
        add(header,anchor,info,external);


    }
}
