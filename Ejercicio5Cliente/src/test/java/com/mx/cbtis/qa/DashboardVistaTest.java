package com.mx.cbtis.qa;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.mx.cbtis.controller.GestorController;
import com.mx.cbtis.modelo.Proyectos;

/**
 * Pruebas automáticas para las vistas y componentes del Dashboard (Inicio) y Detalle de Proyectos.
 */
public class DashboardVistaTest {

    private GestorController gestorController;

    @BeforeEach
    void setUp() {
        gestorController = new GestorController();
    }

    @Test
    @DisplayName("AUT-E03 - Vista/Componente: la consulta de proyectos finaliza correctamente y presenta en dashboard")
    public void AUT_E03_dashboardInicio_consultaProyectos_presentaEnVista() {
        // GIVEN: Acceso a la vista /inicio y consulta de proyectos existentes
        Model modelInicio = new ConcurrentModel();
        Model modelConsulta = new ConcurrentModel();

        Proyectos[] listaMock = new Proyectos[] {
            new Proyectos(101, "Sistema ERP", "Desarrollo ERP", "2024-03-01", "2024-11-01", "En proceso", "#1E88E5")
        };

        try (MockedConstruction<RestTemplate> mocked = Mockito.mockConstruction(RestTemplate.class, (mock, context) -> {
            when(mock.postForObject(eq("http://localhost:8081/buscarProyectos"), any(), eq(Proyectos[].class)))
                .thenReturn(listaMock);
        })) {
            // WHEN: Se carga el dashboard de inicio y se consulta la lista de proyectos
            String vista = gestorController.getInicio(modelInicio);
            ResponseEntity<Object> respuesta = gestorController.consultarProyectos(modelConsulta);

            // THEN: El modelo contiene los proyectos y la vista del dashboard los presenta
            assertEquals("inicio", vista, "La vista devuelta debe ser 'inicio'");
            assertTrue(modelInicio.containsAttribute("proyectos"), "El modelo de inicio debe contener el atributo 'proyectos'");

            assertEquals(HttpStatus.OK.value(), respuesta.getStatusCode().value(), "La consulta debe responder 200 OK");
            assertTrue(modelConsulta.containsAttribute("listaProyectos"), "El modelo debe contener 'listaProyectos' para la vista");
        }
    }

    @Test
    @DisplayName("AUT-F03 - Vista/Componente: se solicita un proyecto inexistente (id que no existe)")
    public void AUT_F03_vistaProyecto_solicitaProyectoInexistente_estadoControlado() {
        // GIVEN: Se solicita un proyecto con ID inexistente (ej. ID 99999)
        Proyectos proyectoInexistente = new Proyectos();
        proyectoInexistente.setId_proyecto(99999);

        try (MockedConstruction<RestTemplate> mocked = Mockito.mockConstruction(RestTemplate.class, (mock, context) -> {
            when(mock.getForObject(eq("http://localhost:8081/buscarProyecto?id_proyecto={id_proyecto}"), eq(Proyectos.class), anyMap()))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND, "Proyecto no encontrado en backend"));
        })) {
            // WHEN & THEN: La interfaz/controlador debe mantener un estado controlado e informar el resultado
            // sin propagar una excepción no controlada hacia el cliente web.
            // NOTA: En GestorController.actualizarProyecto no hay try-catch ante 404 del backend,
            // por lo que fallará evidenciando el defecto de robustez.
            ResponseEntity<Object> respuesta = assertDoesNotThrow(() -> {
                return gestorController.updateProyecto(proyectoInexistente);
            }, "El controlador debe capturar el error 404 del backend y mantener un estado controlado sin lanzar excepciones");

            assertNotNull(respuesta, "Debe retornar una respuesta controlada");
            assertFalse(respuesta.getStatusCode().is2xxSuccessful(), "El código HTTP no debe ser 200 si el proyecto no existe");
        }
    }
}
