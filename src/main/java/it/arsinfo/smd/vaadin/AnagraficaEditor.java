package it.arsinfo.smd.vaadin;

import java.util.EnumSet;

import com.vaadin.data.Binder;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import it.arsinfo.smd.data.CentroDiocesano;
import it.arsinfo.smd.data.Diocesi;
import it.arsinfo.smd.data.Regione;
import it.arsinfo.smd.data.TitoloAnagrafica;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Paese;
import it.arsinfo.smd.repository.AnagraficaDao;


public class AnagraficaEditor extends SmdEditor {

    /**
     * 
     */
    private static final long serialVersionUID = 4673834235533544936L;

    private final AnagraficaDao repo;

    /**
     * The currently edited customer
     */
    private Anagrafica customer;

    private final CheckBox inRegola = new CheckBox("In regola coi pagamenti");
    HorizontalLayout riga0 = new HorizontalLayout(inRegola);

    private final ComboBox<Diocesi> diocesi = new ComboBox<Diocesi>("Diocesi",
                                                                    EnumSet.allOf(Diocesi.class));
    private final ComboBox<Regione> regioneVescovi = new ComboBox<Regione>("Regione Vescovi",
                                                                           EnumSet.allOf(Regione.class));
    private final ComboBox<CentroDiocesano> centroDiocesano = new ComboBox<CentroDiocesano>("Centro Diocesano",
                                                                                            EnumSet.allOf(CentroDiocesano.class));
    HorizontalLayout riga1 = new HorizontalLayout(diocesi, regioneVescovi,
                                                  centroDiocesano);

    private final ComboBox<TitoloAnagrafica> titolo = new ComboBox<TitoloAnagrafica>("Titolo",
                                                                                     EnumSet.allOf(TitoloAnagrafica.class));
    private final TextField nome = new TextField("Nome");
    private final TextField cognome = new TextField("Cognome/Ragione Sociale");
    private final TextField intestazione = new TextField("Intestazione");
    HorizontalLayout riga2 = new HorizontalLayout(titolo, cognome, nome,
                                                  intestazione);

    private final TextField indirizzo = new TextField("Indirizzo");
    private final TextField cap = new TextField("CAP");
    private final TextField citta = new TextField("Citta'");
    private final ComboBox<Paese> paese = new ComboBox<Paese>("Paese",
                                                              EnumSet.allOf(Paese.class));
    HorizontalLayout riga3 = new HorizontalLayout(indirizzo, cap, citta,
                                                  paese);

    private final TextField email = new TextField("Email");
    private final TextField telefono = new TextField("Telefono");
    private final TextField cellulare = new TextField("Cellulare");
    private final TextField codfis = new TextField("Cod. Fis.");
    private final TextField piva = new TextField("P.Iva");
    private final TextField note = new TextField("Note");
    HorizontalLayout riga4 = new HorizontalLayout(email, telefono, cellulare,
                                                  codfis, piva, note);

    private final CheckBox presidenteDiocesano = new CheckBox("Pres. Diocesano");
    private final ComboBox<Regione> regionePresidenteDiocesano = new ComboBox<Regione>("Regione Pres. Diocesano",
            EnumSet.allOf(Regione.class));
    private final CheckBox direttoreDiocesiano = new CheckBox("Dir. Diocesano");
    private final ComboBox<Regione> regioneDirettoreDiocesano = new ComboBox<Regione>("Regione Dir. Diocesano",
            EnumSet.allOf(Regione.class));
    private final CheckBox direttoreZonaMilano = new CheckBox("Dir. Zona Milano");
    VerticalLayout riga5 = new VerticalLayout(new Label("Incarichi Diocesani"),
                                              new HorizontalLayout(
                                                  presidenteDiocesano,
                                                  direttoreDiocesiano,
                                                  direttoreZonaMilano
                                                  ), new HorizontalLayout(regionePresidenteDiocesano,regioneDirettoreDiocesano));

    private final CheckBox consiglioNazionaleADP = new CheckBox("Cons. Naz. ADP"); 
    private final CheckBox presidenzaADP = new CheckBox("Pres. ADP"); 
    private final CheckBox direzioneADP = new CheckBox("Dir. ADP"); 
    private final CheckBox caricheSocialiADP = new CheckBox("Car. Soc. ADP"); 
    private final CheckBox delegatiRegionaliADP = new CheckBox("Del. Reg. ADP");
    VerticalLayout riga6 = new VerticalLayout(new Label("ADP:"), new HorizontalLayout(
                                                                                      consiglioNazionaleADP,
                                                                                      presidenzaADP, 
                                                                                      direzioneADP,
                                                                                      caricheSocialiADP,
                                                                                      delegatiRegionaliADP));

    private final CheckBox elencoMarisaBisi = new CheckBox("Elenco Marisa Bisi"); 
    private final CheckBox promotoreRegionale = new CheckBox("Prom. Reg."); 
    VerticalLayout riga7 = new VerticalLayout(new Label("Altre Categorie:"), new HorizontalLayout(elencoMarisaBisi,
                                                  promotoreRegionale));

    Button save = new Button("Save", VaadinIcons.CHECK);
    Button cancel = new Button("Cancel");
    Button delete = new Button("Delete", VaadinIcons.TRASH);

    HorizontalLayout actions = new HorizontalLayout(save, cancel, delete);

    Binder<Anagrafica> binder = new Binder<>(Anagrafica.class);

    public AnagraficaEditor(AnagraficaDao repo) {

        this.repo = repo;

        addComponents(riga0, riga1, riga2, riga3, riga4, riga5, riga6, riga7,
                      actions);
        setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);

        binder.forField(cognome).asRequired();
        binder.forField(email).withValidator(new EmailValidator("Immettere un indizzo di mail valido"));
        binder.bindInstanceFields(this);

        // Configure and style components
        setSpacing(true);

        diocesi.setItemCaptionGenerator(Diocesi::getDetails);

        save.addStyleName(ValoTheme.BUTTON_PRIMARY);
        delete.addStyleName(ValoTheme.BUTTON_DANGER);

        save.addClickListener(e -> save());
        delete.addClickListener(e -> delete());
        cancel.addClickListener(e -> edit(customer));
        setVisible(false);

    }

    void delete() {
        repo.delete(customer);
        changeHandler.onChange();
    }

    void save() {
        repo.save(customer);
        changeHandler.onChange();
    }

    public final void edit(Anagrafica c) {
        if (c == null) {
            setVisible(false);
            return;
        }
        final boolean persisted = c.getId() != null;
        if (persisted) {
            // Find fresh entity for editing
            customer = repo.findById(c.getId()).get();
        } else {
            customer = c;
        }
        cancel.setVisible(persisted);

        // Bind customer properties to similarly named fields
        // Could also use annotation or "manual binding" or programmatically
        // moving values from fields to entities before saving
        binder.setBean(customer);

        setVisible(true);

        // Focus first name initially
        cognome.focus();
    }

}
