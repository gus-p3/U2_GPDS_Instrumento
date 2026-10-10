package com.mx.cbtis.modelo;

import jakarta.validation.constraints.NotBlank;

public class Proyectos {

	private Integer id_proyecto;

	@NotBlank(message = "El nombre del proyecto es obligatorio")
	private String nombre;

	@NotBlank(message = "La descripción es obligatoria")
	private String descripcion;

	@NotBlank(message = "La fecha de inicio es obligatoria")
	private String fecha_inicio;

	@NotBlank(message = "La fecha de finalización es obligatoria")
	private String fecha_finalizacion;

	@NotBlank(message = "El estado del proyecto es obligatorio")
	private String estado_proyecto;

	private String fondo;
	
	public Proyectos() {
		super();
	}

	public Proyectos(Integer id_proyecto, String nombre, String descripcion, String fecha_inicio,
			String fecha_finalizacion, String estado_proyecto, String fondo) {
		super();
		this.id_proyecto = id_proyecto;
		this.nombre = nombre;
		this.descripcion = descripcion;
		this.fecha_inicio = fecha_inicio;
		this.fecha_finalizacion = fecha_finalizacion;
		this.estado_proyecto = estado_proyecto;
		this.fondo = fondo;
	}

	public Integer getId_proyecto() {
		return id_proyecto;
	}

	public void setId_proyecto(Integer id_proyecto) {
		this.id_proyecto = id_proyecto;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getFecha_inicio() {
		return fecha_inicio;
	}

	public void setFecha_inicio(String fecha_inicio) {
		this.fecha_inicio = fecha_inicio;
	}

	public String getFecha_finalizacion() {
		return fecha_finalizacion;
	}

	public void setFecha_finalizacion(String fecha_finalizacion) {
		this.fecha_finalizacion = fecha_finalizacion;
	}

	public String getEstado_proyecto() {
		return estado_proyecto;
	}

	public void setEstado_proyecto(String estado_proyecto) {
		this.estado_proyecto = estado_proyecto;
	}

	public String getFondo() {
		return fondo;
	}

	public void setFondo(String fondo) {
		this.fondo = fondo;
	}
}
