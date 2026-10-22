package com.mx.cbtis.gestor.dto;

import com.mx.cbtis.gestor.modelo.Tareas;

@SuppressWarnings({"java:S116", "java:S100", "java:S117"})
public record TareasDTO(
		Integer id_tarea,
		String nombre,
		String descripcion,
		String fecha_vencimiento,
		String lista,
		String estado,
		Integer id_proyecto) {

	public Tareas toEntity() {
		return new Tareas(id_tarea, nombre, descripcion, fecha_vencimiento, lista, estado, id_proyecto);
	}
}
