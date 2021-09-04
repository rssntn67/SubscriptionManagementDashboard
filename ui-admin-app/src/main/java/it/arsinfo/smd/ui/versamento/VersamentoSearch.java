package it.arsinfo.smd.ui.versamento;

import java.time.LocalDate;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;

import it.arsinfo.smd.service.api.VersamentoService;
import it.arsinfo.smd.entity.Cassa;
import it.arsinfo.smd.entity.Ccp;
import it.arsinfo.smd.entity.Cuas;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Versamento;
import it.arsinfo.smd.ui.vaadin.SmdSearch;

public class VersamentoSearch extends SmdSearch<Versamento> {

    private String codeLine;
    private String importo;
    private LocalDate dataContabile;
    private LocalDate dataPagamento;
    
    private final ComboBox<Ccp> filterCcp = new ComboBox<Ccp>("Conto Corrente", EnumSet.allOf(Ccp.class));
    private final ComboBox<Cassa> filterCassa = new ComboBox<Cassa>("Cassa", EnumSet.allOf(Cassa.class));
    private final ComboBox<Cuas> filterCuas = new ComboBox<Cuas>("Cuas", EnumSet.allOf(Cuas.class));

    private Anagrafica committente;

    private final VersamentoService dao;
    public VersamentoSearch(VersamentoService dao, List<Anagrafica> anagrafica) {
        super(dao);
        this.dao=dao;
        
        ComboBox<Anagrafica> filterAnagrafica = new ComboBox<Anagrafica>();
        HorizontalLayout ana = new HorizontalLayout();
        ana.addComponentsAndExpand(filterAnagrafica);
        addComponents(ana);
        
        filterAnagrafica.setEmptySelectionAllowed(true);
        filterAnagrafica.setPlaceholder("Cerca per Anagrafica");
        filterAnagrafica.setItems(anagrafica);
        filterAnagrafica.setItemCaptionGenerator(Anagrafica::getCaption);
        filterAnagrafica.addSelectionListener(e -> {
            if (e.getValue() == null) {
            	committente = null;
            } else {
            	committente = e.getSelectedItem().get();
            }
            onChange();
        });

        DateField filterDataContabile = new DateField("Selezionare la data Contabile");
        filterDataContabile.setDateFormat("dd/MM/yyyy");

        DateField filterDataPagamento = new DateField("Selezionare la data Pagamento");
        filterDataPagamento.setDateFormat("dd/MM/yyyy");

        TextField filterImporto = new TextField("Inserire Importo");
        filterImporto.setValueChangeMode(ValueChangeMode.LAZY);

        TextField filterCodeLine = new TextField("Inserire Code Line ");
        filterCodeLine.setValueChangeMode(ValueChangeMode.LAZY);
        
        setComponents(new HorizontalLayout(filterCodeLine, filterImporto, filterDataPagamento,
                                           filterDataContabile,filterCcp,filterCassa,filterCuas),ana);

        filterDataContabile.addValueChangeListener(e -> {
            dataContabile = e.getValue();
            onChange();
        });
        filterDataPagamento.addValueChangeListener(e -> {
            dataPagamento = e.getValue();
            onChange();
        });
        filterImporto.addValueChangeListener(e -> {
            importo = e.getValue();
            onChange();
        });
        filterCodeLine.addValueChangeListener(e -> {
            codeLine = e.getValue();
            onChange();
        });
        
        filterCcp.setEmptySelectionAllowed(true);
        filterCcp.setItemCaptionGenerator(Ccp::getCcp);
        filterCcp.setPlaceholder("Cerca per Conto Corrente");
        filterCcp.addSelectionListener(e -> {
            onChange();
        });
        
        filterCassa.setEmptySelectionAllowed(true);
        filterCassa.setPlaceholder("Cerca per Cassa");
        filterCassa.addSelectionListener(e -> {
            onChange();
        });

        filterCuas.setEmptySelectionAllowed(true);
        filterCuas.setPlaceholder("Cerca per CUAS");
        filterCuas.setItemCaptionGenerator(Cuas::getDenominazione);
        filterCuas.addSelectionListener(e -> {
            onChange();
        });


    }

    @Override
    public List<Versamento> find() {
    	return filterAll(dao.searchBy(importo,dataContabile,dataPagamento,codeLine));
    }
     
    private List<Versamento> filterAll(List<Versamento> versamenti) {
        if (filterCassa.getValue() != null) {
            versamenti = versamenti
                    .stream()
                    .filter(v -> v.getDistintaVersamento().getCassa() == filterCassa.getValue())
                    .collect(Collectors.toList());
        }
        if (filterCcp.getValue() != null) {
            versamenti = versamenti
                    .stream()
                    .filter(v -> v.getDistintaVersamento().getCcp() == filterCcp.getValue())
                    .collect(Collectors.toList());
        }
        if (filterCuas.getValue() != null) {
            versamenti = versamenti
                    .stream()
                    .filter(v -> v.getDistintaVersamento().getCuas() == filterCuas.getValue())
                    .collect(Collectors.toList());
        }       
    	if (committente != null) {
    		versamenti = versamenti
    				.stream()
    				.filter(v -> committente.equals(v.getCommittente()))
    				.collect(Collectors.toList());
    	}  
        return versamenti;
    }
}
