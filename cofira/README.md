# API Cofira - Sistema de Gestión de Gimnasio

## Descripción

API RESTful completa para la gestión de un gimnasio con autenticación JWT, autorización basada en roles, y endpoints CRUD para usuarios, salas, ejercicios, planes y alimentación.

## Características Principales

### ✅ Seguridad Completa
- **Autenticación JWT**: Tokens firmados con HS512, validez de 24 horas
- **Autorización basada en roles**: PARTICIPANTE y ORGANIZADOR
- **Blacklist de tokens**: Persistencia en BD con limpieza automática
- **CORS configurado**: Para clientes frontend (Angular/React)
- **Contraseñas hasheadas**: BCrypt con factor 10
- **Sesiones stateless**: Sin estado en servidor

### ✅ Arquitectura
- **Entidades JPA**: 7 entidades con relaciones completas
- **DTOs**: Separación entre entidades y datos de transferencia
- **Servicios CRUD**: Operaciones completas en todos los recursos
- **Repositorios con consultas personalizadas**: Queries optimizadas
- **Controllers REST**: Endpoints bien organizados
- **Validaciones**: Jakarta Bean Validation en todos los DTOs

### ✅ Monitoreo y Documentación
- **Spring Boot Actuator**: Health checks y métricas
- **Swagger/OpenAPI**: Documentación interactiva de la API
- **H2 Console**: Acceso a la base de datos en desarrollo

## Tecnologías Utilizadas

- **Java 17**
- **Spring Boot 4.0.0**
- **Spring Security 6**
- **JWT (jjwt 0.12.3)**
- **Spring Data JPA**
- **H2 Database**
- **Lombok**
- **SpringDoc OpenAPI 2.3.0**
- **Spring Boot Actuator**
- **Gradle 9.2.1**

## Modelo de Datos

### Entidades

1. **Usuario**
   - Atributos: id, nombre, username, email, password, rol, edad, peso, altura
   - Relaciones: 1:1 con Objetivos y Plan, N:1 con SalaDeGimnasio y Alimento

2. **Objetivos**
   - Atributos: id, listaObjetivos
   - Relación: 1:1 con Usuario

3. **Plan**
   - Atributos: id, precio, subscripcionActiva
   - Relación: 1:1 con Usuario

4. **SalaDeGimnasio**
   - Atributos: id, fechaInicio, fechaFin
   - Relaciones: 1:N con Ejercicios y Usuarios

5. **Ejercicios**
   - Atributos: id, nombreEjercicio, series, repeticiones
   - Relación: N:1 con SalaDeGimnasio

6. **Alimento**
   - Atributos: id, alimentosFavoritos (lista), listaAlergias (lista)
   - Relación: 1:N con Usuario

7. **TokenRevocado**
   - Atributos: id, jti, expiresAt, revokedAt
   - Para blacklist de JWT

### Diagrama ER

```
Usuario (1) ---- (1) Objetivos
Usuario (1) ---- (1) Plan
Usuario (N) ---- (1) SalaDeGimnasio
Usuario (N) ---- (1) Alimento
SalaDeGimnasio (1) ---- (N) Ejercicios
```

## Instalación y Arranque

### Requisitos Previos

- Java 17+
- Gradle 9.2+

### Arrancar la Aplicación

```bash
# Clonar el repositorio
git clone <repository-url>
cd cofira

# Compilar
./gradlew clean build

# Ejecutar
./gradlew bootRun
```

La aplicación estará disponible en `http://localhost:8080`

## Endpoints Principales

### Autenticación

| Método | Endpoint | Descripción | Autenticación |
|--------|----------|-------------|---------------|
| POST | /auth/register | Registrar nuevo usuario | Público |
| POST | /auth/login | Iniciar sesión | Público |
| GET | /auth/me | Perfil del usuario autenticado | Requiere JWT |
| POST | /auth/logout | Cerrar sesión (revocar token) | Requiere JWT |

### Usuarios

| Método | Endpoint | Descripción | Permiso |
|--------|----------|-------------|---------|
| GET | /usuarios | Listar usuarios (paginado) | Autenticado |
| GET | /usuarios/{id} | Obtener usuario por ID | Autenticado |
| POST | /usuarios | Crear usuario | Autenticado |
| PUT | /usuarios/{id} | Actualizar usuario | Autenticado |
| DELETE | /usuarios/{id} | Eliminar usuario | Autenticado |

### Salas de Gimnasio

| Método | Endpoint | Descripción | Permiso |
|--------|----------|-------------|---------|
| GET | /salas | Listar salas | Autenticado |
| GET | /salas/{id} | Obtener sala por ID | Autenticado |
| POST | /salas | Crear sala | ORGANIZADOR |
| PUT | /salas/{id} | Actualizar sala | ORGANIZADOR |
| DELETE | /salas/{id} | Eliminar sala | ORGANIZADOR |

### Ejercicios, Planes, Objetivos, Alimentos

Endpoints similares con sus respectivos permisos.

## Roles y Permisos

### PARTICIPANTE (por defecto)
- Puede ver información (GET)
- Puede gestionar su propio perfil
- NO puede crear, modificar o eliminar recursos administrativos

### ORGANIZADOR
- Todos los permisos de PARTICIPANTE
- Puede crear, modificar y eliminar salas
- Puede gestionar ejercicios, planes, etc.

## Ejemplos de Uso

### 1. Registrarse como ORGANIZADOR

```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "nombre":"Admin User",
    "username":"admin",
    "email":"admin@cofira.com",
    "password":"admin123",
    "rol":"ORGANIZADOR"
  }'
```

**Respuesta:**
```json
{
  "token":"eyJhbGciOiJIUzUxMiJ9...",
  "type":"Bearer",
  "id":1,
  "username":"admin",
  "email":"admin@cofira.com",
  "rol":"ORGANIZADOR"
}
```

### 2. Iniciar Sesión

```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username":"admin",
    "password":"admin123"
  }'
```

### 3. Obtener Perfil

```bash
curl -X GET http://localhost:8080/auth/me \
  -H "Authorization: Bearer <TOKEN>"
```

### 4. Crear Sala (solo ORGANIZADOR)

```bash
curl -X POST http://localhost:8080/salas \
  -H "Authorization: Bearer <TOKEN_ORGANIZADOR>" \
  -H "Content-Type: application/json" \
  -d '{
    "fechaInicio":"2024-01-01",
    "fechaFin":"2024-12-31"
  }'
```

### 5. Listar Salas (cualquier autenticado)

```bash
curl -X GET http://localhost:8080/salas \
  -H "Authorization: Bearer <TOKEN>"
```

## Acceso a Recursos

### H2 Console (Solo Desarrollo)

```
URL: http://localhost:8080/h2-console
JDBC URL: jdbc:h2:mem:cofira
Username: sa
Password: sa
```

### Swagger UI (Documentación Interactiva)

```
URL: http://localhost:8080/swagger-ui.html
```

### Actuator Endpoints

```
Health: http://localhost:8080/actuator/health
Info: http://localhost:8080/actuator/info
Metrics: http://localhost:8080/actuator/metrics
```

## Configuración

### application.properties

```properties
# Servidor
server.port=8080

# Base de Datos H2
spring.datasource.url=jdbc:h2:mem:cofira
spring.h2.console.enabled=true

# JWT
cofira.jwt.secret=<secret-key-128-bits>
cofira.jwt.expiration=86400000

# Actuator
management.endpoints.web.exposure.include=health,info,metrics,env

# Swagger
springdoc.swagger-ui.path=/swagger-ui.html
```

## Seguridad

Para información detallada sobre seguridad, consultar [README_SECURITY.md](./README_SECURITY.md).

### Aspectos Clave

- **Tokens JWT**: Incluir en header `Authorization: Bearer <token>`
- **Blacklist**: Los tokens se revocan al hacer logout
- **Expiración**: Tokens válidos por 24 horas
- **CORS**: Configurado para localhost:4200 y localhost:3000
- **Contraseñas**: Hasheadas con BCrypt, nunca almacenadas en texto plano

## Testing

### Verificar Funcionamiento

```bash
# 1. Registrar ORGANIZADOR
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Admin","username":"admin","email":"admin@test.com","password":"admin123","rol":"ORGANIZADOR"}'

# 2. Registrar PARTICIPANTE
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"nombre":"User","username":"user","email":"user@test.com","password":"user123","rol":"PARTICIPANTE"}'

# 3. Login y guardar token
TOKEN=$(curl -s -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}' \
  | grep -o '"token":"[^"]*"' | cut -d'"' -f4)

# 4. Crear sala (debe funcionar con ORGANIZADOR)
curl -X POST http://localhost:8080/salas \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"fechaInicio":"2024-01-01","fechaFin":"2024-12-31"}'

# 5. Verificar 403 con PARTICIPANTE
TOKEN_USER=$(curl -s -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"user","password":"user123"}' \
  | grep -o '"token":"[^"]*"' | cut -d'"' -f4)

curl -w "\nStatus: %{http_code}\n" -X POST http://localhost:8080/salas \
  -H "Authorization: Bearer $TOKEN_USER" \
  -H "Content-Type: application/json" \
  -d '{"fechaInicio":"2024-01-01","fechaFin":"2024-12-31"}'
```

## Estructura del Proyecto

```
src/main/java/com/gestioneventos/cofira/
├── config/              # Configuración (Security, CORS)
├── controllers/         # Controllers REST
├── dto/                 # Data Transfer Objects
│   ├── auth/           # DTOs de autenticación
│   ├── usuario/        # DTOs de usuario
│   ├── objetivos/      # DTOs de objetivos
│   ├── plan/           # DTOs de plan
│   ├── sala/           # DTOs de sala
│   ├── ejercicios/     # DTOs de ejercicios
│   └── alimento/       # DTOs de alimento
├── entities/           # Entidades JPA
├── enums/              # Enumeraciones (Rol)
├── repositories/       # Repositorios JPA
├── security/           # Componentes de seguridad (JWT, Filters)
└── services/           # Servicios de negocio
```

## Consultas Personalizadas Disponibles

- Usuarios por rango de edad
- Usuarios con plan activo
- Usuarios por sala
- Salas activas en una fecha
- Ejercicios por nombre
- Planes por rango de precio
- Y más...

## Contribuir

1. Fork el proyecto
2. Crear una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abrir un Pull Request

## Licencia

Este proyecto es parte de un ejercicio académico.

## Autor

Proyecto Cofira - Sistema de Gestión de Gimnasio

## Soporte

Para más información, consultar:
- [Documentación de Seguridad](./README_SECURITY.md)
- [Swagger UI](http://localhost:8080/swagger-ui.html)
- [H2 Console](http://localhost:8080/h2-console)
- [Actuator Health](http://localhost:8080/actuator/health)
