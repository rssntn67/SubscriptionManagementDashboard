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
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

@Entity
public class SpedizioneItem implements SmdEntity {

    public static List<SpedizioneItem> generaSpedizioneItems(Rivista ec) throws UnsupportedOperationException {
        log.info("generaSpedizioneItems: {}", ec);
        List<SpedizioneItem> items = new ArrayList<>();
        Map<Anno, EnumSet<Mese>> mappaPubblicazioni = Rivista.getAnnoMeseMap(ec);
        for (Anno anno: mappaPubblicazioni.keySet()) {
            mappaPubblicazioni.get(anno).forEach(mese -> {
                SpedizioneItem item = new SpedizioneItem();
                item.setRivista(ec);
                item.setAnnoPubblicazione(anno);
                item.setMesePubblicazione(mese);
                item.setNumero(ec.getNumero());
                item.setPubblicazione(ec.getPubblicazione());
                items.add(item);
                log.info("generaSpedizioneItems: {} ", item);
            });
        }
        if (items.isEmpty()) {
            throw new UnsupportedOperationException("Nessuna spedizione per rivista in Abbonamento");
        }
        log.info("generaSpedizioneItems: generati {} items", items.size());
        return items;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(optional=false,fetch=FetchType.LAZY)
    private Spedizione spedizione;

    @ManyToOne(optional=false,fetch=FetchType.EAGER)
    private Pubblicazione pubblicazione;

    @ManyToOne(optional=false,fetch=FetchType.LAZY)
    private Rivista rivista;
    
    @Enumerated(EnumType.STRING)
    private Mese mesePubblicazione=Mese.getMeseCorrente();
    @Enumerated(EnumType.STRING)
    private Anno annoPubblicazione=Anno.getAnnoCorrente();
    
    private boolean posticipata = false;
    
    @Enumerated(EnumType.STRING)
    private StatoSpedizione statoSpedizione = StatoSpedizione.PROGRAMMATA;

    private Integer numero=1;

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


    public Rivista getRivista() {
        return rivista;
    }


    public void setRivista(Rivista rivista) {
        this.rivista = rivista;
    }
        
    @Override
    public String toString() {
        return String.format("SpedizioneItem[id=%d, ec=%d,%s %s %s, num. %d, post %b,%s]", 
                             id,
                             rivista.getId(),
                             pubblicazione.getNome(),		
                             mesePubblicazione,
                             annoPubblicazione,
                             numero, 
                             posticipata,
                             statoSpedizione
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
    public String getPubbCaption() {
        return pubblicazione.getNome()+":"+mesePubblicazione.getNomeBreve()+annoPubblicazione.getAnnoAsString();
    }


	@Override
	public String getHeader() {
		return "Elementi Spedizione";
	}


	public StatoSpedizione getStatoSpedizione() {
		return statoSpedizione;
	}


	public void setStatoSpedizione(StatoSpedizione statoSpedizione) {
		this.statoSpedizione = statoSpedizione;
	}
    
 }
