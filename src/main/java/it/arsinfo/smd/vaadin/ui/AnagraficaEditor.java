package it.arsinfo.smd.vaadin.ui;

import java.util.EnumSet;

import com.vaadin.data.Binder;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;

import it.arsinfo.smd.data.CentroDiocesano;
import it.arsinfo.smd.data.Diocesi;
import it.arsinfo.smd.data.Paese;
import it.arsinfo.smd.data.Regione;
import it.arsinfo.smd.data.TitoloAnagrafica;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.repository.AnagraficaDao;
import it.arsinfo.smd.vaadin.model.SmdEditor;

public class AnagraficaEditor extends SmdEditor<Anagrafica> {

    private final TextField cognome = new TextField("Cognome/Ragione Sociale");
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

        HorizontalLayout riga1 = new HorizontalLayout(diocesi, 
                                                      regioneVescovi,
                                                      centroDiocesano,
                                                      regionePresidenteDiocesano,
                                                      regioneDirettoreDiocesano
                                                      );
        HorizontalLayout riga2 = new HorizontalLayout(titolo, 
                                                      cognome, 
                                                      nome,
                                                      indirizzo, 
                                                      cap, 
                                                      citta,
                                                      intestazione
                                                      );
        HorizontalLayout riga3 = new HorizontalLayout(paese,
                                                      email, 
                                                      telefono,
                                                      cellulare, 
                                                      codfis, 
                                                      piva
                                                      );

        HorizontalLayout riga4 = new HorizontalLayout(presidenteDiocesano,
                                                      direttoreDiocesiano,
                                                      direttoreZonaMilano,
                                                      consiglioNazionaleADP,
                                                      presidenzaADP,
                                                      direzioneADP,
                                                      caricheSocialiADP,
                                                      delegatiRegionaliADP,
                                                      elencoMarisaBisi,
                                                      promotoreRegionale
                                                      );

        setComponents(getActions(), riga1, riga2, riga3, riga4);

        getBinder().forField(diocesi).asRequired();
        getBinder().forField(cognome).asRequired();
        getBinder().forField(email).withValidator(new EmailValidator("Immettere un indizzo di mail valido"));
        getBinder().bindInstanceFields(this);

        // Configure and style components
        diocesi.setItemCaptionGenerator(Diocesi::getDetails);

    }

    @Override
    public void focus(boolean persisted, Anagrafica c) {
        cognome.focus();
    }
}