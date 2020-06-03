package it.arsinfo.smd.dto;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import org.springframework.util.Assert;

import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.RivistaAbbonamento;

public class AbbonamentoConRiviste extends Indirizzo {

	public static String numeroPlaceHolder="-";
    private final Abbonamento abbonamento;
    private final NumberFormat fmt_IT  = NumberFormat.getNumberInstance(Locale.ITALIAN);
    private Integer numeroMessaggi=0;
    private Integer numeroBlocchetti=0;
    private Integer numeroLodare=0;
    private Integer numeroManifesti=0;

    private BigDecimal importoMessaggi=BigDecimal.ZERO;
    private BigDecimal importoBlocchetti=BigDecimal.ZERO;
    private BigDecimal importoLodare=BigDecimal.ZERO;
    private BigDecimal importoManifesti=BigDecimal.ZERO;

    public AbbonamentoConRiviste(Abbonamento abbonamento, List<RivistaAbbonamento> estrattiConto, Anagrafica intestatario, Anagrafica co) {
    	super(intestatario,co);
    	fmt_IT.setMaximumFractionDigits(2);    	
    	fmt_IT.setMinimumFractionDigits(2);    	
    	Assert.notNull(abbonamento,"abbonamento must be not null");
    	this.abbonamento= abbonamento;
    	for (RivistaAbbonamento ec:estrattiConto) {
    		if (ec.getPubblicazione().getNome().equals("Messaggio")) {
    			numeroMessaggi+=ec.getNumero();
    			importoMessaggi=importoMessaggi.add(ec.getImporto());
    		} else if(ec.getPubblicazione().getNome().equals("Lodare")) {
    			numeroLodare+=ec.getNumero();
    			importoLodare=importoLodare.add(ec.getImporto());
    		} else if(ec.getPubblicazione().getNome().equals("Blocchetti")) {
    			numeroBlocchetti+=ec.getNumero();
    			importoBlocchetti=importoBlocchetti.add(ec.getImporto());
    		} else if(ec.getPubblicazione().getNome().equals("Estratti")) {
    			numeroManifesti+=ec.getNumero();
    			importoManifesti=importoManifesti.add(ec.getImporto());
    		}     		
    	}
    }

    
    public Anno getAnno() {
        return abbonamento.getAnno();
    }
            
	public String getCodeLine() {
		return abbonamento.getCodeLine();
	}

	public String getNumeroMessaggi() {
		if (numeroMessaggi == 0) {
			return numeroPlaceHolder;
		}			
		return numeroMessaggi.toString();
	}

	public String getNumeroBlocchetti() {
		if (numeroBlocchetti == 0) {
			return numeroPlaceHolder;
		}			
		return numeroBlocchetti.toString();
	}

	public String getNumeroLodare() {
		if (numeroLodare == 0) {
			return numeroPlaceHolder;
		}			
		return numeroLodare.toString();
	}

	public String getNumeroManifesti() {
		if (numeroManifesti == 0) {
			return numeroPlaceHolder;
		}			
		return numeroManifesti.toString();
	}

	public Integer getTotaleMessaggi() {
		return numeroMessaggi;
	}

	public Integer getTotaleBlocchetti() {
		return numeroBlocchetti;
	}

	public Integer getTotaleLodare() {
		return numeroLodare;
	}

	public Integer getTotaleManifesti() {
		return numeroManifesti;
	}

	public BigDecimal getImportoMessaggiBG() {
		return importoMessaggi;
	}

	public BigDecimal getImportoBlocchettiBG() {
		return importoBlocchetti;
	}

	public BigDecimal getImportoLodareBG() {
		return importoLodare;
	}

	public BigDecimal getImportoManifestiBG() {
		return importoManifesti;
	}
	
	public BigDecimal getSpesePostaliBG() {
		return abbonamento.getSpese().add(abbonamento.getSpeseEstero());
	}

	public BigDecimal getSpeseEstrattoContoBG() {
		return abbonamento.getSpeseEstrattoConto();
	}

	public BigDecimal getTotaleImportiBG() {
		return importoManifesti.
				add(importoBlocchetti).
				add(importoMessaggi).
				add(importoLodare).
				add(getSpesePostaliBG()).
				add(getSpeseEstrattoContoBG());
	}
	    
    public BigDecimal getPregressoBG() {
        return abbonamento.getPregresso();
    }
	
    public BigDecimal getTotaleBG() {
		return getTotaleImportiBG().add(abbonamento.getPregresso());
	}
	
    public BigDecimal getIncassatoBG() {
        return abbonamento.getIncassato();
    }

	public BigDecimal getSaldoBG() {
		return getTotaleBG().subtract(getIncassatoBG());
	}
		
	public String getImportoMessaggi() {
		return fmt_IT.format(getImportoMessaggiBG());
	}

	public String getImportoBlocchetti() {
		return fmt_IT.format(getImportoBlocchettiBG());
	}

	public String getImportoLodare() {
		return fmt_IT.format(getImportoLodareBG());
	}

	public String getImportoManifesti() {
		return fmt_IT.format(getImportoManifestiBG());
	}

	public String getSpesePostali() {
		return fmt_IT.format(getSpesePostaliBG());
	}

	public String getSpeseEstrattoConto() {
		return fmt_IT.format(getSpeseEstrattoContoBG());
	}

	public String getTotaleImporti() {
		return fmt_IT.format(getTotaleImportiBG());
	}

    public String getPregresso() {
		return fmt_IT.format(getPregressoBG());
    }

	public String getTotale() {
		return fmt_IT.format(getTotaleBG());
	}
    
	public String getIncassato() {
		return fmt_IT.format(getIncassatoBG());
    }
	public String getRiportoAnnoPrecedente() {
		return fmt_IT.format(getPregressoBG().subtract(getIncassatoBG()));
	}
    
	public String getSaldo() {
		return fmt_IT.format(getSaldoBG());
	}
	

 
    
}