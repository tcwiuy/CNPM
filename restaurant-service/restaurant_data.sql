USE restaurant_db;
DELETE FROM menu_items;
DELETE FROM restaurants;
INSERT INTO restaurants (address, image, name)
VALUES (
        '123 Le Loi, District 1, Ho Chi Minh City',
        'restaurant1.jpg',
        'Huong Viet Restaurant'
    ),
    (
        '45 Tran Phu, District 5, Ho Chi Minh City',
        'restaurant2.jpg',
        'BBQ King'
    ),
    (
        '200 Nguyen Hue, District 1, Ho Chi Minh City',
        'restaurant3.jpg',
        'Pizza House'
    ),
    (
        '89 Hai Ba Trung, District 3, Ho Chi Minh City',
        'restaurant4.jpg',
        'Sushi World'
    );
INSERT INTO menu_items (image_url, name, price, stock, restaurant_id)
VALUES ('pho.jpg', 'Beef Pho', 45000, 50, 1),
    (
        'buncha.jpg',
        'Hanoi Grilled Pork Noodles',
        40000,
        40,
        1
    ),
    ('bbq_pork.jpg', 'BBQ Pork Ribs', 120000, 30, 2),
    (
        'bbq_beef.jpg',
        'Grilled Beef Steak',
        180000,
        20,
        2
    ),
    (
        'pizza_pepperoni.jpg',
        'Pepperoni Pizza',
        150000,
        25,
        3
    ),
    (
        'pizza_hawaii.jpg',
        'Hawaiian Pizza',
        160000,
        20,
        3
    ),
    ('sushi_sake.jpg', 'Salmon Sushi', 90000, 40, 4),
    ('sushi_maguro.jpg', 'Tuna Sushi', 95000, 35, 4);