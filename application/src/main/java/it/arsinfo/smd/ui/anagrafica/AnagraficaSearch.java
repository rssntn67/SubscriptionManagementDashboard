package it.arsinfo.smd.ui.anagrafica;

import java.util.EnumSet;
import java.util.List;

import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;

import it.arsinfo.smd.service.api.AnagraficaService;
import it.arsinfo.smd.data.AreaSpedizione;
import it.arsinfo.smd.data.CentroDiocesano;
import it.arsinfo.smd.data.Diocesi;
import it.arsinfo.smd.data.Paese;
import it.arsinfo.smd.data.Provincia;
import it.arsinfo.smd.data.Regione;
import it.arsinfo.smd.data.TitoloAnagrafica;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.ui.vaadin.SmdSearch;

public class AnagraficaSearch extends SmdSearch<Anagrafica> {

    private Diocesi searchDiocesi;
    private String searchDenominazione;
    private String searchNome;
    private String searchCap;
    private String searchCitta;

    private final ComboBox<Paese> filterPaese = new ComboBox<Paese>("Cerca per Paese",
            EnumSet.allOf(Paese.class));

    private final ComboBox<AreaSpedizione> filterAreaSpedizione = new ComboBox<AreaSpedizione>("Cerca per Area Spedizione",
            EnumSet.allOf(AreaSpedizione.class));

    private final ComboBox<Provincia> filterProvincia = new ComboBox<Provincia>("Cerca per Provincia",
            EnumSet.allOf(Provincia.class));

    private final ComboBox<Regione> filterRegioneVescovi = new ComboBox<Regione>("Cerca per Regione Vescovi",
            EnumSet.allOf(Regione.class));

    private final ComboBox<CentroDiocesano> filterCentroDiocesano = new ComboBox<CentroDiocesano>("Cerca per Centro Diocesano",
                             EnumSet.allOf(CentroDiocesano.class));

    private final ComboBox<TitoloAnagrafica> filterTitolo = new ComboBox<TitoloAnagrafica>("Cerca per Titolo",
                      EnumSet.allOf(TitoloAnagrafica.class));

    private final ComboBox<Regione> filterRegionePresidenteDiocesano = new ComboBox<Regione>("Cerca per Regione Pres. Diocesano",EnumSet.allOf(Regione.class));
    private final ComboBox<Regione> filterRegioneDirettoreDiocesano = new ComboBox<Regione>("Cerca per Regione Dir. Diocesano",EnumSet.allOf(Regione.class));
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
    
    private final AnagraficaService dao;

    public AnagraficaSearch(AnagraficaService dao) {
        super(dao);
        this.dao=dao;
        TextField filterDenominazione = new TextField("Cerca per Denominazione");
        TextField filterNome = new TextField("Cerca per Nome");
        TextField filterCap = new TextField("Cerca per CAP");
        TextField filterCitta = new TextField("Cerca per Città");
        ComboBox<Diocesi> filterDiocesi = new ComboBox<Diocesi>("Cerca per diocesi",
                                                                EnumSet.allOf(Diocesi.class));

        setComponents(
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


        filterDiocesi.setEmptySelectionAllowed(true);
        filterDiocesi.setItemCaptionGenerator(Diocesi::getDetails);
        filterDiocesi.setPlaceholder("Seleziona Diocesi");

        filterDiocesi.addSelectionListener(e -> {
            if (e.getValue() == null) {
                searchDiocesi = null;
            } else {
                searchDiocesi = e.getSelectedItem().get();
            }
            onChange();
        });

        filterDenominazione.setPlaceholder("Inserisci Denominazione");
        filterDenominazione.setValueChangeMode(ValueChangeMode.EAGER);
        filterDenominazione.addValueChangeListener(e -> {
            searchDenominazione = e.getValue();
            onChange();
        });

        filterNome.setPlaceholder("Inserisci Nome");
        filterNome.setValueChangeMode(ValueChangeMode.EAGER);
        filterNome.addValueChangeListener(e -> {
            searchNome = e.getValue();
            onChange();
        });


        filterCap.setPlaceholder("Inserisci CAP");
        filterCap.setValueChangeMode(ValueChangeMode.EAGER);
        filterCap.addValueChangeListener(e -> {
            searchCap = e.getValue();
            onChange();
        });
        
        filterCitta.setPlaceholder("Inserisci Città");
        filterCitta.setValueChangeMode(ValueChangeMode.EAGER);
        filterCitta.addValueChangeListener(e -> {
            searchCitta = e.getValue();
            onChange();
        });


        filterTitolo.setPlaceholder("Seleziona Titolo");
        filterTitolo.addSelectionListener(e -> onChange());
        filterCentroDiocesano.setPlaceholder("Seleziona Centro");
        filterCentroDiocesano.addSelectionListener(e -> onChange());
        filterRegioneVescovi.setPlaceholder("Seleziona Regione");
        filterRegioneVescovi.addSelectionListener(e -> onChange());
        filterProvincia.setPlaceholder("Seleziona Provincia");
        filterProvincia.addSelectionListener(e -> onChange());
        filterPaese.setPlaceholder("Seleziona Paese");
        filterPaese.setItemCaptionGenerator(Paese::getNome);
        filterPaese.addSelectionListener(e -> onChange());
        filterAreaSpedizione.setPlaceholder("Seleziona Area");
        filterAreaSpedizione.addSelectionListener(e -> onChange());

        filterRegionePresidenteDiocesano.setPlaceholder("Seleziona Regione");
        filterRegionePresidenteDiocesano.addSelectionListener(e -> onChange());
        filterRegioneDirettoreDiocesano.setPlaceholder("Seleziona Regione");
        filterRegioneDirettoreDiocesano.addSelectionListener(e -> onChange());

        filterDirettoreDiocesano.addValueChangeListener(e -> onChange());
        filterPresidenteDiocesano.addValueChangeListener(e -> onChange());
        filterDirettoreZonaMilano.addValueChangeListener(e -> onChange());
        filterConsiglioNazionaleADP.addValueChangeListener(e -> onChange());
        filterPresidenzaADP.addValueChangeListener(e -> onChange());
        filterDirezioneADP.addValueChangeListener(e -> onChange());
        filterCaricheSocialiADP.addValueChangeListener(e -> onChange());
        filterDelegatiRegionaliADP.addValueChangeListener(e -> onChange());
        filterElencoMarisaBisi.addValueChangeListener(e -> onChange());
        filterPromotoreRegionale.addValueChangeListener(e -> onChange());

    }

	@Override
	public List<Anagrafica> find() {
		return dao.searchBy(searchDiocesi, searchNome, searchDenominazione, searchCitta, searchCap,
				filterPaese.getValue(), filterAreaSpedizione.getValue(), filterProvincia.getValue(),
				filterTitolo.getValue(), filterRegioneVescovi.getValue(), filterCentroDiocesano.getValue(),
				filterRegioneDirettoreDiocesano.getValue(), filterDirettoreDiocesano.getValue(),
				filterPresidenteDiocesano.getValue(), filterRegionePresidenteDiocesano.getValue(),
				filterDirettoreZonaMilano.getValue(), filterConsiglioNazionaleADP.getValue(),
				filterPresidenzaADP.getValue(), filterDirezioneADP.getValue(), filterCaricheSocialiADP.getValue(),
				filterDelegatiRegionaliADP.getValue(), filterElencoMarisaBisi.getValue(),
				filterPromotoreRegionale.getValue());
	}

}
