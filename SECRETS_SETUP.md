# Hướng dẫn checklist: Thiết lập Secrets cho CI/CD

Mục tiêu: thêm các secret cần thiết vào repository để GitHub Actions có thể build, push image và deploy qua SSH.

---

## Danh sách secret cần thêm (tên và mục đích)
- `DOCKER_USERNAME` / `DOCKER_PASSWORD` — credentials Docker Hub (nếu dùng Docker Hub để push images).
- `GHCR_PAT` — Personal Access Token (GitHub) nếu bạn muốn push private images lên GHCR (scope: `write:packages`, `repo` nếu cần).
- `DEPLOY_HOST` — IP hoặc hostname của server deploy.
- `DEPLOY_USER` — username SSH trên server (ví dụ `deploy` hoặc `ubuntu`).
- `DEPLOY_SSH_KEY` — private SSH key (PEM) dùng để SSH không cần password.
- `DEPLOY_PATH` — đường dẫn chứa `docker-compose.yml` trên server (ví dụ `/home/deploy/apps/cnpm_food-ordering`).
- `DEPLOY_PORT` — (tùy chọn) SSH port, mặc định `22`.
- `DEPLOY_REG_USERNAME` / `DEPLOY_REG_PASSWORD` — (tùy chọn) credentials để server login vào registry (GHCR/Docker Hub) trước khi `docker-compose pull`.

---

## A. Thêm secrets bằng GitHub Web UI (dễ nhất)

1. Mở repository trên GitHub.
2. Vào `Settings` → `Secrets and variables` → `Actions` → `New repository secret`.
3. Nhập `Name` và `Secret` (giá trị), click `Add secret`.
4. Lặp cho toàn bộ danh sách ở trên.

Ghi chú:
- `DEPLOY_SSH_KEY` đặt toàn bộ private key (bắt đầu bằng `-----BEGIN OPENSSH PRIVATE KEY-----` hoặc `-----BEGIN RSA PRIVATE KEY-----`).
- Không chia sẻ secrets ra nơi công cộng.

---

## B. Thêm secrets bằng GitHub CLI (`gh`) — PowerShell (Windows)

1. Cài `gh` nếu chưa có (sử dụng `winget`):

```powershell
winget install --id GitHub.cli
gh auth login
```

2. Thêm secrets cơ bản (ví dụ):

```powershell
gh secret set DEPLOY_HOST --body "your.server.ip.or.host" --repo tcwiuy/CNPM
gh secret set DEPLOY_USER --body "deploy" --repo tcwiuy/CNPM
gh secret set DEPLOY_PATH --body "/home/deploy/apps/cnpm_food-ordering" --repo tcwiuy/CNPM
gh secret set DEPLOY_PORT --body "22" --repo tcwiuy/CNPM

gh secret set DOCKER_USERNAME --body "your_dockerhub_username" --repo tcwiuy/CNPM
gh secret set DOCKER_PASSWORD --body "your_dockerhub_password_or_token" --repo tcwiuy/CNPM

gh secret set GHCR_PAT --body "ghp_xxx..." --repo tcwiuy/CNPM
```

3. Thêm SSH private key an toàn (nếu file ở `%USERPROFILE%\.ssh\cnpm_deploy`):

```powershell
$priv = Get-Content -Raw "$env:USERPROFILE\.ssh\cnpm_deploy"
gh secret set DEPLOY_SSH_KEY --body "$priv" --repo tcwiuy/CNPM
```

Lưu ý: thay `tcwiuy/CNPM` bằng `<owner>/<repo>` nếu khác.

---

## C. Tạo SSH key mới (nếu cần) và cài public key lên server

1. Tạo key trên Windows (PowerShell):

```powershell
ssh-keygen -t ed25519 -C "deploy@cnpm" -f "$env:USERPROFILE\.ssh\cnpm_deploy" -N ""
```

2. Lấy public key và thêm vào server `~/.ssh/authorized_keys` của `DEPLOY_USER`:

```powershell
type $env:USERPROFILE\.ssh\cnpm_deploy.pub
# Copy nội dung và paste vào server: ~/.ssh/authorized_keys (hoặc dùng ssh-copy-id nếu có)
```

Hoặc dùng `ssh-copy-id` (nếu có):

```powershell
ssh-copy-id -i $env:USERPROFILE\.ssh\cnpm_deploy.pub deploy@your.server.ip
```

3. Thêm private key vào secrets (xem phần B).

---

## D. Tạo GitHub Personal Access Token cho GHCR (nếu cần)

1. Trên GitHub (tài khoản của bạn): `Settings` → `Developer settings` → `Personal access tokens` → `Tokens (classic)` hoặc `Fine-grained tokens`.
2. Tạo token mới với scope ít nhất `write:packages`. Nếu image private và repo private, cấp thêm `repo` permission.
3. Sao chép token và lưu vào secret `GHCR_PAT` trong repository.

---

## E. Cho phép Actions có quyền `packages: write` (repo setting)

1. Vào repository → `Settings` → `Actions` → `General` → `Workflow permissions`.
2. Chọn **Read and write permissions** cho workflows.

Ghi chú: Nếu bạn muốn dùng `GITHUB_TOKEN` để push lên GHCR, cần đảm bảo setting này được bật. Ngoài ra một số tổ chức cần PAT riêng.

---

## F. Kiểm tra và test

- Kiểm tra các secret đã tồn tại:

```powershell
gh secret list --repo tcwiuy/CNPM
```

- Thử trigger workflow bằng cách push một commit lên `main` hoặc mở Pull Request.
- Kiểm tra job `deploy` trong Actions tab để xem logs SSH và các lệnh `docker-compose` đã chạy thành công.

---

## G. (Tùy chọn) Đăng nhập registry trên server trong deploy script

Nếu images ở registry private (GHCR hoặc Docker Hub), bạn cần cho server có credential để pull images.

- Tạo secrets `DEPLOY_REG_USERNAME` và `DEPLOY_REG_PASSWORD` trên repo.
- Trong job `deploy`, thêm vào script trước `docker-compose pull` đoạn:

```sh
echo "$DEPLOY_REG_PASSWORD" | docker login ghcr.io -u "$DEPLOY_REG_USERNAME" --password-stdin
# hoặc docker login -u "$DEPLOY_REG_USERNAME" -p "$DEPLOY_REG_PASSWORD"
```

Bạn có thể thêm biến môi trường secrets vào `appleboy/ssh-action` bằng cách tạo các biến môi trường trên Actions step hoặc dùng interpolation trực tiếp trong `script`.

---

## H. Khuyến nghị bảo mật

- Không đặt secrets trong mã nguồn hoặc bình luận công khai.
- Sử dụng key với passphrase (nhưng trong CI thường dùng key không passphrase để SSH tự động — cân nhắc trade-off và sử dụng jump host/agent nếu cần).
- Hạn chế scope của PAT (chỉ cấp `write:packages` nếu chỉ cần push image).

---

Nếu bạn muốn, tôi có thể tự động chạy các lệnh `gh secret set ...` cho bạn (tôi sẽ hiển thị lệnh để bạn copy và chạy trên PowerShell), hoặc tạo file checklist dưới dạng PR. Bạn muốn tôi tiếp theo làm gì?
