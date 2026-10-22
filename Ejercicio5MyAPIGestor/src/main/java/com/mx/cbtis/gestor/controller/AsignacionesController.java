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

import com.mx.cbtis.gestor.dto.AsignacionesDTO;
import com.mx.cbtis.gestor.modelo.Asignaciones;
import com.mx.cbtis.gestor.repository.AsignacionesRepository;
import com.mx.cbtis.gestor.repository.TareasRepository;
import com.mx.cbtis.gestor.repository.UsuarioRepository;

@RestController
public class AsignacionesController {

	private final AsignacionesRepository asignacionestable;
	private final UsuarioRepository usuariotable;
	private final TareasRepository tareastable;

	public AsignacionesController(
			AsignacionesRepository asignacionestable,
			UsuarioRepository usuariotable,
			TareasRepository tareastable) {
		this.asignacionestable = asignacionestable;
		this.usuariotable = usuariotable;
		this.tareastable = tareastable;
	}

	@GetMapping("/buscarAsignacion")
	public ResponseEntity<Asignaciones> getAsignacion(@RequestParam int id) {
		Optional<Asignaciones> asignacion = asignacionestable.findById(id);
		if (asignacion.isPresent()) {
			return new ResponseEntity<>(asignacion.get(), HttpStatus.OK);
		}
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}

	@PostMapping("/addAsignacion")
	public ResponseEntity<Object> addAsignacionEndpoint(@RequestBody(required = false) AsignacionesDTO asignacionDto) {
		return addAsignacion(asignacionDto != null ? asignacionDto.toEntity() : null);
	}

	public ResponseEntity<Object> addAsignacion(Asignaciones asignacion) {
		if (asignacion == null || asignacion.getId_usuario() == null || asignacion.getId_tarea() == null) {
			return new ResponseEntity<>("Datos incompletos para la asignación", HttpStatus.BAD_REQUEST);
		}

		if (!usuariotable.existsById(asignacion.getId_usuario()) || !tareastable.existsById(asignacion.getId_tarea())) {
			return new ResponseEntity<>("El usuario o la tarea referenciada no existe", HttpStatus.NOT_FOUND);
		}

		asignacionestable.save(asignacion);
		return new ResponseEntity<>(1, HttpStatus.OK);
	}

	@PostMapping("/updateAsignacion")
	public int updateAsignacionEndpoint(@RequestBody(required = false) AsignacionesDTO asignacionDto) {
		return updateAsignacion(asignacionDto != null ? asignacionDto.toEntity() : null);
	}

	public int updateAsignacion(Asignaciones asignacion) {
		int flag = 0;
		if (asignacion != null) {
			asignacionestable.save(asignacion);
			flag = 1;
		}
		return flag;
	}

	@DeleteMapping("/deleteAsignacion")
	public int deleteAsignacionEndpoint(@RequestBody(required = false) AsignacionesDTO asignacionDto) {
		return deleteAsignacion(asignacionDto != null ? asignacionDto.toEntity() : null);
	}

	public int deleteAsignacion(Asignaciones asignacion) {
		int flag = 0;
		if (asignacion != null) {
			asignacionestable.delete(asignacion);
			flag = 1;
		}
		return flag;
	}

	@PostMapping("/buscarAsignaciones")
	public List<Asignaciones> getAsignaciones() {
		List<Asignaciones> listaAsignaciones = new ArrayList<>();
		asignacionestable.findAll().forEach(listaAsignaciones::add);
		return listaAsignaciones;
	}
}
