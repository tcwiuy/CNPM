# --- Giai đoạn 1: Build Angular ---
FROM node:18-alpine as build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

# --- Giai đoạn 2: Chạy Nginx ---
FROM nginx:alpine
# Copy toàn bộ nội dung trong thư mục con đầu tiên tìm thấy trong dist/
# Vì Angular thường build ra dist/tên-project/, lệnh này sẽ lấy đúng nội dung web
COPY --from=build /app/dist/*/ /usr/share/nginx/html/

EXPOSE 80