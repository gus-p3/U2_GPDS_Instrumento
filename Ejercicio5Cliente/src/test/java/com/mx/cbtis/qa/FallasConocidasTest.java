package com.mx.cbtis.qa;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@SuppressWarnings("java:S100")
class FallasConocidasTest {

	private final Path templatesDir = Paths.get("src/main/resources/templates");

	@Test
	@DisplayName("UI-07 - Defecto conocido: el formulario de registro (/register) incluye selector real de imagen/avatar")
	void UI_07_formularioRegistro_debeIncluirCampoAvatarVisible() throws IOException {
		Path registerPath = templatesDir.resolve("register.html");
		assertTrue(Files.exists(registerPath), "El archivo register.html debe existir");
		String contenido = Files.readString(registerPath);

		boolean tieneCampoAvatarVisible = contenido.contains("type=\"file\"")
				|| (contenido.contains("imagen_usuario") && !contenido.contains("type=\"hidden\" id=\"imagenUsuario\""));

		assertTrue(tieneCampoAvatarVisible,
			"El formulario /register debe permitir al usuario cargar o seleccionar su avatar");
	}

	@Test
	@DisplayName("UI-14 - Defecto conocido: el modal/formulario de tarea permite descripción y asignación")
	void UI_14_creacionTarea_debePermitirDescripcionYAsignacion() throws IOException {
		Path proyectoPath = templatesDir.resolve("proyecto.html");
		assertTrue(Files.exists(proyectoPath), "El archivo proyecto.html debe existir");
		String contenidoHtml = Files.readString(proyectoPath);

		boolean permiteDescripcionEnCreacion = contenidoHtml.contains("id=\"descripcionTareaNueva\"")
				|| contenidoHtml.contains("placeholder=\"Descripción de la tarea\"")
				|| contenidoHtml.contains("id=\"descripcionT\"");
		boolean permiteAsignadoEnCreacion = contenidoHtml.contains("id=\"asignadoTareaNueva\"")
				|| contenidoHtml.contains("name=\"id_usuario_asignado\"")
				|| contenidoHtml.contains("id=\"reasignarMiembro\"");

		assertTrue(permiteDescripcionEnCreacion && permiteAsignadoEnCreacion,
			"La interfaz de tarea debe ofrecer campos para descripción y asignación de integrante");
	}

	@Test
	@DisplayName("UI-16 - Defecto conocido: la edición de tarea permite reasignar a un integrante")
	void UI_16_edicionTarea_debePermitirReasignarIntegrante() throws IOException {
		Path proyectoPath = templatesDir.resolve("proyecto.html");
		assertTrue(Files.exists(proyectoPath), "El archivo proyecto.html debe existir");
		String contenido = Files.readString(proyectoPath);

		boolean tieneSelectorMiembrosActivo = contenido.contains("id=\"reasignarMiembro\"")
				|| (contenido.contains("Miembros</button>") && !contenido.contains("<!--\r\n\t\t\t\t\t\t\t\t  <button href=\"#\" type=\"button\" class=\"bg-secondary-subtle m-1 p-2 border border-0\"><svg")
					&& !contenido.contains("<!--\n\t\t\t\t\t\t\t\t  <button href=\"#\" type=\"button\" class=\"bg-secondary-subtle m-1 p-2 border border-0\"><svg"));

		assertTrue(tieneSelectorMiembrosActivo,
			"En el modal de edición de tarea (#editarTarea) debe existir un control activo para reasignar integrantes");
	}
}
