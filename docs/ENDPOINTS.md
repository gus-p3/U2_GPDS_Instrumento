# 📌 Colección de Endpoints de la API - Gestor de Proyectos

Documentación completa de los endpoints para pruebas en **Postman**.

> **Variable de entorno recomendada en Postman:**  
> `{{base_url}}` = `http://localhost:8081`  
> *(Definido en `application.properties`: `server.port=8081` sin prefijo de servlet context-path)*

---

## 📋 Índice
1. [Configuración Inicial en Postman](#-configuración-inicial-en-postman)
2. [Módulo: Usuarios](#1-módulo-usuarios)
3. [Módulo: Proyectos](#2-módulo-proyectos)
4. [Módulo: Tareas](#3-módulo-tareas)
5. [Módulo: Comentarios](#4-módulo-comentarios)
6. [Módulo: Asignaciones](#5-módulo-asignaciones)
7. [⚠️ Endpoints Faltantes o Recomendados (No expuestos actualmente)](#️-endpoints-faltantes-o-recomendados-no-expuestos-actualmente)

---

## ⚙️ Configuración Inicial en Postman

- Crea un nuevo Environment o Collection en Postman y añade la variable:
  - **Variable:** `base_url`
  - **Initial Value:** `http://localhost:8081`
  - **Current Value:** `http://localhost:8081`
- En todas las peticiones con cuerpo (`POST`, `PUT`, `DELETE` con body), agrega el encabezado:
  - **Header:** `Content-Type: application/json`

---

## 1. Módulo: Usuarios

Controlador: `UsuarioController.java`

---

### 1.1 Iniciar Sesión (Login)
Valida las credenciales de un usuario mediante correo y contraseña.

- **Método:** `POST`
- **URL:** `{{base_url}}/getUsuarioLogin`
- **Headers:** `Content-Type: application/json`
- **Body (raw JSON):**
```json
{
  "correo_electronico": "ana22533@cbtis75.edu.mx",
  "contrasena": "0722"
}
```
- **Respuesta esperada (200 OK):**
```json
{
  "id_usuario": 131,
  "nombre": "Ana Paola Ramírez Vázquez",
  "correo_electronico": "ana22533@cbtis75.edu.mx",
  "contrasena": "0722",
  "rol": "Estudiante",
  "imagen_usuario": "#70FAB7"
}
```
> *Nota: Si las credenciales no coinciden, la API devuelve un objeto `Usuario` con `nombre: "000"`.*

---

### 1.2 Buscar Usuario por ID
Obtiene los detalles de un usuario a partir de su ID enviado por parámetro URL.

- **Método:** `GET`
- **URL:** `{{base_url}}/buscarUsuario?id=131`
- **Query Params:**
  - `id`: `131` (Número entero)
- **Body:** *Ninguno*
- **Respuesta esperada (200 OK):**
```json
{
  "id_usuario": 131,
  "nombre": "Ana Paola Ramírez Vázquez",
  "correo_electronico": "ana22533@cbtis75.edu.mx",
  "contrasena": "0722",
  "rol": "Estudiante",
  "imagen_usuario": "#70FAB7"
}
```

---

### 1.3 Obtener Todos los Usuarios
Lista todos los usuarios registrados en el sistema.

- **Método:** `POST` *(Nota: Está mapeado con `@PostMapping` en el controlador)*
- **URL:** `{{base_url}}/buscarUsuarios`
- **Body:** *Ninguno*
- **Respuesta esperada (200 OK):**
```json
[
  {
    "id_usuario": 131,
    "nombre": "Ana Paola Ramírez Vázquez",
    "correo_electronico": "ana22533@cbtis75.edu.mx",
    "contrasena": "0722",
    "rol": "Estudiante",
    "imagen_usuario": "#70FAB7"
  },
  {
    "id_usuario": 172,
    "nombre": "Liliana Gómez Martínez",
    "correo_electronico": "liliana22512@cbtis75.edu.mx",
    "contrasena": "ada",
    "rol": "Estudiante",
    "imagen_usuario": "#54EA01"
  }
]
```

---

### 1.4 Registrar Nuevo Usuario
Inserta un nuevo usuario en la base de datos.

- **Método:** `POST`
- **URL:** `{{base_url}}/addUsuario`
- **Headers:** `Content-Type: application/json`
- **Body (raw JSON):**
```json
{
  "nombre": "Juan Carlos Pérez",
  "correo_electronico": "juan22540@cbtis75.edu.mx",
  "contrasena": "password123",
  "rol": "Estudiante",
  "imagen_usuario": "#3498DB"
}
```
- **Respuesta esperada (200 OK):**
```
1
```
*(Devuelve `1` si se guardó con éxito, `0` si falló)*

---

### 1.5 Actualizar Usuario
Modifica los datos de un usuario existente. Debe incluir su `id_usuario`.

- **Método:** `POST`
- **URL:** `{{base_url}}/updateUsuario`
- **Headers:** `Content-Type: application/json`
- **Body (raw JSON):**
```json
{
  "id_usuario": 131,
  "nombre": "Ana Paola Ramírez Vázquez Modificada",
  "correo_electronico": "ana22533@cbtis75.edu.mx",
  "contrasena": "nuevaPass0722",
  "rol": "Administrador",
  "imagen_usuario": "#70FAB7"
}
```
- **Respuesta esperada (200 OK):**
```
1
```

---

### 1.6 Eliminar Usuario
Elimina un usuario de la base de datos enviando el objeto en el cuerpo de la petición.

- **Método:** `DELETE`
- **URL:** `{{base_url}}/deleteUsuario`
- **Headers:** `Content-Type: application/json`
- **Body (raw JSON):**
```json
{
  "id_usuario": 131
}
```
- **Respuesta esperada (200 OK):**
```
1
```

---

## 2. Módulo: Proyectos

Controlador: `ProyectosController.java`

---

### 2.1 Buscar Proyecto por ID
Obtiene los detalles de un proyecto mediante su identificador.

- **Método:** `GET`
- **URL:** `{{base_url}}/buscarProyecto?id_proyecto=197`
- **Query Params:**
  - `id_proyecto`: `197`
- **Body:** *Ninguno*
- **Respuesta esperada (200 OK):**
```json
{
  "id_proyecto": 197,
  "nombre": "My API Rest",
  "descripcion": "Desarrollaran un Api Rest, que satisfaga un sistema de información con al menos 5 tablas.",
  "fecha_inicio": "2024-05-31",
  "fecha_finalizacion": "2024-06-29",
  "estado_proyecto": "En proceso",
  "fondo": "/Images/animacion1.gif"
}
```

---

### 2.2 Obtener Todos los Proyectos
Lista la totalidad de proyectos disponibles.

- **Método:** `POST` *(Mapeado como `@PostMapping` en el backend)*
- **URL:** `{{base_url}}/buscarProyectos`
- **Body:** *Ninguno*
- **Respuesta esperada (200 OK):**
```json
[
  {
    "id_proyecto": 197,
    "nombre": "My API Rest",
    "descripcion": "Desarrollaran un Api Rest, que satisfaga un sistema de información con al menos 5 tablas.",
    "fecha_inicio": "2024-05-31",
    "fecha_finalizacion": "2024-06-29",
    "estado_proyecto": "En proceso",
    "fondo": "/Images/animacion1.gif"
  }
]
```

---

### 2.3 Crear Proyecto
Registra un nuevo proyecto.

- **Método:** `POST`
- **URL:** `{{base_url}}/addProyecto`
- **Headers:** `Content-Type: application/json`
- **Body (raw JSON):**
```json
{
  "nombre": "Sistema de Inventario",
  "descripcion": "Gestión de entradas y salidas de almacén",
  "fecha_inicio": "2026-10-01",
  "fecha_finalizacion": "2026-12-15",
  "estado_proyecto": "En proceso",
  "fondo": "/Images/fondo2.png"
}
```
- **Respuesta esperada (200 OK):**
```
1
```

---

### 2.4 Actualizar Proyecto
Actualiza un proyecto existente pasando su ID en el cuerpo.

- **Método:** `POST`
- **URL:** `{{base_url}}/updateProyecto`
- **Headers:** `Content-Type: application/json`
- **Body (raw JSON):**
```json
{
  "id_proyecto": 197,
  "nombre": "My API Rest Actualizada",
  "descripcion": "Proyecto final de Gestión del Proceso de Software con 5 tablas",
  "fecha_inicio": "2024-05-31",
  "fecha_finalizacion": "2024-07-15",
  "estado_proyecto": "Completado",
  "fondo": "/Images/animacion1.gif"
}
```
- **Respuesta esperada (200 OK):**
```
1
```

---

### 2.5 Eliminar Proyecto
Elimina un proyecto recibiendo su objeto con el ID correspondiente.

- **Método:** `DELETE`
- **URL:** `{{base_url}}/deleteProyecto`
- **Headers:** `Content-Type: application/json`
- **Body (raw JSON):**
```json
{
  "id_proyecto": 197
}
```
- **Respuesta esperada (200 OK):**
```
1
```

---

## 3. Módulo: Tareas

Controlador: `TareasController.java`

---

### 3.1 Obtener Tareas por ID de Proyecto
Recupera todas las tareas pertenecientes a un proyecto específico.

- **Método:** `POST`
- **URL:** `{{base_url}}/getTarea`
- **Headers:** `Content-Type: application/json`
- **Body (raw JSON):**
```json
{
  "id_proyecto": 197
}
```
- **Respuesta esperada (200 OK):**
```json
[
  {
    "id_tarea": 199,
    "nombre": "Base de datos",
    "descripcion": "Realizar la estructura de las tablas",
    "fecha_vencimiento": "2024-06-13",
    "lista": "terminado",
    "estado": "danger",
    "id_proyecto": 197
  }
]
```

---

### 3.2 Buscar Tarea por ID
Obtiene los detalles de una tarea individual.

- **Método:** `GET`
- **URL:** `{{base_url}}/buscarTarea?id_tarea=199`
- **Query Params:**
  - `id_tarea`: `199`
- **Body:** *Ninguno*
- **Respuesta esperada (200 OK):**
```json
{
  "id_tarea": 199,
  "nombre": "Base de datos",
  "descripcion": "Realizar la estructura de las tablas",
  "fecha_vencimiento": "2024-06-13",
  "lista": "terminado",
  "estado": "danger",
  "id_proyecto": 197
}
```

---

### 3.3 Obtener Todas las Tareas
Lista todas las tareas sin filtrar por proyecto.

- **Método:** `POST` *(Mapeado como `@PostMapping` en el backend)*
- **URL:** `{{base_url}}/buscarTareas`
- **Body:** *Ninguno*
- **Respuesta esperada (200 OK):**
```json
[
  {
    "id_tarea": 199,
    "nombre": "Base de datos",
    "descripcion": "Realizar la estructura de las tablas",
    "fecha_vencimiento": "2024-06-13",
    "lista": "terminado",
    "estado": "danger",
    "id_proyecto": 197
  }
]
```

---

### 3.4 Agregar Tarea
Crea una nueva tarea asignada a un proyecto.

- **Método:** `POST`
- **URL:** `{{base_url}}/addTareas`
- **Headers:** `Content-Type: application/json`
- **Body (raw JSON):**
```json
{
  "nombre": "Diseño de la Interfaz UI",
  "descripcion": "Construir las vistas en Thymeleaf con CSS y Bootstrap",
  "fecha_vencimiento": "2026-10-15",
  "lista": "en proceso",
  "estado": "warning",
  "id_proyecto": 197
}
```
- **Respuesta esperada (200 OK):**
```
1
```

---

### 3.5 Actualizar Tarea
Modifica los datos de una tarea existente.

- **Método:** `POST`
- **URL:** `{{base_url}}/updateTareas`
- **Headers:** `Content-Type: application/json`
- **Body (raw JSON):**
```json
{
  "id_tarea": 199,
  "nombre": "Base de datos y Migraciones",
  "descripcion": "Realizar la estructura de las tablas e importar el dump",
  "fecha_vencimiento": "2024-06-20",
  "lista": "terminado",
  "estado": "success",
  "id_proyecto": 197
}
```
- **Respuesta esperada (200 OK):**
```
1
```

---

### 3.6 Eliminar Tarea
Elimina una tarea por su ID.

- **Método:** `DELETE`
- **URL:** `{{base_url}}/deleteTareas`
- **Headers:** `Content-Type: application/json`
- **Body (raw JSON):**
```json
{
  "id_tarea": 199
}
```
- **Respuesta esperada (200 OK):**
```
1
```

---

## 4. Módulo: Comentarios

Controlador: `ComentariosController.java`

---

### 4.1 Obtener Comentarios por Tarea
Obtiene todos los comentarios correspondientes a una tarea en específico.

- **Método:** `POST`
- **URL:** `{{base_url}}/getComentario`
- **Headers:** `Content-Type: application/json`
- **Body (raw JSON):**
```json
{
  "id_tarea": 199
}
```
- **Respuesta esperada (200 OK):**
```json
[
  {
    "id_comentario": 201,
    "id_usuario": "Perla Joceline Martínez Meza",
    "id_tarea": 199,
    "texto_comentario": "Subirlo YA!!!",
    "fecha_hora": "2024-06-26 13:48:00"
  },
  {
    "id_comentario": 202,
    "id_usuario": "Liliana Gómez Martínez",
    "id_tarea": 199,
    "texto_comentario": "hola",
    "fecha_hora": "2024-06-26 13:49:00"
  }
]
```

---

### 4.2 Buscar Comentario por ID
Obtiene un comentario específico mediante su ID.

- **Método:** `GET`
- **URL:** `{{base_url}}/buscarComentario?id=201`
- **Query Params:**
  - `id`: `201`
- **Body:** *Ninguno*
- **Respuesta esperada (200 OK):**
```json
{
  "id_comentario": 201,
  "id_usuario": "Perla Joceline Martínez Meza",
  "id_tarea": 199,
  "texto_comentario": "Subirlo YA!!!",
  "fecha_hora": "2024-06-26 13:48:00"
}
```

---

### 4.3 Obtener Todos los Comentarios
Lista todos los comentarios de la base de datos sin filtrar.

- **Método:** `POST` *(Mapeado como `@PostMapping` en el backend)*
- **URL:** `{{base_url}}/buscarComentarios`
- **Body:** *Ninguno*
- **Respuesta esperada (200 OK):**
```json
[
  {
    "id_comentario": 201,
    "id_usuario": "Perla Joceline Martínez Meza",
    "id_tarea": 199,
    "texto_comentario": "Subirlo YA!!!",
    "fecha_hora": "2024-06-26 13:48:00"
  }
]
```

---

### 4.4 Agregar Comentario
Registra un nuevo comentario asociado a una tarea.

- **Método:** `POST`
- **URL:** `{{base_url}}/addComentario`
- **Headers:** `Content-Type: application/json`
- **Body (raw JSON):**
```json
{
  "id_usuario": "Perla Joceline Martínez Meza",
  "id_tarea": 199,
  "texto_comentario": "Revisado y aprobado para producción.",
  "fecha_hora": "2026-09-30 20:00:00"
}
```
- **Respuesta esperada (200 OK):**
```
1
```

---

### 4.5 Actualizar Comentario
Modifica el texto o metadatos de un comentario existente.

- **Método:** `POST`
- **URL:** `{{base_url}}/updateComentario`
- **Headers:** `Content-Type: application/json`
- **Body (raw JSON):**
```json
{
  "id_comentario": 201,
  "id_usuario": "Perla Joceline Martínez Meza",
  "id_tarea": 199,
  "texto_comentario": "Subirlo YA corregido con las pruebas.",
  "fecha_hora": "2026-09-30 20:05:00"
}
```
- **Respuesta esperada (200 OK):**
```
1
```

---

### 4.6 Eliminar Comentario
Elimina un comentario por su ID.

- **Método:** `DELETE`
- **URL:** `{{base_url}}/deleteComentario`
- **Headers:** `Content-Type: application/json`
- **Body (raw JSON):**
```json
{
  "id_comentario": 201
}
```
- **Respuesta esperada (200 OK):**
```
1
```

---

## 5. Módulo: Asignaciones

Controlador: `AsignacionesController.java`

---

### 5.1 Buscar Asignación por ID
Obtiene los detalles de una asignación por su ID.

- **Método:** `GET`
- **URL:** `{{base_url}}/buscarAsignacion?id=1`
- **Query Params:**
  - `id`: `1`
- **Body:** *Ninguno*
- **Respuesta esperada (200 OK):**
```json
{
  "id_asignacion": 1,
  "id_usuario": 131,
  "id_tarea": 199,
  "fecha_asignacion": "2024-06-26"
}
```

---

### 5.2 Obtener Todas las Asignaciones
Lista todas las asignaciones de tareas a usuarios.

- **Método:** `POST` *(Mapeado como `@PostMapping` en el backend)*
- **URL:** `{{base_url}}/buscarAsignaciones`
- **Body:** *Ninguno*
- **Respuesta esperada (200 OK):**
```json
[
  {
    "id_asignacion": 1,
    "id_usuario": 131,
    "id_tarea": 199,
    "fecha_asignacion": "2024-06-26"
  }
]
```

---

### 5.3 Crear Asignación
Asigna una tarea a un usuario.

- **Método:** `POST`
- **URL:** `{{base_url}}/addAsignacion`
- **Headers:** `Content-Type: application/json`
- **Body (raw JSON):**
```json
{
  "id_usuario": 131,
  "id_tarea": 199,
  "fecha_asignacion": "2026-09-30"
}
```
- **Respuesta esperada (200 OK):**
```
1
```

---

### 5.4 Actualizar Asignación
Modifica una asignación existente.

- **Método:** `POST`
- **URL:** `{{base_url}}/updateAsignacion`
- **Headers:** `Content-Type: application/json`
- **Body (raw JSON):**
```json
{
  "id_asignacion": 1,
  "id_usuario": 172,
  "id_tarea": 199,
  "fecha_asignacion": "2026-10-01"
}
```
- **Respuesta esperada (200 OK):**
```
1
```

---

### 5.5 Eliminar Asignación
Elimina una asignación por su ID.

- **Método:** `DELETE`
- **URL:** `{{base_url}}/deleteAsignacion`
- **Headers:** `Content-Type: application/json`
- **Body (raw JSON):**
```json
{
  "id_asignacion": 1
}
```
- **Respuesta esperada (200 OK):**
```
1
```

---

## ⚠️ Endpoints Faltantes o Recomendados (No expuestos actualmente)

Esta sección identifica los endpoints que **no están implementados actualmente** en la API pero que **deberían existir** por necesidades de la arquitectura, buenas prácticas REST o consultas fundamentales del negocio:

### 1. Endpoints de Negocio Faltantes

#### A. Obtener Asignaciones por Tarea (`/getAsignacionesPorTarea`)
*Actualmente inexistente:* `TareasController` tiene búsqueda por proyecto (`/getTarea`), y `ComentariosController` por tarea (`/getComentario`), pero `AsignacionesController` **no cuenta con ningún endpoint** para saber quiénes están asignados a una tarea.
- **Método sugerido:** `GET {{base_url}}/getAsignacionPorTarea?id_tarea=199`
- **Respuesta esperada:** Lista de asignaciones o usuarios vinculados a esa tarea.

#### B. Obtener Tareas o Asignaciones por Usuario (`/getAsignacionesPorUsuario`)
*Actualmente inexistente:* No es posible consultar las tareas asignadas a un usuario específico directamente desde el backend.
- **Método sugerido:** `GET {{base_url}}/getAsignacionesPorUsuario?id_usuario=131`

#### C. Endpoint Comentado en Código: `POST /buscarProyecto`
*Situación:* En `ProyectosController.java` (líneas 24-34) existe el método `buscarProyecto` con `@PostMapping` y `@RequestBody Proyectos proyecto`, pero se encuentra **comentado**.
- Si se descomenta, permitiría buscar un proyecto enviando `{"id_proyecto": 197}` en el cuerpo de una petición POST.

---

### 2. Inconsistencias de Diseño REST en los Endpoints Actuales

Si planeas mejorar la API bajo estándares RESTful, se detectan las siguientes desviaciones:

| Endpoint Actual | Método Actual | Método REST Recomendado | Motivo / Buena Práctica |
|---|---|---|---|
| `{{base_url}}/buscarUsuarios` | `POST` | `GET` | Consultar listas no debe usar POST si no envía datos a procesar en el servidor. |
| `{{base_url}}/buscarProyectos` | `POST` | `GET` | Mismo caso: una consulta de lectura debe ser idempotente y usar GET. |
| `{{base_url}}/buscarTareas` | `POST` | `GET` | Debe ser GET. |
| `{{base_url}}/buscarComentarios` | `POST` | `GET` | Debe ser GET. |
| `{{base_url}}/buscarAsignaciones` | `POST` | `GET` | Debe ser GET. |
| `{{base_url}}/getTarea` | `POST` con body `{"id_proyecto": 197}` | `GET {{base_url}}/tareas?id_proyecto=197` | Las búsquedas por filtro deben viajar como parámetros query o sub-recurso. |
| `{{base_url}}/getComentario` | `POST` con body `{"id_tarea": 199}` | `GET {{base_url}}/comentarios?id_tarea=199` | Las búsquedas por filtro deben ser GET. |
| Todos los `/update...` | `POST` | `PUT` o `PATCH` | Las actualizaciones de recursos completos deben usar `PUT`. |
| Todos los `/delete...` | `DELETE` con `@RequestBody` | `DELETE {{base_url}}/delete.../{id}` | Enviar un cuerpo JSON en peticiones DELETE es desaconsejado en HTTP y genera problemas en firewalls y clientes HTTP. Se recomienda enviar el ID por PathVariable o QueryParam. |

---

### 3. Inconsistencia en Nombres de Parámetros Query
Al consultar por ID con `GET`, los nombres de los parámetros no son homogéneos:
- `buscarUsuario`: `?id=131` (Usa `id`)
- `buscarProyecto`: `?id_proyecto=197` (Usa `id_proyecto`)
- `buscarTarea`: `?id_tarea=199` (Usa `id_tarea`)
- `buscarComentario`: `?id=201` (Usa `id`)
- `buscarAsignacion`: `?id=1` (Usa `id`)

*Recomendación:* Estandarizar todos a `?id=` o al nombre explícito `?id_{entidad}=`.
