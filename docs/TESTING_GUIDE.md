# Guía de Testing - Star Wars API

## Índice
1. [Estrategia de Testing](#estrategia-de-testing)
2. [Arquitectura de Tests](#arquitectura-de-tests)
3. [Tests Unitarios](#tests-unitarios)
4. [Tests de Integración](#tests-de-integración)
5. [Ejecución de Tests](#ejecución-de-tests)
6. [Ejemplos Prácticos](#ejemplos-prácticos)
7. [Mejores Prácticas](#mejores-prácticas)
8. [Troubleshooting](#troubleshooting)

---

## Estrategia de Testing

### ¿Por qué Testing Híbrido?

El proyecto implementa una **estrategia de testing híbrida** que combina lo mejor de dos enfoques:

#### Tests Unitarios
- **Objetivo**: Probar lógica de negocio en aislamiento
- **Velocidad**: Muy rápidos (< 100ms por test)
- **Contexto**: Sin contexto Spring
- **Dependencias**: Completamente mockeadas

#### Tests de Integración
- **Objetivo**: Probar flujos completos de controladores
- **Velocidad**: Moderados (1-3 segundos por test)
- **Contexto**: Contexto Spring completo
- **Dependencias**: Simulación realista de APIs externas

### Beneficios de la Estrategia Híbrida

1. **Desarrollo Rápido**: Tests unitarios para iteración rápida
2. **Confiabilidad**: Tests de integración para validar flujos completos
3. **Cobertura Completa**: Ambos tipos cubren diferentes aspectos
4. **CI/CD Eficiente**: Ejecutar solo tests unitarios en desarrollo
5. **Debugging Fácil**: Aislamiento para identificar problemas rápidamente

---

## Arquitectura de Tests

### Estructura de Directorios
```
src/test/java/com/starwars/
├── service/                    # Tests Unitarios
│   ├── PersonServiceUnitTest.java
│   ├── FilmServiceUnitTest.java
│   ├── StarshipServiceUnitTest.java
│   ├── VehicleServiceUnitTest.java
│   ├── UserServiceUnitTest.java
│   └── UserDetailsServiceImplUnitTest.java
└── controller/                 # Tests de Integración
    ├── PeopleControllerIntegrationTest.java
    ├── FilmsControllerIntegrationTest.java
    ├── StarshipsControllerIntegrationTest.java
    ├── VehiclesControllerIntegrationTest.java
    └── AuthControllerUnitTest.java
```

### Tecnologías Utilizadas

#### Tests Unitarios
- **JUnit 5 (Jupiter)**: Framework de testing
- **Mockito**: Mocking de dependencias
- **ReflectionTestUtils**: Inyección de campos privados

#### Tests de Integración
- **Spring Boot Test**: Contexto de aplicación
- **MockMvc**: Testing de controladores REST
- **WireMock**: Simulación de APIs externas
- **H2 Database**: Base de datos en memoria para tests

---

## Tests Unitarios

### Características Principales

#### 1. Aislamiento Completo
```java
@ExtendWith(MockitoExtension.class)
public class PersonServiceUnitTest {
    @Mock private PersonMapper personMapper;
    @InjectMocks private PersonService personService;
    
    // Cada test es completamente independiente
}
```

#### 2. Mocks de Dependencias
```java
@Mock
private PersonMapper personMapper;

@Mock
private RestTemplate restTemplate;

// Todas las dependencias externas están mockeadas
```

#### 3. Inyección de Configuración
```java
@BeforeEach
public void setUp() {
    // Inyectar configuración que normalmente viene de @Value
    ReflectionTestUtils.setField(personService, "baseUrl", "https://swapi.tech/api");
}
```

#### 4. Spy para Métodos Heredados
```java
@Test
public void getPersonById_ValidId_ShouldReturnPerson() {
    // Crear spy para mockear métodos heredados
    PersonService spyService = spy(personService);
    doReturn(apiResponse).when(spyService).fetchApiData(anyString(), any(ParameterizedTypeReference.class));
    
    // Ejecutar test
    PersonResponseDto result = spyService.getPersonById("1");
    
    // Validar resultado
    assertNotNull(result);
    assertEquals("Luke Skywalker", result.getName());
}
```

### Patrón de Testing

#### Arrange-Act-Assert (AAA)
```java
@Test
public void listOrSearchPeople_WithNameParameter_ShouldReturnSearchResults() {
    // ARRANGE - Preparar datos y mocks
    String searchName = "Luke";
    int page = 1;
    int limit = 10;
    
    PersonApiDto personApiDto = createPersonApiDto("1", "Luke Skywalker");
    ApiDetailResult<PersonApiDto> detailResult = createApiDetailResult("1", personApiDto);
    List<ApiDetailResult<PersonApiDto>> resultList = Arrays.asList(detailResult);
    ApiEntityResponse<List<ApiDetailResult<PersonApiDto>>> apiResponse = createApiEntityResponse(resultList);
    
    PersonResponseDto responseDto = createPersonResponseDto("1", "Luke Skywalker");
    when(personMapper.toResponseDtoFromDetail(any(ApiResult.class))).thenReturn(responseDto);
    
    PersonService spyService = spy(personService);
    doReturn(apiResponse).when(spyService).fetchApiData(anyString(), any(ParameterizedTypeReference.class));
    
    // ACT - Ejecutar método bajo test
    PageResponseDto<PersonResponseDto> result = spyService.listOrSearchPeople(searchName, page, limit);
    
    // ASSERT - Validar resultados
    assertNotNull(result);
    assertEquals(1, result.getContent().size());
    assertEquals("Luke Skywalker", result.getContent().get(0).getName());
    assertEquals(1, result.getTotalElements());
    assertEquals(1, result.getTotalPages());
    
    verify(personMapper).toResponseDtoFromDetail(any(ApiResult.class));
}
```

### Manejo de Excepciones en JUnit 5

#### Uso de assertThrows
```java
@Test
public void getPersonById_InvalidId_ShouldThrowResourceNotFoundException() {
    // Arrange
    String personId = "999";
    ApiEntityResponse<ApiDetailResult<PersonApiDto>> apiResponse = createApiEntityResponse(null);

    PersonService spyService = spy(personService);
    doReturn(apiResponse).when(spyService).fetchApiData(anyString(), any(ParameterizedTypeReference.class));

    // Act & Assert - JUnit 5 usa assertThrows en lugar de expected
    assertThrows(ResourceNotFoundException.class, () -> {
        spyService.getPersonById(personId);
    });
}
```

### Métodos de Ayuda

#### Creación de Objetos de Test
```java
// Métodos auxiliares para crear objetos de test
private PersonApiDto createPersonApiDto(String uid, String name) {
    PersonApiDto dto = new PersonApiDto();
    ReflectionTestUtils.setField(dto, "name", name);
    ReflectionTestUtils.setField(dto, "height", "172");
    ReflectionTestUtils.setField(dto, "mass", "77");
    return dto;
}

private ApiDetailResult<PersonApiDto> createApiDetailResult(String uid, PersonApiDto properties) {
    ApiDetailResult<PersonApiDto> result = new ApiDetailResult<>();
    result.setUid(uid);
    result.setProperties(properties);
    return result;
}

private ApiEntityResponse<List<ApiDetailResult<PersonApiDto>>> createApiEntityResponse(List<ApiDetailResult<PersonApiDto>> result) {
    ApiEntityResponse<List<ApiDetailResult<PersonApiDto>>> response = new ApiEntityResponse<>();
    response.setResult(result);
    return response;
}
```

---

## Tests de Integración

### Características Principales

#### 1. Contexto Spring Completo
```java
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = StarWarsApplication.class, 
                webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebMvc
@ActiveProfiles("test")
public class PeopleControllerIntegrationTest {
    // Contexto Spring completo disponible
}
```

#### 2. Simulación de API Externa con WireMock
```java
@BeforeEach
public void setUp() {
    mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    
    // Configurar WireMock para simular SWAPI
    wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig().port(9999));
    wireMockServer.start();
    WireMock.configureFor("localhost", 9999);
}

@AfterEach
public void tearDown() {
    wireMockServer.stop();
}
```

#### 3. MockMvc para Testing de Controladores
```java
@Test
public void listPeople_WithoutNameFilter_ShouldReturnPaginatedResults() throws Exception {
    // Configurar WireMock
    String mockApiResponse = "{\"message\":\"ok\",\"total_records\":82,\"results\":[...]}";
    
    stubFor(WireMock.get(urlEqualTo("/api/people?page=1&limit=10&expanded=true"))
            .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody(mockApiResponse)));
    
    // Ejecutar request con MockMvc
    mockMvc.perform(get("/people")
                    .param("page", "1")
                    .param("limit", "10")
                    .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(2)))
            .andExpect(jsonPath("$.totalElements", is(82)));
}
```

### Configuración de Perfil de Test

#### Archivo application-test.properties
```properties
# Configuración de base de datos para tests
spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=create-drop

# Configuración de JWT para tests
JWT_SECRET=testSecretKeyForTestingPurposesOnly12345678901234567890
JWT_EXPIRATION=86400000

# Configuración de la API externa para tests
swapi.api.base-url=http://localhost:9999/api

# Configuración de logging para tests
logging.level.com.starwars=DEBUG
logging.level.org.springframework.security=DEBUG

# Configuración de H2 Console para tests
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# Configuración de seguridad para tests
spring.security.user.name=testuser
spring.security.user.password=testpass
```

#### Activación del Perfil
```java
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = StarWarsApplication.class, 
                webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebMvc
@ActiveProfiles("test")  // Activa el perfil de test
public class PeopleControllerIntegrationTest {
    // ...
}
```

### Configuración de WireMock

#### Stubbing de Endpoints
```java
// Stub para listado paginado
stubFor(WireMock.get(urlPathEqualTo("/api/people"))
        .withQueryParam("page", equalTo("1"))
        .withQueryParam("limit", equalTo("10"))
        .withQueryParam("expanded", equalTo("true"))
        .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody(mockApiResponse)));

// Stub para búsqueda por nombre
stubFor(WireMock.get(urlPathEqualTo("/api/people"))
        .withQueryParam("name", equalTo("Luke"))
        .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody(mockSearchResponse)));

// Stub para entidad por ID
stubFor(WireMock.get(urlPathEqualTo("/api/people/1"))
        .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody(mockEntityResponse)));
```

#### Respuestas Mock Realistas
```java
private String createMockPeopleResponse() {
    return "{\n" +
           "  \"message\": \"ok\",\n" +
           "  \"total_records\": 82,\n" +
           "  \"total_pages\": 9,\n" +
           "  \"previous\": null,\n" +
           "  \"next\": \"https://swapi.tech/api/people?page=2&limit=10\",\n" +
           "  \"results\": [\n" +
           "    {\n" +
           "      \"uid\": \"1\",\n" +
           "      \"properties\": {\n" +
           "        \"height\": \"172\",\n" +
           "        \"mass\": \"77\",\n" +
           "        \"hair_color\": \"blond\",\n" +
           "        \"skin_color\": \"fair\",\n" +
           "        \"eye_color\": \"blue\",\n" +
           "        \"birth_year\": \"19BBY\",\n" +
           "        \"gender\": \"male\",\n" +
           "        \"name\": \"Luke Skywalker\",\n" +
           "        \"homeworld\": \"https://swapi.tech/api/planets/1\",\n" +
           "        \"url\": \"https://swapi.tech/api/people/1\"\n" +
           "      }\n" +
           "    }\n" +
           "  ]\n" +
           "}";
}
```

### Testing de Casos de Error

#### Errores de API Externa
```java
@Test
public void getPersonById_InvalidId_ShouldReturnNotFound() throws Exception {
    // Mock de respuesta 404
    stubFor(WireMock.get(urlPathEqualTo("/api/people/999"))
            .willReturn(aResponse()
                    .withStatus(404)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"message\": \"Not found\"}")));
    
    // Act & Assert
    mockMvc.perform(get("/people/999"))
            .andDo(print())
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message", containsString("Person not found")));
}
```

#### Validación de Parámetros
```java
@Test
public void listPeople_WithInvalidPagination_ShouldReturnBadRequest() throws Exception {
    // Act & Assert - Página inválida (menor a 1)
    mockMvc.perform(get("/people")
                    .param("page", "0")
                    .param("limit", "10"))
            .andDo(print())
            .andExpect(status().isBadRequest());
}

@Test
public void listPeople_WithLargeLimit_ShouldReturnBadRequest() throws Exception {
    // Act & Assert - Límite inválido (mayor a 100)
    mockMvc.perform(get("/people")
                    .param("page", "1")
                    .param("limit", "101"))
            .andDo(print())
            .andExpect(status().isBadRequest());
}
```

---

## Ejecución de Tests

### Comandos Maven

#### Ejecutar Todos los Tests
```bash
# Ejecutar todos los tests
mvn test

# Ejecutar tests con información detallada
mvn test -X

# Ejecutar tests con cobertura JaCoCo
mvn clean test jacoco:report
```

### Configuración de IDE

#### IntelliJ IDEA
1. **Configurar Run Configuration**:
   - Type: JUnit
   - Test kind: Class
   - Search for tests: In whole project
   - Working directory: `$MODULE_DIR$`

2. **Configurar Test Runner**:
   - Default test runner: JUnit 5 (Jupiter)
   - Generate test runner: IntelliJ IDEA

3. **Ejecutar Tests**:
   - Click derecho en clase → Run 'TestClassName'
   - Click derecho en método → Run 'testMethodName'
   - Ctrl+Shift+F10 para ejecutar test actual

#### Eclipse
1. **Configurar JUnit 5**:
   - Help → Eclipse Marketplace → Search "JUnit 5"
   - Install JUnit 5

2. **Ejecutar Tests**:
   - Click derecho en clase → Run As → JUnit Test
   - Click derecho en método → Run As → JUnit Test

---

## Ejemplos Prácticos

### Test Unitario Completo

#### PersonServiceUnitTest.java
```java
@ExtendWith(MockitoExtension.class)
public class PersonServiceUnitTest {

    @Mock
    private PersonMapper personMapper;

    @InjectMocks
    private PersonService personService;

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(personService, "baseUrl", "https://swapi.tech/api");
    }

    @Test
    public void getPersonById_ValidId_ShouldReturnPerson() {
        // Arrange
        String personId = "1";
        PersonApiDto personApiDto = createPersonApiDto("1", "Luke Skywalker");
        ApiDetailResult<PersonApiDto> detailResult = createApiDetailResult("1", personApiDto);
        ApiEntityResponse<ApiDetailResult<PersonApiDto>> apiResponse = createApiEntityResponse(detailResult);

        PersonResponseDto responseDto = createPersonResponseDto("1", "Luke Skywalker");
        when(personMapper.toResponseDtoFromDetail(any(ApiResult.class))).thenReturn(responseDto);

        PersonService spyService = spy(personService);
        doReturn(apiResponse).when(spyService).fetchApiData(anyString(), any(ParameterizedTypeReference.class));

        // Act
        PersonResponseDto result = spyService.getPersonById(personId);
        
        // Assert
        assertNotNull(result);
        assertEquals("Luke Skywalker", result.getName());
        assertEquals("172", result.getHeight());
        assertEquals("77", result.getMass());

        verify(personMapper).toResponseDtoFromDetail(any(ApiResult.class));
    }

    @Test
    public void getPersonById_InvalidId_ShouldThrowResourceNotFoundException() {
        // Arrange
        String personId = "999";
        ApiEntityResponse<ApiDetailResult<PersonApiDto>> apiResponse = createApiEntityResponse(null);

        PersonService spyService = spy(personService);
        doReturn(apiResponse).when(spyService).fetchApiData(anyString(), any(ParameterizedTypeReference.class));

        // Act & Assert - JUnit 5 usa assertThrows
        assertThrows(ResourceNotFoundException.class, () -> {
            spyService.getPersonById(personId);
        });
    }

    // Métodos auxiliares...
}
```

### Test de Integración Completo

#### PeopleControllerIntegrationTest.java
```java
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = StarWarsApplication.class, 
                webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebMvc
@ActiveProfiles("test")
public class PeopleControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;
    private WireMockServer wireMockServer;
    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig().port(9999));
        wireMockServer.start();
        WireMock.configureFor("localhost", 9999);
    }

    @AfterEach
    public void tearDown() {
        wireMockServer.stop();
    }

    @Test
    public void listPeople_WithoutNameFilter_ShouldReturnPaginatedResults() throws Exception {
        // Arrange
        String mockApiResponse = createMockPeopleResponse();
        
        stubFor(WireMock.get(urlPathEqualTo("/api/people"))
                .withQueryParam("page", equalTo("1"))
                .withQueryParam("limit", equalTo("10"))
                .withQueryParam("expanded", equalTo("true"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(mockApiResponse)));

        // Act & Assert
        mockMvc.perform(get("/people")
                        .param("page", "1")
                        .param("limit", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name", is("Luke Skywalker")))
                .andExpect(jsonPath("$.totalElements", is(82)))
                .andExpect(jsonPath("$.totalPages", is(9)));
    }

    @Test
    public void getPersonById_ValidId_ShouldReturnPerson() throws Exception {
        // Arrange
        String mockApiResponse = createMockPersonResponse();
        
        stubFor(WireMock.get(urlPathEqualTo("/api/people/1"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(mockApiResponse)));

        // Act & Assert
        mockMvc.perform(get("/people/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Luke Skywalker")))
                .andExpect(jsonPath("$.height", is("172")))
                .andExpect(jsonPath("$.mass", is("77")));
    }

    // Métodos auxiliares...
}
```

---

## Mejores Prácticas

### 1. Nomenclatura de Tests
```java
// Formato: methodName_scenario_expectedResult
@Test
public void getPersonById_ValidId_ShouldReturnPerson()
@Test
public void getPersonById_InvalidId_ShouldThrowResourceNotFoundException()
@Test
public void listPeople_WithNameFilter_ShouldReturnFilteredResults()
@Test
public void listPeople_WithEmptyName_ShouldReturnAllPeople()
```

### 2. Organización de Tests
```java
@Test
public void methodName_scenario_expectedResult() {
    // ARRANGE - Preparar datos, mocks y configuración
    // ... código de preparación ...
    
    // ACT - Ejecutar método bajo test
    // ... código de ejecución ...
    
    // ASSERT - Validar resultados
    // ... código de validación ...
}
```

### 3. Mocks y Stubs
```java
// Usar when() para stubs que retornan valores
when(personMapper.toResponseDtoFromDetail(any())).thenReturn(responseDto);

// Usar doReturn() para métodos void o métodos heredados
doReturn(apiResponse).when(spyService).fetchApiData(anyString(), any());

// Usar verify() para validar interacciones
verify(personMapper).toResponseDtoFromDetail(any());
verify(personMapper, times(2)).toResponseDtoFromDetail(any());
```

### 4. Validaciones de Assertions
```java
// Validaciones básicas
assertNotNull(result);
assertEquals(expected, actual);
assertTrue(condition);
assertFalse(condition);

// Validaciones de colecciones
assertEquals(expectedSize, list.size());
assertTrue(list.contains(expectedItem));
assertThat(list, hasSize(expectedSize));

// Validaciones de excepciones en JUnit 5
@Test
public void shouldThrowException() {
    assertThrows(ResourceNotFoundException.class, () -> {
        service.methodThatShouldThrowException();
    });
}

// O usando try-catch para validar mensajes específicos
@Test
public void shouldThrowExceptionWithSpecificMessage() {
    Exception exception = assertThrows(ExpectedException.class, () -> {
        service.methodThatShouldThrowException();
    });
    assertEquals("Expected message", exception.getMessage());
}
```

---


### Debugging de Tests

#### 1. Logging Detallado
```properties
# application-test.properties
logging.level.com.starwars=DEBUG
logging.level.org.springframework.web=DEBUG
logging.level.org.springframework.test=DEBUG
```

#### 2. Usar print() en MockMvc
```java
mockMvc.perform(get("/people"))
        .andDo(print())  // Imprime request y response
        .andExpect(status().isOk());
```

#### 3. Debugging con IDE
```java
// Agregar breakpoints en tests
@Test
public void debugTest() {
    String result = service.methodUnderTest();  // Breakpoint aquí
    assertNotNull(result);
}
```

---

## Migración de JUnit 4 a JUnit 5

### Cambios Principales

#### 1. Anotaciones
```java
// JUnit 4
@RunWith(MockitoJUnitRunner.class)
@Before
@After
@Test(expected = Exception.class)

// JUnit 5
@ExtendWith(MockitoExtension.class)
@BeforeEach
@AfterEach
@Test + assertThrows()
```

#### 2. Imports
```java
// JUnit 4
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

// JUnit 5
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
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
// JUnit 4
import org.junit.Assert.*;

// JUnit 5
import org.junit.jupiter.api.Assertions.*;
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

