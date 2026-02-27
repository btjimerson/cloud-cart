package dev.snbv2.cloudcart.catalog;

import org.springframework.data.repository.CrudRepository;

/**
 * Spring Data repository for performing CRUD operations on {@link CatalogItem} entities.
 */
public interface CatalogRepository extends CrudRepository<CatalogItem, Integer> {

}
