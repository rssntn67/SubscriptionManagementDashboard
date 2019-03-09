package it.arsinfo.smd.vaadin.ui;

import java.util.EnumSet;

import com.vaadin.data.Binder;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import it.arsinfo.smd.data.CentroDiocesano;
import it.arsinfo.smd.data.Diocesi;
import it.arsinfo.smd.data.Paese;
import it.arsinfo.smd.data.Regione;
import it.arsinfo.smd.data.TitoloAnagrafica;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.repository.AnagraficaDao;
import it.arsinfo.smd.vaadin.model.SmdEditor;

public class AnagraficaEditor extends SmdEditor<Anagrafica> {

    /**
     * 
     */
    private static final long serialVersionUID = 4673834235533544936L;

    private final TextField cognome = new TextField("Cognome/Ragione Sociale");
    private final CheckBox inRegola = new CheckBox("In regola coi pagamenti");
    private final ComboBox<Diocesi> diocesi = new ComboBox<Diocesi>("Diocesi",
                                                                    EnumSet.allOf(Diocesi.class));
    private final ComboBox<Regione> regioneVescovi = new ComboBox<Regione>("Regione Vescovi",
                                                                           EnumSet.allOf(Regione.class));
    private final ComboBox<CentroDiocesano> centroDiocesano = new ComboBox<CentroDiocesano>("Centro Diocesano",
                                                                                            EnumSet.allOf(CentroDiocesano.class));
    private final ComboBox<TitoloAnagrafica> titolo = new ComboBox<TitoloAnagrafica>("Titolo",
                                                                                     EnumSet.allOf(TitoloAnagrafica.class));
    private final TextField nome = new TextField("Nome");
    private final TextField intestazione = new TextField("Intestazione");
    private final TextField indirizzo = new TextField("Indirizzo");
    private final TextField cap = new TextField("CAP");
    private final TextField citta = new TextField("Citta'");
    private final ComboBox<Paese> paese = new ComboBox<Paese>("Paese",
                                                              EnumSet.allOf(Paese.class));
    private final TextField email = new TextField("Email");
    private final TextField telefono = new TextField("Telefono");
    private final TextField cellulare = new TextField("Cellulare");
    private final TextField codfis = new TextField("Cod. Fis.");
    private final TextField piva = new TextField("P.Iva");
    private final TextField note = new TextField("Note");
    private final CheckBox presidenteDiocesano = new CheckBox("Pres. Diocesano");
    private final ComboBox<Regione> regionePresidenteDiocesano = new ComboBox<Regione>("Regione Pres. Diocesano",
                                                                                       EnumSet.allOf(Regione.class));
    private final CheckBox direttoreDiocesiano = new CheckBox("Dir. Diocesano");
    private final ComboBox<Regione> regioneDirettoreDiocesano = new ComboBox<Regione>("Regione Dir. Diocesano",
                                                                                      EnumSet.allOf(Regione.class));
    private final CheckBox direttoreZonaMilano = new CheckBox("Dir. Zona Milano");
    private final CheckBox consiglioNazionaleADP = new CheckBox("Cons. Naz. ADP");
    private final CheckBox presidenzaADP = new CheckBox("Pres. ADP");
    private final CheckBox direzioneADP = new CheckBox("Dir. ADP");
    private final CheckBox caricheSocialiADP = new CheckBox("Car. Soc. ADP");
    private final CheckBox delegatiRegionaliADP = new CheckBox("Del. Reg. ADP");
    private final CheckBox elencoMarisaBisi = new CheckBox("Elenco Marisa Bisi");
    private final CheckBox promotoreRegionale = new CheckBox("Prom. Reg.");

    public AnagraficaEditor(AnagraficaDao anagraficaDao) {
        super(anagraficaDao, new Binder<>(Anagrafica.class));

        HorizontalLayout riga1 = new HorizontalLayout(diocesi, regioneVescovi,
                                                      centroDiocesano);
        HorizontalLayout riga2 = new HorizontalLayout(titolo, cognome, nome,
                                                      intestazione);
        HorizontalLayout riga3 = new HorizontalLayout(indirizzo, cap, citta,
                                                      paese);
        HorizontalLayout riga4 = new HorizontalLayout(email, telefono,
                                                      cellulare, codfis, piva,
                                                      note);

        HorizontalLayout riga5 = new HorizontalLayout(new VerticalLayout(inRegola,
                                                                         new Label("Incarichi Diocesani"),
                                                                         new HorizontalLayout(presidenteDiocesano,
                                                                                              direttoreDiocesiano,
                                                                                              direttoreZonaMilano),
                                                                         new HorizontalLayout(regionePresidenteDiocesano,
                                                                                              regioneDirettoreDiocesano)),
                                                      new VerticalLayout(new Label("ADP:"),
                                                                         new HorizontalLayout(consiglioNazionaleADP,
                                                                                              presidenzaADP,
                                                                                              direzioneADP,
                                                                                              caricheSocialiADP,
                                                                                              delegatiRegionaliADP),
                                                                         new VerticalLayout(new Label("Altre Categorie:"),
                                                                                            elencoMarisaBisi,
                                                                                            promotoreRegionale)));

        addComponents(getActions(), riga1, riga2, riga3, riga4, riga5);
        setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);

        getBinder().forField(cognome).asRequired();
        getBinder().forField(email).withValidator(new EmailValidator("Immettere un indizzo di mail valido"));
        getBinder().bindInstanceFields(this);

        // Configure and style components
        setSpacing(true);

        diocesi.setItemCaptionGenerator(Diocesi::getDetails);

        setVisible(false);

    }

    @Override
    public void focus(boolean persisted, Anagrafica c) {
        cognome.focus();
    }

}
