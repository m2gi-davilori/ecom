package com.m2gi.ecom.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

/**
 * A Cart.
 */
@Entity
@Table(name = "cart")
public class Cart implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @OneToMany(mappedBy = "cart", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties(value = { "cart" }, allowSetters = true)
    private Set<ProductCart> lines = new HashSet<>();

    @JsonIgnoreProperties(value = { "address", "user", "cart", "orders", "favorites", "preferences" }, allowSetters = true)
    @OneToOne(mappedBy = "cart")
    private UserDetails user;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Cart id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<ProductCart> getLines() {
        return this.lines;
    }

    public void setLines(Set<ProductCart> productCarts) {
        if (this.lines != null) {
            this.lines.forEach(i -> i.setCart(null));
        }
        if (productCarts != null) {
            productCarts.forEach(i -> i.setCart(this));
        }
        this.lines = productCarts;
    }

    public Cart lines(Set<ProductCart> productCarts) {
        this.setLines(productCarts);
        return this;
    }

    public Cart addLines(ProductCart productCart) {
        this.lines.add(productCart);
        productCart.setCart(this);
        return this;
    }

    public Cart removeLines(ProductCart productCart) {
        this.lines.remove(productCart);
        productCart.setCart(null);
        return this;
    }

    public UserDetails getUser() {
        return this.user;
    }

    public void setUser(UserDetails userDetails) {
        if (this.user != null) {
            this.user.setCart(null);
        }
        if (userDetails != null) {
            userDetails.setCart(this);
        }
        this.user = userDetails;
    }

    public Cart user(UserDetails userDetails) {
        this.setUser(userDetails);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Cart)) {
            return false;
        }
        return id != null && id.equals(((Cart) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Cart{" +
            "id=" + getId() +
            "}";
    }
}
