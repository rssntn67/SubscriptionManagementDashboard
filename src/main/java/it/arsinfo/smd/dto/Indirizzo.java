package it.arsinfo.smd.dto;

import org.springframework.util.Assert;

import it.arsinfo.smd.data.Paese;
import it.arsinfo.smd.data.Provincia;
import it.arsinfo.smd.entity.Anagrafica;

public class Indirizzo {

	public static Indirizzo getIndirizzo(Anagrafica primo) {
		return new Indirizzo(primo);
	}
	
	public static Indirizzo getIndirizzo(Anagrafica primo, Anagrafica secondo) {
		return new Indirizzo(primo, secondo);
	}
	public static Indirizzo getIndirizzo(Anagrafica primo, Anagrafica secondo, Anagrafica terzo) {
		return new Indirizzo(primo, secondo,terzo);
	}
    private final Anagrafica primo;
    private final Anagrafica secondo;
    private final Anagrafica terzo;

    protected Indirizzo(Anagrafica primo) {
    	Assert.notNull(primo,"primo must be not null");
    	this.primo=primo;
    	this.secondo=null;
    	this.terzo=null;
    }

    protected Indirizzo(Anagrafica primo, Anagrafica secondo) {
    	Assert.notNull(primo,"primo must be not null");
    	this.primo=primo;
    	this.secondo=secondo;
    	this.terzo=null;
    }

    protected Indirizzo(Anagrafica primo, Anagrafica secondo, Anagrafica terzo) {
    	Assert.notNull(primo,"primo must be not null");
    	Assert.notNull(secondo,"secondo must be not null");
    	this.primo=primo;
    	this.secondo=secondo;
    	this.terzo=terzo;
    }

    public String getIntestazione() {
        return String.format("%s %s", primo.getTitolo().getIntestazione(), primo.getDenominazione());
    }

    public String getSottoIntestazione() {
        if (secondo == null) {
    		return primo.getNome();
        } 
        if (terzo != null) {
        	return String.format("c/o %s %s %s", 
        		terzo.getTitolo().getIntestazione(),
        		terzo.getDenominazione(),terzo.getNome());
        }
    	return String.format("c/o %s %s %s", 
        		secondo.getTitolo().getIntestazione(),
        		secondo.getDenominazione(),secondo.getNome());
    }
    
    public String getIndirizzo() {
        if (secondo == null) {
            return primo.getIndirizzo();
        }
        if (terzo != null) {
        	return terzo.getIndirizzo();
        }
        return secondo.getIndirizzo();            
    }

    public String getCap() {
        if (secondo == null) {
            return primo.getCap();
        }
        if (terzo != null) {
        	return terzo.getCap();
        }
        return secondo.getCap();        
    }

    public String getCitta() {
        if (secondo == null) {
            return primo.getCitta();
        }
        if (terzo != null) {
        	return terzo.getCitta();
        }
        return secondo.getCitta();        
    }

    public Provincia getProvincia() {
        if (secondo == null) {
            return primo.getProvincia();
        }
        if (terzo != null) {
        	return terzo.getProvincia();
        }
        return secondo.getProvincia();        
    }

    public Paese getPaese() {
        if (secondo == null) {
            return primo.getPaese();
        }
        if (terzo != null) {
        	return terzo.getPaese();
        }
        return secondo.getPaese();        
    }
    
}