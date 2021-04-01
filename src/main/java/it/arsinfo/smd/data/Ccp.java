package it.arsinfo.smd.data;


public enum Ccp {
    UNO("000063470009","Poste","IT90C0760103200000063470009"),
    DUE("NA","Mps","NA"),
    TRE("NA","Cassa","NA"),
    QUATTRO("NA","BPER","IT93U0538703222000000001920");
    private final String ccp;
    private final String cc;
    private final String iban;

    public static final String intestazioneCcp = "SEGRETARIATO NAZIONALE DELL'APOSTOLATO DELLA PREGHIERA";

    public static final String accountAuthorizationCode = "AUT. DB/SISB/E 15513 DEL 6/5/2004";

    public static Ccp getByCc(String cc) {
        for (Ccp l : Ccp.values()) {
            if (l.cc.equals(cc)) return l;
        }
        throw new IllegalArgumentException("Ccp not found.");
    }

    Ccp(String cc, String ccp, String iban) {
        this.cc =cc;
        this.ccp = ccp;
        this.iban = iban;
    }
    
    public String getCcp() {
        return ccp;
    }
    
    public String getCc() {
        return cc;
    }

    public String getIban() {return iban;}

}