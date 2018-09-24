package it.arsinfo.smd.vaadin;

import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.entity.Pubblicazione.Tipo;
import it.arsinfo.smd.repository.PubblicazioneDao;

import java.util.EnumSet;

import com.vaadin.data.Binder;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;


public class PubblicazioneEditor extends VerticalLayout {

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
	private final ComboBox<Tipo> tipo = new ComboBox<Tipo>("Tipo", EnumSet.allOf(Tipo.class));
	
	Button save = new Button("Save", VaadinIcons.CHECK);
	Button cancel = new Button("Cancel");
	Button delete = new Button("Delete", VaadinIcons.TRASH);
	

	HorizontalLayout pri = new HorizontalLayout(nome,tipo);
	HorizontalLayout actions = new HorizontalLayout(save, cancel, delete);

	Binder<Pubblicazione> binder = new Binder<>(Pubblicazione.class);
	private ChangeHandler changeHandler;

	public PubblicazioneEditor(PubblicazioneDao repo) {
		
		this.repo=repo;

		addComponents(pri,actions);
		setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);

		binder.bindInstanceFields(this);

		// Configure and style components
		setSpacing(true);

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

	public interface ChangeHandler {
		void onChange();
	}

	public void setChangeHandler(ChangeHandler h) {
		// ChangeHandler is notified when either save or delete
		// is clicked
		changeHandler = h;
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
