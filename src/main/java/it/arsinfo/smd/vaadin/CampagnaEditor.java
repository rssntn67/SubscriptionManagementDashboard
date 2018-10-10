package it.arsinfo.smd.vaadin;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;


import it.arsinfo.smd.SmdApplication;
import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.Abbonamento.Anno;
import it.arsinfo.smd.entity.Abbonamento.Mese;
import it.arsinfo.smd.entity.Campagna;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.repository.AnagraficaDao;
import it.arsinfo.smd.repository.CampagnaDao;
import it.arsinfo.smd.repository.PubblicazioneDao;


import com.vaadin.data.Binder;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;


public class CampagnaEditor extends VerticalLayout {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4673834235533544936L;

	private final CampagnaDao repo;
	private final AnagraficaDao anaDao;
	private final PubblicazioneDao pubbldao;

	/**
	 * The currently edited customer
	 */
	private Campagna campagna;

	private final CheckBox pagato=new CheckBox("Pagato");
	private final CheckBox anagraficaFlagA=new CheckBox("anagraficaFlagA");
	private final CheckBox anagraficaFlagB=new CheckBox("anagraficaFlagB");
	private final CheckBox anagraficaFlagC=new CheckBox("anagraficaFlagC");

	private final CheckBox estratti=new CheckBox("Abb. Ann. Estratti");
    private final CheckBox blocchetti=new CheckBox("Abb. Sem. Blocchetti");
    private final CheckBox lodare=new CheckBox("Abb. Men. Lodare e Service");
    private final CheckBox messaggio=new CheckBox("Abb. Men. Messaggio");
    
    private final ComboBox<Anno> anno = new ComboBox<Abbonamento.Anno>("Selezionare Anno", EnumSet.allOf(Anno.class));
    private final ComboBox<Mese> inizio = new ComboBox<Mese>("Selezionare Inizio", EnumSet.allOf(Mese.class));
    private final ComboBox<Mese> fine = new ComboBox<Mese>("Selezionare Fine", EnumSet.allOf(Mese.class));

	Button save = new Button("Save", VaadinIcons.CHECK);
	Button cancel = new Button("Cancel");
	Button delete = new Button("Delete", VaadinIcons.TRASH);
	

	HorizontalLayout pri = new HorizontalLayout(anno,inizio,fine);
	HorizontalLayout che = new HorizontalLayout(estratti, blocchetti,lodare,messaggio);
	HorizontalLayout pag = new HorizontalLayout(pagato,anagraficaFlagA,anagraficaFlagB,anagraficaFlagC);
	HorizontalLayout actions = new HorizontalLayout(save, cancel, delete);

	Binder<Campagna> binder = new Binder<>(Campagna.class);
	private ChangeHandler changeHandler;

	public CampagnaEditor(CampagnaDao repo, AnagraficaDao anadao,PubblicazioneDao pubdao) {
		
		this.repo=repo;
		this.anaDao=anadao;
		this.pubbldao=pubdao;
		
		addComponents(pri,che,pag,actions);
		setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);

		anno.setItemCaptionGenerator(Anno::getAnnoAsString);

		inizio.setItemCaptionGenerator(Mese::getNomeBreve);
		fine.setItemCaptionGenerator(Mese::getNomeBreve);

		binder.bindInstanceFields(this);
		// Configure and style components
		setSpacing(true);

		save.addStyleName(ValoTheme.BUTTON_PRIMARY);
		delete.addStyleName(ValoTheme.BUTTON_DANGER);
		
		save.addClickListener(e -> save());
		delete.addClickListener(e -> delete());
		cancel.addClickListener(e -> edit(campagna));
		setVisible(false);

	}

	void delete() {
		repo.delete(campagna);
		changeHandler.onChange();
	}

	void save() {
		List<Pubblicazione> abbpub = new ArrayList<Pubblicazione>();
		if (campagna.isBlocchetti()) {
			abbpub.addAll(pubbldao.findByNomeStartsWithIgnoreCase("Blocchetti"));
		}
		if (campagna.isEstratti()) {
			abbpub.addAll(pubbldao.findByNomeStartsWithIgnoreCase("Estratti"));
		}
		if (campagna.isLodare()) {
			abbpub.addAll(pubbldao.findByNomeStartsWithIgnoreCase("Lodare"));
		}
		if (campagna.isMessaggio()) {
			abbpub.addAll(pubbldao.findByNomeStartsWithIgnoreCase("Messaggio"));
		}

		anaDao.findAll().stream().forEach( anag -> {
			Abbonamento abb = new Abbonamento(anag);
			abb.setAnno(campagna.getAnno());
			abb.setInizio(campagna.getInizio());
			abb.setFine(campagna.getFine());
			abb.setEstratti(campagna.isEstratti());
			abb.setBlocchetti(campagna.isBlocchetti());
			abb.setMessaggio(campagna.isMessaggio());
			abb.setLodare(campagna.isLodare());
			abb.setCampo(SmdApplication.generateCampo(campagna.getAnno(),campagna.getInizio(), campagna.getFine()));
			abb.setCost(SmdApplication.getCosto(abb, abbpub));
			if (abb.getCost().doubleValue() == BigDecimal.ZERO.doubleValue()) {
				abb.setOmaggio(true);
			}
			campagna.getAbbonamenti().add(abb);
		});
		repo.save(campagna);
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
	
	public final void edit(Campagna c) {
		if (c == null) {
			setVisible(false);
			return;
		}
		final boolean persisted = c.getId() != null;
		if (persisted) {
			// Find fresh entity for editing
			campagna = repo.findById(c.getId()).get();
			setCampagnaEditable(campagna, true);
		}
		else {
			campagna = c;
			setCampagnaEditable(campagna, false);
		}
		cancel.setVisible(persisted);

		// Bind customer properties to similarly named fields
		// Could also use annotation or "manual binding" or programmatically
		// moving values from fields to entities before saving
		binder.setBean(campagna);
		setVisible(true);

		// Focus first name initially
		anno.focus();
	}
	
	private void setCampagnaEditable(Campagna c,boolean read) {

		estratti.setReadOnly(read);
	    blocchetti.setReadOnly(read);
	    lodare.setReadOnly(read);
	    messaggio.setReadOnly(read);
	    
	    anno.setReadOnly(read);
	    inizio.setReadOnly(read);
	    fine.setReadOnly(read);

		pagato.setReadOnly(read);
		anagraficaFlagA.setReadOnly(read);
		anagraficaFlagB.setReadOnly(read);
		anagraficaFlagC.setReadOnly(read);
		save.setEnabled(!read);
		cancel.setEnabled(!read);

	}
	
}
