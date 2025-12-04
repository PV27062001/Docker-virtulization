FROM node:18-alpine AS builder
WORKDIR /app
COPY package*.json ./
RUN npm ci --silent
COPY . .
RUN npm run build

# Production stage
FROM node:18-alpine
WORKDIR /app
COPY package*.json ./
RUN npm ci --silent --only=production
COPY --from=builder /app/build ./build
EXPOSE 3000
CMD [ "npm", "start" ]