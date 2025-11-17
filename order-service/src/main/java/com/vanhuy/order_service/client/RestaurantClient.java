package com.vanhuy.order_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;

// SỬA Ở ĐÂY: Đã xóa "url = http://localhost:8082"
@FeignClient(name = "restaurant-service")
public interface RestaurantClient {
    @GetMapping("/api/v1/menu-items/{menuItemId}")
    BigDecimal getPriceByMenuItemId(@PathVariable Integer menuItemId);
}