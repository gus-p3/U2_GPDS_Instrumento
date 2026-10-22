package com.mx.cbtis.gestor.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.mx.cbtis.gestor.modelo.Tareas;
import com.mx.cbtis.gestor.repository.ProtectosRepository;
import com.mx.cbtis.gestor.repository.TareasRepository;

@ExtendWith(MockitoExtension.class)
class TareasControllerTest {

    @Mock
    private TareasRepository tareastable;

    @Mock
    private ProtectosRepository proyectostable;

    @InjectMocks
    private TareasController tareasController;

    @Test
    @DisplayName("UT-22: Obtener tareas por proyecto retorna lista filtrada")
    void getTareaPorProyecto_Exito() {
        Tareas filtro = new Tareas();
        filtro.setId_proyecto(10);

        Tareas t1 = new Tareas(1, "Diseñar BD", "Crear modelo ER", "2026-05-10", "pendientes", "danger", 10);
        when(tareastable.findByIdProyecto(10)).thenReturn(Arrays.asList(t1));

        List<Tareas> resultado = tareasController.getTarea(filtro);

        assertEquals(1, resultado.size());
        assertEquals("Diseñar BD", resultado.get(0).getNombre());
    }

    @Test
    @DisplayName("UT-18: Buscar tarea por ID existente retorna 200 OK")
    void getTareaPorId_Existente() {
        Tareas t = new Tareas(1, "Configurar CI/CD", "Pipeline Jenkins", "2026-05-15", "encurso", "warning", 10);
        when(tareastable.findById(1)).thenReturn(Optional.of(t));

        ResponseEntity<Tareas> response = tareasController.getTarea(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getId_tarea());
        assertEquals("Configurar CI/CD", response.getBody().getNombre());
        assertEquals("Pipeline Jenkins", response.getBody().getDescripcion());
        assertEquals("2026-05-15", response.getBody().getFecha_vencimiento());
        assertEquals("encurso", response.getBody().getLista());
        assertEquals("warning", response.getBody().getEstado());
        assertEquals(10, response.getBody().getId_proyecto());
    }

    @Test
    @DisplayName("Buscar tarea por ID inexistente retorna 404 NOT_FOUND")
    void getTareaPorId_Inexistente() {
        when(tareastable.findById(999)).thenReturn(Optional.empty());

        ResponseEntity<Tareas> response = tareasController.getTarea(999);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("UT-17: Agregar tarea válida retorna 200 OK")
    void addTareas_Exito() {
        Tareas t = new Tareas();
        t.setId_tarea(5);
        t.setNombre("Ejecutar SonarQube");
        t.setDescripcion("Análisis estático");
        t.setFecha_vencimiento("2026-05-20");
        t.setLista("pendientes");
        t.setEstado("primary");
        t.setId_proyecto(10);

        when(proyectostable.existsById(10)).thenReturn(true);
        when(tareastable.save(any(Tareas.class))).thenReturn(t);

        ResponseEntity<Object> response = tareasController.addTareas(t);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody());
    }

    @Test
    @DisplayName("Agregar tarea con datos incompletos o proyecto inexistente")
    void addTareas_ErroresControlados() {
        assertEquals(HttpStatus.BAD_REQUEST, tareasController.addTareas(null).getStatusCode());

        Tareas tSinNombre = new Tareas(null, "  ", "Desc", "2026-05-20", "pendientes", "primary", 10);
        assertEquals(HttpStatus.BAD_REQUEST, tareasController.addTareas(tSinNombre).getStatusCode());

        Tareas tSinProyecto = new Tareas(null, "Tarea", "Desc", "2026-05-20", "pendientes", "primary", null);
        assertEquals(HttpStatus.BAD_REQUEST, tareasController.addTareas(tSinProyecto).getStatusCode());

        Tareas tProyectoInexistente = new Tareas(null, "Tarea", "Desc", "2026-05-20", "pendientes", "primary", 999);
        when(proyectostable.existsById(999)).thenReturn(false);
        assertEquals(HttpStatus.NOT_FOUND, tareasController.addTareas(tProyectoInexistente).getStatusCode());
    }

    @Test
    @DisplayName("UT-19: Actualizar tarea retorna 1")
    void updateTareas_Exito() {
        Tareas t = new Tareas(1, "Tarea Modificada", "Desc", "2026-05-20", "terminado", "success", 10);
        when(tareastable.save(any(Tareas.class))).thenReturn(t);

        int flag = tareasController.updateTareas(t);
        assertEquals(1, flag);
    }

    @Test
    @DisplayName("UT-21: Eliminar tarea existente retorna 200 OK")
    void deleteTareas_Existente() {
        Tareas t = new Tareas();
        t.setId_tarea(1);
        when(tareastable.existsById(1)).thenReturn(true);
        doNothing().when(tareastable).delete(any(Tareas.class));

        ResponseEntity<Object> response = tareasController.deleteTareas(t);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody());
    }

    @Test
    @DisplayName("Eliminar tarea inexistente retorna 404 NOT_FOUND")
    void deleteTareas_Inexistente() {
        assertEquals(HttpStatus.NOT_FOUND, tareasController.deleteTareas(null).getStatusCode());

        Tareas tSinId = new Tareas();
        assertEquals(HttpStatus.NOT_FOUND, tareasController.deleteTareas(tSinId).getStatusCode());

        Tareas tNoExiste = new Tareas();
        tNoExiste.setId_tarea(999);
        when(tareastable.existsById(999)).thenReturn(false);
        assertEquals(HttpStatus.NOT_FOUND, tareasController.deleteTareas(tNoExiste).getStatusCode());
    }

    @Test
    @DisplayName("UT-20: Buscar todas las tareas retorna lista")
    void getTareas_Exito() {
        Tareas t1 = new Tareas(1, "T1", "D1", "2026-05-01", "pendientes", "primary", 1);
        when(tareastable.findAll()).thenReturn(Arrays.asList(t1));

        List<Tareas> lista = tareasController.getTareas();
        assertEquals(1, lista.size());
    }
}
