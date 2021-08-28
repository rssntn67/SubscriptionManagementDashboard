package it.arsinfo.smd.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
public class WooCommerceProduct implements SmdEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(optional=false,fetch=FetchType.EAGER)
    private Abbonamento abbonamento;

    @Column(nullable=false)
    private Integer productId;
    @Column(nullable=false)
    private String name;
    @Column(nullable=false)
    private String permalink;
    @Column(nullable=false)
    private String description;
    @Column(nullable=false)
    private String shortDescription;
    @Column(nullable=false)
    private BigDecimal regularPrice;
    @Column(nullable=false)
    private boolean pagato=false;
    @Temporal(TemporalType.TIMESTAMP)
    private Date data = new Date();

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public boolean isPagato() {
        return pagato;
    }

    public void setPagato(boolean pagato) {
        this.pagato = pagato;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Long getId() {
        return id;
    }

    @Override
    public String getHeader() {
        return name;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                "productId=" + productId +
                ", name='" + name + '\'' +
                ", permalink='" + permalink + '\'' +
                ", description='" + description + '\'' +
                ", shortDescription='" + shortDescription + '\'' +
                ", regularPrice=" + regularPrice +
                '}';
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public BigDecimal getRegularPrice() {
        return regularPrice;
    }

    public void setRegularPrice(BigDecimal regularPrice) {
        this.regularPrice = regularPrice;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPermalink() {
        return permalink;
    }

    public void setPermalink(String permalink) {
        this.permalink = permalink;
    }

    public Abbonamento getAbbonamento() {
        return abbonamento;
    }

    public void setAbbonamento(Abbonamento abbonamento) {
        this.abbonamento = abbonamento;
    }
}

