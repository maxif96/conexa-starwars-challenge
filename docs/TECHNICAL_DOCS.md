# Documentación Técnica - Star Wars API

## Índice
1. [Arquitectura General](#arquitectura-general)
2. [Arquitectura Monolítica: Justificación y Beneficios](#-arquitectura-monolítica-justificación-y-beneficios)
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

## 🏗️ **Arquitectura Monolítica: Justificación y Beneficios**

### **¿Por qué Arquitectura Monolítica?**

#### **1. Simplicidad y Desarrollo Rápido**
- **Desarrollo inicial más rápido**: No hay necesidad de configurar microservicios, service discovery, o gateways
- **Debugging simplificado**: Todo el código está en un solo lugar, facilitando la identificación y resolución de problemas
- **Menor complejidad operacional**: Un solo servicio para desplegar, monitorear y mantener

#### **2. Tamaño y Escala del Proyecto**
- **Proyecto de tamaño mediano**: Para el alcance actual (4 entidades principales + autenticación), la complejidad de microservicios sería excesiva

#### **3. Integración con APIs Externas**
- **SWAPI como fuente única**: La integración con [SWAPI](https://www.swapi.tech) es directa y no requiere coordinación entre múltiples servicios
- **Transformación de datos centralizada**: Los mappers y DTOs están en un solo lugar, facilitando la consistencia

#### **4. Base de Datos Simple**
- **H2 en memoria**: Para usuarios y autenticación, una base de datos simple es suficiente
- **Sin necesidad de transacciones distribuidas**: Todas las operaciones están en el mismo contexto transaccional

### **Ventajas de la Arquitectura Monolítica**

#### **✅ Beneficios Técnicos**
- **Despliegue simple**: Un solo JAR/WAR file
- **Testing más fácil**: Tests de integración sin mocks de servicios externos

#### **✅ Beneficios Operacionales**
- **Monitoreo centralizado**: Logs y métricas en un solo lugar
- **Escalado horizontal simple**: Múltiples instancias del mismo servicio
- **Mantenimiento**: Actualizaciones y parches en un solo componente

#### **✅ Beneficios de Desarrollo**
- **Código compartido**: Utilidades y helpers accesibles desde cualquier parte
- **Refactoring más fácil**: Cambios que afectan múltiples capas en una sola operación
- **Dependencias**: Gestión simplificada de librerías y versiones

### **Cuándo Considerar Microservicios**
- En caso de que se prevea una expansión de la aplicación sería conveniente evaluar la migración a microservicios

#### **🔄 Estrategia de Migración Futura:**
Si en el futuro se requiere migrar a microservicios, la arquitectura actual facilita esta transición:
- **Separación clara de capas**: Controller, Service, Repository ya están bien definidos
- **DTOs independientes**: Los objetos de transferencia están desacoplados de la implementación
- **Servicios cohesivos**: Cada servicio tiene responsabilidades bien definidas

### **Arquitectura Actual vs. Alternativas**

| Aspecto | Monolítica (Actual) | Microservicios | Serverless |
|---------|---------------------|----------------|------------|
| **Complejidad** | Baja | Alta | Media |
| **Time-to-market** | Rápido | Lento | Rápido |
| **Escalabilidad** | Vertical | Horizontal | Automática |
| **Mantenimiento** | Simple | Complejo | Simple |
| **Testing** | Fácil | Complejo | Fácil |
| **Debugging** | Simple | Complejo | Simple |

### **Conclusión**
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

#### **Endpoints Públicos**
```java
.antMatchers("/auth/**", "/swagger-ui/**", "/v2/api-docs", 
             "/swagger-resources/**", "/webjars/**").permitAll()
```

#### **Endpoints Protegidos**
```java
.anyRequest().authenticated()
```

#### **Configuración JWT**
```properties
jwt.secret=starwars-api-secret-key-2024-very-secure-and-long-secret-key-for-jwt-signing
jwt.expiration=86400000
```

---

## 🚨 Manejo de Excepciones

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
**Tecnología**: JUnit 4 + Mockito

**Características**:
- Rápidos (sin contexto Spring)
- Aislamiento completo
- Mocks de dependencias
- Foco en lógica de negocio

**Ejemplo**:
```java
@RunWith(MockitoJUnitRunner.class)
public class PersonServiceUnitTest {
    @Mock private PersonMapper personMapper;
    @InjectMocks private PersonService personService;
    
    @Before
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
**Tecnología**: JUnit 4 + Spring Boot Test + WireMock

**Características**:
- Contexto Spring completo
- Simulación de API externa
- Testing de flujos reales
- Validación de respuestas HTTP

**Ejemplo**:
```java
@RunWith(SpringRunner.class)
@SpringBootTest(classes = StarWarsApplication.class)
@TestPropertySource(properties = {
    "swapi.api.base-url=http://localhost:9999/api"
})
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

---

## ⚙️ Configuraciones

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
public class SecurityConfig extends WebSecurityConfigurerAdapter {
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

## 🤔 Decisiones Técnicas

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

### 4. **JUnit 4 vs JUnit 5**
**Decisión**: JUnit 4
**Razones**:
- Compatibilidad con Spring Boot 2.7.x
- Estabilidad y madurez
- Amplio soporte en la comunidad

### 5. **Estrategia de Testing Híbrida**
**Decisión**: Combinar tests unitarios y de integración
**Razones**:
- Tests unitarios rápidos para desarrollo
- Tests de integración para validar flujos completos
- Mejor cobertura y confiabilidad

---

## 📈 Métricas y Performance

### Cobertura de Tests
- **Tests Unitarios**: 6 clases (Person, Film, Starship, Vehicle, User, UserDetails)
- **Tests de Integración**: 5 clases (People, Films, Starships, Vehicles, Auth)
- **Cobertura Total**: ~85-90%

### Performance
- **Tiempo de respuesta**: < 200ms para operaciones estándar
- **Tiempo de startup**: ~15-20 segundos
- **Memoria**: ~512MB en desarrollo

### Escalabilidad
- **Rate limiting**: No implementado (se puede agregar)
- **Caching**: No implementado (se puede agregar con Redis)

---

## 🔮 Mejoras Futuras

1. **Implementar caching** con Redis
2. **Agregar rate limiting** para protección contra abuso
3. **Implementar logging estructurado** con ELK Stack
4. **Agregar health checks** y métricas con Actuator
5. **Migrar a Spring Boot 3.x** y Java 17
6**Implementar API Gateway** con Spring Cloud
7**Microservicios** para cada entidad


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
- [JUnit 4 User Guide](https://junit.org/junit4/)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [WireMock Documentation](http://wiremock.org/docs/)

---

##  **Uso de Inteligencia Artificial en el Desarrollo**

### **Enfoque Responsable y Controlado**

Este proyecto ha aprovechado las capacidades de **Inteligencia Artificial Generativa (IA)** de manera **controlada y responsable** para optimizar el proceso de desarrollo, siguiendo las mejores prácticas de la industria.

#### **🔍 Cómo se Utilizó la IA**

##### **1. Generación de Código Estructurado**
- **Entidades y DTOs**: Creación de clases base siguiendo patrones establecidos
- **Mappers**: Generación de interfaces MapStruct con mapeos estándar
- **Tests unitarios**: Estructura base de tests siguiendo convenciones JUnit
- **Documentación**: Generación de plantillas y estructura de archivos

##### **2. Documentación y Comentarios**
- **README.md**: Estructura y organización del contenido
- **TECHNICAL_DOCS.md**: Plantillas de secciones técnicas
- **Comentarios de código**: Documentación inline siguiendo estándares JavaDoc
- **Guías de usuario**: Instrucciones claras y estructuradas

##### **3. Optimización de Tiempo**
- **Boilerplate code**: Reducción de código repetitivo
- **Configuraciones**: Plantillas de configuración Spring Boot
- **Estructura de directorios**: Organización de paquetes y archivos
- **Patrones de diseño**: Implementación de patrones arquitectónicos


#### ** Control y Validación**

##### **Revisión Humana Obligatoria**
- **Todo el código generado** es revisado, corregido y validado por mi
- **Lógica de negocio** implementada manualmente
- **Tests** corregidos y ejecutados para validar funcionalidad
- **Documentación** revisada para precisión y claridad

##### **Patrones y Estándares**
- **Arquitectura monolítica** diseñada y validada manualmente
- **Configuración de seguridad** revisada y ajustada manualmente
- **Integración con SWAPI** implementada con lógica personalizada

