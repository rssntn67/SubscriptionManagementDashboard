package it.arsinfo.smd.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Spedizione implements SmdEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private Abbonamento abbonamento;

    @ManyToOne
    private Anagrafica destinatario;

    @ManyToOne
    private Pubblicazione pubblicazione;
    
    private Integer numero = 0;


    public Spedizione() {
    }

    public Spedizione(Abbonamento abbonamento) {
        super();
        this.abbonamento = abbonamento;
    }

    public Spedizione(Abbonamento abbonamento, Pubblicazione pubblicazione,
            Anagrafica destinatario, int numero) {
        this.abbonamento= abbonamento;
        this.pubblicazione=pubblicazione;
        this.destinatario=destinatario;
        this.numero=numero;
    }

    public Long getId() {
        return id;
    }

    public Anagrafica getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(Anagrafica destinatario) {
        this.destinatario = destinatario;
    }

    public Abbonamento getAbbonamento() {
        return abbonamento;
    }

    public void setAbbonamento(Abbonamento abbonamento) {
        this.abbonamento = abbonamento;
    }

    public Pubblicazione getPubblicazione() {
        return pubblicazione;
    }

    public void setPubblicazione(Pubblicazione pubblicazione) {
        this.pubblicazione = pubblicazione;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

}
