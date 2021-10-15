package it.arsinfo.smd.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(uniqueConstraints={
        @UniqueConstraint(columnNames = {"anno"})
        })
//create unique index campagna_idx_anno on campagna (anno);
public class Campagna implements SmdEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private Anno anno = Anno.getAnnoProssimo();

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private StatoCampagna statoCampagna=StatoCampagna.Generata;

    @OneToMany(mappedBy="campagna", orphanRemoval=true, fetch=FetchType.EAGER)
    List<CampagnaItem> campagnaItems = new ArrayList<>();

    private boolean running = false;
    
    // Massimo numero di Riviste in abbonamento per debitori da Sospendere per la prossima campagna
    // Chi ha un abbonamento superiore a 10 riviste non va sospeso
    private Integer numero = 10;

    private BigDecimal limiteInvioEstratto=BigDecimal.ZERO;
    private BigDecimal limiteInvioSollecito=BigDecimal.ZERO;
    private BigDecimal speseEstrattoConto=BigDecimal.ZERO;
    private BigDecimal speseSollecito=BigDecimal.ZERO;

    private BigDecimal sogliaImportoTotale = new BigDecimal("70.00");
    private BigDecimal minPercIncassato = new BigDecimal("0.8");
    private BigDecimal maxDebito = new BigDecimal("7");

    public BigDecimal getSogliaImportoTotale() {
        return sogliaImportoTotale;
    }

    public void setSogliaImportoTotale(BigDecimal sogliaImportoTotale) {
        this.sogliaImportoTotale = sogliaImportoTotale;
    }

    public BigDecimal getMinPercIncassato() {
        return minPercIncassato;
    }

    public void setMinPercIncassato(BigDecimal minPercIncassato) {
        this.minPercIncassato = minPercIncassato;
    }

    public BigDecimal getMaxDebito() {
        return maxDebito;
    }

    public void setMaxDebito(BigDecimal maxDebito) {
        this.maxDebito = maxDebito;
    }

    public BigDecimal getLimiteInvioEstratto() {
        return limiteInvioEstratto;
    }

    public void setLimiteInvioEstratto(BigDecimal limiteInvioEstratto) {
        this.limiteInvioEstratto = limiteInvioEstratto;
    }

    public BigDecimal getLimiteInvioSollecito() {
        return limiteInvioSollecito;
    }

    public void setLimiteInvioSollecito(BigDecimal limiteInvioSollecito) {
        this.limiteInvioSollecito = limiteInvioSollecito;
    }

    public BigDecimal getSpeseEstrattoConto() {
        return speseEstrattoConto;
    }

    public void setSpeseEstrattoConto(BigDecimal speseEstrattoConto) {
        this.speseEstrattoConto = speseEstrattoConto;
    }

    public BigDecimal getSpeseSollecito() {
        return speseSollecito;
    }

    public void setSpeseSollecito(BigDecimal speseSollecito) {
        this.speseSollecito = speseSollecito;
    }

    public Campagna() {}

    public Anno getAnno() {
        return anno;
    }

    public void setAnno(Anno anno) {
        this.anno = anno;
    }

    public Long getId() {
        return id;
    }

    public List<CampagnaItem> getCampagnaItems() {
        return campagnaItems;
    }

    public void setCampagnaItems(List<CampagnaItem> campagnaItems) {
        this.campagnaItems = campagnaItems;
    }
    
    public void addCampagnaItem(CampagnaItem campagnaItem) {
        campagnaItems.add(campagnaItem);
    }

    @Transient
    public String getCaption() {
        return String.format("Camp.%s", anno.getAnnoAsString());
    }
    
    @Override
    public String toString() {
        return String.format("Campagna[id=%d, '%d' %s]", id,anno.getAnno(),statoCampagna);
    }

    public StatoCampagna getStatoCampagna() {
        return statoCampagna;
    }

    public void setStatoCampagna(StatoCampagna statoCampagna) {
        this.statoCampagna = statoCampagna;
    }

   @Transient
   public boolean hasPubblicazione(Pubblicazione p) {
	   for (CampagnaItem item:campagnaItems ) {
		   if (item.getPubblicazione().equals(p)) {
			   return true;
		   }
	   }
       return false;
   }

	@Override
	public String getHeader() {
        return String.format("'%s' %s", anno.getAnno(),statoCampagna);
	}

	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	public Integer getNumero() {
		return numero;
	}

	public void setNumero(Integer numero) {
		this.numero = numero;
	}

}
