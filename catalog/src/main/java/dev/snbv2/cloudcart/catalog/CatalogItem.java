package dev.snbv2.cloudcart.catalog;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * JPA entity representing an item in the product catalog.
 */
@Getter
@Setter
@ToString
@Entity
@Table(name = "catalog")
public class CatalogItem {

    @Id
    private Integer id;

    private String name;
    private String imageSource;
    private String description;
    private BigDecimal amount;

    /**
     * Default no-argument constructor required by JPA.
     */
    public CatalogItem() {
    }

    /**
     * Constructs a catalog item with all fields specified.
     *
     * @param id          the unique identifier
     * @param name        the item name
     * @param imageSource the image source URL or path
     * @param description the item description
     * @param amount      the item price amount
     */
    public CatalogItem(Integer id, String name, String imageSource, String description, BigDecimal amount) {
        this.id = id;
        this.name = name;
        this.imageSource = imageSource;
        this.description = description;
        this.amount = amount;
    }

    /**
     * Gets the item identifier as a string, returning an empty string if the identifier is null.
     *
     * @return the item identifier as a string, or an empty string if null
     */
    public String getIdAsString() {
        return id == null ? "" : id.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CatalogItem that = (CatalogItem) o;
        return Objects.equals(id, that.id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
