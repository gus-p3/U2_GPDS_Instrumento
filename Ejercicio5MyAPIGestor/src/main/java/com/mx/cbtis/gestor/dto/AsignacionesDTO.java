package com.mx.cbtis.gestor.dto;

import com.mx.cbtis.gestor.modelo.Asignaciones;

@SuppressWarnings({"java:S116", "java:S100", "java:S117"})
public record AsignacionesDTO(
		Integer id_asignacion,
		Integer id_usuario,
		Integer id_tarea,
		String fecha_asignacion) {

	public Asignaciones toEntity() {
		return new Asignaciones(id_asignacion, id_usuario, id_tarea, fecha_asignacion);
	}
}
