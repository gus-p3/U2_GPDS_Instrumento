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

import com.mx.cbtis.gestor.modelo.Asignaciones;
import com.mx.cbtis.gestor.repository.AsignacionesRepository;
import com.mx.cbtis.gestor.repository.TareasRepository;
import com.mx.cbtis.gestor.repository.UsuarioRepository;

@RestController
public class AsignacionesController {
	
	@Autowired
	AsignacionesRepository asignacionestable;

	@Autowired
	UsuarioRepository usuariotable;

	@Autowired
	TareasRepository tareastable;
	
	@GetMapping("/buscarAsignacion")
	public ResponseEntity<Asignaciones> getAsignacion(@RequestParam int id) {
		Optional<Asignaciones> asignacion = asignacionestable.findById(id);
		if(asignacion.isPresent()) {
			return new ResponseEntity<>(asignacion.get(), HttpStatus.OK);
		}
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}
	
	@PostMapping("/addAsignacion")
	public ResponseEntity<Object> addAsignacion(@RequestBody Asignaciones asignacion) {
		if(asignacion == null || asignacion.getId_usuario() == null || asignacion.getId_tarea() == null) {
			return new ResponseEntity<>("Datos incompletos para la asignación", HttpStatus.BAD_REQUEST);
		}
		
		if(!usuariotable.existsById(asignacion.getId_usuario()) || !tareastable.existsById(asignacion.getId_tarea())) {
			return new ResponseEntity<>("El usuario o la tarea referenciada no existe", HttpStatus.NOT_FOUND);
		}
		
		asignacionestable.save(asignacion);
		return new ResponseEntity<>(1, HttpStatus.OK);
	}
	
	@PostMapping("/updateAsignacion")
	public int updateAsignacion(@RequestBody Asignaciones asignacion) {
		
		int flag = 0;
		if(!asignacion.equals(null)) {
			asignacionestable.save(asignacion);
			flag = 1;
		}
		
		return flag;
	}
	
	@DeleteMapping("/deleteAsignacion")
	public int deleteAsignacion(@RequestBody Asignaciones asignacion) {
		
		int flag = 0;
		if(!asignacion.equals(null)) {
			asignacionestable.delete(asignacion);
			flag = 1;
		}
		
		return flag;
	}   
	
	@PostMapping("/buscarAsignaciones")
	public List<Asignaciones> getAsignaciones(){
		
		List<Asignaciones> listaAsignaciones = new ArrayList<>();
		asignacionestable.findAll().forEach(listaAsignaciones :: add);
		return listaAsignaciones;
	}
}
