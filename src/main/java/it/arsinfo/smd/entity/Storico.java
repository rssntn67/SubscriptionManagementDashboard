package it.arsinfo.smd.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import it.arsinfo.smd.data.Cassa;
import it.arsinfo.smd.data.Invio;
import it.arsinfo.smd.data.InvioSpedizione;
import it.arsinfo.smd.data.StatoStorico;
import it.arsinfo.smd.data.TipoEstrattoConto;

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
    
    @OneToMany(mappedBy="storico", orphanRemoval=true, fetch=FetchType.EAGER)
    private List<Nota> note = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private TipoEstrattoConto tipoEstrattoConto = TipoEstrattoConto.Ordinario;

    @Enumerated(EnumType.STRING)
    private Cassa cassa = Cassa.Ccp;

    @Enumerated(EnumType.STRING)
    private Invio invio = Invio.Destinatario;

    @Enumerated(EnumType.STRING)
    private InvioSpedizione invioSpedizione = InvioSpedizione.Spedizioniere;

    private Integer numero = 1;
    
    @Enumerated(EnumType.STRING)
    private StatoStorico statoStorico = StatoStorico.NUOVO;

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
        return String.format("Storico[id=%d, , '%d %s' a '%s', ->'%s', %s %s, %s, %s]",
                             id, 
                             numero, 
                             pubblicazione.getNome(), 
                             intestatario.getCaption(), 
                             destinatario.getCaption(), 
                             tipoEstrattoConto,invio,statoStorico,cassa);
    }

    public TipoEstrattoConto getTipoEstrattoConto() {
        return tipoEstrattoConto;
    }

    public void setTipoEstrattoConto(TipoEstrattoConto omaggio) {
        this.tipoEstrattoConto = omaggio;
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
    
    @Transient
    public boolean attivo() {
        boolean regolare = false;
        switch (statoStorico) {
        case NUOVO:
            regolare=true;
            break;
        case VALIDO:
            regolare=true;
            break;
        case SOSPESO:
            break;
        default:
            break;
        }
        
        return regolare;
    }

    public InvioSpedizione getInvioSpedizione() {
        return invioSpedizione;
    }

    public void setInvioSpedizione(InvioSpedizione invioSpedizione) {
        this.invioSpedizione = invioSpedizione;
    }
    
}
