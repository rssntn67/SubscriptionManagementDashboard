package it.arsinfo.smd.woocommerce;

import it.arsinfo.smd.entity.Abbonamento;
import it.arsinfo.smd.entity.WooCommerceOrder;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Product {

    public static WooCommerceOrder createFromProduct(Product p, Abbonamento abb) {
        WooCommerceOrder product = new WooCommerceOrder();
        product.setProductId(p.getId());
        product.setDescription(p.getDescription());
        product.setPrice(p.getRegularPrice());
        product.setPermalink(p.getPermalink());
        product.setName(p.getName());
        product.setAbbonamento(abb);
        return product;
    }

    public static Map<String,Object> getUpdateProcessingMap() {
        Map<String,Object> create = new HashMap<>();
        create.put("status","private");
        create.put("catalog_visibility", "hidden");
        return create;
    }

    public static Map<String,Object> getCreateMapFromAbbonamento(Abbonamento abb, String name) {
        Map<String,Object> create = new HashMap<>();
        create.put("name",name);
        create.put("regular_price",abb.getResiduo().toString());
        create.put("description", "Importo Abbonamento Riviste ADP anno "+abb.getAnno().getAnnoAsString()+ " intestatario " +abb.getIntestatario().getDenominazione());
        create.put("short_description","Abbonamento ADP");
        create.put("reviews_allowed","false");
        create.put("virtual","true");
        create.put("tax_class","nessuna-tariffa");
        return create;
    }

    public static Product getFromMap(Map map) {
        int id = Integer.parseInt(map.get("id").toString());
        Product product = new Product();
        product.setId(id);
        product.setName(map.get("name").toString());
        product.setSlug(map.get("slug").toString());
        product.setPermalink((map.get("permalink")).toString());
        product.setDescription(map.get("description").toString());
        product.setShortDescription(map.get("short_description").toString());
        product.setRegularPrice(new BigDecimal(map.get("regular_price").toString()));
        product.setTotalSales(Integer.parseInt(map.get("total_sales").toString()));
        product.setPurchasable(Boolean.parseBoolean(map.get("purchasable").toString()));
        product.setTaxClass(map.get("tax_class").toString());
        return product;
    }

    private int id;
    private String name;
    private String slug;
    private String permalink;
    private String description;
    private String shortDescription;
    private String taxClass;
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
                ", taxClass=" + taxClass +
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

    public String getTaxClass() {
        return taxClass;
    }

    public void setTaxClass(String taxClass) {
        this.taxClass = taxClass;
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

