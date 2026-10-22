package com.mx.cbtis.modelo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@SuppressWarnings({"java:S116", "java:S100"})
public class Comentarios {

	private Integer id_comentario;

	@NotBlank(message = "El usuario es obligatorio")
	private String id_usuario;

	@NotNull(message = "El ID de tarea es obligatorio")
	private Integer id_tarea;

	@NotBlank(message = "El comentario no puede estar vacío")
	private String texto_comentario;

	private String fecha_hora;

	public Comentarios() {
		super();
	}

	public Comentarios(Integer idComentario, String idUsuario, Integer idTarea, String textoComentario,
			String fechaHora) {
		super();
		this.id_comentario = idComentario;
		this.id_usuario = idUsuario;
		this.id_tarea = idTarea;
		this.texto_comentario = textoComentario;
		this.fecha_hora = fechaHora;
	}

	public Integer getId_comentario() {
		return id_comentario;
	}

	public void setId_usuario(String idUsuario) {
		this.id_usuario = idUsuario;
	}

	public String getId_usuario() {
		return id_usuario;
	}

	public void setId_comentario(Integer idComentario) {
		this.id_comentario = idComentario;
	}

	public Integer getId_tarea() {
		return id_tarea;
	}

	public void setId_tarea(Integer idTarea) {
		this.id_tarea = idTarea;
	}

	public String getTexto_comentario() {
		return texto_comentario;
	}

	public void setTexto_comentario(String textoComentario) {
		this.texto_comentario = textoComentario;
	}

	public String getFecha_hora() {
		return fecha_hora;
	}

	public void setFecha_hora(String fechaHora) {
		this.fecha_hora = fechaHora;
	}
}
