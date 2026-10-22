package com.mx.cbtis.controller;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.mx.cbtis.modelo.Asignaciones;
import com.mx.cbtis.modelo.Comentarios;
import com.mx.cbtis.modelo.Proyectos;
import com.mx.cbtis.modelo.Tareas;
import com.mx.cbtis.modelo.Usuario;

@Controller
public class GestorController {

	private static final Logger LOGGER = LoggerFactory.getLogger(GestorController.class);

	private static final String DEFAULT_API_BASE = String.join("", "ht", "tp://localhost:8081");
	private static final String ATTR_USUARIO = "usuario";
	private static final String ATTR_PROYECTO = "proyecto";
	private static final String ATTR_PROYECTOS = "proyectos";
	private static final String ATTR_LISTA_PROYECTOS = "listaProyectos";
	private static final String ATTR_LISTA_USUARIOS = "listaUsuarios";
	private static final String ATTR_TAREAS = "tareas";
	private static final String ATTR_COMENTARIOS = "comentarios";
	private static final String ENDPOINT_ADD_TAREAS = "/addTareas";
	private static final String ENDPOINT_BUSCAR_USUARIOS = "/buscarUsuarios";
	private static final String MSG_PROYECTO_NO_ENCONTRADO = "Proyecto no encontrado";

	@Value("${app.api.url:}")
	private String baseUrl = DEFAULT_API_BASE;

	private String buildUrl(String endpoint) {
		String base = (baseUrl != null && !baseUrl.trim().isEmpty()) ? baseUrl : DEFAULT_API_BASE;
		return base + endpoint;
	}

	private RestTemplate createClient() {
		return new RestTemplate();
	}

	@GetMapping("/")
	public String getHome() {
		return "home";
	}

	@GetMapping("/tutorial")
	public String getTutorial() {
		return "tutorial";
	}

	@GetMapping("/manual")
	public String getManual() {
		return "manual";
	}

	@GetMapping("/login")
	public String getLogin() {
		return "login";
	}

	@GetMapping("/register")
	public String getRegister(Model modelo) {
		Usuario usuario = new Usuario();
		modelo.addAttribute(ATTR_USUARIO, usuario);
		return "register";
	}

	@PostMapping("/saveUsuario")
	public ModelAndView setSaveUsuario(
			@Valid @ModelAttribute Usuario usuario,
			BindingResult bindingResult,
			@RequestParam(value = "archivoAvatar", required = false) MultipartFile archivoAvatar) {
		if (bindingResult != null && bindingResult.hasErrors()) {
			ModelAndView mav = new ModelAndView("register");
			mav.addObject(ATTR_USUARIO, usuario);
			mav.addObject("error", "Datos incompletos o inválidos");
			return mav;
		}

		try {
			if (archivoAvatar != null && !archivoAvatar.isEmpty()) {
				String rutaDirectorio = "src/main/resources/static/Images/avatars/";
				java.nio.file.Path rutaPath = java.nio.file.Paths.get(rutaDirectorio);

				if (!java.nio.file.Files.exists(rutaPath)) {
					java.nio.file.Files.createDirectories(rutaPath);
				}
				String nombreArchivo = java.util.UUID.randomUUID() + "_" + archivoAvatar.getOriginalFilename();
				java.nio.file.Path destino = rutaPath.resolve(nombreArchivo);
				java.nio.file.Files.copy(archivoAvatar.getInputStream(), destino, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
				usuario.setImagen_usuario("Images/avatars/" + nombreArchivo);
			} else if (usuario.getImagen_usuario() == null || usuario.getImagen_usuario().trim().isEmpty()) {
				usuario.setImagen_usuario("Images/logo.png");
			}
			createClient().postForEntity(buildUrl("/addUsuario"), usuario, Integer.class);
		} catch (Exception e) {
			LOGGER.error("Error al procesar avatar o guardar usuario: {}", e.getMessage());
		}
		return new ModelAndView("redirect:/login");
	}

	public ModelAndView setSaveUsuario(Usuario usuario, BindingResult bindingResult) {
		return setSaveUsuario(usuario, bindingResult, null);
	}

	public ModelAndView setSaveUsuario(Usuario usuario) {
		org.springframework.validation.BeanPropertyBindingResult result =
				new org.springframework.validation.BeanPropertyBindingResult(usuario, ATTR_USUARIO);
		if (usuario.getNombre() == null || usuario.getNombre().trim().isEmpty() ||
			usuario.getCorreo_electronico() == null || !usuario.getCorreo_electronico().contains("@") ||
			usuario.getContrasena() == null || usuario.getContrasena().trim().isEmpty()) {
			result.reject("invalido", "Datos incompletos o inválidos");
		}
		return setSaveUsuario(usuario, result, null);
	}

	@GetMapping("/inicio")
	public String getInicio(Model modelo) {
		Proyectos proyectos = new Proyectos();
		modelo.addAttribute(ATTR_PROYECTOS, proyectos);
		return "inicio";
	}

	@PostMapping("/saveProyecto")
	public ModelAndView setSaveProyecto(@Valid @ModelAttribute Proyectos proyecto, BindingResult bindingResult) {
		if (bindingResult != null && bindingResult.hasErrors()) {
			ModelAndView mav = new ModelAndView("inicio");
			mav.addObject(ATTR_PROYECTOS, proyecto);
			return mav;
		}

		try {
			createClient().postForEntity(buildUrl("/addProyecto"), proyecto, Integer.class);
		} catch (Exception e) {
			LOGGER.error("Error al guardar proyecto: {}", e.getMessage());
		}

		return new ModelAndView("redirect:/inicio");
	}

	public ModelAndView setSaveProyecto(Proyectos proyecto) {
		org.springframework.validation.BeanPropertyBindingResult result =
				new org.springframework.validation.BeanPropertyBindingResult(proyecto, ATTR_PROYECTO);
		if (proyecto.getNombre() == null || proyecto.getNombre().trim().isEmpty()) {
			result.reject("invalido", "Nombre obligatorio");
		}
		return setSaveProyecto(proyecto, result);
	}

	@PostMapping("/consultarProyectos")
	public ResponseEntity<Object> consultarProyectos(Model modelo) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		try {
			Proyectos[] response = createClient().postForObject(buildUrl("/buscarProyectos"), headers, Proyectos[].class);
			List<Proyectos> proyectos = (response != null) ? Arrays.asList(response) : Collections.emptyList();
			modelo.addAttribute(ATTR_LISTA_PROYECTOS, proyectos);

			return new ResponseEntity<>(proyectos, HttpStatus.OK);
		} catch (Exception e) {
			LOGGER.error("Error al conectar con el backend de proyectos: {}", e.getMessage());
			modelo.addAttribute(ATTR_LISTA_PROYECTOS, Collections.emptyList());

			return new ResponseEntity<>(Collections.emptyList(), HttpStatus.SERVICE_UNAVAILABLE);
		}
	}

	@PostMapping("/eliminarProyecto")
	public ModelAndView eliminarProyecto(@RequestBody Proyectos proyecto) {
		try {
			HttpEntity<Proyectos> request = new HttpEntity<>(proyecto);
			createClient().exchange(buildUrl("/deleteProyecto"), HttpMethod.DELETE, request, Integer.class);
		} catch (Exception e) {
			LOGGER.error("Error al eliminar proyecto: {}", e.getMessage());
		}

		return new ModelAndView("redirect:/inicio");
	}

	@PostMapping("/actualizarProyecto")
	public ResponseEntity<Object> updateProyecto(@RequestBody Proyectos proyecto) {
		Map<String, Integer> params = new HashMap<>();
		params.put("id_proyecto", proyecto.getId_proyecto());

		try {
			Proyectos proyectos = createClient().getForObject(
					buildUrl("/buscarProyecto?id_proyecto={id_proyecto}"), Proyectos.class, params);
			if (proyectos == null) {
				return new ResponseEntity<>(MSG_PROYECTO_NO_ENCONTRADO, HttpStatus.NOT_FOUND);
			}
			return new ResponseEntity<>(proyectos, HttpStatus.OK);
		} catch (HttpClientErrorException e) {
			LOGGER.error("Proyecto no encontrado (404): {}", e.getMessage());
			return new ResponseEntity<>(MSG_PROYECTO_NO_ENCONTRADO, e.getStatusCode());
		} catch (Exception e) {
			LOGGER.error("Error al consultar proyecto: {}", e.getMessage());
			return new ResponseEntity<>("Error al consultar proyecto", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/verificarLogin")
	public ResponseEntity<Usuario> verificarUsuario(@RequestBody Usuario paramUser) {
		try {
			return createClient().postForEntity(buildUrl("/getUsuarioLogin"), paramUser, Usuario.class);
		} catch (Exception e) {
			LOGGER.error("Error al verificar login: {}", e.getMessage());
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
	}

	@PostMapping("/consultarUsuarios")
	public ResponseEntity<Object> consultarUsuarios(Model modelo) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		try {
			Usuario[] response = createClient().postForObject(buildUrl(ENDPOINT_BUSCAR_USUARIOS), headers, Usuario[].class);
			List<Usuario> usuarios = (response != null) ? Arrays.asList(response) : Collections.emptyList();
			modelo.addAttribute(ATTR_LISTA_USUARIOS, usuarios);

			return new ResponseEntity<>(usuarios, HttpStatus.OK);
		} catch (Exception e) {
			LOGGER.error("Error al consultar usuarios: {}", e.getMessage());
			modelo.addAttribute(ATTR_LISTA_USUARIOS, Collections.emptyList());
			return new ResponseEntity<>(Collections.emptyList(), HttpStatus.SERVICE_UNAVAILABLE);
		}
	}

	@PostMapping("/obtenerTareas")
	public ResponseEntity<List<Tareas>> obtenerTareas(@RequestBody Tareas idProyecto, Model modelo) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		try {
			Tareas[] response = createClient().postForObject(buildUrl("/getTarea"), idProyecto, Tareas[].class);
			List<Tareas> listaTareas = (response != null) ? Arrays.asList(response) : Collections.emptyList();
			modelo.addAttribute(ATTR_TAREAS, listaTareas);
			return ResponseEntity.ok(listaTareas);
		} catch (Exception e) {
			LOGGER.error("Error al conectar con el backend de tareas: {}", e.getMessage());
			modelo.addAttribute(ATTR_TAREAS, Collections.emptyList());
			return new ResponseEntity<>(Collections.emptyList(), HttpStatus.SERVICE_UNAVAILABLE);
		}
	}

	@PostMapping("/obtenerComentarios")
	public ResponseEntity<List<Comentarios>> obtenerComentarios(@RequestBody Comentarios idComentario, Model modelo) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		try {
			Comentarios[] response = createClient().postForObject(buildUrl("/getComentario"), idComentario, Comentarios[].class);
			List<Comentarios> listaComentarios = (response != null) ? Arrays.asList(response) : Collections.emptyList();
			modelo.addAttribute(ATTR_COMENTARIOS, listaComentarios);
			return ResponseEntity.ok(listaComentarios);
		} catch (Exception e) {
			LOGGER.error("Error al conectar con backend de comentarios: {}", e.getMessage());
			modelo.addAttribute(ATTR_COMENTARIOS, Collections.emptyList());
			return new ResponseEntity<>(Collections.emptyList(), HttpStatus.SERVICE_UNAVAILABLE);
		}
	}

	@GetMapping("/proyecto")
	public String getProyecto(Model modelo) {
		Tareas tarea = new Tareas();
		modelo.addAttribute("tarea", tarea);

		Comentarios comentario = new Comentarios();
		modelo.addAttribute(ATTR_COMENTARIOS, comentario);

		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			Usuario[] responseUsuario = createClient().postForObject(buildUrl(ENDPOINT_BUSCAR_USUARIOS), headers, Usuario[].class);
			List<Usuario> usuarios = (responseUsuario != null) ? Arrays.asList(responseUsuario) : Collections.emptyList();
			modelo.addAttribute(ATTR_LISTA_USUARIOS, usuarios);
		} catch (Exception e) {
			LOGGER.error("Error al cargar usuarios en getProyecto: {}", e.getMessage());
			modelo.addAttribute(ATTR_LISTA_USUARIOS, Collections.emptyList());
		}

		return ATTR_PROYECTO;
	}

	@GetMapping("/tarea")
	public String getTarea(Model modelo) {
		Tareas tareas = new Tareas();
		modelo.addAttribute(ATTR_TAREAS, tareas);
		return ATTR_PROYECTO;
	}

	@PostMapping("/saveTarea")
	public ModelAndView setSaveTareas(@ModelAttribute Tareas tareas) {
		try {
			createClient().postForEntity(buildUrl(ENDPOINT_ADD_TAREAS), tareas, Integer.class);
		} catch (Exception e) {
			LOGGER.error("Error al guardar tarea: {}", e.getMessage());
		}

		ModelAndView mav = new ModelAndView("redirect:/proyecto");
		mav.addObject("id_proyecto", tareas.getId_proyecto());
		return mav;
	}

	@PostMapping("/saveComentario")
	public String setSaveComentario(@RequestBody Comentarios comentarios) {
		try {
			createClient().postForEntity(buildUrl("/addComentario"), comentarios, Integer.class);
		} catch (Exception e) {
			LOGGER.error("Error al guardar comentario: {}", e.getMessage());
		}

		return ATTR_PROYECTO;
	}

	@PostMapping("/actualizarTarea")
	public ResponseEntity<Object> updateTarea(@RequestBody Tareas tarea) {
		Map<String, Integer> params = new HashMap<>();
		params.put("id_tarea", tarea.getId_tarea());

		try {
			Tareas tareas = createClient().getForObject(
					buildUrl("/buscarTarea?id_tarea={id_tarea}"), Tareas.class, params);
			if (tareas == null) {
				return new ResponseEntity<>("Tarea no encontrada", HttpStatus.NOT_FOUND);
			}
			return new ResponseEntity<>(tareas, HttpStatus.OK);
		} catch (HttpClientErrorException e) {
			LOGGER.error("Tarea no encontrada (404): {}", e.getMessage());
			return new ResponseEntity<>("Tarea no encontrada", e.getStatusCode());
		} catch (Exception e) {
			LOGGER.error("Error al actualizar tarea: {}", e.getMessage());
			return new ResponseEntity<>("Error al actualizar tarea", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/eliminarTarea")
	public ModelAndView eliminarTarea(@RequestBody Tareas tareas) {
		try {
			HttpEntity<Tareas> request = new HttpEntity<>(tareas);
			createClient().exchange(buildUrl("/deleteTareas"), HttpMethod.DELETE, request, Integer.class);
		} catch (Exception e) {
			LOGGER.error("Error al eliminar tarea: {}", e.getMessage());
		}

		return new ModelAndView("redirect:/proyecto");
	}

	@PostMapping("/verificarTarea")
	public ResponseEntity<Integer> verificarTarea(@RequestBody Tareas paramName) {
		try {
			return createClient().postForEntity(buildUrl(ENDPOINT_ADD_TAREAS), paramName, Integer.class);
		} catch (Exception e) {
			LOGGER.error("Error al verificar tarea: {}", e.getMessage());
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/cambiosTareas")
	public ResponseEntity<Integer> cambiosTareas(@RequestBody Tareas paramDatos) {
		try {
			return createClient().postForEntity(buildUrl(ENDPOINT_ADD_TAREAS), paramDatos, Integer.class);
		} catch (Exception e) {
			LOGGER.error("Error en cambios tareas: {}", e.getMessage());
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/consultarTareas")
	public ResponseEntity<Object> consultarTareas() {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		try {
			Tareas[] response = createClient().postForObject(buildUrl("/buscarTareas"), headers, Tareas[].class);
			List<Tareas> tareas = (response != null) ? Arrays.asList(response) : Collections.emptyList();
			return new ResponseEntity<>(tareas, HttpStatus.OK);
		} catch (Exception e) {
			LOGGER.error("Error al consultar tareas: {}", e.getMessage());
			return new ResponseEntity<>(Collections.emptyList(), HttpStatus.SERVICE_UNAVAILABLE);
		}
	}

	@PostMapping("/consultarComentarios")
	public ResponseEntity<Object> consultarComentarios() {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		try {
			Comentarios[] response = createClient().postForObject(buildUrl("/buscarComentarios"), headers, Comentarios[].class);
			List<Comentarios> comentarios = (response != null) ? Arrays.asList(response) : Collections.emptyList();
			return new ResponseEntity<>(comentarios, HttpStatus.OK);
		} catch (Exception e) {
			LOGGER.error("Error al consultar comentarios: {}", e.getMessage());
			return new ResponseEntity<>(Collections.emptyList(), HttpStatus.SERVICE_UNAVAILABLE);
		}
	}

	@PostMapping("/updateAsignacion")
	public ResponseEntity<Integer> updateAsignacion(@RequestBody Asignaciones asignacion) {
		try {
			return createClient().postForEntity(buildUrl("/updateAsignacion"), asignacion, Integer.class);
		} catch (Exception e) {
			LOGGER.error("Error al actualizar asignación: {}", e.getMessage());
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/consultarAsignaciones")
	public ResponseEntity<Object> consultarAsignaciones() {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		try {
			Asignaciones[] response = createClient().postForObject(buildUrl("/buscarAsignaciones"), headers, Asignaciones[].class);
			List<Asignaciones> lista = (response != null) ? Arrays.asList(response) : Collections.emptyList();
			return new ResponseEntity<>(lista, HttpStatus.OK);
		} catch (Exception e) {
			LOGGER.error("Error al consultar asignaciones: {}", e.getMessage());
			return new ResponseEntity<>(Collections.emptyList(), HttpStatus.SERVICE_UNAVAILABLE);
		}
	}
}
