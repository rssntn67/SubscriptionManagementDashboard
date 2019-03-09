package it.arsinfo.smd.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class AbbonamentoPubblicazione implements SmdEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private Abbonamento abbonamento;
    
    @ManyToOne
    private Pubblicazione pubblicazione;
    
    private Integer numero = 0;
    
    public AbbonamentoPubblicazione(Abbonamento abbonamento, Pubblicazione pubblicazione, int numero) {
        this.pubblicazione = pubblicazione;
        this.numero = numero;
        this.abbonamento = abbonamento;
    }

    public AbbonamentoPubblicazione(Abbonamento abbonamento, Pubblicazione pubblicazione) {
        this.pubblicazione = pubblicazione;
        this.abbonamento = abbonamento;
    }

    public AbbonamentoPubblicazione() {
        super();
    }


    public Abbonamento getAbbonamento() {
        return abbonamento;
    }

    public void setAbbonamento(Abbonamento abbonamento) {
        this.abbonamento = abbonamento;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

}
