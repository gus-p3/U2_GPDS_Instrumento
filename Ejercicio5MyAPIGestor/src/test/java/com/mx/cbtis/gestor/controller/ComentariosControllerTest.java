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

import com.mx.cbtis.gestor.modelo.Comentarios;
import com.mx.cbtis.gestor.repository.ComentariosRepository;
import com.mx.cbtis.gestor.repository.TareasRepository;

@ExtendWith(MockitoExtension.class)
class ComentariosControllerTest {

    @Mock
    private ComentariosRepository comentariostable;

    @Mock
    private TareasRepository tareastable;

    @InjectMocks
    private ComentariosController comentariosController;

    @Test
    @DisplayName("UT-28: Obtener comentarios por tarea retorna lista")
    void getComentarioPorTarea_Exito() {
        Comentarios filtro = new Comentarios();
        filtro.setId_tarea(5);

        Comentarios c1 = new Comentarios(1, "Gustavo", 5, "Excelente avance", "26/10/22 15:00");
        when(comentariostable.findByTarea(5)).thenReturn(Arrays.asList(c1));

        List<Comentarios> lista = comentariosController.getComentario(filtro);

        assertEquals(1, lista.size());
        assertEquals("Excelente avance", lista.get(0).getTexto_comentario());
    }

    @Test
    @DisplayName("UT-24: Buscar comentario por ID existente retorna 200 OK")
    void getComentarioPorId_Existente() {
        Comentarios c = new Comentarios(1, "Gustavo", 5, "Revisado", "26/10/22 15:00");
        when(comentariostable.findById(1)).thenReturn(Optional.of(c));

        ResponseEntity<Comentarios> response = comentariosController.getComentario(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getId_comentario());
        assertEquals("Gustavo", response.getBody().getId_usuario());
        assertEquals(5, response.getBody().getId_tarea());
        assertEquals("Revisado", response.getBody().getTexto_comentario());
        assertEquals("26/10/22 15:00", response.getBody().getFecha_hora());
    }

    @Test
    @DisplayName("Buscar comentario por ID inexistente retorna 404 NOT_FOUND")
    void getComentarioPorId_Inexistente() {
        when(comentariostable.findById(999)).thenReturn(Optional.empty());

        ResponseEntity<Comentarios> response = comentariosController.getComentario(999);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("UT-23: Agregar comentario válido retorna 200 OK")
    void addComentario_Valido() {
        Comentarios c = new Comentarios();
        c.setId_comentario(2);
        c.setId_usuario("Gustavo");
        c.setId_tarea(5);
        c.setTexto_comentario("Nuevo comentario");
        c.setFecha_hora("26/10/22 15:10");

        when(tareastable.existsById(5)).thenReturn(true);
        when(comentariostable.save(any(Comentarios.class))).thenReturn(c);

        ResponseEntity<Object> response = comentariosController.addComentario(c);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody());
    }

    @Test
    @DisplayName("UT-34: Agregar comentario con datos incompletos o tarea inexistente")
    void addComentario_ErroresControlados() {
        assertEquals(HttpStatus.BAD_REQUEST, comentariosController.addComentario(null).getStatusCode());

        Comentarios cSinTarea = new Comentarios(null, "Gustavo", null, "Texto", "Fecha");
        assertEquals(HttpStatus.BAD_REQUEST, comentariosController.addComentario(cSinTarea).getStatusCode());

        Comentarios cTareaInexistente = new Comentarios(null, "Gustavo", 999, "Texto", "Fecha");
        when(tareastable.existsById(999)).thenReturn(false);
        assertEquals(HttpStatus.NOT_FOUND, comentariosController.addComentario(cTareaInexistente).getStatusCode());
    }

    @Test
    @DisplayName("UT-25: Actualizar comentario retorna 1")
    void updateComentario_Exito() {
        Comentarios c = new Comentarios(1, "Gustavo", 5, "Modificado", "26/10/22 15:00");
        when(comentariostable.save(any(Comentarios.class))).thenReturn(c);

        int flag = comentariosController.updateComentario(c);
        assertEquals(1, flag);
    }

    @Test
    @DisplayName("UT-27: Eliminar comentario retorna 1")
    void deleteComentario_Exito() {
        Comentarios c = new Comentarios(1, "Gustavo", 5, "Modificado", "26/10/22 15:00");
        doNothing().when(comentariostable).delete(any(Comentarios.class));

        int flag = comentariosController.deleteComentario(c);
        assertEquals(1, flag);
    }

    @Test
    @DisplayName("UT-26: Buscar todos los comentarios retorna lista")
    void getComentarios_Exito() {
        Comentarios c1 = new Comentarios(1, "Gustavo", 5, "Comentario 1", "26/10/22 15:00");
        when(comentariostable.findAll()).thenReturn(Arrays.asList(c1));

        List<Comentarios> lista = comentariosController.getComentarios();
        assertEquals(1, lista.size());
    }
}
