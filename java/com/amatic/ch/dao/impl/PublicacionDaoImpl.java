package com.amatic.ch.dao.impl;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.cache.CacheException;

import org.springframework.stereotype.Repository;

import com.amatic.ch.dao.PublicacionDao;
import com.amatic.ch.dto.Contacto;
import com.amatic.ch.dto.Email;
import com.amatic.ch.dto.Publicacion;
import com.google.appengine.api.memcache.MemcacheService;

@Repository
public class PublicacionDaoImpl implements PublicacionDao {

    static MemcacheService syncCache = null;

    public static void getCache() throws CacheException {
	// syncCache = MemcacheServiceFactory.getMemcacheService();
	// syncCache.setErrorHandler(ErrorHandlers
	// .getConsistentLogAndContinue(Level.INFO));
    }

    @Override
    public void crearPublicacion(Publicacion publicacion) {
	publicacion.setFechaCreacion(new Date());
	ofy().save().entity(publicacion).now();
    }

    @Override
    public Publicacion getPublicacion(String key, String tipo)
	    throws CacheException {
	Publicacion publicacion;
	// if (syncCache == null) {
	// getCache();
	// }
	if (false) {
	    publicacion = (Publicacion) syncCache.get(key + tipo);
	} else {
	    publicacion = ofy().load().type(Publicacion.class)
		    .filter("key", key).filter("tipo", tipo).first().get();
	    // syncCache.put(key + tipo, publicacion);
	}
	return publicacion;
    }

    @Override
    public List<Publicacion> getUltimasPublicaciones(String tipo)
	    throws CacheException {
	// if (syncCache == null) {
	// getCache();
	// }
	List<Publicacion> ultimasPublicaciones;
	// syncCache.put("ultimasPublicaciones", null);
	if (false) {
	    ultimasPublicaciones = (List<Publicacion>) syncCache
		    .get("ultimasPubs");

	} else {
	    ultimasPublicaciones = ofy().load().type(Publicacion.class)
		    .filter("tipo", tipo).order("-fechaCreacion").list();

	    if (ultimasPublicaciones.size() > 15) {
		ultimasPublicaciones = ultimasPublicaciones.subList(0, 15);
	    }
	    // syncCache.put("ultimasPublicaciones", ultimasPublicaciones);
	    // syncCache.put("ultimasPubs", ultimasPublicaciones.size());
	}
	return ultimasPublicaciones;
    }

    @Override
    public List<Publicacion> getPublicacionesDestacadas() throws CacheException {
	List<Publicacion> publicacionesDestacadas;
	String tipo = "destacadas";
	// if (syncCache == null) {
	// getCache();
	// }
	// syncCache.put("publicacionesDestacadas", null);
	if (false) {
	    publicacionesDestacadas = new ArrayList<Publicacion>();
	    publicacionesDestacadas = (List<Publicacion>) syncCache
		    .get("publicacionesDestacadas");
	} else {

	    publicacionesDestacadas = ofy().load().type(Publicacion.class)
		    .filter("destacado", "S").order("-fechaCreacion").list();

	    // syncCache.put("publicacionesDestacadas",
	    // publicacionesDestacadas);

	}
	return publicacionesDestacadas;
    }

    @Override
    public List<Publicacion> getPublicacionesPortada() throws CacheException {

	List<Publicacion> publicacionesPortada;
	// String tipo = "portada";
	// if (syncCache == null) {
	// getCache();
	// }
	// syncCache.put("publicacionesPortada", null);
	if (false) {
	    publicacionesPortada = (List<Publicacion>) syncCache
		    .get("portadaPubs");
	} else {

	    publicacionesPortada = ofy().load().type(Publicacion.class)
		    .filter("portada", "S").order("-fechaCreacion").list();

	    // syncCache.put("publicacionesPortada", publicacionesPortada);

	    // syncCache.put("portadaPubs", publicacionesPortada.size());

	}
	return publicacionesPortada;

    }

    @Override
    public List<Publicacion> getPublicaciones(String tipo)
	    throws CacheException {

	List<Publicacion> publicaciones;
	// String tipo2 = "pubs";
	// if (syncCache == null) {
	// getCache();
	// }
	// syncCache.put("publicacionesPubs", null);
	if (false) {
	    publicaciones = (List<Publicacion>) syncCache
		    .get("publicacionesPubs");
	} else {

	    publicaciones = ofy().load().type(Publicacion.class)
		    .filter("tipo", tipo).order("-fechaCreacion").list();

	    // syncCache.put("publicacionesPubs", publicaciones);

	    // syncCache.put("pubsPubs", publicaciones.size());

	}
	return publicaciones;
    }

    @Override
    public List<Publicacion> getPublicacionesMasVistas(String tipo)
	    throws CacheException {

	List<Publicacion> publicaciones;
	// String tipo2 = "masvistas";
	// if (syncCache == null) {
	// getCache();
	// }
	// syncCache.put("masvistasPublicaciones", null);
	if (false) {
	    publicaciones = (List<Publicacion>) syncCache.get("masvistasPubs");

	} else {

	    publicaciones = ofy().load().type(Publicacion.class)
		    .filter("tipo", tipo).order("-numVisitas").list();

	    // syncCache.put("masvistasPublicaciones", publicaciones);

	    // syncCache.put("masvistasPubs", publicaciones.size());

	}
	return publicaciones;
    }

    @Override
    public void update(Publicacion publicacion) {
	Publicacion publicacionUpd = ofy().load().type(Publicacion.class)
		.id(publicacion.getId()).safeGet();

	copiarPublicacion(publicacionUpd, publicacion);

	ofy().save().entity(publicacionUpd);

    }

    public void copiarPublicacion(Publicacion updatePublicacion,
	    Publicacion publicacion) {
	updatePublicacion.setlImages(publicacion.getlImages());
	updatePublicacion.setlImagesKeys(publicacion.getlImagesKeys());
	updatePublicacion.setKey(publicacion.getKey());
	updatePublicacion.setNumVisitas(publicacion.getNumVisitas());
	updatePublicacion.setTitulo(publicacion.getTitulo());
	updatePublicacion.setResumen(publicacion.getResumen());
	updatePublicacion.setDescripcion(publicacion.getDescripcion());
	updatePublicacion.setArticulo(publicacion.getArticulo());
	updatePublicacion.setKeywords(publicacion.getKeywords());
	updatePublicacion.setClase1(publicacion.getClase1());
	updatePublicacion.setClase2(publicacion.getClase2());
	updatePublicacion.setClase3(publicacion.getClase3());
	updatePublicacion.setClase4(publicacion.getClase4());
	updatePublicacion.setClase7(publicacion.getClase7());
	updatePublicacion.setClase10(publicacion.getClase10());
	updatePublicacion.setClase11(publicacion.getClase11());
	updatePublicacion.setClase12(publicacion.getClase12());
	updatePublicacion.setTipo(publicacion.getTipo());
	updatePublicacion.setAutor(publicacion.getAutor());
	updatePublicacion.setGoogleAutor(publicacion.getGoogleAutor());
	updatePublicacion.setDestacado(publicacion.getDestacado());
	updatePublicacion.setPortada(publicacion.getPortada());
	updatePublicacion.setNumeros(publicacion.getNumeros());
	updatePublicacion.setTituloPortada(publicacion.getTituloPortada());
	updatePublicacion.setDescPortada(publicacion.getDescPortada());
	updatePublicacion.setTitulo2(publicacion.getTitulo2());
	updatePublicacion.setScript(publicacion.getScript());
	updatePublicacion.setScript2(publicacion.getScript2());
	updatePublicacion.setDisponible(publicacion.getDisponible());
	updatePublicacion.setScript21(publicacion.getScript21());
	updatePublicacion.setScript22(publicacion.getScript22());
	updatePublicacion.setScript31(publicacion.getScript31());
	updatePublicacion.setScript32(publicacion.getScript32());
	updatePublicacion.setScript41(publicacion.getScript41());
	updatePublicacion.setScript42(publicacion.getScript42());
	updatePublicacion.setScript51(publicacion.getScript51());
	updatePublicacion.setScript52(publicacion.getScript52());
	updatePublicacion.setScript61(publicacion.getScript61());
	updatePublicacion.setScript62(publicacion.getScript62());
	updatePublicacion.setScript71(publicacion.getScript71());
	updatePublicacion.setScript72(publicacion.getScript72());
	updatePublicacion.setScript81(publicacion.getScript81());
	updatePublicacion.setScript82(publicacion.getScript82());
	updatePublicacion.setScript91(publicacion.getScript91());
	updatePublicacion.setScript92(publicacion.getScript92());
	updatePublicacion.setScript101(publicacion.getScript101());
	updatePublicacion.setScript102(publicacion.getScript102());
	updatePublicacion.setScript111(publicacion.getScript111());
	updatePublicacion.setScript112(publicacion.getScript112());
	updatePublicacion.setScript121(publicacion.getScript121());
	updatePublicacion.setScript122(publicacion.getScript122());
	updatePublicacion.setEstrellas(publicacion.getEstrellas());
    }

    @Override
    public void saveEmail(Email email) {

	ofy().save().entity(email);

    }

    @Override
    public void saveContacto(Contacto contacto) {

	ofy().save().entity(contacto);

    }

}
