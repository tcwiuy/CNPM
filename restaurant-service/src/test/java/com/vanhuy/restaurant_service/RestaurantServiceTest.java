package com.vanhuy.restaurant_service.service;

import com.vanhuy.restaurant_service.exception.RestaurantNotFoundException;
import com.vanhuy.restaurant_service.model.Restaurant;
import com.vanhuy.restaurant_service.repository.RestaurantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class RestaurantServiceTest {

    // Tạo mock cho repository
    @Mock
    private RestaurantRepository restaurantRepository;

    // Mock cho dependency FileStorageService
    @Mock
    private FileStorageService fileStorageService; 

    // Inject các mock vào đối tượng cần kiểm tra
    @InjectMocks
    private RestaurantService restaurantService;

    @BeforeEach
    public void setUp() {
        // Khởi tạo các mock và inject vào service
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getRestaurantById_ShouldThrowNotFound_WhenIdDoesNotExist() {
        // Arrange: Chuẩn bị dữ liệu
        Integer nonExistentId = 99;
        // Giả lập repository trả về Optional.empty() khi tìm kiếm
        when(restaurantRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert: Thực thi và kiểm tra
        // Đảm bảo rằng phương thức ném ra RestaurantNotFoundException
        assertThrows(RestaurantNotFoundException.class, () -> {
            restaurantService.getRestaurantById(nonExistentId);
        }, "Nên ném ra RestaurantNotFoundException khi ID không tồn tại.");
    }

    @Test
    void getRestaurantById_ShouldReturnRestaurant_WhenIdExists() {
        // Arrange
        Integer existingId = 1;
        Restaurant mockRestaurant = Restaurant.builder()
                .restaurantId(existingId)
                .name("Test Restaurant")
                .address("Test Address")
                .build();
        // Giả lập repository trả về đối tượng Restaurant
        when(restaurantRepository.findById(existingId)).thenReturn(Optional.of(mockRestaurant));

        // Act
        Restaurant result = restaurantService.getRestaurantById(existingId);

        // Assert
        assertEquals(existingId, result.getRestaurantId(), "ID trả về nên khớp với ID mong muốn.");
        assertEquals("Test Restaurant", result.getName(), "Tên trả về nên khớp với tên mong muốn.");
    }
}