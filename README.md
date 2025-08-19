# Star Wars API - Sistema de Autenticación y Gestión de Datos

## Descripción
API REST que integra con la API oficial de Star Wars ([swapi.tech](https://www.swapi.tech)) y proporciona un sistema de autenticación con JWT. La aplicación maneja cuatro entidades principales: **People** (Personajes), **Films** (Películas), **Starships** (Naves Espaciales) y **Vehicles** (Vehículos), con funcionalidades de listado paginado, búsqueda por nombre y obtención por ID.

### 🔗 **Integración con SWAPI**
Esta API se integra con [SWAPI (Star Wars API)](https://www.swapi.tech) para obtener datos oficiales de Star Wars. SWAPI es una API REST pública que proporciona información completa sobre personajes, películas, naves espaciales y vehículos del universo de Star Wars.

- **Documentación oficial**: [https://www.swapi.tech/documentation](https://www.swapi.tech/documentation)
- **Base URL**: `https://www.swapi.tech/api`
- **Datos**: Información canónica y actualizada de Star Wars

## Características Principales
- **Sistema de autenticación JWT** completo
- **Integración con SWAPI** (Star Wars API oficial) - [Ver documentación](https://www.swapi.tech/documentation)
- **Listado paginado** de todas las entidades
- **Búsqueda por nombre** con filtrado
- **Obtención por ID** con manejo de errores
- **Documentación Swagger/OpenAPI** completa
- **Base de datos H2** en memoria para usuarios
- **Tests unitarios e integración** con estrategia híbrida
- **Mensajes de error en español** para mejor UX

## 🔗 **Integración con SWAPI (Star Wars API)**

### **¿Qué es SWAPI?**
[SWAPI](https://www.swapi.tech) es una API REST pública que proporciona información completa y canónica sobre el universo de Star Wars. Es la fuente oficial de datos para personajes, películas, naves espaciales, vehículos y más.

### **Características de SWAPI**
- **Datos oficiales**: Información canónica de Star Wars
- **API REST**: Endpoints estándar y bien documentados
- **Gratuita**: Sin costos ni límites de uso
- **Actualizada**: Datos mantenidos por la comunidad

### **Documentación y Recursos**
- Documentación oficial: [https://www.swapi.tech/documentation](https://www.swapi.tech/documentation)
- Sitio web: [https://www.swapi.tech](https://www.swapi.tech)
-  Base URL: `https://www.swapi.tech/api`
- 📊 Endpoints disponibles: People, Films, Starships, Vehicles, Planets, Species

### **Cómo se integra con nuestra API**
Nuestra API actúa como un **wrapper inteligente** de SWAPI, proporcionando:
- **Autenticación JWT** para acceso controlado
- **Transformación de datos** para respuestas consistentes
- **Paginación mejorada** con parámetros personalizables
- **Búsqueda por nombre** con filtrado inteligente
- **Manejo de errores** robusto y mensajes en español

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

### Patrones de diseño y arquitectónicos aplicados
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
1. **Registro**: `POST /auth/register`
2. **Login**: `POST /auth/login`
3. **Verificación**: `GET /auth/check-username/{username}`
4. **Uso**: Incluir token en header `Authorization: Bearer {token}`

## URLs de Acceso

### 🌐 **Local Development**
- **Página de Inicio**: `http://localhost:8080/`
- **Base URL**: `http://localhost:8080`
- **Swagger UI**: `http://localhost:8080/swagger-ui/index.html`
- **H2 Console**: `http://localhost:8080/h2-console`

### 🚀 **Heroku Production**
- **Página de Inicio**: `https://conexa-starwars-api-f5c72652ce2f.herokuapp.com/`
- **Base URL**: `https://conexa-starwars-api-f5c72652ce2f.herokuapp.com`
- **Swagger UI**: `https://conexa-starwars-api-f5c72652ce2f.herokuapp.com/swagger-ui/index.html`
- **API Docs**: `https://conexa-starwars-api-f5c72652ce2f.herokuapp.com/api-docs`

## 🏠 **Página de Inicio**

### 🌟 **Acceso a la Página Principal**
Cuando accedas a la URL base de la API, encontrarás una página de inicio atractiva y funcional que incluye:

- **Información general** sobre la API
- **Enlaces directos** a Swagger UI y documentación
- **Lista de endpoints** principales con ejemplos
- **Diseño responsive** y moderno con tema Star Wars
- **Navegación intuitiva** a todas las funcionalidades

#### **URLs de Acceso:**
- **Local**: `http://localhost:8080/`
- **Heroku**: `https://conexa-starwars-api-f5c72652ce2f.herokuapp.com/`

### 📋 **Información de la API (JSON)**
También puedes obtener información de la API en formato JSON:

- **Local**: `http://localhost:8080/api`
- **Heroku**: `https://conexa-starwars-api-f5c72652ce2f.herokuapp.com/api`

Esta respuesta incluye:
- Nombre y descripción de la API
- Versión y autor
- Lista de endpoints disponibles
- Enlaces a documentación

### Desarrollo Asistido por IA
Este proyecto ha sido desarrollado utilizando **Inteligencia Artificial de forma controlada y responsable** para optimizar el proceso de desarrollo. La IA se utilizó principalmente para:
- **Generación de código estructurado** (entidades, DTOs, mappers)
- **Documentación y plantillas** (README, documentación técnica)
- **Configuraciones base** (Spring Boot, seguridad, testing)
- **Reducción de código boilerplate** y tareas repetitivas

**Importante**: Todo el código generado por IA fue revisado, validado y ajustado manualmente por mi, garantizando la calidad y corrección de la implementación. Para más detalles consultar la [Documentación Técnica](./docs/TECHNICAL_DOCS.md#-uso-de-inteligencia-artificial-en-el-desarrollo).

###  **Acceso a Swagger UI**
Swagger UI proporciona una interfaz web interactiva para probar todos los endpoints de la API de manera sencilla.

#### **URLs de Acceso:**
- **Local**: `http://localhost:8080/swagger-ui/index.html`
- **Heroku**: `https://conexa-starwars-api-f5c72652ce2f.herokuapp.com/swagger-ui/index.html`

### 🔐 **Configuración de Autenticación JWT**

#### **Paso 1: Obtener Token JWT**
1. **Accede a Swagger UI** usando una de las URLs anteriores
2. **Ve a la sección "A. Authentication"**
3. **Usa el endpoint de login o registro** para obtener un token JWT
4. **Ejemplo de login:**
   ```json
   {
     "username": "admin",
     "password": "admin123"
   }
   ```
5. **Copia el token** de la respuesta (sin incluir "Bearer ")

#### **Paso 2: Configurar Autenticación**
1. **Haz clic en el botón "Authorize" (🔒)** en la parte superior derecha
2. **Ingresa tu token JWT** en el campo "bearerAuth"
3. **Formato**: `eyJhbGciOiJIUzI1NiJ9...` (solo el token)
4. **Haz clic en "Authorize"**
5. **Cierra el modal** de autorización

#### **Paso 3: Probar Endpoints Protegidos**
1. **Ahora puedes probar** todos los endpoints protegidos
2. **El token se enviará automáticamente** en el header Authorization
3. **Swagger UI** mostrará el tiempo de respuesta de cada petición

### 📋 **Organización de Endpoints en Swagger UI**

#### **Grupos de Endpoints:**
1. **A. Authentication** - Login, registro y verificación de usuarios
2. **B. Films** - Gestión de películas de Star Wars
3. **C. People** - Gestión de personajes de Star Wars
4. **D. Starships** - Gestión de naves espaciales
5. **E. Vehicles** - Gestión de vehículos

#### **Características de la UI:**
- **Endpoints colapsados** por defecto para mejor organización
- **Ordenamiento alfabético** de grupos y operaciones
- **Tiempo de respuesta** visible para cada petición
- **Validación automática** de esquemas de entrada
- **Respuestas de ejemplo** para mejor comprensión

### 🧪 **Ejemplos de Uso con Swagger UI**

#### **Ejemplo 1: Autenticación Completa**
1. **Registra un nuevo usuario** usando `/auth/register`
2. **Obtén el token JWT** de la respuesta
3. **Configura la autenticación** con el botón Authorize
4. **Prueba endpoints protegidos** como `/people` o `/films`

#### **Ejemplo 2: Búsqueda de Personajes**
1. **Asegúrate de estar autenticado**
2. **Ve a la sección "C. People"**
3. **Expande el endpoint GET `/people`**
4. **Configura parámetros opcionales:**
   - `name`: Filtro por nombre (ej: "Luke")
   - `page`: Número de página (default: 1)
   - `limit`: Resultados por página (default: 10)
5. **Haz clic en "Try it out"**
6. **Ejecuta la petición** y revisa la respuesta

#### **Ejemplo 3: Obtención por ID**
1. **Selecciona un endpoint** como GET `/people/{id}`
2. **Ingresa un ID válido** (ej: "1")
3. **Ejecuta la petición** para obtener detalles completos

### 🔧 **Solución de Problemas Comunes**

#### **Error 401 (Unauthorized):**
- **Verifica** que hayas configurado el token JWT
- **Asegúrate** de que el token no haya expirado
- **Revisa** que el token esté en el formato correcto

#### **Error 403 (Forbidden):**
- **Verifica** que el token sea válido
- **Verifica** que el token no haya caducado

#### **Error 404 (Not Found):**
- **Revisa** que la URL del endpoint sea correcta
- **Verifica** que el ID del recurso exista

#### **Error 500 (Internal Server Error):**
- **Revisa** los logs del servidor
- **Verifica** que los parámetros de entrada sean válidos

### 💡 **Consejos para Mejor Experiencia**

1. **Usa los usuarios de prueba**: `admin/admin123`, `user/user123`, `test/test123`
2. **Revisa las respuestas**: Swagger UI muestra el esquema completo de respuestas
3. **Experimenta con parámetros**: Prueba diferentes valores para entender mejor la API
4. **Usa la documentación**: Cada endpoint tiene descripción detallada y ejemplos

---

## Endpoints de la API

### Autenticación

#### 1. Registro de Usuario

**Local:**
```http
POST http://localhost:8080/auth/register
Content-Type: application/json

{
    "username": "nuevo_usuario",
    "password": "mi_contraseña",
    "confirmPassword": "mi_contraseña"
}
```

**Heroku:**
```http
POST https://conexa-starwars-api-f5c72652ce2f.herokuapp.com/auth/register
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
POST http://localhost:8080/auth/login
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
GET http://localhost:8080/auth/check-username/{username}
```

**Respuesta:**
- `true` si el username está disponible
- `false` si ya está en uso

###  People (Personajes)

#### Listado Paginado

**Local:**
```http
GET http://localhost:8080/people?page=1&limit=10
GET http://localhost:8080/people?page=2&limit=5
```

**Heroku:**
```http
GET https://conexa-starwars-api-f5c72652ce2f.herokuapp.com/people?page=1&limit=10
GET https://conexa-starwars-api-f5c72652ce2f.herokuapp.com/people?page=2&limit=5
```

#### Búsqueda por Nombre
```http
GET http://localhost:8080/people?name=skywalker
GET http://localhost:8080/people?name=L
GET http://localhost:8080/people?name=r2
```

#### Obtener por ID
```http
GET http://localhost:8080/people/1
```

###  Films (Películas)

#### Listado Paginado
```http
GET http://localhost:8080/films?page=1&limit=10
GET http://localhost:8080/films?page=1&limit=3
```

#### Búsqueda por Título
```http
GET http://localhost:8080/films?title=hope
GET http://localhost:8080/films?title=jedi
```

#### Obtener por ID
```http
GET http://localhost:8080/films/1
```

### Starships (Naves Espaciales)

#### Listado Paginado
```http
GET http://localhost:8080/starships
GET http://localhost:8080/starships?page=2
```

#### Búsqueda por Nombre
```http
GET http://localhost:8080/starships?name=destroyer
GET http://localhost:8080/starships?name=falcon
```

#### Obtener por ID
```http
GET http://localhost:8080/starships/9
GET http://localhost:8080/starships/10
```

###  Vehicles (Vehículos)

#### Listado Paginado
```http
GET http://localhost:8080/vehicles
GET http://localhost:8080/vehicles?page=1&limit=5
```

#### Búsqueda por Nombre
```http
GET http://localhost:8080/vehicles?name=speeder
GET http://localhost:8080/vehicles?name=crawler
```

#### Obtener por ID
```http
GET http://localhost:8080/vehicles/4
```

## Configuración y Ejecución

### Requisitos
- Java 8 o superior
- Maven 3.6+
- Conexión a internet (para SWAPI)

### Configuración
- **Puerto**: 8080
- **Context Path**: ``
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
    "path": "/people/999"
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
TOKEN=$(curl -s -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}' | \
  jq -r '.token')

# 2. Buscar personajes con el nombre "Luke"
curl -X GET "http://localhost:8080/people?name=Luke&page=1&limit=5" \
  -H "Authorization: Bearer $TOKEN"

# 3. Obtener película por ID
curl -X GET "http://localhost:8080/films/1" \
  -H "Authorization: Bearer $TOKEN"
```

### Ejemplo con Postman
1. **Configurar Collection** con variable `{{base_url}}` = `http://localhost:8080`
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

### Endpoints Públicos
- `/auth/**` - Autenticación
- `/swagger-ui/**` - Documentación
- `/v2/api-docs` - Especificación OpenAPI

### Endpoints Protegidos
- `/people/**` - Gestión de personajes
- `/films/**` - Gestión de películas
- `/starships/**` - Gestión de naves
- `/vehicles/**` - Gestión de vehículos

## Documentación Adicional

Para información técnica detallada, consulta:
- **[Documentación Técnica](./docs/TECHNICAL_DOCS.md)** - Arquitectura y decisiones técnicas
- **[Guía de Testing](./docs/TESTING_GUIDE.md)** - Estrategia y ejemplos de tests
## Licencia
Este proyecto es parte del **Conexa Challenge** y está diseñado para demostrar habilidades técnicas en desarrollo Java con Spring Boot.

---

## Quick Start

```bash
# Clonar y ejecutar
git clone https://github.com/maxif96/conexa-starwars-challenge
cd starwars-api
mvn spring-boot:run
```

**¡Que la Fuerza esté contigo!**
