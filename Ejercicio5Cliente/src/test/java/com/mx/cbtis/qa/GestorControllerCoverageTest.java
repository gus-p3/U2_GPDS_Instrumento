package com.mx.cbtis.qa;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import com.mx.cbtis.controller.GestorController;
import com.mx.cbtis.modelo.Asignaciones;
import com.mx.cbtis.modelo.Comentarios;
import com.mx.cbtis.modelo.Proyectos;
import com.mx.cbtis.modelo.Tareas;
import com.mx.cbtis.modelo.Usuario;

class GestorControllerCoverageTest {

    private GestorController controller;

    @BeforeEach
    void setUp() {
        controller = new GestorController();
    }

    @Test
    @DisplayName("Navegación de vistas estáticas: home, tutorial, manual, login, register, tarea")
    void vistasEstaticas_RetornanNombreVistaCorrecto() {
        assertEquals("home", controller.getHome());
        assertEquals("tutorial", controller.getTutorial());
        assertEquals("manual", controller.getManual());
        assertEquals("login", controller.getLogin());

        Model modelReg = new ConcurrentModel();
        assertEquals("register", controller.getRegister(modelReg));
        assertTrue(modelReg.containsAttribute("usuario"));

        Model modelTar = new ConcurrentModel();
        assertEquals("proyecto", controller.getTarea(modelTar));
        assertTrue(modelTar.containsAttribute("tareas"));
    }

    @Test
    @DisplayName("Modelos DTO de FrontEnd: constructores, getters y setters")
    void modelosDto_GettersYSetters() {
        Asignaciones asig = new Asignaciones();
        asig.setId_asignacion(1);
        asig.setId_usuario(2);
        asig.setId_tarea(3);
        asig.setFecha_asignacion("2026-10-22");
        Asignaciones asig2 = new Asignaciones(asig.getId_asignacion(), asig.getId_usuario(), asig.getId_tarea(), asig.getFecha_asignacion());
        assertEquals(1, asig2.getId_asignacion());

        Comentarios com = new Comentarios();
        com.setId_comentario(1);
        com.setId_usuario("Gustavo");
        com.setId_tarea(3);
        com.setTexto_comentario("Texto");
        com.setFecha_hora("2026-10-22");
        Comentarios com2 = new Comentarios(com.getId_comentario(), com.getId_usuario(), com.getId_tarea(), com.getTexto_comentario(), com.getFecha_hora());
        assertEquals("Gustavo", com2.getId_usuario());

        Proyectos proy = new Proyectos();
        proy.setId_proyecto(1);
        proy.setNombre("P1");
        proy.setDescripcion("D1");
        proy.setFecha_inicio("2026-01-01");
        proy.setFecha_finalizacion("2026-12-31");
        proy.setEstado_proyecto("En proceso");
        proy.setFondo("Fondo");
        Proyectos proy2 = new Proyectos(proy.getId_proyecto(), proy.getNombre(), proy.getDescripcion(),
                proy.getFecha_inicio(), proy.getFecha_finalizacion(), proy.getEstado_proyecto(), proy.getFondo());
        assertEquals("P1", proy2.getNombre());

        Tareas tar = new Tareas();
        tar.setId_tarea(1);
        tar.setNombre("T1");
        tar.setDescripcion("D1");
        tar.setFecha_vencimiento("2026-10-30");
        tar.setLista("pendientes");
        tar.setEstado("primary");
        tar.setId_proyecto(1);
        Tareas tar2 = new Tareas(tar.getId_tarea(), tar.getNombre(), tar.getDescripcion(),
                tar.getFecha_vencimiento(), tar.getLista(), tar.getEstado(), tar.getId_proyecto());
        assertEquals("T1", tar2.getNombre());

        Usuario usr = new Usuario();
        usr.setId_usuario(1);
        usr.setNombre("Gustavo");
        usr.setCorreo_electronico("gustavo@cbtis.edu.mx");
        usr.setContrasena("1234");
        usr.setRol("Docente");
        usr.setImagen_usuario("Images/logo.png");
        Usuario usr2 = new Usuario(usr.getId_usuario(), usr.getNombre(), usr.getCorreo_electronico(),
                usr.getContrasena(), usr.getRol(), usr.getImagen_usuario());
        assertEquals("Docente", usr2.getRol());
    }

    @Test
    @DisplayName("Guardar y eliminar proyecto: éxito y manejo de errores")
    void saveYEliminarProyecto_ExitoYErrores() {
        Proyectos pValido = new Proyectos(1, "Proyecto X", "Desc", "2026-01-01", "2026-06-01", "En proceso", "Fondo");
        try (MockedConstruction<RestTemplate> mocked = Mockito.mockConstruction(RestTemplate.class, (mock, ctx) -> {
            when(mock.postForEntity(eq("http://localhost:8081/addProyecto"), any(), eq(Integer.class)))
                    .thenReturn(new ResponseEntity<>(1, HttpStatus.OK));
            when(mock.exchange(eq("http://localhost:8081/deleteProyecto"), eq(HttpMethod.DELETE), any(), eq(Integer.class)))
                    .thenReturn(new ResponseEntity<>(1, HttpStatus.OK));
            when(mock.getForObject(eq("http://localhost:8081/buscarProyecto?id_proyecto={id_proyecto}"), eq(Proyectos.class), anyMap()))
                    .thenReturn(pValido);
        })) {
            ModelAndView mavSave = controller.setSaveProyecto(pValido);
            assertEquals("redirect:/inicio", mavSave.getViewName());

            ModelAndView mavDel = controller.eliminarProyecto(pValido);
            assertEquals("redirect:/inicio", mavDel.getViewName());

            ResponseEntity<Object> respUpd = controller.updateProyecto(pValido);
            assertEquals(HttpStatus.OK, respUpd.getStatusCode());
        }

        Proyectos pInvalido = new Proyectos();
        ModelAndView mavInv = controller.setSaveProyecto(pInvalido);
        assertEquals("inicio", mavInv.getViewName());
    }

    @Test
    @DisplayName("Consultas y operaciones sobre Usuarios, Tareas, Comentarios y Asignaciones")
    void operacionesCompletas_Exito() {
        Model model = new ConcurrentModel();
        Usuario u = new Usuario(1, "Gustavo", "g@test.com", "123", "Docente", null);
        Tareas t = new Tareas(1, "Tarea 1", "Desc", "2026-10-22", "pendientes", "primary", 10);
        Comentarios c = new Comentarios(1, "Gustavo", 1, "Hola", "2026-10-22");
        Asignaciones a = new Asignaciones(1, 1, 1, "2026-10-22");

        try (MockedConstruction<RestTemplate> mocked = Mockito.mockConstruction(RestTemplate.class, (mock, ctx) -> {
            when(mock.postForEntity(eq("http://localhost:8081/getUsuarioLogin"), any(), eq(Usuario.class)))
                    .thenReturn(new ResponseEntity<>(u, HttpStatus.OK));
            when(mock.postForObject(eq("http://localhost:8081/buscarUsuarios"), any(), eq(Usuario[].class)))
                    .thenReturn(new Usuario[] { u });
            when(mock.postForObject(eq("http://localhost:8081/getTarea"), any(), eq(Tareas[].class)))
                    .thenReturn(new Tareas[] { t });
            when(mock.postForObject(eq("http://localhost:8081/getComentario"), any(), eq(Comentarios[].class)))
                    .thenReturn(new Comentarios[] { c });
            when(mock.postForEntity(eq("http://localhost:8081/addTareas"), any(), eq(Integer.class)))
                    .thenReturn(new ResponseEntity<>(1, HttpStatus.OK));
            when(mock.postForEntity(eq("http://localhost:8081/addComentario"), any(), eq(Integer.class)))
                    .thenReturn(new ResponseEntity<>(1, HttpStatus.OK));
            when(mock.getForObject(eq("http://localhost:8081/buscarTarea?id_tarea={id_tarea}"), eq(Tareas.class), anyMap()))
                    .thenReturn(t);
            when(mock.exchange(eq("http://localhost:8081/deleteTareas"), eq(HttpMethod.DELETE), any(), eq(Integer.class)))
                    .thenReturn(new ResponseEntity<>(1, HttpStatus.OK));
            when(mock.postForObject(eq("http://localhost:8081/buscarTareas"), any(), eq(Tareas[].class)))
                    .thenReturn(new Tareas[] { t });
            when(mock.postForObject(eq("http://localhost:8081/buscarComentarios"), any(), eq(Comentarios[].class)))
                    .thenReturn(new Comentarios[] { c });
            when(mock.postForEntity(eq("http://localhost:8081/updateAsignacion"), any(), eq(Integer.class)))
                    .thenReturn(new ResponseEntity<>(1, HttpStatus.OK));
            when(mock.postForObject(eq("http://localhost:8081/buscarAsignaciones"), any(), eq(Asignaciones[].class)))
                    .thenReturn(new Asignaciones[] { a });
        })) {
            assertEquals(HttpStatus.OK, controller.verificarUsuario(u).getStatusCode());
            assertEquals(HttpStatus.OK, controller.consultarUsuarios(model).getStatusCode());
            assertEquals(HttpStatus.OK, controller.obtenerTareas(t, model).getStatusCode());
            assertEquals(HttpStatus.OK, controller.obtenerComentarios(c, model).getStatusCode());
            assertEquals("proyecto", controller.getProyecto(model));
            assertEquals("redirect:/proyecto", controller.setSaveTareas(t).getViewName());
            assertEquals("proyecto", controller.setSaveComentario(c));
            assertEquals(HttpStatus.OK, controller.updateTarea(t).getStatusCode());
            assertEquals("redirect:/proyecto", controller.eliminarTarea(t).getViewName());
            assertEquals(HttpStatus.OK, controller.verificarTarea(t).getStatusCode());
            assertEquals(HttpStatus.OK, controller.cambiosTareas(t).getStatusCode());
            assertEquals(HttpStatus.OK, controller.consultarTareas().getStatusCode());
            assertEquals(HttpStatus.OK, controller.consultarComentarios().getStatusCode());
            assertEquals(HttpStatus.OK, controller.updateAsignacion(a).getStatusCode());
            assertEquals(HttpStatus.OK, controller.consultarAsignaciones().getStatusCode());

            BindingResult br = new BeanPropertyBindingResult(u, "usuario");
            assertNotNull(controller.setSaveUsuario(u, br));
        }
    }

    @Test
    @DisplayName("Manejo de excepciones cuando el backend no está disponible")
    void operacionesCompletas_BackendCaido() {
        Model model = new ConcurrentModel();
        Usuario u = new Usuario(1, "Gustavo", "g@test.com", "123", "Docente", null);
        Tareas t = new Tareas(1, "Tarea 1", "Desc", "2026-10-22", "pendientes", "primary", 10);
        Comentarios c = new Comentarios(1, "Gustavo", 1, "Hola", "2026-10-22");
        Asignaciones a = new Asignaciones(1, 1, 1, "2026-10-22");

        try (MockedConstruction<RestTemplate> mocked = Mockito.mockConstruction(RestTemplate.class, (mock, ctx) -> {
            when(mock.postForEntity(any(String.class), any(), any()))
                    .thenThrow(new RestClientException("Backend caído"));
            when(mock.postForObject(any(String.class), any(), any()))
                    .thenThrow(new RestClientException("Backend caído"));
            when(mock.getForObject(any(String.class), any(), anyMap()))
                    .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));
            when(mock.exchange(any(String.class), any(), any(), eq(Integer.class)))
                    .thenThrow(new RestClientException("Backend caído"));
        })) {
            assertEquals(HttpStatus.UNAUTHORIZED, controller.verificarUsuario(u).getStatusCode());
            assertEquals(HttpStatus.SERVICE_UNAVAILABLE, controller.consultarUsuarios(model).getStatusCode());
            assertEquals(HttpStatus.SERVICE_UNAVAILABLE, controller.obtenerTareas(t, model).getStatusCode());
            assertEquals(HttpStatus.SERVICE_UNAVAILABLE, controller.obtenerComentarios(c, model).getStatusCode());
            assertEquals("proyecto", controller.getProyecto(model));
            assertEquals("redirect:/proyecto", controller.setSaveTareas(t).getViewName());
            assertEquals("proyecto", controller.setSaveComentario(c));
            assertEquals(HttpStatus.NOT_FOUND, controller.updateTarea(t).getStatusCode());
            assertEquals("redirect:/proyecto", controller.eliminarTarea(t).getViewName());
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, controller.verificarTarea(t).getStatusCode());
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, controller.cambiosTareas(t).getStatusCode());
            assertEquals(HttpStatus.SERVICE_UNAVAILABLE, controller.consultarTareas().getStatusCode());
            assertEquals(HttpStatus.SERVICE_UNAVAILABLE, controller.consultarComentarios().getStatusCode());
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, controller.updateAsignacion(a).getStatusCode());
            assertEquals(HttpStatus.SERVICE_UNAVAILABLE, controller.consultarAsignaciones().getStatusCode());
        }
    }
}
