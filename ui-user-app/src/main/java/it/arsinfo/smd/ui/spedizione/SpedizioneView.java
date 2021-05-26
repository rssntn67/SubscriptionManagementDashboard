package it.arsinfo.smd.ui.spedizione;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import it.arsinfo.smd.ui.MainLayout;

/**
 * A sample Vaadin view class.
 * <p>
 * To implement a Vaadin view just extend any Vaadin component and
 * use @Route annotation to announce it in a URL as a Spring managed
 * bean.
 * Use the @PWA annotation make the application installable on phones,
 * tablets and some desktop browsers.
 * <p>
 * A new instance of this class is created for every new user and every
 * browser tab/window.
 */

@Route(value="adp/spedizione", layout = MainLayout.class)
@PageTitle("Spedizione | ADP Portale")
public class SpedizioneView extends VerticalLayout {

    public SpedizioneView() {

        H1 header = new H1("Benvenuto nella User App dell' Apostolato della Preghiera");
        add(header);

    }
}
