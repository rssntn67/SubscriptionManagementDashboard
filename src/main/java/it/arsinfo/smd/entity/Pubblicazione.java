package it.arsinfo.smd.entity;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

import it.arsinfo.smd.data.Mese;
import it.arsinfo.smd.data.TipoPubblicazione;

@Entity
public class Pubblicazione implements SmdEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String nome;

    private String autore;

    private String editore;

    private boolean active;

    private boolean abbonamento;

    @Enumerated(EnumType.STRING)
    private Mese primaPubblicazione;

    private BigDecimal costoUnitario;

    private BigDecimal costoScontato;

    @Enumerated(EnumType.STRING)
    private TipoPubblicazione tipo;

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public TipoPubblicazione getTipo() {
        return tipo;
    }

    public void setTipo(TipoPubblicazione tipo) {
        this.tipo = tipo;
    }

    @Override
    public String toString() {
        return String.format("Pubblicazione[id=%d, Nome='%s', Tipo='%s', Prima Pubblicazione='%s']",
                             id, nome, tipo, primaPubblicazione);
    }

    public Pubblicazione(String nome, TipoPubblicazione tipo) {
        super();
        this.nome = nome;
        this.tipo = tipo;
        this.costoUnitario = BigDecimal.ZERO;
        this.costoScontato = BigDecimal.ZERO;
    }

    public Pubblicazione(String nome) {
        super();
        this.nome = nome;
        this.tipo = TipoPubblicazione.UNICO;
        this.costoUnitario = BigDecimal.ZERO;
        this.costoScontato = BigDecimal.ZERO;
    }

    public Pubblicazione() {
        super();
        this.nome = "";
        this.tipo = TipoPubblicazione.UNICO;
        this.costoUnitario = BigDecimal.ZERO;
        this.costoScontato = BigDecimal.ZERO;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isAbbonamento() {
        return abbonamento;
    }

    public void setAbbonamento(boolean abbonamento) {
        this.abbonamento = abbonamento;
    }

    public BigDecimal getCostoUnitario() {
        return costoUnitario;
    }

    public void setCostoUnitario(BigDecimal costo) {
        this.costoUnitario = costo;
    }

    public String getAutore() {
        return autore;
    }

    public void setAutore(String autore) {
        this.autore = autore;
    }

    public String getEditore() {
        return editore;
    }

    public void setEditore(String editore) {
        this.editore = editore;
    }

    public Mese getPrimaPubblicazione() {
        return primaPubblicazione;
    }

    public void setPrimaPubblicazione(Mese primapubblicazione) {
        this.primaPubblicazione = primapubblicazione;
    }

    public BigDecimal getCostoScontato() {
        if (costoScontato == BigDecimal.ZERO) {
            return costoUnitario;
        }
        return costoScontato;
    }

    public void setCostoScontato(BigDecimal costoScontato) {
        this.costoScontato = costoScontato;
    }

    @Transient
    public String getCaption() {
        return String.format("%s active=%b", nome, active);
    }

}
