package com.mx.cbtis.qa;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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

/**
 * Pruebas automáticas para la validación y envío de formularios (registro de usuarios).
 */
public class FormularioValidacionTest {

    private GestorController gestorController;

    @BeforeEach
    void setUp() {
        gestorController = new GestorController();
    }

    @Test
    @DisplayName("AUT-E02 - Formulario: se proporcionan datos válidos (nombre, correo con formato válido, contraseña)")
    public void AUT_E02_formularioRegistro_datosValidos_permiteEnvio() {
        // GIVEN: Usuario con información completa y válida
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
            // WHEN: Se envía el formulario al endpoint /saveUsuario
            ModelAndView resultado = gestorController.setSaveUsuario(usuarioValido);

            // THEN: La validación permite enviar la información y redirige a /login
            assertNotNull(resultado, "El resultado del formulario no debe ser nulo");
            assertEquals("redirect:/login", resultado.getViewName(), "El envío válido debe redirigir a /login");
        }
    }

    @Test
    @DisplayName("AUT-F02 - Formulario: datos inválidos o incompletos (campos vacíos, correo sin formato)")
    public void AUT_F02_formularioRegistro_datosInvalidos_impideEnvio() {
        // GIVEN: Usuario con datos inválidos (nombre vacío, correo sin formato, contraseña vacía)
        Usuario usuarioInvalido = new Usuario();
        usuarioInvalido.setNombre(""); // Campo vacío
        usuarioInvalido.setCorreo_electronico("correo-sin-formato-arroba"); // Correo inválido
        usuarioInvalido.setContrasena(""); // Contraseña vacía
        usuarioInvalido.setRol("0");

        try (MockedConstruction<RestTemplate> mocked = Mockito.mockConstruction(RestTemplate.class, (mock, context) -> {
            when(mock.postForEntity(eq("http://localhost:8081/addUsuario"), any(Usuario.class), eq(Integer.class)))
                .thenReturn(new ResponseEntity<>(1, HttpStatus.OK));
        })) {
            // WHEN: Se procesa el formulario en el controlador
            ModelAndView resultado = gestorController.setSaveUsuario(usuarioInvalido);

            // THEN: La validación debe impedir el envío y no redirigir con éxito como si fuera válido.
            // NOTA: El frontend carece de Bean Validation (@Valid/BindingResult) en el servidor;
            // por tanto el controlador procesa el formulario ciegamente y redirige a /login.
            // Esta aserción verifica el requerimiento de calidad y fallará revelando el defecto.
            assertNotNull(resultado, "Debe retornar un modelo de vista");
            assertFalse("redirect:/login".equals(resultado.getViewName()),
                "La validación del servidor debió impedir el envío con datos inválidos en lugar de redirigir a /login");
        }
    }
}
