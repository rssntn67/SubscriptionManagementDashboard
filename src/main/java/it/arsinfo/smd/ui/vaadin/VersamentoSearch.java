package it.arsinfo.smd.ui.vaadin;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.util.StringUtils;

import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;

import it.arsinfo.smd.Smd;
import it.arsinfo.smd.data.Cassa;
import it.arsinfo.smd.data.Ccp;
import it.arsinfo.smd.data.Cuas;
import it.arsinfo.smd.entity.Versamento;
import it.arsinfo.smd.repository.VersamentoDao;

public class VersamentoSearch extends SmdSearch<Versamento> {

    private String codeLine;
    private String importo;
    private LocalDate dataContabile;
    private LocalDate dataPagamento;
    
    private final ComboBox<Ccp> filterCcp = new ComboBox<Ccp>("Conto Corrente", EnumSet.allOf(Ccp.class));
    private final ComboBox<Cassa> filterCassa = new ComboBox<Cassa>("Cassa", EnumSet.allOf(Cassa.class));
    private final ComboBox<Cuas> filterCuas = new ComboBox<Cuas>("Cuas", EnumSet.allOf(Cuas.class));
  
    public VersamentoSearch(VersamentoDao versamentoDao) {
        super(versamentoDao);
        DateField filterDataContabile = new DateField("Selezionare la data Contabile");
        filterDataContabile.setDateFormat("yyyy-MM-dd");

        DateField filterDataPagamento = new DateField("Selezionare la data Pagamento");
        filterDataPagamento.setDateFormat("yyyy-MM-dd");

        TextField filterImporto = new TextField("Inserire Importo");
        filterImporto.setValueChangeMode(ValueChangeMode.LAZY);

        TextField filterCodeLine = new TextField("Inserire Code Line ");
        filterCodeLine.setValueChangeMode(ValueChangeMode.LAZY);
        
        setComponents(new HorizontalLayout(filterCodeLine, filterImporto, filterDataPagamento,
                                           filterDataContabile,filterCcp,filterCassa,filterCuas));

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
        if (StringUtils.isEmpty(importo) && dataContabile == null
                && dataPagamento == null && StringUtils.isEmpty(codeLine)) {
            return filterAll(findAll());
        }
        if (!StringUtils.isEmpty(importo)) {
            try {
                new BigDecimal(importo);
            } catch (NumberFormatException e) {
                return new ArrayList<>();
            }
        }
         
        if (dataContabile == null && dataPagamento == null && StringUtils.isEmpty(codeLine)) {
                return filterAll(((VersamentoDao) getRepo())
                    .findByImporto(new BigDecimal(importo)));
        }

        if (dataContabile == null && dataPagamento == null && StringUtils.isEmpty(importo)) {
            return filterAll(((VersamentoDao) getRepo())
                    .findByCodeLineContainingIgnoreCase(codeLine));
        }

        if (StringUtils.isEmpty(importo) && dataPagamento == null && StringUtils.isEmpty(codeLine)) {
            return filterAll(((VersamentoDao) getRepo())
                    .findByDataContabile(Smd.getStandardDate(dataContabile)));
        }

        if (StringUtils.isEmpty(importo) && dataContabile == null && StringUtils.isEmpty(codeLine)) {
            return filterAll(((VersamentoDao) getRepo())
                    .findByDataPagamento(Smd.getStandardDate(dataPagamento)));
        }

        if (dataContabile == null && dataPagamento == null) {
            return filterAll(((VersamentoDao) getRepo())
                    .findByCodeLineContainingIgnoreCase(codeLine)
                    .stream()
                    .filter(v-> v.getImporto().compareTo(new BigDecimal(importo)) == 0)
                    .collect(Collectors.toList()));
        }


        if (dataContabile == null && StringUtils.isEmpty(codeLine)) {
            return filterAll(((VersamentoDao) getRepo())
                    .findByImporto(new BigDecimal(importo))
                    .stream()
                    .filter(v -> v.getDataPagamento().getTime() == Smd.getStandardDate(dataPagamento).getTime())
                    .collect(Collectors.toList()));
        }

        if (dataPagamento == null && StringUtils.isEmpty(codeLine)) {
            return filterAll(((VersamentoDao) getRepo())
                    .findByImporto(new BigDecimal(importo))
                    .stream()
                    .filter(v -> v.getDataContabile().getTime() == Smd.getStandardDate(dataContabile).getTime())
                    .collect(Collectors.toList()));
        }

        if (dataContabile == null && StringUtils.isEmpty(importo)) {
            return filterAll(((VersamentoDao) getRepo())
                    .findByCodeLineContainingIgnoreCase(codeLine)
                    .stream()
                    .filter(v -> v.getDataPagamento().getTime() == Smd.getStandardDate(dataPagamento).getTime())
                    .collect(Collectors.toList()));
        }

        if (dataPagamento == null && StringUtils.isEmpty(importo)) {
            return filterAll(((VersamentoDao) getRepo())
                    .findByCodeLineContainingIgnoreCase(codeLine)
                    .stream()
                    .filter(v -> v.getDataContabile().getTime() == Smd.getStandardDate(dataContabile).getTime())
                    .collect(Collectors.toList()));
        }

        if (StringUtils.isEmpty(codeLine) && StringUtils.isEmpty(importo)) {
            return filterAll(((VersamentoDao) getRepo())
                    .findByDataPagamento(Smd.getStandardDate(dataPagamento))
                    .stream()
                    .filter(v -> v.getDataContabile().getTime() == Smd.getStandardDate(dataContabile).getTime())
                    .collect(Collectors.toList()));
        }

        if (StringUtils.isEmpty(codeLine)) {
            return filterAll(((VersamentoDao) getRepo())
                    .findByImporto(new BigDecimal(importo))
                    .stream()
                    .filter(v -> 
                       v.getDataContabile().getTime() == Smd.getStandardDate(dataContabile).getTime()
                    && v.getDataPagamento().getTime() == Smd.getStandardDate(dataPagamento).getTime()
                    )
                    .collect(Collectors.toList()));
        }

        if (StringUtils.isEmpty(importo)) {
            return filterAll(((VersamentoDao) getRepo())
                    .findByCodeLineContainingIgnoreCase(codeLine)
                    .stream()
                    .filter(v -> 
                       v.getDataContabile().getTime() == Smd.getStandardDate(dataContabile).getTime()
                    && v.getDataPagamento().getTime() == Smd.getStandardDate(dataPagamento).getTime()
                    )
                    .collect(Collectors.toList()));
        }

        if (dataPagamento == null) {
            return filterAll(((VersamentoDao) getRepo())
                    .findByCodeLineContainingIgnoreCase(codeLine)
                    .stream()
                    .filter(v -> 
                       v.getDataContabile().getTime() == Smd.getStandardDate(dataContabile).getTime()
                    && v.getImporto().compareTo(new BigDecimal(importo)) == 0
                    )
                    .collect(Collectors.toList()));
            
        }
        if (dataContabile == null) {
            return filterAll(((VersamentoDao) getRepo())
                    .findByCodeLineContainingIgnoreCase(codeLine)
                    .stream()
                    .filter(v -> 
                       v.getDataPagamento().getTime() == Smd.getStandardDate(dataPagamento).getTime()
                    && v.getImporto().compareTo(new BigDecimal(importo)) == 0
                    )
                    .collect(Collectors.toList()));
            
        }
        return filterAll(((VersamentoDao) getRepo())
                .findByCodeLineContainingIgnoreCase(codeLine)
                .stream()
                .filter(v -> 
                   v.getDataContabile().getTime() == Smd.getStandardDate(dataContabile).getTime()
                && v.getDataPagamento().getTime() == Smd.getStandardDate(dataPagamento).getTime()
                && v.getImporto().compareTo(new BigDecimal(importo)) == 0
                )
                .collect(Collectors.toList()));

    }
     
    private List<Versamento> filterAll(List<Versamento> versamenti) {
        if (filterCassa.getValue() != null) {
            versamenti = versamenti
                    .stream()
                    .filter(v -> v.getIncasso().getCassa() == filterCassa.getValue())
                    .collect(Collectors.toList());
        }
        if (filterCcp.getValue() != null) {
            versamenti = versamenti
                    .stream()
                    .filter(v -> v.getIncasso().getCcp() == filterCcp.getValue())
                    .collect(Collectors.toList());
        }
        if (filterCuas.getValue() != null) {
            versamenti = versamenti
                    .stream()
                    .filter(v -> v.getIncasso().getCuas() == filterCuas.getValue())
                    .collect(Collectors.toList());
        }
        return versamenti;
    }
}
