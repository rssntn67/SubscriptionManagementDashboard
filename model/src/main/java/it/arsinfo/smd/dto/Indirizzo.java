package it.arsinfo.smd.dto;

import it.arsinfo.smd.entity.Anagrafica;
import it.arsinfo.smd.entity.Paese;
import it.arsinfo.smd.entity.Provincia;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public class Indirizzo {

    public static String getIntestazione(Anagrafica tizio) {
        return String.format("%s %s", tizio.getTitolo().getIntestazione(), tizio.getDenominazione());
    }

	public static Indirizzo getIndirizzo(Anagrafica primo) {
		return new Indirizzo(primo);
	}

    private final Anagrafica primo;

    protected Indirizzo(Anagrafica primo) {
    	Assert.notNull(primo,"primo must be not null");
    	this.primo=primo;
    }

    public String getIntestazione() {
    	return getIntestazione(primo);
    }

    public String getSottoIntestazione() {
        if ( StringUtils.hasLength(primo.getNome())) {
    		return primo.getNome();
        }
        return "";
    }
    
    public String getIndirizzo() {
        return primo.getIndirizzo();
    }

    public String getCap() {
        return primo.getCap();
    }

    public String getCitta() {
        return primo.getCitta();
    }

    public Provincia getProvincia() {
        return primo.getProvincia();
    }

    public Paese getPaese() {
        return primo.getPaese();
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