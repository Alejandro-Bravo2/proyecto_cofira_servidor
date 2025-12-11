# 🏋️ COFIRA - Sistema de Gestión de Gimnasios

**API RESTful** para la gestión integral de gimnasios, incluyendo planes de entrenamiento, rutinas de alimentación, ejercicios y usuarios. Desarrollado con **Spring Boot 4.0** y **PostgreSQL**, con autenticación JWT y arquitectura de microservicios.

---

## 📑 Tabla de Contenidos

- [Características Principales](#-características-principales)
- [Arquitectura del Proyecto](#-arquitectura-del-proyecto)
- [Diagrama Entidad-Relación](#-diagrama-entidad-relación)
- [Seguridad y Autenticación JWT](#-seguridad-y-autenticación-jwt)
- [Instalación y Configuración](#-instalación-y-configuración)
- [Endpoints de la API](#-endpoints-de-la-api)
- [DTOs y Validaciones](#-dtos-y-validaciones)
- [Consultas Personalizadas](#-consultas-personalizadas)
- [Testing y Evidencias](#-testing-y-evidencias)
- [Tecnologías Utilizadas](#-tecnologías-utilizadas)

---

## ✨ Características Principales

- 🔐 **Autenticación JWT** con roles (USER, ADMIN)
- 🔄 **Sistema de blacklist** para tokens revocados (persistente en BD)
- 🌐 **CORS configurado** para integración con cliente Angular
- 📊 **Gestión completa** de planes, rutinas, ejercicios y alimentación
- 🏋️ **Sistema de salas de gimnasio** y objetivos personalizados
- 📈 **Actuator** para monitoreo de la aplicación
- 📚 **Swagger/OpenAPI** para documentación interactiva
- ✅ **Validaciones** con Bean Validation
- 🐘 **PostgreSQL** como base de datos principal
- 🐳 **Docker Compose** para despliegue de BD y pgAdmin

---

## 🏗️ Arquitectura del Proyecto

```
cofira/
├── src/main/java/com/gestioneventos/cofira/
│   ├── api/                    # Interfaces OpenAPI
│   ├── config/                 # Configuraciones (Security, OpenAPI)
│   ├── controllers/            # Controladores REST
│   │   ├── AuthController.java
│   │   ├── UsuarioController.java
│   │   ├── PlanController.java
│   │   ├── RutinaEjercicioController.java
│   │   ├── RutinaAlimentacionController.java
│   │   ├── EjerciciosController.java
│   │   ├── AlimentoController.java
│   │   ├── ObjetivosController.java
│   │   └── SalaDeGimnasioController.java
│   ├── dto/                    # Data Transfer Objects
│   │   ├── auth/               # DTOs de autenticación
│   │   ├── usuario/
│   │   ├── plan/
│   │   ├── rutinaejercicio/
│   │   ├── rutinaalimentacion/
│   │   ├── ejercicios/
│   │   ├── alimento/
│   │   ├── objetivos/
│   │   └── sala/
│   ├── entities/               # Entidades JPA
│   │   ├── Usuario.java
│   │   ├── Plan.java
│   │   ├── RutinaEjercicio.java
│   │   ├── RutinaAlimentacion.java
│   │   ├── Ejercicios.java
│   │   ├── Alimento.java
│   │   ├── Objetivos.java
│   │   ├── SalaDeGimnasio.java
│   │   ├── DiaEjercicio.java
│   │   ├── DiaAlimentacion.java
│   │   ├── Desayuno.java
│   │   ├── Almuerzo.java
│   │   ├── Comida.java
│   │   ├── Merienda.java
│   │   ├── Cena.java
│   │   └── TokenRevocado.java
│   ├── enums/                  # Enumeraciones
│   │   ├── Rol.java            # USER, ADMIN
│   │   └── DiaSemana.java
│   ├── exceptions/             # Manejo de excepciones
│   ├── repositories/           # Repositorios JPA
│   ├── security/               # Seguridad JWT
│   │   ├── AuthTokenFilter.java
│   │   ├── JwtUtils.java
│   │   └── UserDetailsServiceImpl.java
│   └── services/               # Lógica de negocio
└── src/main/resources/
    ├── application.properties  # Configuración principal
    └── static/                 # Recursos estáticos
```

---

## 🗄️ Diagrama Entidad-Relación

![Diagrama](./assets/diagramaPerfecto.svg)

## 🔐 Seguridad y Autenticación JWT

### Descripción General

El sistema implementa **autenticación basada en JWT (JSON Web Token)** con las siguientes características:

- ✅ **Tokens firmados** con algoritmo HS512
- ✅ **Passwords hasheadas** con BCrypt
- ✅ **Blacklist persistente** en base de datos
- ✅ **CORS configurado** para cliente Angular
- ✅ **Roles de autorización** (USER, ADMIN)
- ✅ **Filtro JWT** para validación en cada request

### Configuración CORS

```java
// Configuración en SecurityConfig.java
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200"));
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
    configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With"));
    configuration.setAllowCredentials(true);
    configuration.setMaxAge(3600L);
    return source;
}
```

### Roles y Permisos

| Rol       | Descripción      | Permisos                                                             |
| --------- | ---------------- | -------------------------------------------------------------------- |
| **USER**  | Usuario estándar | Consultar y gestionar sus propios planes, rutinas y objetivos        |
| **ADMIN** | Administrador    | Acceso total: CRUD de usuarios, planes, ejercicios, salas, alimentos |

### Endpoints de Autenticación

#### 1. **POST** `/auth/register` - Registro de Usuario

**Request:**

```json
{
  "nombre": "Juan Pérez",
  "username": "juanperez",
  "email": "juan@example.com",
  "password": "Password123!",
  "rol": "USER"
}
```

**Response (201 Created):**

```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "type": "Bearer",
  "username": "juanperez",
  "email": "juan@example.com",
  "roles": ["USER"]
}
```

**Notas:**

- El rol por defecto es `USER`
- El campo `rol` es opcional (solo ADMIN puede crear otros ADMIN)
- Password debe tener mínimo 8 caracteres

---

#### 2. **POST** `/auth/login` - Inicio de Sesión

**Request:**

```json
{
  "username": "juanperez",
  "password": "Password123!"
}
```

**Response (200 OK):**

```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJqdWFucGVyZXoiLCJyb2xlcyI6WyJVU0VSIl0sImlhdCI6MTcwMjMwNjgwMCwiZXhwIjoxNzAyMzkzMjAwLCJqdGkiOiI1ZjNkMWU3Yi1hYmNkLTQxMjMtOTg3Ni1lZjEyMzQ1Njc4OTAifQ...",
  "type": "Bearer",
  "username": "juanperez",
  "email": "juan@example.com",
  "roles": ["USER"]
}
```

**Estructura del JWT:**

```json
{
  "sub": "juanperez", // Username
  "roles": ["USER"], // Roles del usuario
  "iat": 1702306800, // Issued at
  "exp": 1702393200, // Expiration (24h)
  "jti": "5f3d1e7b-abcd-4123..." // ID único del token
}
```

---

#### 3. **GET** `/auth/me` - Usuario Actual

**Headers:**

```
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
```

**Response (200 OK):**

```json
{
  "id": 1,
  "nombre": "Juan Pérez",
  "username": "juanperez",
  "email": "juan@example.com",
  "rol": "USER",
  "edad": 30,
  "peso": 75.5,
  "altura": 1.75
}
```

**Nota:** No expone la contraseña (campo excluido en DTO)

---

#### 4. **POST** `/auth/logout` - Cerrar Sesión

**Headers:**

```
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
```

**Response (200 OK):**

```json
"Logout exitoso"
```

**Funcionamiento:**

1. Extrae el `jti` del token
2. Guarda el `jti` y `expiresAt` en la tabla `TokenRevocado`
3. El filtro JWT rechazará futuros requests con ese token
4. Limpieza automática de tokens expirados mediante job programado

---

### Uso del Token en Requests

Todos los endpoints protegidos requieren el header:

```http
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
```

**Ejemplo con cURL:**

```bash
curl -X GET http://localhost:8080/api/usuarios/me \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9..."
```

**Ejemplo con Postman:**

1. Selecciona el tab **Authorization**
2. Type: **Bearer Token**
3. Pega el token obtenido en `/auth/login`

---

### Rutas Públicas vs Protegidas

#### 🌐 Rutas Públicas (sin autenticación)

```
POST   /auth/register
POST   /auth/login
GET    /actuator/**          (Monitoreo)
GET    /swagger-ui/**        (Documentación)
GET    /v3/api-docs/**       (OpenAPI)
```

#### 🔒 Rutas Protegidas (requieren autenticación)

| Endpoint                       | Método            | Rol Requerido | Descripción                 |
| ------------------------------ | ----------------- | ------------- | --------------------------- |
| `/auth/me`                     | GET               | USER, ADMIN   | Usuario actual              |
| `/auth/logout`                 | POST              | USER, ADMIN   | Cerrar sesión               |
| `/api/usuarios/**`             | GET               | USER, ADMIN   | Consultar usuarios          |
| `/api/usuarios/**`             | POST, PUT, DELETE | ADMIN         | Gestionar usuarios          |
| `/api/planes/**`               | ALL               | USER, ADMIN   | Gestionar planes propios    |
| `/api/rutinas-ejercicio/**`    | ALL               | USER, ADMIN   | Gestionar rutinas           |
| `/api/rutinas-alimentacion/**` | ALL               | USER, ADMIN   | Gestionar alimentación      |
| `/api/ejercicios/**`           | GET               | USER, ADMIN   | Consultar ejercicios        |
| `/api/ejercicios/**`           | POST, PUT, DELETE | ADMIN         | Gestionar ejercicios        |
| `/api/alimentos/**`            | GET               | USER, ADMIN   | Consultar alimentos         |
| `/api/alimentos/**`            | POST, PUT, DELETE | ADMIN         | Gestionar alimentos         |
| `/api/salas/**`                | GET               | USER, ADMIN   | Consultar salas             |
| `/api/salas/**`                | POST, PUT, DELETE | ADMIN         | Gestionar salas             |
| `/api/objetivos/**`            | ALL               | USER, ADMIN   | Gestionar objetivos propios |

---

### Sistema de Blacklist (Tokens Revocados)

#### Implementación Persistente

```java
@Entity
public class TokenRevocado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String jti;           // ID único del token

    private LocalDateTime expiresAt;  // Fecha de expiración
}
```

#### Validación en Filtro JWT

```java
// 1. Extraer jti del token
String jti = jwtUtils.getJtiFromToken(token);

// 2. Verificar si está revocado
if (tokenRevocadoRepository.existsByJti(jti)) {
    throw new JwtRevokedException("Token revocado");
}

// 3. Validar firma y expiración
if (jwtUtils.validateJwtToken(token)) {
    // Construir autenticación
}
```

#### Limpieza Automática (Job Programado)

```java
@Scheduled(cron = "0 0 3 * * ?") // Todos los días a las 3 AM
public void cleanupExpiredTokens() {
    LocalDateTime now = LocalDateTime.now();
    tokenRevocadoRepository.deleteByExpiresAtBefore(now);
}
```

---

### Manejo de Errores de Seguridad

#### 401 Unauthorized

```json
{
  "timestamp": "2025-12-11T12:30:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "Token inválido o expirado",
  "path": "/api/planes"
}
```

#### 403 Forbidden

```json
{
  "timestamp": "2025-12-11T12:30:00",
  "status": 403,
  "error": "Forbidden",
  "message": "Acceso denegado: requiere rol ADMIN",
  "path": "/api/usuarios"
}
```

---

### Extras Implementados

#### ✅ Blacklist Persistente

- Tokens revocados guardados en PostgreSQL
- Limpieza automática de tokens expirados
- Tabla `token_revocado` con índice en `jti`

#### ✅ Configuración Avanzada de CORS

- Permite cliente Angular en `http://localhost:4200`
- Headers permitidos: `Authorization`, `Content-Type`
- Métodos: `GET`, `POST`, `PUT`, `DELETE`, `OPTIONS`, `PATCH`
- Credentials habilitados para cookies/sessions

#### ✅ Swagger con Seguridad

- Rutas `/swagger-ui.html` y `/v3/api-docs` públicas
- Documentación de endpoints con anotaciones OpenAPI
- Configuración de Bearer token en Swagger UI

#### ✅ Actuator para Monitoreo

- Endpoints: `health`, `info`, `metrics`, `env`
- Información de la aplicación en `/actuator/info`
- Health checks en `/actuator/health`

---

## 🚀 Instalación y Configuración

### Prerrequisitos

- ☕ **Java 17** o superior
- 🐳 **Docker** y **Docker Compose**
- 🐘 **PostgreSQL 16** (o usar Docker)
- 🔧 **Gradle 8.x** (incluido con wrapper)

### 1. Clonar el Repositorio

```bash
git clone https://github.com/Alejandro-Bravo2/proyecto_cofira_servidor.git
cd proyecto_cofira_servidor/cofira
```

### 2. Configurar Base de Datos con Docker

```bash
# Iniciar PostgreSQL y pgAdmin
docker compose up -d

# Verificar que los contenedores están corriendo
docker ps
```

**Credenciales:**

- **PostgreSQL:**

  - Host: `localhost:5432`
  - Database: `cofira`
  - User: `admin`
  - Password: `admin123`

- **pgAdmin:**
  - URL: `http://localhost:5050`
  - Email: `admin@admin.com`
  - Password: `admin123`

### 3. Configurar `application.properties`

El archivo ya está configurado en `src/main/resources/application.properties`:

```properties
# PostgreSQL
spring.datasource.url=jdbc:postgresql://localhost:5432/cofira
spring.datasource.username=admin
spring.datasource.password=admin123

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# JWT
cofira.jwt.secret=<clave-segura-generada>
cofira.jwt.expiration=86400000  # 24 horas

# Puerto
server.port=8080
```

### 4. Compilar y Ejecutar

```bash
# Dar permisos al wrapper de Gradle (solo primera vez)
chmod +x gradlew

# Compilar el proyecto
./gradlew clean build

# Ejecutar la aplicación
./gradlew bootRun
```

La aplicación estará disponible en: **http://localhost:8080**

### 5. Verificar Instalación

```bash
# Health check
curl http://localhost:8080/actuator/health

# Documentación Swagger
open http://localhost:8080/swagger-ui.html
```

---

## 📡 Endpoints de la API

### Autenticación (`/auth`)

| Método | Endpoint         | Descripción       | Público |
| ------ | ---------------- | ----------------- | ------- |
| POST   | `/auth/register` | Registrar usuario | ✅      |
| POST   | `/auth/login`    | Iniciar sesión    | ✅      |
| GET    | `/auth/me`       | Usuario actual    | ❌      |
| POST   | `/auth/logout`   | Cerrar sesión     | ❌      |

### Usuarios (`/api/usuarios`)

| Método | Endpoint                                 | Descripción             | Rol   |
| ------ | ---------------------------------------- | ----------------------- | ----- |
| GET    | `/api/usuarios`                          | Listar todos (paginado) | ADMIN |
| GET    | `/api/usuarios/{id}`                     | Obtener por ID          | USER  |
| GET    | `/api/usuarios/me`                       | Usuario actual          | USER  |
| POST   | `/api/usuarios`                          | Crear usuario           | ADMIN |
| PUT    | `/api/usuarios/{id}`                     | Actualizar usuario      | ADMIN |
| DELETE | `/api/usuarios/{id}`                     | Eliminar usuario        | ADMIN |
| GET    | `/api/usuarios/search?nombre={nombre}`   | Buscar por nombre       | USER  |
| GET    | `/api/usuarios/edad?min={min}&max={max}` | Buscar por rango edad   | ADMIN |

### Planes (`/api/planes`)

| Método | Endpoint                          | Descripción          | Rol  |
| ------ | --------------------------------- | -------------------- | ---- |
| GET    | `/api/planes`                     | Listar todos         | USER |
| GET    | `/api/planes/{id}`                | Obtener por ID       | USER |
| POST   | `/api/planes`                     | Crear plan           | USER |
| PUT    | `/api/planes/{id}`                | Actualizar plan      | USER |
| DELETE | `/api/planes/{id}`                | Eliminar plan        | USER |
| GET    | `/api/planes/usuario/{usuarioId}` | Planes de un usuario | USER |

### Rutinas de Ejercicio (`/api/rutinas-ejercicio`)

| Método | Endpoint                      | Descripción       | Rol  |
| ------ | ----------------------------- | ----------------- | ---- |
| GET    | `/api/rutinas-ejercicio`      | Listar todas      | USER |
| GET    | `/api/rutinas-ejercicio/{id}` | Obtener por ID    | USER |
| POST   | `/api/rutinas-ejercicio`      | Crear rutina      | USER |
| PUT    | `/api/rutinas-ejercicio/{id}` | Actualizar rutina | USER |
| DELETE | `/api/rutinas-ejercicio/{id}` | Eliminar rutina   | USER |

### Rutinas de Alimentación (`/api/rutinas-alimentacion`)

| Método | Endpoint                         | Descripción       | Rol  |
| ------ | -------------------------------- | ----------------- | ---- |
| GET    | `/api/rutinas-alimentacion`      | Listar todas      | USER |
| GET    | `/api/rutinas-alimentacion/{id}` | Obtener por ID    | USER |
| POST   | `/api/rutinas-alimentacion`      | Crear rutina      | USER |
| PUT    | `/api/rutinas-alimentacion/{id}` | Actualizar rutina | USER |
| DELETE | `/api/rutinas-alimentacion/{id}` | Eliminar rutina   | USER |

### Ejercicios (`/api/ejercicios`)

| Método | Endpoint                                       | Descripción          | Rol   |
| ------ | ---------------------------------------------- | -------------------- | ----- |
| GET    | `/api/ejercicios`                              | Listar todos         | USER  |
| GET    | `/api/ejercicios/{id}`                         | Obtener por ID       | USER  |
| POST   | `/api/ejercicios`                              | Crear ejercicio      | ADMIN |
| PUT    | `/api/ejercicios/{id}`                         | Actualizar ejercicio | ADMIN |
| DELETE | `/api/ejercicios/{id}`                         | Eliminar ejercicio   | ADMIN |
| GET    | `/api/ejercicios/sala/{salaId}`                | Ejercicios por sala  | USER  |
| GET    | `/api/ejercicios/calorias?min={min}&max={max}` | Por rango calorías   | USER  |

### Alimentos (`/api/alimentos`)

| Método | Endpoint                             | Descripción         | Rol   |
| ------ | ------------------------------------ | ------------------- | ----- |
| GET    | `/api/alimentos`                     | Listar todos        | USER  |
| GET    | `/api/alimentos/{id}`                | Obtener por ID      | USER  |
| POST   | `/api/alimentos`                     | Crear alimento      | ADMIN |
| PUT    | `/api/alimentos/{id}`                | Actualizar alimento | ADMIN |
| DELETE | `/api/alimentos/{id}`                | Eliminar alimento   | ADMIN |
| GET    | `/api/alimentos/calorias?max={max}`  | Bajo en calorías    | USER  |
| GET    | `/api/alimentos/proteinas?min={min}` | Alto en proteínas   | USER  |

### Salas de Gimnasio (`/api/salas`)

| Método | Endpoint                         | Descripción          | Rol   |
| ------ | -------------------------------- | -------------------- | ----- |
| GET    | `/api/salas`                     | Listar todas         | USER  |
| GET    | `/api/salas/{id}`                | Obtener por ID       | USER  |
| POST   | `/api/salas`                     | Crear sala           | ADMIN |
| PUT    | `/api/salas/{id}`                | Actualizar sala      | ADMIN |
| DELETE | `/api/salas/{id}`                | Eliminar sala        | ADMIN |
| GET    | `/api/salas/capacidad?min={min}` | Por capacidad mínima | USER  |

### Objetivos (`/api/objetivos`)

| Método | Endpoint                             | Descripción         | Rol  |
| ------ | ------------------------------------ | ------------------- | ---- |
| GET    | `/api/objetivos`                     | Listar todos        | USER |
| GET    | `/api/objetivos/{id}`                | Obtener por ID      | USER |
| POST   | `/api/objetivos`                     | Crear objetivo      | USER |
| PUT    | `/api/objetivos/{id}`                | Actualizar objetivo | USER |
| DELETE | `/api/objetivos/{id}`                | Eliminar objetivo   | USER |
| GET    | `/api/objetivos/usuario/{usuarioId}` | Objetivo de usuario | USER |

---

## 📦 DTOs y Validaciones

### DTOs de Autenticación

#### `LoginRequestDTO`

```java
{
  "username": "String @NotBlank",
  "password": "String @NotBlank @Size(min=8)"
}
```

#### `RegisterRequestDTO`

```java
{
  "nombre": "String @NotBlank",
  "username": "String @NotBlank @Size(min=4)",
  "email": "String @NotBlank @Email",
  "password": "String @NotBlank @Size(min=8)",
  "rol": "Rol (opcional, default: USER)",
  "edad": "Integer @Min(18)",
  "peso": "Double @Positive",
  "altura": "Double @Positive"
}
```

#### `AuthResponseDTO`

```java
{
  "token": "String",
  "type": "String (Bearer)",
  "username": "String",
  "email": "String",
  "roles": "List<String>"
}
```

#### `UserInfoDTO`

```java
{
  "id": "Long",
  "nombre": "String",
  "username": "String",
  "email": "String",
  "rol": "String",
  "edad": "Integer",
  "peso": "Double",
  "altura": "Double"
}
```

### DTOs de Entidades

Cada entidad tiene sus DTOs correspondientes en el paquete `dto/`:

- `UsuarioDTO`, `CreateUsuarioDTO`, `UpdateUsuarioDTO`
- `PlanDTO`, `CreatePlanDTO`, `UpdatePlanDTO`
- `RutinaEjercicioDTO`, `CreateRutinaEjercicioDTO`
- `RutinaAlimentacionDTO`, `CreateRutinaAlimentacionDTO`
- `EjerciciosDTO`, `CreateEjerciciosDTO`
- `AlimentoDTO`, `CreateAlimentoDTO`
- `SalaDeGimnasioDTO`, `CreateSalaDTO`
- `ObjetivosDTO`, `CreateObjetivosDTO`

**Características comunes:**

- ✅ **Validaciones** con `@Valid`, `@NotNull`, `@NotBlank`, `@Email`, `@Min`, `@Max`, `@Positive`
- ✅ **No exponen entidades** directamente
- ✅ **Separación** entre DTOs de creación, actualización y respuesta
- ✅ **Documentación** con anotaciones OpenAPI

---

## 🔍 Consultas Personalizadas

### UsuarioRepository

```java
// Búsqueda por email
Optional<Usuario> findByEmail(String email);

// Búsqueda por username
Optional<Usuario> findByUsername(String username);

// Verificar existencia
boolean existsByUsername(String username);
boolean existsByEmail(String email);

// Búsqueda por nombre (paginada)
Page<Usuario> findByNombreContainingIgnoreCase(String nombre, Pageable pageable);

// Búsqueda por rango de edad
List<Usuario> findByEdadBetween(Integer edadMin, Integer edadMax);

// Buscar usuarios por peso superior
@Query("SELECT u FROM Usuario u WHERE u.peso > :peso")
List<Usuario> findByPesoGreaterThan(@Param("peso") Double peso);
```

### EjerciciosRepository

```java
// Búsqueda por sala
List<Ejercicios> findBySalaId(Long salaId);

// Búsqueda por nombre
Optional<Ejercicios> findByNombre(String nombre);

// Búsqueda por rango de calorías
@Query("SELECT e FROM Ejercicios e WHERE e.calorias BETWEEN :min AND :max")
List<Ejercicios> findByCaloriasBetween(@Param("min") Integer min, @Param("max") Integer max);

// Ejercicios con más de X series
List<Ejercicios> findBySeriesGreaterThan(Integer series);
```

### AlimentoRepository

```java
// Búsqueda por nombre
Optional<Alimento> findByNombre(String nombre);

// Búsqueda por calorías bajas
List<Alimento> findByCaloriasLessThan(Double calorias);

// Alto en proteínas
@Query("SELECT a FROM Alimento a WHERE a.proteinas > :min ORDER BY a.proteinas DESC")
List<Alimento> findHighProtein(@Param("min") Double min);

// Búsqueda por rango nutricional
@Query("SELECT a FROM Alimento a WHERE a.calorias <= :maxCal AND a.proteinas >= :minProt")
List<Alimento> findByNutritionalRange(@Param("maxCal") Double maxCal, @Param("minProt") Double minProt);
```

### PlanRepository

```java
// Búsqueda por usuario
List<Plan> findByUsuarioId(Long usuarioId);

// Búsqueda por nombre de plan
List<Plan> findByNombrePlanContainingIgnoreCase(String nombre);

// Planes con rutina de ejercicio
@Query("SELECT p FROM Plan p WHERE p.rutinaEjercicio IS NOT NULL")
List<Plan> findPlanesConRutinaEjercicio();
```

### SalaDeGimnasioRepository

```java
// Búsqueda por nombre
Optional<SalaDeGimnasio> findByNombre(String nombre);

// Búsqueda por capacidad mínima
List<SalaDeGimnasio> findByCapacidadGreaterThanEqual(Integer capacidad);

// Búsqueda por ubicación
List<SalaDeGimnasio> findByUbicacionContainingIgnoreCase(String ubicacion);
```

### TokenRevocadoRepository

```java
// Verificar si existe token revocado
boolean existsByJti(String jti);

// Eliminar tokens expirados
@Modifying
@Query("DELETE FROM TokenRevocado t WHERE t.expiresAt < :date")
void deleteByExpiresAtBefore(@Param("date") LocalDateTime date);
```

---

## 🧪 Testing y Evidencias

### Colección de Postman

Se incluye una colección completa en `postman/Cofira_API_Collection.json` con:

1. **Registro de usuarios** (USER y ADMIN)
2. **Login** y obtención de tokens
3. **GET `/auth/me`** con token válido
4. **CRUD completo** de todas las entidades
5. **Pruebas de autorización** (403 Forbidden)
6. **Logout** y revocación de tokens

### Capturas de Evidencias

#### 1. Login Exitoso

![Login](docs/screenshots/01_login.png)

#### 2. Usuario Actual (`/auth/me`)

![Me](docs/screenshots/02_me.png)

#### 3. Crear Plan como ADMIN

![Create Plan](docs/screenshots/03_create_plan_admin.png)

#### 4. Error 403 - USER intenta crear ejercicio (solo ADMIN)

![403 Forbidden](docs/screenshots/04_403_forbidden.png)

#### 5. CRUD de Rutinas con Token

![CRUD Rutinas](docs/screenshots/05_crud_rutinas.png)

#### 6. Logout y Token Revocado

![Logout](docs/screenshots/06_logout.png)

### Casos de Prueba

```bash
# 1. Registrar usuario USER
POST /auth/register
Body: { username: "user1", password: "pass123", rol: "USER" }
Expected: 201 Created + token

# 2. Login con credenciales válidas
POST /auth/login
Body: { username: "user1", password: "pass123" }
Expected: 200 OK + token JWT

# 3. Acceder a /auth/me con token
GET /auth/me
Headers: Authorization: Bearer <token>
Expected: 200 OK + datos del usuario

# 4. USER intenta crear ejercicio (solo ADMIN)
POST /api/ejercicios
Headers: Authorization: Bearer <token-user>
Expected: 403 Forbidden

# 5. ADMIN crea ejercicio exitosamente
POST /api/ejercicios
Headers: Authorization: Bearer <token-admin>
Expected: 201 Created

# 6. Logout invalida el token
POST /auth/logout
Headers: Authorization: Bearer <token>
Expected: 200 OK

# 7. Usar token revocado falla
GET /auth/me
Headers: Authorization: Bearer <token-revocado>
Expected: 401 Unauthorized
```

---

## 🛠️ Tecnologías Utilizadas

### Backend

- ☕ **Java 17** - Lenguaje de programación
- 🍃 **Spring Boot 4.0** - Framework principal
- 🔐 **Spring Security** - Seguridad y autenticación
- 🗄️ **Spring Data JPA** - Persistencia de datos
- 🔑 **JWT (jjwt 0.12.6)** - Autenticación basada en tokens
- ✅ **Bean Validation** - Validaciones de datos

### Base de Datos

- 🐘 **PostgreSQL 16** - Base de datos relacional
- 🐳 **Docker Compose** - Orquestación de contenedores
- 🔧 **Flyway/Liquibase** (opcional) - Migraciones

### Herramientas

- 📚 **Swagger/OpenAPI 3.0** - Documentación interactiva
- 📊 **Spring Actuator** - Monitoreo y métricas
- 🔨 **Gradle 8.x** - Gestión de dependencias
- 🌱 **Lombok** - Reducción de boilerplate
- 🐋 **pgAdmin 4** - Administración de PostgreSQL

### Testing

- ✅ **JUnit 5** - Framework de testing
- 🃏 **Mockito** - Mocking
- 🔍 **Postman** - Testing de API

---

## 📁 Archivos Importantes

```
cofira/
├── build.gradle                    # Dependencias y configuración Gradle
├── compose.yaml                    # Docker Compose (PostgreSQL + pgAdmin)
├── SWAGGER_GUIDE.md                # Guía de uso de Swagger
├── src/main/resources/
│   └── application.properties      # Configuración principal
├── src/main/java/.../config/
│   ├── SecurityConfig.java         # Configuración de seguridad
│   └── OpenApiConfig.java          # Configuración de Swagger
├── src/main/java/.../security/
│   ├── AuthTokenFilter.java        # Filtro JWT
│   ├── JwtUtils.java               # Utilidades JWT
│   └── UserDetailsServiceImpl.java # Carga de usuarios
└── postman/
    └── Cofira_API_Collection.json  # Colección Postman
```

---

## 🚦 Comandos Útiles

### Docker

```bash
# Iniciar servicios
docker compose up -d

# Ver logs
docker logs cofira-postgres
docker logs cofira-pgadmin

# Detener servicios
docker compose down

# Reiniciar servicios
docker compose restart

# Eliminar volúmenes (⚠️ borra datos)
docker compose down -v
```

### Gradle

```bash
# Compilar
./gradlew build

# Ejecutar
./gradlew bootRun

# Limpiar build
./gradlew clean

# Ver dependencias
./gradlew dependencies

# Ejecutar tests
./gradlew test
```

### Aplicación

```bash
# Health check
curl http://localhost:8080/actuator/health

# Info
curl http://localhost:8080/actuator/info

# Login
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

# Usuario actual
curl -X GET http://localhost:8080/auth/me \
  -H "Authorization: Bearer <token>"
```

---

## 📝 Notas Adicionales

### Variables de Entorno

Puedes sobrescribir la configuración con variables de entorno:

```bash
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/cofira
export SPRING_DATASOURCE_USERNAME=admin
export SPRING_DATASOURCE_PASSWORD=admin123
export COFIRA_JWT_SECRET=<tu-secreto>
export COFIRA_JWT_EXPIRATION=86400000
```

### Perfil de Desarrollo

Para habilitar logs detallados:

```properties
# application-dev.properties
logging.level.com.gestioneventos.cofira=DEBUG
logging.level.org.springframework.security=DEBUG
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

Ejecutar con perfil dev:

```bash
./gradlew bootRun --args='--spring.profiles.active=dev'
```

### Regenerar JWT Secret

```bash
# Generar secreto seguro (mínimo 512 bits para HS512)
openssl rand -hex 64
```


## 👤 Autor

**Alejandro Bravo Calderón**

- GitHub: [@Alejandro-Bravo2](https://github.com/Alejandro-Bravo2)
- Email: [alejandro@example.com](mailto:alejandro@example.com)

---

## 🔗 Enlaces Útiles

- 📚 **Swagger UI**: http://localhost:8080/swagger-ui.html
- 📊 **Actuator**: http://localhost:8080/actuator
- 🐘 **pgAdmin**: http://localhost:5050
- 📖 **Spring Boot Docs**: https://docs.spring.io/spring-boot/docs/current/reference/html/
- 🔐 **JWT.io**: https://jwt.io

---

**Última actualización:** 11 de diciembre de 2025
