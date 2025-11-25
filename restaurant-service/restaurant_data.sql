USE restaurant_db;

INSERT INTO restaurants (address, image, name) VALUES
('123 Lê Lợi, Q.1, TP.HCM', 'restaurant1.jpg', 'Nhà hàng Hương Việt'),
('45 Trần Phú, Q.5, TP.HCM', 'restaurant2.jpg', 'BBQ King'),
('200 Nguyễn Huệ, Q.1, TP.HCM', 'restaurant3.jpg', 'Pizza House'),
('89 Hai Bà Trưng, Q.3, TP.HCM', 'restaurant4.jpg', 'Sushi World');

INSERT INTO menu_items (image_url, name, price, stock, restaurant_id) VALUES
('pho.jpg', 'Phở bò tái', 45000, 50, 1),
('buncha.jpg', 'Bún chả Hà Nội', 40000, 40, 1),
('bbq_pork.jpg', 'Sườn nướng BBQ', 120000, 30, 2),
('bbq_beef.jpg', 'Bò nướng tảng', 180000, 20, 2),
('pizza_pepperoni.jpg', 'Pizza Pepperoni', 150000, 25, 3),
('pizza_hawaii.jpg', 'Pizza Hawaii', 160000, 20, 3),
('sushi_sake.jpg', 'Sushi cá hồi', 90000, 40, 4),
('sushi_maguro.jpg', 'Sushi cá ngừ', 95000, 35, 4);

