package com.mx.cbtis.gestor.dto;

import com.mx.cbtis.gestor.modelo.Comentarios;

@SuppressWarnings({"java:S116", "java:S100", "java:S117"})
public record ComentariosDTO(
		Integer id_comentario,
		String id_usuario,
		Integer id_tarea,
		String texto_comentario,
		String fecha_hora) {

	public Comentarios toEntity() {
		return new Comentarios(id_comentario, id_usuario, id_tarea, texto_comentario, fecha_hora);
	}
}
