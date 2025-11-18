package com.vanhuy.notification_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles; // Import cần thiết

@SpringBootTest
@ActiveProfiles("test-ci") // <-- THÊM DÒNG NÀY
class NotificationServiceApplicationTests {

    @Test
    void contextLoads() {
    }
}