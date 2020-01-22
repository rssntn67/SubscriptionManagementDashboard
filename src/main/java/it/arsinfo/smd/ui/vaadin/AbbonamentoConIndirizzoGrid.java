package it.arsinfo.smd.ui.vaadin;

import java.math.BigDecimal;
import java.util.List;

import com.vaadin.ui.Grid;
import com.vaadin.ui.components.grid.FooterRow;

import it.arsinfo.smd.entity.AbbonamentoConEstrattoConto;

public class AbbonamentoConIndirizzoGrid extends SmdGrid<AbbonamentoConEstrattoConto> {

    private final FooterRow gridfooter;

    public AbbonamentoConIndirizzoGrid(String gridName) {
        super(new Grid<>(AbbonamentoConEstrattoConto.class),gridName);
        setColumns(
        		"codeLine",
        		"captionBrief",
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
    public void populate(List<AbbonamentoConEstrattoConto> items) {
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
    
    private Integer getNumeroMessaggi(List<AbbonamentoConEstrattoConto> items) {
    	Integer numero = 0;
    	for (AbbonamentoConEstrattoConto abbec: items) {
    		numero+=abbec.getNumeroMessaggi();
    	}
    	return numero;
    }
    private Integer getNumeroLodare(List<AbbonamentoConEstrattoConto> items) {
    	Integer numero = 0;
    	for (AbbonamentoConEstrattoConto abbec: items) {
    		numero+=abbec.getNumeroLodare();
    	}
    	return numero;
    }
    private Integer getNumeroManifesti(List<AbbonamentoConEstrattoConto> items) {
    	Integer numero = 0;
    	for (AbbonamentoConEstrattoConto abbec: items) {
    		numero+=abbec.getNumeroManifesti();
    	}
    	return numero;
    }
    private Integer getNumeroBlocchetti(List<AbbonamentoConEstrattoConto> items) {
    	Integer numero = 0;
    	for (AbbonamentoConEstrattoConto abbec: items) {
    		numero+=abbec.getNumeroBlocchetti();
    	}
    	return numero;
    }

    private BigDecimal getImportoMessaggi(List<AbbonamentoConEstrattoConto> abbonamenti) {
        BigDecimal importo = BigDecimal.ZERO;
        for (AbbonamentoConEstrattoConto abbonamento:abbonamenti) {
            importo=importo.add(abbonamento.getImportoMessaggi());
        }
        return importo;
    }

    private BigDecimal getImportoLodare(List<AbbonamentoConEstrattoConto> abbonamenti) {
        BigDecimal importo = BigDecimal.ZERO;
        for (AbbonamentoConEstrattoConto abbonamento:abbonamenti) {
            importo=importo.add(abbonamento.getImportoLodare());
        }
        return importo;
    }

    private BigDecimal getImportoBlocchetti(List<AbbonamentoConEstrattoConto> abbonamenti) {
        BigDecimal importo = BigDecimal.ZERO;
        for (AbbonamentoConEstrattoConto abbonamento:abbonamenti) {
            importo=importo.add(abbonamento.getImportoBlocchetti());
        }
        return importo;
    }

    private BigDecimal getImportoManifesti(List<AbbonamentoConEstrattoConto> abbonamenti) {
        BigDecimal importo = BigDecimal.ZERO;
        for (AbbonamentoConEstrattoConto abbonamento:abbonamenti) {
            importo=importo.add(abbonamento.getImportoManifesti());
        }
        return importo;
    }

    private BigDecimal getPregresso(List<AbbonamentoConEstrattoConto> abbonamenti) {
        BigDecimal importo = BigDecimal.ZERO;
        for (AbbonamentoConEstrattoConto abbonamento:abbonamenti) {
            importo=importo.add(abbonamento.getPregresso());
        }
        return importo;
    }


    private BigDecimal getSpesePostali(List<AbbonamentoConEstrattoConto> abbonamenti) {
        BigDecimal importo = BigDecimal.ZERO;
        for (AbbonamentoConEstrattoConto abbonamento:abbonamenti) {
            importo=importo.add(abbonamento.getSpesePostali());
        }
        return importo;
    }

    private BigDecimal getSpeseEstrattoConto(List<AbbonamentoConEstrattoConto> abbonamenti) {
        BigDecimal importo = BigDecimal.ZERO;
        for (AbbonamentoConEstrattoConto abbonamento:abbonamenti) {
            importo=importo.add(abbonamento.getSpeseEstrattoConto());
        }
        return importo;
    }

    private BigDecimal getTotaleImporti(List<AbbonamentoConEstrattoConto> abbonamenti) {
        BigDecimal importo = BigDecimal.ZERO;
        for (AbbonamentoConEstrattoConto abbonamento:abbonamenti) {
            importo=importo.add(abbonamento.getTotaleImporti());
        }
        return importo;
    }

    private BigDecimal getTotale(List<AbbonamentoConEstrattoConto> abbonamenti) {
        BigDecimal importo = BigDecimal.ZERO;
        for (AbbonamentoConEstrattoConto abbonamento:abbonamenti) {
            importo=importo.add(abbonamento.getTotale());
        }
        return importo;
    }

    private BigDecimal getIncassato(List<AbbonamentoConEstrattoConto> abbonamenti) {
        BigDecimal importo = BigDecimal.ZERO;
        for (AbbonamentoConEstrattoConto abb:abbonamenti) {
            if (abb.getIncassato() != null)
                importo=importo.add(abb.getIncassato());
        }
        return importo;
    }

    private BigDecimal getSaldo(List<AbbonamentoConEstrattoConto> abbonamenti) {
        BigDecimal importo = BigDecimal.ZERO;
        for (AbbonamentoConEstrattoConto abbonamneto:abbonamenti) {
            importo=importo.add(abbonamneto.getSaldo());
        }
        return importo;
    }


}
