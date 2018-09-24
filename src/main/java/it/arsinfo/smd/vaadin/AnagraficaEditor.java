package it.arsinfo.smd.vaadin;

import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.repository.AnagraficaDao;

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
	private final TextField nome = new TextField("Nome");
	private final TextField cognome = new TextField("Cognome");

	private final TextField indirizzo = new TextField("Indirizzo");
	private final ComboBox<Anagrafica.Diocesi> diocesi = new ComboBox<Anagrafica.Diocesi>("Diocesi", EnumSet.allOf(Anagrafica.Diocesi.class));
	
	Button save = new Button("Save", VaadinIcons.CHECK);
	Button cancel = new Button("Cancel");
	Button delete = new Button("Delete", VaadinIcons.TRASH);
	

	HorizontalLayout pri = new HorizontalLayout(nome,cognome);
	HorizontalLayout sec = new HorizontalLayout(diocesi,indirizzo);
	HorizontalLayout actions = new HorizontalLayout(save, cancel, delete);

	Binder<Anagrafica> binder = new Binder<>(Anagrafica.class);
	private ChangeHandler changeHandler;

	public AnagraficaEditor(AnagraficaDao repo) {
		
		this.repo=repo;

		addComponents(pri,sec,actions);
		setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);

		binder.bindInstanceFields(this);

		// Configure and style components
		setSpacing(true);

		save.addStyleName(ValoTheme.BUTTON_PRIMARY);
		delete.addStyleName(ValoTheme.BUTTON_DANGER);
		
		save.addClickListener(e -> save());
		delete.addClickListener(e -> delete());
		cancel.addClickListener(e -> editCustomer(customer));
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
	
	public final void editCustomer(Anagrafica c) {
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
