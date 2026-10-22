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

import com.mx.cbtis.gestor.dto.TareasDTO;
import com.mx.cbtis.gestor.modelo.Tareas;
import com.mx.cbtis.gestor.repository.ProtectosRepository;
import com.mx.cbtis.gestor.repository.TareasRepository;

@RestController
public class TareasController {

	private final TareasRepository tareastable;
	private final ProtectosRepository proyectostable;

	public TareasController(TareasRepository tareastable, ProtectosRepository proyectostable) {
		this.tareastable = tareastable;
		this.proyectostable = proyectostable;
	}

	@PostMapping("/getTarea")
	public List<Tareas> getTareaEndpoint(@RequestBody(required = false) TareasDTO tareaProyectoDto) {
		return getTarea(tareaProyectoDto != null ? tareaProyectoDto.toEntity() : null);
	}

	public List<Tareas> getTarea(Tareas tareaProyecto) {
		if (tareaProyecto == null || tareaProyecto.getId_proyecto() == null) {
			return Collections.emptyList();
		}
		return tareastable.findByIdProyecto(tareaProyecto.getId_proyecto());
	}

	@GetMapping("/buscarTarea")
	public ResponseEntity<Tareas> getTarea(@RequestParam("id_tarea") int idTarea) {
		Optional<Tareas> tarea = tareastable.findById(idTarea);
		if (tarea.isPresent()) {
			return new ResponseEntity<>(tarea.get(), HttpStatus.OK);
		}
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}

	@PostMapping("/addTareas")
	public ResponseEntity<Object> addTareasEndpoint(@RequestBody(required = false) TareasDTO tareasDto) {
		return addTareas(tareasDto != null ? tareasDto.toEntity() : null);
	}

	public ResponseEntity<Object> addTareas(Tareas tareas) {
		if (tareas == null || tareas.getNombre() == null || tareas.getNombre().trim().isEmpty() || tareas.getId_proyecto() == null) {
			return new ResponseEntity<>("Datos incompletos para la tarea", HttpStatus.BAD_REQUEST);
		}

		if (!proyectostable.existsById(tareas.getId_proyecto())) {
			return new ResponseEntity<>("El proyecto referenciado no existe", HttpStatus.NOT_FOUND);
		}

		tareastable.save(tareas);
		return new ResponseEntity<>(1, HttpStatus.OK);
	}

	@PostMapping("/updateTareas")
	public int updateTareasEndpoint(@RequestBody(required = false) TareasDTO tareasDto) {
		return updateTareas(tareasDto != null ? tareasDto.toEntity() : null);
	}

	public int updateTareas(Tareas tareas) {
		int flag = 0;
		if (tareas != null) {
			tareastable.save(tareas);
			flag = 1;
		}
		return flag;
	}

	@DeleteMapping("/deleteTareas")
	public ResponseEntity<Object> deleteTareasEndpoint(@RequestBody(required = false) TareasDTO tareasDto) {
		return deleteTareas(tareasDto != null ? tareasDto.toEntity() : null);
	}

	public ResponseEntity<Object> deleteTareas(Tareas tareas) {
		if (tareas == null || tareas.getId_tarea() == null || !tareastable.existsById(tareas.getId_tarea())) {
			return new ResponseEntity<>("La tarea a eliminar no existe", HttpStatus.NOT_FOUND);
		}

		tareastable.delete(tareas);
		return new ResponseEntity<>(1, HttpStatus.OK);
	}

	@PostMapping("/buscarTareas")
	public List<Tareas> getTareas() {
		List<Tareas> listaTareas = new ArrayList<>();
		tareastable.findAll().forEach(listaTareas::add);
		return listaTareas;
	}
}
