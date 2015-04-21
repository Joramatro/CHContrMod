package com.amatic.ch.controller;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.akismet.Akismet;

import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.ModelMap;

import com.amatic.ch.constants.WebConstants;
import com.amatic.ch.dto.Comentario;
import com.amatic.ch.dto.Deref;
import com.amatic.ch.dto.Publicacion;
import com.amatic.ch.exception.UnknownResourceException;
import com.amatic.ch.service.ComentarioService;
import com.amatic.ch.service.PublicacionService;
import com.amatic.ch.utils.WebUtils;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;

public abstract class PublicacionAbstract {
    private static final Logger log = LoggerFactory
	    .getLogger(PublicacionAbstract.class);
    @Autowired
    PublicacionService publicacionService;
    @Autowired
    ComentarioService comentarioService;

    @Value("#{application['domain']}")
    String DOMAIN;

    @Value("#{application['brand']}")
    String BRAND;

    @Value("#{application['emailmail']}")
    String EMAILMAIL;

    @Value("#{application['emailcontact']}")
    String EMAILCONTACT;

    @Value("#{application['logo']}")
    String LOGO;

    void guardarComentarioPub(HttpServletRequest request, String url,
	    String nombre, String email, String puntos, String comentario,
	    String web, String nbrComment, HttpServletResponse response)
	    throws Exception {
	Akismet akismet = new Akismet("49f8a3bfb431", "http://www." + DOMAIN);
	boolean isSpam = akismet.commentCheck(request.getRemoteAddr(),
		request.getHeader("User-agent"), request.getHeader("referer"),
		"", // permalink
		"comment", // comment type
		"", // author
		"", // email
		"", comentario, // Text to check
		request.getParameterMap());

	if (isSpam || (comentario != null && comentario.length() < 3)) {
	    // Mail.sendMail(
	    // "Comentario Spam Akimet con ip "
	    // + WebUtils.getClienAddress(request) + " y email: "
	    // + email + "\n Dejado en:" + url + "\n Comentario:"
	    // + comentario + "\n Web:" + web + "\n Puntos:"
	    // + puntos + "\n Nombre:" + nombre,
	    // "Spam Akimet comentario en " + BRAND);
	    // response.sendRedirect("/");
	    // response.flushBuffer();
	    throw new UnknownResourceException("invalid comment");
	} else {
	    String keyNormalizada = WebUtils.SHA1(url.replaceAll("-", " ")
		    .toLowerCase());
	    Publicacion publicacion = publicacionService.getPublicacion(
		    keyNormalizada, WebConstants.SessionConstants.EBOOK);
	    if (publicacion == null) {
		publicacion = publicacionService.getPublicacion(keyNormalizada,
			WebConstants.SessionConstants.ARTICULO);
	    }
	    if (publicacion == null || Integer.parseInt(puntos) < 0
		    || Integer.parseInt(puntos) > 5 || email == null
		    || (email != null && email.trim().equals(""))) {
		String uri = request.getRequestURI();
		throw new UnknownResourceException("Error en comentario " + uri);
		// return "channelNotFound";
	    }

	    List<Ref<Comentario>> lComentarios = publicacion.getlComentarios();
	    Comentario nuevoComentario = new Comentario();
	    if (!nbrComment.equals("") && Integer.parseInt(nbrComment) > 0) {
		Ref<Comentario> refComentReply = lComentarios.get(Integer
			.parseInt(nbrComment) - 1);
		Comentario comentReply = Deref.deref(refComentReply);
		nuevoComentario.setComentarioReply(comentReply.getComentario());
		nuevoComentario.setComentarioReplyNombre(comentReply
			.getNombre());
		nuevoComentario.setComentarioReplyNbr(nbrComment);
	    }

	    nuevoComentario.setFecha(new Date());
	    nuevoComentario.setMail(email);
	    nuevoComentario.setNombre(nombre);
	    nuevoComentario.setPuntos(Integer.parseInt(puntos));
	    comentario = comentario.replaceAll("\r\n", "<br>");
	    String safeComentario = Jsoup.clean(comentario, Whitelist.basic());
	    safeComentario = safeComentario.replaceAll("\n", "");
	    nuevoComentario.setComentario(safeComentario);
	    nuevoComentario.setWeb(web);
	    nuevoComentario.setGravatar(WebUtils.getGravatar80pxUrl(email));
	    nuevoComentario.setIpAddress(WebUtils.getClienAddress(request));
	    nuevoComentario.setPublicacion(publicacion);

	    Key<Comentario> keyNuevoComentario = comentarioService
		    .crearComentario(nuevoComentario);

	    lComentarios.add(Ref.create(keyNuevoComentario));

	    publicacionService.update(publicacion);

	    String urlSpam = "http://www." + DOMAIN
		    + "/comments?action=spam&c=" + keyNuevoComentario.getId();
	    String urlNospam = "http://www." + DOMAIN
		    + "/comments?action=nospam&c=" + keyNuevoComentario.getId();
	    String urlDelete = "http://www." + DOMAIN
		    + "/comments?action=delete&c=" + keyNuevoComentario.getId();
	    ;
	    this.sendMail(
		    "Comentario con IP: " + WebUtils.getClienAddress(request)
			    + " - Email: " + email + "\nDejado en: http://www."
			    + DOMAIN + "/" + url + "\n" + comentario + "\nWeb:"
			    + web + "\nPuntos:" + puntos + "\nNombre:" + nombre
			    + "\n\nSpam: " + urlSpam + "\n\nNospam: "
			    + urlNospam + "\n\nDelete: " + urlDelete

		    , "Nuevo Comentario " + BRAND);
	}

    }

    void setPublicaciones(ModelMap model, String tipo) {
	List<Publicacion> publicaciones = publicacionService
		.getPublicaciones(tipo);

	List<String> categorias = new ArrayList<String>();
	for (Publicacion publicacion : publicaciones) {
	    if (!publicacion.getClase1().equals("")
		    && !categorias.contains(publicacion.getClase1())
		    && !publicacion.getClase1().contains(",")) {
		categorias.add(publicacion.getClase1());
	    }
	    if (!publicacion.getClase2().equals("")
		    && !categorias.contains(publicacion.getClase2())
		    && !publicacion.getClase2().contains(",")) {
		categorias.add(publicacion.getClase2());
	    }
	    if (publicacion.getClase7() != null
		    && !publicacion.getClase7().equals("")
		    && !categorias.contains(publicacion.getClase7())
		    && !publicacion.getClase7().contains(",")) {
		categorias.add(publicacion.getClase7());
	    }
	    if (publicacion.getClase10() != null
		    && !publicacion.getClase10().equals("")
		    && !categorias.contains(publicacion.getClase10())
		    && !publicacion.getClase10().contains(",")) {
		categorias.add(publicacion.getClase10());
	    }
	    if (publicacion.getClase11() != null
		    && !publicacion.getClase11().equals("")
		    && !categorias.contains(publicacion.getClase11())
		    && !publicacion.getClase11().contains(",")) {
		categorias.add(publicacion.getClase11());
	    }
	    if (publicacion.getClase12() != null
		    && !publicacion.getClase12().equals("")
		    && !categorias.contains(publicacion.getClase12())
		    && !publicacion.getClase12().contains(",")) {
		categorias.add(publicacion.getClase12());
	    }
	}
	Collections.sort(categorias);

	List<String> categoriasPrecio = new ArrayList<String>();
	for (Publicacion publicacion : publicaciones) {
	    if (!publicacion.getClase3().equals("")
		    && !categoriasPrecio.contains(publicacion.getClase3())) {
		categoriasPrecio.add(publicacion.getClase3());
	    }
	    if (!publicacion.getClase4().equals("")
		    && !categoriasPrecio.contains(publicacion.getClase4())) {
		categoriasPrecio.add(publicacion.getClase4());
	    }

	    if (!publicacion.getClase3().equals("")) {
		publicacion.setClase5(publicacion.getClase1()
			+ publicacion.getClase3());
		if (!publicacion.getClase2().equals("")) {
		    publicacion.setClase6(publicacion.getClase2()
			    + publicacion.getClase3());
		}
		if (publicacion.getClase7() != null
			&& !publicacion.getClase7().equals("")) {
		    publicacion.setClase8(publicacion.getClase7()
			    + publicacion.getClase3());
		}
		if (publicacion.getClase13() != null
			&& !publicacion.getClase10().equals("")) {
		    publicacion.setClase13(publicacion.getClase10()
			    + publicacion.getClase3());
		}
		if (publicacion.getClase14() != null
			&& !publicacion.getClase11().equals("")) {
		    publicacion.setClase14(publicacion.getClase11()
			    + publicacion.getClase3());
		}
		if (publicacion.getClase15() != null
			&& !publicacion.getClase12().equals("")) {
		    publicacion.setClase15(publicacion.getClase12()
			    + publicacion.getClase3());
		}
	    }
	}
	Collections.sort(categoriasPrecio);

	List<Publicacion> publicacionesMVE = publicacionService
		.getPublicacionesMasVistas(WebConstants.SessionConstants.EBOOK);

	List<Publicacion> publicacionesMVA = publicacionService
		.getPublicacionesMasVistas(WebConstants.SessionConstants.ARTICULO);

	List<Comentario> comentarios = comentarioService
		.getUltimosComentarios();
	List<Comentario> ultimosComentarios = new ArrayList<Comentario>();
	for (Comentario comentario : comentarios) {
	    Comentario ultimoComentario = new Comentario();
	    ultimoComentario.setComentario(Jsoup.clean(
		    comentario.getComentario(), Whitelist.simpleText()));
	    ultimoComentario.setNombre(comentario.getNombre());
	    ultimoComentario.setPublicacion(comentario.getPublicacion());
	    ultimosComentarios.add(ultimoComentario);
	}
	model.addAttribute("comentarios", ultimosComentarios);
	model.addAttribute("publicacionesMVE", publicacionesMVE);
	model.addAttribute("publicacionesMVA", publicacionesMVA);
	model.addAttribute("categorias", categorias);
	model.addAttribute("categoriasPrecio", categoriasPrecio);
	model.addAttribute("publicaciones", publicaciones);

    }

    String setPublicacion(String url, HttpServletRequest request, ModelMap model)
	    throws NoSuchAlgorithmException, UnsupportedEncodingException {
	String keyNormalizada = WebUtils.SHA1(url.replaceAll("-", " ")
		.toLowerCase());
	String view = "ebook";
	Publicacion publicacion = publicacionService.getPublicacion(
		keyNormalizada, WebConstants.SessionConstants.EBOOK);
	if (publicacion == null) {
	    publicacion = publicacionService.getPublicacion(keyNormalizada,
		    WebConstants.SessionConstants.ARTICULO);
	    view = "articulo";
	}
	if (publicacion == null) {
	    String uri = request.getRequestURI();
	    throw new UnknownResourceException("No existe el recurso: " + uri);
	    // return "channelNotFound";
	}
	// incremeanting number viewers
	publicacion.setNumVisitas(publicacion.getNumVisitas() + 1);
	publicacionService.update(publicacion);

	model.addAttribute("publicacion", publicacion);

	List<Publicacion> publicaciones = publicacionService
		.getUltimasPublicaciones(publicacion.getTipo());

	List<Publicacion> publicacionesInteresantes = new ArrayList<Publicacion>();
	for (Publicacion publicacionNoRep : publicaciones) {
	    if (!publicacion.getKey().equals(publicacionNoRep.getKey())) {
		publicacionesInteresantes.add(publicacionNoRep);
	    }
	}

	List<Publicacion> publicacionesMVE = publicacionService
		.getPublicacionesMasVistas(WebConstants.SessionConstants.EBOOK);

	List<Publicacion> publicacionesMVA = publicacionService
		.getPublicacionesMasVistas(WebConstants.SessionConstants.ARTICULO);

	if (LOGO.equals("CSMG")) {
	    List<Comentario> comentarios = comentarioService
		    .getUltimosComentarios();
	    List<Comentario> ultimosComentarios = new ArrayList<Comentario>();
	    for (Comentario comentario : comentarios) {
		Comentario ultimoComentario = new Comentario();
		ultimoComentario.setComentario(Jsoup.clean(comentario
			.getComentario().replaceAll("<br />", " "), Whitelist
			.simpleText()));
		ultimoComentario.setNombre(comentario.getNombre());
		ultimoComentario.setPublicacion(comentario.getPublicacion());
		ultimosComentarios.add(ultimoComentario);
	    }
	    model.addAttribute("comentarios", ultimosComentarios);
	}

	model.addAttribute("publicacionesMVE", publicacionesMVE);

	model.addAttribute("publicacionesMVA", publicacionesMVA);

	model.addAttribute("publicaciones", publicacionesInteresantes);

	return view;
    }

    static Properties props = new Properties();
    static Session session = Session.getDefaultInstance(props, null);

    public void sendMail(String msgBody, String subject) {
	try {
	    // log.warn("MAIL---------------: " + EMAILMAIL);
	    // log.warn("MAILCONTACT---------------: " + EMAILCONTACT);
	    Message msg = new MimeMessage(session);
	    msg.setFrom(new InternetAddress(EMAILMAIL, "Jorge " + DOMAIN));

	    msg.addRecipient(Message.RecipientType.TO, new InternetAddress(
		    EMAILCONTACT, "Jorge Amat"));

	    msg.setSubject(subject);
	    msg.setText(msgBody);
	    Transport.send(msg);

	} catch (Exception e) {
	    log.error("error al mandar mail", e);
	}
    }
}
