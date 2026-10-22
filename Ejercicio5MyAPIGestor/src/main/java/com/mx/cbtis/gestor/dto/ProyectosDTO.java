package com.mx.cbtis.gestor.dto;

import com.mx.cbtis.gestor.modelo.Proyectos;

@SuppressWarnings({"java:S116", "java:S100", "java:S117"})
public record ProyectosDTO(
		Integer id_proyecto,
		String nombre,
		String descripcion,
		String fecha_inicio,
		String fecha_finalizacion,
		String estado_proyecto,
		String fondo) {

	public Proyectos toEntity() {
		return new Proyectos(id_proyecto, nombre, descripcion, fecha_inicio, fecha_finalizacion, estado_proyecto, fondo);
	}
}
