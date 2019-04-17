package it.arsinfo.smd.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import it.arsinfo.smd.data.Cassa;
import it.arsinfo.smd.data.Invio;
import it.arsinfo.smd.data.Omaggio;
import it.arsinfo.smd.data.StatoStorico;

@Entity
public class Storico implements SmdEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private Anagrafica intestatario;

    @ManyToOne
    private Anagrafica destinatario;

    @ManyToOne
    private Pubblicazione pubblicazione;
    
    @OneToMany(cascade = CascadeType.ALL)
    private List<Nota> note = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private Omaggio omaggio = Omaggio.No;

    @Enumerated(EnumType.STRING)
    private Cassa cassa = Cassa.Ccp;

    @Enumerated(EnumType.STRING)
    private Invio invio = Invio.Destinatario;

    private Integer numero = 1;
    
    @Enumerated(EnumType.STRING)
    private StatoStorico statoStorico = StatoStorico.N;

    public Storico() {
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
    public String getCaptionDestinatario() {
        return destinatario.getCaption();
    }

    @Transient
    public String getCaptionIntestatario() {
        return intestatario.getCaption();
    }
    
    @Transient
    public String getCaptionPubblicazione() {
        if (pubblicazione != null)
            return pubblicazione.getNome();
        else
            return "";
    }
    
    @Transient
    public String getCaption() {
        return String.format("Intestatario:'%s', Pubblicazione:'%s', Destinatario:'%s'", intestatario.getCaption(), pubblicazione.getNome(),destinatario.getCaption());
    }
    @Transient
    public String getHeader() {
        return String.format("%s:Storico:Edit", intestatario.getHeader());
    }
    
    @Override
    public String toString() {
        return String.format("Storico[id=%d, Pubblicazione='%d', Intestatario='%d', Destinatario='%d', Numero='%d', Omaggio=%s, Invio=%s, Stato=%b, Cassa=%s]",
                             id, pubblicazione.getId(), intestatario.getId(), destinatario.getId(), numero,omaggio,invio,statoStorico,cassa);
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

    public Cassa getCassa() {
        return cassa;
    }

    public void setCassa(Cassa cassa) {
        this.cassa = cassa;
    }

    public List<Nota> getNote() {
        return note;
    }

    public void setNote(List<Nota> note) {
        this.note = note;
    }

    public void addNota(Nota nota) {
        note.add(nota);
    }

    public StatoStorico getStatoStorico() {
        return statoStorico;
    }

    public void setStatoStorico(StatoStorico statoStorico) {
        this.statoStorico = statoStorico;
    }
    
}
