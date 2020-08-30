package it.arsinfo.smd.ui.abbonamento;

import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.util.StringUtils;

import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;

import it.arsinfo.smd.dao.AbbonamentoServiceDao;
import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.Ccp;
import it.arsinfo.smd.data.StatoAbbonamento;
import it.arsinfo.smd.data.TipoAbbonamentoRivista;
import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Campagna;
import it.arsinfo.smd.entity.Pubblicazione;
import it.arsinfo.smd.ui.vaadin.SmdSearch;

public class AbbonamentoSearch extends SmdSearch<Abbonamento> {

    private String searchCodeLine;
    private Anagrafica customer;
    private Anno anno;
    private Campagna campagna;
    private final ComboBox<Ccp> filterCcp = new ComboBox<Ccp>();
    private final CheckBox filterContrassegno = new CheckBox("Contrassegno");
    private final ComboBox<StatoAbbonamento> filterStatoAbbonamento= new ComboBox<StatoAbbonamento>();
    private Pubblicazione pubblicazione;
    private final ComboBox<TipoAbbonamentoRivista> filterTipoAbbonamentoRivista = new ComboBox<TipoAbbonamentoRivista>();
    
    private final AbbonamentoServiceDao dao;

    public AbbonamentoSearch(AbbonamentoServiceDao dao, List<Campagna> campagne, List<Pubblicazione> pubblicazioni,
    		List<Anagrafica> anagrafica) {
        super(dao);

        this.dao=dao;
        ComboBox<Anagrafica> filterAnagrafica = new ComboBox<Anagrafica>();
        ComboBox<Anno> filterAnno = new ComboBox<Anno>();
        ComboBox<Campagna> filterCampagna = new ComboBox<Campagna>();
        ComboBox<Pubblicazione> filterPubblicazione = new ComboBox<Pubblicazione>();
        
        TextField filterCodeLine = new TextField();

        HorizontalLayout anag = new HorizontalLayout(filterPubblicazione,filterStatoAbbonamento,filterContrassegno);
        anag.addComponentsAndExpand(filterAnagrafica);
        HorizontalLayout tipo = new HorizontalLayout(filterAnno,filterCodeLine,filterCampagna,filterCcp);
        tipo.addComponentsAndExpand(filterTipoAbbonamentoRivista);
        
        setComponents(anag,tipo);
        filterCodeLine.setPlaceholder("Inserisci Code Line");
        filterCodeLine.setValueChangeMode(ValueChangeMode.LAZY);
        filterCodeLine.addValueChangeListener(e -> {
            searchCodeLine = e.getValue();
            onChange();
        });

        filterCampagna.setEmptySelectionAllowed(true);
        filterCampagna.setPlaceholder("Cerca per Campagna");
        filterCampagna.setItems(campagne);
        filterCampagna.setItemCaptionGenerator(Campagna::getCaption);
        filterCampagna.addSelectionListener(e -> {
            if (e.getValue() == null) {
                campagna = null;
            } else {
                campagna=e.getSelectedItem().get();
            }
            onChange();
        });

        filterPubblicazione.setEmptySelectionAllowed(true);
        filterPubblicazione.setPlaceholder("Cerca per Pubblicazioni");
        filterPubblicazione.setItems(pubblicazioni);
        filterPubblicazione.setItemCaptionGenerator(Pubblicazione::getNome);
        filterPubblicazione.addSelectionListener(e -> {
            if (e.getValue() == null) {
                pubblicazione = null;
            } else {
                pubblicazione = e.getSelectedItem().get();
            }
            onChange();
        });

        filterAnno.setEmptySelectionAllowed(true);
        filterAnno.setPlaceholder("Cerca per Anno");
        filterAnno.setItems(EnumSet.allOf(Anno.class));
        filterAnno.setItemCaptionGenerator(Anno::getAnnoAsString);
        filterAnno.addSelectionListener(e -> {
            if (e.getValue() == null) {
                anno = null;
            } else {
                anno=e.getSelectedItem().get();
            }
            onChange();
        });

        filterAnagrafica.setEmptySelectionAllowed(true);
        filterAnagrafica.setPlaceholder("Cerca per Anagrafica");
        filterAnagrafica.setItems(anagrafica);
        filterAnagrafica.setItemCaptionGenerator(Anagrafica::getCaption);
        filterAnagrafica.addSelectionListener(e -> {
            if (e.getValue() == null) {
                customer = null;
            } else {
                customer = e.getSelectedItem().get();
            }
            onChange();
        });
        
        filterContrassegno.addValueChangeListener(e -> onChange());

        filterStatoAbbonamento.setPlaceholder("Cerca per Stato");
        filterStatoAbbonamento.setItems(EnumSet.allOf(StatoAbbonamento.class));
        filterStatoAbbonamento.addSelectionListener(e ->onChange());

        filterCcp.setPlaceholder("Cerca per Cc");
        filterCcp.setItems(EnumSet.allOf(Ccp.class));
        filterCcp.setItemCaptionGenerator(Ccp::getCcp);
        filterCcp.addSelectionListener(e ->onChange());
        
        filterTipoAbbonamentoRivista.setPlaceholder("Cerca per Tipo");
        filterTipoAbbonamentoRivista.setItems(EnumSet.allOf(TipoAbbonamentoRivista.class));
        filterTipoAbbonamentoRivista.addSelectionListener(e ->onChange());


    }

    @Override
    public List<Abbonamento> find() {
    	return filterAll(
    			dao.searchBy(
    					campagna,
    					customer,
    					anno,
    					pubblicazione,
    					filterTipoAbbonamentoRivista.getValue(),
    					filterStatoAbbonamento.getValue()));
    }

    private List<Abbonamento> filterAll(List<Abbonamento> abbonamenti) {
        if (filterCcp.getValue() != null) {
            abbonamenti=abbonamenti.stream().filter(a -> a.getCcp() == filterCcp.getValue()).collect(Collectors.toList());      
        }
        if (filterContrassegno.getValue() != null) {
            abbonamenti=abbonamenti.stream().filter(a -> a.isContrassegno() == filterContrassegno.getValue()).collect(Collectors.toList());      
        }
        if (!StringUtils.isEmpty(searchCodeLine)) {
            abbonamenti=abbonamenti.stream().filter(a -> a.getCodeLine().toLowerCase().contains(searchCodeLine.toLowerCase())).collect(Collectors.toList());                  
        }
        return abbonamenti;
    }
}
