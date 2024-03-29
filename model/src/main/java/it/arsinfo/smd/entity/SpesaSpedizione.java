package it.arsinfo.smd.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(uniqueConstraints={@UniqueConstraint(columnNames = {"rangeSpeseSpedizione" , "areaSpedizione"})})
public class SpesaSpedizione implements SmdEntity {

	public static SpesaSpedizione getSpesaSpedizione(List<SpesaSpedizione> ss, AreaSpedizione area, RangeSpeseSpedizione range) throws UnsupportedOperationException {
		for (SpesaSpedizione s: ss) {
			if (s.getAreaSpedizione() == area && s.getRangeSpeseSpedizione() == range) {
				return s;
			}
		}
		throw new UnsupportedOperationException("cannot get spese di spedizione per Area: " + area.name() + ", range: " + range.name());
	}

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private BigDecimal spese=BigDecimal.ZERO;
    private BigDecimal cor3gg=BigDecimal.ZERO;
    private BigDecimal cor24h=BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    private RangeSpeseSpedizione rangeSpeseSpedizione = RangeSpeseSpedizione.Base;
    @Enumerated(EnumType.STRING)
    private AreaSpedizione areaSpedizione=AreaSpedizione.Italia;

    public Long getId() {
        return id;
    }

    public SpesaSpedizione() {
    }

    @Override
    public String toString() {
        return String.format("SpesaSpedizione[id=%d,%s '%.2f' Eur - %s]",
                             id, rangeSpeseSpedizione,spese,areaSpedizione);
    }

    public BigDecimal getSpese() {
        return spese;
    }

    public void setSpese(BigDecimal spese) {
        this.spese = spese;
    }

	public RangeSpeseSpedizione getRangeSpeseSpedizione() {
		return rangeSpeseSpedizione;
	}

	public AreaSpedizione getAreaSpedizione() {
		return areaSpedizione;
	}

	public void setRangeSpeseSpedizione(RangeSpeseSpedizione rangeSpeseSpedizione) {
		this.rangeSpeseSpedizione = rangeSpeseSpedizione;
	}

	public void setAreaSpedizione(AreaSpedizione areaSpedizione) {
		this.areaSpedizione = areaSpedizione;
	}

	public BigDecimal getCor3gg() {
		return cor3gg;
	}

	public void setCor3gg(BigDecimal cor3gg) {
		this.cor3gg = cor3gg;
	}

	public BigDecimal getCor24h() {
		return cor24h;
	}

	public void setCor24h(BigDecimal cor24h) {
		this.cor24h = cor24h;
	}

	@Override
	public String getHeader() {
        return String.format("%s - %s]",
                rangeSpeseSpedizione,areaSpedizione);
	}

	@Transient
	public BigDecimal calcolaSpesePostali(InvioSpedizione invioSpedizione)  {
		BigDecimal spesePostali = BigDecimal.ZERO;
		switch (invioSpedizione) {
			case AdpSede:
				spesePostali = spese;
				break;
			case AdpSedeCorriere24hh:
				spesePostali = cor24h;
				break;
			case AdpSedeCorriere3gg:
				spesePostali = cor3gg;
				break;
			default:
				break;
		}
		return spesePostali;
	}

}
