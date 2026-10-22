package com.mx.cbtis.qa;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.mx.cbtis.controller.GestorController;
import com.mx.cbtis.modelo.Proyectos;

@SuppressWarnings("java:S100")
class ProyectoServicioConsultaTest {

	private GestorController gestorController;

	@BeforeEach
	void setUp() {
		gestorController = new GestorController();
	}

	@Test
	@DisplayName("AUT-E01 - Servicio de consulta: el backend (simulado) responde con lista válida y código 200")
	void AUT_E01_consultarProyectos_exito() {
		Proyectos p1 = new Proyectos(1, "Proyecto Alpha", "Descripción Alpha", "2024-01-01", "2024-06-01", "En proceso", "#FF5733");
		Proyectos p2 = new Proyectos(2, "Proyecto Beta", "Descripción Beta", "2024-02-01", "2024-08-01", "Finalizado", "#33FF57");
		Proyectos[] proyectosSimulados = new Proyectos[] { p1, p2 };

		Model model = new ConcurrentModel();

		try (MockedConstruction<RestTemplate> mocked = Mockito.mockConstruction(RestTemplate.class, (mock, context) -> {
			when(mock.postForObject(eq("http://localhost:8081/buscarProyectos"), any(), eq(Proyectos[].class)))
				.thenReturn(proyectosSimulados);
		})) {
			ResponseEntity<Object> respuesta = gestorController.consultarProyectos(model);

			assertNotNull(respuesta, "La respuesta no debe ser nula");
			assertEquals(HttpStatus.OK.value(), respuesta.getStatusCode().value(), "El código de respuesta debe ser 200");

			@SuppressWarnings("unchecked")
			List<Proyectos> resultado = (List<Proyectos>) respuesta.getBody();
			assertNotNull(resultado, "El cuerpo de la respuesta debe contener la lista");
			assertEquals(2, resultado.size(), "Debe devolver exactamente los 2 proyectos simulados");
			assertEquals("Proyecto Alpha", resultado.get(0).getNombre());

			assertTrue(model.containsAttribute("listaProyectos"), "El modelo debe contener el atributo 'listaProyectos'");
		}
	}

	@Test
	@DisplayName("AUT-F01 - Servicio de consulta: el backend (simulado) responde error 500 o no disponible")
	void AUT_F01_consultarProyectos_backendError500() {
		Model model = new ConcurrentModel();

		try (MockedConstruction<RestTemplate> mocked = Mockito.mockConstruction(RestTemplate.class, (mock, context) -> {
			when(mock.postForObject(eq("http://localhost:8081/buscarProyectos"), any(), eq(Proyectos[].class)))
				.thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno en el backend"));
		})) {
			ResponseEntity<Object> respuesta = assertDoesNotThrow(() -> gestorController.consultarProyectos(model),
				"El controlador debe capturar la falla del backend y no propagar una excepción no controlada");

			assertNotNull(respuesta, "Debe retornar una respuesta controlada ante error del backend");
			assertFalse(respuesta.getStatusCode().is2xxSuccessful(), "El código no debe indicar éxito si el backend falló");
		}
	}
}
