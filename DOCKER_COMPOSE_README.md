# Docker Compose Setup Guide

## Overview
This `docker-compose.yml` orchestrates three services:
1. **Backend** - Spring Boot REST API (Port 8080)
2. **Frontend** - React UI (Port 3000)
3. **HTML** - Static HTML server with Nginx (Port 5000)

All services communicate through a bridge network (`app-net`).

---

## Project Structure

```
Docker-virtulization/
├── JAVA/springForDocker/          # Spring Boot backend
│   ├── Dockerfile
│   ├── build/libs/                # Pre-built JAR file
│   ├── src/
│   └── build.gradle
├── virtulization/                 # React frontend
│   ├── Dockerfile
│   ├── .env                        # Environment variables
│   ├── src/
│   └── package.json
├── HTML/                           # Static HTML content
│   └── Dynamic page/to-do/
│       ├── Dockerfile
│       ├── index.html
│       └── style.css
└── docker-compose.yml             # Orchestration file
```

---

## Services Explained

### 1. Backend Service (Spring Boot)
```yaml
backend:
  build: ./JAVA/springForDocker
  container_name: spring_api
  ports:
    - 8080:8080
  networks:
    - app-net
```

**Details:**
- **Image**: Built from `JAVA/springForDocker/Dockerfile`
- **Port Mapping**: `8080:8080` (localhost:8080 → container:8080)
- **Container Name**: `spring_api`
- **Endpoint**: `GET http://localhost:8080/message`
- **Response**: `"welcome Praveen!!!"`
- **Network**: Connected to `app-net` bridge

**Requirements:**
- JAR file must exist at `JAVA/springForDocker/build/libs/springForDocker-0.0.1-SNAPSHOT.jar`
- Build locally first: `cd JAVA/springForDocker && ./gradlew build`

---

### 2. Frontend Service (React)
```yaml
frontend:
  build: ./virtulization
  container_name: react_ui
  ports:
    - 3000:3000
  depends_on:
    - backend
  environment:
    - REACT_APP_BACKEND_URL=http://backend:8080/message
  networks:
    - app-net
```

**Details:**
- **Image**: Built from `virtulization/Dockerfile`
- **Port Mapping**: `3000:3000` (localhost:3000 → container:3000)
- **Container Name**: `react_ui`
- **Depends On**: Waits for backend to start first
- **Environment Variable**: `REACT_APP_BACKEND_URL=http://backend:8080/message`
  - Uses service name `backend` (Docker DNS)
  - React fetches from this URL
- **Network**: Connected to `app-net` bridge

**How it works:**
1. React container starts after backend
2. Reads environment variable
3. Fetches from `http://backend:8080/message` (service name resolution)
4. Displays response from backend

---

### 3. HTML Service (Nginx)
```yaml
html:
  build: "./HTML/Dynamic page/to-do"
  container_name: html_server
  volumes:
    - ./HTML:/usr/share/nginx/html
  ports:
    - 5000:80
  networks:
    - app-net
```

**Details:**
- **Image**: Built from `HTML/Dynamic page/to-do/Dockerfile`
- **Port Mapping**: `5000:80` (localhost:5000 → container:80)
- **Container Name**: `html_server`
- **Volume Mount**: `./HTML:/usr/share/nginx/html`
  - Local HTML folder syncs with Nginx serving directory
  - Live file updates without container restart
- **Network**: Connected to `app-net` bridge

---

## Network Configuration

```yaml
networks:
  app-net:
    driver: bridge
```

**Purpose:** Bridge network allows service-to-service communication using service names.

**How it works:**
- Services access each other via **service name**, not localhost
- Example: Frontend reaches backend at `http://backend:8080` (not `localhost:8080`)
- DNS resolution: Docker internally maps service names to container IPs

---

## Quick Start

### Prerequisites
1. Docker Desktop installed
2. Spring Boot JAR built locally

### Step 1: Build Spring Boot JAR
```powershell
cd JAVA/springForDocker
./gradlew.bat build -x test
cd ../..
```

### Step 2: Start all services
```powershell
docker-compose up --build
```

**Flags:**
- `--build`: Rebuild images (use when Dockerfile changes)
- `-d`: Run in detached mode (background)

### Step 3: Access services
- **Frontend**: http://localhost:3000
- **Backend API**: http://localhost:8080/message
- **HTML Server**: http://localhost:5000

### Step 4: Stop services
```powershell
docker-compose down
```

---

## Common Commands

```powershell
# View running containers
docker-compose ps

# View logs for all services
docker-compose logs

# View logs for specific service
docker-compose logs backend
docker-compose logs frontend

# Rebuild only frontend
docker-compose up --build frontend

# Remove all containers/networks
docker-compose down

# Remove containers, networks, and volumes
docker-compose down -v
```

---

## Environment Variables

### Frontend (.env file)
```
REACT_APP_BACKEND_URL=http://backend:8080/message
```

**Local Development**: Uses service name `backend` (won't work outside Docker)

**For localhost testing**: Change to `http://localhost:8080/message`

---

## Dockerfile Details

### Backend (Spring Boot)
```dockerfile
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
VOLUME /tmp
EXPOSE 8080
COPY build/libs/springForDocker-0.0.1-SNAPSHOT.jar springForDocker.jar
ENTRYPOINT ["java", "-jar", "springForDocker.jar"]
```
- Uses JRE (not JDK) for smaller image
- Pre-built JAR from local build

### Frontend (React)
```dockerfile
FROM node:18-alpine AS builder
WORKDIR /app
COPY package*.json ./
RUN npm ci --silent
COPY . .
RUN npm run build

FROM node:18-alpine
WORKDIR /app
COPY package*.json ./
RUN npm ci --silent --only=production
COPY --from=builder /app/dist ./dist
EXPOSE 3000
CMD [ "npm", "start" ]
```
- Multi-stage build for smaller image
- Build stage: Compiles React
- Runtime stage: Only production dependencies

### HTML (Nginx)
```dockerfile
FROM nginx:alpine
COPY . /usr/share/nginx/html
```
- Lightweight Nginx server
- Serves static files

---

## Data Flow

```
User Browser (Port 3000)
        ↓
   React Frontend
        ↓ (HTTP Request via REACT_APP_BACKEND_URL)
        ↓ http://backend:8080/message
   Spring Boot Backend (Port 8080)
        ↓ (JSON Response)
        ↓ "welcome Praveen!!!"
   React Display
```

---

## Troubleshooting

### Issue: "gradlew: not found"
**Solution**: Build JAR locally first
```powershell
cd JAVA/springForDocker
./gradlew.bat build -x test
```

### Issue: "Cannot connect to backend from frontend"
**Solution**: Ensure both use service name `backend` (not `localhost`)

### Issue: "Port already in use"
**Solution**: Change port mapping in docker-compose.yml
```yaml
ports:
  - 8081:8080  # Use 8081 instead of 8080
```

### Issue: HTML files not updating
**Solution**: Restart service (volume mount should sync automatically)
```powershell
docker-compose restart html
```

---

## Best Practices

✅ Build JAR locally before running docker-compose
✅ Use service names for inter-service communication
✅ Environment variables for configuration
✅ Volume mounts for live file syncing
✅ Multi-stage builds for smaller images
✅ Alpine Linux for lightweight containers

