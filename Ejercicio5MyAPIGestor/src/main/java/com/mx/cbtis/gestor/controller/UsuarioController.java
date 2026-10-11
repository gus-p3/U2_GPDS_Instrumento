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

import com.mx.cbtis.gestor.modelo.Usuario;
import com.mx.cbtis.gestor.repository.UsuarioRepository;

@RestController
public class UsuarioController {
	
	@Autowired
	UsuarioRepository usuariotable;
	
	@PostMapping("/getUsuarioLogin")
	public ResponseEntity<Usuario> getUsuarioLogin(@RequestBody Usuario userParam) {
		if (userParam == null || userParam.getCorreo_electronico() == null || userParam.getContrasena() == null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
		Optional<Usuario> optionaluser = usuariotable.findByCorreoPass(
				userParam.getCorreo_electronico(),
				userParam.getContrasena());
		
		if(optionaluser.isPresent()) {
			return new ResponseEntity<>(optionaluser.get(), HttpStatus.OK);
		} else {
			Usuario fallback = new Usuario();
			fallback.setNombre("000");
			return new ResponseEntity<>(fallback, HttpStatus.UNAUTHORIZED);
		}
	}
	
	@GetMapping("/buscarUsuario")
	public ResponseEntity<Usuario> getUsuario(@RequestParam int id) {
		Optional<Usuario> usuario = usuariotable.findById(id);
		if(usuario.isPresent()) {
			return new ResponseEntity<>(usuario.get(), HttpStatus.OK);
		}
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}
	
	@PostMapping("/addUsuario")
	public ResponseEntity<Object> addUsuario(@RequestBody Usuario usuario) {
		if(usuario == null || 
		   usuario.getNombre() == null || usuario.getNombre().trim().isEmpty() ||
		   usuario.getCorreo_electronico() == null || !usuario.getCorreo_electronico().contains("@") ||
		   usuario.getContrasena() == null || usuario.getContrasena().trim().isEmpty()) {
			return new ResponseEntity<>("Datos inválidos o campos obligatorios faltantes", HttpStatus.BAD_REQUEST);
		}
		
		usuariotable.save(usuario);
		return new ResponseEntity<>(1, HttpStatus.OK);
	}
	
	@PostMapping("/updateUsuario")
	public int updateUsuario(@RequestBody Usuario usuario) {
		int flag = 0;
		if(!usuario.equals(null)) {
			usuariotable.save(usuario);
			flag = 1;
		}
		
		return flag;
	}
	
	@DeleteMapping("/deleteUsuario")
	public int deleteUsuario(@RequestBody Usuario usuario) {
		int flag = 0;
		if(!usuario.equals(null)) {
			usuariotable.delete(usuario);
			flag = 1;
		}
		
		return flag;
	}
	
	@PostMapping("/buscarUsuarios")
	public List<Usuario> getUsuarios(){
		List<Usuario> listaUsuarios = new ArrayList<>(); 
		usuariotable.findAll().forEach(listaUsuarios::add);
		return listaUsuarios;
	}		
}
