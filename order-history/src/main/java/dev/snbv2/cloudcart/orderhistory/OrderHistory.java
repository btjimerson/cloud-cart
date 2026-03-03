package dev.snbv2.cloudcart.orderhistory;

import java.math.BigDecimal;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="order_history")
public class OrderHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(unique = true)
    private String correlationId;

    @Column
    private String purchaseDate;

    @Column
    private BigDecimal purchaseAmount;

    @Column
    private Integer numberOfItems;

    @Column
    private String paymentStatus;

    @Column
    private String orderStatus;

    public OrderHistory() {
    }

    public OrderHistory(Integer id, String correlationId, String purchaseDate, BigDecimal purchaseAmount,
            Integer numberOfItems, String paymentStatus, String orderStatus) {
        this.id = id;
        this.correlationId = correlationId;
        this.purchaseDate = purchaseDate;
        this.purchaseAmount = purchaseAmount;
        this.numberOfItems = numberOfItems;
        this.paymentStatus = paymentStatus;
        this.orderStatus = orderStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderHistory that = (OrderHistory) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "OrderHistory [id=" + id + ", correlationId=" + correlationId + ", purchaseDate=" + purchaseDate
                + ", purchaseAmount=" + purchaseAmount + ", numberOfItems=" + numberOfItems
                + ", paymentStatus=" + paymentStatus + ", orderStatus=" + orderStatus + "]";
    }

}
