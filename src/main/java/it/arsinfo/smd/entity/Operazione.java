package it.arsinfo.smd.entity;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import it.arsinfo.smd.SmdEntity;
import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.Mese;
import it.arsinfo.smd.data.StatoOperazione;

@Entity
@Table(uniqueConstraints={@UniqueConstraint(columnNames = {"anno","mese","pubblicazione_id"})})
public class Operazione implements SmdEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(optional=false,fetch=FetchType.EAGER)
    private Pubblicazione pubblicazione;
    
    @Enumerated(EnumType.STRING)
    private Anno annoPubblicazione; 

    @Enumerated(EnumType.STRING)
    private Mese mesePubblicazione;
    
    
    @Enumerated(EnumType.STRING)
    private Anno anno = Anno.getAnnoCorrente();

    @Enumerated(EnumType.STRING)
    private Mese mese = Mese.getMeseCorrente();

    @Enumerated(EnumType.STRING)
    private StatoOperazione statoOperazione = StatoOperazione.PROGRAMMATA;

    private Integer definitivoSped = 0;
    private Integer definitivoSede = 0;

    private Integer stimatoSped = 0;

    private Integer stimatoSede = 0;

    public Operazione() {
    }


    public Operazione(Pubblicazione pubblicazione, Anno anno, Mese mese) {
        super();
        this.pubblicazione = pubblicazione;
        this.anno = anno;
        this.mese = mese;
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
        return String.format("Operazione[id=%d, %s %s %s '%s %s %s', Stim.Sede=%d, Stim.sped=%d, Def.Sped=%d, Def.Sped=%d, ]", 
                             id,
                             mese,
                             anno,
                             statoOperazione,
                             pubblicazione.getNome(),
                             mesePubblicazione,
                             annoPubblicazione,
                             stimatoSede,
                             stimatoSped,
                             definitivoSede,
                             definitivoSped);
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
    
    @Transient
    public int getTotaleStimato() {
        return stimatoSede+stimatoSped;
    }
    
    @Transient
    public int getTotaleDefinitivo() {
        if (definitivoSede != null && definitivoSped != null)
            return definitivoSede+definitivoSped;
        if (definitivoSede != null ) 
            return definitivoSede;
        if (definitivoSped != null)
            return definitivoSped;
        return 0;

    }


    public StatoOperazione getStatoOperazione() {
        return statoOperazione;
    }


    public void setStatoOperazione(StatoOperazione statoOperazione) {
        this.statoOperazione = statoOperazione;
    }

    @Transient 
    public String getPubblCaption() {
        return pubblicazione.getNome()+":"+mesePubblicazione.getNomeBreve()+annoPubblicazione.getAnnoAsString();
    }

    @Transient 
    public String getCaption() {
        return mese.getNomeBreve()+anno.getAnnoAsString();
    }

}
