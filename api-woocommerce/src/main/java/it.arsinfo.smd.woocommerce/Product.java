package it.arsinfo.smd.woocommerce;

import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.WooCommerceProduct;

import java.math.BigDecimal;
import java.util.Objects;

public class Product {

    public static WooCommerceProduct createFromProduct(Product p, Abbonamento abb) {
        WooCommerceProduct product = new WooCommerceProduct();
        product.setProductId(p.getId());
        product.setDescription(p.getDescription());
        product.setRegularPrice(p.getRegularPrice());
        product.setPermalink(p.getPermalink());
        product.setName(p.getName());
        product.setAbbonamento(abb);
        return product;
    }

    private int id;
    private String name;
    private String slug;
    private String permalink;
    private String description;
    private String shortDescription;
    private BigDecimal regularPrice;
    private int totalSales;
    private boolean purchasable;

    public boolean isPurchasable() {
        return purchasable;
    }

    public void setPurchasable(boolean purchasable) {
        this.purchasable = purchasable;
    }

    public int getTotalSales() {
        return totalSales;
    }

    public void setTotalSales(int totalSales) {
        this.totalSales = totalSales;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", slug='" + slug + '\'' +
                ", permalink='" + permalink + '\'' +
                ", description='" + description + '\'' +
                ", shortDescription='" + shortDescription + '\'' +
                ", purchasable=" + purchasable +
                ", regularPrice=" + regularPrice +
                ", totalSales=" + totalSales +
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

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getPermalink() {
        return permalink;
    }

    public void setPermalink(String permalink) {
        this.permalink = permalink;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return id == product.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

