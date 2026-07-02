# =============================================================
# Stage 1: Builder — Build JAR dengan Gradle
# =============================================================
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /workspace

# Copy Gradle wrapper & config dulu (layer cache optimization)
COPY gradlew gradlew.bat ./
COPY gradle ./gradle
COPY settings.gradle build.gradle ./

# Download dependencies (terpisah agar ter-cache jika source belum berubah)
RUN chmod +x gradlew && ./gradlew dependencies --no-daemon --quiet

# Copy source code & build
COPY src ./src
RUN ./gradlew bootJar --no-daemon -x test

# =============================================================
# Stage 2: Runtime — Image production yang minimal & aman
# =============================================================
FROM eclipse-temurin:21-jre-alpine AS runtime

# Install curl untuk health check
RUN apk add --no-cache curl

# Buat non-root user untuk keamanan
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

WORKDIR /app

# Copy JAR dari builder stage
COPY --from=builder /workspace/build/libs/*.jar app.jar

# Buat direktori config & log, set ownership
RUN mkdir -p /app/config /app/logs && chown -R appuser:appgroup /app

USER appuser

# Port yang di-expose
EXPOSE 8080

# Health check via Spring Actuator
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Jalankan aplikasi dengan JVM flags optimal untuk container
ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", "/app/app.jar"]
