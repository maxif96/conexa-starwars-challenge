# Guía de Despliegue - Star Wars API

## Índice
1. [Configuración de Producción](#configuración-de-producción)
2. [Variables de Entorno](#variables-de-entorno)
3. [Perfiles de Spring](#perfiles-de-spring)
4. [Docker](#docker)
5. [Despliegue en Servidores](#despliegue-en-servidores)
6. [CI/CD](#cicd)
7. [Monitoreo y Logging](#monitoreo-y-logging)
8. [Seguridad en Producción](#seguridad-en-producción)
9. [Troubleshooting](#troubleshooting)

---

## Configuración de Producción

### Requisitos del Sistema

#### Requisitos Mínimos
- **Java**: OpenJDK 8 o superior
- **Memoria**: 512MB RAM mínimo, 1GB recomendado
- **Disco**: 100MB para aplicación + logs
- **CPU**: 1 core mínimo, 2 cores recomendado
- **Sistema Operativo**: Linux, Windows Server, macOS

#### Requisitos Recomendados
- **Java**: OpenJDK 11 LTS
- **Memoria**: 2GB RAM
- **Disco**: 500MB SSD
- **CPU**: 2 cores
- **Sistema Operativo**: Ubuntu 20.04 LTS, CentOS 8, Windows Server 2019

### Configuración de JVM

#### Parámetros de Memoria
```bash
# Configuración básica
java -Xms512m -Xmx1g -jar starwars-api.jar

# Configuración optimizada
java -Xms1g -Xmx2g \
     -XX:+UseG1GC \
     -XX:MaxGCPauseMillis=200 \
     -jar starwars-api.jar

# Configuración para producción
java -Xms2g -Xmx4g \
     -XX:+UseG1GC \
     -XX:MaxGCPauseMillis=100 \
     -XX:+UseStringDeduplication \
     -jar starwars-api.jar
```

#### Parámetros de Performance
```bash
# Optimizaciones de GC
-XX:+UseG1GC                    # Usar G1 Garbage Collector
-XX:MaxGCPauseMillis=200       # Pausa máxima de GC
-XX:+UseStringDeduplication    # Deduplicación de strings

# Optimizaciones de JVM
-XX:+TieredCompilation         # Compilación por niveles
```

---

## Variables de Entorno

### Configuración Esencial

#### Base de Datos
```bash
# H2 Database (desarrollo/testing)
SPRING_DATASOURCE_URL=jdbc:h2:mem:testdb
SPRING_DATASOURCE_USERNAME=sa
SPRING_DATASOURCE_PASSWORD=

# PostgreSQL (producción)
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/starwars
SPRING_DATASOURCE_USERNAME=starwars_user
SPRING_DATASOURCE_PASSWORD=secure_password_123

# MySQL (producción)
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/starwars?useSSL=false&serverTimezone=UTC
SPRING_DATASOURCE_USERNAME=starwars_user
SPRING_DATASOURCE_PASSWORD=secure_password_123
```

#### JWT y Seguridad
```bash
# JWT Configuration
JWT_SECRET=your-super-secure-jwt-secret-key-2024-very-long-and-random
JWT_EXPIRATION=86400000

# Spring Security
SPRING_SECURITY_USER_NAME=admin
SPRING_SECURITY_USER_PASSWORD=admin123
```

#### SWAPI y Servidor
```bash
# SWAPI Configuration
SWAPI_API_BASE_URL=https://swapi.tech/api

# Server Configuration
SERVER_PORT=8080
SERVER_SERVLET_CONTEXT_PATH=/api/v1

# Logging
LOGGING_LEVEL_COM_STARWARS=INFO
LOGGING_LEVEL_ORG_SPRINGFRAMEWORK_WEB=WARN
LOGGING_LEVEL_ROOT=WARN
```

### Archivo de Configuración

#### application-prod.properties
```properties
# ========================================
# CONFIGURACIÓN DE PRODUCCIÓN
# ========================================

# Base de Datos
spring.datasource.url=${SPRING_DATASOURCE_URL:jdbc:postgresql://localhost:5432/starwars}
spring.datasource.username=${SPRING_DATASOURCE_USERNAME:starwars_user}
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD:secure_password_123}
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA Configuration
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=false

# JWT Configuration
jwt.secret=${JWT_SECRET:your-super-secure-jwt-secret-key-2024-very-long-and-random}
jwt.expiration=${JWT_EXPIRATION:86400000}

# SWAPI Configuration
swapi.api.base-url=${SWAPI_API_BASE_URL:https://swapi.tech/api}

# Server Configuration
server.port=${SERVER_PORT:8080}
server.servlet.context-path=${SERVER_SERVLET_CONTEXT_PATH:/api/v1}

# Logging Configuration
logging.level.com.starwars=${LOGGING_LEVEL_COM_STARWARS:INFO}
logging.level.org.springframework.web=${LOGGING_LEVEL_ORG_SPRINGFRAMEWORK_WEB:WARN}
logging.level.root=${LOGGING_LEVEL_ROOT:WARN}
logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n
logging.file.name=logs/starwars-api.log
logging.file.max-size=100MB
logging.file.max-history=30

# Performance Configuration
spring.jpa.properties.hibernate.jdbc.batch_size=20
spring.jpa.properties.hibernate.order_inserts=true
spring.jpa.properties.hibernate.order_updates=true
spring.jpa.properties.hibernate.jdbc.batch_versioned_data=true

# Security Configuration
spring.security.user.name=${SPRING_SECURITY_USER_NAME:admin}
spring.security.user.password=${SPRING_SECURITY_USER_PASSWORD:admin123}
```

---

## Perfiles de Spring

### Configuración de Perfiles

#### **Perfil de Desarrollo**
```bash
# Ejecutar con perfil de desarrollo
java -jar -Dspring.profiles.active=dev starwars-api.jar

# O usando variable de entorno
export SPRING_PROFILES_ACTIVE=dev
java -jar starwars-api.jar
```

#### **Perfil de Producción**
```bash
# Ejecutar con perfil de producción
java -jar -Dspring.profiles.active=prod starwars-api.jar

# O usando variable de entorno
export SPRING_PROFILES_ACTIVE=prod
java -jar starwars-api.jar
```

#### **Perfil de Testing**
```bash
# Ejecutar tests con perfil de testing
mvn test -Dspring.profiles.active=test

# O en aplicación.properties
spring.profiles.active=test
```

### Configuración por Perfil

#### **application-dev.properties**
```properties
# Configuración de desarrollo
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# Logging detallado
logging.level.com.starwars=DEBUG
logging.level.org.springframework.web=DEBUG

# H2 Database
spring.datasource.url=jdbc:h2:mem:devdb
spring.datasource.driver-class-name=org.h2.Driver
spring.h2.console.enabled=true
```

#### **application-test.properties**
```properties
# Configuración de testing
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=false

# Logging para tests
logging.level.com.starwars=INFO
logging.level.org.springframework.web=WARN

# H2 Database para tests
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driver-class-name=org.h2.Driver
```

---

## 🐳 Docker

### Dockerfile

#### **Dockerfile Optimizado**
```dockerfile
# Multi-stage build para optimizar imagen
FROM openjdk:8-jdk-alpine AS builder

# Instalar Maven
RUN apk add --no-cache maven

# Copiar código fuente
WORKDIR /app
COPY pom.xml .
COPY src ./src

# Compilar aplicación
RUN mvn clean package -DskipTests

# Imagen de producción
FROM openjdk:8-jre-alpine

# Crear usuario no-root
RUN addgroup -g 1001 -S starwars && \
    adduser -u 1001 -S starwars -G starwars

# Instalar dependencias necesarias
RUN apk add --no-cache curl

# Crear directorios necesarios
RUN mkdir -p /app/logs && \
    chown -R starwars:starwars /app

# Cambiar a usuario no-root
USER starwars

# Copiar JAR compilado
COPY --from=builder --chown=starwars:starwars /app/target/*.jar app.jar

# Exponer puerto
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/api/v1/actuator/health || exit 1

# Comando de ejecución
ENTRYPOINT ["java", "-Xms512m", "-Xmx1g", "-jar", "app.jar"]
```

#### **Dockerfile con JVM Optimizado**
```dockerfile
FROM openjdk:8-jre-alpine

# Variables de entorno para JVM
ENV JAVA_OPTS="-Xms1g -Xmx2g -XX:+UseG1GC -XX:MaxGCPauseMillis=200"

# Crear usuario no-root
RUN addgroup -g 1001 -S starwars && \
    adduser -u 1001 -S starwars -G starwars

# Instalar dependencias
RUN apk add --no-cache curl

# Crear directorios
RUN mkdir -p /app/logs && \
    chown -R starwars:starwars /app

USER starwars

# Copiar JAR
COPY --chown=starwars:starwars target/*.jar app.jar

EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/api/v1/actuator/health || exit 1

# Comando con variables de entorno
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
```

### Docker Compose

#### **docker-compose.yml**
```yaml
version: '3.8'

services:
  starwars-api:
    build: .
    container_name: starwars-api
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/starwars
      - SPRING_DATASOURCE_USERNAME=starwars_user
      - SPRING_DATASOURCE_PASSWORD=secure_password_123
      - JWT_SECRET=your-super-secure-jwt-secret-key-2024
      - SWAPI_API_BASE_URL=https://swapi.tech/api
    volumes:
      - ./logs:/app/logs
    depends_on:
      - postgres
    restart: unless-stopped
    networks:
      - starwars-network

  postgres:
    image: postgres:13-alpine
    container_name: starwars-postgres
    environment:
      - POSTGRES_DB=starwars
      - POSTGRES_USER=starwars_user
      - POSTGRES_PASSWORD=secure_password_123
    volumes:
      - postgres_data:/var/lib/postgresql/data
      - ./init.sql:/docker-entrypoint-initdb.d/init.sql
    ports:
      - "5432:5432"
    restart: unless-stopped
    networks:
      - starwars-network

  nginx:
    image: nginx:alpine
    container_name: starwars-nginx
    ports:
      - "80:80"
      - "443:443"
    volumes:
      - ./nginx.conf:/etc/nginx/nginx.conf
      - ./ssl:/etc/nginx/ssl
    depends_on:
      - starwars-api
    restart: unless-stopped
    networks:
      - starwars-network

volumes:
  postgres_data:

networks:
  starwars-network:
    driver: bridge
```

#### **docker-compose.prod.yml**
```yaml
version: '3.8'

services:
  starwars-api:
    build: .
    container_name: starwars-api-prod
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/starwars
      - SPRING_DATASOURCE_USERNAME=${DB_USERNAME}
      - SPRING_DATASOURCE_PASSWORD=${DB_PASSWORD}
      - JWT_SECRET=${JWT_SECRET}
      - SWAPI_API_BASE_URL=https://swapi.tech/api
    volumes:
      - ./logs:/app/logs
    depends_on:
      - postgres
    restart: always
    deploy:
      resources:
        limits:
          memory: 2G
          cpus: '1.0'
        reservations:
          memory: 1G
          cpus: '0.5'
    networks:
      - starwars-network

  postgres:
    image: postgres:13-alpine
    container_name: starwars-postgres-prod
    environment:
      - POSTGRES_DB=starwars
      - POSTGRES_USER=${DB_USERNAME}
      - POSTGRES_PASSWORD=${DB_PASSWORD}
    volumes:
      - postgres_data:/var/lib/postgresql/data
      - ./init.sql:/docker-entrypoint-initdb.d/init.sql
    ports:
      - "5432:5432"
    restart: always
    deploy:
      resources:
        limits:
          memory: 1G
          cpus: '0.5'
    networks:
      - starwars-network

volumes:
  postgres_data:

networks:
  starwars-network:
    driver: bridge
```

### Scripts de Docker

#### **build.sh**
```bash
#!/bin/bash

# Script para construir y ejecutar con Docker

echo "Construyendo imagen Docker..."
docker build -t starwars-api:latest .

echo "📦 Creando red Docker..."
docker network create starwars-network 2>/dev/null || true

echo "Iniciando PostgreSQL..."
docker run -d \
    --name starwars-postgres \
    --network starwars-network \
    -e POSTGRES_DB=starwars \
    -e POSTGRES_USER=starwars_user \
    -e POSTGRES_PASSWORD=secure_password_123 \
    -v postgres_data:/var/lib/postgresql/data \
    postgres:13-alpine

echo "⏳ Esperando PostgreSQL..."
sleep 10

echo "Iniciando Star Wars API..."
docker run -d \
    --name starwars-api \
    --network starwars-network \
    -p 8080:8080 \
    -e SPRING_PROFILES_ACTIVE=prod \
    -e SPRING_DATASOURCE_URL=jdbc:postgresql://starwars-postgres:5432/starwars \
    -e SPRING_DATASOURCE_USERNAME=starwars_user \
    -e SPRING_DATASOURCE_PASSWORD=secure_password_123 \
    -e JWT_SECRET=your-super-secure-jwt-secret-key-2024 \
    starwars-api:latest

echo "Aplicación iniciada en http://localhost:8080/api/v1"
echo "Swagger UI: http://localhost:8080/api/v1/swagger-ui/"
```

#### **deploy.sh**
```bash
#!/bin/bash

# Script de despliegue para producción

set -e

echo "Iniciando despliegue de Star Wars API..."

# Verificar variables de entorno
if [ -z "$DB_USERNAME" ] || [ -z "$DB_PASSWORD" ] || [ -z "$JWT_SECRET" ]; then
    echo "Error: Variables de entorno requeridas no están configuradas"
    echo "   DB_USERNAME, DB_PASSWORD, JWT_SECRET"
    exit 1
fi

# Parar contenedores existentes
echo "🛑 Parando contenedores existentes..."
docker-compose -f docker-compose.prod.yml down

# Limpiar imágenes antiguas
echo "🧹 Limpiando imágenes antiguas..."
docker image prune -f

# Construir nueva imagen
echo "🔨 Construyendo nueva imagen..."
docker build -t starwars-api:latest .

# Iniciar servicios
echo "Iniciando servicios..."
docker-compose -f docker-compose.prod.yml up -d

# Verificar salud
echo "🏥 Verificando salud de la aplicación..."
sleep 30

if curl -f http://localhost:8080/api/v1/actuator/health > /dev/null 2>&1; then
    echo "Despliegue exitoso!"
echo "API disponible en: http://localhost:8080/api/v1"
echo "Swagger UI: http://localhost:8080/api/v1/swagger-ui/"
else
    echo "Error: La aplicación no responde correctamente"
    docker-compose -f docker-compose.prod.yml logs starwars-api
    exit 1
fi
```

---

## 🖥️ Despliegue en Servidores

### Despliegue Manual

#### **1. Preparar Servidor**
```bash
# Actualizar sistema
sudo apt update && sudo apt upgrade -y

# Instalar Java
sudo apt install openjdk-8-jdk -y

# Verificar instalación
java -version

# Crear usuario para la aplicación
sudo useradd -r -s /bin/false starwars
sudo mkdir -p /opt/starwars-api
sudo chown starwars:starwars /opt/starwars-api
```

#### **2. Desplegar Aplicación**
```bash
# Copiar JAR al servidor
scp target/starwars-api.jar user@server:/opt/starwars-api/

# Crear archivo de configuración
sudo tee /opt/starwars-api/application-prod.properties << EOF
spring.datasource.url=jdbc:postgresql://localhost:5432/starwars
spring.datasource.username=starwars_user
spring.datasource.password=secure_password_123
jwt.secret=your-super-secure-jwt-secret-key-2024
swapi.api.base-url=https://swapi.tech/api
server.port=8080
server.servlet.context-path=/api/v1
EOF

# Crear script de inicio
sudo tee /opt/starwars-api/start.sh << EOF
#!/bin/bash
cd /opt/starwars-api
java -Xms1g -Xmx2g -jar starwars-api.jar --spring.profiles.active=prod
EOF

sudo chmod +x /opt/starwars-api/start.sh
sudo chown starwars:starwars /opt/starwars-api/*
```

#### **3. Configurar Systemd Service**
```bash
# Crear servicio systemd
sudo tee /etc/systemd/system/starwars-api.service << EOF
[Unit]
Description=Star Wars API
After=network.target postgresql.service

[Service]
Type=simple
User=starwars
WorkingDirectory=/opt/starwars-api
ExecStart=/opt/starwars-api/start.sh
Restart=always
RestartSec=10
StandardOutput=journal
StandardError=journal

[Install]
WantedBy=multi-user.target
EOF

# Habilitar y iniciar servicio
sudo systemctl daemon-reload
sudo systemctl enable starwars-api
sudo systemctl start starwars-api

# Verificar estado
sudo systemctl status starwars-api
```

### Despliegue con Ansible

#### **inventory.yml**
```yaml
all:
  children:
    starwars_servers:
      hosts:
        starwars-prod:
          ansible_host: 192.168.1.100
          ansible_user: ubuntu
          ansible_ssh_private_key_file: ~/.ssh/id_rsa
          db_host: localhost
          db_name: starwars
          db_user: starwars_user
          db_password: secure_password_123
          jwt_secret: your-super-secure-jwt-secret-key-2024
```

#### **deploy.yml**
```yaml
---
- name: Desplegar Star Wars API
  hosts: starwars_servers
  become: yes
  
  tasks:
    - name: Actualizar sistema
      apt:
        update_cache: yes
        upgrade: yes
        
    - name: Instalar Java 8
      apt:
        name: openjdk-8-jdk
        state: present
        
    - name: Crear usuario starwars
      user:
        name: starwars
        system: yes
        shell: /bin/false
        home: /opt/starwars-api
        
    - name: Crear directorio de aplicación
      file:
        path: /opt/starwars-api
        state: directory
        owner: starwars
        group: starwars
        mode: '0755'
        
    - name: Copiar JAR de aplicación
      copy:
        src: target/starwars-api.jar
        dest: /opt/starwars-api/starwars-api.jar
        owner: starwars
        group: starwars
        mode: '0644'
        
    - name: Crear archivo de configuración
      template:
        src: application-prod.properties.j2
        dest: /opt/starwars-api/application-prod.properties
        owner: starwars
        group: starwars
        mode: '0644'
        
    - name: Crear script de inicio
      template:
        src: start.sh.j2
        dest: /opt/starwars-api/start.sh
        owner: starwars
        group: starwars
        mode: '0755'
        
    - name: Configurar servicio systemd
      template:
        src: starwars-api.service.j2
        dest: /etc/systemd/system/starwars-api.service
        mode: '0644'
        
    - name: Recargar systemd
      systemd:
        daemon_reload: yes
        
    - name: Habilitar servicio
      systemd:
        name: starwars-api
        enabled: yes
        state: started
```

---

## CI/CD

### GitHub Actions

#### **.github/workflows/deploy.yml**
```yaml
name: Deploy Star Wars API

on:
  push:
    branches: [ main ]
  pull_request:
    branches: [ main ]

jobs:
  test:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v3
    
    - name: Set up JDK 8
      uses: actions/setup-java@v3
      with:
        java-version: '8'
        distribution: 'temurin'
        
    - name: Cache Maven packages
      uses: actions/cache@v3
      with:
        path: ~/.m2
        key: ${{ runner.os }}-m2-${{ hashFiles('**/pom.xml') }}
        restore-keys: ${{ runner.os }}-m2
        
    - name: Run tests
      run: mvn test
      
    - name: Build application
      run: mvn clean package -DskipTests
      
    - name: Upload artifact
      uses: actions/upload-artifact@v3
      with:
        name: starwars-api
        path: target/*.jar

  deploy:
    needs: test
    runs-on: ubuntu-latest
    if: github.ref == 'refs/heads/main'
    
    steps:
    - name: Download artifact
      uses: actions/download-artifact@v3
      with:
        name: starwars-api
        
    - name: Deploy to production
      uses: appleboy/ssh-action@v0.1.5
      with:
        host: ${{ secrets.HOST }}
        username: ${{ secrets.USERNAME }}
        key: ${{ secrets.KEY }}
        script: |
          cd /opt/starwars-api
          sudo systemctl stop starwars-api
          cp starwars-api.jar starwars-api.jar.backup
          mv ~/starwars-api.jar .
          sudo systemctl start starwars-api
          sleep 10
          if curl -f http://localhost:8080/api/v1/actuator/health; then
            echo "Deployment successful"
          else
            echo "Deployment failed, rolling back"
            mv starwars-api.jar.backup starwars-api.jar
            sudo systemctl start starwars-api
            exit 1
          fi
```

### Jenkins Pipeline

#### **Jenkinsfile**
```groovy
pipeline {
    agent any
    
    environment {
        JAVA_HOME = tool 'JDK8'
        MAVEN_HOME = tool 'Maven3'
    }
    
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        
        stage('Test') {
            steps {
                sh "${MAVEN_HOME}/bin/mvn test"
            }
        }
        
        stage('Build') {
            steps {
                sh "${MAVEN_HOME}/bin/mvn clean package -DskipTests"
            }
        }
        
        stage('Deploy to Staging') {
            when {
                branch 'develop'
            }
            steps {
                sh 'scp target/*.jar staging-server:/opt/starwars-api/'
                sh 'ssh staging-server "cd /opt/starwars-api && ./restart.sh"'
            }
        }
        
        stage('Deploy to Production') {
            when {
                branch 'main'
            }
            steps {
                sh 'scp target/*.jar prod-server:/opt/starwars-api/'
                sh 'ssh prod-server "cd /opt/starwars-api && ./restart.sh"'
            }
        }
    }
    
    post {
        always {
            cleanWs()
        }
        success {
            echo 'Pipeline completed successfully!'
        }
        failure {
            echo 'Pipeline failed!'
        }
    }
}
```

---

## Monitoreo y Logging

### Spring Boot Actuator

#### **Dependencias en pom.xml**
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

#### **Configuración en application-prod.properties**
```properties
# Actuator Configuration
management.endpoints.web.exposure.include=health,info,metrics,prometheus
management.endpoint.health.show-details=when-authorized
management.endpoint.health.show-components=when-authorized
management.health.db.enabled=true
management.health.diskspace.enabled=true

# Metrics Configuration
management.metrics.export.prometheus.enabled=true
management.metrics.distribution.percentiles-histogram.http.server.requests=true
```

#### **Endpoints de Monitoreo**
```bash
# Health Check
GET /api/v1/actuator/health

# Información de la aplicación
GET /api/v1/actuator/info

# Métricas
GET /api/v1/actuator/metrics

# Métricas específicas
GET /api/v1/actuator/metrics/http.server.requests
GET /api/v1/actuator/metrics/jvm.memory.used
GET /api/v1/actuator/metrics/process.cpu.usage
```

### Logging Estructurado

#### **logback-spring.xml**
```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <springProfile name="prod">
        <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
            <file>logs/starwars-api.log</file>
            <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
                <fileNamePattern>logs/starwars-api.%d{yyyy-MM-dd}.%i.log</fileNamePattern>
                <timeBasedFileNamingAndTriggeringPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP">
                    <maxFileSize>100MB</maxFileSize>
                </timeBasedFileNamingAndTriggeringPolicy>
                <maxHistory>30</maxHistory>
            </rollingPolicy>
            <encoder class="net.logstash.logback.encoder.LoggingEventCompositeJsonEncoder">
                <providers>
                    <timestamp/>
                    <logLevel/>
                    <loggerName/>
                    <message/>
                    <mdc/>
                    <stackTrace/>
                </providers>
            </encoder>
        </appender>
        
        <root level="INFO">
            <appender-ref ref="FILE"/>
        </root>
    </springProfile>
</configuration>
```

### Prometheus y Grafana

#### **docker-compose.monitoring.yml**
```yaml
version: '3.8'

services:
  prometheus:
    image: prom/prometheus:latest
    container_name: prometheus
    ports:
      - "9090:9090"
    volumes:
      - ./prometheus.yml:/etc/prometheus/prometheus.yml
      - prometheus_data:/prometheus
    command:
      - '--config.file=/etc/prometheus/prometheus.yml'
      - '--storage.tsdb.path=/prometheus'
      - '--web.console.libraries=/etc/prometheus/console_libraries'
      - '--web.console.templates=/etc/prometheus/consoles'
      - '--storage.tsdb.retention.time=200h'
      - '--web.enable-lifecycle'
    networks:
      - monitoring

  grafana:
    image: grafana/grafana:latest
    container_name: grafana
    ports:
      - "3000:3000"
    environment:
      - GF_SECURITY_ADMIN_PASSWORD=admin
    volumes:
      - grafana_data:/var/lib/grafana
    networks:
      - monitoring

volumes:
  prometheus_data:
  grafana_data:

networks:
  monitoring:
    driver: bridge
```

---

## 🔒 Seguridad en Producción

### Configuración de Seguridad

#### **application-prod.properties**
```properties
# Security Configuration
spring.security.user.name=${ADMIN_USERNAME:admin}
spring.security.user.password=${ADMIN_PASSWORD:secure_password_123}

# JWT Security
jwt.secret=${JWT_SECRET:your-super-secure-jwt-secret-key-2024-very-long-and-random}
jwt.expiration=${JWT_EXPIRATION:86400000}

# HTTPS Configuration
server.ssl.enabled=true
server.ssl.key-store=classpath:keystore.p12
server.ssl.key-store-password=${SSL_KEYSTORE_PASSWORD}
server.ssl.key-store-type=PKCS12
server.ssl.key-alias=tomcat

# CORS Configuration
spring.web.cors.allowed-origins=${ALLOWED_ORIGINS:https://yourdomain.com}
spring.web.cors.allowed-methods=GET,POST,PUT,DELETE,OPTIONS
spring.web.cors.allowed-headers=*
spring.web.cors.allow-credentials=true
```

### Nginx como Reverse Proxy

#### **nginx.conf**
```nginx
events {
    worker_connections 1024;
}

http {
    upstream starwars_api {
        server starwars-api:8080;
    }

    # Rate Limiting
    limit_req_zone $binary_remote_addr zone=api:10m rate=10r/s;
    limit_req_zone $binary_remote_addr zone=login:10m rate=5r/m;

    server {
        listen 80;
        server_name yourdomain.com;
        return 301 https://$server_name$request_uri;
    }

    server {
        listen 443 ssl http2;
        server_name yourdomain.com;

        # SSL Configuration
        ssl_certificate /etc/nginx/ssl/cert.pem;
        ssl_certificate_key /etc/nginx/ssl/key.pem;
        ssl_protocols TLSv1.2 TLSv1.3;
        ssl_ciphers ECDHE-RSA-AES128-GCM-SHA256:ECDHE-RSA-AES256-GCM-SHA384;
        ssl_prefer_server_ciphers off;

        # Security Headers
        add_header X-Frame-Options DENY;
        add_header X-Content-Type-Options nosniff;
        add_header X-XSS-Protection "1; mode=block";
        add_header Strict-Transport-Security "max-age=31536000; includeSubDomains";

        # Rate Limiting
        location /api/v1/auth/login {
            limit_req zone=login burst=10 nodelay;
            proxy_pass http://starwars_api;
        }

        location /api/v1/ {
            limit_req zone=api burst=20 nodelay;
            proxy_pass http://starwars_api;
        }

        # Proxy Configuration
        location / {
            proxy_pass http://starwars_api;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;
            
            # Timeouts
            proxy_connect_timeout 30s;
            proxy_send_timeout 30s;
            proxy_read_timeout 30s;
        }
    }
}
```

---

## Troubleshooting

### Problemas Comunes

#### **1. Aplicación No Inicia**
```bash
# Verificar logs
sudo journalctl -u starwars-api -f

# Verificar puerto
sudo netstat -tlnp | grep :8080

# Verificar permisos
ls -la /opt/starwars-api/
```

#### **2. Problemas de Memoria**
```bash
# Verificar uso de memoria
free -h
ps aux | grep java

# Ajustar parámetros JVM
java -Xms1g -Xmx2g -XX:+UseG1GC -jar app.jar
```

#### **3. Problemas de Base de Datos**
```bash
# Verificar conexión PostgreSQL
psql -h localhost -U starwars_user -d starwars

# Verificar logs de PostgreSQL
sudo tail -f /var/log/postgresql/postgresql-13-main.log
```

#### **4. Problemas de Red**
```bash
# Verificar conectividad
curl -v http://localhost:8080/api/v1/actuator/health

# Verificar firewall
sudo ufw status
sudo iptables -L
```

### Comandos de Debugging

#### **Verificar Estado del Servicio**
```bash
# Estado del servicio
sudo systemctl status starwars-api

# Logs en tiempo real
sudo journalctl -u starwars-api -f

# Logs de las últimas 100 líneas
sudo journalctl -u starwars-api -n 100
```

#### **Verificar Recursos del Sistema**
```bash
# Uso de CPU y memoria
top
htop

# Uso de disco
df -h
du -sh /opt/starwars-api/

# Procesos Java
jps -l
jstat -gc <pid>
```

#### **Verificar Red**
```bash
# Puertos abiertos
sudo netstat -tlnp
sudo ss -tlnp

# Conectividad
telnet localhost 8080
curl -v http://localhost:8080/api/v1/actuator/health
```

---

## Recursos Adicionales

### Documentación
- [Spring Boot Deployment](https://docs.spring.io/spring-boot/docs/current/reference/html/deployment.html)
- [Docker Best Practices](https://docs.docker.com/develop/dev-best-practices/)
- [Nginx Configuration](https://nginx.org/en/docs/)

### Herramientas
- [Prometheus](https://prometheus.io/) - Monitoreo y métricas
- [Grafana](https://grafana.com/) - Visualización de datos
- [Ansible](https://www.ansible.com/) - Automatización de despliegue

---

*Esta guía de despliegue se actualiza regularmente. Última actualización: Enero 2024*
