# Documentación Técnica - Star Wars API

## Índice
1. [Arquitectura General](#arquitectura-general)
2. [Arquitectura Monolítica: Justificación y Beneficios](#arquitectura-monolítica-justificación-y-beneficios)
3. [Patrones de Diseño](#patrones-de-diseño)
4. [Estructura de DTOs](#estructura-de-dtos)
5. [Manejo de SWAPI](#manejo-de-swapi)
6. [Sistema de Autenticación](#sistema-de-autenticación)
7. [Manejo de Excepciones](#manejo-de-excepciones)
8. [Estrategia de Testing](#estrategia-de-testing)
9. [Configuraciones](#configuraciones)
10. [Decisiones Técnicas](#decisiones-técnicas)

---

## Arquitectura General

### Diagrama de Arquitectura
```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Cliente      │    │   Spring Boot   │    │   SWAPI        │
│   (Postman,    │◄──►│   Application   │◄──►│   (swapi.tech) │
│   cURL, etc.)  │    │                 │    │                 │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                                │
                                ▼
                       ┌─────────────────┐
                       │   H2 Database  │
                       │   (Users)      │
                       └─────────────────┘
```

### Capas de la Aplicación
1. **Controller Layer**: Maneja requests HTTP y respuestas
2. **Service Layer**: Lógica de negocio y transformación de datos
3. **Repository Layer**: Acceso a datos locales (usuarios)
4. **External API Layer**: Integración con SWAPI
5. **Security Layer**: Autenticación y autorización JWT

---

## Arquitectura Monolítica: Justificación y Beneficios

### ¿Por qué Arquitectura Monolítica?

#### 1. Simplicidad y Desarrollo Rápido
- **Desarrollo inicial más rápido**: No hay necesidad de configurar microservicios, service discovery, o gateways
- **Debugging simplificado**: Todo el código está en un solo lugar, facilitando la identificación y resolución de problemas
- **Menor complejidad operacional**: Un solo servicio para desplegar, monitorear y mantener

#### 2. Tamaño y Escala del Proyecto
- **Proyecto de tamaño mediano**: Para el alcance actual (4 entidades principales + autenticación), la complejidad de microservicios sería excesiva

#### 3. Integración con APIs Externas
- **SWAPI como fuente única**: La integración con [SWAPI](https://www.swapi.tech) es directa y no requiere coordinación entre múltiples servicios
- **Transformación de datos centralizada**: Los mappers y DTOs están en un solo lugar, facilitando la consistencia

#### 4. Base de Datos Simple
- **H2 en memoria**: Para usuarios y autenticación, una base de datos simple es suficiente
- **Sin necesidad de transacciones distribuidas**: Todas las operaciones están en el mismo contexto transaccional

### Ventajas de la Arquitectura Monolítica

#### Beneficios Técnicos
- **Despliegue simple**: Un solo JAR/WAR file
- **Testing más fácil**: Tests de integración sin mocks de servicios externos

#### Beneficios Operacionales
- **Monitoreo centralizado**: Logs y métricas en un solo lugar
- **Escalado horizontal simple**: Múltiples instancias del mismo servicio
- **Mantenimiento**: Actualizaciones y parches en un solo componente

#### Beneficios de Desarrollo
- **Código compartido**: Utilidades y helpers accesibles desde cualquier parte
- **Refactoring más fácil**: Cambios que afectan múltiples capas en una sola operación
- **Dependencias**: Gestión simplificada de librerías y versiones

### Cuándo Considerar Microservicios
- En caso de que se prevea una expansión de la aplicación sería conveniente evaluar la migración a microservicios

#### Estrategia de Migración Futura:
Si en el futuro se requiere migrar a microservicios, la arquitectura actual facilita esta transición:
- **Separación clara de capas**: Controller, Service, Repository ya están bien definidos
- **DTOs independientes**: Los objetos de transferencia están desacoplados de la implementación
- **Servicios cohesivos**: Cada servicio tiene responsabilidades bien definidas

### Arquitectura Actual vs. Alternativas

| Aspecto | Monolítica (Actual) | Microservicios | Serverless |
|---------|---------------------|----------------|------------|
| **Complejidad** | Baja | Alta | Media |
| **Time-to-market** | Rápido | Lento | Rápido |
| **Escalabilidad** | Vertical | Horizontal | Automática |
| **Mantenimiento** | Simple | Complejo | Simple |
| **Testing** | Fácil | Complejo | Fácil |
| **Debugging** | Simple | Complejo | Simple |

### Conclusión
Para el **Challenge Técnico Conexa** y el alcance actual del proyecto, la arquitectura monolítica es la elección más apropiada porque:
1. **Permite desarrollo rápido** y entrega de valor
2. **Mantiene la simplicidad** operacional
3. **Facilita el mantenimiento** y debugging
4. **Proporciona una base sólida** para futuras evoluciones
5. **Se alinea con las mejores prácticas** para proyectos de este tamaño

---

## Patrones de diseño y arquitectónicos aplicados

### 1. **DTO Pattern (Data Transfer Object)**
**Propósito**: Separar la representación de datos externos (SWAPI) de la respuesta interna de la aplicación.

**Implementación**:
- **`Api*Dto`**: Representan la estructura exacta de SWAPI
- **`*ResponseDto`**: Representan la respuesta estandarizada de la aplicación
- **`*RequestDto`**: Representan las solicitudes de entrada

**Beneficios**:
- Desacoplamiento entre API externa e interna
- Flexibilidad para cambios en SWAPI
- Validación independiente de cada capa

### 2. **Mapper Pattern**
**Propósito**: Conversión automática entre diferentes representaciones de datos.

**Implementación**: MapStruct con interfaces `*Mapper`
```java
@Mapper(componentModel = "spring")
public interface PersonMapper {
    @Mapping(source = "uid", target = "id")
    @Mapping(source = "properties.name", target = "name")
    PersonResponseDto toResponseDtoFromDetail(ApiResult<PersonApiDto> apiResult);
}
```

**Beneficios**:
- Conversión automática en tiempo de compilación
- Código limpio y mantenible
- Performance optimizada

### 3. **Repository Pattern**
**Propósito**: Abstraer el acceso a datos y proporcionar una interfaz consistente.

**Implementación**: Interfaces que extienden `JpaRepository`
```java
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
```

### 4. **Service Layer Pattern**
**Propósito**: Encapsular la lógica de negocio y coordinar operaciones.

**Implementación**: Servicios que implementan la lógica de transformación y paginación.

### 5. **Global Exception Handler**
**Propósito**: Manejo centralizado y consistente de errores.

**Implementación**: `@RestControllerAdvice` con métodos auxiliares para reducir duplicación.

---

## Estructura de DTOs

### Jerarquía de DTOs

#### **API DTOs** (Respuestas de SWAPI)
```
ApiPageResponse<T>           # Listado paginado
├── message: String
├── total_records: Integer
├── total_pages: Integer
├── previous: String
├── next: String
└── results: List<ApiDetailResult<T>>

ApiEntityResponse<T>         # Entidad única
├── message: String
└── result: T

ApiDetailResult<T>           # Detalle de entidad
├── uid: String
└── properties: T

T extends BaseApiDto         # Propiedades específicas de cada entidad
```

#### **Response DTOs** (Respuestas de la aplicación)
```
PageResponseDto<T>           # Respuesta paginada estandarizada
├── content: List<T>
├── totalPages: int
├── totalElements: long
├── page: int
├── size: int
├── first: boolean
├── last: boolean
├── hasNext: boolean
└── hasPrevious: boolean

T extends BaseResponseDto    # Respuesta específica de cada entidad
```

### Mapeo de Entidades

#### **People (Personajes)**
```java
// SWAPI → Aplicación
ApiResult<PersonApiDto> → PersonResponseDto

// Campos mapeados
uid → id
properties.name → name
properties.height → height
properties.mass → mass
properties.hairColor → hairColor
properties.skinColor → skinColor
properties.eyeColor → eyeColor
properties.birthYear → birthYear
properties.gender → gender
properties.homeworld → homeworld
```

#### **Films (Películas)**
```java
// SWAPI → Aplicación
ApiResult<FilmApiDto> → FilmResponseDto

// Campos mapeados
uid → id
properties.title → title
properties.episodeId → episodeId
properties.openingCrawl → openingCrawl
properties.director → director
properties.producer → producer
properties.releaseDate → releaseDate
```

#### **Starships (Naves Espaciales)**
```java
// SWAPI → Aplicación
ApiResult<StarshipApiDto> → StarshipResponseDto

// Campos mapeados
uid → id
properties.name → name
properties.model → model
properties.manufacturer → manufacturer
properties.costInCredits → costInCredits
properties.length → length
properties.crew → crew
properties.passengers → passengers
properties.starshipClass → starshipClass
```

#### **Vehicles (Vehículos)**
```java
// SWAPI → Aplicación
ApiResult<VehicleApiDto> → VehicleResponseDto

// Campos mapeados
uid → id
properties.name → name
properties.model → model
properties.manufacturer → manufacturer
properties.costInCredits → costInCredits
properties.length → length
properties.crew → crew
properties.passengers → passengers
properties.vehicleClass → vehicleClass
```

---

## Manejo de SWAPI

### Desafíos Identificados

#### **1. Formatos de Respuesta Variables**
SWAPI devuelve **tres estructuras diferentes** según el contexto:

**Listado Paginado** (sin filtro):
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

**Búsqueda por Nombre** (con filtro):
```json
{
    "message": "ok",
    "result": [...]
}
```

**Entidad Única** (por ID):
```json
{
    "message": "ok",
    "result": {
        "uid": "1",
        "properties": {...}
    }
}
```

#### **2. Paginación Inconsistente**
- **Listado general**: Paginado por SWAPI
- **Búsqueda por nombre**: Sin paginación, múltiples resultados
- **Entidad única**: Sin paginación

### Soluciones Implementadas

#### **1. DTOs Adaptadores**
- **`ApiPageResponse<T>`**: Maneja respuestas paginadas
- **`ApiEntityResponse<T>`**: Maneja entidades únicas
- **`ApiDetailResult<T>`**: Maneja propiedades de entidades

#### **2. Paginación Inteligente**
```java
// En BaseStarWarsService
protected <T> PageResponseDto<T> createPaginatedResponse(
    List<T> content, 
    int page, 
    int limit, 
    long totalElements
) {
    int totalPages = (int) Math.ceil((double) totalElements / limit);
    
    return PageResponseDto.<T>builder()
        .content(content)
        .totalPages(totalPages)
        .totalElements(totalElements)
        .page(page)
        .size(limit)
        .first(page == 1)
        .last(page >= totalPages)
        .hasNext(page < totalPages)
        .hasPrevious(page > 1)
        .build();
}
```

#### **3. Mappers Específicos**
Cada entidad tiene su propio mapper que maneja las particularidades:
- **`PersonMapper`**: Mapeo estándar
- **`FilmMapper`**: Mapeo con variaciones específicas
- **`StarshipMapper`**: Mapeo estándar
- **`VehicleMapper`**: Mapeo estándar

---

## Sistema de Autenticación

### Arquitectura de Seguridad

#### **Componentes Principales**
1. **`SecurityConfig`**: Configuración de Spring Security
2. **`JwtRequestFilter`**: Filtro para validar tokens JWT
3. **`JwtUtil`**: Utilidades para generar y validar JWT
4. **`UserDetailsServiceImpl`**: Servicio de detalles de usuario
5. **`UserService`**: Lógica de negocio para usuarios

#### **Flujo de Autenticación**
```
1. Cliente → POST /auth/login
2. AuthController → AuthenticationManager.authenticate()
3. UserDetailsServiceImpl.loadUserByUsername()
4. Validación de credenciales
5. Generación de JWT
6. Respuesta con token
```

#### **Flujo de Autorización**
```
1. Cliente → Request con Authorization: Bearer {token}
2. JwtRequestFilter → Extrae y valida token
3. Spring Security → Crea Authentication
4. Controller → Procesa request
```

### Configuración de Seguridad

#### **Arquitectura Moderna**
- **Spring Security 6.x** compatible (eliminado WebSecurityConfigurerAdapter deprecado)
- **SecurityFilterChain** para configuración moderna
- **AntPathRequestMatcher** para patrones de URL

#### **Endpoints Públicos**
```java
.requestMatchers(
    new AntPathRequestMatcher("/"),
    new AntPathRequestMatcher("/api"),
    new AntPathRequestMatcher("/auth/**"),
    new AntPathRequestMatcher("/swagger-ui/**"),
    new AntPathRequestMatcher("/swagger-ui.html"),
    new AntPathRequestMatcher("/api-docs/**"),
    new AntPathRequestMatcher("/v3/api-docs/**"),
    new AntPathRequestMatcher("/swagger-resources/**"),
    new AntPathRequestMatcher("/webjars/**"),
    new AntPathRequestMatcher("/h2-console/**")
).permitAll()
```

#### **Endpoints Protegidos**
```java
.anyRequest().authenticated()
```

#### **Configuración JWT Mejorada**
```properties
# Desarrollo (application-dev.properties)
jwt.secret=dev-secret-key-2024-very-long-and-secure-for-development-only-minimum-32-chars
jwt.expiration=86400000
jwt.issuer=starwars-api-dev

# Producción (application-prod.properties)
jwt.secret=${JWT_SECRET}  # Variable de entorno obligatoria
jwt.expiration=${JWT_EXPIRATION:86400000}
jwt.issuer=${JWT_ISSUER:starwars-api}
```

#### **Mejoras de Seguridad Implementadas**
- **Algoritmo HS512** en lugar de HS256
- **Validación de issuer** en tokens JWT
- **BCrypt con fuerza 12** para contraseñas
- **Manejo robusto de errores** de autenticación
- **JwtAuthenticationEntryPoint** para respuestas seguras
- **Sin secretos hardcoded** en el código

#### **Configuración en IntelliJ**

##### **Opción 1: Perfil de Desarrollo (Recomendado)**
1. **Run/Debug Configurations** → **Edit Configurations**
2. **VM options**: `-Dspring.profiles.active=dev`
3. **Apply** y **Run**

##### **Opción 2: Variables de Entorno**
1. **Run/Debug Configurations** → **Edit Configurations**
2. **Environment variables**:
   ```
   JWT_SECRET=dev-secret-key-2024-very-long-and-secure-for-development-only-minimum-32-chars
   JWT_EXPIRATION=86400000
   JWT_ISSUER=starwars-api-dev
   ```

#### **Ejecución desde Terminal**

##### **Desarrollo Local**
```bash
# Usar perfil de desarrollo
./mvnw spring-boot:run -Dspring.profiles.active=dev
```

##### **Producción**
```bash
# Definir JWT_SECRET obligatoriamente
export JWT_SECRET="tu-clave-super-secreta-de-al-menos-32-caracteres"
./mvnw spring-boot:run -Dspring.profiles.active=prod
```

---

## Manejo de Excepciones

### Estrategia de Manejo

#### **1. Excepciones Personalizadas**
- **`ResourceNotFoundException`**: Recurso no encontrado
- **`BadCredentialsException`**: Credenciales incorrectas
- **`AccessDeniedException`**: Acceso denegado

#### **2. Global Exception Handler**
```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    // Métodos auxiliares para reducir duplicación
    private ErrorResponse buildErrorResponse(HttpStatus status, String error, 
                                          String message, WebRequest request)
    
    // Manejo específico de cada tipo de excepción
    @ExceptionHandler(ResourceNotFoundException.class)
    @ExceptionHandler(BadCredentialsException.class)
    @ExceptionHandler(AccessDeniedException.class)
    // ... otros handlers
}
```

#### **3. Respuestas Estandarizadas**
```json
{
    "timestamp": "2024-01-15T10:30:00",
    "status": 404,
    "error": "No Encontrado",
    "message": "Person no encontrado con id : '999'",
    "path": "/people/999"
}
```

---

## Estrategia de Testing

### Arquitectura Híbrida

#### **Tests Unitarios**
**Ubicación**: `src/test/java/com/starwars/service/`
**Objetivo**: Probar lógica de negocio en aislamiento
**Tecnología**: JUnit 5 (Jupiter) + Mockito

**Características**:
- Rápidos (sin contexto Spring)
- Aislamiento completo
- Mocks de dependencias
- Foco en lógica de negocio

**Ejemplo**:
```java
@ExtendWith(MockitoExtension.class)
public class PersonServiceUnitTest {
    @Mock private PersonMapper personMapper;
    @InjectMocks private PersonService personService;
    
    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(personService, "baseUrl", "https://swapi.tech/api");
    }
    
    @Test
    public void getPersonById_ValidId_ShouldReturnPerson() {
        // Arrange
        // Act
        // Assert
    }
}
```

#### **Tests de Integración**
**Ubicación**: `src/test/java/com/starwars/controller/`
**Objetivo**: Probar flujo completo de controladores
**Tecnología**: JUnit 5 (Jupiter) + Spring Boot Test + WireMock

**Características**:
- Contexto Spring completo
- Simulación de API externa
- Testing de flujos reales
- Validación de respuestas HTTP

**Ejemplo**:
```java
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = StarWarsApplication.class)
@ActiveProfiles("test")  // Activa perfil de test
public class PeopleControllerIntegrationTest {
    @Autowired private WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;
    private WireMockServer wireMockServer;
    
    @Test
    public void listPeople_WithoutNameFilter_ShouldReturnPaginatedResults() {
        // Configurar WireMock
        // Ejecutar request
        // Validar respuesta
    }
}
```

### Ventajas de la Estrategia Híbrida

1. **Velocidad**: Tests unitarios rápidos para desarrollo
2. **Cobertura**: Tests de integración para validar flujos completos
3. **Mantenibilidad**: Separación clara de responsabilidades
4. **Flexibilidad**: Ejecutar solo tests unitarios o de integración

### Configuración de Tests

#### **Perfil de Test**
```properties
# src/test/resources/application-test.properties
# Configuración de base de datos para tests
spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1
spring.datasource.driverClassName=org.h2.Driver
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=create-drop

# Configuración de JWT para tests
JWT_SECRET=testSecretKeyForTestingPurposesOnly12345678901234567890
JWT_EXPIRATION=86400000

# Configuración de la API externa para tests
swapi.api.base-url=http://localhost:9999/api
```

#### **Activación del Perfil**
```java
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = StarWarsApplication.class)
@ActiveProfiles("test")  // Activa el perfil de test
public class ControllerIntegrationTest {
    // ...
}
```

---

## Configuraciones

### Application Properties

#### **Configuración de Base de Datos**
```properties
# H2 Database
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# JPA Configuration
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=create
spring.jpa.defer-datasource-initialization=true

# Data Initialization
spring.sql.init.mode=always
spring.sql.init.schema-locations=classpath:schema.sql
spring.sql.init.data-locations=classpath:data.sql
```

#### **Configuración de SWAPI**
```properties
# SWAPI Base URL
swapi.api.base-url=https://swapi.tech/api
```

#### **Configuración de JWT**
```properties
# JWT Configuration
jwt.secret=starwars-api-secret-key-2024-very-secure-and-long-secret-key-for-jwt-signing
jwt.expiration=86400000
```

#### **Configuración del Servidor**
```properties
# Server Configuration
server.port=8080
```

### Configuraciones de Spring

#### **SecurityConfig**
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    // Configuración de autenticación
    // Configuración de autorización
    // Configuración de JWT
}
```

#### **OpenApiConfig**
```java
@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        // Configuración de Swagger/OpenAPI
    }
}
```

---

## Decisiones Técnicas

### 1. **Spring Boot 2.7.18 vs 3.x**
**Decisión**: Spring Boot 2.7.18
**Razones**:
- Compatibilidad con Java 8
- Estabilidad y madurez
- Amplia documentación y soporte

### 2. **H2 vs PostgreSQL/MySQL**
**Decisión**: H2 en memoria
**Razones**:
- Simplicidad para desarrollo y testing
- No requiere configuración externa
- Ideal para demostración y pruebas

### 3. **MapStruct vs ModelMapper**
**Decisión**: MapStruct
**Razones**:
- Generación de código en tiempo de compilación
- Mejor performance
- Código más limpio y mantenible


### 4. **Estrategia de Testing Híbrida**
**Decisión**: Combinar tests unitarios y de integración
**Razones**:
- Tests unitarios rápidos para desarrollo
- Tests de integración para validar flujos completos
- Mejor cobertura y confiabilidad

---

## Métricas y Performance

### Cobertura de Tests
- **Tests Unitarios**: 6 clases (Person, Film, Starship, Vehicle, User, UserDetails)
- **Tests de Integración**: 5 clases (People, Films, Starships, Vehicles, Auth)
- **Cobertura Total (Servicios y controladores)**: ~90-95%

  <img width="408" height="268" alt="image" src="https://github.com/user-attachments/assets/1f9bcdea-24e3-45be-a601-aaf19a6d6974" />


### Escalabilidad
- **Rate limiting**: No implementado (se puede agregar)
- **Caching**: No implementado (se puede agregar con Redis)

---


## Referencias y Recursos

### Documentación Oficial
- [Spring Boot 2.7.x Reference](https://docs.spring.io/spring-boot/docs/2.7.18/reference/html/)
- [Spring Security Reference](https://docs.spring.io/spring-security/reference/)
- [MapStruct Documentation](https://mapstruct.org/documentation/stable/reference/html/)
- [OpenAPI Specification](https://swagger.io/specification/)
- [Swapi Documentation](https://www.swapi.tech/documentation)

### Mejores Prácticas
- [Spring Framework Best Practices](https://spring.io/guides)
- [JWT Best Practices](https://auth0.com/blog/a-look-at-the-latest-draft-for-jwt-bcp/)
- [REST API Design Best Practices](https://restfulapi.net/rest-api-design-tutorial-with-example/)

### Herramientas de Testing
- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [WireMock Documentation](http://wiremock.org/docs/)

---

## Uso de Inteligencia Artificial en el Desarrollo

### Enfoque Responsable y Controlado

Este proyecto ha aprovechado las capacidades de **Inteligencia Artificial Generativa (IA)** de manera **controlada y responsable** para optimizar el proceso de desarrollo, siguiendo las mejores prácticas de la industria.

#### Cómo se Utilizó la IA

##### 1. Generación de Código Estructurado
- **Entidades y DTOs**: Creación de clases base siguiendo patrones establecidos
- **Mappers**: Generación de interfaces MapStruct con mapeos estándar
- **Tests unitarios**: Estructura base de tests siguiendo convenciones JUnit
- **Documentación**: Generación de plantillas y estructura de archivos

##### 2. Documentación y Comentarios
- **README.md**: Estructura y organización del contenido
- **TECHNICAL_DOCS.md**: Plantillas de secciones técnicas
- **Comentarios de código**: Documentación inline siguiendo estándares JavaDoc
- **Guías de usuario**: Instrucciones claras y estructuradas

##### 3. Optimización de Tiempo
- **Boilerplate code**: Reducción de código repetitivo
- **Configuraciones**: Plantillas de configuración Spring Boot
- **Estructura de directorios**: Organización de paquetes y archivos
- **Patrones de diseño**: Implementación de patrones arquitectónicos

#### Control y Validación

##### Revisión Humana Obligatoria
- **Todo el código generado** es revisado, corregido y validado por mi
- **Lógica de negocio** implementada manualmente
- **Tests** corregidos y ejecutados para validar funcionalidad
- **Documentación** revisada para precisión y claridad

##### Patrones y Estándares
- **Arquitectura monolítica** diseñada y validada manualmente
- **Configuración de seguridad** revisada y ajustada manualmente
- **Integración con SWAPI** implementada con lógica personalizada

---

## Dependencias JWT Actualizadas

### Migración de JWT 0.9.1 a 0.11.5

#### Dependencias Anteriores (Deprecadas)
```xml
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt</artifactId>
    <version>0.9.1</version>
</dependency>
```

#### Dependencias Actuales (Recomendadas)
```xml
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.11.5</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.11.5</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.11.5</version>
    <scope>runtime</scope>
</dependency>
```

#### Beneficios de la Actualización
- **Seguridad mejorada**: Algoritmos más robustos y validaciones adicionales
- **Compatibilidad**: Mejor integración con versiones modernas de Java
- **Performance**: Optimizaciones en el parsing y validación de tokens
- **Mantenimiento**: Soporte activo y correcciones de seguridad

#### Cambios en el Código
- **JwtUtil**: Migrado para usar la nueva API de JWT 0.11.5
- **Algoritmo**: Cambiado de HS256 a HS512 para mayor seguridad
- **Validación**: Agregada validación de issuer y manejo robusto de errores
- **Claves**: Uso de `Keys.hmacShaKeyFor()` para generación segura de claves

---

## Migración de JUnit 4 a JUnit 5

### Cambios Principales Implementados

#### 1. Anotaciones
```java
// JUnit 4 → JUnit 5
@RunWith(MockitoJUnitRunner.class) → @ExtendWith(MockitoExtension.class)
@RunWith(SpringRunner.class) → @ExtendWith(SpringExtension.class)
@Before → @BeforeEach
@After → @AfterEach
@Test(expected = Exception.class) → @Test + assertThrows()
```

#### 2. Imports
```java
// JUnit 4 → JUnit 5
import org.junit.Before; → import org.junit.jupiter.api.BeforeEach;
import org.junit.Test; → import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith; → import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.MockitoJUnitRunner; → import org.mockito.junit.jupiter.MockitoExtension;
```

#### 3. Manejo de Excepciones
```java
// JUnit 4
@Test(expected = ResourceNotFoundException.class)
public void shouldThrowException() {
    service.methodThatShouldThrowException();
}

// JUnit 5
@Test
public void shouldThrowException() {
    assertThrows(ResourceNotFoundException.class, () -> {
        service.methodThatShouldThrowException();
    });
}
```

#### 4. Assertions
```java
// JUnit 4 → JUnit 5
import org.junit.Assert.*; → import org.junit.jupiter.api.Assertions.*;
```

### Configuración de Maven

#### Dependencias Actualizadas
```xml
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-junit-jupiter</artifactId>
    <scope>test</scope>
</dependency>
```

#### Plugin de Surefire
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>3.0.0</version>
    <configuration>
        <includes>
            <include>**/*Test.java</include>
            <include>**/*Tests.java</include>
        </includes>
        <excludes>
            <exclude>**/Abstract*.java</exclude>
        </excludes>
    </configuration>
</plugin>
```

### Beneficios de la Migración

1. **Framework Moderno**: JUnit 5 es activamente mantenido y mejorado
2. **Mejor Sintaxis**: assertThrows más expresivo que expected
3. **Extensibilidad**: Sistema de extensiones más flexible
4. **Compatibilidad**: Mejor integración con Spring Boot 2.7.x
5. **Futuro**: Base para futuras migraciones a Spring Boot 3.x

---

## Cobertura de Tests con JaCoCo

### Configuración del Plugin
```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.10</version>
    <executions>
        <execution>
            <goals>
                <goal>prepare-agent</goal>
            </goals>
        </execution>
        <execution>
            <id>report</id>
            <phase>test</phase>
            <goals>
                <goal>report</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

### Generación de Reportes
```bash
# Ejecutar tests y generar reporte de cobertura
mvn clean test jacoco:report

# Ver reporte en target/site/jacoco/index.html
```

### Configuración de Umbrales
```xml
<execution>
    <id>check</id>
    <goals>
        <goal>check</goal>
    </goals>
    <configuration>
        <rules>
            <rule>
                <element>BUNDLE</element>
                <limits>
                    <limit>
                        <counter>LINE</counter>
                        <value>COVEREDRATIO</value>
                        <minimum>0.80</minimum>
                    </limit>
                </limits>
            </rule>
        </rules>
    </configuration>
</execution>
```

