package it.arsinfo.smd.data;

public enum RangeSpeseSpedizione {
	Base(0,100),
	Da100grA200gr(101,200),
    Da200grA350gr(201,350),
	Da350grA1Kg(351,1000),
    Da1KgA2Kg(1001,2000),
    Da2KgA5Kg(2001,5000),
    Da5KgA10Kg(5001,10000),
    Da10KgA20Kg(10001,20000),
    Da20KgA30Kg(20001,30000),
    Extra(30001,-1);
	
	private final int minPeso;
    private final int maxPeso;

	
    RangeSpeseSpedizione(int min, int max) {
		this.minPeso=min;
		this.maxPeso=max;
	}

	public static RangeSpeseSpedizione getByPeso(int peso) {
	    if (peso <= 0) {
	        return Base;
	    }
	    for (RangeSpeseSpedizione range: values()) {
	        if (range.getMinPeso() <= peso && range.getMaxPeso() >= peso) {
	            return range;
	        }
	    }
	    return Extra;
	}

    public int getMinPeso() {
        return minPeso;
    }
    public int getMaxPeso() {
        return maxPeso;
    }

}