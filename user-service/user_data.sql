USE user_db;

INSERT INTO users (address, email, is_active, password, profile_image_name, username) VALUES
('123 Lê Lợi, Q.1, TP.HCM', 'nguyen.vy@example.com', b'1', 'password1', 'profile1.jpg', 'nguyenvy'),
('45 Trần Phú, Q.5, TP.HCM', 'tran.hieu@example.com', b'1', 'password2', 'profile2.jpg', 'tranhieu'),
('200 Nguyễn Huệ, Q.1, TP.HCM', 'pham.lan@example.com', b'0', 'password3', 'profile3.jpg', 'phamlan');

INSERT INTO user_roles (user_id, role) VALUES
(1, 'ROLE_USER'),
(1, 'ROLE_ADMIN'),
(2, 'ROLE_USER'),
(3, 'ROLE_USER');

INSERT INTO password_reset_token (expiry_date, token, used, user_id) VALUES
(NOW() + INTERVAL 1 DAY, 'token123', b'0', 1),
(NOW() + INTERVAL 1 DAY, 'token456', b'0', 2),
(NOW() + INTERVAL 1 DAY, 'token789', b'1', 3);
