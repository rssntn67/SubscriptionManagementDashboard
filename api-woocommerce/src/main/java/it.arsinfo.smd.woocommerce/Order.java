package it.arsinfo.smd.woocommerce;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Order {

    public enum OrderStatus{
        Unknown,
        Processing,
        Completed;

        public static OrderStatus get(String status) {
            if (status.equals("completed") )
                return OrderStatus.Completed;
            if (status.equals("processing"))
                return OrderStatus.Processing;
            return OrderStatus.Unknown;
        }
    }
    public static Date getFromString(String d) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Order getFromMap(Map m) {
        Order order = new Order();
        order.setId(Integer.parseInt(m.get("id").toString()));
        order.setNumber(Integer.parseInt(m.get("number").toString()));
        order.setStatus(OrderStatus.get(m.get("status").toString()));
        order.setPaymentMethod(m.get("payment_method").toString());
        order.setTransactionId(m.get("transaction_id").toString());
        if ( m.get("date_completed") != null)
            order.setDateCompleted(getFromString(m.get("date_completed").toString()));
        order.setDatePaid(getFromString(m.get("date_paid").toString()));
        order.setCartHash(m.get("cart_hash").toString());
        order.setTotalTax(new BigDecimal(m.get("total_tax").toString()));
        order.setTotal(new BigDecimal(m.get("total").toString()));
        List<Map> line_items = (List)m.get("line_items");
        for (Map line_item: line_items) {
            order.getOrderItems().add(OrderItem.getFromMap(line_item));
        }
        Map b = (Map) m.get("billing");
        order.setBilling(Billing.getFromMap(b));
        return order;
    }

    public static Map<String,Object> getStatusCompletedMap() {
        Map<String,Object> update = new HashMap<>();
        update.put("status","completed");
        return update;
    }

    public static Map<String,Object> getStatusProcessingMap() {
        Map<String,Object> update = new HashMap<>();
        update.put("status","processing");
        return update;
    }

    private int id;
    private int number;
    private OrderStatus status;
    private String paymentMethod;
    private String transactionId;
    private Date dateCompleted;
    private Date datePaid;
    private String cartHash;
    private BigDecimal totalTax;
    private BigDecimal total;
    List<OrderItem> orderItems = new ArrayList<>();
    private Billing billing;

    public Billing getBilling() {
        return billing;
    }

    public void setBilling(Billing billing) {
        this.billing = billing;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public Date getDateCompleted() {
        return dateCompleted;
    }

    public void setDateCompleted(Date dateCompleted) {
        this.dateCompleted = dateCompleted;
    }

    public Date getDatePaid() {
        return datePaid;
    }

    public void setDatePaid(Date datePaid) {
        this.datePaid = datePaid;
    }

    public String getCartHash() {
        return cartHash;
    }

    public void setCartHash(String cartHash) {
        this.cartHash = cartHash;
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

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", number=" + number +
                ", status=" + status +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", transactionId='" + transactionId + '\'' +
                ", dateCompleted=" + dateCompleted +
                ", datePaid=" + datePaid +
                ", cartHash='" + cartHash + '\'' +
                ", totalTax=" + totalTax +
                ", total=" + total +
                ", orderItems=" + orderItems +
                ", billing=" + billing +
                '}';
    }
}

