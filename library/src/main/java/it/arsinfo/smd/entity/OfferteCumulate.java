package it.arsinfo.smd.entity;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import it.arsinfo.smd.data.Anno;

@Entity
@Table(uniqueConstraints={
        @UniqueConstraint(columnNames = {"anno"})
        })
public class OfferteCumulate implements SmdEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Anno anno = Anno.getAnnoCorrente();

    @Column(nullable = false)
    private BigDecimal importo=BigDecimal.ZERO;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Anno getAnno() {
		return anno;
	}

	public void setAnno(Anno anno) {
		this.anno = anno;
	}

	public BigDecimal getImporto() {
		return importo;
	}

	public void setImporto(BigDecimal importo) {
		this.importo = importo;
	}

	@Override
	public String getHeader() {
		return String.format("%s %.2f", anno.getAnnoAsString(),importo);
	}

	@Override
	public String toString() {
		return String.format("OfferteCumulate [id=%d %s %.2f]",id, anno.getAnnoAsString(),importo);
	}

}
