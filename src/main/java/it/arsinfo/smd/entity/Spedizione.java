package it.arsinfo.smd.entity;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import it.arsinfo.smd.Smd;
import it.arsinfo.smd.data.Invio;
import it.arsinfo.smd.data.InvioSpedizione;
import it.arsinfo.smd.data.Omaggio;
import it.arsinfo.smd.data.Paese;
import it.arsinfo.smd.data.Provincia;

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
    
    @ManyToOne
    private Storico storico;
    
    @Enumerated(EnumType.STRING)
    private Omaggio omaggio = Omaggio.No;

    @Enumerated(EnumType.STRING)
    private Invio invio = Invio.Destinatario;

    @Enumerated(EnumType.STRING)
    private InvioSpedizione invioSpedizione = InvioSpedizione.Spedizioniere;

    private Integer numero = 1;

    private boolean sospesa=false;

    public Spedizione() {
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

    @Transient
    public String getHeader() {
        return String.format("%s:Spedizione:Edit:'%s'", abbonamento.getHeader(),pubblicazione.getNome());
    }

    @Override
    public String toString() {
        if (storico == null) {
            return String.format("Spedizione[id=%d, Abbonamento=%d, Pubblicazione=%d, Numero=%d, Destinatario=%d, Omaggio=%s, Invio=%s, Sospeso=%b]", 
                             id,abbonamento.getId(),pubblicazione.getId(),numero, destinatario.getId(), omaggio, invio,sospesa);
        }
        return String.format("Spedizione[id=%d, Abbonamento=%d, Pubblicazione=%d, Numero=%d, Destinatario=%d, Omaggio=%s, Invio=%s, Sospeso=%b, Storico=%d]", 
                             id,abbonamento.getId(),pubblicazione.getId(),numero, destinatario.getId(), omaggio, invio, sospesa,storico.getId());
    }

    public boolean isSospesa() {
        return sospesa;
    }

    public void setSospesa(boolean sospesa) {
        this.sospesa = sospesa;
    }
    
    @Transient
    public String getDecodeSospesa() {
        return Smd.decodeForGrid(sospesa);
    }

    @Transient
    public String getIntestazione() {
        return destinatario.getCaption();
    }

    @Transient
    public String getSottoIntestazione() {
        if (invio == Invio.Destinatario) {
            return "";
        }
        return "c/o " + abbonamento.getIntestatario().getNome() + " " + abbonamento.getIntestatario().getCognome();
    }
    
    @Transient
    public String getIndirizzo() {
        if (invio == Invio.Destinatario) {
            return destinatario.getIndirizzo();
        }
        return abbonamento.getIntestatario().getIndirizzo();
    }

    @Transient
    public String getCap() {
        if (invio == Invio.Destinatario) {
            return destinatario.getCap();
        }
        return abbonamento.getIntestatario().getCap();
    }

    @Transient
    public String getCitta() {
        if (invio == Invio.Destinatario) {
            return destinatario.getCitta();
        }
        return abbonamento.getIntestatario().getCitta();
    }

    @Transient
    public Provincia getProvincia() {
        if (invio == Invio.Destinatario) {
            return destinatario.getProvincia();
        }
        return abbonamento.getIntestatario().getProvincia();

    }
    @Transient
    public Paese getPaese() {
        if (invio == Invio.Destinatario) {
            return destinatario.getPaese();
        }
        return abbonamento.getIntestatario().getPaese();        
    }

    public Storico getStorico() {
        return storico;
    }

    public void setStorico(Storico storico) {
        this.storico = storico;
    }

    public InvioSpedizione getInvioSpedizione() {
        return invioSpedizione;
    }

    public void setInvioSpedizione(InvioSpedizione invioSpedizione) {
        this.invioSpedizione = invioSpedizione;
    }
}
