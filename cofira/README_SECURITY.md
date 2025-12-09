# Documentación de Seguridad - API Cofira

## Descripción General

La API Cofira implementa autenticación y autorización basada en JWT (JSON Web Tokens) con dos roles de usuario: **PARTICIPANTE** y **ORGANIZADOR**.

### Características de Seguridad

- **Autenticación JWT**: Tokens firmados con HS512
- **Autorización basada en roles**: PARTICIPANTE y ORGANIZADOR
- **Blacklist de tokens**: Tokens revocados persistidos en base de datos
- **CORS configurado**: Para clientes Angular en localhost:4200 y localhost:3000
- **Contraseñas hasheadas**: Usando BCrypt
- **Sesiones stateless**: No se mantiene estado en el servidor

## Roles y Permisos

### PARTICIPANTE
- Puede ver información (GET)
- Puede gestionar su propio perfil
- **NO puede** crear, modificar o eliminar recursos del sistema

### ORGANIZADOR
- Todos los permisos de PARTICIPANTE
- Puede crear salas de gimnasio (POST /salas)
- Puede modificar salas (PUT /salas/{id})
- Puede eliminar salas (DELETE /salas/{id})
- Puede gestionar ejercicios, planes, etc.

## Endpoints de Autenticación

### POST /auth/register
Registro de nuevo usuario.

**Request:**
```json
{
  "nombre": "Juan Pérez",
  "username": "juanperez",
  "email": "juan@example.com",
  "password": "password123",
  "rol": "PARTICIPANTE"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "type": "Bearer",
  "id": 1,
  "username": "juanperez",
  "email": "juan@example.com",
  "rol": "PARTICIPANTE"
}
```

**Notas:**
- El rol por defecto es `PARTICIPANTE` si no se especifica
- El password se hashea automáticamente con BCrypt
- Retorna un token JWT válido por 24 horas

### POST /auth/login
Inicio de sesión.

**Request:**
```json
{
  "username": "juanperez",
  "password": "password123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "type": "Bearer",
  "id": 1,
  "username": "juanperez",
  "email": "juan@example.com",
  "rol": "PARTICIPANTE"
}
```

### GET /auth/me
Obtiene información del usuario autenticado.

**Headers:**
```
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
```

**Response:**
```json
{
  "id": 1,
  "nombre": "Juan Pérez",
  "username": "juanperez",
  "email": "juan@example.com",
  "rol": "PARTICIPANTE",
  "edad": 25,
  "peso": 70.5,
  "altura": 1.75
}
```

### POST /auth/logout
Revoca el token actual (lo añade a la blacklist).

**Headers:**
```
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
```

**Response:**
```
"Logout exitoso"
```

## Rutas Públicas vs Protegidas

### Rutas Públicas (sin autenticación)
- `POST /auth/register` - Registro
- `POST /auth/login` - Login
- `/h2-console/**` - Consola H2 (solo desarrollo)
- `/actuator/**` - Endpoints de monitoreo
- `/swagger-ui/**` - Documentación Swagger
- `/v3/api-docs/**` - OpenAPI docs

### Rutas Protegidas (requieren autenticación)
- `GET /auth/me` - Perfil del usuario
- `POST /auth/logout` - Cerrar sesión
- `GET /usuarios` - Listar usuarios (cualquier autenticado)
- `GET /usuarios/{id}` - Ver usuario (cualquier autenticado)
- `GET /salas` - Listar salas (cualquier autenticado)
- `GET /salas/{id}` - Ver sala (cualquier autenticado)

### Rutas Restringidas a ORGANIZADOR
- `POST /salas` - Crear sala
- `PUT /salas/{id}` - Modificar sala
- `DELETE /salas/{id}` - Eliminar sala
- Creación/modificación/eliminación de ejercicios, planes, etc.

## Uso del Token JWT

### Incluir el token en las peticiones

Todas las rutas protegidas requieren el token JWT en el header `Authorization`:

```
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJqdWFucGVyZXoiLCJyb2xlcyI6IlJPTEVfUEFSVElDSVBBTlRFIiwiZW1haWwiOiJqdWFuQGV4YW1wbGUuY29tIiwianRpIjoiNTIzYTM5ZTUtN2Y4Yi00NzQ2LWE4YTItOWRkZjJhMzM1YjJhIiwiaWF0IjoxNzMzNzg4ODAwLCJleHAiOjE3MzM4NzUyMDB9.signature
```

### Información del Token

El token JWT contiene:
- `sub`: username del usuario
- `roles`: rol del usuario (ROLE_PARTICIPANTE o ROLE_ORGANIZADOR)
- `email`: email del usuario
- `jti`: ID único del token (para blacklist)
- `iat`: fecha de emisión
- `exp`: fecha de expiración (24 horas)

### Ejemplo con cURL

```bash
# Login
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"juanperez","password":"password123"}'

# Usar el token
curl -X GET http://localhost:8080/auth/me \
  -H "Authorization: Bearer <TOKEN_AQUI>"

# Crear sala (solo ORGANIZADOR)
curl -X POST http://localhost:8080/salas \
  -H "Authorization: Bearer <TOKEN_ORGANIZADOR>" \
  -H "Content-Type: application/json" \
  -d '{"fechaInicio":"2024-01-01","fechaFin":"2024-12-31"}'
```

### Ejemplo con JavaScript/Angular

```javascript
// Guardar token después del login
localStorage.setItem('token', response.token);

// Incluir en peticiones HTTP
const headers = {
  'Authorization': `Bearer ${localStorage.getItem('token')}`,
  'Content-Type': 'application/json'
};

fetch('http://localhost:8080/auth/me', { headers })
  .then(res => res.json())
  .then(data => console.log(data));
```

## Configuración CORS

CORS está configurado para permitir:

**Orígenes permitidos:**
- `http://localhost:4200` (Angular)
- `http://localhost:3000` (React/otros)

**Métodos permitidos:**
- GET, POST, PUT, DELETE, PATCH, OPTIONS

**Headers permitidos:**
- Authorization
- Content-Type
- X-Requested-With

## Blacklist de Tokens

Los tokens revocados (logout) se almacenan en una tabla `revoked_tokens` con:
- `jti`: ID único del token
- `expires_at`: fecha de expiración
- `revoked_at`: fecha de revocación

Los tokens caducados se limpian automáticamente de la blacklist.

## Manejo de Errores

### 401 Unauthorized
- Token no proporcionado
- Token inválido o expirado
- Token en blacklist (revocado)

### 403 Forbidden
- Usuario autenticado pero sin permisos suficientes
- Ejemplo: PARTICIPANTE intentando crear una sala

### 400 Bad Request
- Datos de login/registro inválidos
- Email o username duplicados

## Configuración de Desarrollo

### Arrancar la API

```bash
./gradlew bootRun
```

La API estará disponible en `http://localhost:8080`

### Consola H2

Acceder a la base de datos H2 en:
```
http://localhost:8080/h2-console
```

Credenciales:
- JDBC URL: `jdbc:h2:mem:cofira`
- Username: `sa`
- Password: `sa`

### Swagger UI

Documentación interactiva en:
```
http://localhost:8080/swagger-ui.html
```

### Actuator Endpoints

Métricas y salud de la aplicación:
```
http://localhost:8080/actuator/health
http://localhost:8080/actuator/metrics
http://localhost:8080/actuator/info
```

## Extras Implementados

### ✅ Blacklist Persistente
Los tokens revocados se guardan en base de datos y se limpian automáticamente cuando expiran.

### ✅ CORS Completo
Configuración de CORS para desarrollo con Angular/React incluyendo Authorization header.

### ✅ Actuator
Endpoints de monitoreo habilitados para verificar salud y métricas de la aplicación.

### ✅ Swagger/OpenAPI
Documentación interactiva de la API disponible en `/swagger-ui.html`.

## Notas de Seguridad

- El `jwt.secret` debe ser un valor seguro en producción (128+ bits)
- En producción, usar HTTPS para todas las comunicaciones
- Los tokens tienen una validez de 24 horas
- Las contraseñas se hashean con BCrypt (factor 10)
- La blacklist se limpia periódicamente de tokens expirados

## Testing

### Crear un ORGANIZADOR

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

### Crear un PARTICIPANTE

```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "nombre":"Regular User",
    "username":"user",
    "email":"user@cofira.com",
    "password":"user123",
    "rol":"PARTICIPANTE"
  }'
```

### Probar 403 Forbidden

Intentar crear una sala con token de PARTICIPANTE:

```bash
# 1. Login como PARTICIPANTE
TOKEN=$(curl -s -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"user","password":"user123"}' \
  | jq -r '.token')

# 2. Intentar crear sala (debe fallar con 403)
curl -X POST http://localhost:8080/salas \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"fechaInicio":"2024-01-01","fechaFin":"2024-12-31"}'
```

## Soporte

Para más información, consultar:
- Código fuente en `/src/main/java/com/gestioneventos/cofira/`
- Configuración de seguridad en `SecurityConfig.java`
- Lógica JWT en `JwtUtils.java` y `AuthTokenFilter.java`
