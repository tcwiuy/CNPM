package com.vanhuy.order_service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OrderUnitTest {

    @Test
    @DisplayName("Simple arithmetic sanity check")
    void simpleArithmetic() {
        assertEquals(4, 2 + 2, "Basic arithmetic should hold");
    }

    @Test
    @DisplayName("List contains expected element")
    void listContainsElement() {
        var items = List.of("apple", "banana", "cherry");
        assertTrue(items.contains("banana"), "List should contain 'banana'");
    }

}
