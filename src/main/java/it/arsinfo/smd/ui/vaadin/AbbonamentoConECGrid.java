package it.arsinfo.smd.ui.vaadin;

import java.math.BigDecimal;
import java.util.List;

import com.vaadin.ui.Grid;
import com.vaadin.ui.components.grid.FooterRow;

import it.arsinfo.smd.dto.AbbonamentoConEC;

public class AbbonamentoConECGrid extends SmdGrid<AbbonamentoConEC> {

    private final FooterRow gridfooter;

    public AbbonamentoConECGrid(String gridName) {
        super(new Grid<>(AbbonamentoConEC.class),gridName);
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
                "saldo",
                "anno");
        setColumnCaption("captionBrief", "Intestatario");		
        gridfooter = getGrid().prependFooterRow();
	}

    @Override
    public void populate(List<AbbonamentoConEC> items) {
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
    
    private Integer getNumeroMessaggi(List<AbbonamentoConEC> items) {
    	Integer numero = 0;
    	for (AbbonamentoConEC abbec: items) {
    		numero+=abbec.getTotaleMessaggi();
    	}
    	return numero;
    }
    private Integer getNumeroLodare(List<AbbonamentoConEC> items) {
    	Integer numero = 0;
    	for (AbbonamentoConEC abbec: items) {
    		numero+=abbec.getTotaleLodare();
    	}
    	return numero;
    }
    private Integer getNumeroManifesti(List<AbbonamentoConEC> items) {
    	Integer numero = 0;
    	for (AbbonamentoConEC abbec: items) {
    		numero+=abbec.getTotaleManifesti();
    	}
    	return numero;
    }
    private Integer getNumeroBlocchetti(List<AbbonamentoConEC> items) {
    	Integer numero = 0;
    	for (AbbonamentoConEC abbec: items) {
    		numero+=abbec.getTotaleBlocchetti();
    	}
    	return numero;
    }

    private BigDecimal getImportoMessaggi(List<AbbonamentoConEC> abbonamenti) {
        BigDecimal importo = BigDecimal.ZERO;
        for (AbbonamentoConEC abbonamento:abbonamenti) {
            importo=importo.add(abbonamento.getImportoMessaggiBG());
        }
        return importo;
    }

    private BigDecimal getImportoLodare(List<AbbonamentoConEC> abbonamenti) {
        BigDecimal importo = BigDecimal.ZERO;
        for (AbbonamentoConEC abbonamento:abbonamenti) {
            importo=importo.add(abbonamento.getImportoLodareBG());
        }
        return importo;
    }

    private BigDecimal getImportoBlocchetti(List<AbbonamentoConEC> abbonamenti) {
        BigDecimal importo = BigDecimal.ZERO;
        for (AbbonamentoConEC abbonamento:abbonamenti) {
            importo=importo.add(abbonamento.getImportoBlocchettiBG());
        }
        return importo;
    }

    private BigDecimal getImportoManifesti(List<AbbonamentoConEC> abbonamenti) {
        BigDecimal importo = BigDecimal.ZERO;
        for (AbbonamentoConEC abbonamento:abbonamenti) {
            importo=importo.add(abbonamento.getImportoManifestiBG());
        }
        return importo;
    }

    private BigDecimal getPregresso(List<AbbonamentoConEC> abbonamenti) {
        BigDecimal importo = BigDecimal.ZERO;
        for (AbbonamentoConEC abbonamento:abbonamenti) {
            importo=importo.add(abbonamento.getPregressoBG());
        }
        return importo;
    }


    private BigDecimal getSpesePostali(List<AbbonamentoConEC> abbonamenti) {
        BigDecimal importo = BigDecimal.ZERO;
        for (AbbonamentoConEC abbonamento:abbonamenti) {
            importo=importo.add(abbonamento.getSpesePostaliBG());
        }
        return importo;
    }

    private BigDecimal getSpeseEstrattoConto(List<AbbonamentoConEC> abbonamenti) {
        BigDecimal importo = BigDecimal.ZERO;
        for (AbbonamentoConEC abbonamento:abbonamenti) {
            importo=importo.add(abbonamento.getSpeseEstrattoContoBG());
        }
        return importo;
    }

    private BigDecimal getTotaleImporti(List<AbbonamentoConEC> abbonamenti) {
        BigDecimal importo = BigDecimal.ZERO;
        for (AbbonamentoConEC abbonamento:abbonamenti) {
            importo=importo.add(abbonamento.getTotaleImportiBG());
        }
        return importo;
    }

    private BigDecimal getTotale(List<AbbonamentoConEC> abbonamenti) {
        BigDecimal importo = BigDecimal.ZERO;
        for (AbbonamentoConEC abbonamento:abbonamenti) {
            importo=importo.add(abbonamento.getTotaleBG());
        }
        return importo;
    }

    private BigDecimal getIncassato(List<AbbonamentoConEC> abbonamenti) {
        BigDecimal importo = BigDecimal.ZERO;
        for (AbbonamentoConEC abb:abbonamenti) {
            if (abb.getIncassato() != null)
                importo=importo.add(abb.getIncassatoBG());
        }
        return importo;
    }

    private BigDecimal getSaldo(List<AbbonamentoConEC> abbonamenti) {
        BigDecimal importo = BigDecimal.ZERO;
        for (AbbonamentoConEC abbonamneto:abbonamenti) {
            importo=importo.add(abbonamneto.getSaldoBG());
        }
        return importo;
    }


}
