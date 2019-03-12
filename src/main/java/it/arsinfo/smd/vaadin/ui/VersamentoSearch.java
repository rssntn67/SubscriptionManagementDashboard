package it.arsinfo.smd.vaadin.ui;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.util.StringUtils;

import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;

import it.arsinfo.smd.entity.Versamento;
import it.arsinfo.smd.repository.VersamentoDao;
import it.arsinfo.smd.vaadin.model.SmdSearch;

public class VersamentoSearch extends SmdSearch<Versamento> {

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

        setComponents(new HorizontalLayout(filterImporto, filterDataPagamento,
                                           filterDataContabile));

        filterDataContabile.addValueChangeListener(e -> {
            dataContabile = e.getValue();
            onChange();
        });
        filterDataPagamento.addValueChangeListener(e -> {
            dataPagamento = e.getValue();
            onChange();
        });
        filterImporto.setValueChangeMode(ValueChangeMode.LAZY);
        filterImporto.addValueChangeListener(e -> {
            importo = e.getValue();
            onChange();
        });

    }

    @Override
    public List<Versamento> find() {
        if (StringUtils.isEmpty(importo) && dataContabile == null
                && dataPagamento == null) {
            return findAll();
        }
        if (dataContabile == null && dataPagamento == null) {
            try {
                return ((VersamentoDao) getRepo()).findByImporto(new BigDecimal(importo));
            } catch (NumberFormatException e) {
                return new ArrayList<>();
            }
        }
        if (StringUtils.isEmpty(importo) && dataPagamento == null) {
            return ((VersamentoDao) getRepo()).findByDataContabile(java.util.Date.from(dataContabile.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
        }

        if (StringUtils.isEmpty(importo) && dataContabile == null) {
            return ((VersamentoDao) getRepo()).findByDataPagamento(java.util.Date.from(dataPagamento.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
        }

        if (dataContabile == null) {
            try {
                return ((VersamentoDao) getRepo()).findByImporto(new BigDecimal(importo)).stream().filter(v -> v.getDataPagamento().getTime() == java.util.Date.from(dataPagamento.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()).getTime()).collect(Collectors.toList());
            } catch (NumberFormatException e) {
                return new ArrayList<>();
            }
        }

        if (dataPagamento == null) {
            try {
                return ((VersamentoDao) getRepo()).findByImporto(new BigDecimal(importo)).stream().filter(v -> v.getDataContabile().getTime() == java.util.Date.from(dataContabile.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()).getTime()).collect(Collectors.toList());
            } catch (NumberFormatException e) {
                return new ArrayList<>();
            }
        }

        try {
            return ((VersamentoDao) getRepo()).findByImporto(new BigDecimal(importo)).stream().filter(v -> v.getDataContabile().getTime() == java.util.Date.from(dataContabile.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()).getTime()
                    && v.getDataPagamento().getTime() == java.util.Date.from(dataPagamento.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()).getTime()).collect(Collectors.toList());
        } catch (NumberFormatException e) {
            return new ArrayList<>();
        }
    }
}
