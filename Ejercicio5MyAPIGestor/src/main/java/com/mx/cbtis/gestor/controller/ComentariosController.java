package com.mx.cbtis.gestor.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mx.cbtis.gestor.dto.ComentariosDTO;
import com.mx.cbtis.gestor.modelo.Comentarios;
import com.mx.cbtis.gestor.repository.ComentariosRepository;
import com.mx.cbtis.gestor.repository.TareasRepository;

@RestController
public class ComentariosController {

	private final ComentariosRepository comentariostable;
	private final TareasRepository tareastable;

	public ComentariosController(ComentariosRepository comentariostable, TareasRepository tareastable) {
		this.comentariostable = comentariostable;
		this.tareastable = tareastable;
	}

	@PostMapping("/getComentario")
	public List<Comentarios> getComentarioEndpoint(@RequestBody(required = false) ComentariosDTO idComentarioDto) {
		return getComentario(idComentarioDto != null ? idComentarioDto.toEntity() : null);
	}

	public List<Comentarios> getComentario(Comentarios idComentario) {
		if (idComentario == null || idComentario.getId_tarea() == null) {
			return Collections.emptyList();
		}
		return comentariostable.findByTarea(idComentario.getId_tarea());
	}

	@GetMapping("/buscarComentario")
	public ResponseEntity<Comentarios> getComentario(@RequestParam int id) {
		Optional<Comentarios> comentario = comentariostable.findById(id);
		if (comentario.isPresent()) {
			return new ResponseEntity<>(comentario.get(), HttpStatus.OK);
		}
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}

	@PostMapping("/addComentario")
	public ResponseEntity<Object> addComentarioEndpoint(@RequestBody(required = false) ComentariosDTO comentarioDto) {
		return addComentario(comentarioDto != null ? comentarioDto.toEntity() : null);
	}

	public ResponseEntity<Object> addComentario(Comentarios comentario) {
		if (comentario == null || comentario.getId_tarea() == null) {
			return new ResponseEntity<>("Datos incompletos para el comentario", HttpStatus.BAD_REQUEST);
		}

		if (!tareastable.existsById(comentario.getId_tarea())) {
			return new ResponseEntity<>("La tarea referenciada no existe", HttpStatus.NOT_FOUND);
		}

		comentariostable.save(comentario);
		return new ResponseEntity<>(1, HttpStatus.OK);
	}

	@PostMapping("/updateComentario")
	public int updateComentarioEndpoint(@RequestBody(required = false) ComentariosDTO comentarioDto) {
		return updateComentario(comentarioDto != null ? comentarioDto.toEntity() : null);
	}

	public int updateComentario(Comentarios comentario) {
		int flag = 0;
		if (comentario != null) {
			comentariostable.save(comentario);
			flag = 1;
		}
		return flag;
	}

	@DeleteMapping("/deleteComentario")
	public int deleteComentarioEndpoint(@RequestBody(required = false) ComentariosDTO comentarioDto) {
		return deleteComentario(comentarioDto != null ? comentarioDto.toEntity() : null);
	}

	public int deleteComentario(Comentarios comentario) {
		int flag = 0;
		if (comentario != null) {
			comentariostable.delete(comentario);
			flag = 1;
		}
		return flag;
	}

	@PostMapping("/buscarComentarios")
	public List<Comentarios> getComentarios() {
		List<Comentarios> listaComentarios = new ArrayList<>();
		comentariostable.findAll().forEach(listaComentarios::add);
		return listaComentarios;
	}
}
