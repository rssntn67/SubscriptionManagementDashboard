package it.arsinfo.smd.data;

import java.text.SimpleDateFormat;
import java.util.Date;

public enum Mese {
	GENNAIO("Gen.",1, "01"),
	FEBBRAIO("Feb.",2, "02"),
	MARZO("Mar.",3, "03"),
	APRILE("Apr.",4, "04"),
	MAGGIO("Mag.",5, "05"),
	GIUGNO("Giu.",6, "06"),
	LUGLIO("Lug.",7, "07"),
	AGOSTO("Ago.",8, "08"),
	SETTEMBRE("Set.",9, "09"),
	OTTOBRE("Ott.",10, "10"),
	NOVEMBRE("Nov.",11, "11"),
	DICEMBRE("Dic.",12, "12");
	
	private final String nomeBreve;
	private final int posizione;
	private final String code;

	public static Mese getMeseSuccessivo(Mese mese) {
	    if (mese == Mese.DICEMBRE) 
	        return Mese.GENNAIO;
	    return getByPosizione(mese.getPosizione()+1);
	}
	
	public static Mese getByPosizione(int posizione) {
            for (Mese mese: values()) {
                if (mese.getPosizione() == posizione) {
                    return mese;
                }
            }
            return null;
	    
	}
	
	public static Mese getByCode(String code) {
	    for (Mese mese: values()) {
	        if (mese.getCode().equals(code)) {
	            return mese;
	        }
	    }
	    return null;
	}
	
	Mese(String nome, int posizione, String cod) {
		this.nomeBreve=nome;
		this.posizione=posizione;
		this.code=cod;
	}

	public String getNomeBreve() {
		return nomeBreve;
	}
	public int getPosizione() {
		return posizione;
	}
	public String getCode() {
		return code;
	}
    public static Mese getMeseCorrente() {
        return getByCode(new SimpleDateFormat("MM").format(new Date()));        
    }
}