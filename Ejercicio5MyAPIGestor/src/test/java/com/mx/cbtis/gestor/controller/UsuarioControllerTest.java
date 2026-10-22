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

import com.mx.cbtis.gestor.modelo.Usuario;
import com.mx.cbtis.gestor.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class UsuarioControllerTest {

    @Mock
    private UsuarioRepository usuariotable;

    @InjectMocks
    private UsuarioController usuarioController;

    @Test
    @DisplayName("UT-01: Login exitoso de usuario retorna 200 OK")
    void getUsuarioLogin_Exito() {
        Usuario user = new Usuario(1, "Gustavo Mendoza", "gustavo@cbtis75.edu.mx", "Pass123*", "Docente", "Images/logo.png");
        when(usuariotable.findByCorreoPass("gustavo@cbtis75.edu.mx", "Pass123*")).thenReturn(Optional.of(user));

        ResponseEntity<Usuario> response = usuarioController.getUsuarioLogin(user);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Gustavo Mendoza", response.getBody().getNombre());
    }

    @Test
    @DisplayName("UT-29: Login con credenciales incorrectas retorna 401 UNAUTHORIZED")
    void getUsuarioLogin_NoAutorizado() {
        Usuario user = new Usuario(null, null, "gustavo@cbtis75.edu.mx", "WrongPass", null, null);
        when(usuariotable.findByCorreoPass("gustavo@cbtis75.edu.mx", "WrongPass")).thenReturn(Optional.empty());

        ResponseEntity<Usuario> response = usuarioController.getUsuarioLogin(user);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("000", response.getBody().getNombre());
    }

    @Test
    @DisplayName("Login con datos nulos retorna 400 BAD_REQUEST")
    void getUsuarioLogin_BadRequest() {
        ResponseEntity<Usuario> responseNull = usuarioController.getUsuarioLogin(null);
        assertEquals(HttpStatus.BAD_REQUEST, responseNull.getStatusCode());

        Usuario userSinCorreo = new Usuario();
        ResponseEntity<Usuario> responseSinCorreo = usuarioController.getUsuarioLogin(userSinCorreo);
        assertEquals(HttpStatus.BAD_REQUEST, responseSinCorreo.getStatusCode());

        Usuario userSinPass = new Usuario();
        userSinPass.setCorreo_electronico("gustavo@cbtis75.edu.mx");
        ResponseEntity<Usuario> responseSinPass = usuarioController.getUsuarioLogin(userSinPass);
        assertEquals(HttpStatus.BAD_REQUEST, responseSinPass.getStatusCode());
    }

    @Test
    @DisplayName("UT-02: Buscar usuario por ID existente retorna 200 OK")
    void getUsuario_Existente() {
        Usuario user = new Usuario(1, "Ana Lopez", "ana@cbtis75.edu.mx", "Clave123", "Estudiante", "#FF5733");
        when(usuariotable.findById(1)).thenReturn(Optional.of(user));

        ResponseEntity<Usuario> response = usuarioController.getUsuario(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getId_usuario());
        assertEquals("ana@cbtis75.edu.mx", response.getBody().getCorreo_electronico());
        assertEquals("Clave123", response.getBody().getContrasena());
        assertEquals("Estudiante", response.getBody().getRol());
        assertEquals("#FF5733", response.getBody().getImagen_usuario());
    }

    @Test
    @DisplayName("UT-30: Buscar usuario por ID inexistente retorna 404 NOT_FOUND")
    void getUsuario_Inexistente() {
        when(usuariotable.findById(999)).thenReturn(Optional.empty());

        ResponseEntity<Usuario> response = usuarioController.getUsuario(999);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("UT-03: Agregar usuario válido retorna 200 OK")
    void addUsuario_Valido() {
        Usuario user = new Usuario();
        user.setId_usuario(2);
        user.setNombre("Carlos Ruiz");
        user.setCorreo_electronico("carlos@cbtis75.edu.mx");
        user.setContrasena("Pass2026");
        user.setRol("Docente");
        user.setImagen_usuario("Images/logo.png");

        when(usuariotable.save(any(Usuario.class))).thenReturn(user);

        ResponseEntity<Object> response = usuarioController.addUsuario(user);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody());
    }

    @Test
    @DisplayName("UT-31: Agregar usuario con datos inválidos retorna 400 BAD_REQUEST")
    void addUsuario_Invalido() {
        assertEquals(HttpStatus.BAD_REQUEST, usuarioController.addUsuario(null).getStatusCode());

        Usuario u1 = new Usuario(null, "", "correo@valido.com", "123", "Rol", "Img");
        assertEquals(HttpStatus.BAD_REQUEST, usuarioController.addUsuario(u1).getStatusCode());

        Usuario u2 = new Usuario(null, "Nombre", "correo-invalido", "123", "Rol", "Img");
        assertEquals(HttpStatus.BAD_REQUEST, usuarioController.addUsuario(u2).getStatusCode());

        Usuario u3 = new Usuario(null, "Nombre", "correo@valido.com", "   ", "Rol", "Img");
        assertEquals(HttpStatus.BAD_REQUEST, usuarioController.addUsuario(u3).getStatusCode());
    }

    @Test
    @DisplayName("UT-04: Actualizar usuario retorna 1")
    void updateUsuario_Exito() {
        Usuario user = new Usuario(1, "Carlos Modificado", "carlos@cbtis75.edu.mx", "Pass", "Docente", "Img");
        when(usuariotable.save(any(Usuario.class))).thenReturn(user);

        int flag = usuarioController.updateUsuario(user);
        assertEquals(1, flag);
    }

    @Test
    @DisplayName("UT-05: Eliminar usuario retorna 1")
    void deleteUsuario_Exito() {
        Usuario user = new Usuario(1, "Carlos", "carlos@cbtis75.edu.mx", "Pass", "Docente", "Img");
        doNothing().when(usuariotable).delete(any(Usuario.class));

        int flag = usuarioController.deleteUsuario(user);
        assertEquals(1, flag);
    }

    @Test
    @DisplayName("UT-06: Buscar todos los usuarios retorna lista completa")
    void getUsuarios_Exito() {
        Usuario u1 = new Usuario(1, "U1", "u1@test.com", "123", "Docente", "Img1");
        Usuario u2 = new Usuario(2, "U2", "u2@test.com", "456", "Estudiante", "Img2");
        when(usuariotable.findAll()).thenReturn(Arrays.asList(u1, u2));

        List<Usuario> resultado = usuarioController.getUsuarios();

        assertEquals(2, resultado.size());
    }
}
