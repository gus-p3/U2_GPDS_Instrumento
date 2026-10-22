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

import com.mx.cbtis.gestor.modelo.Asignaciones;
import com.mx.cbtis.gestor.repository.AsignacionesRepository;
import com.mx.cbtis.gestor.repository.TareasRepository;
import com.mx.cbtis.gestor.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class AsignacionesControllerTest {

    @Mock
    private AsignacionesRepository asignacionestable;

    @Mock
    private UsuarioRepository usuariotable;

    @Mock
    private TareasRepository tareastable;

    @InjectMocks
    private AsignacionesController asignacionesController;

    @Test
    @DisplayName("UT-13: Buscar asignación por ID existente retorna 200 OK")
    void getAsignacion_Existente() {
        Asignaciones a = new Asignaciones(1, 2, 3, "2026-10-22");
        when(asignacionestable.findById(1)).thenReturn(Optional.of(a));

        ResponseEntity<Asignaciones> response = asignacionesController.getAsignacion(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getId_asignacion());
        assertEquals(2, response.getBody().getId_usuario());
        assertEquals(3, response.getBody().getId_tarea());
        assertEquals("2026-10-22", response.getBody().getFecha_asignacion());
    }

    @Test
    @DisplayName("Buscar asignación por ID inexistente retorna 404 NOT_FOUND")
    void getAsignacion_Inexistente() {
        when(asignacionestable.findById(999)).thenReturn(Optional.empty());

        ResponseEntity<Asignaciones> response = asignacionesController.getAsignacion(999);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("UT-12: Agregar asignación válida retorna 200 OK")
    void addAsignacion_Valida() {
        Asignaciones a = new Asignaciones();
        a.setId_asignacion(10);
        a.setId_usuario(2);
        a.setId_tarea(3);
        a.setFecha_asignacion("2026-10-22");

        when(usuariotable.existsById(2)).thenReturn(true);
        when(tareastable.existsById(3)).thenReturn(true);
        when(asignacionestable.save(any(Asignaciones.class))).thenReturn(a);

        ResponseEntity<Object> response = asignacionesController.addAsignacion(a);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody());
    }

    @Test
    @DisplayName("UT-33: Agregar asignación con datos incompletos o FK inexistente")
    void addAsignacion_ErroresControlados() {
        assertEquals(HttpStatus.BAD_REQUEST, asignacionesController.addAsignacion(null).getStatusCode());

        Asignaciones aSinUsuario = new Asignaciones(null, null, 3, "2026-10-22");
        assertEquals(HttpStatus.BAD_REQUEST, asignacionesController.addAsignacion(aSinUsuario).getStatusCode());

        Asignaciones aSinTarea = new Asignaciones(null, 2, null, "2026-10-22");
        assertEquals(HttpStatus.BAD_REQUEST, asignacionesController.addAsignacion(aSinTarea).getStatusCode());

        Asignaciones aUsuarioNoExiste = new Asignaciones(null, 99, 3, "2026-10-22");
        when(usuariotable.existsById(99)).thenReturn(false);
        assertEquals(HttpStatus.NOT_FOUND, asignacionesController.addAsignacion(aUsuarioNoExiste).getStatusCode());

        Asignaciones aTareaNoExiste = new Asignaciones(null, 2, 99, "2026-10-22");
        when(usuariotable.existsById(2)).thenReturn(true);
        when(tareastable.existsById(99)).thenReturn(false);
        assertEquals(HttpStatus.NOT_FOUND, asignacionesController.addAsignacion(aTareaNoExiste).getStatusCode());
    }

    @Test
    @DisplayName("UT-14: Actualizar asignación retorna 1")
    void updateAsignacion_Exito() {
        Asignaciones a = new Asignaciones(1, 2, 3, "2026-10-22");
        when(asignacionestable.save(any(Asignaciones.class))).thenReturn(a);

        int flag = asignacionesController.updateAsignacion(a);
        assertEquals(1, flag);
    }

    @Test
    @DisplayName("UT-16: Eliminar asignación retorna 1")
    void deleteAsignacion_Exito() {
        Asignaciones a = new Asignaciones(1, 2, 3, "2026-10-22");
        doNothing().when(asignacionestable).delete(any(Asignaciones.class));

        int flag = asignacionesController.deleteAsignacion(a);
        assertEquals(1, flag);
    }

    @Test
    @DisplayName("UT-15: Buscar todas las asignaciones retorna lista")
    void getAsignaciones_Exito() {
        Asignaciones a1 = new Asignaciones(1, 2, 3, "2026-10-22");
        when(asignacionestable.findAll()).thenReturn(Arrays.asList(a1));

        List<Asignaciones> lista = asignacionesController.getAsignaciones();
        assertEquals(1, lista.size());
    }
}
