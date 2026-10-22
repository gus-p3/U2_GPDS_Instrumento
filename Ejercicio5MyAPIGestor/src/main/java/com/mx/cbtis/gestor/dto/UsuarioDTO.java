package com.mx.cbtis.gestor.dto;

import com.mx.cbtis.gestor.modelo.Usuario;

@SuppressWarnings({"java:S116", "java:S100", "java:S117"})
public record UsuarioDTO(
		Integer id_usuario,
		String nombre,
		String correo_electronico,
		String contrasena,
		String rol,
		String imagen_usuario) {

	public Usuario toEntity() {
		return new Usuario(id_usuario, nombre, correo_electronico, contrasena, rol, imagen_usuario);
	}
}
