package com.m2gi.ecom.repository;

import com.m2gi.ecom.domain.Cart;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Cart entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    @Query("select cart from Cart cart left join fetch cart.lines where cart.user.user.login =:login")
    Optional<Cart> findOneWithEagerRelationshipsByLogin(@Param("login") String login);

    @Modifying
    @Query("delete from ProductCart p where p.cart = :cart")
    void empty(@Param("cart") Cart cart);
}
