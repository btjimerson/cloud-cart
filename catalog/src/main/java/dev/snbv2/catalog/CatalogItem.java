package dev.snbv2.catalog;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "catalog")
public class CatalogItem {

    @Id
    private Integer id;

    private String name;
    private String imageSource;
    private String description;
    private Double amount;

    public CatalogItem() {
    }

    public CatalogItem(Integer id, String name, String imageSource, String description, Double amount) {
        this.id = id;
        this.name = name;
        this.imageSource = imageSource;
        this.description = description;
        this.amount = amount;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageSource() {
        return imageSource;
    }

    public void setImageSource(String imageSource) {
        this.imageSource = imageSource;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getIdAsString() {
        return id == null ? "" : id.toString();
    }

    @Override
    public String toString() {
        return "CatalogItem [amount=" + amount + ", description=" + description + ", id=" + id + ", name=" + name + "]";
    }

}
