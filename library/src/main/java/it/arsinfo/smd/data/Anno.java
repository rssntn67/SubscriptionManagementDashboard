package it.arsinfo.smd.data;

import java.text.SimpleDateFormat;
import java.util.Date;

public enum Anno {
    ANNO2016(2016),
    ANNO2017(2017),
    ANNO2018(2018),
	ANNO2019(2019),
	ANNO2020(2020),
	ANNO2021(2021),
    ANNO2022(2022),
	ANNO2023(2023),
    ANNO2024(2024),
    ANNO2025(2025),
    ANNO2026(2026),
    ANNO2027(2027),
    ANNO2028(2028),
    ANNO2029(2029),
	ANNO2030(2030);
	
	private int anno;
	
	public static Anno getAnnoPrecedente(Anno anno) {
	    int annomenouno= anno.getAnno()-1;
	    return Anno.valueOf("ANNO"+annomenouno);
	    
	}
        public static Anno getAnnoSuccessivo(Anno anno) {
            int annopiuuno= anno.getAnno()+1;
            return Anno.valueOf("ANNO"+annopiuuno);
            
        }Anno(int anno) {
		this.anno=anno;
	}
	
	public String getAnnoAsString() {
		return Integer.toString(anno);
	}

	public int getAnno() {
		return anno;
	}

	public void setAnno(int anno) {
		this.anno = anno;
	}
    public static Anno getAnnoProssimo() {
        int annoProssimo = getAnnoCorrente().getAnno()+1;
        return valueOf("ANNO"+annoProssimo);
    }
    public static Anno getAnnoPassato() {
        int annoScorso = getAnnoCorrente().getAnno()-1;
        return valueOf("ANNO"+annoScorso);
    }
    public static Anno getAnnoCorrente() {
        return valueOf("ANNO"+new SimpleDateFormat("yyyy").format(new Date()));        
    }
}