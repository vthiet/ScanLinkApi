#!/bin/bash
# =============================================================
# setup-ssl.sh — Tạo self-signed SSL cert để Nginx phục vụ HTTPS
#
# Chạy TRƯỚC lần deploy đầu tiên:
#   bash setup-ssl.sh
#
# Cert sẽ được tạo tại: ~/deploy/ssl/
# Sau đó chạy deploy.sh như bình thường.
#
# Nếu có domain thật và muốn dùng Let's Encrypt:
#   1. Đảm bảo domain trỏ về IP server
#   2. Chạy: bash setup-ssl.sh --certbot yourdomain.com
# =============================================================

set -euo pipefail

DEPLOY_BASE="${DEPLOY_BASE:-$HOME/deploy}"
SSL_DIR="$DEPLOY_BASE/ssl"

RED='\033[0;31m'; GREEN='\033[0;32m'; YELLOW='\033[1;33m'; CYAN='\033[0;36m'; NC='\033[0m'
log()   { echo -e "${CYAN}[INFO]${NC}  $*"; }
ok()    { echo -e "${GREEN}[OK]${NC}    $*"; }
warn()  { echo -e "${YELLOW}[WARN]${NC}  $*"; }
error() { echo -e "${RED}[ERROR]${NC} $*" >&2; exit 1; }

USE_CERTBOT=false
DOMAIN=""

for arg in "$@"; do
  case $arg in
    --certbot) USE_CERTBOT=true ;;
    --domain=*) DOMAIN="${arg#*=}" ;;
    --help|-h)
      echo "Usage: bash setup-ssl.sh [--certbot] [--domain=yourdomain.com]"
      echo "  (no args)        : Tạo self-signed cert (dùng IP, không cần domain)"
      echo "  --certbot        : Dùng Let's Encrypt (cần domain + port 80 mở)"
      echo "  --domain=<name>  : Domain name cho certbot"
      exit 0
      ;;
    *) warn "Unknown option: $arg" ;;
  esac
done

mkdir -p "$SSL_DIR"

if [ "$USE_CERTBOT" = true ]; then
  # --- Let's Encrypt via Certbot ---
  [ -z "$DOMAIN" ] && error "Cần truyền domain: bash setup-ssl.sh --certbot --domain=yourdomain.com"

  log "Đang cài Certbot (nếu chưa có)..."
  command -v certbot &>/dev/null || {
    if command -v apt-get &>/dev/null; then
      sudo apt-get install -y certbot
    elif command -v yum &>/dev/null; then
      sudo yum install -y certbot
    else
      error "Không tìm thấy apt-get hoặc yum. Cài Certbot thủ công rồi chạy lại."
    fi
  }

  log "Đang lấy cert cho domain: $DOMAIN ..."
  warn "Yêu cầu: domain $DOMAIN đã trỏ về IP server này, port 80 đang mở."
  sudo certbot certonly --standalone -d "$DOMAIN" --non-interactive --agree-tos \
    --register-unsafely-without-email || error "Certbot thất bại. Kiểm tra domain và port 80."

  CERT_PATH="/etc/letsencrypt/live/$DOMAIN"
  cp "$CERT_PATH/fullchain.pem" "$SSL_DIR/server.crt"
  cp "$CERT_PATH/privkey.pem"   "$SSL_DIR/server.key"
  ok "Let's Encrypt cert đã lưu vào $SSL_DIR/"
  log "Gia hạn tự động: sudo certbot renew --quiet (thêm vào crontab nếu muốn)"

else
  # --- Self-signed cert (dùng IP, không cần domain) ---
  log "Tạo self-signed SSL cert tại: $SSL_DIR"
  warn "Self-signed cert sẽ hiện cảnh báo 'Not Secure' trên browser — bình thường cho môi trường nội bộ/staging."

  # Lấy IP public (để thêm vào SAN)
  SERVER_IP=$(curl -s --connect-timeout 5 ifconfig.me 2>/dev/null || hostname -I | awk '{print $1}')
  log "Server IP: $SERVER_IP"

  openssl req -x509 -nodes -days 365 -newkey rsa:2048 \
    -keyout "$SSL_DIR/server.key" \
    -out    "$SSL_DIR/server.crt" \
    -subj   "/C=VN/ST=HCM/L=HoChiMinh/O=ScanLink/OU=Dev/CN=$SERVER_IP" \
    -addext "subjectAltName=IP:$SERVER_IP,IP:127.0.0.1" \
    2>/dev/null

  ok "Self-signed cert tạo thành công!"
  log "  Certificate : $SSL_DIR/server.crt"
  log "  Private key : $SSL_DIR/server.key"
fi

echo ""
ok "SSL setup hoàn tất. Bây giờ chạy deploy.sh để deploy."
