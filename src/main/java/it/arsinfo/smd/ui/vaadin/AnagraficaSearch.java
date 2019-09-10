package it.arsinfo.smd.ui.vaadin;

import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.util.StringUtils;

import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;

import it.arsinfo.smd.data.AreaSpedizione;
import it.arsinfo.smd.data.CentroDiocesano;
import it.arsinfo.smd.data.Diocesi;
import it.arsinfo.smd.data.Paese;
import it.arsinfo.smd.data.Provincia;
import it.arsinfo.smd.data.Regione;
import it.arsinfo.smd.data.TitoloAnagrafica;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.repository.AnagraficaDao;

public class AnagraficaSearch extends SmdSearch<Anagrafica> {

    private Diocesi searchDiocesi;
    private String searchDenominazione;
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
    private final CheckBox filterDirettoreDiocesiano = new CheckBox("Cerca Dir. Diocesano");
    private final CheckBox filterPresidenteDiocesano = new CheckBox("Cerca Pres. Diocesano");
    private final CheckBox filterDirettoreZonaMilano = new CheckBox("Cerca Dir. Zona Milano");
    private final CheckBox filterConsiglioNazionaleADP = new CheckBox("Cerca Cons. Naz. ADP");
    private final CheckBox filterPresidenzaADP = new CheckBox("Cerca Pres. ADP");
    private final CheckBox filterDirezioneADP = new CheckBox("Cerca Dir. ADP");
    private final CheckBox filterCaricheSocialiADP = new CheckBox("Cerca Car. Soc. ADP");
    private final CheckBox filterDelegatiRegionaliADP = new CheckBox("Cerca Del. Reg. ADP");
    private final CheckBox filterElencoMarisaBisi = new CheckBox("Cerca Elenco Marisa Bisi");
    private final CheckBox filterPromotoreRegionale = new CheckBox("Cerca Prom. Reg.");

    public AnagraficaSearch(AnagraficaDao anagraficaDao) {
        super(anagraficaDao);
        TextField filterDenominazione = new TextField("Cerca per Denominazione");
        TextField filterCap = new TextField("Cerca per CAP");
        TextField filterCitta = new TextField("Cerca per Città");
        ComboBox<Diocesi> filterDiocesi = new ComboBox<Diocesi>("Cerca per diocesi",
                                                                EnumSet.allOf(Diocesi.class));

        setComponents(new HorizontalLayout(filterDiocesi, 
                                           filterDenominazione,
                                           filterProvincia,
                                           filterCitta,
                                           filterCap,
                                           filterPaese,
                                           filterAreaSpedizione
                                           ),
                      new HorizontalLayout(filterTitolo,
                                           filterCentroDiocesano,
                                           filterRegioneVescovi,
                                           filterRegioneDirettoreDiocesano,
                                           filterRegionePresidenteDiocesano
                                           ),
                      new HorizontalLayout(filterDirettoreDiocesiano,
                                           filterPresidenteDiocesano,
                                           filterDirettoreZonaMilano,
                                           filterConsiglioNazionaleADP,
                                           filterPresidenzaADP,
                                           filterDirezioneADP,
                                           filterCaricheSocialiADP,
                                           filterDelegatiRegionaliADP,
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

        filterDirettoreDiocesiano.addValueChangeListener(e -> onChange());
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
        if (StringUtils.isEmpty(searchDenominazione) && StringUtils.isEmpty(searchCitta) && StringUtils.isEmpty(searchCap) && searchDiocesi == null) {
            return filterAll(findAll());
        }
        
        if (StringUtils.isEmpty(searchDenominazione) && StringUtils.isEmpty(searchCitta) && StringUtils.isEmpty(searchCap)) {
            return filterAll(((AnagraficaDao) getRepo()).findByDiocesi(searchDiocesi));
        }
        if (searchDiocesi == null && StringUtils.isEmpty(searchCitta) && StringUtils.isEmpty(searchCap)) {
            return filterAll(((AnagraficaDao) getRepo()).findByDenominazioneContainingIgnoreCase(searchDenominazione));
        }
        if (searchDiocesi == null && StringUtils.isEmpty(searchDenominazione) && StringUtils.isEmpty(searchCap)) {
            return filterAll(((AnagraficaDao) getRepo()).findByCittaContainingIgnoreCase(searchCitta));
        }
        if (searchDiocesi == null && StringUtils.isEmpty(searchDenominazione) && StringUtils.isEmpty(searchCitta)) {
            return filterAll(((AnagraficaDao) getRepo()).findByCapContainingIgnoreCase(searchCap));
        }

        if (StringUtils.isEmpty(searchCitta) && StringUtils.isEmpty(searchCap)) {
            return filterAll(((AnagraficaDao) getRepo())
                             .findByDenominazioneContainingIgnoreCase(searchDenominazione)
                             .stream()
                             .filter(tizio -> tizio.getDiocesi().equals(searchDiocesi))
                             .collect(Collectors.toList()));
        }

        if (StringUtils.isEmpty(searchDenominazione) && StringUtils.isEmpty(searchCap)) {
            return filterAll(((AnagraficaDao) getRepo())
                             .findByCittaContainingIgnoreCase(searchCitta)
                             .stream()
                             .filter(tizio -> tizio.getDiocesi().equals(searchDiocesi))
                             .collect(Collectors.toList()));
        }
        if (StringUtils.isEmpty(searchDenominazione) && StringUtils.isEmpty(searchCitta)) {
            return filterAll(((AnagraficaDao) getRepo())
                             .findByCapContainingIgnoreCase(searchCap)
                             .stream()
                             .filter(tizio -> tizio.getDiocesi().equals(searchDiocesi))
                             .collect(Collectors.toList()));
        }
        if (searchDiocesi == null && StringUtils.isEmpty(searchCitta)) {
            return filterAll(((AnagraficaDao) getRepo()).findByDenominazioneContainingIgnoreCase(searchDenominazione)
                             .stream()
                             .filter(tizio -> 
                                    tizio.getCap() != null 
                                 && tizio.getCap().toLowerCase().contains(searchCap.toLowerCase())
                             )
                             .collect(Collectors.toList()));
        }
        if (searchDiocesi == null && StringUtils.isEmpty(searchDenominazione)) {
            return filterAll(((AnagraficaDao) getRepo()).findByCittaContainingIgnoreCase(searchCitta)
                             .stream()
                             .filter(tizio -> 
                                    tizio.getCap() != null 
                                 && tizio.getCap().toLowerCase().contains(searchCap.toLowerCase())
                             )
                             .collect(Collectors.toList()));
        }
        if (searchDiocesi == null && StringUtils.isEmpty(searchCitta)) {
            return filterAll(((AnagraficaDao) getRepo()).findByCapContainingIgnoreCase(searchCap)
                             .stream()
                             .filter(tizio -> 
                                    tizio.getDenominazione() != null 
                                 && tizio.getDenominazione().toLowerCase().contains(searchDenominazione.toLowerCase())
                             )
                             .collect(Collectors.toList()));
        }

        if (StringUtils.isEmpty(searchCap)) {
            return filterAll(((AnagraficaDao) getRepo())
                             .findByDenominazioneContainingIgnoreCase(searchDenominazione)
                             .stream()
                             .filter(tizio -> 
                                    tizio.getDiocesi().equals(searchDiocesi) 
                                 && tizio.getCitta() != null && tizio.getCitta().toLowerCase().contains(searchCitta.toLowerCase())
                             )
                             .collect(Collectors.toList()));
        }
        if (StringUtils.isEmpty(searchDenominazione)) {
            return filterAll(((AnagraficaDao) getRepo())
                             .findByCittaContainingIgnoreCase(searchCitta)
                             .stream()
                             .filter(tizio -> 
                                tizio.getDiocesi().equals(searchDiocesi) 
                             && tizio.getCap() != null 
                             && tizio.getCap().toLowerCase().contains(searchCap.toLowerCase())
                             )
                             .collect(Collectors.toList()));
        }
        if (StringUtils.isEmpty(searchCitta)) {
            return filterAll(((AnagraficaDao) getRepo()).findByCapContainingIgnoreCase(searchCap)
                             .stream()
                             .filter(tizio ->
                                    tizio.getDiocesi().equals(searchDiocesi) 
                                 && tizio.getDenominazione() != null 
                                 && tizio.getDenominazione().toLowerCase().contains(searchDenominazione.toLowerCase())
                             )
                             .collect(Collectors.toList()));
        }
        if (searchDiocesi == null) {
            return filterAll(((AnagraficaDao) getRepo())
                             .findByCapContainingIgnoreCase(searchCap)
                             .stream()
                             .filter(tizio -> 
                                tizio.getCitta() != null 
                             && tizio.getCitta().toLowerCase().contains(searchCitta.toLowerCase())
                             && tizio.getDenominazione() != null 
                             && tizio.getDenominazione().toLowerCase().contains(searchDenominazione.toLowerCase())
                             )
                             .collect(Collectors.toList()));
        }

        return filterAll(((AnagraficaDao) getRepo()).findByDiocesi(searchDiocesi)
                         .stream()
                         .filter(
                                 tizio -> 
                            tizio.getDiocesi().equals(searchDiocesi) 
                         && tizio.getCitta() != null 
                         && tizio.getCitta().toLowerCase().contains(searchCitta.toLowerCase())
                         && tizio.getDenominazione() != null 
                         && tizio.getDenominazione().toLowerCase().contains(searchDenominazione.toLowerCase())
                         && tizio.getCap() != null 
                         && tizio.getCap().toLowerCase().contains(searchCap.toLowerCase())
                         )
                         .collect(Collectors.toList()));

    }

    private List<Anagrafica> filterAll(List<Anagrafica> anagrafiche) {

        if (filterPaese.getValue() != null) {
            anagrafiche = anagrafiche.stream().filter(a -> filterPaese.getValue() == a.getPaese()).collect(Collectors.toList());
        }
        if (filterAreaSpedizione.getValue() != null) {
            anagrafiche = anagrafiche.stream().filter(a -> filterAreaSpedizione.getValue() == a.getAreaSpedizione()).collect(Collectors.toList());
        }
        if (filterProvincia.getValue() != null) {
            anagrafiche = anagrafiche.stream().filter(a -> filterProvincia.getValue() == a.getProvincia()).collect(Collectors.toList());
        }
        if (filterTitolo.getValue() != null) {
            anagrafiche = anagrafiche.stream().filter(a -> filterTitolo.getValue() == a.getTitolo()).collect(Collectors.toList());
        }
        if (filterRegioneVescovi.getValue() != null) {
            anagrafiche = anagrafiche.stream().filter(a -> filterRegioneVescovi.getValue() == a.getRegioneVescovi()).collect(Collectors.toList());
        }
        if (filterCentroDiocesano.getValue() != null) {
            anagrafiche = anagrafiche.stream().filter(a -> filterCentroDiocesano.getValue() == a.getCentroDiocesano()).collect(Collectors.toList());
        }
        if (filterRegioneDirettoreDiocesano.getValue() != null) {
            anagrafiche = anagrafiche.stream().filter(a -> filterRegioneDirettoreDiocesano.getValue() == a.getRegioneDirettoreDiocesano()).collect(Collectors.toList());
        }
        if (filterDirettoreDiocesiano.getValue()) {
            anagrafiche = anagrafiche.stream().filter(a -> a.isDirettoreDiocesiano()).collect(Collectors.toList());
        }
        if (filterPresidenteDiocesano.getValue()) {
            anagrafiche = anagrafiche.stream().filter(a -> a.isPresidenteDiocesano()).collect(Collectors.toList());
        }
        if (filterDirettoreZonaMilano.getValue()) {
            anagrafiche = anagrafiche.stream().filter(a -> a.isDirettoreZonaMilano()).collect(Collectors.toList());
        }
        if (filterConsiglioNazionaleADP.getValue()) {
            anagrafiche = anagrafiche.stream().filter(a -> a.isConsiglioNazionaleADP()).collect(Collectors.toList());
        }
        if (filterPresidenzaADP.getValue()) {
            anagrafiche = anagrafiche.stream().filter(a -> a.isPresidenzaADP()).collect(Collectors.toList());
        }
        if (filterDirezioneADP.getValue()) {
            anagrafiche = anagrafiche.stream().filter(a -> a.isDirezioneADP()).collect(Collectors.toList());
        }
        if (filterCaricheSocialiADP.getValue()) {
            anagrafiche = anagrafiche.stream().filter(a -> a.isCaricheSocialiADP()).collect(Collectors.toList());
        }
        if (filterDelegatiRegionaliADP.getValue()) {
            anagrafiche = anagrafiche.stream().filter(a -> a.isDelegatiRegionaliADP()).collect(Collectors.toList());
        }
        if (filterElencoMarisaBisi.getValue()) {
            anagrafiche = anagrafiche.stream().filter(a -> a.isElencoMarisaBisi()).collect(Collectors.toList());
        }
        if (filterPromotoreRegionale.getValue()) {
            anagrafiche = anagrafiche.stream().filter(a -> a.isPromotoreRegionale()).collect(Collectors.toList());            
        }
       
        return anagrafiche;
    }

}
