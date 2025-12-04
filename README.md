# Docker Virtualization Project

A comprehensive full-stack Docker containerization project demonstrating how to build, containerize, and orchestrate multiple applications (React frontend, Spring Boot backend, and static HTML services) using Docker and Docker Compose.

---

## 🎯 Project Overview

This project showcases Docker best practices with three interconnected services:

| Service | Technology | Port | Purpose |
|---------|-----------|------|---------|
| **Backend** | Spring Boot 3.1 + Java 17 | 8080 | REST API server |
| **Frontend** | React 19 + Node.js | 3000 | Web UI (fetches from backend) |
| **HTML** | Nginx + Alpine Linux | 5000 | Static HTML server |

---

## 📁 Project Structure

```
Docker-virtulization/
├── JAVA/
│   └── springForDocker/           # Spring Boot Backend
│       ├── Dockerfile
│       ├── build.gradle
│       ├── gradlew
│       ├── src/
│       │   ├── main/
│       │   │   ├── java/...       # WelcomeController.java
│       │   │   └── resources/
│       │   └── test/
│       └── build/                 # Compiled JAR files
│
├── virtulization/                 # React Frontend
│   ├── Dockerfile
│   ├── .env                        # Environment variables
│   ├── package.json
│   ├── src/
│   │   ├── App.js                 # Main React component
│   │   └── ...
│   └── public/
│
├── HTML/                           # Static Content
│   ├── Static page/
│   │   ├── Dockerfile
│   │   ├── index.html
│   │   └── style.css
│   └── Dynamic page/
│       └── to-do/
│           ├── Dockerfile
│           ├── index.html
│           ├── style.css
│           └── to_do.js
│
├── docker-compose.yml             # Orchestration file
├── .gitignore                      # Git configuration
├── README.md                       # This file
└── DOCKER_COMPOSE_README.md        # Docker Compose detailed guide
```

---

## 🚀 Quick Start

### Prerequisites
- Docker Desktop installed ([Download](https://www.docker.com/products/docker-desktop))
- Git installed
- Basic command line knowledge

### Step 1: Clone the repository
```bash
git clone https://github.com/PV27062001/Docker-virtulization.git
cd Docker-virtulization
```

### Step 2: Build the Spring Boot JAR
```powershell
cd JAVA/springForDocker
./gradlew.bat build -x test
cd ../..
```

### Step 3: Start all services
```powershell
docker-compose up --build
```

### Step 4: Access the applications

| Service | URL |
|---------|-----|
| Frontend | http://localhost:3000 |
| Backend API | http://localhost:8080/message |
| HTML Server | http://localhost:5000 |

### Step 5: Stop services
```powershell
docker-compose down
```

---

## 🏗️ Architecture

### Data Flow
```
┌─────────────────┐
│  User Browser   │
│  :3000          │
└────────┬────────┘
         │
         ▼
┌─────────────────────────────────┐
│  React Frontend Container       │
│  - Displays UI                  │
│  - Fetches from backend API     │
└────────┬────────────────────────┘
         │ HTTP Request
         │ http://backend:8080/message
         ▼
┌─────────────────────────────────┐
│  Spring Boot Backend Container  │
│  - REST API                     │
│  - WelcomeController            │
│  - Returns: "welcome Praveen!!!"│
└─────────────────────────────────┘
```

### Network Communication
- All services connected via `app-net` bridge network
- Services communicate using Docker DNS (service names)
- Frontend reaches backend via: `http://backend:8080/message`

---

## 📦 Services Details

### Backend (Spring Boot)
**Dockerfile**: `JAVA/springForDocker/Dockerfile`
```dockerfile
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY build/libs/springForDocker-0.0.1-SNAPSHOT.jar springForDocker.jar
ENTRYPOINT ["java", "-jar", "springForDocker.jar"]
```

**Key Features:**
- REST endpoint: `GET /message`
- Returns: `"welcome Praveen!!!"`
- Uses lightweight Alpine Linux image
- Port: 8080

**Build Steps:**
1. Uses pre-built JAR from `./gradlew build`
2. Copies JAR to Docker image
3. Runs Java application on startup

---

### Frontend (React)
**Dockerfile**: `virtulization/Dockerfile`
```dockerfile
FROM node:18-alpine AS builder
# Build stage: Compiles React code
...
FROM node:18-alpine
# Runtime stage: Serves production build
...
```

**Key Features:**
- Multi-stage build for optimization
- Fetches backend data dynamically
- Port: 3000
- Environment variable: `REACT_APP_BACKEND_URL`

**How it works:**
1. React app calls `fetch(process.env.REACT_APP_BACKEND_URL)`
2. Displays message from backend
3. Shows loading and error states

---

### HTML (Nginx)
**Dockerfile**: `HTML/Dynamic page/to-do/Dockerfile`
```dockerfile
FROM nginx:alpine
COPY . /usr/share/nginx/html
```

**Key Features:**
- Lightweight Nginx web server
- Serves static HTML, CSS, JavaScript
- Port: 5000 (mapped from container port 80)
- Volume mount for live file syncing

---

## 🔧 Configuration

### Docker Compose
See `docker-compose.yml` for:
- Service definitions
- Port mappings
- Network configuration
- Volume mounts
- Environment variables
- Service dependencies

For detailed documentation: `DOCKER_COMPOSE_README.md`

### Environment Variables
**Frontend** (`.env` file):
```
REACT_APP_BACKEND_URL=http://backend:8080/message
```

- **In Docker**: Uses service name `backend`
- **Local dev**: Can use `http://localhost:8080/message`

---

## 📝 Common Commands

### Docker Compose
```powershell
# Start services
docker-compose up --build

# Start in background
docker-compose up -d --build

# View status
docker-compose ps

# View logs
docker-compose logs
docker-compose logs frontend
docker-compose logs backend

# Stop services
docker-compose down

# Remove containers and volumes
docker-compose down -v

# Rebuild specific service
docker-compose build backend
```

### Docker
```powershell
# List images
docker images

# List running containers
docker ps

# View container logs
docker logs <container-name>

# Execute command in running container
docker exec -it spring_api bash

# Stop container
docker stop <container-name>

# Remove container
docker rm <container-name>
```

---

## 🐛 Troubleshooting

### Issue: `gradlew: not found`
**Solution**: Build JAR locally first
```powershell
cd JAVA/springForDocker && ./gradlew.bat build -x test
```

### Issue: Port already in use
**Solution**: Change port in `docker-compose.yml`
```yaml
ports:
  - 8081:8080  # Use 8081 instead of 8080
```

### Issue: Frontend can't reach backend
**Verify**:
1. Both services are on same network (`app-net`)
2. Frontend uses correct URL: `http://backend:8080/message`
3. Backend container is running: `docker-compose ps`

### Issue: Changes not reflecting
**Solution**: Rebuild and restart
```powershell
docker-compose down
docker-compose up --build
```

---

## 🎓 Learning Resources

### Docker Concepts Used
- ✅ Multi-stage builds (React)
- ✅ Image optimization (Alpine Linux)
- ✅ Container networking (Bridge network)
- ✅ Volume mounts (Live file syncing)
- ✅ Environment variables
- ✅ Service dependencies
- ✅ Port mapping

### Key Technologies
- **Docker**: Container runtime
- **Docker Compose**: Multi-container orchestration
- **React**: Frontend framework
- **Spring Boot**: Java REST API framework
- **Nginx**: Web server
- **Alpine Linux**: Lightweight OS

---

## 📚 Additional Documentation

- **Docker Compose Guide**: See `DOCKER_COMPOSE_README.md` for detailed service configuration
- **Docker Official Docs**: https://docs.docker.com/
- **Docker Compose Docs**: https://docs.docker.com/compose/
- **Spring Boot Docs**: https://spring.io/projects/spring-boot
- **React Docs**: https://react.dev/

---

## 🔐 .gitignore

The project includes a comprehensive `.gitignore` covering:
- Java/Gradle artifacts (`.gradle/`, `build/`, `*.jar`)
- Node.js dependencies (`node_modules/`, `package-lock.json`)
- IDE configurations (`.vscode/`, `.idea/`)
- Environment files (`.env`)
- OS-specific files (`.DS_Store`, `Thumbs.db`)

---

## 📊 Project Statistics

| Metric | Value |
|--------|-------|
| Services | 3 |
| Dockerfiles | 3 |
| Languages | 2 (Java, JavaScript) |
| Frameworks | 2 (Spring Boot, React) |
| Build Tools | 2 (Gradle, npm) |

---

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/improvement`)
3. Commit changes (`git commit -am 'Add improvement'`)
4. Push to branch (`git push origin feature/improvement`)
5. Open a Pull Request

---

## 📄 License

This project is open source and available for educational purposes.

---

## 👤 Author

**Praveen** - [GitHub](https://github.com/PV27062001)

---

## 🔗 References

- [Docker Best Practices](https://docs.docker.com/develop/dev-best-practices/)
- [Docker Compose Documentation](https://docs.docker.com/compose/)
- [Spring Boot Docker Guide](https://spring.io/guides/topicals/spring-boot-docker)
- [React with Docker](https://reactjs.org/docs/create-a-new-react-app.html#docker)

---

**Last Updated**: December 4, 2025

**Status**: ✅ Production Ready
