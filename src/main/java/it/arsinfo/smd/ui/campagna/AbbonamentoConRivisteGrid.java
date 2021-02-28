package it.arsinfo.smd.ui.campagna;

import java.math.BigDecimal;
import java.util.List;

import com.vaadin.ui.Grid;
import com.vaadin.ui.components.grid.FooterRow;

import it.arsinfo.smd.dto.AbbonamentoConRiviste;
import it.arsinfo.smd.ui.EuroConverter;
import it.arsinfo.smd.ui.vaadin.SmdGrid;

public class AbbonamentoConRivisteGrid extends SmdGrid<AbbonamentoConRiviste> {

    private final FooterRow gridfooter;

    public AbbonamentoConRivisteGrid(String gridName) {
        super(new Grid<>(AbbonamentoConRiviste.class),gridName);
        getGrid().addColumn("codeLine");
        getGrid().addColumn("intestazione");
        getGrid().addColumn("sottoIntestazione");
        getGrid().addColumn("indirizzo");
        getGrid().addColumn("citta");
        getGrid().addColumn("cap");
        getGrid().addColumn("provincia");
        getGrid().addColumn("paese");
        getGrid().addColumn("numeroMessaggi");
        getGrid().addColumn("numeroBlocchetti");
        getGrid().addColumn("numeroManifesti");
        getGrid().addColumn("numeroLodare");
        getGrid().addColumn("importoMessaggi",EuroConverter.getEuroRenderer());
        getGrid().addColumn("importoBlocchetti",EuroConverter.getEuroRenderer());
        getGrid().addColumn("importoManifesti",EuroConverter.getEuroRenderer());
        getGrid().addColumn("importoLodare",EuroConverter.getEuroRenderer());
        getGrid().addColumn("spesePostali",EuroConverter.getEuroRenderer());
        getGrid().addColumn("speseEstrattoConto",EuroConverter.getEuroRenderer());
        getGrid().addColumn("totaleImporti",EuroConverter.getEuroRenderer());
        getGrid().addColumn("pregresso",EuroConverter.getEuroRenderer());
        getGrid().addColumn("totale",EuroConverter.getEuroRenderer());
        getGrid().addColumn("incassato",EuroConverter.getEuroRenderer());
        getGrid().addColumn("riportoAnnoPrecedente",EuroConverter.getEuroRenderer());
        getGrid().addColumn("saldo",EuroConverter.getEuroRenderer());
        getGrid().addColumn("anno.anno").setCaption("Anno");

        getGrid().setColumnOrder(
        		"codeLine",
        		"intestazione",
        		"sottoIntestazione",
        		"indirizzo",
        		"citta",
        		"cap",
        		"provincia",
        		"paese",
        		"numeroMessaggi",
        		"numeroBlocchetti",
        		"numeroManifesti",
           		"numeroLodare",
        		"importoMessaggi",
        		"importoBlocchetti",
        		"importoManifesti",
           		"importoLodare",
                "spesePostali",
                "speseEstrattoConto",
                "totaleImporti",
                "pregresso",
                "totale",
                "incassato",
                "riportoAnnoPrecedente",
                "saldo",
                "anno.anno"
                );
        gridfooter = getGrid().prependFooterRow();
	}

    @Override
    public void populate(List<AbbonamentoConRiviste> items) {
        super.populate(items);
        gridfooter.getCell("numeroMessaggi").setHtml("<b>"+getNumeroMessaggi(items).toString()+"</b>");
        gridfooter.getCell("numeroBlocchetti").setHtml("<b>"+getNumeroBlocchetti(items).toString()+"</b>");
        gridfooter.getCell("numeroManifesti").setHtml("<b>"+getNumeroManifesti(items).toString()+"</b>");
        gridfooter.getCell("numeroLodare").setHtml("<b>"+getNumeroLodare(items).toString()+"</b>");
        gridfooter.getCell("importoMessaggi").setHtml("<b>"+getImportoMessaggi(items).toString()+"</b>");
        gridfooter.getCell("importoBlocchetti").setHtml("<b>"+getImportoBlocchetti(items).toString()+"</b>");
        gridfooter.getCell("importoManifesti").setHtml("<b>"+getImportoManifesti(items).toString()+"</b>");
        gridfooter.getCell("importoLodare").setHtml("<b>"+getImportoLodare(items).toString()+"</b>");
        gridfooter.getCell("spesePostali").setHtml("<b>"+getSpesePostali(items).toString()+"</b>");
        gridfooter.getCell("speseEstrattoConto").setHtml("<b>"+getSpeseEstrattoConto(items).toString()+"</b>");
        gridfooter.getCell("totaleImporti").setHtml("<b>"+getTotaleImporti(items).toString()+"</b>");
        gridfooter.getCell("pregresso").setHtml("<b>"+getPregresso(items).toString()+"</b>");
        gridfooter.getCell("totale").setHtml("<b>"+getTotale(items).toString()+"</b>");
        gridfooter.getCell("incassato").setHtml("<b>"+getIncassato(items).toString()+"</b>");
        gridfooter.getCell("saldo").setHtml("<b>"+getSaldo(items).toString()+"</b>"); 
    }
    
    private Integer getNumeroMessaggi(List<AbbonamentoConRiviste> items) {
    	Integer numero = 0;
    	for (AbbonamentoConRiviste abbec: items) {
    		numero+=abbec.getTotaleMessaggi();
    	}
    	return numero;
    }
    private Integer getNumeroLodare(List<AbbonamentoConRiviste> items) {
    	Integer numero = 0;
    	for (AbbonamentoConRiviste abbec: items) {
    		numero+=abbec.getTotaleLodare();
    	}
    	return numero;
    }
    private Integer getNumeroManifesti(List<AbbonamentoConRiviste> items) {
    	Integer numero = 0;
    	for (AbbonamentoConRiviste abbec: items) {
    		numero+=abbec.getTotaleManifesti();
    	}
    	return numero;
    }
    private Integer getNumeroBlocchetti(List<AbbonamentoConRiviste> items) {
    	Integer numero = 0;
    	for (AbbonamentoConRiviste abbec: items) {
    		numero+=abbec.getTotaleBlocchetti();
    	}
    	return numero;
    }

    private BigDecimal getImportoMessaggi(List<AbbonamentoConRiviste> abbonamenti) {
        BigDecimal importo = BigDecimal.ZERO;
        for (AbbonamentoConRiviste abbonamento:abbonamenti) {
            importo=importo.add(abbonamento.getImportoMessaggi());
        }
        return importo;
    }

    private BigDecimal getImportoLodare(List<AbbonamentoConRiviste> abbonamenti) {
        BigDecimal importo = BigDecimal.ZERO;
        for (AbbonamentoConRiviste abbonamento:abbonamenti) {
            importo=importo.add(abbonamento.getImportoLodare());
        }
        return importo;
    }

    private BigDecimal getImportoBlocchetti(List<AbbonamentoConRiviste> abbonamenti) {
        BigDecimal importo = BigDecimal.ZERO;
        for (AbbonamentoConRiviste abbonamento:abbonamenti) {
            importo=importo.add(abbonamento.getImportoBlocchetti());
        }
        return importo;
    }

    private BigDecimal getImportoManifesti(List<AbbonamentoConRiviste> abbonamenti) {
        BigDecimal importo = BigDecimal.ZERO;
        for (AbbonamentoConRiviste abbonamento:abbonamenti) {
            importo=importo.add(abbonamento.getImportoManifesti());
        }
        return importo;
    }

    private BigDecimal getPregresso(List<AbbonamentoConRiviste> abbonamenti) {
        BigDecimal importo = BigDecimal.ZERO;
        for (AbbonamentoConRiviste abbonamento:abbonamenti) {
            importo=importo.add(abbonamento.getPregresso());
        }
        return importo;
    }


    private BigDecimal getSpesePostali(List<AbbonamentoConRiviste> abbonamenti) {
        BigDecimal importo = BigDecimal.ZERO;
        for (AbbonamentoConRiviste abbonamento:abbonamenti) {
            importo=importo.add(abbonamento.getSpesePostali());
        }
        return importo;
    }

    private BigDecimal getSpeseEstrattoConto(List<AbbonamentoConRiviste> abbonamenti) {
        BigDecimal importo = BigDecimal.ZERO;
        for (AbbonamentoConRiviste abbonamento:abbonamenti) {
            importo=importo.add(abbonamento.getSpeseEstrattoConto());
        }
        return importo;
    }

    private BigDecimal getTotaleImporti(List<AbbonamentoConRiviste> abbonamenti) {
        BigDecimal importo = BigDecimal.ZERO;
        for (AbbonamentoConRiviste abbonamento:abbonamenti) {
            importo=importo.add(abbonamento.getTotaleImporti());
        }
        return importo;
    }

    private BigDecimal getTotale(List<AbbonamentoConRiviste> abbonamenti) {
        BigDecimal importo = BigDecimal.ZERO;
        for (AbbonamentoConRiviste abbonamento:abbonamenti) {
            importo=importo.add(abbonamento.getTotale());
        }
        return importo;
    }

    private BigDecimal getIncassato(List<AbbonamentoConRiviste> abbonamenti) {
        BigDecimal importo = BigDecimal.ZERO;
        for (AbbonamentoConRiviste abb:abbonamenti) {
            if (abb.getIncassato() != null)
                importo=importo.add(abb.getIncassato());
        }
        return importo;
    }

    private BigDecimal getSaldo(List<AbbonamentoConRiviste> abbonamenti) {
        BigDecimal importo = BigDecimal.ZERO;
        for (AbbonamentoConRiviste abbonamneto:abbonamenti) {
            importo=importo.add(abbonamneto.getSaldo());
        }
        return importo;
    }


}
