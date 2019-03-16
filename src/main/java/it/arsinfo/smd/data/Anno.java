package it.arsinfo.smd.data;

public enum Anno {
	ANNO2019(2019),
	ANNO2020(2020),
	ANNO2021(2021),
	ANNO2022(2022),
	ANNO2023(2023);
	
	private int anno;
	
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