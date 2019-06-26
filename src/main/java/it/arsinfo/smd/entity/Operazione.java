package it.arsinfo.smd.entity;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import it.arsinfo.smd.Smd;
import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.Mese;

@Entity
public class Operazione implements SmdEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private Pubblicazione pubblicazione;
    
    @Enumerated(EnumType.STRING)
    private Anno annoPubblicazione; 

    @Enumerated(EnumType.STRING)
    private Mese mesePubblicazione;
    
    
    @Enumerated(EnumType.STRING)
    private Anno anno = Smd.getAnnoCorrente();

    @Enumerated(EnumType.STRING)
    private Mese mese = Smd.getMeseCorrente();

    private Integer definitivoSped = -1;
    private Integer definitivoSede = -1;

    private Integer stimatoSped = 0;

    private Integer stimatoSede = 0;

    public Operazione() {
    }


    public Operazione(Pubblicazione pubblicazione, Anno anno, Mese mese) {
        super();
        this.pubblicazione = pubblicazione;
        this.anno = anno;
        this.mese = mese;
        int posizioneMese=mese.getPosizione()+pubblicazione.getAnticipoSpedizione();
        if (posizioneMese > 12) {
            this.mesePubblicazione = Mese.getByPosizione(posizioneMese-12);
            this.annoPubblicazione = Anno.getAnnoSuccessivo(anno);
        } else {
            this.annoPubblicazione=anno;
            this.mesePubblicazione=Mese.getByPosizione(posizioneMese);
        }
    }


    public Long getId() {
        return id;
    }


    public Pubblicazione getPubblicazione() {
        return pubblicazione;
    }

    public void setPubblicazione(Pubblicazione pubblicazione) {
        this.pubblicazione = pubblicazione;
    }

    @Override
    public String toString() {
        return String.format("Operazione[id=%d, Pubblicazione=%s %s %s, Stim.Sede=%d, Def.Sede=%d, Stim.Sped=%d, Def.Sped=%d,Totale=%d, %s %s]", 
                             id,
                             pubblicazione.getNome(),
                             annoPubblicazione,
                             mesePubblicazione,
                             stimatoSede,
                             definitivoSede,
                             stimatoSped,
                             definitivoSped,
                             definitivoSped+definitivoSede,
                             mese,
                             anno);
    }

    public Anno getAnno() {
        return anno;
    }

    public void setAnno(Anno anno) {
        this.anno = anno;
    }

    public Mese getMese() {
        return mese;
    }

    public void setMese(Mese mese) {
        this.mese = mese;
    }

    public Integer getDefinitivoSped() {
        return definitivoSped;
    }

    public void setDefinitivoSped(Integer definitivo) {
        this.definitivoSped = definitivo;
    }

    public Integer getDefinitivoSede() {
        return definitivoSede;
    }

    public void setDefinitivoSede(Integer definitivo) {
        this.definitivoSede = definitivo;
    }


    public Anno getAnnoPubblicazione() {
        return annoPubblicazione;
    }


    public void setAnnoPubblicazione(Anno annoPubblicazione) {
        this.annoPubblicazione = annoPubblicazione;
    }


    public Mese getMesePubblicazione() {
        return mesePubblicazione;
    }


    public void setMesePubblicazione(Mese mesePubblicazione) {
        this.mesePubblicazione = mesePubblicazione;
    }


    public Integer getStimatoSped() {
        return stimatoSped;
    }


    public void setStimatoSped(Integer stimatoSped) {
        this.stimatoSped = stimatoSped;
    }


    public Integer getStimatoSede() {
        return stimatoSede;
    }


    public void setStimatoSede(Integer stimatoSede) {
        this.stimatoSede = stimatoSede;
    }
    
}
