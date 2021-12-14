package com.m2gi.ecom.web.rest;

import com.m2gi.ecom.domain.Cart;
import com.m2gi.ecom.domain.ProductCart;
import com.m2gi.ecom.domain.User;
import com.m2gi.ecom.repository.CartRepository;
import com.m2gi.ecom.repository.ProductCartRepository;
import com.m2gi.ecom.repository.UserRepository;
import com.m2gi.ecom.security.SecurityUtils;
import com.m2gi.ecom.service.CartService;
import com.m2gi.ecom.service.ProductService;
import com.m2gi.ecom.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.m2gi.ecom.domain.Cart}.
 */
@RestController
@RequestMapping("/api")
public class CartResource {

    private final Logger log = LoggerFactory.getLogger(CartResource.class);

    private static final String ENTITY_NAME = "cart";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CartService cartService;

    private final CartRepository cartRepository;

    private final ProductCartRepository productCartRepository;

    private final UserRepository userRepo;

    private final ProductService productService;

    public CartResource(
        ProductService productService,
        UserRepository userRepo,
        ProductCartRepository productCartRepository,
        CartService cartService,
        CartRepository cartRepository
    ) {
        this.productService = productService;
        this.userRepo = userRepo;
        this.productCartRepository = productCartRepository;
        this.cartService = cartService;
        this.cartRepository = cartRepository;
    }

    /**
     * {@code POST  /carts} : Create a new cart.
     *
     * @param cart the cart to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new cart, or with status {@code 400 (Bad Request)} if the cart has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/carts")
    public ResponseEntity<Cart> createCart(@RequestBody Cart cart) throws URISyntaxException {
        log.debug("REST request to save Cart : {}", cart);
        if (cart.getId() != null) {
            throw new BadRequestAlertException("A new cart cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Cart result = cartService.save(cart);
        return ResponseEntity
            .created(new URI("/api/carts/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /carts/:id} : Updates an existing cart.
     *
     * @param id the id of the cart to save.
     * @param cart the cart to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated cart,
     * or with status {@code 400 (Bad Request)} if the cart is not valid,
     * or with status {@code 500 (Internal Server Error)} if the cart couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/carts/{id}")
    public ResponseEntity<Cart> updateCart(@PathVariable(value = "id", required = false) final Long id, @RequestBody Cart cart)
        throws URISyntaxException {
        log.debug("REST request to update Cart : {}, {}", id, cart);
        if (cart.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, cart.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!cartRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Cart result = cartService.save(cart);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, cart.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /carts/:id} : Partial updates given fields of an existing cart, field will ignore if it is null
     *
     * @param id the id of the cart to save.
     * @param cart the cart to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated cart,
     * or with status {@code 400 (Bad Request)} if the cart is not valid,
     * or with status {@code 404 (Not Found)} if the cart is not found,
     * or with status {@code 500 (Internal Server Error)} if the cart couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/carts/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Cart> partialUpdateCart(@PathVariable(value = "id", required = false) final Long id, @RequestBody Cart cart)
        throws URISyntaxException {
        log.debug("REST request to partial update Cart partially : {}, {}", id, cart);
        if (cart.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, cart.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!cartRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Cart> result = cartService.partialUpdate(cart);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, cart.getId().toString())
        );
    }

    /**
     * {@code GET  /carts} : get all the carts.
     *
     * @param filter the filter of the request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of carts in body.
     */
    @GetMapping("/carts")
    public List<Cart> getAllCarts(@RequestParam(required = false) String filter) {
        if ("user-is-null".equals(filter)) {
            log.debug("REST request to get all Carts where user is null");
            return cartService.findAllWhereUserIsNull();
        }
        log.debug("REST request to get all Carts");
        return cartService.findAll();
    }

    /**
     * {@code GET  /carts/:id} : get the "id" cart.
     *
     * @param id the id of the cart to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the cart, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/carts/{id}")
    public ResponseEntity<Cart> getCart(@PathVariable Long id) {
        log.debug("REST request to get Cart : {}", id);
        Optional<Cart> cart = cartService.findOne(id);
        return ResponseUtil.wrapOrNotFound(cart);
    }

    /**
     * {@code DELETE  /carts/:id} : delete the "id" cart.
     *
     * @param id the id of the cart to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/carts/{id}")
    public ResponseEntity<Void> deleteCart(@PathVariable Long id) {
        log.debug("REST request to delete Cart : {}", id);
        cartService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code GET  /cart} : get the current authenticated user's cart.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the cart.
     */
    @GetMapping("/cart")
    public ResponseEntity<Cart> getCartForCurrentUser() {
        log.debug("REST request to get all ProductCarts");
        Optional<Cart> cart = cartService.findOneWithEagerRelationshipsByLogin(SecurityUtils.getCurrentUserLogin().get());
        return ResponseUtil.wrapOrNotFound(cart);
    }

    //    /**
    //     * {@code PATCH  /cart/product/:id} : Update productCart with product "id"
    //     *
    //     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the cart.
    //     */
    //    @PatchMapping("/cart/product/{id}")
    //    public ResponseEntity<ProductCart> updateQuantityProduct(
    //        @PathVariable(value = "id") final Long idProduct,
    //        @RequestParam(value = "quantity") final int quantity
    //    ) throws URISyntaxException {
    //        log.debug("REST request to update quatity of ProductCarts by id of product");
    //        final User user = userRepo.findOneByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
    //        final ProductCart result;
    //        Cart userCart = user.getDetails().getCart();
    //        for (ProductCart lineProduct : userCart.getLines()) {
    //            if (lineProduct.getProduct().getId() == idProduct) {
    //                lineProduct.setQuantity(quantity);
    //                result = productCartService.save(lineProduct);
    //                return ResponseEntity.created(new URI("/api/product-carts/" + result.getId())).body(result);
    //            }
    //        }
    //        throw new BadRequestAlertException("Erreure survenu lors de l'update de la quantite", ENTITY_NAME, "idnotfound");
    //    }

    /**
     * {@code POST  /cart/product/:id} : Create a new productCart with product "id".
     *
     * @param idProduct the id of product that should create a productCart.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new productCart, or with status {@code 400 (Bad Request)} if the productCart has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/cart/products/{id}")
    public ResponseEntity<ProductCart> createProductCart(@PathVariable(value = "id") final Long idProduct) throws URISyntaxException {
        log.debug("REST request to add product to a productCart to Cart : {}", idProduct);

        final User user = userRepo.findOneByLogin(SecurityUtils.getCurrentUserLogin().orElseThrow()).orElseThrow();
        final ProductCart productCart = new ProductCart();
        productCart
            .cart(user.getDetails().getCart())
            .product(productService.findOne(idProduct).orElseThrow())
            .quantity(1)
            .creationDatetime(Instant.now());

        final ProductCart result = cartService.addLine(productCart);

        return ResponseEntity.created(new URI("/api/product-carts/" + result.getId())).body(result);
    }

    /**
     * {@code PATCH  /cart/productCart/:id} : Update productCart with ProductCart "id"
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the cart.
     */
    @PatchMapping("/cart/products/{id}")
    public ResponseEntity<ProductCart> updateQuantityProductCart(
        @PathVariable(value = "id") final Long idProductCart,
        @RequestParam(value = "quantity") final int quantity
    ) throws URISyntaxException {
        log.debug("REST request to update quantity of ProductCarts by id of ProductCart");
        if (!productCartRepository.existsById(idProductCart)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }
        final ProductCart result = cartService.updateLine(idProductCart, quantity);
        return ResponseEntity.created(new URI("/api/product-carts/" + result.getId())).body(result);
    }

    /**
     * {@code DELETE  /product-carts/:id} : delete the "id" productCart.
     *
     * @param id the id of the productCart to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/cart/products/{id}")
    public ResponseEntity<Void> deleteProductCart(@PathVariable Long id) {
        log.debug("REST request to delete ProductCart : {}", id);
        cartService.removeLine(id);
        return ResponseEntity.noContent().build();
    }
}
