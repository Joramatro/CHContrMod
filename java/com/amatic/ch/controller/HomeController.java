package com.amatic.ch.controller;

import java.io.IOException;
import java.util.Date;

import javax.cache.CacheException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.amatic.ch.exception.UnknownResourceException;
import com.amatic.ch.service.ComentarioService;
import com.amatic.ch.service.PublicacionService;

@Controller
public class HomeController {

    private static final Logger log = LoggerFactory
	    .getLogger(HomeController.class);
    @Autowired
    private PublicacionService publicacionService;

    @Autowired
    private ComentarioService comentarioService;

    @RequestMapping(value = { "/index", "/" }, method = { RequestMethod.GET,
	    RequestMethod.POST })
    public String getMainScreen(ModelMap model, HttpServletRequest request,
	    HttpServletResponse response) throws IOException, CacheException {
	HttpSession session = request.getSession();
	response.setDateHeader("Expires", (new Date()).getTime() + 604800000L);
	// User user = (User) session
	// .getAttribute(WebConstants.SessionConstants.RC_USER);
	// // Ref<?> value has not been initialized
	// if (user != null) {
	// user = this.userService.findUser(user.getMail());
	// }
	// // Saltando Uservalidation
	// if (user == null) {
	// user = new User();
	// user.setMail((String) oIdUserBean.getAttribute("email"));
	// user.setName((String) oIdUserBean.getAttribute("nickname"));
	// session.setAttribute(WebConstants.SessionConstants.RC_USER, user);
	// try {
	// user = this.userService.findUser(user.getMail());
	// } catch (com.googlecode.objectify.NotFoundException nf) {
	// this.userService.create(user, false);
	// }
	// }
	// user.setNewUser(true);
	// Fin Uservalidation trick

	// List<Publicacion> publicacionesEbooks = publicacionService
	// .getUltimasPublicaciones(WebConstants.SessionConstants.EBOOK);
	//
	// List<Publicacion> publicacionesBlog = publicacionService
	// .getUltimasPublicaciones(WebConstants.SessionConstants.ARTICULO);
	//
	// List<Publicacion> publicacionesMVE = publicacionService
	// .getPublicacionesMasVistas(WebConstants.SessionConstants.EBOOK);
	//
	// List<Publicacion> publicacionesMVA = publicacionService
	// .getPublicacionesMasVistas(WebConstants.SessionConstants.ARTICULO);
	//
	// List<Publicacion> publicacionesDestacadas = publicacionService
	// .getPublicacionesDestacadas();
	//
	// List<Publicacion> publicacionesPortada = publicacionService
	// .getPublicacionesPortada();
	//
	// List<Comentario> comentarios = comentarioService
	// .getUltimosComentarios();
	// List<Comentario> ultimosComentarios = new ArrayList<Comentario>();
	// for (Comentario comentario : comentarios) {
	// Comentario ultimoComentario = new Comentario();
	// ultimoComentario.setComentario(Jsoup.clean(comentario
	// .getComentario().replaceAll("<br />", " "), Whitelist
	// .simpleText()));
	// ultimoComentario.setNombre(comentario.getNombre());
	// ultimoComentario.setPublicacion(comentario.getPublicacion());
	// ultimosComentarios.add(ultimoComentario);
	// }
	// model.addAttribute("comentarios", ultimosComentarios);
	// model.addAttribute("publicacionesMVE", publicacionesMVE);
	// model.addAttribute("publicacionesMVA", publicacionesMVA);
	// model.addAttribute("publicacionesEbooks", publicacionesEbooks);
	// model.addAttribute("publicacionesBlog", publicacionesBlog);
	// model.addAttribute("publicacionesDestacadas",
	// publicacionesDestacadas);
	// model.addAttribute("publicacionesPortada", publicacionesPortada);

	return "index";
    }

    @RequestMapping("/**")
    public void unmappedRequest(HttpServletRequest request) {
	String uri = request.getRequestURI();

	UnknownResourceException urexc = new UnknownResourceException(
		"No existe esta ruta: " + uri);
	log.error("error Bad Request", urexc);

	throw urexc;
	// return "errors/error";
    }

}
