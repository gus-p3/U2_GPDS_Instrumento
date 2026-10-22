package com.mx.cbtis.qa;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import com.mx.cbtis.controller.GestorController;
import com.mx.cbtis.modelo.Usuario;

@SuppressWarnings("java:S100")
class FormularioValidacionTest {

	private GestorController gestorController;

	@BeforeEach
	void setUp() {
		gestorController = new GestorController();
	}

	@Test
	@DisplayName("AUT-E02 - Formulario: se proporcionan datos válidos (nombre, correo con formato válido, contraseña)")
	void AUT_E02_formularioRegistro_datosValidos_permiteEnvio() {
		Usuario usuarioValido = new Usuario();
		usuarioValido.setNombre("Carlos Ruiz");
		usuarioValido.setCorreo_electronico("carlos.ruiz@cbtis75.edu.mx");
		usuarioValido.setContrasena("ClaveSegura2024*");
		usuarioValido.setRol("Docente");
		usuarioValido.setImagen_usuario("#4A90E2");

		try (MockedConstruction<RestTemplate> mocked = Mockito.mockConstruction(RestTemplate.class, (mock, context) -> {
			when(mock.postForEntity(eq("http://localhost:8081/addUsuario"), any(Usuario.class), eq(Integer.class)))
				.thenReturn(new ResponseEntity<>(1, HttpStatus.OK));
		})) {
			ModelAndView resultado = gestorController.setSaveUsuario(usuarioValido);

			assertNotNull(resultado, "El resultado del formulario no debe ser nulo");
			assertEquals("redirect:/login", resultado.getViewName(), "El envío válido debe redirigir a /login");
		}
	}

	@Test
	@DisplayName("AUT-F02 - Formulario: datos inválidos o incompletos (campos vacíos, correo sin formato)")
	void AUT_F02_formularioRegistro_datosInvalidos_impideEnvio() {
		Usuario usuarioInvalido = new Usuario();
		usuarioInvalido.setNombre("");
		usuarioInvalido.setCorreo_electronico("correo-sin-formato-arroba");
		usuarioInvalido.setContrasena("");
		usuarioInvalido.setRol("0");

		try (MockedConstruction<RestTemplate> mocked = Mockito.mockConstruction(RestTemplate.class, (mock, context) -> {
			when(mock.postForEntity(eq("http://localhost:8081/addUsuario"), any(Usuario.class), eq(Integer.class)))
				.thenReturn(new ResponseEntity<>(1, HttpStatus.OK));
		})) {
			ModelAndView resultado = gestorController.setSaveUsuario(usuarioInvalido);

			assertNotNull(resultado, "Debe retornar un modelo de vista");
			assertNotEquals("redirect:/login", resultado.getViewName(),
				"La validación del servidor debe impedir el envío con datos inválidos");
		}
	}
}
