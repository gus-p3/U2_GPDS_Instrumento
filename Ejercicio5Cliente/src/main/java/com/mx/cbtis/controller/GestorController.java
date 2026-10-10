package com.mx.cbtis.controller;

import java.util.*;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
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
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mx.cbtis.modelo.Comentarios;
import com.mx.cbtis.modelo.Proyectos;
import com.mx.cbtis.modelo.Tareas;
import com.mx.cbtis.modelo.Usuario;

@Controller
public class GestorController {
	@Value("${app.api.url:http://localhost:8081}")
	private String baseUrl = "http://localhost:8081";
	
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
		modelo.addAttribute("usuario", usuario);
		return "register";
	}

	@PostMapping("/saveUsuario")
	public ModelAndView setSaveUsuario(@Valid @ModelAttribute Usuario usuario, BindingResult bindingResult) {
		if (bindingResult != null && bindingResult.hasErrors()) {
			ModelAndView mav = new ModelAndView("register");
			mav.addObject("usuario", usuario);
			mav.addObject("error", "Datos incompletos o inválidos");
			return mav;
		}

		try {
			RestTemplate template = new RestTemplate();
			String urlservicebd = (baseUrl != null ? baseUrl : "http://localhost:8081") + "/addUsuario";
			template.postForEntity(urlservicebd, usuario, Integer.class);
		} catch (Exception e) {
			System.err.println("Error al registrar usuario: " + e.getMessage());
		}

		return new ModelAndView("redirect:/login");
	}

	public ModelAndView setSaveUsuario(Usuario usuario) {
		org.springframework.validation.BeanPropertyBindingResult result = 
				new org.springframework.validation.BeanPropertyBindingResult(usuario, "usuario");
		if (usuario.getNombre() == null || usuario.getNombre().trim().isEmpty() ||
		    usuario.getCorreo_electronico() == null || !usuario.getCorreo_electronico().contains("@") ||
		    usuario.getContrasena() == null || usuario.getContrasena().trim().isEmpty()) {
			result.reject("invalido", "Datos incompletos o inválidos");
		}
		return setSaveUsuario(usuario, result);
	}
	
	@GetMapping("/inicio")
	public String getInicio(Model modelo) {
		Proyectos proyectos = new Proyectos();
		modelo.addAttribute("proyectos", proyectos);
		return "inicio";
	}

	@PostMapping("/saveProyecto")
	public ModelAndView setSaveProyecto(@Valid @ModelAttribute Proyectos proyecto, BindingResult bindingResult) {
		if (bindingResult != null && bindingResult.hasErrors()) {
			ModelAndView mav = new ModelAndView("inicio");
			mav.addObject("proyectos", proyecto);
			return mav;
		}

		try {
			RestTemplate template = new RestTemplate();
			String urlservicebd = (baseUrl != null ? baseUrl : "http://localhost:8081") + "/addProyecto";
			template.postForEntity(urlservicebd, proyecto, Integer.class);
		} catch (Exception e) {
			System.err.println("Error al guardar proyecto: " + e.getMessage());
		}

		return new ModelAndView("redirect:/inicio");
	}

	public ModelAndView setSaveProyecto(Proyectos proyecto) {
		org.springframework.validation.BeanPropertyBindingResult result = 
				new org.springframework.validation.BeanPropertyBindingResult(proyecto, "proyecto");
		if (proyecto.getNombre() == null || proyecto.getNombre().trim().isEmpty()) {
			result.reject("invalido", "Nombre obligatorio");
		}
		return setSaveProyecto(proyecto, result);
	}

	@PostMapping("/consultarProyectos")
	public ResponseEntity<Object> consultarProyectos(Model modelo) {
		RestTemplate template = new RestTemplate();
		String urlservicebd = (baseUrl != null ? baseUrl : "http://localhost:8081") + "/buscarProyectos";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		try {
			Proyectos[] response = template.postForObject(urlservicebd, headers, Proyectos[].class);
			List<Proyectos> proyectos = (response != null) ? Arrays.asList(response) : Collections.emptyList();
			modelo.addAttribute("listaProyectos", proyectos);

			return new ResponseEntity<>(proyectos, HttpStatus.OK);
		} catch (Exception e) {
			System.err.println("Error al conectar con el backend de proyectos: " + e.getMessage());
			modelo.addAttribute("listaProyectos", Collections.emptyList());

			return new ResponseEntity<>(Collections.emptyList(), HttpStatus.SERVICE_UNAVAILABLE);
		}
	}

	@PostMapping("/eliminarProyecto")
	public ModelAndView eliminarProyecto(@RequestBody Proyectos proyecto) {
		try {
			RestTemplate template = new RestTemplate();
			String urlservicebd = (baseUrl != null ? baseUrl : "http://localhost:8081") + "/deleteProyecto";
			HttpEntity<Proyectos> request = new HttpEntity<Proyectos>(proyecto); 
			template.exchange(urlservicebd, HttpMethod.DELETE, request, Integer.class);
		} catch (Exception e) {
			System.err.println("Error al eliminar proyecto: " + e.getMessage());
		}
	
		return new ModelAndView("redirect:/inicio");
	}
	
	@PostMapping ("/actualizarProyecto")
	public ResponseEntity<Object> updateProyecto(@RequestBody Proyectos proyecto) {
		RestTemplate template = new RestTemplate();
		Map<String,Integer> params = new HashMap<String,Integer>();
		params.put("id_proyecto", proyecto.getId_proyecto());
		String urlservicebd = (baseUrl != null ? baseUrl : "http://localhost:8081") + "/buscarProyecto?id_proyecto={id_proyecto}";
		
		try {
			Proyectos proyectos = template.getForObject(urlservicebd, Proyectos.class, params);
			if (proyectos == null) {
				return new ResponseEntity<>("Proyecto no encontrado", HttpStatus.NOT_FOUND);
			}
			return new ResponseEntity<Object>(proyectos, HttpStatus.OK);
		} catch (HttpClientErrorException e) {
			System.err.println("Proyecto no encontrado (404): " + e.getMessage());
			return new ResponseEntity<>("Proyecto no encontrado", e.getStatusCode());
		} catch (Exception e) {
			System.err.println("Error al consultar proyecto: " + e.getMessage());
			return new ResponseEntity<>("Error al consultar proyecto", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PostMapping("/verificarLogin")
	public ResponseEntity<Usuario> verificarUsuario(@RequestBody Usuario paramUser) {
		try {
			String urlservicebd = (baseUrl != null ? baseUrl : "http://localhost:8081") + "/getUsuarioLogin";
			RestTemplate template = new RestTemplate();
			return template.postForEntity(urlservicebd, paramUser, Usuario.class);
		} catch (Exception e) {
			System.err.println("Error al verificar login: " + e.getMessage());
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
	}
	
	@PostMapping("/consultarUsuarios")
	public ResponseEntity<Object> consultarUsuarios(Model modelo) {
		RestTemplate template = new RestTemplate();
		String urlservicebd = (baseUrl != null ? baseUrl : "http://localhost:8081") + "/buscarUsuarios";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		try {
			Usuario[] response = template.postForObject(urlservicebd, headers, Usuario[].class);
			List<Usuario> usuarios = (response != null) ? Arrays.asList(response) : Collections.emptyList();
			modelo.addAttribute("listaUsuarios", usuarios);

			return new ResponseEntity<Object>(usuarios, HttpStatus.OK);
		} catch (Exception e) {
			System.err.println("Error al consultar usuarios: " + e.getMessage());
			modelo.addAttribute("listaUsuarios", Collections.emptyList());
			return new ResponseEntity<>(Collections.emptyList(), HttpStatus.SERVICE_UNAVAILABLE);
		}
	}
	
	@PostMapping("/obtenerTareas")
	public ResponseEntity<List<Tareas>> obtenerTareas(@RequestBody Tareas idProyecto, Model modelo) {
	    String urlservicebd = (baseUrl != null ? baseUrl : "http://localhost:8081") + "/getTarea";
	    RestTemplate template = new RestTemplate();
	    HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_JSON);
		try {
	    	Tareas[] response = template.postForObject(urlservicebd, idProyecto, Tareas[].class);
            List<Tareas> listaTareas = (response != null) ? Arrays.asList(response) : Collections.emptyList();
			modelo.addAttribute("tareas", listaTareas);
			return ResponseEntity.ok(listaTareas);
		} catch (Exception e) {
			System.err.println("Error al conectar con el backend de tareas: " + e.getMessage());
			modelo.addAttribute("tareas", Collections.emptyList());
			return new ResponseEntity<>(Collections.emptyList(), HttpStatus.SERVICE_UNAVAILABLE);
		}
	}
	
	@PostMapping("/obtenerComentarios")
	public ResponseEntity<List<Comentarios>> obtenerComentarios(@RequestBody Comentarios idComentario, Model modelo) {
	    String urlservicebd = (baseUrl != null ? baseUrl : "http://localhost:8081") + "/getComentario";
	    RestTemplate template = new RestTemplate();
	    HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_JSON);
	    try {
	    	Comentarios[] response = template.postForObject(urlservicebd, idComentario, Comentarios[].class);
	    	List<Comentarios> listaComentarios = (response != null) ? Arrays.asList(response) : Collections.emptyList();
	    	modelo.addAttribute("comentarios", listaComentarios);
	    	return ResponseEntity.ok(listaComentarios);
	    } catch (Exception e) {
	    	System.err.println("Error al conectar con backend de comentarios: " + e.getMessage());
	    	modelo.addAttribute("comentarios", Collections.emptyList());
	    	return new ResponseEntity<>(Collections.emptyList(), HttpStatus.SERVICE_UNAVAILABLE);
	    }
	}
	
	@GetMapping("/proyecto")
	public String getProyecto(Model modelo) {
		Tareas tarea = new Tareas();
		modelo.addAttribute("tarea", tarea);
		
		Comentarios comentario = new Comentarios();
		modelo.addAttribute("comentarios", comentario);
		
		try {
			RestTemplate template = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			
			String urlservicebdUsuario = (baseUrl != null ? baseUrl : "http://localhost:8081") + "/buscarUsuarios";
			Usuario[] responseUsuario = template.postForObject(urlservicebdUsuario, headers, Usuario[].class);  
			List<Usuario> usuarios = (responseUsuario != null) ? Arrays.asList(responseUsuario) : Collections.emptyList();
			modelo.addAttribute("listaUsuarios", usuarios);
		} catch (Exception e) {
			System.err.println("Error al cargar usuarios en getProyecto: " + e.getMessage());
			modelo.addAttribute("listaUsuarios", Collections.emptyList());
		}
		
		return "proyecto";
	}
	
	@GetMapping("/tarea")
	public String getTarea(Model modelo) {
		Tareas tareas = new Tareas();
		modelo.addAttribute("tareas", tareas);
		return "proyecto";
	}
	
	@PostMapping("/saveTarea")
	public ModelAndView setSaveTareas(@ModelAttribute Tareas tareas) {
		try {
			RestTemplate template = new RestTemplate();
			String urlservicebd = (baseUrl != null ? baseUrl : "http://localhost:8081") + "/addTareas";
			template.postForEntity(urlservicebd, tareas, Integer.class);
		} catch (Exception e) {
			System.err.println("Error al guardar tarea: " + e.getMessage());
		}

		ModelAndView mav = new ModelAndView("redirect:/proyecto");
		mav.addObject("id_proyecto", tareas.getId_proyecto());
		return mav;
	}

	@PostMapping("/saveComentario")
	public String setSaveComentario(@RequestBody Comentarios comentarios) {
		try {
			RestTemplate template = new RestTemplate();
			String urlservicebd = (baseUrl != null ? baseUrl : "http://localhost:8081") + "/addComentario";
			template.postForEntity(urlservicebd, comentarios, Integer.class); 
		} catch (Exception e) {
			System.err.println("Error al guardar comentario: " + e.getMessage());
		}
		
		return "proyecto";
	}
	
	@PostMapping("/actualizarTarea")
	public ResponseEntity<Object> updateTarea(@RequestBody Tareas tarea) {
		RestTemplate template = new RestTemplate();
		Map<String,Integer> params = new HashMap<String,Integer>();
		params.put("id_tarea", tarea.getId_tarea());
		String urlservicebd = (baseUrl != null ? baseUrl : "http://localhost:8081") + "/buscarTarea?id_tarea={id_tarea}";
		
		try {
			Tareas tareas = template.getForObject(urlservicebd, Tareas.class, params);
			if (tareas == null) {
				return new ResponseEntity<>("Tarea no encontrada", HttpStatus.NOT_FOUND);
			}
			return new ResponseEntity<Object>(tareas, HttpStatus.OK);
		} catch (HttpClientErrorException e) {
			System.err.println("Tarea no encontrada (404): " + e.getMessage());
			return new ResponseEntity<>("Tarea no encontrada", e.getStatusCode());
		} catch (Exception e) {
			System.err.println("Error al actualizar tarea: " + e.getMessage());
			return new ResponseEntity<>("Error al actualizar tarea", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PostMapping("/eliminarTarea")
	public ModelAndView eliminarTarea(@RequestBody Tareas tareas) {
		try {
			RestTemplate template = new RestTemplate();
			String urlservicebd = (baseUrl != null ? baseUrl : "http://localhost:8081") + "/deleteTareas";
			HttpEntity<Tareas> request = new HttpEntity<Tareas>(tareas);
			template.exchange(urlservicebd, HttpMethod.DELETE, request, Integer.class);
		} catch (Exception e) {
			System.err.println("Error al eliminar tarea: " + e.getMessage());
		}
		
		return new ModelAndView("redirect:/proyecto");
	}
	
	@PostMapping("/verificarTarea")
	public ResponseEntity<Integer> verificarTarea(@RequestBody Tareas paramName){
		try {
			String urlservicebd = (baseUrl != null ? baseUrl : "http://localhost:8081") + "/addTareas";
			RestTemplate template = new RestTemplate();
			return template.postForEntity(urlservicebd, paramName, Integer.class);
		} catch (Exception e) {
			System.err.println("Error al verificar tarea: " + e.getMessage());
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PostMapping("/cambiosTareas")
	public ResponseEntity<Integer> cambiosTareas(@RequestBody Tareas paramDatos){
		try {
			String urlservicebd = (baseUrl != null ? baseUrl : "http://localhost:8081") + "/addTareas";
			RestTemplate template = new RestTemplate();
			return template.postForEntity(urlservicebd, paramDatos, Integer.class);
		} catch (Exception e) {
			System.err.println("Error en cambios tareas: " + e.getMessage());
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PostMapping("/consultarTareas")
	public ResponseEntity<Object> consultarTareas() {	
		RestTemplate template = new RestTemplate();
		String urlservicebd = (baseUrl != null ? baseUrl : "http://localhost:8081") + "/buscarTareas";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		try {
			Tareas[] response = template.postForObject(urlservicebd, headers, Tareas[].class);
			List<Tareas> tareas = (response != null) ? Arrays.asList(response) : Collections.emptyList();
			return new ResponseEntity<Object>(tareas, HttpStatus.OK);
		} catch (Exception e) {
			System.err.println("Error al consultar tareas: " + e.getMessage());
			return new ResponseEntity<>(Collections.emptyList(), HttpStatus.SERVICE_UNAVAILABLE);
		}
	}
	
	@PostMapping("/consultarComentarios")
	public ResponseEntity<Object> consultarComentarios() {	
		RestTemplate template = new RestTemplate();
		String urlservicebd = (baseUrl != null ? baseUrl : "http://localhost:8081") + "/buscarComentarios";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		try {
			Comentarios[] response = template.postForObject(urlservicebd, headers, Comentarios[].class);
			List<Comentarios> comentarios = (response != null) ? Arrays.asList(response) : Collections.emptyList();
			return new ResponseEntity<Object>(comentarios, HttpStatus.OK);
		} catch (Exception e) {
			System.err.println("Error al consultar comentarios: " + e.getMessage());
			return new ResponseEntity<>(Collections.emptyList(), HttpStatus.SERVICE_UNAVAILABLE);
		}
	}
}
