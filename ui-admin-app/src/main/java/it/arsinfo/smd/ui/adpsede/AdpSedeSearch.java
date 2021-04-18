package it.arsinfo.smd.ui.adpsede;

import java.util.EnumSet;
import java.util.List;

import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;

import it.arsinfo.smd.service.api.SmdService;
import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.InvioSpedizione;
import it.arsinfo.smd.data.Mese;
import it.arsinfo.smd.data.StatoSpedizione;
import it.arsinfo.smd.service.dto.SpedizioneDto;
import it.arsinfo.smd.ui.vaadin.SmdBaseSearch;

public class AdpSedeSearch extends SmdBaseSearch<SpedizioneDto> {

    private InvioSpedizione invio=InvioSpedizione.AdpSede;
    private StatoSpedizione stato=StatoSpedizione.PROGRAMMATA;
    private Anno anno = Anno.getAnnoCorrente();
    private Mese mese = Mese.getMeseCorrente();
    ComboBox<StatoSpedizione> statoSpedizioneComboBox = new ComboBox<>("Stato", EnumSet.allOf(StatoSpedizione.class));

    private final SmdService service;
    public AdpSedeSearch(SmdService service) {
        this.service = service;

        ComboBox<Anno> filterAnno = new ComboBox<>("Anno",EnumSet.allOf(Anno.class));
        ComboBox<Mese> filterMese = new ComboBox<>("Mese",EnumSet.allOf(Mese.class));
        ComboBox<InvioSpedizione> invioSpedizioneComboBox = new ComboBox<>("Invio", EnumSet.complementOf(EnumSet.of(InvioSpedizione.Spedizioniere)));

        setComponents(new HorizontalLayout(filterAnno,filterMese,invioSpedizioneComboBox,statoSpedizioneComboBox));

        filterAnno.setEmptySelectionAllowed(false);
        filterAnno.setPlaceholder("Anno");
        filterAnno.setValue(anno);
        filterAnno.setItemCaptionGenerator(Anno::getAnnoAsString);
        filterAnno.addSelectionListener(e -> {
        	anno = e.getSelectedItem().get();
            onChange();
        });

        filterMese.setEmptySelectionAllowed(false);
        filterMese.setPlaceholder("Mese");
        filterMese.setValue(mese);
        filterMese.setItemCaptionGenerator(Mese::getNomeBreve);
        filterMese.addSelectionListener(e -> {
        	mese = e.getSelectedItem().get();
            onChange();
        });
        
        statoSpedizioneComboBox.setValue(stato);
        statoSpedizioneComboBox.setEmptySelectionAllowed(false);
        statoSpedizioneComboBox.setSizeFull();
        statoSpedizioneComboBox.addSelectionListener(e -> {
        	stato=e.getSelectedItem().get();
        	onChange();
        });

        invioSpedizioneComboBox.setValue(invio);
        invioSpedizioneComboBox.setEmptySelectionAllowed(false);
        invioSpedizioneComboBox.setSizeFull();
        invioSpedizioneComboBox.addSelectionListener(e -> {
        	invio=e.getSelectedItem().get();
        	onChange();
        });
        
        
    }
    
    @Override
    public List<SpedizioneDto> find() {
        return service.listBy(mese,anno,stato,invio);
    }

    public InvioSpedizione getInvio() {
    	return invio;
    }
    
    public StatoSpedizione getStato() {
    	return stato;
    }

    public void setStato(StatoSpedizione stato) {
    	this.stato=stato;
    	this.statoSpedizioneComboBox.setValue(stato);
    }
    
    public Anno getAnno() {
    	return anno;
    }
    
    public Mese getMese() {
    	return mese;
    }
    
}