package it.arsinfo.smd.data;

public enum Anno {
        ANNO2016(2016),
        ANNO2017(2017),
        ANNO2018(2018),
	ANNO2019(2019),
	ANNO2020(2020),
	ANNO2021(2021),
	ANNO2022(2022),
	ANNO2023(2023);
	
	private int anno;
	
	public static Anno getAnnoPrecedente(Anno anno) {
	    int annomenouno= anno.getAnno()-1;
	    return Anno.valueOf("ANNO"+annomenouno);
	    
	}
	private Anno(int anno) {
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
}