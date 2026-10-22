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

import com.mx.cbtis.gestor.modelo.Proyectos;
import com.mx.cbtis.gestor.repository.ProtectosRepository;

@ExtendWith(MockitoExtension.class)
class ProyectosControllerTest {

    @Mock
    private ProtectosRepository proyectostable;

    @InjectMocks
    private ProyectosController proyectosController;

    @Test
    @DisplayName("UT-08: Buscar proyecto por ID existente retorna 200 OK")
    void getProyecto_Existente() {
        Proyectos p = new Proyectos(1, "ERP Escolar", "Sistema de gestión", "2026-01-01", "2026-06-30", "En proceso", "/Images/naranja.png");
        when(proyectostable.findById(1)).thenReturn(Optional.of(p));

        ResponseEntity<Proyectos> response = proyectosController.getProyecto(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getId_proyecto());
        assertEquals("ERP Escolar", response.getBody().getNombre());
        assertEquals("Sistema de gestión", response.getBody().getDescripcion());
        assertEquals("2026-01-01", response.getBody().getFecha_inicio());
        assertEquals("2026-06-30", response.getBody().getFecha_finalizacion());
        assertEquals("En proceso", response.getBody().getEstado_proyecto());
        assertEquals("/Images/naranja.png", response.getBody().getFondo());
    }

    @Test
    @DisplayName("Buscar proyecto por ID inexistente retorna 404 NOT_FOUND")
    void getProyecto_Inexistente() {
        when(proyectostable.findById(99)).thenReturn(Optional.empty());

        ResponseEntity<Proyectos> response = proyectosController.getProyecto(99);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("UT-09: Agregar proyecto retorna 1")
    void addProyecto_Exito() {
        Proyectos p = new Proyectos();
        p.setId_proyecto(2);
        p.setNombre("Portal Web");
        p.setDescripcion("Portal institucional");
        p.setFecha_inicio("2026-02-01");
        p.setFecha_finalizacion("2026-08-01");
        p.setEstado_proyecto("En proceso");
        p.setFondo("/Images/azulReal.png");

        when(proyectostable.save(any(Proyectos.class))).thenReturn(p);

        int flag = proyectosController.addProyecto(p);
        assertEquals(1, flag);
    }

    @Test
    @DisplayName("UT-10: Actualizar proyecto retorna 1")
    void updateProyecto_Exito() {
        Proyectos p = new Proyectos(1, "ERP Actualizado", "Desc", "2026-01-01", "2026-06-30", "Finalizado", "Fondo");
        when(proyectostable.save(any(Proyectos.class))).thenReturn(p);

        int flag = proyectosController.updateProyecto(p);
        assertEquals(1, flag);
    }

    @Test
    @DisplayName("UT-11: Eliminar proyecto existente retorna 200 OK")
    void deleteProyecto_Existente() {
        Proyectos p = new Proyectos();
        p.setId_proyecto(1);
        when(proyectostable.existsById(1)).thenReturn(true);
        doNothing().when(proyectostable).delete(any(Proyectos.class));

        ResponseEntity<Object> response = proyectosController.deleteProyecto(p);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody());
    }

    @Test
    @DisplayName("UT-32: Eliminar proyecto con ID inexistente o nulo retorna 404 NOT_FOUND")
    void deleteProyecto_Inexistente() {
        assertEquals(HttpStatus.NOT_FOUND, proyectosController.deleteProyecto(null).getStatusCode());

        Proyectos pSinId = new Proyectos();
        assertEquals(HttpStatus.NOT_FOUND, proyectosController.deleteProyecto(pSinId).getStatusCode());

        Proyectos pNoExiste = new Proyectos();
        pNoExiste.setId_proyecto(999);
        when(proyectostable.existsById(999)).thenReturn(false);
        assertEquals(HttpStatus.NOT_FOUND, proyectosController.deleteProyecto(pNoExiste).getStatusCode());
    }

    @Test
    @DisplayName("UT-07: Buscar todos los proyectos retorna lista")
    void getProyectos_Exito() {
        Proyectos p1 = new Proyectos(1, "P1", "D1", "2026-01-01", "2026-02-01", "En proceso", "F1");
        Proyectos p2 = new Proyectos(2, "P2", "D2", "2026-03-01", "2026-04-01", "Finalizado", "F2");
        when(proyectostable.findAll()).thenReturn(Arrays.asList(p1, p2));

        List<Proyectos> lista = proyectosController.getProyectos();

        assertEquals(2, lista.size());
    }
}
