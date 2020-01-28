package it.arsinfo.smd.entity;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.Transient;

import it.arsinfo.smd.SmdEntity;
import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.Paese;
import it.arsinfo.smd.data.Provincia;

public class AbbonamentoConEstrattoConto implements SmdEntity {

    private Anagrafica intestatario;
    private String codeLine;

    private Anno anno = Anno.getAnnoCorrente();
    
    private BigDecimal pregresso=BigDecimal.ZERO;
    private BigDecimal importo=BigDecimal.ZERO;
    private BigDecimal spesePostali=BigDecimal.ZERO;
    private BigDecimal speseEstrattoConto=BigDecimal.ZERO;
    private BigDecimal incassato=BigDecimal.ZERO;
    
    private Integer numeroMessaggi=0;
    private Integer numeroBlocchetti=0;
    private Integer numeroLodare=0;
    private Integer numeroManifesti=0;

    private BigDecimal importoMessaggi=BigDecimal.ZERO;
    private BigDecimal importoBlocchetti=BigDecimal.ZERO;
    private BigDecimal importoLodare=BigDecimal.ZERO;
    private BigDecimal importoManifesti=BigDecimal.ZERO;

    private final Long id;
    public AbbonamentoConEstrattoConto(Abbonamento abbonamento, List<EstrattoConto> estrattiConto) {
    	id = abbonamento.getId();
    	intestatario=abbonamento.getIntestatario();
    	codeLine = abbonamento.getCodeLine();
    	anno = abbonamento.getAnno();
    	
    	incassato= abbonamento.getIncassato();
    	pregresso = abbonamento.getPregresso();
    	importo = abbonamento.getImporto();
    	spesePostali=abbonamento.getSpese().add(abbonamento.getSpeseEstero());
    	speseEstrattoConto = abbonamento.getSpeseEstrattoConto();
    	for (EstrattoConto ec:estrattiConto) {
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

    public Anagrafica getIntestatario() {
        return intestatario;
    }

    public void setIntestatario(Anagrafica anagrafica) {
        this.intestatario = anagrafica;
    }
    
    public Anno getAnno() {
        return anno;
    }

    public void setAnno(Anno anno) {
        this.anno = anno;
    }
            
    public BigDecimal getIncassato() {
        return incassato;
    }
    
    public String getCaption() {
        return intestatario.getCaption();
    }

    public String getCaptionBrief() {
        return String.format("'%s %s'", intestatario.getTitolo().getIntestazione(), intestatario.getDenominazione());
    }

    public String getSottoIntestazione() {
        if (intestatario.getCo() == null ) {
        	if (intestatario.getNome() != null) {
        		return intestatario.getNome();
        	} else {
        		return "";
        	}
        } 
        return "c/o" + intestatario.getCo().getCaptionBrief();
    }
    
    @Transient
    public String getIndirizzo() {
        if (intestatario.getCo() == null) {
            return intestatario.getIndirizzo();
        }
        return intestatario.getCo().getIndirizzo();            
    }

    public String getCap() {
        if (intestatario.getCo() == null) {
            return intestatario.getCap();
        }
        return intestatario.getCo().getCap();        
    }

    public String getCitta() {
        if (intestatario.getCo() == null) {
            return intestatario.getCitta();
        }
        return intestatario.getCo().getCitta();        
    }

    public Provincia getProvincia() {
        if (intestatario.getCo() == null) {
            return intestatario.getProvincia();
        }
        return intestatario.getCo().getProvincia();        
    }

    public Paese getPaese() {
        if (intestatario.getCo() == null) {
            return intestatario.getPaese();
        }
        return intestatario.getCo().getPaese();        
    }

    public BigDecimal getTotale() {
        return importo.add(pregresso).add(spesePostali).add(speseEstrattoConto);
    }

    public BigDecimal getImporto() {
        return importo;
    }

    public void setImporto(BigDecimal importo) {
        this.importo = importo;
    }

    public void setIncassato(BigDecimal incassato) {
        this.incassato = incassato;
    }

    public BigDecimal getPregresso() {
        return pregresso;
    }

    public void setPregresso(BigDecimal pregresso) {
        this.pregresso = pregresso;
    }


	public BigDecimal getSpesePostali() {
		return spesePostali;
	}

	public void setSpesePostali(BigDecimal speseEstero) {
		this.spesePostali = speseEstero;
	}

	public BigDecimal getSpeseEstrattoConto() {
		return speseEstrattoConto;
	}

	public void setSpeseEstrattoConto(BigDecimal speseEstrattoConto) {
		this.speseEstrattoConto = speseEstrattoConto;
	}

	public String getCodeLine() {
		return codeLine;
	}

	public void setCodeLine(String codeLine) {
		this.codeLine = codeLine;
	}

	public Integer getNumeroMessaggi() {
		return numeroMessaggi;
	}

	public void setNumeroMessaggi(Integer numeroMessaggi) {
		this.numeroMessaggi = numeroMessaggi;
	}

	public Integer getNumeroBlocchetti() {
		return numeroBlocchetti;
	}

	public void setNumeroBlocchetti(Integer numeroBlocchetti) {
		this.numeroBlocchetti = numeroBlocchetti;
	}

	public Integer getNumeroLodare() {
		return numeroLodare;
	}

	public void setNumeroLodare(Integer numeroLodare) {
		this.numeroLodare = numeroLodare;
	}

	public Integer getNumeroManifesti() {
		return numeroManifesti;
	}

	public void setNumeroManifesti(Integer numeroManifesti) {
		this.numeroManifesti = numeroManifesti;
	}

	public BigDecimal getImportoMessaggi() {
		return importoMessaggi;
	}

	public void setImportoMessaggi(BigDecimal importoMessaggi) {
		this.importoMessaggi = importoMessaggi;
	}

	public BigDecimal getImportoBlocchetti() {
		return importoBlocchetti;
	}

	public void setImportoBlocchetti(BigDecimal importoBlocchetti) {
		this.importoBlocchetti = importoBlocchetti;
	}

	public BigDecimal getImportoLodare() {
		return importoLodare;
	}

	public void setImportoLodare(BigDecimal importoLodare) {
		this.importoLodare = importoLodare;
	}

	public BigDecimal getImportoManifesti() {
		return importoManifesti;
	}

	public void setImportoManifesti(BigDecimal importoManifesti) {
		this.importoManifesti = importoManifesti;
	}
	
	public BigDecimal getTotaleImporti() {
		return importo.add(spesePostali).add(speseEstrattoConto);
	}
	
	public BigDecimal getSaldo() {
		return getTotale().subtract(incassato);
	}

	@Override
	public Long getId() {
		return id;
	}
    
}