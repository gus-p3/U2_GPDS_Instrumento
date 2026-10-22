package com.mx.cbtis.gestor.controller;

import java.util.ArrayList;
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

import com.mx.cbtis.gestor.dto.ProyectosDTO;
import com.mx.cbtis.gestor.modelo.Proyectos;
import com.mx.cbtis.gestor.repository.ProtectosRepository;

@RestController
public class ProyectosController {

	private final ProtectosRepository proyectostable;

	public ProyectosController(ProtectosRepository proyectostable) {
		this.proyectostable = proyectostable;
	}

	@GetMapping("/buscarProyecto")
	public ResponseEntity<Proyectos> getProyecto(@RequestParam("id_proyecto") int idProyecto) {
		Optional<Proyectos> proyecto = proyectostable.findById(idProyecto);
		if (proyecto.isPresent()) {
			return new ResponseEntity<>(proyecto.get(), HttpStatus.OK);
		}
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}

	@PostMapping("/addProyecto")
	public int addProyectoEndpoint(@RequestBody(required = false) ProyectosDTO proyectoDto) {
		return addProyecto(proyectoDto != null ? proyectoDto.toEntity() : null);
	}

	public int addProyecto(Proyectos proyecto) {
		int flag = 0;
		if (proyecto != null) {
			proyectostable.save(proyecto);
			flag = 1;
		}
		return flag;
	}

	@PostMapping("/updateProyecto")
	public int updateProyectoEndpoint(@RequestBody(required = false) ProyectosDTO proyectoDto) {
		return updateProyecto(proyectoDto != null ? proyectoDto.toEntity() : null);
	}

	public int updateProyecto(Proyectos proyecto) {
		if (proyecto == null) {
			return 0;
		}
		proyectostable.save(proyecto);
		return 1;
	}

	@DeleteMapping("/deleteProyecto")
	public ResponseEntity<Object> deleteProyectoEndpoint(@RequestBody(required = false) ProyectosDTO proyectoDto) {
		return deleteProyecto(proyectoDto != null ? proyectoDto.toEntity() : null);
	}

	public ResponseEntity<Object> deleteProyecto(Proyectos proyecto) {
		if (proyecto == null || proyecto.getId_proyecto() == null || !proyectostable.existsById(proyecto.getId_proyecto())) {
			return new ResponseEntity<>("Proyecto no encontrado", HttpStatus.NOT_FOUND);
		}

		proyectostable.delete(proyecto);
		return new ResponseEntity<>(1, HttpStatus.OK);
	}

	@PostMapping("/buscarProyectos")
	public List<Proyectos> getProyectos() {
		List<Proyectos> listaProyectos = new ArrayList<>();
		proyectostable.findAll().forEach(listaProyectos::add);
		return listaProyectos;
	}
}
