package com.vanhuy.order_service.client;

import com.vanhuy.order_service.dto.OrderResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "notification-service",)
public interface NotificationClient {
    @PostMapping("/api/v1/notifications/order")
    String sendOrderNotification(@RequestBody OrderResponse orderResponse);
}
