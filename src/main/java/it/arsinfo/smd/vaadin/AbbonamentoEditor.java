package it.arsinfo.smd.vaadin;

import java.util.EnumSet;

import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Abbonamento.Anno;
import it.arsinfo.smd.entity.Abbonamento.Mese;
import it.arsinfo.smd.repository.AbbonamentoDao;
import it.arsinfo.smd.repository.AnagraficaDao;

import com.vaadin.data.Binder;
import com.vaadin.data.converter.LocalDateToDateConverter;
import com.vaadin.data.converter.StringToFloatConverter;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
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
	private final ComboBox<Anagrafica> destinatario = new ComboBox<Anagrafica>("Selezionare il destinatario");
	private final TextField campo = new TextField("V Campo Poste Italiane");
	private final TextField cost = new TextField("Costo");
	
	private final CheckBox pagato=new CheckBox("Pagato");
	private final DateField dataincasso = new DateField("Incassato");
    private final CheckBox estratti=new CheckBox("Abb. Ann. Estratti");
    private final CheckBox blocchetti=new CheckBox("Abb. Sem. Blocchetti");
    private final CheckBox lodare=new CheckBox("Abb. Men. Lodare e Service");
    private final CheckBox messaggio=new CheckBox("Abb. Men. Messaggio");
    private final CheckBox costi=new CheckBox("Costi Spedizione");
    
    private final ComboBox<Anno> anno = new ComboBox<Abbonamento.Anno>("Selezionare Anno", EnumSet.allOf(Anno.class));
    private final ComboBox<Mese> inizio = new ComboBox<Mese>("Selezionare Inizio", EnumSet.allOf(Mese.class));
    private final ComboBox<Mese> fine = new ComboBox<Mese>("Selezionare Fine", EnumSet.allOf(Mese.class));

	Button save = new Button("Save", VaadinIcons.CHECK);
	Button cancel = new Button("Cancel");
	Button delete = new Button("Delete", VaadinIcons.TRASH);
	

	HorizontalLayout pri = new HorizontalLayout(anagrafica,destinatario,anno,inizio,fine);
	HorizontalLayout sec = new HorizontalLayout(campo, cost);
	HorizontalLayout che = new HorizontalLayout(estratti, blocchetti,lodare,messaggio,costi);
	HorizontalLayout pag = new HorizontalLayout(pagato);
	HorizontalLayout pagfield = new HorizontalLayout(dataincasso);
	HorizontalLayout actions = new HorizontalLayout(save, cancel, delete);

	Binder<Abbonamento> binder = new Binder<>(Abbonamento.class);
	private ChangeHandler changeHandler;

	public AbbonamentoEditor(AbbonamentoDao repo, AnagraficaDao anagraficaDao) {
		
		this.repo=repo;

		addComponents(pri,sec,che,pag,pagfield,actions);
		setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);

		anno.setItemCaptionGenerator(Anno::getAnnoAsString);

		inizio.setItemCaptionGenerator(Mese::getNomeBreve);
		fine.setItemCaptionGenerator(Mese::getNomeBreve);

		anagrafica.setItems(anagraficaDao.findAll());
		anagrafica.setItemCaptionGenerator(Anagrafica::getCognome);
		destinatario.setItems(anagraficaDao.findAll());
		destinatario.setItemCaptionGenerator(Anagrafica::getCognome);

		binder.forField(anagrafica).asRequired().withValidator(an -> an != null, "Scegliere un Cliente" ).bind(Abbonamento::getAnagrafica, Abbonamento::setAnagrafica);
		binder.forField(destinatario).bind("destinatario");
		binder.forField(anno).bind("anno");
		binder.forField(inizio).bind("inizio");
		binder.forField(fine).bind("fine");
		binder.forField(campo).asRequired().withValidator(ca -> ca != null, "Deve essere definito").bind(Abbonamento::getCampo, Abbonamento::setCampo);
		binder.forField(cost).asRequired()
		.withConverter(new StringToFloatConverter("Conversione in Eur")).withValidator( f -> f > 0, "Deve essere maggire di 0" )
		.bind(Abbonamento::getCost, Abbonamento::setCost);
		binder.forField(lodare).bind("lodare");
		binder.forField(messaggio).bind("messaggio");
		binder.forField(estratti).bind("estratti");
		binder.forField(blocchetti).bind("blocchetti");
		binder.forField(costi).bind("costi");
		binder.forField(pagato).bind("pagato");
		binder.forField(dataincasso).
		withConverter(new LocalDateToDateConverter()).bind("dataincasso");

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
		if (abbonamento.getDataincasso() != null) {
			abbonamento.setPagato(true);
		}
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
			setAbbonamentoEditable(abbonamento, true);
		}
		else {
			abbonamento = c;
			setAbbonamentoEditable(abbonamento, false);
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
	
	private void setAbbonamentoEditable(Abbonamento c,boolean read) {

		anagrafica.setReadOnly(read);
		destinatario.setReadOnly(read);

		estratti.setReadOnly(read);
	    blocchetti.setReadOnly(read);
	    lodare.setReadOnly(read);
	    messaggio.setReadOnly(read);
	    costi.setReadOnly(read);
	    
	    anno.setReadOnly(read);
	    inizio.setReadOnly(read);
	    fine.setReadOnly(read);

		if (c.isPagato()) {
			save.setEnabled(false);
			cancel.setEnabled(false);
			pagato.setVisible(true);
			pagato.setReadOnly(true);
			dataincasso.setReadOnly(true);
		} else {
			save.setEnabled(true);
			cancel.setEnabled(true);
			pagato.setVisible(false);
			dataincasso.setReadOnly(false);
		}
		cost.setVisible(read);
		cost.setReadOnly(read);
		campo.setVisible(read);
		campo.setReadOnly(read);		
		dataincasso.setVisible(read);

	}
	
}
