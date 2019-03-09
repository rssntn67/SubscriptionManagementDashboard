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

    private Integer estratti = 0;
    private Integer blocchetti = 0;
    private Integer lodare = 0;
    private Integer messaggio = 0;


    public Spedizione() {
    }

    public Spedizione(Abbonamento abbonamento) {
        super();
        this.abbonamento = abbonamento;
    }

    public Long getId() {
        return id;
    }

    public Integer getEstratti() {
        return estratti;
    }

    public void setEstratti(Integer estratti) {
        this.estratti = estratti;
    }

    public Integer getBlocchetti() {
        return blocchetti;
    }

    public void setBlocchetti(Integer blocchetti) {
        this.blocchetti = blocchetti;
    }

    public Integer getLodare() {
        return lodare;
    }

    public void setLodare(Integer lodare) {
        this.lodare = lodare;
    }

    public Integer getMessaggio() {
        return messaggio;
    }

    public void setMessaggio(Integer messaggio) {
        this.messaggio = messaggio;
    }

    public Anagrafica getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(Anagrafica destinatario) {
        this.destinatario = destinatario;
    }

}
