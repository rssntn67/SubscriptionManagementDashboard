package it.arsinfo.smd.ui.subscription;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.Shortcuts;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import it.arsinfo.smd.data.UserSession;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.ui.MainLayout;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

@Route(value="adp/subscription", layout = MainLayout.class)
@PageTitle("Subscription | ADP Portale")
public class SubscriptionView extends VerticalLayout {

    @Autowired
    private UserSession userSession;

    private final Div content = new Div();
    private final TextField userCode = new TextField("codice cliente");

    @PostConstruct
    public void init() {
        content.setText("Inserire il codice cliente che si vuole gestire");

        userCode.setPlaceholder("inserire il codice cliente");

        Button attiva = new Button("Attiva");
        attiva.addClickListener( e -> activate(userCode.getValue()));

        Button disattiva = new Button("Rimuovi Iscrizione Portale");
        disattiva.addClickListener( e -> deactivate());

        add(content,userCode,attiva,disattiva);

    }

    private void deactivate() {
        Dialog dialog = new Dialog();
        dialog.add(new Text("Rimuovere account '"
                + userSession.getUser().getEmail()
                + "' ? Per riaccedere bisogna fare una nuova sottoscrizione"));
        dialog.setCloseOnEsc(false);
        dialog.setCloseOnOutsideClick(false);

        Button confirmButton = new Button("Confirm", event -> {
            try {
                userSession.unsubscribe();
                UI.getCurrent().navigate("/logout");
            } catch (Exception e) {
                Notification.show("Error: " +e.getLocalizedMessage());
                dialog.close();
                return;
            }
            dialog.close();
        });
        Button cancelButton = new Button("Cancel", event -> dialog.close());
        Shortcuts.addShortcutListener(dialog, dialog::close, Key.ESCAPE);

        dialog.add(new Div( confirmButton, cancelButton));
        dialog.open();

    }
    private void activate(String code) {
        Anagrafica remoteuser = userSession.findIntestatario(code);
        if (remoteuser == null) {
            content.setText("Nessuna anagrafica associata a: " + code + "'. Inserire un valido codice cliente");
            userCode.clear();
            return;
        }

        Dialog dialog = new Dialog();
        dialog.add(new Text("Associare '"
                + userSession.getUser().getEmail()
                + "'in anagrafica a: '"+remoteuser.getCaption()+"'"));
        dialog.setCloseOnEsc(false);
        dialog.setCloseOnOutsideClick(false);

        Button confirmButton = new Button("Confirm", event -> {
            try {
                userSession.add(code);
            } catch (Exception e) {
                Notification.show("Error: " +e.getLocalizedMessage());
                dialog.close();
                return;
            }
            dialog.close();
        });
        Button cancelButton = new Button("Cancel", event -> dialog.close());
        Shortcuts.addShortcutListener(dialog, dialog::close, Key.ESCAPE);

        dialog.add(new Div( confirmButton, cancelButton));
        dialog.open();
    }
}
