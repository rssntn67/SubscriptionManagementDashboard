package it.arsinfo.smd.ui.stat;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;

import it.arsinfo.smd.service.api.AbbonamentoConRivisteService;
import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.AreaSpedizione;
import it.arsinfo.smd.data.CentroDiocesano;
import it.arsinfo.smd.data.Diocesi;
import it.arsinfo.smd.data.Paese;
import it.arsinfo.smd.data.Provincia;
import it.arsinfo.smd.data.Regione;
import it.arsinfo.smd.data.TitoloAnagrafica;
import it.arsinfo.smd.dto.AbbonamentoConRiviste;
import it.arsinfo.smd.ui.vaadin.SmdChangeHandler;

public class StatSearch extends SmdChangeHandler {

    private Anno anno;
    private Diocesi searchDiocesi;
    private String searchDenominazione;
    private String searchNome;
    private String searchCap;
    private String searchCitta;

    private final Button searchButton = new Button("Cerca", VaadinIcons.SEARCH);
    private final ComboBox<Paese> filterPaese = new ComboBox<Paese>("Paese",
            EnumSet.allOf(Paese.class));

    private final ComboBox<AreaSpedizione> filterAreaSpedizione = new ComboBox<AreaSpedizione>("Area Spedizione",
            EnumSet.allOf(AreaSpedizione.class));

    private final ComboBox<Provincia> filterProvincia = new ComboBox<Provincia>("Provincia",
            EnumSet.allOf(Provincia.class));

    private final ComboBox<Regione> filterRegioneVescovi = new ComboBox<Regione>("Regione Vescovi",
            EnumSet.allOf(Regione.class));

    private final ComboBox<CentroDiocesano> filterCentroDiocesano = new ComboBox<CentroDiocesano>("Centro Diocesano",
                             EnumSet.allOf(CentroDiocesano.class));

    private final ComboBox<TitoloAnagrafica> filterTitolo = new ComboBox<TitoloAnagrafica>("Titolo",
                      EnumSet.allOf(TitoloAnagrafica.class));

    private final ComboBox<Regione> filterRegionePresidenteDiocesano = new ComboBox<Regione>("Regione Pres. Diocesano",EnumSet.allOf(Regione.class));
    private final ComboBox<Regione> filterRegioneDirettoreDiocesano = new ComboBox<Regione>("Regione Dir. Diocesano",EnumSet.allOf(Regione.class));
    private final CheckBox filterDirettoreDiocesano = new CheckBox("Dir. Diocesano");
    private final CheckBox filterPresidenteDiocesano = new CheckBox("Pres. Diocesano");
    private final CheckBox filterDirettoreZonaMilano = new CheckBox("Dir. Zona Milano");
    private final CheckBox filterConsiglioNazionaleADP = new CheckBox("Cons. Naz. ADP");
    private final CheckBox filterPresidenzaADP = new CheckBox("Pres. ADP");
    private final CheckBox filterDirezioneADP = new CheckBox("Dir. ADP");
    private final CheckBox filterCaricheSocialiADP = new CheckBox("Car. Soc. ADP");
    private final CheckBox filterDelegatiRegionaliADP = new CheckBox("Del. Reg. ADP");
    private final CheckBox filterElencoMarisaBisi = new CheckBox("Elenco Marisa Bisi");
    private final CheckBox filterPromotoreRegionale = new CheckBox("Prom. Reg.");
    private final ComboBox<Anno> filterAnno = new ComboBox<Anno>("Selezionare Anno",EnumSet.allOf(Anno.class));
    
    private final AbbonamentoConRivisteService dao;

    public StatSearch(AbbonamentoConRivisteService dao) {
        this.dao=dao;
        
        TextField filterDenominazione = new TextField("Denominazione");
        TextField filterNome = new TextField("Nome");
        TextField filterCap = new TextField("CAP");
        TextField filterCitta = new TextField("Città");
        ComboBox<Diocesi> filterDiocesi = new ComboBox<Diocesi>("diocesi",
                                                                EnumSet.allOf(Diocesi.class));

        setComponents(
        			new HorizontalLayout(searchButton),
                	new HorizontalLayout(filterAnno),
                      new HorizontalLayout(
                                           filterTitolo,
                                           filterDiocesi, 
                                           filterDenominazione,
                                           filterNome
                                           ),
                      new HorizontalLayout(
                                           filterProvincia,
                                           filterCitta,
                                           filterCap,
                                           filterPaese,
                                           filterAreaSpedizione
                                           ),
                      new HorizontalLayout(
                                           filterCentroDiocesano,
                                           filterRegioneVescovi,
                                           filterRegioneDirettoreDiocesano,
                                           filterRegionePresidenteDiocesano
                                           ),
                      new HorizontalLayout(
                                           filterDirettoreDiocesano,
                                           filterPresidenteDiocesano,
                                           filterDirettoreZonaMilano
                                           ),
                      new HorizontalLayout(
                                           filterConsiglioNazionaleADP,
                                           filterPresidenzaADP,
                                           filterDirezioneADP,
                                           filterCaricheSocialiADP,
                                           filterDelegatiRegionaliADP),
                      new HorizontalLayout(
                                           filterElencoMarisaBisi,
                                           filterPromotoreRegionale));


        filterAnno.setEmptySelectionAllowed(false);
        filterAnno.setItemCaptionGenerator(Anno::getAnnoAsString);
        anno=Anno.getAnnoCorrente();
        filterAnno.setValue(anno);
		
        filterAnno.addSelectionListener(e -> {
        		anno = e.getSelectedItem().get();
        });

        filterDiocesi.setEmptySelectionAllowed(true);
        filterDiocesi.setItemCaptionGenerator(Diocesi::getDetails);
        filterDiocesi.setPlaceholder("Seleziona Diocesi");

        filterDiocesi.addSelectionListener(e -> {
            if (e.getValue() == null) {
                searchDiocesi = null;
            } else {
                searchDiocesi = e.getSelectedItem().get();
            }
        });

        filterDenominazione.setPlaceholder("Inserisci Denominazione");
        filterDenominazione.setValueChangeMode(ValueChangeMode.EAGER);
        filterDenominazione.addValueChangeListener(e -> {
            searchDenominazione = e.getValue();
        });

        filterNome.setPlaceholder("Inserisci Nome");
        filterNome.setValueChangeMode(ValueChangeMode.EAGER);
        filterNome.addValueChangeListener(e -> {
            searchNome = e.getValue();
        });


        filterCap.setPlaceholder("Inserisci CAP");
        filterCap.setValueChangeMode(ValueChangeMode.EAGER);
        filterCap.addValueChangeListener(e -> {
            searchCap = e.getValue();
        });
        
        filterCitta.setPlaceholder("Inserisci Città");
        filterCitta.setValueChangeMode(ValueChangeMode.EAGER);
        filterCitta.addValueChangeListener(e -> {
            searchCitta = e.getValue();
        });


        filterTitolo.setPlaceholder("Seleziona Titolo");
        filterCentroDiocesano.setPlaceholder("Seleziona Centro");
        filterRegioneVescovi.setPlaceholder("Seleziona Regione");
        filterProvincia.setPlaceholder("Seleziona Provincia");
        filterPaese.setPlaceholder("Seleziona Paese");
        filterPaese.setItemCaptionGenerator(Paese::getNome);
        filterAreaSpedizione.setPlaceholder("Seleziona Area");

        filterRegionePresidenteDiocesano.setPlaceholder("Seleziona Regione");
        filterRegioneDirettoreDiocesano.setPlaceholder("Seleziona Regione");
        
        searchButton.addClickListener(e -> onChange());

    }

    public List<AbbonamentoConRiviste> find() {
        return dao.searchBy(
        		anno,
        		searchDiocesi,
				searchNome,
				searchDenominazione,
				searchCitta,
				searchCap,
				filterPaese.getValue(),
				filterAreaSpedizione.getValue(),
				filterProvincia.getValue(),
				filterTitolo.getValue(),
				filterRegioneVescovi.getValue(),
				filterCentroDiocesano.getValue(),
				filterRegioneDirettoreDiocesano.getValue(),
	    		filterDirettoreDiocesano.getValue(),
	    		filterPresidenteDiocesano.getValue(),
	    		filterRegionePresidenteDiocesano.getValue(),
	    		filterDirettoreZonaMilano.getValue(),
	    		filterConsiglioNazionaleADP.getValue(),
	    		filterPresidenzaADP.getValue(),
	    		filterDirezioneADP.getValue(),
	    		filterCaricheSocialiADP.getValue(),
	    		filterDelegatiRegionaliADP.getValue(),
	    		filterElencoMarisaBisi.getValue(),
	    		filterPromotoreRegionale.getValue()
				);
    }


	public List<AbbonamentoConRiviste> findNone() {
		return new ArrayList<AbbonamentoConRiviste>();
	}

}
