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

import com.mx.cbtis.gestor.modelo.Tareas;
import com.mx.cbtis.gestor.repository.ProtectosRepository;
import com.mx.cbtis.gestor.repository.TareasRepository;

@RestController
public class TareasController {
	
	@Autowired
	TareasRepository tareastable;

	@Autowired
	ProtectosRepository proyectostable;
	
	@PostMapping("/getTarea")
	public List<Tareas> getTarea(@RequestBody Tareas id_proyecto) {
		List<Tareas> listaTareas = tareastable.findByIdProyecto(id_proyecto.getId_proyecto());
		return listaTareas;
	}
	
	@GetMapping("/buscarTarea")
	public ResponseEntity<Tareas> getTarea(@RequestParam int id_tarea) {
		Optional<Tareas> tarea = tareastable.findById(id_tarea);
		if(tarea.isPresent()) {
			return new ResponseEntity<>(tarea.get(), HttpStatus.OK);
		}
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}
	
	@PostMapping("/addTareas")
	public ResponseEntity<Object> addTareas(@RequestBody Tareas tareas) {
		if(tareas == null || tareas.getNombre() == null || tareas.getNombre().trim().isEmpty() || tareas.getId_proyecto() == null) {
			return new ResponseEntity<>("Datos incompletos para la tarea", HttpStatus.BAD_REQUEST);
		}
		
		if(!proyectostable.existsById(tareas.getId_proyecto())) {
			return new ResponseEntity<>("El proyecto referenciado no existe", HttpStatus.NOT_FOUND);
		}
		
		tareastable.save(tareas);
		return new ResponseEntity<>(1, HttpStatus.OK);
	}
	
	@PostMapping("/updateTareas")
	public int updateTareas(@RequestBody Tareas tareas) {
		
		int flag = 0;
		if(!tareas.equals(null)) {
			tareastable.save(tareas);
			flag = 1;
		}
		
		return flag;
	}
	
	@DeleteMapping("/deleteTareas")
	public ResponseEntity<Object> deleteTareas(@RequestBody Tareas tareas) {
		if(tareas == null || tareas.getId_tarea() == null || !tareastable.existsById(tareas.getId_tarea())) {
			return new ResponseEntity<>("La tarea a eliminar no existe", HttpStatus.NOT_FOUND);
		}
		
		tareastable.delete(tareas);
		return new ResponseEntity<>(1, HttpStatus.OK);
	}
	
	@PostMapping("/buscarTareas")
	public List<Tareas> getTareas(){
		
		List<Tareas> listaTareas = new ArrayList<>();
		tareastable.findAll().forEach(listaTareas :: add);
		return listaTareas;
	}

}
