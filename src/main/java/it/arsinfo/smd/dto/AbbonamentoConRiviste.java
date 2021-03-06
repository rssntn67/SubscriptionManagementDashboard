package it.arsinfo.smd.dto;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.util.Assert;

import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.RivistaAbbonamento;

public class AbbonamentoConRiviste extends Indirizzo {

	public static String numeroPlaceHolder="-";
    private final Abbonamento abbonamento;
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


    public AbbonamentoConRiviste(Abbonamento abbonamento, List<RivistaAbbonamento> estrattiConto, Anagrafica intestatario) {
    	super(intestatario);
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

	public BigDecimal getImportoMessaggi() {
		return importoMessaggi;
	}

	public BigDecimal getImportoBlocchetti() {
		return importoBlocchetti;
	}

	public BigDecimal getImportoLodare() {
		return importoLodare;
	}

	public BigDecimal getImportoManifesti() {
		return importoManifesti;
	}
	
	public BigDecimal getSpesePostali() {
		return abbonamento.getSpese().add(abbonamento.getSpeseEstero());
	}

	public BigDecimal getSpeseEstrattoConto() {
		return abbonamento.getSpeseEstrattoConto();
	}

	public BigDecimal getTotaleImporti() {
		return importoManifesti.
				add(importoBlocchetti).
				add(importoMessaggi).
				add(importoLodare).
				add(getSpesePostali()).
				add(getSpeseEstrattoConto());
	}
	    
    public BigDecimal getPregresso() {
        return abbonamento.getPregresso();
    }
	
    public BigDecimal getTotale() {
		return getTotaleImporti().add(abbonamento.getPregresso());
	}
	
    public BigDecimal getIncassato() {
        return abbonamento.getIncassato();
    }

	public BigDecimal getSaldo() {
		return getTotale().subtract(getIncassato());
	}
		
	public BigDecimal getRiportoAnnoPrecedente() {
		return getPregresso().subtract(getIncassato());
	}
    
	

 
    
}