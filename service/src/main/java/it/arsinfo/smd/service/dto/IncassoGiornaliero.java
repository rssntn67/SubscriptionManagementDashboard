package it.arsinfo.smd.service.dto;

import it.arsinfo.smd.entity.Cassa;
import it.arsinfo.smd.entity.Ccp;
import it.arsinfo.smd.entity.SmdEntity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

public class IncassoGiornaliero {

    public IncassoGiornaliero(Date dataIncasso, Cassa cassa, Ccp ccp) {
        this.dataIncasso = SmdEntity.getStandardDate(dataIncasso);
        this.cassa = cassa;
        this.ccp = ccp;
    }

    final private Date dataIncasso;
    final private Cassa cassa;
    final private Ccp ccp;

    private BigDecimal importo=BigDecimal.ZERO;

    @Override
    public int hashCode() {
        return Objects.hash(SmdEntity.getStandardDate(dataIncasso), cassa, ccp);
    }

    private BigDecimal incassato=BigDecimal.ZERO;
    private BigDecimal messaggio=BigDecimal.ZERO;
    private BigDecimal blocchetti=BigDecimal.ZERO;
    private BigDecimal manifesti=BigDecimal.ZERO;
    private BigDecimal lodare=BigDecimal.ZERO;
    private BigDecimal nonAssociati=BigDecimal.ZERO;
    private BigDecimal residuo=BigDecimal.ZERO;

    public Cassa getCassa() {
        return cassa;
    }

    public Ccp getCcp() {
        return ccp;
    }

    public BigDecimal getImporto() {
        return importo;
    }

    public void setImporto(BigDecimal importo) {
        this.importo = importo;
    }

    public BigDecimal getIncassato() {
        return incassato;
    }

    public void setIncassato(BigDecimal incassato) {
        this.incassato = incassato;
    }

    public BigDecimal getMessaggio() {
        return messaggio;
    }

    public void setMessaggio(BigDecimal messaggio) {
        this.messaggio = messaggio;
    }

    public BigDecimal getBlocchetti() {
        return blocchetti;
    }

    public void setBlocchetti(BigDecimal blocchetti) {
        this.blocchetti = blocchetti;
    }

    public BigDecimal getManifesti() {
        return manifesti;
    }

    public void setManifesti(BigDecimal manifesti) {
        this.manifesti = manifesti;
    }

    public BigDecimal getLodare() {
        return lodare;
    }

    public void setLodare(BigDecimal lodare) {
        this.lodare = lodare;
    }

    public BigDecimal getNonAssociati() {
        return nonAssociati;
    }

    public void setNonAssociati(BigDecimal nonAssociati) {
        this.nonAssociati = nonAssociati;
    }

    public BigDecimal getResiduo() {
        return residuo;
    }

    public void setResiduo(BigDecimal residuo) {
        this.residuo = residuo;
    }

    public Date getDataIncasso() {
        return dataIncasso;
    }

}
