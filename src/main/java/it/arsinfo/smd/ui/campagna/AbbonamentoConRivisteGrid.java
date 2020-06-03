package it.arsinfo.smd.ui.campagna;

import java.math.BigDecimal;
import java.util.List;

import com.vaadin.ui.Grid;
import com.vaadin.ui.components.grid.FooterRow;

import it.arsinfo.smd.dto.AbbonamentoConRiviste;
import it.arsinfo.smd.ui.vaadin.SmdGrid;

public class AbbonamentoConRivisteGrid extends SmdGrid<AbbonamentoConRiviste> {

    private final FooterRow gridfooter;

    public AbbonamentoConRivisteGrid(String gridName) {
        super(new Grid<>(AbbonamentoConRiviste.class),gridName);
        setColumns(
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
                "anno");
        setColumnCaption("captionBrief", "Intestatario");		
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
            importo=importo.add(abbonamento.getImportoMessaggiBG());
        }
        return importo;
    }

    private BigDecimal getImportoLodare(List<AbbonamentoConRiviste> abbonamenti) {
        BigDecimal importo = BigDecimal.ZERO;
        for (AbbonamentoConRiviste abbonamento:abbonamenti) {
            importo=importo.add(abbonamento.getImportoLodareBG());
        }
        return importo;
    }

    private BigDecimal getImportoBlocchetti(List<AbbonamentoConRiviste> abbonamenti) {
        BigDecimal importo = BigDecimal.ZERO;
        for (AbbonamentoConRiviste abbonamento:abbonamenti) {
            importo=importo.add(abbonamento.getImportoBlocchettiBG());
        }
        return importo;
    }

    private BigDecimal getImportoManifesti(List<AbbonamentoConRiviste> abbonamenti) {
        BigDecimal importo = BigDecimal.ZERO;
        for (AbbonamentoConRiviste abbonamento:abbonamenti) {
            importo=importo.add(abbonamento.getImportoManifestiBG());
        }
        return importo;
    }

    private BigDecimal getPregresso(List<AbbonamentoConRiviste> abbonamenti) {
        BigDecimal importo = BigDecimal.ZERO;
        for (AbbonamentoConRiviste abbonamento:abbonamenti) {
            importo=importo.add(abbonamento.getPregressoBG());
        }
        return importo;
    }


    private BigDecimal getSpesePostali(List<AbbonamentoConRiviste> abbonamenti) {
        BigDecimal importo = BigDecimal.ZERO;
        for (AbbonamentoConRiviste abbonamento:abbonamenti) {
            importo=importo.add(abbonamento.getSpesePostaliBG());
        }
        return importo;
    }

    private BigDecimal getSpeseEstrattoConto(List<AbbonamentoConRiviste> abbonamenti) {
        BigDecimal importo = BigDecimal.ZERO;
        for (AbbonamentoConRiviste abbonamento:abbonamenti) {
            importo=importo.add(abbonamento.getSpeseEstrattoContoBG());
        }
        return importo;
    }

    private BigDecimal getTotaleImporti(List<AbbonamentoConRiviste> abbonamenti) {
        BigDecimal importo = BigDecimal.ZERO;
        for (AbbonamentoConRiviste abbonamento:abbonamenti) {
            importo=importo.add(abbonamento.getTotaleImportiBG());
        }
        return importo;
    }

    private BigDecimal getTotale(List<AbbonamentoConRiviste> abbonamenti) {
        BigDecimal importo = BigDecimal.ZERO;
        for (AbbonamentoConRiviste abbonamento:abbonamenti) {
            importo=importo.add(abbonamento.getTotaleBG());
        }
        return importo;
    }

    private BigDecimal getIncassato(List<AbbonamentoConRiviste> abbonamenti) {
        BigDecimal importo = BigDecimal.ZERO;
        for (AbbonamentoConRiviste abb:abbonamenti) {
            if (abb.getIncassato() != null)
                importo=importo.add(abb.getIncassatoBG());
        }
        return importo;
    }

    private BigDecimal getSaldo(List<AbbonamentoConRiviste> abbonamenti) {
        BigDecimal importo = BigDecimal.ZERO;
        for (AbbonamentoConRiviste abbonamneto:abbonamenti) {
            importo=importo.add(abbonamneto.getSaldoBG());
        }
        return importo;
    }


}
