package it.arsinfo.smd.dto;

import org.springframework.util.Assert;

import it.arsinfo.smd.data.Paese;
import it.arsinfo.smd.data.Provincia;
import it.arsinfo.smd.entity.Anagrafica;

public class Indirizzo {

    private final Anagrafica intestatario;
    private final Anagrafica co;
        
    public Indirizzo(Anagrafica intestatario, Anagrafica co) {
    	Assert.notNull(intestatario,"intestatario must be not null");
    	this.intestatario=intestatario;
    	this.co=co;
    }
    
    public String getIntestazione() {
        return String.format("%s %s", intestatario.getTitolo().getIntestazione(), intestatario.getDenominazione());
    }

    public String getSottoIntestazione() {
        if (co == null ) {
    		return intestatario.getNome();
        } 
        return String.format("c/o %s %s %s", co.getTitolo().getIntestazione(),co.getDenominazione(),co.getNome());
    }
    
    public String getIndirizzo() {
        if (co == null) {
            return intestatario.getIndirizzo();
        }
        return co.getIndirizzo();            
    }

    public String getCap() {
        if (co == null) {
            return intestatario.getCap();
        }
        return co.getCap();        
    }

    public String getCitta() {
        if (co == null) {
            return intestatario.getCitta();
        }
        return co.getCitta();        
    }

    public Provincia getProvincia() {
        if (co == null) {
            return intestatario.getProvincia();
        }
        return co.getProvincia();        
    }

    public Paese getPaese() {
        if (co == null) {
            return intestatario.getPaese();
        }
        return co.getPaese();        
    }
    
}