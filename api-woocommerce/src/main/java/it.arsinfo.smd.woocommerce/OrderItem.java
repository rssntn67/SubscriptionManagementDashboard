package it.arsinfo.smd.woocommerce;

import java.math.BigDecimal;
import java.util.Map;

public class OrderItem {

    public static OrderItem getFromMap(Map m) {
        OrderItem orderitem = new OrderItem();
        orderitem.setProductId(Integer.parseInt(m.get("product_id").toString()));
        orderitem.setName(m.get("name").toString());
        orderitem.setTotalTax(new BigDecimal(m.get("total_tax").toString()));
        orderitem.setTotal(new BigDecimal(m.get("total").toString()));
        return orderitem;
    }

    @Override
    public String toString() {
        return "OrderItem{" +
                ", productId=" + productId +
                ", name='" + name + '\'' +
                ", totalTax=" + totalTax +
                ", total=" + total +
                '}';
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getTotalTax() {
        return totalTax;
    }

    public void setTotalTax(BigDecimal totalTax) {
        this.totalTax = totalTax;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    private int productId;
    private String name;
    private BigDecimal totalTax;
    private BigDecimal total;


}

