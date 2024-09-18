package dev.snbv2.catalog;

import java.util.List;

public class Order {
    
    private List<CatalogItem> catalogItems;
    private BillingAddress billingAddress;
    private Payment payment;
    private Double orderTotal;

    public List<CatalogItem> getCatalogItems() {
        return catalogItems;
    }

    public void setCatalogItems(List<CatalogItem> catalogItems) {
        this.catalogItems = catalogItems;
    }

    public BillingAddress getBillingAddress() {
        return billingAddress;
    }

    public void setBillingAddress(BillingAddress billingAddress) {
        this.billingAddress = billingAddress;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public Double getOrderTotal() {
        return orderTotal;
    }

    public void setOrderTotal(Double orderTotal) {
        this.orderTotal = orderTotal;
    }

    @Override
    public String toString() {
        return "Order [billingAddress=" + billingAddress + ", catalogItems=" + catalogItems + ", payment=" + payment
                + ", orderTotal=" + orderTotal + "]";
    }



}
