package it.arsinfo.smd.entity;

import java.util.concurrent.ThreadLocalRandom;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import it.arsinfo.smd.SmdEntity;
import it.arsinfo.smd.data.AreaSpedizione;
import it.arsinfo.smd.data.CentroDiocesano;
import it.arsinfo.smd.data.Diocesi;
import it.arsinfo.smd.data.Paese;
import it.arsinfo.smd.data.Provincia;
import it.arsinfo.smd.data.Regione;
import it.arsinfo.smd.data.TitoloAnagrafica;

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
    private Diocesi diocesi = Diocesi.DIOCESISTD;
    @Enumerated(EnumType.STRING)
    private Provincia provincia = Provincia.ND;
    @Enumerated(EnumType.STRING)
    private Regione regioneVescovi;
    @Enumerated(EnumType.STRING)
    private CentroDiocesano centroDiocesano;

    private TitoloAnagrafica titolo=TitoloAnagrafica.Nessuno;   
    
    private String nome;
    private String denominazione;
    private String descr;
    
    @ManyToOne
    private Anagrafica co;
    
    private String indirizzo;
    private String indirizzoSecondaRiga;

    private String cap;
    private String citta;
    @Enumerated(EnumType.STRING)
    private Paese paese = Paese.IT;

    @Enumerated(EnumType.STRING)
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
    public String getCaptionBrief() {
        return String.format("'%s %s %s'", titolo.getIntestazione(), denominazione, nome);
    }

    @Transient
    public String getCaption() {
        return String.format("'%s %s %s %s %s %s'", titolo.getIntestazione(), nome, denominazione,citta,cap,codeLineBase);
    }

    @Transient
    public String getHeader() {
        return String.format("Anagrafica:Edit:%s", getCaption());
    }

    public Provincia getProvincia() {
        return provincia;
    }

    public void setProvincia(Provincia provincia) {
        this.provincia = provincia;
    }

    public Anagrafica getCo() {
        return co;
    }

    public void setCo(Anagrafica co) {
        this.co = co;
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
        } else if (!id.equals(other.id)) {
            return false;
        } else {
            return true;
        }

        if (codeLineBase == null) {
            if (other.codeLineBase != null)
                return false;
        } else if (!codeLineBase.equals(other.codeLineBase))
            return false;
        return true;
    }

    public String getCodeLineBase() {
        return codeLineBase;
    }

    public void setCodeLineBase(String codeLineBase) {
        this.codeLineBase = codeLineBase;
    }
    
    public static String generaCodeLineBase() {
        return String.format("%014d", ThreadLocalRandom.current().nextLong(99999999999999l));
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
