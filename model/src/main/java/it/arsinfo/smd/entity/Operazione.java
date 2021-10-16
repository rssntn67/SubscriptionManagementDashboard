package it.arsinfo.smd.entity;

import it.arsinfo.smd.dto.SpedizioneWithItems;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(uniqueConstraints={
        @UniqueConstraint(columnNames = {"anno","mese","pubblicazione_id"})
        })
//create unique index operazione_idx_key on operazione (anno,mese,pubblicazione_id);
public class Operazione implements SmdEntity {

    public static Operazione generaOperazione(
            Pubblicazione pubblicazione,
            List<SpedizioneWithItems> spedizioni, Mese mese, Anno anno) {
        log.info("generaOperazione {}, {}, {}", pubblicazione,mese,anno);
        final Operazione op = new Operazione(pubblicazione, anno, mese);
        int posizioneMese=mese.getPosizione()+pubblicazione.getAnticipoSpedizione();
        Mese mesePubblicazione;
        Anno annoPubblicazione;
        if (posizioneMese > 12) {
            mesePubblicazione = Mese.getByPosizione(posizioneMese-12);
            annoPubblicazione = Anno.getAnnoSuccessivo(anno);
        } else {
            annoPubblicazione=anno;
            mesePubblicazione=Mese.getByPosizione(posizioneMese);
        }

        if (!pubblicazione.getMesiPubblicazione().contains(mesePubblicazione)) {
            return op;
        }
        op.setMesePubblicazione(mesePubblicazione);
        op.setAnnoPubblicazione(annoPubblicazione);
        spedizioni
                .stream()
                .filter( sped ->
                        sped.getSpedizione().getAnnoSpedizione() == anno
                                && sped.getSpedizione().getMeseSpedizione() == mese)
                .forEach( sped ->
                        sped
                                .getSpedizioneItems()
                                .stream()
                                .filter(item ->
                                        item.getStatoSpedizione() == StatoSpedizione.PROGRAMMATA
                                                && !item.isPosticipata()
                                                && item.getPubblicazione().hashCode() == pubblicazione.hashCode())
                                .forEach(item ->
                                {
                                    if (sped.getSpedizione().getInvioSpedizione() == InvioSpedizione.Spedizioniere) {
                                        op.setStimatoSped(op.getStimatoSped() + item.getNumero());
                                    } else {
                                        op.setStimatoSede(op.getStimatoSede() + item.getNumero());
                                    }
                                })
                );
        return op;
    }

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
    private StatoOperazione statoOperazione = StatoOperazione.Programmata;

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


	@Override
	public String getHeader() {
        return String.format("%s %s %s '%s'", 
                mese.getNomeBreve(),
                anno.getAnno(),
                pubblicazione.getHeader(),
                statoOperazione);
	}

}
