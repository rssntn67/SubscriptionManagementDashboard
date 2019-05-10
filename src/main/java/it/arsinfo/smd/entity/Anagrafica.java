package it.arsinfo.smd.entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import it.arsinfo.smd.data.CentroDiocesano;
import it.arsinfo.smd.data.Diocesi;
import it.arsinfo.smd.data.Paese;
import it.arsinfo.smd.data.Provincia;
import it.arsinfo.smd.data.Regione;
import it.arsinfo.smd.data.TitoloAnagrafica;

@Entity
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
    private String cognome;
    
    private String indirizzo;
    private String cap;
    private String citta;
    @Enumerated(EnumType.STRING)
    private Paese paese;

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
    
    @OneToMany(cascade = { CascadeType.PERSIST })
    private List<Nota> note;

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

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public String getIndirizzo() {
        return indirizzo;
    }

    public void setIndirizzo(String indirizzo) {
        this.indirizzo = indirizzo;
    }

    @Override
    public String toString() {
        return String.format("Anagrafica[id=%d, Nome='%s', Cognome='%s', Diocesi='%s']",
                             id, nome, cognome, diocesi);
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
        return String.format("'%s %s %s'", titolo.getIntestazione(), nome, cognome);
    }

    @Transient
    public String getHeader() {
        return String.format("Anagrafica:Edit:%s", getCaption());
    }

    public List<Nota> getNote() {
        return note;
    }

    public void setNote(List<Nota> note) {
        this.note = note;
    }

    public Provincia getProvincia() {
        return provincia;
    }

    public void setProvincia(Provincia provincia) {
        this.provincia = provincia;
    }
}
