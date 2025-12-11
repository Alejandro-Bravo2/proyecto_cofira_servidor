# 🎨 Visualización de Mejoras - Arquitectura Antes vs Después

## 📊 Arquitectura ANTES (Problemas)

```
┌─────────────────────────────────────────────────────────────────┐
│                    EjerciciosController                         │
│  POST: Recibe Ejercicios (Entity)                               │
│  GET: Devuelve Ejercicios (Entity)                              │
└──────────────────────────┬──────────────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────────────┐
│                    EjerciciosService                            │
│  • Recibe Entity directa ❌                                      │
│  • Retorna Entity directa ❌                                     │
│  • RuntimeException genérica ❌                                  │
│  • No valida si sala existe ❌                                   │
│  • Actualización incompleta ❌                                   │
│  • Sin validaciones de negocio ❌                                │
└──────────────────────────┬──────────────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────────────┐
│                  EjerciciosRepository                           │
│              Base de Datos (Ejercicios)                         │
└─────────────────────────────────────────────────────────────────┘

PROBLEMAS:
❌ Cambios en BD = cambios en API
❌ Sin aislamiento de datos
❌ Validaciones débiles
❌ Excepciones poco específicas
❌ HTTP 200 para creaciones
```

---

## ✅ Arquitectura DESPUÉS (Mejorada)

```
┌──────────────────────────────────────────────────────────────────┐
│                   EjerciciosController                           │
│  POST: Recibe CrearEjerciciosDTO (con @Valid)                   │
│  GET:  Devuelve EjerciciosDTO (sin lógica)                       │
│  PUT:  Recibe ModificarEjerciciosDTO (con @Valid)               │
│  HTTP 201 CREATED para POST ✅                                   │
└────────────────────────┬─────────────────────────────────────────┘
                         │ DTOs con validaciones
                         ▼
┌──────────────────────────────────────────────────────────────────┐
│                    EjerciciosService                             │
│  ✅ Entrada: CrearEjerciciosDTO                                  │
│  ✅ Salida:  EjerciciosDTO                                       │
│  ✅ Excepciones específicas (RecursoNoEncontradoException)       │
│  ✅ Valida si sala existe antes de crear                         │
│  ✅ Actualización completa (todos los campos)                    │
│  ✅ Conversión automática Entity ↔ DTO                           │
│                                                                  │
│  Métodos:                                                        │
│  • listarEjercicios() → List<EjerciciosDTO>                      │
│  • obtenerEjercicio(id) → EjerciciosDTO                          │
│  • crearEjercicio(CrearEjerciciosDTO) → EjerciciosDTO            │
│  • actualizarEjercicio(id, ModificarEjerciciosDTO) → EjerciciosDTO
│  • eliminarEjercicio(id) → void                                  │
│  • convertirADTO(Ejercicios) → EjerciciosDTO [PRIVADO]          │
└────────────────────────┬─────────────────────────────────────────┘
                         │
                         ▼
┌──────────────────────────────────────────────────────────────────┐
│                  EjerciciosRepository                            │
│              Base de Datos (Ejercicios)                          │
└──────────────────────────────────────────────────────────────────┘

BENEFICIOS:
✅ Cambios en BD NO afectan API (solo DTOs)
✅ Aislamiento de datos (Entities son internas)
✅ Validaciones robustas (multinivel)
✅ Excepciones específicas
✅ HTTP 201 para creaciones
✅ Mapeo automático Entity ↔ DTO
```

---

## 🔄 Flujos de Datos Detallados

### POST: Crear Ejercicio

**ANTES:**

```
Cliente
  ↓
POST /ejercicios {nombreEjercicio, series, repeticiones, salaDeGimnasioId}
  ↓ (Recibe Ejercicios Entity)
EjerciciosController
  ↓
EjerciciosService.crearEjercicio(Ejercicios) ❌ AceptaEntity
  - Sin validación de sala
  - Guardar tal cual
  ↓
BD
  ↓
Retorna HTTP 200 ❌ Debería ser 201
  ↓
Cliente recibe Ejercicios Entity (expone estructura BD)
```

**DESPUÉS:**

```
Cliente
  ↓
POST /ejercicios {nombreEjercicio, series, repeticiones, salaDeGimnasioId}
  ↓ (Recibe CrearEjerciciosDTO)
EjerciciosController (@Valid valida)
  - nombreEjercicio: @NotBlank
  - series: @Positive
  - repeticiones: @Positive
  - salaDeGimnasioId: @NotNull
  ↓
EjerciciosService.crearEjercicio(CrearEjerciciosDTO)
  1. Valida salaDeGimnasioId existe (en BD)
  2. Crea Ejercicios Entity
  3. Asigna valores de DTO
  4. Guarda en BD
  5. Convierte Entity → EjerciciosDTO
  ↓
BD
  ↓
Retorna HTTP 201 CREATED ✅
  ↓
Cliente recibe EjerciciosDTO (solo campos necesarios)
```

---

## 🛡️ Validación Multinivel

```
NIVEL 1: DTO Annotations
┌─────────────────────────────────────────┐
│ @NotNull, @NotBlank, @Positive, etc.   │
│ Ejecutadas ANTES de llegar a Service   │
└─────────────────────────────────────────┘
                  ↓
NIVEL 2: Service Logic
┌─────────────────────────────────────────┐
│ • Validar relaciones (sala, usuario)    │
│ • Validar lógica de negocio             │
│ • Convertir DTOs                        │
└─────────────────────────────────────────┘
                  ↓
NIVEL 3: Global Exception Handler
┌─────────────────────────────────────────┐
│ • RecursoNoEncontradoException          │
│ • BadRequestException                   │
│ • Otra excepción personalizada          │
│ → Retorna JSON error con HTTP status    │
└─────────────────────────────────────────┘
```

---

## 📝 Ejemplo: CrearEjerciciosDTO

**ANTES:** Recibía Ejercicios Entity

```java
@PostMapping
public ResponseEntity<Ejercicios> crearEjercicio(
    @RequestBody Ejercicios ejercicio) {  // ❌ Entity directa
    return ResponseEntity.ok(
        ejerciciosService.crearEjercicio(ejercicio)
    );
}
```

**DESPUÉS:** Recibe DTO Estructurado

```java
@PostMapping
public ResponseEntity<EjerciciosDTO> crearEjercicio(
    @RequestBody @Valid CrearEjerciciosDTO dto) {  // ✅ DTO
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ejerciciosService.crearEjercicio(dto));
}
```

**CrearEjerciciosDTO:**

```java
@Data
public class CrearEjerciciosDTO {
    @NotBlank(message = "El nombre del ejercicio no puede estar vacío")
    private String nombreEjercicio;

    @NotNull(message = "El número de series no puede ser nulo")
    @Positive(message = "El número de series debe ser mayor a 0")
    private Integer series;

    @NotNull(message = "El número de repeticiones no puede ser nulo")
    @Positive(message = "El número de repeticiones debe ser mayor a 0")
    private Integer repeticiones;

    @NotNull(message = "El ID de la sala no puede ser nulo")
    private Long salaDeGimnasioId;
}
```

---

## 🔗 DTOs Creados/Mejorados

### Ejercicios

- `CrearEjerciciosDTO` - Mejorado con @Positive
- `ModificarEjerciciosDTO` - Mejorado con @Positive + salaDeGimnasioId
- `EjerciciosDTO` - Nuevo mapeo automático

### Plan

- `CrearPlanDTO` - Mejorado con @Positive
- `ModificarPlanDTO` - Mejorado con @Positive + usuarioId
- `PlanDTO` - Mapeo automático

### Sala

- `CrearSalaDTO` - Con validaciones
- `ModificarSalaDTO` - Con validaciones
- `SalaDTO` - Mapeo automático

### Objetivos

- `CrearObjetivosDTO` - Con validaciones
- `ModificarObjetivosDTO` - Con validaciones
- `ObjetivosDTO` - Mapeo automático

---

## 🎯 Resumen de Mejoras por Componente

| Componente      | Mejora                                          | Beneficio               |
| --------------- | ----------------------------------------------- | ----------------------- |
| **DTOs**        | Agregadas validaciones                          | Validación temprana     |
| **Services**    | Entity → DTO                                    | Aislamiento de datos    |
| **Services**    | RuntimeException → RecursoNoEncontradoException | Excepciones específicas |
| **Services**    | Validación de relaciones                        | Integridad referencial  |
| **Services**    | Método convertirADTO()                          | Mapeo automático        |
| **Controllers** | Entity → DTO entrada/salida                     | Contrato limpio         |
| **Controllers** | HTTP 201 en POST                                | REST compliant          |
| **Controllers** | @Valid en DTOs                                  | Validación automática   |
| **Excepciones** | BadRequestException nuevo                       | Errores de negocio      |
