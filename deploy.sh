#!/bin/bash

set -e

REPO_URL="https://github.com/vthiet/ScanLinkApi.git"
BRANCH="feat/auth"

PROJECT_DIR="$HOME/deploy/ScanLinkApi"

APP_NAME="scanlink-api"
HOST_PORT="8080"
CONTAINER_PORT="8080"

echo "========================================="
echo "Deploying $APP_NAME"
echo "Branch: $BRANCH"
echo "Project: $PROJECT_DIR"
echo "========================================="

if [ ! -d "$PROJECT_DIR/.git" ]; then
echo "[1/6] Cloning repository..."


git clone \
    --branch "$BRANCH" \
    "$REPO_URL" \
    "$PROJECT_DIR"

else
echo "[1/6] Updating repository..."

cd "$PROJECT_DIR"

git fetch origin
git checkout "$BRANCH"
git reset --hard "origin/$BRANCH"

fi

cd "$PROJECT_DIR"

echo "[2/6] Building Docker image..."

docker build --pull -t "$APP_NAME" .

echo "[3/6] Removing old container if exists..."

docker rm -f "$APP_NAME" 2>/dev/null || true

echo "[4/6] Starting new container..."

docker run -d 
--name "$APP_NAME" 
-p "$HOST_PORT:$CONTAINER_PORT" 
--restart unless-stopped 
"$APP_NAME"

echo "[5/6] Waiting for application startup..."

sleep 15

echo "[6/6] Checking container status..."

if docker ps --format '{{.Names}}' | grep -q "^${APP_NAME}$"; then
echo ""
echo "========================================="
echo "Deployment successful!"
echo "Application is running."
echo "========================================="


docker ps | grep "$APP_NAME"


else
echo ""
echo "========================================="
echo "Deployment failed!"
echo "Container is not running."
echo "========================================="


docker logs "$APP_NAME"

exit 1

fi

echo ""
echo "Cleaning unused images..."

docker image prune -f
