package it.arsinfo.smd.vaadin;

import java.util.EnumSet;

import com.vaadin.data.Binder;
import com.vaadin.data.converter.StringToBigDecimalConverter;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;

import it.arsinfo.smd.entity.Mese;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.entity.TipoPubblicazione;
import it.arsinfo.smd.repository.PubblicazioneDao;


public class PubblicazioneEditor extends SmdEditor {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4673834235533544936L;

	private final PubblicazioneDao repo;

	/**
	 * The currently edited customer
	 */
	private Pubblicazione pubblicazione;
	private final TextField nome = new TextField("Nome");
	private final TextField autore = new TextField("Autore");
	private final TextField editore = new TextField("Editore");
	private final ComboBox<TipoPubblicazione> tipo = new ComboBox<TipoPubblicazione>("Tipo", EnumSet.allOf(TipoPubblicazione.class));
	private final ComboBox<Mese> primaPubblicazione = new ComboBox<Mese>("Prima Pubblicazione", EnumSet.allOf(Mese.class));
	private final TextField costo = new TextField("Costo");
	private final TextField costoScontato = new TextField("Costo Scontato");

	private final CheckBox active = new CheckBox("Active");
	private final CheckBox abbonamento = new CheckBox("Abbonamento");
	Button save = new Button("Save", VaadinIcons.CHECK);
	Button cancel = new Button("Cancel");
	Button delete = new Button("Delete", VaadinIcons.TRASH);
	

	HorizontalLayout basic = new HorizontalLayout(nome,tipo,autore,editore);
	HorizontalLayout costi = new HorizontalLayout(costo,costoScontato,primaPubblicazione);
	HorizontalLayout check = new HorizontalLayout(active,abbonamento);
	HorizontalLayout actions = new HorizontalLayout(save, cancel, delete);

	Binder<Pubblicazione> binder = new Binder<>(Pubblicazione.class);

	public PubblicazioneEditor(PubblicazioneDao repo) {
		
		this.repo=repo;

		addComponents(basic,costi,check,actions);
		setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);

		binder.forField(nome).asRequired("Il Nome della Pubblicazione e' abbligatorio")
			.bind(Pubblicazione::getNome, Pubblicazione::setNome);
		binder.forField(tipo).asRequired("Il Tipo di pubblicazione e' obbligatorio")
			.bind(Pubblicazione::getTipo, Pubblicazione::setTipo);
		binder.bind(autore, Pubblicazione::getAutore, Pubblicazione::setAutore);
		binder.bind(editore, Pubblicazione::getEditore,Pubblicazione::setEditore);
		binder.forField(costo).asRequired()
		.withConverter(new StringToBigDecimalConverter("Conversione in Eur")).withValidator( f -> f.signum() == 1 , "Deve essere maggiore di 0" )
		.bind(Pubblicazione::getCosto, Pubblicazione::setCosto);
		binder.forField(costoScontato).asRequired()
		.withConverter(new StringToBigDecimalConverter("Conversione in Eur")).withValidator( f -> f.signum() == 1 , "Deve essere maggiore di 0" )
		.bind(Pubblicazione::getCostoScontato, Pubblicazione::setCostoScontato);
		binder.forField(active).bind(Pubblicazione::isActive,Pubblicazione::setActive);
		binder.forField(abbonamento).bind(Pubblicazione::isAbbonamento,Pubblicazione::setAbbonamento);
		binder.forField(primaPubblicazione).bind(Pubblicazione::getPrimaPubblicazione, Pubblicazione::setPrimaPubblicazione);
		// Configure and style components
		setSpacing(true);

		primaPubblicazione.setItemCaptionGenerator(Mese::getNomeBreve);

		save.addStyleName(ValoTheme.BUTTON_PRIMARY);
		delete.addStyleName(ValoTheme.BUTTON_DANGER);
		
		save.addClickListener(e -> save());
		delete.addClickListener(e -> delete());
		cancel.addClickListener(e -> edit(pubblicazione));
		setVisible(false);

	}

	void delete() {
		repo.delete(pubblicazione);
		changeHandler.onChange();
	}

	void save() {
		repo.save(pubblicazione);
		changeHandler.onChange();
	}
	
	public final void edit(Pubblicazione c) {
		if (c == null) {
			setVisible(false);
			return;
		}
		final boolean persisted = c.getId() != null;
		if (persisted) {
			// Find fresh entity for editing
			pubblicazione = repo.findById(c.getId()).get();
		}
		else {
			pubblicazione = c;
		}
		cancel.setVisible(persisted);

		// Bind customer properties to similarly named fields
		// Could also use annotation or "manual binding" or programmatically
		// moving values from fields to entities before saving
		binder.setBean(pubblicazione);

		setVisible(true);

		// Focus first name initially
		nome.focus();
	}
	
}
