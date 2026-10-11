package com.mx.cbtis.gestor.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mx.cbtis.gestor.modelo.Comentarios;
import com.mx.cbtis.gestor.repository.ComentariosRepository;
import com.mx.cbtis.gestor.repository.TareasRepository;

@RestController
public class ComentariosController {
	
	@Autowired
	ComentariosRepository comentariostable;

	@Autowired
	TareasRepository tareastable;
	
	@PostMapping("/getComentario")

	public List<Comentarios> getComentario(@RequestBody Comentarios idComentario){
		List<Comentarios> listaComentarios = comentariostable.findByTarea(idComentario.getId_tarea());
		return listaComentarios;
	}
	
	@GetMapping("/buscarComentario")
	public ResponseEntity<Comentarios> getComentario(@RequestParam int id) {
		Optional<Comentarios> comentario = comentariostable.findById(id);
		if(comentario.isPresent()) {
			return new ResponseEntity<>(comentario.get(), HttpStatus.OK);
		}
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}
	
	@PostMapping("/addComentario")
	public ResponseEntity<Object> addComentario(@RequestBody Comentarios comentario) {
		if(comentario == null || comentario.getId_tarea() == null) {
			return new ResponseEntity<>("Datos incompletos para el comentario", HttpStatus.BAD_REQUEST);
		}
		
		if(!tareastable.existsById(comentario.getId_tarea())) {
			return new ResponseEntity<>("La tarea referenciada no existe", HttpStatus.NOT_FOUND);
		}
		
		comentariostable.save(comentario);
		return new ResponseEntity<>(1, HttpStatus.OK);
	}
	
	@PostMapping("/updateComentario")
	public int updateComentario(@RequestBody Comentarios comentario) {
		
		int flag = 0;
		if(!comentario.equals(null)) {
			comentariostable.save(comentario);
			flag = 1;
		}
		
		return flag;
	}
	
	@DeleteMapping("/deleteComentario")
	public int deleteComentario(@RequestBody Comentarios comentario) {
		
		int flag = 0;
		if(!comentario.equals(null)) {
			comentariostable.delete(comentario);
			flag = 1;
		}
		
		return flag;
	}
	
	@PostMapping("/buscarComentarios")
	public List<Comentarios> getComentarios(){
		
		List<Comentarios> listaComentarios = new ArrayList<>();
		comentariostable.findAll().forEach(listaComentarios :: add);
		return listaComentarios;
	}

}
