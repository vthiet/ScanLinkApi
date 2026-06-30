# ScanLink API — Hướng dẫn Deploy Production

## Kiến trúc

```
Internet
  │
  ├─ :80  (HTTP)  ─→ Nginx (Docker)  ─→ redirect 301 sang HTTPS
  └─ :443 (HTTPS) ─→ Nginx (Docker)  ─→ proxy ─→ Spring Boot Tomcat :8080 (Docker, internal)
```

- **Nginx**: container `scanlink-nginx`, đón cổng 80 + 443 từ host
- **Spring Boot (Tomcat embedded)**: container `scanlink-api`, cổng 8080 chỉ nội bộ Docker network, **không expose ra ngoài**

---

## Yêu cầu trên server

- Docker Engine ≥ 24 (có `docker compose` plugin)
- Git
- openssl (có sẵn trên hầu hết distro)
- Cổng 80 và 443 mở trên firewall

---

## Lần đầu deploy

### Bước 1: Upload file nhạy cảm lên server

```bash
# Từ máy local, copy lên server (ví dụ IP: 1.2.3.4)
scp .env                                  user@1.2.3.4:~/deploy/.env
scp scanlink-firebase-service-account.json user@1.2.3.4:~/deploy/scanlink-firebase-service-account.json
```

### Bước 2: SSH vào server và setup SSL

```bash
ssh user@1.2.3.4

# Tạo self-signed cert (HTTP + HTTPS hoạt động ngay, browser sẽ cảnh báo "Not Secure")
# Dùng khi chưa có domain, hoặc chỉ cần IP
bash ~/deploy/ScanLinkApi/scripts/setup-ssl.sh

# --- HOẶC ---

# Dùng Let's Encrypt (cần domain trỏ về server, port 80 mở)
bash ~/deploy/ScanLinkApi/scripts/setup-ssl.sh --certbot --domain=api.yourdomain.com
```

> Cert được tạo tại `~/deploy/ssl/server.crt` và `~/deploy/ssl/server.key`

### Bước 3: Chạy deploy

```bash
# Cách 1: dùng biến mặc định (file .env và firebase.json ở ~/deploy/)
bash ~/deploy/ScanLinkApi/scripts/deploy.sh

# Cách 2: chỉ định đường dẫn tùy ý
ENV_FILE=/custom/path/.env \
FIREBASE_FILE=/custom/path/firebase.json \
bash ~/deploy/ScanLinkApi/scripts/deploy.sh

# Cách 3: override branch
BRANCH=main bash ~/deploy/ScanLinkApi/scripts/deploy.sh
```

**Lần đầu**: script sẽ tự `git clone` repo vào `~/deploy/ScanLinkApi/`

**Lần sau**: script sẽ `git pull` và rebuild Docker image

---

## Deploy lại (update code)

```bash
bash ~/deploy/ScanLinkApi/scripts/deploy.sh
```

Script sẽ tự động:
1. Pull code mới nhất từ branch
2. Sync `.env` + Firebase JSON vào project
3. `docker compose down` → `docker compose up -d --build`
4. Chờ health check
5. Báo kết quả

---

## File cần chuẩn bị (không commit vào git)

| File | Vị trí mặc định trên server | Mô tả |
|------|------------------------------|-------|
| `.env` | `~/deploy/.env` | Biến môi trường (MongoDB, Cloudinary, ...) |
| Firebase JSON | `~/deploy/scanlink-firebase-service-account.json` | Firebase service account |
| SSL cert | `~/deploy/ssl/server.crt` | TLS certificate (tạo bởi setup-ssl.sh) |
| SSL key | `~/deploy/ssl/server.key` | TLS private key |

---

## Cấu trúc thư mục trên server sau deploy

```
~/deploy/
├── .env                                    ← tự tạo, không commit
├── scanlink-firebase-service-account.json  ← tự upload, không commit
├── ssl/
│   ├── server.crt                          ← tạo bởi setup-ssl.sh
│   └── server.key
└── ScanLinkApi/                            ← git clone tự động
    ├── docker-compose.yml
    ├── Dockerfile
    ├── nginx/default.conf
    ├── scripts/
    │   ├── deploy.sh
    │   └── setup-ssl.sh
    └── src/
```

---

## Biến môi trường của script

| Biến | Mặc định | Mô tả |
|------|----------|-------|
| `ENV_FILE` | `~/deploy/.env` | Đường dẫn tới file .env |
| `FIREBASE_FILE` | `~/deploy/scanlink-firebase-service-account.json` | Firebase JSON |
| `DEPLOY_BASE` | `~/deploy` | Thư mục deploy gốc |
| `BRANCH` | `feat/auth` | Git branch để deploy |
| `SSL_DIR` | đọc từ .env hoặc `~/deploy/ssl` | Thư mục chứa SSL cert |

---

## Kiểm tra sau deploy

```bash
# Xem trạng thái container
docker compose -f ~/deploy/ScanLinkApi/docker-compose.yml ps

# Xem log API
docker compose -f ~/deploy/ScanLinkApi/docker-compose.yml logs -f scanlink-api

# Test HTTP (phải redirect về HTTPS)
curl -I http://localhost

# Test HTTPS (self-signed: dùng -k để bỏ qua cert warning)
curl -k https://localhost/actuator/health

# Test nginx health check
curl -k https://localhost/nginx-health
```

---

## Deploy local (development)

```bash
# Từ thư mục project
bash scripts/deploy-local.sh

# Với options
bash scripts/deploy-local.sh --clean   # rebuild không cache
bash scripts/deploy-local.sh --logs    # tail logs sau khi start
bash scripts/deploy-local.sh --down    # stop tất cả containers
```
