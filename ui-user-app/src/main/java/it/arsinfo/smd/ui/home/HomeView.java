package it.arsinfo.smd.ui.home;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.Shortcuts;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import it.arsinfo.smd.data.UserSession;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.ui.MainLayout;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.ArrayList;

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

@Route(value="", layout = MainLayout.class)
@PageTitle("Home | ADP Portale")
public class HomeView extends VerticalLayout implements BeforeEnterObserver {

    @Autowired
    private UserSession userSession;

    private final Div content = new Div();

    private final TextField userCode = new TextField("codice cliente");
    private final Grid<Anagrafica> grid = new Grid<>(Anagrafica.class);

    private final Button attiva = new Button("Attiva");
    private final Button disattiva = new Button("Rimuovi Iscrizione Portale");

    private final Label gridLabel = new Label("Sottoscrizioni effettuate");
    @PostConstruct
    public void init() {
        Div idic = new Div();
        idic.setText("Benvenuto nel Portale Adp: " + userSession.getUser().getProvider() + " "+userSession.getUser().getEmail());
        userCode.setPlaceholder("inserire il codice cliente");
        grid.addClassName("grid");
        grid.setColumns("nome","denominazione","indirizzo","citta");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));

        attiva.addClickListener( e -> activate(userCode.getValue()));
        disattiva.addClickListener( e -> deactivate());

        add(idic,disattiva,content,userCode,attiva,gridLabel,grid);
    }

    private void activate(String code) {
        Anagrafica remoteuser = userSession.findIntestatario(code);
        if (remoteuser == null) {
            content.setText("Nessuna anagrafica associabile a: '" + code + "'. Inserire un codice cliente valido");
            return;
        }

        Dialog dialog = new Dialog();
        dialog.add(new Text("Associare '"
                + userSession.getUser().getEmail()
                + "'in anagrafica a: '"+remoteuser.getCaption()+"' In questo modo accetti le condizioni di uso del sito e le private policy"));
        dialog.setCloseOnEsc(false);
        dialog.setCloseOnOutsideClick(false);

        Button confirmButton = new Button("Confirm", event -> {
            try {
                userSession.save(code);
            } catch (Exception e) {
                Notification.show("Error: " +e.getLocalizedMessage());
                dialog.close();
                return;
            }
            dialog.close();
            UI.getCurrent().navigate("");
        });
        Button cancelButton = new Button("Cancel", event -> dialog.close());
        Shortcuts.addShortcutListener(dialog, dialog::close, Key.ESCAPE);

        dialog.add(new Div( confirmButton, cancelButton));
        dialog.open();
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
                UI.getCurrent().getPage().setLocation("/logout");
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


    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        if (userSession.getLoggedIn() == null) {
            disattiva.setVisible(false);
            content.setText("Nessuna anagrafica associata! Inserisci il tuo codice cliente");
            gridLabel.setVisible(false);
            grid.setItems(new ArrayList<>());
            grid.setVisible(false);
        } else {
            disattiva.setVisible(true);
            content.setText("Aggiungi codice cliente");
            gridLabel.setVisible(true);
            grid.setItems(userSession.getSubscriptions());
            grid.setVisible(true);
        }

    }
}
