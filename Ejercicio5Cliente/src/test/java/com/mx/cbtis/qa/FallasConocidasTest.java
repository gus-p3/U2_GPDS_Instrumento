package com.mx.cbtis.qa;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Pruebas automáticas que documentan defectos conocidos del frontend (UI-07, UI-14, UI-16).
 * Diseñadas para FALLAR en el estado actual del código y servir como reprueba cuando se corrijan.
 */
public class FallasConocidasTest {

    private final Path templatesDir = Paths.get("src/main/resources/templates");
    private final Path jsDir = Paths.get("src/main/resources/static/js");

    @Test
    @DisplayName("UI-07 - Defecto conocido: el formulario de registro (/register) no incluye selector real de imagen/avatar")
    public void UI_07_formularioRegistro_debeIncluirCampoAvatarVisible() throws IOException {
        // GIVEN: La plantilla de registro register.html
        Path registerPath = templatesDir.resolve("register.html");
        assertTrue(Files.exists(registerPath), "El archivo register.html debe existir");
        String contenido = Files.readString(registerPath);

        // WHEN & THEN: Se verifica que exista un campo interactivo visible (p. ej. type="file" o selector de avatar)
        // Defecto actual: Solo tiene un input type="hidden" generado con un color hexadecimal por JS.
        boolean tieneCampoAvatarVisible = contenido.contains("type=\"file\"") 
                || (contenido.contains("imagen_usuario") && !contenido.contains("type=\"hidden\" id=\"imagenUsuario\""));

        assertTrue(tieneCampoAvatarVisible, 
            "FALLO ESPERADO (UI-07): El formulario /register solo contiene un input hidden con color aleatorio y no permite al usuario cargar ni seleccionar su avatar");
    }

    @Test
    @DisplayName("UI-14 - Defecto conocido: el modal/formulario de nueva tarea solo captura nombre; no permite descripción ni asignación")
    public void UI_14_creacionTarea_debePermitirDescripcionYAsignacion() throws IOException {
        // GIVEN: El formulario de nueva tarea en proyecto.html
        Path proyectoPath = templatesDir.resolve("proyecto.html");
        assertTrue(Files.exists(proyectoPath), "El archivo proyecto.html debe existir");
        String contenidoHtml = Files.readString(proyectoPath);

        // WHEN & THEN: La creación de tarea debe permitir capturar descripción y miembro asignado
        // Defecto actual: El bloque <form> de nueva tarea (#inputTarea) solo contiene el input de texto del nombre.
        boolean permiteDescripcionEnCreacion = contenidoHtml.contains("id=\"descripcionTareaNueva\"") 
                || contenidoHtml.contains("placeholder=\"Descripción de la tarea\"");
        boolean permiteAsignadoEnCreacion = contenidoHtml.contains("id=\"asignadoTareaNueva\"") 
                || contenidoHtml.contains("name=\"id_usuario_asignado\"");

        assertTrue(permiteDescripcionEnCreacion && permiteAsignadoEnCreacion,
            "FALLO ESPERADO (UI-14): La interfaz de creación rápida de tarea únicamente captura el nombre; no ofrece campos para descripción, fecha de vencimiento ni asignación de integrante");
    }

    @Test
    @DisplayName("UI-16 - Defecto conocido: la edición de tarea no permite reasignar a un integrante")
    public void UI_16_edicionTarea_debePermitirReasignarIntegrante() throws IOException {
        // GIVEN: El modal de edición de tarea #editarTarea en proyecto.html
        Path proyectoPath = templatesDir.resolve("proyecto.html");
        assertTrue(Files.exists(proyectoPath), "El archivo proyecto.html debe existir");
        String contenido = Files.readString(proyectoPath);

        // WHEN & THEN: Debe existir un control activo (no comentado) en el modal para reasignar integrante
        // Defecto actual: El botón de miembros (<button ...> Miembros</button>) se encuentra dentro de un bloque comentado <!-- ... -->
        boolean tieneSelectorMiembrosActivo = contenido.contains("id=\"reasignarMiembro\"") 
                || (contenido.contains("Miembros</button>") && !contenido.contains("<!--\r\n\t\t\t\t\t\t\t\t  <button href=\"#\" type=\"button\" class=\"bg-secondary-subtle m-1 p-2 border border-0\"><svg")
                    && !contenido.contains("<!--\n\t\t\t\t\t\t\t\t  <button href=\"#\" type=\"button\" class=\"bg-secondary-subtle m-1 p-2 border border-0\"><svg"));

        assertTrue(tieneSelectorMiembrosActivo,
            "FALLO ESPERADO (UI-16): En el modal de edición de tarea (#editarTarea) el botón y control de Miembros está completamente comentado en el código HTML, impidiendo reasignar integrantes");
    }
}
