package it.arsinfo.smd.dto;

import org.springframework.util.Assert;

import it.arsinfo.smd.data.Paese;
import it.arsinfo.smd.data.Provincia;
import it.arsinfo.smd.entity.Anagrafica;

public class Indirizzo {

	public Anagrafica getPrimo() {
		return primo;
	}

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
    
    public static String getIntestazione(Anagrafica tizio) {
        return String.format("%s %s", tizio.getTitolo().getIntestazione(), tizio.getDenominazione());
    }

    public static String getIntestazioneCo(Anagrafica caio) {
        return String.format("c/o %s %s", caio.getTitolo().getIntestazione(), caio.getDenominazione());
    }

    public String getIntestazione() {
    	return getIntestazione(primo);
    }

    public String getNome() {
    	return primo.getNome();
    }

    public String getSottoIntestazione() {
        if (secondo == null) {
    		return null;
        }
        if (terzo != null) {
        	return getIntestazioneCo(terzo);
        }
    	return getIntestazioneCo(secondo);
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
    
    public String getTelefono() {
        String telefono = primo.getTelefono();
        if (telefono == null) {
            	return "-";
        }
        return telefono;
    }

    public String getEmail() {
        String email = primo.getEmail();
        if (email == null) {
            	return "-";
        }
        return email;
    }    

    public String getCellulare() {
        String cell = primo.getCellulare();
        if (cell == null) {
            	return "-";
        }
        return cell;
    }    

    
}