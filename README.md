# Star Wars API - Sistema de Autenticación y Gestión de Datos

## Para el equipo técnico de Conexa que revisará el challenge
Primero que nada, un gusto, deseo que se encuentren bien. Ahora, me gustaría aclarar una pequeña cuestión: Entregué el proyecto en tiempo y forma el día Viernes, 15 de Agosto, pero a mi parecer, el proyecto todavía tenía detalles que pulir, por lo que fui editando el código, la arquitectura, la seguridad, etc, para dejar un proyecto final con las mejores prácticas posibles. Es por eso que podrían notar nuevos commits. El proyecto entregado el Viernes podría ser la base para la evaluación, y ya dependerá de ustedes si quieren tener en cuenta los cambios realizados post esa fecha. Mi aclaración va más que nada porque no quiero que se malinterpreten las mejoras posteriores que realicé; estaba con tiempo libre y me sentía un poco insatisfecho con ciertos detalles. Sin más que decir, espero que la navegación a través del código sea de facil entendimiento. Muchas gracias.

## Update 21/08/2025
Para evitar cargos por parte de Heroku, se migró el deployment a Railway, por lo que cualquier url de Heroku ya no funcionará. La siguiente documentación fue actualizada y contiene las urls al deploy en Railways.

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
- **JUnit 5 + Mockito** para testing
- **WireMock** para tests de integración

## Arquitectura del Proyecto

### Estructura de Directorios (Por Funcionalidad)
```
src/main/java/com/starwars/
├── auth/           # Funcionalidad de autenticación
│   ├── controller/ # Controlador de autenticación
│   ├── service/    # Servicios de autenticación
│   ├── dto/        # DTOs de autenticación
│   ├── entity/     # Entidad User
│   └── repository/ # Repositorio de usuarios
├── people/         # Funcionalidad de personajes
│   ├── controller/ # Controlador de personajes
│   ├── service/    # Servicio de personajes
│   ├── dto/        # DTOs de personajes
│   ├── mapper/     # Mapeador de personajes
│   └── repository/ # Repositorio de personajes
├── films/          # Funcionalidad de películas
│   ├── controller/ # Controlador de películas
│   ├── service/    # Servicio de películas
│   ├── dto/        # DTOs de películas
│   ├── mapper/     # Mapeador de películas
│   └── repository/ # Repositorio de películas
├── starships/      # Funcionalidad de naves espaciales
│   ├── controller/ # Controlador de naves
│   ├── service/    # Servicio de naves
│   ├── dto/        # DTOs de naves
│   ├── mapper/     # Mapeador de naves
│   └── repository/ # Repositorio de naves
├── vehicles/       # Funcionalidad de vehículos
│   ├── controller/ # Controlador de vehículos
│   ├── service/    # Servicio de vehículos
│   ├── dto/        # DTOs de vehículos
│   ├── mapper/     # Mapeador de vehículos
│   └── repository/ # Repositorio de vehículos
└── shared/         # Componentes compartidos
    ├── config/     # Configuraciones (Security, OpenAPI)
    ├── controller/ # Controladores compartidos (Home)
    ├── dto/        # DTOs compartidos (PageResponse, API base)
    ├── exception/  # Manejo centralizado de excepciones
    ├── mapper/     # Mapeadores compartidos
    ├── security/   # Configuración de seguridad JWT
    └── service/    # Servicios base compartidos
```

### Patrones de diseño y arquitectónicos aplicados
- **DTO Pattern**: Separación entre datos de API externa y respuesta interna
- **Mapper Pattern**: Conversión automática entre DTOs usando MapStruct
- **Repository Pattern**: Abstracción de acceso a datos
- **Service Layer**: Lógica de negocio centralizada
- **Global Exception Handler**: Manejo centralizado de errores

### Estructura por Funcionalidad (Feature-Based Architecture)
- **Organización Vertical**: Cada funcionalidad principal tiene su propio paquete autocontenido
- **Cohesión Alta**: Todos los componentes relacionados están agrupados lógicamente
- **Bajo Acoplamiento**: Las funcionalidades son independientes entre sí
- **Escalabilidad**: Fácil agregar nuevas funcionalidades sin afectar las existentes
- **Mantenibilidad**: Código más fácil de navegar y entender
- **Migración a Microservicios**: Estructura preparada para futuras migraciones

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

### 🚀 **Railway Production**
- **Página de Inicio**: `https://web-production-e48ff.up.railway.app/`
- **Base URL**: `https://web-production-e48ff.up.railway.app/`
- **Swagger UI**: `https://web-production-e48ff.up.railway.app/swagger-ui/index.html`
- **API Docs**: `https://web-production-e48ff.up.railway.app/api-docs`

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
- **Railway**: `https://web-production-e48ff.up.railway.app/`

### 📋 **Información de la API (JSON)**
También puedes obtener información de la API en formato JSON:

- **Local**: `http://localhost:8080/api`
- **Railway**: `https://web-production-e48ff.up.railway.app/api`

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
- **RailWay**: `https://web-production-e48ff.up.railway.app/swagger-ui/index.html`

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

Nota: Si estás probando en local, verifica que el server seleccionado sea el de local y no producción: 

<img width="633" height="128" alt="image" src="https://github.com/user-attachments/assets/67213a4c-8968-4f5a-b5b9-a1315dd363b3" />


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

**RailWay:**
```http
POST https://web-production-e48ff.up.railway.app//auth/register
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

**Railway:**
```http
GET https://web-production-e48ff.up.railway.app/people?page=1&limit=10
GET https://web-production-e48ff.up.railway.app/people?page=2&limit=5
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

**Railway Production:**
- **Swagger UI**: `https://web-production-e48ff.up.railway.app/swagger-ui.html`
- **API Docs**: `https://web-production-e48ff.up.railway.app/api-docs`
- **H2 Console**: No disponible en producción

## Testing

### Estrategia de Testing Híbrida
El proyecto implementa una estrategia de testing híbrida que combina:

#### Tests Unitarios (`src/test/java/com/starwars/service/`)
- **Objetivo**: Probar lógica de negocio en aislamiento
- **Tecnología**: JUnit 5 + Mockito
- **Características**: Rápidos, sin contexto Spring, mocks de dependencias
- **Ubicación**: `*ServiceUnitTest.java`

#### Tests de Integración (`src/test/java/com/starwars/controller/`)
- **Objetivo**: Probar flujo completo de controladores
- **Tecnología**: JUnit 5 + Spring Boot Test + WireMock
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

## Seguridad

### Características de Seguridad
- **BCrypt con fuerza 12** para encriptación de contraseñas
- **JWT con algoritmo HS512** para autenticación stateless
- **Spring Security 6.x** para autorización (configuración moderna)
- **Validación robusta de JWT** con issuer validation
- **Manejo seguro de errores** de autenticación
- **Configuración por perfiles** (desarrollo vs producción)

### Configuración por Perfiles

#### **Desarrollo Local**
```properties
# application-dev.properties
jwt.secret=dev-secret-key-2024-very-long-and-secure-for-development-only-minimum-32-chars
jwt.expiration=86400000
jwt.issuer=starwars-api-dev
```

#### **Producción**
```properties
# application-prod.properties
jwt.secret=${JWT_SECRET}  # DEBE venir de variable de entorno (Actualmente ya seteadas en Railway)
jwt.expiration=${JWT_EXPIRATION:86400000}
jwt.issuer=${JWT_ISSUER:starwars-api}
```

### Configuración en IntelliJ

#### **Opción 1: Perfil de Desarrollo (Recomendado)**
1. **Run/Debug Configurations** → **Edit Configurations**
2. **VM options**: `-Dspring.profiles.active=dev`
3. **Apply** y **Run**

#### **Opción 2: Variables de Entorno**
1. **Run/Debug Configurations** → **Edit Configurations**
2. **Environment variables**:
   ```
   JWT_SECRET=dev-secret-key-2024-very-long-and-secure-for-development-only-minimum-32-chars
   JWT_EXPIRATION=86400000
   JWT_ISSUER=starwars-api-dev
   ```

### Endpoints Públicos
- `/` - Página de inicio
- `/api` - Información de la API
- `/auth/**` - Autenticación
- `/swagger-ui/**` - Documentación Swagger
- `/api-docs/**` - Especificación OpenAPI
- `/h2-console/**` - Consola H2 (solo desarrollo)

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
