package it.arsinfo.smd.entity;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import it.arsinfo.smd.data.Invio;
import it.arsinfo.smd.data.Omaggio;

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
    
    @Enumerated(EnumType.STRING)
    private Omaggio omaggio = Omaggio.No;

    @Enumerated(EnumType.STRING)
    private Invio invio = Invio.Destinatario;

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

    public Omaggio getOmaggio() {
        return omaggio;
    }

    public void setOmaggio(Omaggio omaggio) {
        this.omaggio = omaggio;
    }

    public Invio getInvio() {
        return invio;
    }

    public void setInvio(Invio invio) {
        this.invio = invio;
    }

}
