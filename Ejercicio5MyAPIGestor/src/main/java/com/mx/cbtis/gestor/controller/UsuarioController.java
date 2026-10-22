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

import com.mx.cbtis.gestor.dto.UsuarioDTO;
import com.mx.cbtis.gestor.modelo.Usuario;
import com.mx.cbtis.gestor.repository.UsuarioRepository;

@RestController
public class UsuarioController {

	private final UsuarioRepository usuariotable;

	public UsuarioController(UsuarioRepository usuariotable) {
		this.usuariotable = usuariotable;
	}

	@PostMapping("/getUsuarioLogin")
	public ResponseEntity<Usuario> getUsuarioLoginEndpoint(@RequestBody(required = false) UsuarioDTO userParamDto) {
		return getUsuarioLogin(userParamDto != null ? userParamDto.toEntity() : null);
	}

	public ResponseEntity<Usuario> getUsuarioLogin(Usuario userParam) {
		if (userParam == null || userParam.getCorreo_electronico() == null || userParam.getContrasena() == null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		Optional<Usuario> optionaluser = usuariotable.findByCorreoPass(
				userParam.getCorreo_electronico(),
				userParam.getContrasena());

		if (optionaluser.isPresent()) {
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
		if (usuario.isPresent()) {
			return new ResponseEntity<>(usuario.get(), HttpStatus.OK);
		}
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}

	@PostMapping("/addUsuario")
	public ResponseEntity<Object> addUsuarioEndpoint(@RequestBody(required = false) UsuarioDTO usuarioDto) {
		return addUsuario(usuarioDto != null ? usuarioDto.toEntity() : null);
	}

	public ResponseEntity<Object> addUsuario(Usuario usuario) {
		if (usuario == null ||
			usuario.getNombre() == null || usuario.getNombre().trim().isEmpty() ||
			usuario.getCorreo_electronico() == null || !usuario.getCorreo_electronico().contains("@") ||
			usuario.getContrasena() == null || usuario.getContrasena().trim().isEmpty()) {
			return new ResponseEntity<>("Datos inválidos o campos obligatorios faltantes", HttpStatus.BAD_REQUEST);
		}

		usuariotable.save(usuario);
		return new ResponseEntity<>(1, HttpStatus.OK);
	}

	@PostMapping("/updateUsuario")
	public int updateUsuarioEndpoint(@RequestBody(required = false) UsuarioDTO usuarioDto) {
		return updateUsuario(usuarioDto != null ? usuarioDto.toEntity() : null);
	}

	public int updateUsuario(Usuario usuario) {
		int flag = 0;
		if (usuario != null) {
			usuariotable.save(usuario);
			flag = 1;
		}
		return flag;
	}

	@DeleteMapping("/deleteUsuario")
	public int deleteUsuarioEndpoint(@RequestBody(required = false) UsuarioDTO usuarioDto) {
		return deleteUsuario(usuarioDto != null ? usuarioDto.toEntity() : null);
	}

	public int deleteUsuario(Usuario usuario) {
		int flag = 0;
		if (usuario != null) {
			usuariotable.delete(usuario);
			flag = 1;
		}
		return flag;
	}

	@PostMapping("/buscarUsuarios")
	public List<Usuario> getUsuarios() {
		List<Usuario> listaUsuarios = new ArrayList<>();
		usuariotable.findAll().forEach(listaUsuarios::add);
		return listaUsuarios;
	}
}
