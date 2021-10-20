package it.arsinfo.smd.entity;

import javax.persistence.*;
import java.util.concurrent.ThreadLocalRandom;

@Entity
@Table(uniqueConstraints={
        @UniqueConstraint(columnNames = {"codeLineBase"})
        })
//create unique index anagrafica_idx_codeline on anagrafica (codelinebase);
public class Anagrafica implements SmdEntity {
	
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private Diocesi diocesi = Diocesi.DIOCESISTD;
    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private Provincia provincia = Provincia.ND;
    @Enumerated(EnumType.STRING)
    private Regione regioneVescovi;
    @Enumerated(EnumType.STRING)
    private CentroDiocesano centroDiocesano;

    @Column(nullable=false)
    private TitoloAnagrafica titolo=TitoloAnagrafica.Nessuno;

    @Column(nullable=false)
    private String nome;
    @Column(nullable=false)
    private String denominazione;
    private String descr;

    private String indirizzo;
    private String indirizzoSecondaRiga;

    private String cap;
    private String citta;
    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private Paese paese = Paese.IT;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private AreaSpedizione areaSpedizione = AreaSpedizione.Italia;

    private String email;
    private String telefono;
    private String cellulare;
    private String codfis;
    private String piva;
       
    
    @Enumerated(EnumType.STRING)
    private Regione regionePresidenteDiocesano;    
    @Enumerated(EnumType.STRING)
    private Regione regioneDirettoreDiocesano;


    private boolean presidenteDiocesano;    
    private boolean direttoreDiocesiano;        
    private boolean direttoreZonaMilano;
    
    private boolean consiglioNazionaleADP; 
    private boolean presidenzaADP; 
    private boolean direzioneADP; 
    private boolean caricheSocialiADP; 
    private boolean delegatiRegionaliADP;
   
    private boolean elencoMarisaBisi; 
    private boolean promotoreRegionale; 
    
    @Column(nullable=false)
    private String codeLineBase;

    public Anagrafica() {
    }

    public Long getId() {
        return id;
    }

    public Diocesi getDiocesi() {
        return diocesi;
    }

    public void setDiocesi(Diocesi diocesi) {
        this.diocesi = diocesi;
    }

    public String getNome() {
    	if (nome == null) {
    		return "";
    	}
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDenominazione() {
        return denominazione;
    }

    public void setDenominazione(String denominazione) {
        this.denominazione = denominazione;
    }

    public String getIndirizzo() {
        return indirizzo;
    }

    public void setIndirizzo(String indirizzo) {
        this.indirizzo = indirizzo;
    }

    @Override
    public String toString() {
        return String.format("Anagrafica[id=%d, %s %s %s %s]",
                             id, nome, denominazione, diocesi.getDetails(),codeLineBase);
    }

    public String getCap() {
        return cap;
    }

    public void setCap(String cap) {
        this.cap = cap;
    }

    public String getCitta() {
        return citta;
    }

    public void setCitta(String citta) {
        this.citta = citta;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCellulare() {
        return cellulare;
    }

    public void setCellulare(String cellulare) {
        this.cellulare = cellulare;
    }

    public Paese getPaese() {
        return paese;
    }

    public void setPaese(Paese paese) {
        this.paese = paese;
    }

    public String getCodfis() {
        return codfis;
    }

    public void setCodfis(String codfis) {
        this.codfis = codfis;
    }

    public String getPiva() {
        return piva;
    }

    public void setPiva(String piva) {
        this.piva = piva;
    }

    public CentroDiocesano getCentroDiocesano() {
        return centroDiocesano;
    }

    public void setCentroDiocesano(CentroDiocesano centroDiocesano) {
        this.centroDiocesano = centroDiocesano;
    }

    public boolean isConsiglioNazionaleADP() {
        return consiglioNazionaleADP;
    }

    public void setConsiglioNazionaleADP(boolean consiglioNazionaleADP) {
        this.consiglioNazionaleADP = consiglioNazionaleADP;
    }

    public boolean isPresidenzaADP() {
        return presidenzaADP;
    }

    public void setPresidenzaADP(boolean presidenzaADP) {
        this.presidenzaADP = presidenzaADP;
    }

    public boolean isDirezioneADP() {
        return direzioneADP;
    }

    public void setDirezioneADP(boolean direzioneADP) {
        this.direzioneADP = direzioneADP;
    }

    public boolean isCaricheSocialiADP() {
        return caricheSocialiADP;
    }

    public void setCaricheSocialiADP(boolean caricheSocialiADP) {
        this.caricheSocialiADP = caricheSocialiADP;
    }

    public boolean isDelegatiRegionaliADP() {
        return delegatiRegionaliADP;
    }

    public void setDelegatiRegionaliADP(boolean delegatiRegionaliADP) {
        this.delegatiRegionaliADP = delegatiRegionaliADP;
    }

    public Regione getRegioneVescovi() {
        return regioneVescovi;
    }

    public void setRegioneVescovi(Regione regioneVescovi) {
        this.regioneVescovi = regioneVescovi;
    }

    public boolean isDirettoreZonaMilano() {
        return direttoreZonaMilano;
    }

    public void setDirettoreZonaMilano(boolean direttoreZonaMilano) {
        this.direttoreZonaMilano = direttoreZonaMilano;
    }

    public boolean isPresidenteDiocesano() {
        return presidenteDiocesano;
    }

    public void setPresidenteDiocesano(boolean presidenteDiocesano) {
        this.presidenteDiocesano = presidenteDiocesano;
    }

    public Regione getRegionePresidenteDiocesano() {
        return regionePresidenteDiocesano;
    }

    public void setRegionePresidenteDiocesano(
            Regione regionePresidenteDiocesano) {
        this.regionePresidenteDiocesano = regionePresidenteDiocesano;
    }

    public boolean isDirettoreDiocesiano() {
        return direttoreDiocesiano;
    }

    public void setDirettoreDiocesiano(boolean direttoreDiocesiano) {
        this.direttoreDiocesiano = direttoreDiocesiano;
    }

    public Regione getRegioneDirettoreDiocesano() {
        return regioneDirettoreDiocesano;
    }

    public void setRegioneDirettoreDiocesano(
            Regione regioneDirettoreDiocesano) {
        this.regioneDirettoreDiocesano = regioneDirettoreDiocesano;
    }

    public boolean isElencoMarisaBisi() {
        return elencoMarisaBisi;
    }

    public void setElencoMarisaBisi(boolean elencoMarisaBisi) {
        this.elencoMarisaBisi = elencoMarisaBisi;
    }

    public boolean isPromotoreRegionale() {
        return promotoreRegionale;
    }

    public void setPromotoreRegionale(boolean promotoreRegionale) {
        this.promotoreRegionale = promotoreRegionale;
    }

    public TitoloAnagrafica getTitolo() {
        return titolo;
    }

    public void setTitolo(TitoloAnagrafica titolo) {
        this.titolo = titolo;
    }

    @Transient
    public String getCaption() {
    	return Anagrafica.generaCaption(this);
    }

    @Transient
    public String getIntestazione() {
    	return Anagrafica.generaIntestazione(this);
    }

    @Transient
    public String getHeader() {
    	return generaIntestazione(this);
    }

    public Provincia getProvincia() {
        return provincia;
    }

    public void setProvincia(Provincia provincia) {
        this.provincia = provincia;
    }

    public AreaSpedizione getAreaSpedizione() {
        return areaSpedizione;
    }

    public void setAreaSpedizione(AreaSpedizione areaSpedizione) {
        this.areaSpedizione = areaSpedizione;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((codeLineBase == null) ? 0 : codeLineBase.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Anagrafica other = (Anagrafica) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else return id.equals(other.id);

        if (codeLineBase == null) {
            return other.codeLineBase == null;
        } else return codeLineBase.equals(other.codeLineBase);
    }

    public String getCodeLineBase() {
        return codeLineBase;
    }

    public void setCodeLineBase(String codeLineBase) {
        this.codeLineBase = codeLineBase;
    }
    
    public static String generaCodeLineBase() {
        return String.format("%014d", ThreadLocalRandom.current().nextLong(99999999999999L));
    }

    public static String generaIntestazione(Anagrafica a) {
    	return String.format("%s %s %s", a.getTitolo().getIntestazione(), 
        		a.getDenominazione(),a.getNome());
    }

    public static String generaCaption(Anagrafica a) {
        return String.format("'%s %s %s %s %s %s'", 
        		a.getTitolo().getIntestazione(), 
        		a.getDenominazione(),
        		a.getNome(),
        		a.getCitta(),
        		a.getCap(),
        		a.getCodeLineBase());
    }

    public String getIndirizzoSecondaRiga() {
        return indirizzoSecondaRiga;
    }

    public void setIndirizzoSecondaRiga(String indirizzoSecondaRiga) {
        this.indirizzoSecondaRiga = indirizzoSecondaRiga;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }
}
