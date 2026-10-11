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

import com.mx.cbtis.gestor.modelo.Proyectos;
import com.mx.cbtis.gestor.repository.ProtectosRepository;

@RestController
public class ProyectosController {
	
	@Autowired 
	ProtectosRepository proyectostable;
	
	/*@PostMapping("/buscarProyecto")
	public Proyectos getProyecto(@RequestBody Proyectos proyecto) {
		
		Proyectos buscar = new Proyectos();
		Optional<Proyectos> proyectos = proyectostable.findById(proyecto.getId_proyecto());
		if(proyectos.isPresent()) {
			buscar = proyectos.get();
		}
		
		return buscar;
	}*/
	
	@GetMapping("/buscarProyecto")
	public ResponseEntity<Proyectos> getProyecto(@RequestParam int id_proyecto) {
		Optional<Proyectos> proyecto = proyectostable.findById(id_proyecto);
		if(proyecto.isPresent()) {
			return new ResponseEntity<>(proyecto.get(), HttpStatus.OK);
		}
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}
	
	@PostMapping("/addProyecto")
	public int addProyecto(@RequestBody Proyectos proyecto) {
		
		int flag = 0;
		if(!proyecto.equals(null)) {
			
			proyectostable.save(proyecto);
			flag = 1;
		}
		
		return flag;
	}
	
	@PostMapping("/updateProyecto")
	public int updateProyecto(@RequestBody Proyectos proyecto) {
		
		int flag = 0;
		if(!proyecto.equals(null)) {
			proyectostable.save(proyecto);
			flag = 1;
		}
		
		return flag;
	}
	
	@DeleteMapping("/deleteProyecto")
	public ResponseEntity<Object> deleteProyecto(@RequestBody Proyectos proyecto) {
		if (proyecto == null || proyecto.getId_proyecto() == null || !proyectostable.existsById(proyecto.getId_proyecto())) {
			return new ResponseEntity<>("Proyecto no encontrado", HttpStatus.NOT_FOUND);
		}
		
		proyectostable.delete(proyecto);
		return new ResponseEntity<>(1, HttpStatus.OK);
	}   
	
	@PostMapping("/buscarProyectos")
	public List<Proyectos> getProyectos(){
		
		List<Proyectos> listaProyectos = new ArrayList<>();
		proyectostable.findAll().forEach(listaProyectos :: add);
		return listaProyectos;
	}

}
