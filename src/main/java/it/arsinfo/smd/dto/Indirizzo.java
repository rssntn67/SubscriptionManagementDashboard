package it.arsinfo.smd.dto;

import org.springframework.util.Assert;

import it.arsinfo.smd.data.Paese;
import it.arsinfo.smd.data.Provincia;
import it.arsinfo.smd.entity.Anagrafica;

public class Indirizzo {

    public static String getIntestazione(Anagrafica tizio) {
        return String.format("%s %s", tizio.getTitolo().getIntestazione(), tizio.getDenominazione());
    }

    public static String getIntestazioneCo(Anagrafica caio) {
        return String.format("c/o %s %s", caio.getTitolo().getIntestazione(), caio.getDenominazione());
    }

	public static Indirizzo getIndirizzo(Anagrafica primo) {
		return new Indirizzo(primo);
	}
	public static Indirizzo getIndirizzo(Anagrafica primo, Anagrafica secondo) {
		return new Indirizzo(primo, secondo);
	}

    private final Anagrafica primo;
    private final Anagrafica secondo;

    protected Indirizzo(Anagrafica primo) {
    	Assert.notNull(primo,"primo must be not null");
    	this.primo=primo;
    	this.secondo=null;
    }

    protected Indirizzo(Anagrafica primo, Anagrafica secondo) {
    	Assert.notNull(primo,"primo must be not null");
        Assert.notNull(secondo,"secondo must be not null");
    	this.primo=primo;
    	this.secondo=secondo;
    }

    public String getIntestazione() {
    	return getIntestazione(primo);
    }

    public String getSottoIntestazione() {
        if (secondo == null) {
    		return primo.getNome();
        }
    	return getIntestazioneCo(secondo);
    }
    
    public String getIndirizzo() {
        if (secondo == null) {
            return primo.getIndirizzo();
        }
        return secondo.getIndirizzo();
    }

    public String getCap() {
        if (secondo == null) {
            return primo.getCap();
        }
        return secondo.getCap();
    }

    public String getCitta() {
        if (secondo == null) {
            return primo.getCitta();
        }
        return secondo.getCitta();
    }

    public Provincia getProvincia() {
        if (secondo == null) {
            return primo.getProvincia();
        }
        return secondo.getProvincia();
    }

    public Paese getPaese() {
        if (secondo == null) {
            return primo.getPaese();
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