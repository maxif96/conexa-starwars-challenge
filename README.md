# Star Wars API - Sistema de Autenticación y Gestión de Datos

## Descripción
API REST que integra con la API oficial de Star Wars (swapi.tech) y proporciona un sistema de autenticación con JWT. La aplicación maneja cuatro entidades principales: **People** (Personajes), **Films** (Películas), **Starships** (Naves Espaciales) y **Vehicles** (Vehículos), con funcionalidades de listado paginado, búsqueda por nombre y obtención por ID.

## Características Principales
- **Sistema de autenticación JWT** completo
- **Integración con SWAPI** (Star Wars API oficial)
- **Listado paginado** de todas las entidades
- **Búsqueda por nombre** con filtrado
- **Obtención por ID** con manejo de errores
- **Documentación Swagger/OpenAPI** completa
- **Base de datos H2** en memoria para usuarios
- **Tests unitarios e integración** con estrategia híbrida
- **Mensajes de error en español** para mejor UX

## Tecnologías Utilizadas
- **Java 8**
- **Spring Boot 2.7.18**
- **Spring Security + JWT**
- **Spring Data JPA**
- **H2 Database** (en memoria)
- **MapStruct** para mapeo de objetos
- **Swagger/OpenAPI 3** para documentación
- **Lombok** para reducir boilerplate
- **JUnit 4/5 + Mockito** para testing
- **WireMock** para tests de integración

## Arquitectura del Proyecto

### Estructura de Directorios
```
src/main/java/com/starwars/
├── config/          # Configuraciones (Security, OpenAPI)
├── controller/      # Controladores REST
├── dto/            # Objetos de transferencia de datos
│   ├── api/        # DTOs para respuestas de SWAPI
│   ├── response/   # DTOs de respuesta de la aplicación
│   └── authentication/ # DTOs de autenticación
├── entity/         # Entidades JPA
├── exception/      # Manejo centralizado de excepciones
├── mapper/         # Mapeadores MapStruct
├── repository/     # Repositorios de datos
├── security/       # Configuración de seguridad JWT
└── service/        # Lógica de negocio
```

### Patrones de Diseño
- **DTO Pattern**: Separación entre datos de API externa y respuesta interna
- **Mapper Pattern**: Conversión automática entre DTOs usando MapStruct
- **Repository Pattern**: Abstracción de acceso a datos
- **Service Layer**: Lógica de negocio centralizada
- **Global Exception Handler**: Manejo centralizado de errores

## Sistema de Autenticación

### Usuarios de Prueba Predefinidos
La aplicación incluye usuarios predefinidos para testing:

| Username | Password | Descripción |
|----------|----------|-------------|
| `admin`  | `admin123` | Usuario administrador |
| `user`   | `user123`  | Usuario estándar |
| `test`   | `test123`  | Usuario de pruebas |

### Flujo de Autenticación
1. **Registro**: `POST /api/v1/auth/register`
2. **Login**: `POST /api/v1/auth/login`
3. **Verificación**: `GET /api/v1/auth/check-username/{username}`
4. **Uso**: Incluir token en header `Authorization: Bearer {token}`

## URLs de Acceso

### 🌐 **Local Development**
- **Base URL**: `http://localhost:8080/api/v1`
- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **H2 Console**: `http://localhost:8080/h2-console`

### 🚀 **Heroku Production**
- **Base URL**: `https://conexa-starwars-api-f5c72652ce2f.herokuapp.com/api/v1`
- **Swagger UI**: `https://conexa-starwars-api-f5c72652ce2f.herokuapp.com/swagger-ui.html`
- **API Docs**: `https://conexa-starwars-api-f5c72652ce2f.herokuapp.com/api-docs`

---

## Endpoints de la API

### Autenticación

#### 1. Registro de Usuario

**Local:**
```http
POST http://localhost:8080/api/v1/auth/register
Content-Type: application/json

{
    "username": "nuevo_usuario",
    "password": "mi_contraseña",
    "confirmPassword": "mi_contraseña"
}
```

**Heroku:**
```http
POST https://conexa-starwars-api-f5c72652ce2f.herokuapp.com/api/v1/auth/register
Content-Type: application/json

{
    "username": "nuevo_usuario",
    "password": "mi_contraseña",
    "confirmPassword": "mi_contraseña"
}
```

**Respuesta exitosa:**
```json
{
    "message": "Usuario registrado exitosamente",
    "username": "nuevo_usuario",
    "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

#### 2. Inicio de Sesión
```http
POST http://localhost:8080/api/v1/auth/login
Content-Type: application/json

{
    "username": "admin",
    "password": "admin123"
}
```

**Respuesta exitosa:**
```json
{
    "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

#### 3. Verificar Disponibilidad de Username
```http
GET http://localhost:8080/api/v1/auth/check-username/{username}
```

**Respuesta:**
- `true` si el username está disponible
- `false` si ya está en uso

### 👥 People (Personajes)

#### Listado Paginado

**Local:**
```http
GET http://localhost:8080/api/v1/people?page=1&limit=10
GET http://localhost:8080/api/v1/people?page=2&limit=5
```

**Heroku:**
```http
GET https://conexa-starwars-api-f5c72652ce2f.herokuapp.com/api/v1/people?page=1&limit=10
GET https://conexa-starwars-api-f5c72652ce2f.herokuapp.com/api/v1/people?page=2&limit=5
```

#### Búsqueda por Nombre
```http
GET http://localhost:8080/api/v1/people?name=skywalker
GET http://localhost:8080/api/v1/people?name=L
GET http://localhost:8080/api/v1/people?name=r2
```

#### Obtener por ID
```http
GET http://localhost:8080/api/v1/people/1
```

### 🎬 Films (Películas)

#### Listado Paginado
```http
GET http://localhost:8080/api/v1/films?page=1&limit=10
GET http://localhost:8080/api/v1/films?page=1&limit=3
```

#### Búsqueda por Título
```http
GET http://localhost:8080/api/v1/films?title=hope
GET http://localhost:8080/api/v1/films?title=jedi
```

#### Obtener por ID
```http
GET http://localhost:8080/api/v1/films/1
```

### Starships (Naves Espaciales)

#### Listado Paginado
```http
GET http://localhost:8080/api/v1/starships
GET http://localhost:8080/api/v1/starships?page=2
```

#### Búsqueda por Nombre
```http
GET http://localhost:8080/api/v1/starships?name=destroyer
GET http://localhost:8080/api/v1/starships?name=falcon
```

#### Obtener por ID
```http
GET http://localhost:8080/api/v1/starships/9
GET http://localhost:8080/api/v1/starships/10
```

### 🚗 Vehicles (Vehículos)

#### Listado Paginado
```http
GET http://localhost:8080/api/v1/vehicles
GET http://localhost:8080/api/v1/vehicles?page=1&limit=5
```

#### Búsqueda por Nombre
```http
GET http://localhost:8080/api/v1/vehicles?name=speeder
GET http://localhost:8080/api/v1/vehicles?name=crawler
```

#### Obtener por ID
```http
GET http://localhost:8080/api/v1/vehicles/4
```

## Configuración y Ejecución

### Requisitos
- Java 8 o superior
- Maven 3.6+
- Conexión a internet (para SWAPI)

### Configuración
- **Puerto**: 8080
- **Context Path**: `/api/v1`
- **Base de datos**: H2 en memoria
- **JWT Secret**: Configurado en `application.properties`

### Ejecución
```bash
# Compilar
mvn clean compile

# Ejecutar
mvn spring-boot:run

# Ejecutar tests
mvn test

# Ejecutar tests de integración
mvn verify
```

## 🚀 Despliegue en Heroku

### Configuración Automática
La aplicación está configurada para desplegarse automáticamente en Heroku:

1. **Variables de Entorno Configuradas:**
   - `JWT_SECRET`: Clave secreta para JWT
   - `JWT_EXPIRATION`: Tiempo de expiración del token
   - `SPRING_PROFILES_ACTIVE`: Perfil de producción

2. **Buildpacks:**
   - Java 8 runtime
   - Maven build system

3. **URL de Producción:**
   - **App**: `https://conexa-starwars-api-f5c72652ce2f.herokuapp.com`
   - **Swagger UI**: `https://conexa-starwars-api-f5c72652ce2f.herokuapp.com/swagger-ui.html`

### Despliegue Manual (si es necesario)
```bash
# Instalar Heroku CLI
# Crear app en Heroku
heroku create conexa-starwars-api

# Configurar variables de entorno
heroku config:set JWT_SECRET="tu-secret-key"
heroku config:set JWT_EXPIRATION="86400000"
heroku config:set SPRING_PROFILES_ACTIVE="prod"

# Desplegar
git push heroku main
```

### Acceso a Herramientas

**Local Development:**
- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **H2 Console**: `http://localhost:8080/h2-console`
- **API Docs**: `http://localhost:8080/api-docs`

**Heroku Production:**
- **Swagger UI**: `https://conexa-starwars-api-f5c72652ce2f.herokuapp.com/swagger-ui.html`
- **API Docs**: `https://conexa-starwars-api-f5c72652ce2f.herokuapp.com/api-docs`
- **H2 Console**: No disponible en producción

## Testing

### Estrategia de Testing Híbrida
El proyecto implementa una estrategia de testing híbrida que combina:

#### Tests Unitarios (`src/test/java/com/starwars/service/`)
- **Objetivo**: Probar lógica de negocio en aislamiento
- **Tecnología**: JUnit 4 + Mockito
- **Características**: Rápidos, sin contexto Spring, mocks de dependencias
- **Ubicación**: `*ServiceUnitTest.java`

#### Tests de Integración (`src/test/java/com/starwars/controller/`)
- **Objetivo**: Probar flujo completo de controladores
- **Tecnología**: JUnit 4 + Spring Boot Test + WireMock
- **Características**: Contexto Spring completo, simulación de API externa
- **Ubicación**: `*ControllerIntegrationTest.java`

### Ejecución de Tests
```bash
# Solo tests unitarios
mvn test -Dtest="*UnitTest"

# Solo tests de integración
mvn test -Dtest="*IntegrationTest"

# Todos los tests
mvn test
```

## 🚨 Manejo de Errores

### Excepciones Personalizadas
- **`ResourceNotFoundException`**: Recurso no encontrado
- **`BadCredentialsException`**: Credenciales incorrectas
- **`AccessDeniedException`**: Acceso denegado

### Respuestas de Error Estandarizadas
```json
{
    "timestamp": "2024-01-15T10:30:00",
    "status": 404,
    "error": "No Encontrado",
    "message": "Person no encontrado con id : '999'",
    "path": "/api/v1/people/999"
}
```

### Códigos de Estado HTTP
- **200**: OK - Operación exitosa
- **201**: Created - Recurso creado
- **400**: Bad Request - Parámetros inválidos
- **401**: Unauthorized - No autenticado
- **403**: Forbidden - Acceso denegado
- **404**: Not Found - Recurso no encontrado
- **500**: Internal Server Error - Error del servidor

## Particularidades Técnicas

### Formatos de Respuesta de SWAPI
La API de Star Wars devuelve **tres formatos diferentes** de JSON según el endpoint:

#### 1. **Listado Paginado** (sin filtro de nombre)
```json
{
    "message": "ok",
    "total_records": 82,
    "total_pages": 9,
    "previous": null,
    "next": "https://swapi.tech/api/people?page=2&limit=10",
    "results": [...]
}
```

#### 2. **Búsqueda por Nombre** (con filtro)
```json
{
    "message": "ok",
    "result": [...]
}
```

#### 3. **Entidad Única** (por ID)
```json
{
    "message": "ok",
    "result": {
        "uid": "1",
        "properties": {...}
    }
}
```

### Adaptadores y Mappers
Para manejar estas diferencias, se crearon:

- **`ApiPageResponse<T>`**: Para respuestas paginadas
- **`ApiEntityResponse<T>`**: Para entidades únicas
- **`ApiDetailResult<T>`**: Para propiedades de entidades
- **`PageResponseDto<T>`**: Respuesta estandarizada de la aplicación

### Diferencia en Films
**Films** tiene una pequeña variación en la estructura de datos, por lo que se implementó un **mapper específico** (`FilmMapper`) que maneja estas diferencias.

### Paginación Inteligente
Cuando se busca por nombre, SWAPI devuelve **múltiples resultados sin paginar**. La aplicación:
1. **Recibe** todos los resultados
2. **Aplica** paginación manual
3. **Devuelve** respuesta paginada consistente

## Ejemplos de Uso

### Ejemplo Completo con cURL
```bash
# 1. Login y obtener token
TOKEN=$(curl -s -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}' | \
  jq -r '.token')

# 2. Buscar personajes con el nombre "Luke"
curl -X GET "http://localhost:8080/api/v1/people?name=Luke&page=1&limit=5" \
  -H "Authorization: Bearer $TOKEN"

# 3. Obtener película por ID
curl -X GET "http://localhost:8080/api/v1/films/1" \
  -H "Authorization: Bearer $TOKEN"
```

### Ejemplo con Postman
1. **Configurar Collection** con variable `{{base_url}}` = `http://localhost:8080/api/v1`
2. **Login** → `POST {{base_url}}/auth/login`
3. **Extraer token** de la respuesta
4. **Configurar Authorization** → Type: Bearer Token, Token: `{{token}}`
5. **Usar endpoints** protegidos

## 🔒 Seguridad

### Características de Seguridad
- **BCrypt** para encriptación de contraseñas
- **JWT** para autenticación stateless
- **Spring Security** para autorización
- **Validación de entrada** en todos los endpoints
- **Manejo de errores** centralizado y seguro

### ⚠️ Nota sobre JWT Secret
**Importante**: La JWT secret está configurada en `application.properties` por practicidad para este challenge técnico. En ambientes de producción, se recomienda usar variables de entorno o servicios de gestión de secretos para mayor seguridad.

### Endpoints Públicos
- `/api/v1/auth/**` - Autenticación
- `/api/v1/swagger-ui/**` - Documentación
- `/api/v1/v2/api-docs` - Especificación OpenAPI

### Endpoints Protegidos
- `/api/v1/people/**` - Gestión de personajes
- `/api/v1/films/**` - Gestión de películas
- `/api/v1/starships/**` - Gestión de naves
- `/api/v1/vehicles/**` - Gestión de vehículos

## Documentación Adicional

Para información técnica detallada, consulta:
- **[Documentación Técnica](./docs/TECHNICAL_DOCS.md)** - Arquitectura y decisiones técnicas
- **[Guía de Testing](./docs/TESTING_GUIDE.md)** - Estrategia y ejemplos de tests
- **[Guía de Despliegue](./docs/DEPLOYMENT_GUIDE.md)** - Configuración de producción

## Contribución

### Estructura de Commits
- `feat:` Nueva funcionalidad
- `fix:` Corrección de bugs
- `docs:` Documentación
- `test:` Tests
- `refactor:` Refactorización de código
- `style:` Formato de código

### Estándares de Código
- **Java**: Google Java Style Guide
- **Spring**: Spring Framework conventions
- **Testing**: Arrange-Act-Assert pattern
- **Documentación**: Javadoc para métodos públicos

## Licencia
Este proyecto es parte del **Conexa Challenge** y está diseñado para demostrar habilidades técnicas en desarrollo Java con Spring Boot.

---

## Quick Start

```bash
# Clonar y ejecutar
git clone <repository-url>
cd starwars-api
mvn spring-boot:run

# Acceder a Swagger
open http://localhost:8080/api/v1/swagger-ui/

# Login con usuario de prueba
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

**¡Que la Fuerza esté contigo!**
