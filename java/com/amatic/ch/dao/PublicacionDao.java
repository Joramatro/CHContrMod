package com.amatic.ch.dao;

import java.util.List;

import javax.cache.CacheException;

import com.amatic.ch.dto.Contacto;
import com.amatic.ch.dto.Email;
import com.amatic.ch.dto.Publicacion;

public interface PublicacionDao {

    public void crearPublicacion(Publicacion publicacion);

    public Publicacion getPublicacion(String key, String tipo)
	    throws CacheException;

    public List<Publicacion> getUltimasPublicaciones(String tipo)
	    throws CacheException;

    public List<Publicacion> getPublicaciones(String tipo)
	    throws CacheException;

    public void update(Publicacion publicacion);

    public void saveEmail(Email email);

    void saveContacto(Contacto contacto);

    public List<Publicacion> getPublicacionesMasVistas(String tipo)
	    throws CacheException;

    public List<Publicacion> getPublicacionesDestacadas() throws CacheException;

    public List<Publicacion> getPublicacionesPortada() throws CacheException;

}
