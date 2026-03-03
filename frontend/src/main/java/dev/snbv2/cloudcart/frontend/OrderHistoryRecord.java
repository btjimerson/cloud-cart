package dev.snbv2.cloudcart.frontend;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class OrderHistoryRecord {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a");

    private Integer id;
    private String correlationId;
    private String purchaseDate;
    private BigDecimal purchaseAmount;
    private Integer numberOfItems;
    private String paymentStatus;
    private String orderStatus;

    public OrderHistoryRecord() {
    }

    public String getFormattedPurchaseDate() {
        if (purchaseDate == null || purchaseDate.isBlank()) {
            return "";
        }
        return Instant.parse(purchaseDate)
                .atZone(ZoneId.systemDefault())
                .format(FORMATTER);
    }

}
