package it.arsinfo.smd.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

@Entity
public class AnagraficaPubblicazione {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private Anagrafica intestatario;

    @ManyToOne
    private Anagrafica destinatario;

    @ManyToOne
    private Pubblicazione pubblicazione;
    
    private Integer numero = 0;
    
    public AnagraficaPubblicazione(Anagrafica intestatario, Pubblicazione pubblicazione, int numero) {
        this.pubblicazione = pubblicazione;
        this.numero = numero;
        this.intestatario = intestatario;
        this.destinatario = intestatario;
    }

    public AnagraficaPubblicazione(Anagrafica intestatario, Anagrafica destinatario,Pubblicazione pubblicazione, int numero) {
        this.pubblicazione = pubblicazione;
        this.numero = numero;
        this.intestatario = intestatario;
        this.destinatario = destinatario;
    }

    public AnagraficaPubblicazione() {
        super();
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

    public Anagrafica getIntestatario() {
        return intestatario;
    }

    public void setIntestatario(Anagrafica intestatario) {
        this.intestatario = intestatario;
    }

    public Anagrafica getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(Anagrafica destinatario) {
        this.destinatario = destinatario;
    }
    
    @Transient
    public String getNomeDestinatario() {
        return String.format("'%s %s'", destinatario.getNome(),destinatario.getCognome());
    }

    @Transient
    public String getNomeIntestatario() {
        return String.format("'%s %s'", intestatario.getNome(),intestatario.getCognome());
    }
    
    @Transient
    public String getNomePubblicazione() {
        return pubblicazione.getNome();
    }


    @Override
    public String toString() {
        return String.format("AnagraficaPubblicazione[id=%d, Pubblicazione='%s', Intestatario='%s', Destinatario='%s', Numero='%d']",
                             id, pubblicazione, intestatario, destinatario, numero);
    }

}
