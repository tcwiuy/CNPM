package com.vanhuy.restaurant_service.controller;

import com.vanhuy.restaurant_service.dto.MenuItemDTO;
import com.vanhuy.restaurant_service.exception.ResourceNotFoundException;
import com.vanhuy.restaurant_service.model.Restaurant;
import com.vanhuy.restaurant_service.service.FileStorageService;
import com.vanhuy.restaurant_service.service.MenuItemService;
import com.vanhuy.restaurant_service.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/menu-items")
public class MenuItemController {
    private final MenuItemService menuItemService;
    private final RestaurantService restaurantService;
    private final FileStorageService imageService;

    @CrossOrigin(origins = "*") // Thêm dòng này để cho phép gọi từ trình duyệt
    @PostMapping("/{restaurantId}")
    public ResponseEntity<MenuItemDTO> createMenuItem(
            @PathVariable Integer restaurantId,
            @RequestBody MenuItemDTO menuItemDTO) {
        MenuItemDTO createdMenuItem = menuItemService.createMenuItem(menuItemDTO, restaurantId);
        return ResponseEntity.ok(createdMenuItem);
    }

    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<List<MenuItemDTO>> getMenuItemsByRestaurantId(@PathVariable Integer restaurantId) {
        Restaurant restaurant = restaurantService.getRestaurantById(restaurantId);
        if (restaurant == null) {
            return ResponseEntity.notFound().build();
        }
        List<MenuItemDTO> menuItems = menuItemService.getMenuItemsByRestaurantId(restaurant);
        return ResponseEntity.ok(menuItems);
    }

    @PostMapping(value = "/{menuItemId}/upload-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MenuItemDTO> uploadImage(
            @PathVariable Integer menuItemId,
            @RequestParam("file") MultipartFile file) {
        try {
            MenuItemDTO menuItemDTO = menuItemService.uploadImage(menuItemId, file);
            return ResponseEntity.ok(menuItemDTO);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IOException e) {
            // log.error("Error uploading image for menu item {}", menuItemId, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/images/{filename:.+}")
    public ResponseEntity<Resource> getImage(@PathVariable String filename) {
        Resource resource = imageService.getImage(filename);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .header(HttpHeaders.CACHE_CONTROL, "max-age=31536000") // Cache for 1 year
                .body(resource);
    }

    // get price by menu item id
    @GetMapping("/{menuItemId}")
    public ResponseEntity<BigDecimal> getPriceByMenuItemId(@PathVariable Integer menuItemId) {
        BigDecimal price = menuItemService.getPriceByMenuItemId(menuItemId);
        return ResponseEntity.ok(price);
    }

}