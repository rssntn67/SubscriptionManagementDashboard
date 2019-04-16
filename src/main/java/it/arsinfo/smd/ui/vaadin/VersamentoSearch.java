package it.arsinfo.smd.ui.vaadin;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.util.StringUtils;

import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;

import it.arsinfo.smd.Smd;
import it.arsinfo.smd.entity.Versamento;
import it.arsinfo.smd.repository.VersamentoDao;

public class VersamentoSearch extends SmdSearch<Versamento> {

    String campo;
    String importo;
    LocalDate dataContabile;
    LocalDate dataPagamento;

    public VersamentoSearch(VersamentoDao versamentoDao) {
        super(versamentoDao);
        DateField filterDataContabile = new DateField("Selezionare la data Contabile");
        filterDataContabile.setDateFormat("yyyy-MM-dd");

        DateField filterDataPagamento = new DateField("Selezionare la data Pagamento");
        filterDataPagamento.setDateFormat("yyyy-MM-dd");

        TextField filterImporto = new TextField("Inserire Importo");
        filterImporto.setValueChangeMode(ValueChangeMode.LAZY);

        TextField filterCampo = new TextField("Inserire V Campo ");
        filterCampo.setValueChangeMode(ValueChangeMode.LAZY);
        
        setComponents(new HorizontalLayout(filterCampo, filterImporto, filterDataPagamento,
                                           filterDataContabile));

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
        filterCampo.addValueChangeListener(e -> {
            campo = e.getValue();
            onChange();
        });


    }

    @Override
    public List<Versamento> find() {
        if (StringUtils.isEmpty(importo) && dataContabile == null
                && dataPagamento == null && StringUtils.isEmpty(campo)) {
            return findAll();
        }
        if (!StringUtils.isEmpty(importo)) {
            try {
                new BigDecimal(importo);
            } catch (NumberFormatException e) {
                return new ArrayList<>();
            }
        }
         
        if (dataContabile == null && dataPagamento == null && StringUtils.isEmpty(campo)) {
                return ((VersamentoDao) getRepo())
                    .findByImporto(new BigDecimal(importo));
        }

        if (dataContabile == null && dataPagamento == null && StringUtils.isEmpty(importo)) {
            return ((VersamentoDao) getRepo())
                    .findByCampoContainingIgnoreCase(campo);
        }

        if (StringUtils.isEmpty(importo) && dataPagamento == null && StringUtils.isEmpty(campo)) {
            return ((VersamentoDao) getRepo())
                    .findByDataContabile(Smd.getStandardDate(dataContabile));
        }

        if (StringUtils.isEmpty(importo) && dataContabile == null && StringUtils.isEmpty(campo)) {
            return ((VersamentoDao) getRepo())
                    .findByDataPagamento(Smd.getStandardDate(dataPagamento));
        }

        if (dataContabile == null && dataPagamento == null) {
            return ((VersamentoDao) getRepo())
                    .findByCampoContainingIgnoreCase(campo)
                    .stream()
                    .filter(v-> v.getImporto().compareTo(new BigDecimal(importo)) == 0)
                    .collect(Collectors.toList());
        }


        if (dataContabile == null && StringUtils.isEmpty(campo)) {
            return ((VersamentoDao) getRepo())
                    .findByImporto(new BigDecimal(importo))
                    .stream()
                    .filter(v -> v.getDataPagamento().getTime() == Smd.getStandardDate(dataPagamento).getTime())
                    .collect(Collectors.toList());
        }

        if (dataPagamento == null && StringUtils.isEmpty(campo)) {
            return ((VersamentoDao) getRepo())
                    .findByImporto(new BigDecimal(importo))
                    .stream()
                    .filter(v -> v.getDataContabile().getTime() == Smd.getStandardDate(dataContabile).getTime())
                    .collect(Collectors.toList());
        }

        if (dataContabile == null && StringUtils.isEmpty(importo)) {
            return ((VersamentoDao) getRepo())
                    .findByCampoContainingIgnoreCase(campo)
                    .stream()
                    .filter(v -> v.getDataPagamento().getTime() == Smd.getStandardDate(dataPagamento).getTime())
                    .collect(Collectors.toList());
        }

        if (dataPagamento == null && StringUtils.isEmpty(importo)) {
            return ((VersamentoDao) getRepo())
                    .findByCampoContainingIgnoreCase(campo)
                    .stream()
                    .filter(v -> v.getDataContabile().getTime() == Smd.getStandardDate(dataContabile).getTime())
                    .collect(Collectors.toList());
        }

        if (StringUtils.isEmpty(campo) && StringUtils.isEmpty(importo)) {
            return ((VersamentoDao) getRepo())
                    .findByDataPagamento(Smd.getStandardDate(dataPagamento))
                    .stream()
                    .filter(v -> v.getDataContabile().getTime() == Smd.getStandardDate(dataContabile).getTime())
                    .collect(Collectors.toList());
        }

        if (StringUtils.isEmpty(campo)) {
            return ((VersamentoDao) getRepo())
                    .findByImporto(new BigDecimal(importo))
                    .stream()
                    .filter(v -> 
                       v.getDataContabile().getTime() == Smd.getStandardDate(dataContabile).getTime()
                    && v.getDataPagamento().getTime() == Smd.getStandardDate(dataPagamento).getTime()
                    )
                    .collect(Collectors.toList());
        }

        if (StringUtils.isEmpty(importo)) {
            return ((VersamentoDao) getRepo())
                    .findByCampoContainingIgnoreCase(campo)
                    .stream()
                    .filter(v -> 
                       v.getDataContabile().getTime() == Smd.getStandardDate(dataContabile).getTime()
                    && v.getDataPagamento().getTime() == Smd.getStandardDate(dataPagamento).getTime()
                    )
                    .collect(Collectors.toList());
        }

        if (dataPagamento == null) {
            return ((VersamentoDao) getRepo())
                    .findByCampoContainingIgnoreCase(campo)
                    .stream()
                    .filter(v -> 
                       v.getDataContabile().getTime() == Smd.getStandardDate(dataContabile).getTime()
                    && v.getImporto().compareTo(new BigDecimal(importo)) == 0
                    )
                    .collect(Collectors.toList());
            
        }
        if (dataContabile == null) {
            return ((VersamentoDao) getRepo())
                    .findByCampoContainingIgnoreCase(campo)
                    .stream()
                    .filter(v -> 
                       v.getDataPagamento().getTime() == Smd.getStandardDate(dataPagamento).getTime()
                    && v.getImporto().compareTo(new BigDecimal(importo)) == 0
                    )
                    .collect(Collectors.toList());
            
        }
        return ((VersamentoDao) getRepo())
                .findByCampoContainingIgnoreCase(campo)
                .stream()
                .filter(v -> 
                   v.getDataContabile().getTime() == Smd.getStandardDate(dataContabile).getTime()
                && v.getDataPagamento().getTime() == Smd.getStandardDate(dataPagamento).getTime()
                && v.getImporto().compareTo(new BigDecimal(importo)) == 0
                )
                .collect(Collectors.toList());

    }
        
}
