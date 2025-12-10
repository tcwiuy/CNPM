import { Component } from '@angular/core';
import { RestaurantService } from '../../service/restaurant.service';
import { Restaurant } from '../../dto/Restaurant';
import { Router } from '@angular/router';
import { Page } from '../../dto/Page';

@Component({
  selector: 'app-list-restaurant',
  templateUrl: './list-restaurant.component.html',
  styleUrl: './list-restaurant.component.css'
})
export class ListRestaurantComponent {
  restaurants: Restaurant[] = [];
  keyword = '';

  currentPage = 0;
  pageSize = 8;
  totalPages = 0;

  constructor(
    private restaurantService: RestaurantService,
    private router: Router) { }

  ngOnInit(): void {
    this.loadRestaurants();
  }

  loadRestaurants(): void {
    this.restaurantService.getAllRestaurants(this.currentPage, this.pageSize, this.keyword)
      .subscribe((response: Page<Restaurant>) => {
        console.log('API Response:', response); // Log dữ liệu trả về từ API
        this.restaurants = response.content;
        this.totalPages = response.totalPages;
      });
  }

  goToRestaurantDetail(restaurantId: number): void {
    this.router.navigate(['/restaurant', restaurantId]);
  }

  // Pagination
  goToPage(pageNumber: number): void {
    if (pageNumber >= 0 && pageNumber < this.totalPages) {
      this.currentPage = pageNumber;
      this.loadRestaurants();
    }
  }

  nextPage(): void {
    if (this.currentPage < this.totalPages - 1) {
      this.currentPage++;
      this.loadRestaurants();
    }
  }

  previousPage(): void {
    if (this.currentPage > 0) {
      this.currentPage--;
      this.loadRestaurants();
    }
  }

  isFirstPage(): boolean {
    return this.currentPage === 0;
  }

  isLastPage(): boolean {
    return this.currentPage === this.totalPages - 1;
  }

  // Server-Side Search
  onSearch(): void {
    this.currentPage = 0;  // Reset to the first page when searching
    this.loadRestaurants();
  }

  clearSearch(): void {
    this.keyword = '';
    this.currentPage = 0;
    this.loadRestaurants();
  }
}
