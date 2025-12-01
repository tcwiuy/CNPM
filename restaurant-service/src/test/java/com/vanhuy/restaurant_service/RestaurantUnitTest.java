package com.vanhuy.restaurant_service;

import com.vanhuy.restaurant_service.model.MenuItem;
import com.vanhuy.restaurant_service.model.Restaurant;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class RestaurantUnitTest {

    @Test
    void restaurantBuilderSetsFields() {
        Restaurant r = Restaurant.builder()
                .restaurantId(1)
                .name("Pho Place")
                .address("123 Hanoi")
                .image("img.png")
                .build();

        assertEquals(1, r.getRestaurantId());
        assertEquals("Pho Place", r.getName());
        assertEquals("123 Hanoi", r.getAddress());
        assertEquals("img.png", r.getImage());
    }

    @Test
    void menuItemPriceAndRestaurantLink() {
        Restaurant r = Restaurant.builder()
                .restaurantId(2)
                .name("Bun House")
                .address("456 Street")
                .build();

        MenuItem m = MenuItem.builder()
                .itemId(10)
                .name("Bun cha")
                .price(new BigDecimal("4.50"))
                .stock(5)
                .imageUrl("bun.jpg")
                .restaurant(r)
                .build();

        assertEquals(0, new BigDecimal("4.50").compareTo(m.getPrice()));
        assertEquals("Bun cha", m.getName());
        assertSame(r, m.getRestaurant());

        BigDecimal total = m.getPrice().multiply(new BigDecimal(m.getStock()));
        assertEquals(0, new BigDecimal("22.50").compareTo(total));
    }
}
