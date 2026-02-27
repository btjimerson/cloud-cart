package dev.snbv2.cloudcart.catalog;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lombok.extern.apachecommons.CommonsLog;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * REST controller that exposes endpoints for retrieving catalog items.
 */
@CommonsLog
@RestController
public class CatalogController {

    private final CatalogRepository catalogRepository;

    public CatalogController(CatalogRepository catalogRepository) {
        this.catalogRepository = catalogRepository;
    }

    /**
     * Retrieves all catalog items from the repository.
     *
     * @return a list of all catalog items
     */
    @GetMapping("/catalog")
    public List<CatalogItem> getAllCatalogItems() {

        Iterable<CatalogItem> catalogItemsIterable = catalogRepository.findAll();
        List<CatalogItem> catalogItems = new ArrayList<>();

        for (CatalogItem ci : catalogItemsIterable) {
            catalogItems.add(ci);
        }

        log.debug(String.format("All catalog items = [%s]", catalogItems));
        return catalogItems;

    }

    /**
     * Retrieves a single catalog item by its identifier.
     *
     * @param id the identifier of the catalog item to retrieve
     * @return the matching catalog item
     * @throws ResponseStatusException if no catalog item is found for the given id
     */
    @GetMapping("/catalog/{id}")
    public CatalogItem getCatalogItem(@PathVariable("id") Integer id) {

        Optional<CatalogItem> catalogItem = catalogRepository.findById(id);

        if (catalogItem.isPresent()) {
            log.debug(String.format("Catalog item retrieved = [%s]", catalogItem.get()));
            return catalogItem.get();
        } else {
            log.info(String.format("No catalog item found for id [%s]", id));
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Catalog item not found for id " + id);
        }

    }
}
