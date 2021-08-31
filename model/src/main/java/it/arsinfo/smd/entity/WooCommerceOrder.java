package it.arsinfo.smd.entity;

import it.arsinfo.smd.data.StatoWooCommerceOrder;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
public class WooCommerceOrder implements SmdEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(optional=false,fetch=FetchType.EAGER)
    private Abbonamento abbonamento;

    @Column(nullable=false)
    private Integer productId;

    private Integer orderId;

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    @Column(nullable=false)
    private String name;
    @Column(nullable=false)
    private String permalink;
    @Column(nullable=false)
    private String description;
    @Column(nullable=false)
    private String shortDescription;
    @Column(nullable=false)
    private BigDecimal price;
    @Column(nullable=false)
    @Enumerated(EnumType.STRING)
    private StatoWooCommerceOrder status=StatoWooCommerceOrder.Generated;
    @Temporal(TemporalType.TIMESTAMP)
    private Date data = new Date();

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
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

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
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

    @Override
    public String toString() {
        return "WooCommerceOrder{" +
                "id=" + id +
                ", abbonamento=" + abbonamento +
                ", productId=" + productId +
                ", orderId=" + orderId +
                ", name='" + name + '\'' +
                ", permalink='" + permalink + '\'' +
                ", description='" + description + '\'' +
                ", shortDescription='" + shortDescription + '\'' +
                ", price=" + price +
                ", status=" + status +
                ", data=" + data +
                '}';
    }

    public StatoWooCommerceOrder getStatus() {
        return status;
    }

    public void setStatus(StatoWooCommerceOrder status) {
        this.status = status;
    }
}

