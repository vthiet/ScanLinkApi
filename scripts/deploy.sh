#!/bin/bash
# =============================================================
# deploy.sh — Deploy ScanLinkApi on REMOTE SERVER (production)
#
# CÁCH DÙNG:
#   export ENV_FILE=/path/to/.env
#   export FIREBASE_FILE=/path/to/scanlink-firebase-service-account.json
#   bash deploy.sh
#
# Hoặc truyền thẳng một lần:
#   ENV_FILE=~/deploy/.env FIREBASE_FILE=~/deploy/firebase.json bash deploy.sh
#
# Script sẽ clone/pull repo vào ~/deploy/ScanLinkApi rồi docker compose up.
# =============================================================

set -euo pipefail

# --- Cấu hình mặc định ---
REPO_URL="https://github.com/vthiet/ScanLinkApi.git"
BRANCH="${BRANCH:-main}"
DEPLOY_BASE="${DEPLOY_BASE:-$HOME/deploy}"
PROJECT_DIR="$DEPLOY_BASE/ScanLinkApi"

# --- Biến môi trường bắt buộc ---
ENV_FILE="${ENV_FILE:-$DEPLOY_BASE/.env}"
FIREBASE_FILE="${FIREBASE_FILE:-$DEPLOY_BASE/scanlink-firebase-service-account.json}"

# --- Colors ---
RED='\033[0;31m'; GREEN='\033[0;32m'; YELLOW='\033[1;33m'; CYAN='\033[0;36m'; NC='\033[0m'
log()   { echo -e "${CYAN}[INFO]${NC}  $*"; }
ok()    { echo -e "${GREEN}[OK]${NC}    $*"; }
warn()  { echo -e "${YELLOW}[WARN]${NC}  $*"; }
error() { echo -e "${RED}[ERROR]${NC} $*" >&2; exit 1; }

echo ""
echo "=============================================="
echo "   ScanLink API — Production Deployment"
echo "   Branch      : $BRANCH"
echo "   Deploy dir  : $DEPLOY_BASE"
echo "   Project dir : $PROJECT_DIR"
echo "   ENV file    : $ENV_FILE"
echo "   Firebase    : $FIREBASE_FILE"
echo "=============================================="
echo ""

# --- 1. Check prerequisites ---
log "Checking prerequisites..."
command -v docker &>/dev/null  || error "Docker not found. Install Docker first."
docker info &>/dev/null        || error "Docker daemon is not running. Run: sudo systemctl start docker"
docker compose version &>/dev/null || error "docker compose plugin not found. Update Docker."
command -v git &>/dev/null     || error "git not found. Install git first."
ok "Prerequisites OK"

# --- 2. Validate ENV_FILE ---
log "Checking .env file..."
if [ ! -f "$ENV_FILE" ]; then
  error ".env not found at: $ENV_FILE
  Create it from .env.example:
    cp $PROJECT_DIR/.env.example $ENV_FILE
    nano $ENV_FILE   # fill in real values
  Then re-run: ENV_FILE=$ENV_FILE bash deploy.sh"
fi

REQUIRED_VARS=("MONGODB_URI" "CLOUDINARY_CLOUD_NAME" "CLOUDINARY_API_KEY" "CLOUDINARY_API_SECRET")
MISSING=()
for var in "${REQUIRED_VARS[@]}"; do
  val=$(grep -E "^${var}=" "$ENV_FILE" 2>/dev/null | cut -d= -f2- | tr -d '"' | tr -d "'")
  if [ -z "$val" ] || [[ "$val" == *"<"* ]]; then
    MISSING+=("$var")
  fi
done

if [ ${#MISSING[@]} -gt 0 ]; then
  error "Missing or placeholder values in $ENV_FILE: ${MISSING[*]}"
fi
ok ".env validated"

# Đọc SSL_DIR từ .env (nếu có), fallback về DEPLOY_BASE/ssl
SSL_DIR_FROM_ENV=$(grep -E "^SSL_DIR=" "$ENV_FILE" 2>/dev/null | cut -d= -f2- | tr -d '"' | tr -d "'" || true)
export SSL_DIR="${SSL_DIR_FROM_ENV:-$DEPLOY_BASE/ssl}"

# --- 3. Validate Firebase service account ---
log "Checking Firebase service account..."
if [ ! -f "$FIREBASE_FILE" ]; then
  error "Firebase service account not found at: $FIREBASE_FILE
  Download it from Firebase Console → Project Settings → Service Accounts → Generate new private key
  Then set: export FIREBASE_FILE=/path/to/your-file.json"
fi
ok "Firebase service account found"

# --- 3b. Validate SSL certs ---
log "Checking SSL certificates (SSL_DIR=$SSL_DIR)..."
if [ ! -f "$SSL_DIR/server.crt" ] || [ ! -f "$SSL_DIR/server.key" ]; then
  error "SSL certs not found in $SSL_DIR/
  Chạy setup SSL trước:
    bash $(dirname "$0")/setup-ssl.sh
  Hoặc đặt SSL_DIR= trong $ENV_FILE trỏ tới thư mục chứa server.crt và server.key"
fi
ok "SSL certs found in $SSL_DIR/"

# --- 4. Ensure deploy base dir exists ---
mkdir -p "$DEPLOY_BASE"

# --- 5. Clone or update repo ---
log "[1/5] Syncing repository (branch: $BRANCH)..."
if [ ! -d "$PROJECT_DIR/.git" ]; then
  log "Cloning fresh repository..."
  git clone --branch "$BRANCH" "$REPO_URL" "$PROJECT_DIR"
else
  log "Pulling latest changes..."
  cd "$PROJECT_DIR"
  git fetch origin
  git checkout "$BRANCH"
  git reset --hard "origin/$BRANCH"
fi
ok "Repository up to date"

# --- 6. Sync configs into project dir ---
log "[2/5] Syncing deployment config files..."
cp "$ENV_FILE"      "$PROJECT_DIR/.env"
cp "$FIREBASE_FILE" "$PROJECT_DIR/src/main/resources/scanlink-firebase-service-account.json"

# Đảm bảo ENV_FIREBASE_PATH luôn trỏ đúng path trong Docker container
# (Spring Boot đọc biến này để load Firebase service account)
FIREBASE_CONTAINER_PATH="/app/scanlink-firebase-service-account.json"
if grep -qE "^ENV_FIREBASE_PATH=" "$PROJECT_DIR/.env" 2>/dev/null; then
  # Cập nhật giá trị nếu đã có
  sed -i "s|^ENV_FIREBASE_PATH=.*|ENV_FIREBASE_PATH=$FIREBASE_CONTAINER_PATH|" "$PROJECT_DIR/.env"
else
  # Thêm mới nếu chưa có
  echo "ENV_FIREBASE_PATH=$FIREBASE_CONTAINER_PATH" >> "$PROJECT_DIR/.env"
fi
ok "Config files synced (ENV_FIREBASE_PATH=$FIREBASE_CONTAINER_PATH)"

# --- 7. Remove stale standalone containers ---
log "[3/5] Removing stale containers (if any)..."
for c in scanlink-api scanlink-nginx nginx; do
  if docker ps -aq -f "name=^/${c}$" | grep -q .; then
    warn "Removing stale container: $c"
    docker rm -f "$c" || true
  fi
done

# --- 8. Build & Start ---
log "[4/5] Building and starting services..."
cd "$PROJECT_DIR"
docker compose down --remove-orphans || true
docker compose up -d --build
ok "Services started"

# --- 9. Wait for health ---
log "[5/5] Waiting for application startup (up to 120s)..."
MAX_WAIT=120
ELAPSED=0
INTERVAL=5

while [ $ELAPSED -lt $MAX_WAIT ]; do
  STATUS=$(docker inspect --format='{{.State.Health.Status}}' scanlink-api 2>/dev/null || echo "none")
  if [ "$STATUS" = "healthy" ]; then
    ok "scanlink-api is healthy!"; break
  elif [ "$STATUS" = "unhealthy" ]; then
    echo ""
    echo "=== scanlink-api logs ==="
    docker compose logs --tail=40 scanlink-api
    error "scanlink-api is unhealthy. Check logs above."
  fi
  echo -n "  (${ELAPSED}s — status: ${STATUS})"$'\r'
  sleep $INTERVAL
  ELAPSED=$((ELAPSED + INTERVAL))
done

if [ $ELAPSED -ge $MAX_WAIT ]; then
  warn "Health check timed out after ${MAX_WAIT}s. Container may still be starting."
  warn "Check status: docker compose -f $PROJECT_DIR/docker-compose.yml ps"
fi

echo ""

# --- 10. Final status ---
API_UP=false; NGINX_UP=false
docker ps --format '{{.Names}}' | grep -q "^scanlink-api$"   && API_UP=true   || true
docker ps --format '{{.Names}}' | grep -q "^scanlink-nginx$" && NGINX_UP=true || true

docker compose ps

if [ "$API_UP" = true ] && [ "$NGINX_UP" = true ]; then
  echo ""
  echo "=============================================="
  ok "Deployment successful!"
  echo "  HTTP   → http://<server-ip>"
  echo "  HTTPS  → https://<server-ip>"
  echo "  API    → http://scanlink-api:8080 (internal only)"
  echo "=============================================="
else
  echo ""
  error "One or more containers failed to start.
  Run: docker compose -f $PROJECT_DIR/docker-compose.yml logs
  to see what went wrong."
fi

# --- 11. Cleanup dangling images ---
log "Cleaning dangling images..."
docker image prune -f >/dev/null
ok "Done."
