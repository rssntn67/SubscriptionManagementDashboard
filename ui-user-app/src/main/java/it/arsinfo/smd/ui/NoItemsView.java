package it.arsinfo.smd.ui;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.Shortcuts;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import it.arsinfo.smd.dao.AnagraficaDao;
import it.arsinfo.smd.dao.UserInfoDao;
import it.arsinfo.smd.data.UserSession;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

@Route("no-items")
public class NoItemsView extends VerticalLayout {

    @Autowired
    private UserSession userSession;

    @Autowired
    private UserInfoDao userInfoDao;

    @Autowired
    private AnagraficaDao anagraficaDao;

    private final Div content = new Div();
    private final TextField userCode = new TextField("codice cliente");

    @PostConstruct
    public void init() {
        H1 logo = new H1("Portale Riviste ADP");
        logo.addClassName("logo");

        HorizontalLayout header = new HorizontalLayout(new DrawerToggle(), logo);
        header.expand(logo);
        header.setDefaultVerticalComponentAlignment(
        FlexComponent.Alignment.CENTER);
        header.setWidth("50%");
        header.addClassName("header");

        Div div = new Div();
        div.setText("Benvenuto " + userSession.getUser().getEmail());
        div.getElement().getStyle().set("font-size", "xx-large");

        // Spring maps the 'logout' url so we should ignore it
        Anchor logout = new Anchor("/logout", "Logout");
        logout.getElement().setAttribute("router-ignore", true);

        header.add(div,logout);

        content.setText("Nessuna anagrafica associata: Per continuare bisogna inserire il codice cliente");
        content.getElement().getStyle().set("font-size", "xx-large");

        userCode.setPlaceholder("inserire il codice cliente");

        Button attiva = new Button("Attiva");
        attiva.addClickListener( e -> activate(userCode.getValue()));

        add(header,content,userCode,attiva);

    }

    private void activate(String code) {
        Anagrafica remoteuser = anagraficaDao.findByCodeLineBase(code);
        if (remoteuser == null) {
            content.setText("Nessuna anagrafica associata a: " + userSession.getUser().getEmail() + "'. Inserire un valido codice cliente");
            userCode.clear();
            return;
        }

        Dialog dialog = new Dialog();
        dialog.add(new Text("Nessuna anagrafica associata a: "
                + userSession.getUser().getEmail()
                + "'in anagrafica a: '"+remoteuser.getCaption()+"'"));
        dialog.setCloseOnEsc(false);
        dialog.setCloseOnOutsideClick(false);

        Button confirmButton = new Button("Confirm", event -> {
            UserInfo remote = new UserInfo();
            remote.setUsername(userSession.getUser().getEmail());
            remote.setPasswordHash(code);
            remote.setRole(UserInfo.Role.ENDUSER);
            userInfoDao.save(remote);
            dialog.close();
            UI.getCurrent().navigate("");
        });
        Button cancelButton = new Button("Cancel", event -> dialog.close());
        Shortcuts.addShortcutListener(dialog, dialog::close, Key.ESCAPE);

        dialog.add(new Div( confirmButton, cancelButton));
        dialog.open();
    }
}
