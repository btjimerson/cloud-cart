package dev.snbv2.cloudcart.frontend;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Represents an item in the product catalog with its name, image, description, and price.
 */
@Getter
@Setter
@ToString
public class CatalogItem {

    private Integer id;
    private String name;
    private String imageSource;
    private String description;
    private BigDecimal amount;

    /**
     * Constructs an empty catalog item.
     */
    public CatalogItem() {
    }

    /**
     * Constructs a catalog item with the specified properties.
     *
     * @param id the unique identifier of the catalog item
     * @param name the display name of the item
     * @param imageSource the image source URL or path for the item
     * @param description a textual description of the item
     * @param amount the price of the item
     */
    public CatalogItem(Integer id, String name, String imageSource, String description, BigDecimal amount) {
        this.id = id;
        this.name = name;
        this.imageSource = imageSource;
        this.description = description;
        this.amount = amount;
    }

}
