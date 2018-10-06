package it.arsinfo.smd.vaadin;

import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Note;
import it.arsinfo.smd.repository.AnagraficaDao;
import it.arsinfo.smd.repository.NoteDao;

import com.vaadin.data.Binder;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;


public class NoteEditor extends VerticalLayout {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4673834235533544936L;

	private final NoteDao repo;

	/**
	 * The currently edited customer
	 */
	private Note note;

	private final TextField description = new TextField("Descrizione");
	private final ComboBox<Anagrafica> anagrafica = new ComboBox<Anagrafica>("Selezionare il cliente");
    
	Button save = new Button("Save", VaadinIcons.CHECK);
	Button cancel = new Button("Cancel");
	Button delete = new Button("Delete", VaadinIcons.TRASH);
	

	HorizontalLayout pri = new HorizontalLayout(anagrafica);
	HorizontalLayout che = new HorizontalLayout(description);
	HorizontalLayout actions = new HorizontalLayout(save, cancel, delete);

	Binder<Note> binder = new Binder<>(Note.class);
	private ChangeHandler changeHandler;

	public NoteEditor(NoteDao repo, AnagraficaDao anadao) {
		
		this.repo=repo;

		addComponents(pri,che,actions);
		setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);

		anagrafica.setItems(anadao.findAll());
		anagrafica.setItemCaptionGenerator(Anagrafica::getCognome);

		binder.forField(anagrafica).asRequired().withValidator(an -> an != null, "Scegliere un Cliente" ).bind(Note::getAnagrafica, Note::setAnagrafica);
		binder.forField(description).bind(Note::getDescription, Note::setDescription);
		binder.bindInstanceFields(this);
		// Configure and style components
		setSpacing(true);


		save.addStyleName(ValoTheme.BUTTON_PRIMARY);
		delete.addStyleName(ValoTheme.BUTTON_DANGER);
		
		save.addClickListener(e -> save());
		delete.addClickListener(e -> delete());
		cancel.addClickListener(e -> edit(note));
		setVisible(false);

	}

	void delete() {
		repo.delete(note);
		changeHandler.onChange();
	}

	void save() {
		repo.save(note);
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
	
	public final void edit(Note c) {
		if (c == null) {
			setVisible(false);
			return;
		}
		final boolean persisted = c.getId() != null;
		if (persisted) {
			// Find fresh entity for editing
			note = repo.findById(c.getId()).get();
			setNoteEditable(note, true);
		}
		else {
			note = c;
			setNoteEditable(note, false);
		}
		cancel.setVisible(persisted);

		// Bind customer properties to similarly named fields
		// Could also use annotation or "manual binding" or programmatically
		// moving values from fields to entities before saving
		binder.setBean(note);
		setVisible(true);

		// Focus first name initially
		anagrafica.focus();
	}
	
	private void setNoteEditable(Note c,boolean read) {

		anagrafica.setReadOnly(read);
		description.setReadOnly(read);
		save.setEnabled(!read);
		cancel.setEnabled(!read);

	}
	
}
