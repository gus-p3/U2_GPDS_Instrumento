package com.mx.cbtis.gestor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import com.mx.cbtis.gestor.controller.AsignacionesController;
import com.mx.cbtis.gestor.controller.ComentariosController;
import com.mx.cbtis.gestor.controller.ProyectosController;
import com.mx.cbtis.gestor.controller.TareasController;
import com.mx.cbtis.gestor.controller.UsuarioController;
import com.mx.cbtis.gestor.dto.AsignacionesDTO;
import com.mx.cbtis.gestor.dto.ComentariosDTO;
import com.mx.cbtis.gestor.dto.ProyectosDTO;
import com.mx.cbtis.gestor.dto.TareasDTO;
import com.mx.cbtis.gestor.dto.UsuarioDTO;
import com.mx.cbtis.gestor.repository.AsignacionesRepository;
import com.mx.cbtis.gestor.repository.ComentariosRepository;
import com.mx.cbtis.gestor.repository.ProtectosRepository;
import com.mx.cbtis.gestor.repository.TareasRepository;
import com.mx.cbtis.gestor.repository.UsuarioRepository;

class Ejercicio5MyApiGestorApplicationTests {

	@Test
	void contextLoadsAndDtoRecordsToEntities() {
		Ejercicio5MyApiGestorApplication app = new Ejercicio5MyApiGestorApplication();
		assertNotNull(app);

		AsignacionesDTO adto = new AsignacionesDTO(1, 2, 3, "2026-10-22");
		assertEquals(1, adto.id_asignacion());
		assertNotNull(adto.toEntity());

		ComentariosDTO cdto = new ComentariosDTO(1, "2", 3, "Ok", "2026-10-22");
		assertEquals(1, cdto.id_comentario());
		assertNotNull(cdto.toEntity());

		ProyectosDTO pdto = new ProyectosDTO(1, "P", "D", "2026-01-01", "2026-02-01", "Activo", "F");
		assertEquals(1, pdto.id_proyecto());
		assertNotNull(pdto.toEntity());

		TareasDTO tdto = new TareasDTO(1, "T", "D", "2026-01-01", "pendientes", "danger", 1);
		assertEquals(1, tdto.id_tarea());
		assertNotNull(tdto.toEntity());

		UsuarioDTO udto = new UsuarioDTO(1, "U", "u@test.com", "1234", "Admin", "img.png");
		assertEquals(1, udto.id_usuario());
		assertNotNull(udto.toEntity());
	}

	@Test
	void asignacionesAndComentariosDtoEndpoints() {
		AsignacionesDTO adto = new AsignacionesDTO(1, 2, 3, "2026-10-22");
		AsignacionesController ac = new AsignacionesController(
				mock(AsignacionesRepository.class), mock(UsuarioRepository.class), mock(TareasRepository.class));
		assertEquals(HttpStatus.BAD_REQUEST, ac.addAsignacionEndpoint(null).getStatusCode());
		assertEquals(HttpStatus.NOT_FOUND, ac.addAsignacionEndpoint(adto).getStatusCode());
		assertEquals(1, ac.updateAsignacionEndpoint(adto));
		assertEquals(0, ac.updateAsignacionEndpoint(null));
		assertEquals(1, ac.deleteAsignacionEndpoint(adto));
		assertEquals(0, ac.deleteAsignacionEndpoint(null));

		ComentariosDTO cdto = new ComentariosDTO(1, "2", 3, "Ok", "2026-10-22");
		ComentariosController cc = new ComentariosController(
				mock(ComentariosRepository.class), mock(TareasRepository.class));
		assertNotNull(cc.getComentarioEndpoint(cdto));
		assertNotNull(cc.getComentarioEndpoint(null));
		assertEquals(HttpStatus.BAD_REQUEST, cc.addComentarioEndpoint(null).getStatusCode());
		assertEquals(HttpStatus.NOT_FOUND, cc.addComentarioEndpoint(cdto).getStatusCode());
		assertEquals(1, cc.updateComentarioEndpoint(cdto));
		assertEquals(0, cc.updateComentarioEndpoint(null));
		assertEquals(1, cc.deleteComentarioEndpoint(cdto));
		assertEquals(0, cc.deleteComentarioEndpoint(null));
	}

	@Test
	void proyectosTareasAndUsuarioDtoEndpoints() {
		ProyectosDTO pdto = new ProyectosDTO(1, "P", "D", "2026-01-01", "2026-02-01", "Activo", "F");
		ProyectosController pc = new ProyectosController(mock(ProtectosRepository.class));
		assertEquals(1, pc.addProyectoEndpoint(pdto));
		assertEquals(0, pc.addProyectoEndpoint(null));
		assertEquals(1, pc.updateProyectoEndpoint(pdto));
		assertEquals(0, pc.updateProyectoEndpoint(null));
		assertEquals(HttpStatus.NOT_FOUND, pc.deleteProyectoEndpoint(pdto).getStatusCode());
		assertEquals(HttpStatus.NOT_FOUND, pc.deleteProyectoEndpoint(null).getStatusCode());

		TareasDTO tdto = new TareasDTO(1, "T", "D", "2026-01-01", "pendientes", "danger", 1);
		TareasController tc = new TareasController(mock(TareasRepository.class), mock(ProtectosRepository.class));
		assertNotNull(tc.getTareaEndpoint(tdto));
		assertNotNull(tc.getTareaEndpoint(null));
		assertEquals(HttpStatus.BAD_REQUEST, tc.addTareasEndpoint(null).getStatusCode());
		assertEquals(HttpStatus.NOT_FOUND, tc.addTareasEndpoint(tdto).getStatusCode());
		assertEquals(1, tc.updateTareasEndpoint(tdto));
		assertEquals(0, tc.updateTareasEndpoint(null));
		assertEquals(HttpStatus.NOT_FOUND, tc.deleteTareasEndpoint(tdto).getStatusCode());
		assertEquals(HttpStatus.NOT_FOUND, tc.deleteTareasEndpoint(null).getStatusCode());

		UsuarioDTO udto = new UsuarioDTO(1, "U", "u@test.com", "1234", "Admin", "img.png");
		UsuarioController uc = new UsuarioController(mock(UsuarioRepository.class));
		assertEquals(HttpStatus.BAD_REQUEST, uc.getUsuarioLoginEndpoint(null).getStatusCode());
		assertEquals(HttpStatus.UNAUTHORIZED, uc.getUsuarioLoginEndpoint(udto).getStatusCode());
		assertEquals(HttpStatus.BAD_REQUEST, uc.addUsuarioEndpoint(null).getStatusCode());
		assertEquals(HttpStatus.OK, uc.addUsuarioEndpoint(udto).getStatusCode());
		assertEquals(1, uc.updateUsuarioEndpoint(udto));
		assertEquals(0, uc.updateUsuarioEndpoint(null));
		assertEquals(1, uc.deleteUsuarioEndpoint(udto));
		assertEquals(0, uc.deleteUsuarioEndpoint(null));
	}
}
