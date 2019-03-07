package it.arsinfo.smd.vaadin;

import java.util.EnumSet;

import com.vaadin.data.Binder;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.themes.ValoTheme;

import it.arsinfo.smd.SmdApplication;
import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.Mese;
import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Campagna;
import it.arsinfo.smd.repository.AnagraficaDao;
import it.arsinfo.smd.repository.CampagnaDao;
import it.arsinfo.smd.repository.PubblicazioneDao;


public class CampagnaEditor extends SmdEditor {

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

	private final CheckBox rinnovaSoloAbbonatiInRegola=new CheckBox("Selezionare per rinnovo Abbonati in Regola");
    
        private final ComboBox<Anno> anno = new ComboBox<Anno>("Selezionare Anno", EnumSet.allOf(Anno.class));
        private final ComboBox<Mese> inizio = new ComboBox<Mese>("Selezionare Inizio", EnumSet.allOf(Mese.class));
        private final ComboBox<Mese> fine = new ComboBox<Mese>("Selezionare Fine", EnumSet.allOf(Mese.class));

	Button save = new Button("Save", VaadinIcons.CHECK);
	Button cancel = new Button("Cancel");
	Button delete = new Button("Delete", VaadinIcons.TRASH);
	

	HorizontalLayout pri = new HorizontalLayout(anno,inizio,fine);
	HorizontalLayout pag = new HorizontalLayout(rinnovaSoloAbbonatiInRegola);
	HorizontalLayout actions = new HorizontalLayout(save, cancel, delete);

	Binder<Campagna> binder = new Binder<>(Campagna.class);
	
	public CampagnaEditor(CampagnaDao repo, AnagraficaDao anadao,PubblicazioneDao pubdao) {
		
		this.repo=repo;
		this.anaDao=anadao;
		this.pubbldao=pubdao;

	        
		addComponents(pri, pag,actions);
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
        anaDao.findAll().stream().forEach(anag -> {
            Abbonamento abb = new Abbonamento(anag);
            abb.setAnno(campagna.getAnno());
            abb.setInizio(campagna.getInizio());
            abb.setFine(campagna.getFine());
            abb.setCampo(SmdApplication.generateCampo(campagna.getAnno(),
                                                      campagna.getInizio(),
                                                      campagna.getFine()));
            abb.setCost(SmdApplication.generaCosto(abb));
            campagna.getAbbonamenti().add(abb);
        });
        repo.save(campagna);
        changeHandler.onChange();
	}
	
	//FIXME need to get the stored value
	public int getNumero(Anagrafica anagrafica) {
	    return 1;
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
	
    private void setCampagnaEditable(Campagna c, boolean read) {

        anno.setReadOnly(read);
        inizio.setReadOnly(read);
        fine.setReadOnly(read);

        save.setEnabled(!read);
        cancel.setEnabled(!read);

    }	
	
	
}
