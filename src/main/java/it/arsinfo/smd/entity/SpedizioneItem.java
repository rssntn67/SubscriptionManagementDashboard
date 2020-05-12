package it.arsinfo.smd.entity;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import it.arsinfo.smd.data.Anno;
import it.arsinfo.smd.data.Mese;

@Entity
public class SpedizioneItem implements SmdEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(optional=false,fetch=FetchType.EAGER)
    private Spedizione spedizione;

    @ManyToOne(optional=false,fetch=FetchType.EAGER)
    private Pubblicazione pubblicazione;

    @ManyToOne(optional=false,fetch=FetchType.LAZY)
    private RivistaAbbonamento rivistaAbbonamento;
    
    @Enumerated(EnumType.STRING)
    private Mese mesePubblicazione=Mese.getMeseCorrente();
    @Enumerated(EnumType.STRING)
    private Anno annoPubblicazione=Anno.getAnnoCorrente();
    
    private boolean posticipata = false;
    
    Integer numero=1;

    public SpedizioneItem() {
    }


    public Long getId() {
        return id;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public Spedizione getSpedizione() {
        return spedizione;
    }

    public void setSpedizione(Spedizione spedizione) {
        this.spedizione = spedizione;
    }

    public Mese getMesePubblicazione() {
        return mesePubblicazione;
    }

    public void setMesePubblicazione(Mese mesePubblicazione) {
        this.mesePubblicazione = mesePubblicazione;
    }

    public Anno getAnnoPubblicazione() {
        return annoPubblicazione;
    }

    public void setAnnoPubblicazione(Anno annoPubblicazione) {
        this.annoPubblicazione = annoPubblicazione;
    }


    public RivistaAbbonamento getRivistaAbbonamento() {
        return rivistaAbbonamento;
    }


    public void setRivistaAbbonamento(RivistaAbbonamento estrattoConto) {
        this.rivistaAbbonamento = estrattoConto;
    }
        
    @Override
    public String toString() {
        return String.format("SpedizioneItem[id=%d, ec=%d,%s %s %s, num. %d, post %b, %s ]", 
                             id,
                             rivistaAbbonamento.getId(),
                             pubblicazione.getNome(),
                             mesePubblicazione,
                             annoPubblicazione,
                             numero, 
                             posticipata,
                             spedizione
                             );
    }


    public boolean isPosticipata() {
        return posticipata;
    }


    public void setPosticipata(boolean posticipata) {
        this.posticipata = posticipata;
    }


    public Pubblicazione getPubblicazione() {
        return pubblicazione;
    }


    public void setPubblicazione(Pubblicazione pubblicazione) {
        this.pubblicazione = pubblicazione;
    }

    @Transient
    public boolean stessaPubblicazione(SpedizioneItem item) {
        if (item.getMesePubblicazione() != mesePubblicazione) {
            return false;
        }
        if (item.getAnnoPubblicazione() != annoPubblicazione) {
            return false;
        }
        if (pubblicazione.getId() != null && item.getPubblicazione().getId() != null) {
            return pubblicazione.getId() == item.getPubblicazione().getId();
        }
        return pubblicazione == item.getPubblicazione();
    }
    
    @Transient
    public String getSpedCaption() {
        return spedizione.getMeseSpedizione().getNomeBreve()+spedizione.getAnnoSpedizione().getAnnoAsString();

    }
    @Transient
    public String getPubbCaption() {
        return pubblicazione.getNome()+":"+mesePubblicazione.getNomeBreve()+annoPubblicazione.getAnnoAsString();
    }


	@Override
	public String getHeader() {
		return "Elementi Spedizione";
	}
    
 }
