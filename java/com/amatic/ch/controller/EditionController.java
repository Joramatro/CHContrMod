package com.amatic.ch.controller;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.cache.CacheException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.amatic.ch.constants.WebConstants;
import com.amatic.ch.dto.Comentario;
import com.amatic.ch.dto.Publicacion;
import com.amatic.ch.dto.User;
import com.amatic.ch.exception.UnknownResourceException;
import com.amatic.ch.service.ComentarioService;
import com.amatic.ch.service.PublicacionService;
import com.amatic.ch.utils.WebUtils;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;

@Controller
public class EditionController {

    private static final Logger log = LoggerFactory
	    .getLogger(EditionController.class);

    @Autowired
    private PublicacionService publicacionService;

    @Autowired
    private ComentarioService comentarioService;

    @Value("#{application['logo']}")
    String logo;

    @RequestMapping(value = { "/edicion/nuevo" }, method = { RequestMethod.GET,
	    RequestMethod.POST })
    public String getEdicion(ModelMap model, HttpServletRequest request,
	    HttpServletResponse response) throws IOException {
	request.getSession();

	return "edicion/nuevaPublicacion";
    }

    @RequestMapping(value = { "/edicion/guardarPublicacion" }, method = { RequestMethod.POST })
    public void guardarPublicacion(
	    ModelMap model,
	    @RequestParam("titulo") String titulo,
	    @RequestParam("descripcion") String descripcion,
	    @RequestParam("resumen") String resumen,
	    @RequestParam("articulo") String articulo,
	    @RequestParam("clase1") String clase1,
	    @RequestParam("clase2") String clase2,
	    @RequestParam("clase3") String clase3,
	    @RequestParam("clase4") String clase4,
	    @RequestParam("clase7") String clase7,
	    @RequestParam("clase10") String clase10,
	    @RequestParam("clase11") String clase11,
	    @RequestParam("clase12") String clase12,
	    @RequestParam("tipo") String tipo,
	    @RequestParam("autor") String autor,
	    @RequestParam("googleAutor") String googleAutor,
	    @RequestParam("portada") String portada,
	    @RequestParam("descPortada") String descPortada,
	    @RequestParam("tituloPortada") String tituloPortada,
	    @RequestParam("destacado") String destacado,
	    @RequestParam("numeros") String numeros,
	    @RequestParam("titulo2") String titulo2,
	    @RequestParam("script") String script,
	    @RequestParam("script2") String script2,
	    @RequestParam("script21") String script21,
	    @RequestParam("script22") String script22,
	    @RequestParam("script31") String script31,
	    @RequestParam("script32") String script32,
	    @RequestParam("script41") String script41,
	    @RequestParam("script42") String script42,
	    @RequestParam("script51") String script51,
	    @RequestParam("script52") String script52,
	    @RequestParam(value = "script61", required = false) String script61,
	    @RequestParam(value = "script62", required = false) String script62,
	    @RequestParam(value = "script71", required = false) String script71,
	    @RequestParam(value = "script72", required = false) String script72,
	    @RequestParam(value = "script81", required = false) String script81,
	    @RequestParam(value = "script82", required = false) String script82,
	    @RequestParam(value = "script91", required = false) String script91,
	    @RequestParam(value = "script92", required = false) String script92,
	    @RequestParam(value = "script101", required = false) String script101,
	    @RequestParam(value = "script102", required = false) String script102,
	    @RequestParam(value = "script111", required = false) String script111,
	    @RequestParam(value = "script112", required = false) String script112,
	    @RequestParam(value = "script121", required = false) String script121,
	    @RequestParam(value = "script122", required = false) String script122,
	    @RequestParam(value = "estrellas", required = false) String estrellas,
	    @RequestParam("disponible") String disponible,
	    HttpServletRequest request, HttpServletResponse response)
	    throws IOException, NoSuchAlgorithmException {
	HttpSession session = request.getSession();

	User user = (User) session
		.getAttribute(WebConstants.SessionConstants.RC_USER);
	if (user == null) {
	    response.sendRedirect("/editar");
	    response.flushBuffer();
	    response.reset();
	}
	Publicacion publicacion = new Publicacion();
	try {
	    publicacion.setKey(WebUtils.SHA1(WebUtils.cleanTildes(titulo
		    .toLowerCase())));
	    publicacion.setNumVisitas(0);
	    publicacion.setTitulo(titulo);
	    publicacion.setUser(Ref.create(Key.create(User.class,
		    user.getMail())));
	    publicacion.setResumen(resumen);
	    publicacion.setDescripcion(descripcion);
	    publicacion.setNumeros(numeros);

	    int i = 1;
	    int punto = 1;
	    articulo = "<p>" + articulo;
	    while (articulo.contains("\n\n")) {
		if (i % 2 != 0) {
		    if (publicacion.getNumeros().equals("S")) {
			articulo = articulo.replaceFirst("\n\n",
				"</p><br><br><h2><span class=\"dropcap color\">"
					+ punto + "</span>");
		    } else {
			articulo = articulo.replaceFirst("\n\n",
				"</p><br><br><h2>");
		    }
		    punto++;
		    if (i == 1) {
			articulo = articulo.replaceFirst("<br>", "</p><br>");
		    }
		} else {
		    articulo = articulo.replaceFirst("\n\n", "</h2><br><p>");
		}
		i++;
	    }
	    articulo = articulo.replaceAll("\n", "</p><p>");
	    articulo = articulo.concat("</p>");

	    String outpath = "ofertas";
	    // Para antiguos como cce
	    if (logo.startsWith("C")) {
		outpath = "venta/principal";
	    }

	    articulo = articulo.replaceAll("<a>",
		    "<a class=\"linkContextual\" target=\"_blank\" href=\"/"
			    + outpath + "/" + publicacion.getUrl() + "\">");
	    articulo = articulo.replaceAll("<a1>",
		    "<a class=\"linkContextual\" target=\"_blank\" href=\"/"
			    + outpath + "/" + publicacion.getUrl() + "\">");
	    articulo = articulo.replaceAll("<a2>",
		    "<a class=\"linkContextual\" target=\"_blank\" href=\"/"
			    + outpath + "/" + publicacion.getUrl() + "-2\">");
	    articulo = articulo.replaceAll("<a3>",
		    "<a class=\"linkContextual\" target=\"_blank\" href=\"/"
			    + outpath + "/" + publicacion.getUrl() + "-3\">");
	    articulo = articulo.replaceAll("<a4>",
		    "<a class=\"linkContextual\" target=\"_blank\" href=\"/"
			    + outpath + "/" + publicacion.getUrl() + "-4\">");
	    articulo = articulo.replaceAll("<a5>",
		    "<a class=\"linkContextual\" target=\"_blank\" href=\"/"
			    + outpath + "/" + publicacion.getUrl() + "-5\">");

	    articulo = articulo.replaceAll("<a6>",
		    "<a class=\"linkContextual\" target=\"_blank\" href=\"/"
			    + outpath + "/" + publicacion.getUrl() + "-6\">");
	    articulo = articulo.replaceAll("<a7>",
		    "<a class=\"linkContextual\" target=\"_blank\" href=\"/"
			    + outpath + "/" + publicacion.getUrl() + "-7\">");
	    articulo = articulo.replaceAll("<a8>",
		    "<a class=\"linkContextual\" target=\"_blank\" href=\"/"
			    + outpath + "/" + publicacion.getUrl() + "-8\">");
	    articulo = articulo.replaceAll("<a9>",
		    "<a class=\"linkContextual\" target=\"_blank\" href=\"/"
			    + outpath + "/" + publicacion.getUrl() + "-9\">");
	    articulo = articulo.replaceAll("<a10>",
		    "<a class=\"linkContextual\" target=\"_blank\" href=\"/"
			    + outpath + "/" + publicacion.getUrl() + "-10\">");
	    articulo = articulo.replaceAll("<a11>",
		    "<a class=\"linkContextual\" target=\"_blank\" href=\"/"
			    + outpath + "/" + publicacion.getUrl() + "-11\">");
	    articulo = articulo.replaceAll("<a12>",
		    "<a class=\"linkContextual\" target=\"_blank\" href=\"/"
			    + outpath + "/" + publicacion.getUrl() + "-12\">");

	    articulo = articulo.replaceAll("<href *",
		    "<a target=\"_blank\" href=");
	    articulo = articulo.replaceAll("</href>", "</a>");

	    i = 1;
	    while (articulo.contains("**")) {
		if (i % 2 != 0) {
		    articulo = articulo.replaceFirst("\\*\\*", "<b>");
		    i++;
		} else {
		    articulo = articulo.replaceFirst("\\*\\*", "</b>");
		    i++;
		}
	    }

	    articulo = articulo.replaceAll("</p></p>", "</p>");

	    publicacion.setArticulo(articulo);
	    publicacion.setClase1(clase1);
	    publicacion.setClase2(clase2);
	    publicacion.setClase3(clase3);
	    publicacion.setClase4(clase4);
	    publicacion.setClase7(clase7);
	    if (clase10 == null || clase10.equals("")) {
		publicacion.setClase10(null);
	    } else {
		publicacion.setClase10(clase10);
	    }
	    if (clase11 == null || clase11.equals("")) {
		publicacion.setClase11(null);
	    } else {
		publicacion.setClase11(clase11);
	    }
	    if (clase12 == null || clase12.equals("")) {
		publicacion.setClase12(null);
	    } else {
		publicacion.setClase12(clase12);
	    }
	    publicacion.setTipo(tipo);
	    publicacion.setAutor(autor);
	    publicacion.setGoogleAutor(googleAutor);
	    publicacion.setPortada(portada);
	    publicacion.setDescPortada(descPortada);
	    publicacion.setTituloPortada(tituloPortada);
	    publicacion.setDestacado(destacado);
	    publicacion.setTitulo2(titulo2);
	    publicacion.setScript(script);
	    publicacion.setScript2(script2);
	    publicacion.setDisponible(disponible);
	    publicacion.setScript21(script21);
	    publicacion.setScript22(script22);
	    publicacion.setScript31(script31);
	    publicacion.setScript32(script32);
	    publicacion.setScript41(script41);
	    publicacion.setScript42(script42);
	    publicacion.setScript51(script51);
	    publicacion.setScript52(script52);

	    publicacion.setScript61(script61);
	    publicacion.setScript62(script62);
	    publicacion.setScript71(script71);
	    publicacion.setScript72(script72);
	    publicacion.setScript81(script81);
	    publicacion.setScript82(script82);
	    publicacion.setScript91(script91);
	    publicacion.setScript92(script92);
	    publicacion.setScript101(script101);
	    publicacion.setScript102(script102);
	    publicacion.setScript111(script111);
	    publicacion.setScript112(script112);
	    publicacion.setScript121(script121);
	    publicacion.setScript122(script122);
	    publicacion.setEstrellas(estrellas);

	    publicacionService.crearPublicacion(publicacion);
	} catch (Exception e) {
	    log.error("error en editioncontroller", e);
	}
	//
	// List<Ref<Publicacion>> lChannels = user.getChannels();
	// lChannels.add(Ref.create(Key.create(Channel.class,
	// channel.getId())));
	//
	// user = this.userService.update(user);
	// request.getSession().setAttribute(
	// WebConstants.SessionConstants.RC_USER, user);

	request.getSession().setAttribute("tituloNuevaPublicacion",
		publicacion.getKey());

	request.getSession().setAttribute("tipoNuevaPublicacion",
		publicacion.getTipo());

	return;

    }

    @RequestMapping(value = { "/{url}/editar" }, method = { RequestMethod.GET,
	    RequestMethod.POST })
    public String editarPublicacion(ModelMap model,
	    @PathVariable("url") String url, HttpServletRequest request,
	    HttpServletResponse response) throws IOException,
	    NoSuchAlgorithmException, CacheException {
	HttpSession session = request.getSession();
	User user = (User) session
		.getAttribute(WebConstants.SessionConstants.RC_USER);
	if (user == null) {
	    response.sendRedirect("/editar");
	    response.flushBuffer();
	    response.reset();
	}

	String keyNormalizada = WebUtils.SHA1(url.replaceAll("-", " ")
		.toLowerCase());
	Publicacion publicacion = publicacionService.getPublicacion(
		keyNormalizada, WebConstants.SessionConstants.EBOOK);
	if (publicacion == null) {
	    publicacion = publicacionService.getPublicacion(keyNormalizada,
		    WebConstants.SessionConstants.ARTICULO);
	}
	if (publicacion == null) {
	    publicacion = publicacionService.getPublicacion(keyNormalizada,
		    WebConstants.SessionConstants.ACCESORIO);
	}

	if (publicacion == null) {
	    String uri = request.getRequestURI();
	    throw new UnknownResourceException("No existe el recurso: " + uri);
	}

	request.getSession().setAttribute("publicacionKey",
		publicacion.getKey());

	request.getSession().setAttribute("publicacionTipo",
		publicacion.getTipo());

	model.addAttribute("publicacion", publicacion);

	return "edicion/editarPublicacion";

    }

    @RequestMapping(value = { "/edicion/guardarEdicionPublicacion" }, method = { RequestMethod.POST })
    public void guardarEdicionPublicacion(
	    ModelMap model,
	    @RequestParam("articulo") String articulo,
	    @RequestParam("portada") String portada,
	    @RequestParam("destacado") String destacado,
	    @RequestParam("descPortada") String descPortada,
	    @RequestParam("tituloPortada") String tituloPortada,
	    @RequestParam("tipo") String tipo,
	    @RequestParam("titulo") String titulo,
	    @RequestParam("titulo2") String titulo2,
	    @RequestParam("resumen") String resumen,
	    @RequestParam("descripcion") String descripcion,
	    @RequestParam("autor") String autor,
	    @RequestParam("googleAutor") String googleAutor,
	    @RequestParam("clase1") String clase1,
	    @RequestParam("clase2") String clase2,
	    @RequestParam("clase3") String clase3,
	    @RequestParam("clase4") String clase4,
	    @RequestParam("clase7") String clase7,
	    @RequestParam("clase10") String clase10,
	    @RequestParam("clase11") String clase11,
	    @RequestParam("clase12") String clase12,
	    @RequestParam("script") String script,
	    @RequestParam("script2") String script2,
	    @RequestParam("script21") String script21,
	    @RequestParam("script22") String script22,
	    @RequestParam("script31") String script31,
	    @RequestParam("script32") String script32,
	    @RequestParam("script41") String script41,
	    @RequestParam("script42") String script42,
	    @RequestParam("script51") String script51,
	    @RequestParam("script52") String script52,
	    @RequestParam(value = "script61", required = false) String script61,
	    @RequestParam(value = "script62", required = false) String script62,
	    @RequestParam(value = "script71", required = false) String script71,
	    @RequestParam(value = "script72", required = false) String script72,
	    @RequestParam(value = "script81", required = false) String script81,
	    @RequestParam(value = "script82", required = false) String script82,
	    @RequestParam(value = "script91", required = false) String script91,
	    @RequestParam(value = "script92", required = false) String script92,
	    @RequestParam(value = "script101", required = false) String script101,
	    @RequestParam(value = "script102", required = false) String script102,
	    @RequestParam(value = "script111", required = false) String script111,
	    @RequestParam(value = "script112", required = false) String script112,
	    @RequestParam(value = "script121", required = false) String script121,
	    @RequestParam(value = "script122", required = false) String script122,
	    @RequestParam(value = "estrellas", required = false) String estrellas,
	    @RequestParam("disponible") String disponible,
	    HttpServletRequest request, HttpServletResponse response)
	    throws IOException, NoSuchAlgorithmException, CacheException {
	HttpSession session = request.getSession();
	User user = (User) session
		.getAttribute(WebConstants.SessionConstants.RC_USER);
	if (user == null) {
	    response.sendRedirect("/editar");
	    response.flushBuffer();
	    response.reset();
	}

	Publicacion publicacion = publicacionService.getPublicacion(
		(String) session.getAttribute("publicacionKey"),
		(String) session.getAttribute("publicacionTipo"));
	try {
	    // articulo = articulo.replaceAll("\n", "");
	    publicacion.setArticulo(articulo);
	    publicacion.setPortada(portada);
	    publicacion.setDestacado(destacado);
	    publicacion.setTipo(tipo);
	    publicacion.setTitulo(titulo);
	    publicacion.setKey(WebUtils.SHA1(WebUtils.cleanTildes(titulo
		    .toLowerCase())));
	    publicacion.setTitulo2(titulo2);
	    publicacion.setResumen(resumen);
	    publicacion.setDescripcion(descripcion);
	    publicacion.setDescPortada(descPortada);
	    publicacion.setTituloPortada(tituloPortada);
	    publicacion.setAutor(autor);
	    publicacion.setGoogleAutor(googleAutor);
	    publicacion.setClase1(clase1);
	    publicacion.setClase2(clase2);
	    publicacion.setClase3(clase3);
	    publicacion.setClase4(clase4);
	    publicacion.setClase7(clase7);
	    if (clase10 == null || clase10.equals("")) {
		publicacion.setClase10(null);
	    } else {
		publicacion.setClase10(clase10);
	    }
	    if (clase11 == null || clase11.equals("")) {
		publicacion.setClase11(null);
	    } else {
		publicacion.setClase11(clase11);
	    }
	    if (clase12 == null || clase12.equals("")) {
		publicacion.setClase12(null);
	    } else {
		publicacion.setClase12(clase12);
	    }
	    publicacion.setScript(script);
	    publicacion.setScript2(script2);
	    publicacion.setDisponible(disponible);
	    publicacion.setScript21(script21);
	    publicacion.setScript22(script22);
	    publicacion.setScript31(script31);
	    publicacion.setScript32(script32);
	    publicacion.setScript41(script41);
	    publicacion.setScript42(script42);
	    publicacion.setScript51(script51);
	    publicacion.setScript52(script52);

	    publicacion.setScript61(script61);
	    publicacion.setScript62(script62);
	    publicacion.setScript71(script71);
	    publicacion.setScript72(script72);
	    publicacion.setScript81(script81);
	    publicacion.setScript82(script82);
	    publicacion.setScript91(script91);
	    publicacion.setScript92(script92);
	    publicacion.setScript101(script101);
	    publicacion.setScript102(script102);
	    publicacion.setScript111(script111);
	    publicacion.setScript112(script112);
	    publicacion.setScript121(script121);
	    publicacion.setScript122(script122);
	    publicacion.setEstrellas(estrellas);

	    publicacionService.update(publicacion);
	} catch (Exception e) {
	    log.error("error en editioncontroller", e);
	}

	session.setAttribute("publicacion", null);
	response.sendRedirect("/");

    }

    @RequestMapping(value = { "/edicion/guardarEdicionFotosPub" }, method = {
	    RequestMethod.GET, RequestMethod.POST })
    public void guardarFotosEdicionPublicacion(
	    ModelMap model,
	    @RequestParam("articulo") String articulo,
	    @RequestParam("portada") String portada,
	    @RequestParam("destacado") String destacado,
	    @RequestParam("descPortada") String descPortada,
	    @RequestParam("tituloPortada") String tituloPortada,
	    @RequestParam("tipo") String tipo,
	    @RequestParam("titulo") String titulo,
	    @RequestParam("titulo2") String titulo2,
	    @RequestParam("resumen") String resumen,
	    @RequestParam("descripcion") String descripcion,
	    @RequestParam("autor") String autor,
	    @RequestParam("googleAutor") String googleAutor,
	    @RequestParam("clase1") String clase1,
	    @RequestParam("clase2") String clase2,
	    @RequestParam("clase3") String clase3,
	    @RequestParam("clase4") String clase4,
	    @RequestParam("clase7") String clase7,
	    @RequestParam("clase10") String clase10,
	    @RequestParam("clase11") String clase11,
	    @RequestParam("clase12") String clase12,
	    @RequestParam("script") String script,
	    @RequestParam("script2") String script2,
	    @RequestParam("script21") String script21,
	    @RequestParam("script22") String script22,
	    @RequestParam("script31") String script31,
	    @RequestParam("script32") String script32,
	    @RequestParam("script41") String script41,
	    @RequestParam("script42") String script42,
	    @RequestParam("script51") String script51,
	    @RequestParam("script52") String script52,
	    @RequestParam(value = "script61", required = false) String script61,
	    @RequestParam(value = "script62", required = false) String script62,
	    @RequestParam(value = "script71", required = false) String script71,
	    @RequestParam(value = "script72", required = false) String script72,
	    @RequestParam(value = "script81", required = false) String script81,
	    @RequestParam(value = "script82", required = false) String script82,
	    @RequestParam(value = "script91", required = false) String script91,
	    @RequestParam(value = "script92", required = false) String script92,
	    @RequestParam(value = "script101", required = false) String script101,
	    @RequestParam(value = "script102", required = false) String script102,
	    @RequestParam(value = "script111", required = false) String script111,
	    @RequestParam(value = "script112", required = false) String script112,
	    @RequestParam(value = "script121", required = false) String script121,
	    @RequestParam(value = "script122", required = false) String script122,
	    @RequestParam(value = "estrellas", required = false) String estrellas,
	    @RequestParam("disponible") String disponible,
	    HttpServletRequest request, HttpServletResponse response)
	    throws IOException, NoSuchAlgorithmException {
	HttpSession session = request.getSession();
	User user = (User) session
		.getAttribute(WebConstants.SessionConstants.RC_USER);
	if (user == null) {
	    response.sendRedirect("/editar");
	    response.flushBuffer();
	    response.reset();
	}
	try {
	    Publicacion publicacion = publicacionService.getPublicacion(
		    (String) session.getAttribute("publicacionKey"),
		    (String) session.getAttribute("publicacionTipo"));

	    publicacion.setArticulo(articulo);
	    publicacion.setPortada(portada);
	    publicacion.setDestacado(destacado);
	    publicacion.setTipo(tipo);
	    publicacion.setTitulo(titulo);
	    publicacion.setKey(WebUtils.SHA1(WebUtils.cleanTildes(titulo
		    .toLowerCase())));
	    publicacion.setTitulo2(titulo2);
	    publicacion.setResumen(resumen);
	    publicacion.setDescripcion(descripcion);
	    publicacion.setDescPortada(descPortada);
	    publicacion.setTituloPortada(tituloPortada);
	    publicacion.setAutor(autor);
	    publicacion.setGoogleAutor(googleAutor);
	    publicacion.setClase1(clase1);
	    publicacion.setClase2(clase2);
	    publicacion.setClase3(clase3);
	    publicacion.setClase4(clase4);
	    publicacion.setClase7(clase7);
	    publicacion.setClase10(clase10);
	    publicacion.setClase11(clase11);
	    publicacion.setClase12(clase12);
	    publicacion.setScript(script);
	    publicacion.setScript2(script2);
	    publicacion.setDisponible(disponible);
	    publicacion.setScript21(script21);
	    publicacion.setScript22(script22);
	    publicacion.setScript31(script31);
	    publicacion.setScript32(script32);
	    publicacion.setScript41(script41);
	    publicacion.setScript42(script42);
	    publicacion.setScript51(script51);
	    publicacion.setScript52(script52);

	    publicacion.setScript61(script61);
	    publicacion.setScript62(script62);
	    publicacion.setScript71(script71);
	    publicacion.setScript72(script72);
	    publicacion.setScript81(script81);
	    publicacion.setScript82(script82);
	    publicacion.setScript91(script91);
	    publicacion.setScript92(script92);
	    publicacion.setScript101(script101);
	    publicacion.setScript102(script102);
	    publicacion.setScript111(script111);
	    publicacion.setScript112(script112);
	    publicacion.setScript121(script121);
	    publicacion.setScript122(script122);
	    publicacion.setEstrellas(estrellas);

	    // // reemplazo tercera imagen
	    // List<String> lImagenes = publicacion.getlImages();
	    // if (lImagenes.size() >= 3) {
	    // articulo = articulo
	    // .replaceAll(
	    // "<br><a target=\"_blank\" href=\"/venta/principal/"
	    // + publicacion.getUrl()
	    // + "\"><img src=\""
	    // + lImagenes.get(2)
	    // + "\" alt=\""
	    // + publicacion.getDescripcion()
	    // +
	    // "\" style=\"width:430px; height:400px; margin-left: 28%;\"/></a><br> ",
	    // "<img>");
	    // }

	    request.getSession().setAttribute("tituloNuevaPublicacion",
		    publicacion.getKey());

	    request.getSession().setAttribute("tipoNuevaPublicacion",
		    publicacion.getTipo());
	    // FileResource fr = new FileResource();
	    // for (String image : publicacion.getlImagesKeys()) {
	    // fr.delete(image, request, response);
	    // }

	    publicacion.getlImages().clear();
	    publicacion.getlImagesKeys().clear();
	    publicacionService.update(publicacion);
	} catch (Exception e) {
	    log.error("error en editioncontroller", e);
	}

	session.setAttribute("publicacion", null);
	return;

    }

    @RequestMapping(value = { "/edicion/existente" }, method = {
	    RequestMethod.GET, RequestMethod.POST })
    public String getEditarExistentePublicacion(ModelMap model,
	    HttpServletRequest request, HttpServletResponse response)
	    throws IOException {
	request.getSession();

	return "edicion/editarPublicacion";
    }

    @RequestMapping(value = { "/edicion/limpiarComentario" }, method = {
	    RequestMethod.GET, RequestMethod.POST })
    public void getLogEditar(ModelMap model, HttpServletRequest request,
	    HttpServletResponse response) throws IOException,
	    NoSuchAlgorithmException, CacheException {
	HttpSession session = request.getSession();

	Publicacion publicacion = publicacionService.getPublicacion(
		WebUtils.SHA1("kindle"), WebConstants.SessionConstants.EBOOK);

	List<Ref<Comentario>> lComentarios = publicacion.getlComentarios();
	lComentarios.remove(1);
	publicacionService.update(publicacion);
    }

    @RequestMapping(value = { "/edicion/actualizarComentarios" }, method = {
	    RequestMethod.GET, RequestMethod.POST })
    public void getActualizar(ModelMap model, HttpServletRequest request,
	    HttpServletResponse response) throws IOException, CacheException {
	List<Publicacion> publicaciones = publicacionService
		.getPublicaciones(WebConstants.SessionConstants.EBOOK);

	for (Publicacion publicacion : publicaciones) {
	    List<Ref<Comentario>> lComentarios = publicacion.getlComentarios();
	    if (lComentarios == null) {
		lComentarios = new ArrayList<Ref<Comentario>>();
		publicacion.setlComentarios(lComentarios);
	    }
	    List<Comentario> comentarios = publicacion.getComentariosDeref();
	    int i = 0;
	    if (comentarios != null) {
		for (Comentario comentario : comentarios) {
		    if (comentario != null) {
			comentario.setPublicacion(publicacion);
			comentarioService.update(comentario);
		    } else {
			lComentarios.remove(i);
			publicacionService.update(publicacion);
		    }
		}
	    }

	}

	List<Publicacion> publicacionesblog = publicacionService
		.getPublicaciones(WebConstants.SessionConstants.ARTICULO);

	for (Publicacion publicacion : publicacionesblog) {
	    List<Ref<Comentario>> lComentarios = publicacion.getlComentarios();
	    if (lComentarios == null) {
		lComentarios = new ArrayList<Ref<Comentario>>();
		publicacion.setlComentarios(lComentarios);
	    }
	    List<Comentario> comentarios = publicacion.getComentariosDeref();
	    int i = 0;
	    if (comentarios != null) {
		for (Comentario comentario : comentarios) {
		    if (comentario != null) {
			comentario.setPublicacion(publicacion);
			comentarioService.update(comentario);
		    } else {
			lComentarios.remove(i);
			publicacionService.update(publicacion);
		    }
		}
	    }
	}
    }

    @RequestMapping(value = { "/edicion/actualizarPublicaciones" }, method = {
	    RequestMethod.GET, RequestMethod.POST })
    public void getActualizarPublicaciones(ModelMap model,
	    HttpServletRequest request, HttpServletResponse response)
	    throws IOException, NoSuchAlgorithmException, CacheException {

	List<Publicacion> publicaciones = publicacionService
		.getPublicaciones(WebConstants.SessionConstants.EBOOK);
	for (Publicacion publicacion : publicaciones) {
	    publicacion.setKey(WebUtils.SHA1(WebUtils.cleanTildes(publicacion
		    .getTitulo().toLowerCase())));
	    // if (publicacion.getClase3() == null) {
	    // publicacion.setClase3("");
	    // }
	    // if (publicacion.getClase4() == null) {
	    // publicacion.setClase4("");
	    // }
	    // if (publicacion.getDescPortada() == null) {
	    // publicacion.setDescPortada("");
	    // }
	    // if (publicacion.getDestacado() == null) {
	    // publicacion.setDestacado("N");
	    // }
	    // if (publicacion.getGoogleAutor() == null) {
	    // publicacion
	    // .setGoogleAutor("https://plus.google.com/u/0/108657243775074009859?rel=author");
	    // }
	    // if (publicacion.getlImagesNames() == null) {
	    // publicacion.setlImagesNames(new ArrayList<String>());
	    // }
	    // if (publicacion.getNumeros() == null) {
	    // publicacion.setNumeros("S");
	    // }
	    // if (publicacion.getPortada() == null) {
	    // publicacion.setPortada("N");
	    // }
	    // if (publicacion.getTituloPortada() == null) {
	    // publicacion.setTituloPortada(publicacion.getTitulo());
	    // }
	    // if (publicacion.getDisponible() == null) {
	    // publicacion.setDisponible("S");
	    // }
	    publicacionService.update(publicacion);
	}

	List<Publicacion> publicacionesblog = publicacionService
		.getPublicaciones(WebConstants.SessionConstants.ARTICULO);

	for (Publicacion publicacion : publicacionesblog) {
	    publicacion.setKey(WebUtils.SHA1(WebUtils.cleanTildes(publicacion
		    .getTitulo().toLowerCase())));
	    // if (publicacion.getClase3() == null) {
	    // publicacion.setClase3("");
	    // }
	    // if (publicacion.getClase4() == null) {
	    // publicacion.setClase4("");
	    // }
	    // if (publicacion.getDescPortada() == null) {
	    // publicacion.setDescPortada("");
	    // }
	    // if (publicacion.getDestacado() == null) {
	    // publicacion.setDestacado("N");
	    // }
	    // if (publicacion.getGoogleAutor() == null) {
	    // publicacion
	    // .setGoogleAutor("https://plus.google.com/u/0/108657243775074009859?rel=author");
	    // }
	    // if (publicacion.getlImagesNames() == null) {
	    // publicacion.setlImagesNames(new ArrayList<String>());
	    // }
	    // if (publicacion.getNumeros() == null) {
	    // publicacion.setNumeros("S");
	    // }
	    // if (publicacion.getPortada() == null) {
	    // publicacion.setPortada("N");
	    // }
	    // if (publicacion.getTituloPortada() == null) {
	    // publicacion.setTituloPortada(publicacion.getTitulo());
	    // }
	    // if (publicacion.getDisponible() == null) {
	    // publicacion.setDisponible("S");
	    // }
	    publicacionService.update(publicacion);
	}

	List<Publicacion> publicacionesExtras = publicacionService
		.getPublicaciones(WebConstants.SessionConstants.ACCESORIO);
	if (publicacionesExtras != null) {
	    for (Publicacion publicacion : publicacionesExtras) {
		publicacion.setKey(WebUtils.SHA1(WebUtils
			.cleanTildes(publicacion.getTitulo().toLowerCase())));
		// if (publicacion.getClase7() == null) {
		// publicacion.setClase7("");
		// }
		// if (publicacion.getClase3() == null) {
		// publicacion.setClase3("");
		// }
		// if (publicacion.getClase4() == null) {
		// publicacion.setClase4("");
		// }
		// if (publicacion.getDescPortada() == null) {
		// publicacion.setDescPortada("");
		// }
		// if (publicacion.getDestacado() == null) {
		// publicacion.setDestacado("N");
		// }
		// if (publicacion.getGoogleAutor() == null) {
		// publicacion
		// .setGoogleAutor("https://plus.google.com/u/0/108657243775074009859?rel=author");
		// }
		// if (publicacion.getlImagesNames() == null) {
		// publicacion.setlImagesNames(new ArrayList<String>());
		// }
		// if (publicacion.getNumeros() == null) {
		// publicacion.setNumeros("N");
		// }
		// if (publicacion.getPortada() == null) {
		// publicacion.setPortada("N");
		// }
		// if (publicacion.getTituloPortada() == null) {
		// publicacion.setTituloPortada("");
		// }
		// if (publicacion.getDisponible() == null) {
		// publicacion.setDisponible("N");
		// }
		publicacionService.update(publicacion);
	    }
	}
    }

    @RequestMapping(value = { "/edicion/pubUrlTitulo" }, method = { RequestMethod.GET })
    public String getPubUrlTitulo(ModelMap model, HttpServletRequest request,
	    HttpServletResponse response) throws IOException, CacheException {
	List<Publicacion> publicacionesEbook = publicacionService
		.getPublicaciones(WebConstants.SessionConstants.EBOOK);

	List<Publicacion> publicacionesBlog = publicacionService
		.getPublicaciones(WebConstants.SessionConstants.ARTICULO);

	List<Publicacion> publicacionesExtra = publicacionService
		.getPublicaciones(WebConstants.SessionConstants.ACCESORIO);

	model.addAttribute("publicacionesEbook", publicacionesEbook);

	model.addAttribute("publicacionesBlog", publicacionesBlog);

	model.addAttribute("publicacionesExtra", publicacionesExtra);

	return "edicion/pubUrlTitulo";
    }

    @RequestMapping(value = { "/{url}/infoFotos" }, method = { RequestMethod.GET })
    public String verNombreFotos(ModelMap model,
	    @PathVariable("url") String url, HttpServletRequest request,
	    HttpServletResponse response) throws IOException,
	    NoSuchAlgorithmException, CacheException {
	HttpSession session = request.getSession();
	User user = (User) session
		.getAttribute(WebConstants.SessionConstants.RC_USER);
	if (user == null) {
	    response.sendRedirect("/editar");
	    response.flushBuffer();
	    response.reset();
	}

	String keyNormalizada = WebUtils.SHA1(url.replaceAll("-", " ")
		.toLowerCase());
	Publicacion publicacion = publicacionService.getPublicacion(
		keyNormalizada, WebConstants.SessionConstants.EBOOK);
	if (publicacion == null) {
	    publicacion = publicacionService.getPublicacion(keyNormalizada,
		    WebConstants.SessionConstants.ARTICULO);
	    if (publicacion == null) {
		publicacion = publicacionService.getPublicacion(keyNormalizada,
			WebConstants.SessionConstants.ACCESORIO);
	    }
	}

	if (publicacion == null) {
	    String uri = request.getRequestURI();
	    throw new UnknownResourceException("No existe el recurso: " + uri);
	    // return "channelNotFound";
	}

	model.addAttribute("pubNombresFotos", publicacion.getlImagesNames());
	model.addAttribute("pubUrlsFotos", publicacion.getlImages());
	model.addAttribute("pubKeysFotos", publicacion.getlImagesKeys());

	return "edicion/pubNombresFotos";

    }

    @RequestMapping(value = { "/{tipoedit}/{url}/editar" }, method = {
	    RequestMethod.GET, RequestMethod.POST })
    public String editarPublicacion(ModelMap model,
	    @PathVariable("url") String url,
	    @PathVariable("tipoedit") String tipoedit,
	    HttpServletRequest request, HttpServletResponse response)
	    throws IOException, NoSuchAlgorithmException, CacheException {
	HttpSession session = request.getSession();
	User user = (User) session
		.getAttribute(WebConstants.SessionConstants.RC_USER);
	if (user == null) {
	    response.sendRedirect("/editar");
	    response.flushBuffer();
	    response.reset();
	}

	String tipo = "";
	if (tipoedit.equals(WebConstants.SessionConstants.tipo1)) {
	    tipo = WebConstants.SessionConstants.EBOOK;

	} else if (tipoedit.equals(WebConstants.SessionConstants.tipo2)) {
	    tipo = WebConstants.SessionConstants.ARTICULO;
	} else if (tipoedit.equals(WebConstants.SessionConstants.tipo3)) {
	    tipo = WebConstants.SessionConstants.ACCESORIO;
	}
	String keyNormalizada = WebUtils.SHA1(url.replaceAll("-", " ")
		.toLowerCase());
	Publicacion publicacion = publicacionService.getPublicacion(
		keyNormalizada, tipo);
	if (publicacion == null) {
	    String uri = request.getRequestURI();
	    throw new UnknownResourceException("No existe el recurso: " + uri);
	}
	session.setAttribute("publicacionKey", keyNormalizada);
	session.setAttribute("publicacionTipo", tipo);

	model.addAttribute("publicacion", publicacion);

	return "edicion/editarPublicacion";

    }

    @RequestMapping(value = { "/{tipoedit}/{url}/infoFotos" }, method = { RequestMethod.GET })
    public String verNombreFotos(ModelMap model,
	    @PathVariable("url") String url,
	    @PathVariable("tipoedit") String tipoedit,
	    HttpServletRequest request, HttpServletResponse response)
	    throws IOException, NoSuchAlgorithmException, CacheException {
	HttpSession session = request.getSession();
	User user = (User) session
		.getAttribute(WebConstants.SessionConstants.RC_USER);
	if (user == null) {
	    response.sendRedirect("/editar");
	    response.flushBuffer();
	    response.reset();
	}

	String tipo = "";
	if (tipoedit.equals(WebConstants.SessionConstants.tipo1)) {
	    tipo = WebConstants.SessionConstants.EBOOK;
	} else if (tipoedit.equals(WebConstants.SessionConstants.tipo2)) {
	    tipo = WebConstants.SessionConstants.ARTICULO;
	} else if (tipoedit.equals(WebConstants.SessionConstants.tipo3)) {
	    tipo = WebConstants.SessionConstants.ACCESORIO;
	}

	String keyNormalizada = WebUtils.SHA1(url.replaceAll("-", " ")
		.toLowerCase());
	Publicacion publicacion = publicacionService.getPublicacion(
		keyNormalizada, tipo);

	model.addAttribute("pubNombresFotos", publicacion.getlImagesNames());
	model.addAttribute("pubUrlsFotos", publicacion.getlImages());
	model.addAttribute("pubKeysFotos", publicacion.getlImagesKeys());

	return "edicion/pubNombresFotos";

    }

    @RequestMapping(value = { "/comments" }, method = { RequestMethod.GET })
    public void modificarComentario(ModelMap model, HttpServletRequest request,
	    HttpServletResponse response,
	    @RequestParam("action") String action, @RequestParam("c") String c)
	    throws IOException, NoSuchAlgorithmException {
	HttpSession session = request.getSession();
	User user = (User) session
		.getAttribute(WebConstants.SessionConstants.RC_USER);
	if (user == null) {
	    response.sendRedirect("/editar");
	    response.flushBuffer();
	    response.reset();
	}

	if (action.equals("spam")) {
	    comentarioService.commentAction("spam", Long.parseLong(c));
	} else if (action.equals("nospam")) {
	    comentarioService.commentAction("nospam", Long.parseLong(c));
	} else if (action.equals("delete")) {
	    comentarioService.commentAction("delete", Long.parseLong(c));
	}

    }

}
