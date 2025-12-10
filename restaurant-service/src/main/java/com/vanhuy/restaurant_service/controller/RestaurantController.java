package com.vanhuy.restaurant_service.controller;

import com.vanhuy.restaurant_service.dto.RestaurantDTO;
import com.vanhuy.restaurant_service.exception.RestaurantNotFoundException;
import com.vanhuy.restaurant_service.service.FileStorageService;
import com.vanhuy.restaurant_service.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/restaurants")
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantService restaurantService;
    private final FileStorageService imageService;

    @PostMapping
    public ResponseEntity<RestaurantDTO> createRestaurant(@RequestBody RestaurantDTO restaurantDTO) {
        RestaurantDTO createdRestaurant = restaurantService.createRestaurant(restaurantDTO);
        return new ResponseEntity<>(createdRestaurant, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Page<RestaurantDTO>> getAllRestaurants(@RequestParam int page,
            @RequestParam int size,
            @RequestParam(required = false) String keyword) {
        Pageable pageable = PageRequest.of(page, size);
        if (keyword != null && !keyword.isEmpty()) {
            return ResponseEntity.ok(restaurantService.searchRestaurants(keyword, pageable));
        }
        return ResponseEntity.ok(restaurantService.getRestaurantsByPage(pageable));
    }

    @GetMapping("/images/{filename:.+}")
    public ResponseEntity<Resource> getImage(@PathVariable String filename) {
        Resource resource = imageService.getImage(filename);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG) // Bạn có thể cần xem xét các kiểu ảnh khác như PNG
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .header(HttpHeaders.CACHE_CONTROL, "max-age=31536000")
                .body(resource);
    }

    @PostMapping(value = "/{restaurantId}/upload-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<RestaurantDTO> uploadImage(
            @PathVariable Integer restaurantId,
            @RequestParam("file") MultipartFile file) {
        try {
            RestaurantDTO updatedRestaurant = restaurantService.uploadImage(restaurantId, file);
            return ResponseEntity.ok(updatedRestaurant);
        } catch (RestaurantNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{restaurantId}")
    public ResponseEntity<Void> deleteRestaurant(@PathVariable Integer restaurantId) {
        try {
            restaurantService.deleteRestaurant(restaurantId);
            return ResponseEntity.noContent().build(); // 204
        } catch (RestaurantNotFoundException e) {
            return ResponseEntity.notFound().build(); // 404
        }
    }

}