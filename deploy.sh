#!/bin/bash

# Exit immediately if a command exits with a non-zero status
set -e

REPO_URL="https://github.com/vthiet/ScanLinkApi.git"
BRANCH="feat/auth"

# Dynamically locate the deploy directory (where deploy.sh is located)
# This will work whether it's placed in /deploy/ or $HOME/deploy/
DEPLOY_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$DEPLOY_DIR/ScanLinkApi"

echo "========================================="
echo "Deploying ScanLinkApi with Nginx proxy"
echo "Branch: $BRANCH"
echo "Deploy Directory: $DEPLOY_DIR"
echo "Project Directory: $PROJECT_DIR"
echo "========================================="

# 1. Update or clone the repository
if [ ! -d "$PROJECT_DIR/.git" ]; then
    echo "[1/5] Cloning repository..."
    git clone \
        --branch "$BRANCH" \
        "$REPO_URL" \
        "$PROJECT_DIR"
else
    echo "[1/5] Updating repository..."
    cd "$PROJECT_DIR"
    git fetch origin
    git checkout "$BRANCH"
    git reset --hard "origin/$BRANCH"
fi

# 2. Copy Docker Compose and Nginx configuration files to the deploy directory
echo "[2/5] Copying deployment configurations..."
cp "$PROJECT_DIR/docker-compose.yml" "$DEPLOY_DIR/docker-compose.yml"
mkdir -p "$DEPLOY_DIR/nginx"
cp "$PROJECT_DIR/nginx/default.conf" "$DEPLOY_DIR/nginx/default.conf"

# 3. Start services using Docker Compose
echo "[3/5] Starting services with Docker Compose..."
cd "$DEPLOY_DIR"

# Stop and remove any standalone containers if they exist to avoid conflicts with docker-compose
if [ "$(docker ps -aq -f name=^/scanlink-api$)" ]; then
    echo "Found standalone 'scanlink-api' container. Stopping and removing it to prevent name conflict..."
    docker rm -f scanlink-api || true
fi
if [ "$(docker ps -aq -f name=^/nginx$)" ]; then
    echo "Found standalone 'nginx' container. Stopping and removing it to prevent name conflict..."
    docker rm -f nginx || true
fi

# Force build clean image
docker compose down || true
docker compose up -d --build

# 4. Wait for services to startup
echo "[4/5] Waiting for application startup..."
sleep 15

# 5. Check container status
echo "[5/5] Checking container status..."
cd "$DEPLOY_DIR"

API_RUNNING=false
NGINX_RUNNING=false

if docker ps --format '{{.Names}}' | grep -q "^scanlink-api$"; then
    API_RUNNING=true
fi

if docker ps --format '{{.Names}}' | grep -q "^nginx$"; then
    NGINX_RUNNING=true
fi

if [ "$API_RUNNING" = true ] && [ "$NGINX_RUNNING" = true ]; then
    echo ""
    echo "========================================="
    echo "Deployment successful!"
    echo "Both scanlink-api and nginx are running."
    echo "========================================="
    docker compose ps
else
    echo ""
    echo "========================================="
    echo "Deployment failed!"
    echo "One or more containers are not running."
    echo "scanlink-api: $([ "$API_RUNNING" = true ] && echo "RUNNING" || echo "STOPPED")"
    echo "nginx:        $([ "$NGINX_RUNNING" = true ] && echo "RUNNING" || echo "STOPPED")"
    echo "========================================="
    echo ">>> scanlink-api logs:"
    docker compose logs scanlink-api || true
    echo ">>> nginx logs:"
    docker compose logs nginx || true
    exit 1
fi

echo ""
echo "Cleaning unused images..."
docker image prune -f
