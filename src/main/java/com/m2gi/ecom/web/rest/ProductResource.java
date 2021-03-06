package com.m2gi.ecom.web.rest;

import com.m2gi.ecom.domain.Category;
import com.m2gi.ecom.domain.Product;
import com.m2gi.ecom.domain.UserDetails;
import com.m2gi.ecom.repository.ProductRepository;
import com.m2gi.ecom.security.SecurityUtils;
import com.m2gi.ecom.service.CategoryService;
import com.m2gi.ecom.service.ProductService;
import com.m2gi.ecom.service.UserDetailsService;
import com.m2gi.ecom.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.m2gi.ecom.domain.Product}.
 */
@RestController
@RequestMapping("/api")
public class ProductResource {

    private final Logger log = LoggerFactory.getLogger(ProductResource.class);

    private static final String ENTITY_NAME = "product";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ProductService productService;
    private final UserDetailsService userDetailsService;
    private final CategoryService categoryService;

    private final ProductRepository productRepository;

    public ProductResource(
        ProductService productService,
        UserDetailsService userDetailsService,
        ProductRepository productRepository,
        CategoryService categoryService
    ) {
        this.productService = productService;
        this.productRepository = productRepository;
        this.userDetailsService = userDetailsService;
        this.categoryService = categoryService;
    }

    /**
     * {@code POST  /products} : Create a new product.
     *
     * @param product the product to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new product, or with status {@code 400 (Bad Request)} if the product has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/products")
    public ResponseEntity<Product> createProduct(@Valid @RequestBody Product product) throws URISyntaxException {
        log.debug("REST request to save Product : {}", product);
        if (product.getId() != null) {
            throw new BadRequestAlertException("A new product cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Product result = productService.save(product);
        return ResponseEntity
            .created(new URI("/api/products/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /products/:id} : Updates an existing product.
     *
     * @param id the id of the product to save.
     * @param product the product to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated product,
     * or with status {@code 400 (Bad Request)} if the product is not valid,
     * or with status {@code 500 (Internal Server Error)} if the product couldn't be updated.
     */
    @PutMapping("/products/{id}")
    public ResponseEntity<Product> updateProduct(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Product product
    ) {
        log.debug("REST request to update Product : {}, {}", id, product);
        if (product.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, product.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!productRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Product result = productService.save(product);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, product.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /products/:id} : Partial updates given fields of an existing product, field will ignore if it is null
     *
     * @param id the id of the product to save.
     * @param product the product to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated product,
     * or with status {@code 400 (Bad Request)} if the product is not valid,
     * or with status {@code 404 (Not Found)} if the product is not found,
     * or with status {@code 500 (Internal Server Error)} if the product couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/products/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Product> partialUpdateProduct(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Product product
    ) throws URISyntaxException {
        log.debug("REST request to partial update Product partially : {}, {}", id, product);
        if (product.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, product.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!productRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Product> result = productService.partialUpdate(product);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, product.getId().toString())
        );
    }

    /**
     * {@code GET  /products} : get products from research.
     *
     * @param query the research query.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of products in body.
     */
    @GetMapping("/products")
    public List<Product> getProducts(
        @RequestParam(name = "query", required = false) String query,
        @RequestParam(name = "category", required = false) Long categoryId,
        @RequestParam(name = "sortBy", required = false) String sortBy,
        @RequestParam(name = "sortOrder", required = false) Sort.Direction sortOrder
    ) {
        Sort sort;
        if (sortBy != null && sortOrder != null) {
            sort = Sort.by(sortOrder, sortBy);
        } else {
            sort = Sort.by(Sort.Direction.ASC, "name");
        }
        if (query != null) {
            log.debug("REST request to get Research Products for query : {}", query);
            return productService.findResearch(query, sort);
        } else if (categoryId != null) {
            log.debug("REST request to get Products for category : {}", categoryId);
            Optional<Category> cat = categoryService.findOne(categoryId);
            if (cat.isPresent()) {
                return productService.findCategory(cat.get(), sort);
            } else {
                throw new BadRequestAlertException("Category unknown", "category", "idnotfound");
            }
        } else {
            log.debug("REST request to get all Products");
            return productService.findAll(sort);
        }
    }

    /**
     * {@code GET  /products/:id} : get the "id" product.
     *
     * @param id the id of the product to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the product, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/products/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable Long id) {
        log.debug("REST request to get Product : {}", id);
        Optional<Product> product = productService.findOne(id);
        return ResponseUtil.wrapOrNotFound(product);
    }

    /**
     * {@code DELETE  /products/:id} : delete the "id" product.
     *
     * @param id the id of the product to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/products/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        log.debug("REST request to delete Product : {}", id);
        productService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code GET  /favorite-products} : get the current authenticated user's favorite products.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of favorite products.
     */
    @GetMapping("/products/favorite-products")
    public List<Product> getFavoriteProductsForCurrentUser() {
        log.debug("REST request to get all Favorite Products for user {}", SecurityUtils.getCurrentUserLogin().orElseThrow());
        return productService.findAllFavorite(SecurityUtils.getCurrentUserLogin().orElseThrow());
    }

    /**
     * {@code POST  /favorite-products} : get the current authenticated user's favorite products after modifications.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of favorite products.
     */
    @PostMapping("/products/favorite-products/{id}")
    public List<Product> updateFavoriteProductsForCurrentUser(@PathVariable Long id) {
        String login = SecurityUtils.getCurrentUserLogin().orElseThrow();
        log.debug("REST request to update product {} in Favorite Products for user {}", id, login);
        //Get Product
        Product product = productService.findOne(id).orElseThrow();
        //get userDetails
        UserDetails user = productRepository.getUserDetails(login);
        //if favorite contains product we remove it, else we add it to Favorites
        if (user.getFavorites().contains(product)) {
            user.getFavorites().remove(product);
        } else {
            user.getFavorites().add(product);
        }
        //Sauvegarde
        user = userDetailsService.save(user);
        return new ArrayList<>(user.getFavorites());
    }
}
