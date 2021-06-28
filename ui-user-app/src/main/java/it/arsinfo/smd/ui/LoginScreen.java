package it.arsinfo.smd.ui;

import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

/**
 * Adds an explicit link that the user has to click to login.
 */
@Route("login")
@PageTitle("Login")
public class LoginScreen extends FlexLayout {

    private static final String GOOGLE_URL = "/oauth2/authorization/google";
    private static final String FACEBOOK_URL = "/oauth2/authorization/facebook";

    /**
     * This methods gets the user into google sign in page.
     */
    public LoginScreen() {
        HorizontalLayout gl = new HorizontalLayout();
        gl.setSpacing(true);
        gl.setMargin(true);
        Icon gicon = new Icon(VaadinIcon.GOOGLE_PLUS);
        Anchor googleLoginButton = new Anchor(GOOGLE_URL, "Login with Google");
        gl.add(gicon,googleLoginButton);

        HorizontalLayout fl = new HorizontalLayout();
        fl.setSpacing(true);
        fl.setMargin(true);
        Icon ficon = new Icon(VaadinIcon.FACEBOOK);
        Anchor facebookLoginButton = new Anchor(FACEBOOK_URL, "Login with Facebook");
        fl.add(ficon,facebookLoginButton);

        VerticalLayout vl =  new VerticalLayout();
        vl.setSpacing(true);
        vl.setMargin(true);
        vl.add(new H3("Portale ADP - Scegliere un provider di autenticazione"));
        vl.add(new H1(""));
        vl.add(gl,fl);
        vl.setAlignItems(Alignment.CENTER);

        add(vl);
    }
}