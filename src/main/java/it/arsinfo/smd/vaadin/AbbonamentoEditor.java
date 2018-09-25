package it.arsinfo.smd.vaadin;

import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.repository.AbbonamentoDao;
import it.arsinfo.smd.repository.AnagraficaDao;


import com.vaadin.data.Binder;
import com.vaadin.data.converter.StringToFloatConverter;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;


public class AbbonamentoEditor extends VerticalLayout {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4673834235533544936L;

	private final AbbonamentoDao repo;

	/**
	 * The currently edited customer
	 */
	private Abbonamento abbonamento;
	private final ComboBox<Anagrafica> anagrafica = new ComboBox<Anagrafica>("Selezionare il cliente");
	private final TextField campo = new TextField("V Campo Poste Italiane");
	private final TextField cost = new TextField("Costo");
	
	Button save = new Button("Save", VaadinIcons.CHECK);
	Button cancel = new Button("Cancel");
	Button delete = new Button("Delete", VaadinIcons.TRASH);
	

	HorizontalLayout pri = new HorizontalLayout(anagrafica,campo, cost);
	HorizontalLayout actions = new HorizontalLayout(save, cancel, delete);

	Binder<Abbonamento> binder = new Binder<>(Abbonamento.class);
	private ChangeHandler changeHandler;

	public AbbonamentoEditor(AbbonamentoDao repo, AnagraficaDao anagraficaDao) {
		
		this.repo=repo;

		addComponents(pri,actions);
		setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);
		
		anagrafica.setItems(anagraficaDao.findAll());
		anagrafica.setItemCaptionGenerator(Anagrafica::getCognome);
		binder.forField(anagrafica).asRequired().withValidator(an -> an != null, "Scegliere un Cliente" ).bind(Abbonamento::getAnagrafica, Abbonamento::setAnagrafica);
		binder.forField(campo).asRequired().withValidator(ca -> ca != null, "Deve essere definito").bind(Abbonamento::getCampo, Abbonamento::setCampo);
		binder.forField(cost).asRequired()
		.withConverter(new StringToFloatConverter("Conversione in Eur")).withValidator( f -> f > 0, "Deve essere maggire di 0" )
		.bind(Abbonamento::getCost, Abbonamento::setCost);


		// Configure and style components
		setSpacing(true);

		save.addStyleName(ValoTheme.BUTTON_PRIMARY);
		delete.addStyleName(ValoTheme.BUTTON_DANGER);
		
		save.addClickListener(e -> save());
		delete.addClickListener(e -> delete());
		cancel.addClickListener(e -> edit(abbonamento));
		setVisible(false);

	}

	void delete() {
		repo.delete(abbonamento);
		changeHandler.onChange();
	}

	void save() {
		repo.save(abbonamento);
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
	
	public final void edit(Abbonamento c) {
		if (c == null) {
			setVisible(false);
			return;
		}
		final boolean persisted = c.getId() != null;
		if (persisted) {
			// Find fresh entity for editing
			abbonamento = repo.findById(c.getId()).get();
		}
		else {
			abbonamento = c;
		}
		cancel.setVisible(persisted);

		// Bind customer properties to similarly named fields
		// Could also use annotation or "manual binding" or programmatically
		// moving values from fields to entities before saving
		binder.setBean(abbonamento);

		setVisible(true);

		// Focus first name initially
		anagrafica.focus();
	}
	
}
