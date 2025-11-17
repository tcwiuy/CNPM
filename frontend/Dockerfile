# --- Giai đoạn 1: Build Angular ---
FROM node:18-alpine as build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
# File angular.json build ra "dist/frontend"
RUN npm run build

# --- Giai đoạn 2: Chạy Nginx ---
FROM nginx:alpine

# SỬA 1: Copy từ thư mục build đúng của Angular 17 (dist/frontend/browser)
COPY --from=build /app/dist/frontend/browser/ /usr/share/nginx/html/

# SỬA 2: SỬA LỖI ĐƯỜNG DẪN
# (Đã xóa "./frontend/" ở đầu vì Docker build context đã là /frontend rồi)
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80