package it.arsinfo.smd.vaadin;

import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Anagrafica.CentroDiocesano;
import it.arsinfo.smd.entity.Anagrafica.Diocesi;
import it.arsinfo.smd.entity.Anagrafica.Regione;
import it.arsinfo.smd.repository.AnagraficaDao;

import java.util.EnumSet;

import com.vaadin.data.Binder;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;


public class AnagraficaEditor extends VerticalLayout {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4673834235533544936L;

	private final AnagraficaDao repo;

	/**
	 * The currently edited customer
	 */
	private Anagrafica customer;
	
	private final ComboBox<Anagrafica.Diocesi> diocesi = new ComboBox<Anagrafica.Diocesi>("Diocesi", EnumSet.allOf(Anagrafica.Diocesi.class));
    private final ComboBox<Regione> regioneVescovi = new ComboBox<Anagrafica.Regione>("Regione Vescovi", EnumSet.allOf(Regione.class));
    private final ComboBox<CentroDiocesano> centroDiocesano = new ComboBox<Anagrafica.CentroDiocesano>("Centro Diocesano", EnumSet.allOf(CentroDiocesano.class));
	HorizontalLayout riga1 = new HorizontalLayout(diocesi,regioneVescovi, centroDiocesano);

	private final ComboBox<Anagrafica.Titolo> titolo = new ComboBox<Anagrafica.Titolo>("Titolo", EnumSet.allOf(Anagrafica.Titolo.class));
	private final TextField nome = new TextField("Nome");
	private final TextField cognome = new TextField("Cognome/Ragione Sociale");
	private final TextField intestazione = new TextField("Intestazione");
	HorizontalLayout riga2 = new HorizontalLayout(titolo,cognome, nome, intestazione);

	private final TextField indirizzo = new TextField("Indirizzo");
	private final TextField cap = new TextField("CAP");
	private final TextField citta = new TextField("Citta'");
	private final ComboBox<Anagrafica.Paese> paese = new ComboBox<Anagrafica.Paese>("Paese", EnumSet.allOf(Anagrafica.Paese.class));
	HorizontalLayout riga3 = new HorizontalLayout(indirizzo, cap, citta, paese);

	private final TextField email = new TextField("Email");
	private final TextField telefono = new TextField("Telefono");
	private final TextField cellulare = new TextField("Cellulare");
	private final TextField note = new TextField("Note");
	private final TextField piva = new TextField("P.Iva");
	private final TextField codfis = new TextField("Cod. Fis.");
	HorizontalLayout riga4 = new HorizontalLayout(email,telefono, cellulare, codfis,piva,note);
	
	private final CheckBox presidenteDiocesano = new CheckBox("Pres. Diocesano");//52 | Presidenti e Referenti DIOCESANI    
	private final CheckBox direttoreDiocesiano = new CheckBox("Dir. Diocesano");//1 | DIRETTORE DIOCESANO	
	private final CheckBox direttoreZonaMilano = new CheckBox("Dir. Zona Milano");//00013 | DIRETTORI ZONE MILANO	
	private final CheckBox elencoMarisaBisi = new CheckBox("Elenco Marisa Bisi"); //144 | MARISA BISI ELENCO
	private final CheckBox promotoreRegionale = new CheckBox("Prom. Reg.") ; //12 | PROMOTORI REGIONALI
	HorizontalLayout riga5 = new HorizontalLayout(presidenteDiocesano,direttoreDiocesiano,direttoreZonaMilano,elencoMarisaBisi,promotoreRegionale);
	private final ComboBox<Anagrafica.Omaggio> omaggio = new ComboBox<Anagrafica.Omaggio>("Omaggio", EnumSet.allOf(Anagrafica.Omaggio.class));
	private final ComboBox<Anagrafica.BmCassa> bmCassa = new ComboBox<Anagrafica.BmCassa>("Blocchetti Mensili Cassa", EnumSet.allOf(Anagrafica.BmCassa.class));
	private final ComboBox<Regione> regionePresidenteDiocesano = new ComboBox<Anagrafica.Regione>("Regione Pres. Diocesano", EnumSet.allOf(Regione.class));
	private final ComboBox<Regione> regioneDirettoreDiocesano = new ComboBox<Anagrafica.Regione>("Regione Dir. Diocesano", EnumSet.allOf(Regione.class));
	HorizontalLayout riga6 = new HorizontalLayout(omaggio, bmCassa,regionePresidenteDiocesano, regioneDirettoreDiocesano);
	
	private final CheckBox consiglioNazionaleADP = new CheckBox("Cons. Naz. ADP"); //10 | CONSIGLIO NAZIONALE A.D.P.
	private final CheckBox presidenzaADP = new CheckBox("Pres. ADP"); //49 | CONSIGLIO PRESIDENZA ADP
	private final CheckBox direzioneADP = new CheckBox("Dir. ADP"); //15 | MEMBRI DIREZIONE ADP
	private final CheckBox caricheSocialiADP = new CheckBox("Car. Soc. ADP"); //141 | CARICHE SOCIALI E RAPPRESENTANTI
	private final CheckBox delegatiRegionaliADP = new CheckBox("Del. Reg. ADP"); //140 | DELEGATI REGIONALI
	HorizontalLayout riga7 = new HorizontalLayout(consiglioNazionaleADP, presidenzaADP,direzioneADP,caricheSocialiADP,delegatiRegionaliADP);

    
	Button save = new Button("Save", VaadinIcons.CHECK);
	Button cancel = new Button("Cancel");
	Button delete = new Button("Delete", VaadinIcons.TRASH);
	

	HorizontalLayout actions = new HorizontalLayout(save, cancel, delete);

	Binder<Anagrafica> binder = new Binder<>(Anagrafica.class);
	private ChangeHandler changeHandler;

	public AnagraficaEditor(AnagraficaDao repo) {
		
		this.repo=repo;

		addComponents(riga1,riga2,riga3,riga4,riga5,riga7,riga6,actions);
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

	public interface ChangeHandler {
		void onChange();
	}

	public void setChangeHandler(ChangeHandler h) {
		// ChangeHandler is notified when either save or delete
		// is clicked
		changeHandler = h;
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
		}
		else {
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
