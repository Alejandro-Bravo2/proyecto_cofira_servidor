### **Estado Actual del Proyecto y Aspectos Pendientes**

Con base en el flujo del desarrollo y las necesidades planteadas, aquí tienes un análisis detallado de lo que le falta al proyecto para considerarse completo y robusto.

---

### **1. Gestión de Imágenes (Pendiente)**
- **Estado actual:**
    - El manejo de imágenes está planeado pero no implementado.
    - Aún no existe el campo `avatar` en el modelo `Usuario` ni su validación.
    - Tampoco hay controladores o servicios específicos para manejar subidas de archivos.

- **Acciones pendientes:**
    1. **Agregar el campo `avatar` a la entidad `Usuario`.**
        - Validar formatos aceptados (`jpg`, `png`, etc.) y establecer un tamaño máximo.
    2. **Crear un controlador para subir imágenes.**
        - Asegurar el almacenamiento local o en un servicio externo como AWS S3.
        - Devolver la ruta de la imagen almacenada al cliente.
    3. **Endpoint para recuperar imágenes.**
        - Proporcionar un endpoint para servir imágenes almacenadas.

---

### **2. Roles y Autorización**
- **Estado actual:**
    - Los roles `USUARIO` y `BIBLIOTECARIO` están definidos.
    - Se usa `@PreAuthorize` para restringir accesos.
    - Sin embargo, la asignación de roles es básica y no contempla casos de uso avanzados.

- **Acciones pendientes:**
    1. **Validar y restringir asignación de roles:**
        - Solo bibliotecarios deberían poder cambiar roles.
    2. **Endpoint para listar y modificar roles.**
        - Crear un servicio/controlador para administrar roles.
    3. **Añadir más roles si es necesario:**
        - Por ejemplo, `SUPER_ADMIN` o roles específicos según el dominio.

---

### **3. Testeo y Cobertura**
- **Estado actual:**
    - Existen tests unitarios y de integración para las funcionalidades principales.
    - No hay cobertura exhaustiva en todas las nuevas funcionalidades (como JWT, DTOs o manejo de errores).

- **Acciones pendientes:**
    1. **Crear tests específicos para:**
        - **JWT:** Validar autenticación y autorización.
        - **Manejo de errores global:** Asegurarse de que los mensajes personalizados funcionan.
        - **Subida y recuperación de imágenes.**
    2. **Integrar cobertura de tests (opcional):**
        - Herramientas como Jacoco o SonarQube para medir la cobertura.

---

### **4. Experiencia de Usuario y Endpoints**
- **Estado actual:**
    - Los endpoints principales están definidos.
    - Falta mejorar algunos flujos y facilitar pruebas (por ejemplo, en el flujo de registro o creación de libros).

- **Acciones pendientes:**
    1. **Mejorar la documentación de la API:**
        - Usar **Swagger** o **OpenAPI** para que los endpoints sean más claros.
    2. **Validar entradas y manejar errores:**
        - Mejorar mensajes de error para que sean más comprensibles para el cliente.
    3. **Endpoints para búsquedas avanzadas:**
        - Por ejemplo, libros por múltiples criterios, usuarios con préstamos vencidos, etc.

---

### **5. Optimización y Seguridad**
- **Estado actual:**
    - JWT está implementado, pero necesita pruebas avanzadas.
    - Faltan configuraciones de seguridad más específicas, como protección contra ataques CSRF o fuerza bruta.

- **Acciones pendientes:**
    1. **Añadir expiración y renovación de tokens.**
        - Implementar un endpoint para renovar el token antes de que expire.
    2. **Seguridad de subida de archivos:**
        - Asegurarse de que no se suban archivos maliciosos.
    3. **Auditoría de roles y permisos:**
        - Revisar permisos actuales y asegurarse de que están bien definidos.

---

### **6. Flujo de Trabajo**
- **Estado actual:**
    - Los flujos de autenticación, préstamos y manejo de libros funcionan.
    - Falta integración total con imágenes y roles avanzados.

- **Acciones pendientes:**
    1. **Optimizar flujos:**
        - Simplificar la asignación de roles en el registro.
        - Validar estados de libros al realizar operaciones (por ejemplo, no permitir préstamo de libros ya prestados).
    2. **Revisión final de DTOs:**
        - Asegurarse de que todos los DTOs están alineados con los requisitos del cliente.

---

### **7. Opcionales**
1. **Integración con Frontend (React, Vue, etc.):**
    - Crear un frontend sencillo para probar y visualizar el sistema.
2. **Notificaciones:**
    - Enviar correos electrónicos o notificaciones cuando haya acciones importantes (por ejemplo, préstamo vencido).
3. **Internacionalización:**
    - Agregar soporte para múltiples idiomas.

---

### **Siguientes Pasos**
1. Implementar el **manejo de imágenes**.
2. Refinar los roles y la autorización.
3. Mejorar la documentación y las pruebas.
4. Revisar la seguridad del sistema.

¿Te gustaría empezar con alguno de estos puntos o necesitas más detalle en alguno en particular? 😊