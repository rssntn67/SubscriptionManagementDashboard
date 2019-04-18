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

import it.arsinfo.smd.data.CentroDiocesano;
import it.arsinfo.smd.data.Diocesi;
import it.arsinfo.smd.data.Omaggio;
import it.arsinfo.smd.data.Regione;
import it.arsinfo.smd.data.TitoloAnagrafica;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Storico;
import it.arsinfo.smd.repository.AnagraficaDao;
import it.arsinfo.smd.repository.StoricoDao;

public class AnagraficaSearch extends SmdSearch<Anagrafica> {

    private Diocesi searchDiocesi;
    private String searchCognome;

    private final ComboBox<Regione> filterRegioneVescovi = new ComboBox<Regione>("Regione Vescovi",
            EnumSet.allOf(Regione.class));

    private final ComboBox<CentroDiocesano> filterCentroDiocesano = new ComboBox<CentroDiocesano>("Centro Diocesano",
                             EnumSet.allOf(CentroDiocesano.class));

    private final ComboBox<TitoloAnagrafica> filterTitolo = new ComboBox<TitoloAnagrafica>("Titolo",
                      EnumSet.allOf(TitoloAnagrafica.class));

    private final ComboBox<Regione> filterRegionePresidenteDiocesano = new ComboBox<Regione>("Regione Pres. Diocesano",EnumSet.allOf(Regione.class));
    private final ComboBox<Regione> filterRegioneDirettoreDiocesano = new ComboBox<Regione>("Regione Dir. Diocesano",EnumSet.allOf(Regione.class));
    private final CheckBox filterDirettoreDiocesiano = new CheckBox("Dir. Diocesano");
    private final CheckBox filterPresidenteDiocesano = new CheckBox("Pres. Diocesano");
    private final CheckBox filterDirettoreZonaMilano = new CheckBox("Dir. Zona Milano");
    private final CheckBox filterConsiglioNazionaleADP = new CheckBox("Cons. Naz. ADP");
    private final CheckBox filterPresidenzaADP = new CheckBox("Pres. ADP");
    private final CheckBox filterDirezioneADP = new CheckBox("Dir. ADP");
    private final CheckBox filterCaricheSocialiADP = new CheckBox("Car. Soc. ADP");
    private final CheckBox filterDelegatiRegionaliADP = new CheckBox("Del. Reg. ADP");
    private final CheckBox filterElencoMarisaBisi = new CheckBox("Elenco Marisa Bisi");
    private final CheckBox filterPromotoreRegionale = new CheckBox("Prom. Reg.");
    private final ComboBox<Omaggio> filterOmaggio = new ComboBox<Omaggio>("Omaggio", EnumSet.allOf(Omaggio.class));

    private final StoricoDao storicoDao;
    public AnagraficaSearch(AnagraficaDao anagraficaDao, StoricoDao storicoDao) {
        super(anagraficaDao);
        this.storicoDao = storicoDao;
        TextField filterCognome = new TextField("Ricerca per Cognome");
        ComboBox<Diocesi> filterDiocesi = new ComboBox<Diocesi>("Ricerca per diocesi",
                                                                EnumSet.allOf(Diocesi.class));

        setComponents(new HorizontalLayout(
                                           filterDiocesi, 
                                           filterCognome,
                                           filterTitolo,
                                           filterRegioneVescovi,
                                           filterOmaggio,
                                           filterCentroDiocesano,
                                           filterRegioneDirettoreDiocesano,
                                           filterRegionePresidenteDiocesano
                                           ),
                      new HorizontalLayout(
                                           filterDirettoreDiocesiano,
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

        filterCognome.setPlaceholder("Inserisci Cognome");
        filterCognome.setValueChangeMode(ValueChangeMode.EAGER);
        filterCognome.addValueChangeListener(e -> {
            searchCognome = e.getValue();
            onChange();
        });
        
        filterTitolo.setPlaceholder("Seleziona Titolo");
        filterTitolo.addSelectionListener(e -> onChange());
        filterCentroDiocesano.setPlaceholder("Seleziona Centro");
        filterCentroDiocesano.addSelectionListener(e -> onChange());
        filterRegioneVescovi.setPlaceholder("Seleziona Regione");
        filterRegioneVescovi.addSelectionListener(e -> onChange());
        filterRegionePresidenteDiocesano.setPlaceholder("Seleziona Regione");
        filterRegionePresidenteDiocesano.addSelectionListener(e -> onChange());
        filterRegioneDirettoreDiocesano.setPlaceholder("Seleziona Regione");
        filterRegioneDirettoreDiocesano.addSelectionListener(e -> onChange());
        filterOmaggio.setPlaceholder("Seleziona Omaggio");
        filterOmaggio.addSelectionListener(e ->onChange());

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
        if (StringUtils.isEmpty(searchCognome) && searchDiocesi == null) {
            return filterAll(findAll());
        }
        if (searchDiocesi == null) {
            return filterAll(((AnagraficaDao) getRepo()).findByCognomeContainingIgnoreCase(searchCognome));
        }
        if (StringUtils.isEmpty(searchCognome)) {
            return filterAll(((AnagraficaDao) getRepo()).findByDiocesi(searchDiocesi));
        }

        return filterAll(((AnagraficaDao) getRepo()).findByCognomeContainingIgnoreCase(searchCognome).stream().filter(tizio -> tizio.getDiocesi().equals(searchDiocesi)).collect(Collectors.toList()));
    }

    private List<Anagrafica> filterAll(List<Anagrafica> anagrafiche) {

        if (filterOmaggio.getValue() != null) {
            final List<Storico> storiciByOmaggio = storicoDao.findByOmaggio(filterOmaggio.getValue());
            anagrafiche=anagrafiche.stream().filter(a -> {
                for (Storico storicoA: storiciByOmaggio) {
                    if (storicoA.getIntestatario().getId() == a.getId()) {
                        return true;
                    }
                }
                return false;
            }).collect(Collectors.toList());
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
