package com.amatic.ch.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.amatic.ch.utils.WebUtils;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Ignore;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Load;

@Entity
public class Publicacion implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 6590618419604151092L;

    @Id
    public Long id;

    @Index
    String key;

    @Index
    Integer numVisitas;
    @Index
    String titulo;
    @Load
    Ref<User> user;
    @Index
    List<String> lImages = new ArrayList<String>();
    @Index
    private Date fechaCreacion;
    @Index
    List<String> lImagesKeys = new ArrayList<String>();
    @Index
    List<String> lImagesNames = new ArrayList<String>();
    @Index
    String descripcion;
    String resumen;
    String articulo;
    @Index
    String keywords;
    @Index
    String clase1;
    @Index
    String clase2;
    @Index
    String clase3;
    @Index
    String clase4;
    @Index
    String clase7;
    @Index
    String clase10;
    @Index
    String clase11;
    @Index
    String clase12;
    @Ignore
    String clase5;
    @Ignore
    String clase6;
    @Ignore
    String clase8;
    @Ignore
    String clase13;
    @Ignore
    String clase14;
    @Ignore
    String clase15;
    @Index
    String tipo;
    @Index
    String autor;
    @Index
    String googleAutor;
    @Index
    String portada;
    @Index
    String tituloPortada;
    @Index
    String descPortada;
    @Index
    String destacado;
    @Index
    String numeros;
    @Index
    String titulo2;
    @Index
    String script;
    @Index
    String script2;
    @Load
    List<Ref<Comentario>> lComentarios = new ArrayList<Ref<Comentario>>();
    @Index
    String disponible;
    @Index
    String script21;
    @Index
    String script22;
    @Index
    String script31;
    @Index
    String script32;
    @Index
    String script41;
    @Index
    String script42;
    @Index
    String script51;
    @Index
    String script52;

    @Index
    String script61;
    @Index
    String script62;
    @Index
    String script71;
    @Index
    String script72;
    @Index
    String script81;
    @Index
    String script82;
    @Index
    String script91;
    @Index
    String script92;
    @Index
    String script101;
    @Index
    String script102;
    @Index
    String script111;
    @Index
    String script112;
    @Index
    String script121;
    @Index
    String script122;
    @Index
    String estrellas;

    public String getAutor() {
	return autor;
    }

    public void setAutor(String autor) {
	this.autor = autor;
    }

    public String getTipo() {
	return tipo;
    }

    public void setTipo(String tipo) {
	this.tipo = tipo;
    }

    public List<Ref<Comentario>> getlComentarios() {
	Iterator<Ref<Comentario>> it = lComentarios.iterator();
	while (it.hasNext()) {
	    Ref<Comentario> refComentario = it.next();
	    Comentario comentario = Deref.deref(refComentario);
	    if ((comentario == null)
		    || (comentario.getPublicado() == null)
		    || (comentario != null && comentario.getPublicado().equals(
			    "N"))) {
		it.remove();
	    }
	}

	return lComentarios;
    }

    public void setlComentarios(List<Ref<Comentario>> lComentarios) {
	this.lComentarios = lComentarios;
    }

    public String getClase1() {
	return clase1;
    }

    public void setClase1(String clase1) {
	this.clase1 = clase1;
    }

    public String getClase2() {
	return clase2;
    }

    public void setClase2(String clase2) {
	this.clase2 = clase2;
    }

    public Integer getNumVisitas() {
	return numVisitas;
    }

    public void setNumVisitas(Integer numVisitas) {
	this.numVisitas = numVisitas;
    }

    public String getTitulo() {
	return titulo;
    }

    public void setTitulo(String titulo) {
	this.titulo = titulo;
    }

    public Date getFechaCreacion() {
	return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
	this.fechaCreacion = fechaCreacion;
    }

    public String getDescripcion() {
	return descripcion;
    }

    public void setDescripcion(String descripcion) {
	this.descripcion = descripcion;
    }

    public String getResumen() {
	return resumen;
    }

    public void setResumen(String resumen) {
	this.resumen = resumen;
    }

    public String getArticulo() {
	return articulo;
    }

    public void setArticulo(String articulo) {
	this.articulo = articulo;
    }

    public String getKeywords() {
	return keywords;
    }

    public void setKeywords(String keywords) {
	this.keywords = keywords;
    }

    public Long getId() {
	return id;
    }

    public String getKey() {
	return key;
    }

    public List<String> getlImages() {
	return lImages;
    }

    public List<String> getlImagesKeys() {
	return lImagesKeys;
    }

    public Ref<User> getUser() {
	return user;
    }

    public User getUserDeref() {
	return Deref.deref(user);
    }

    public void setId(Long id) {
	this.id = id;
    }

    public void setKey(String key) {
	this.key = key;
    }

    public void setlImages(List<String> lImages) {
	this.lImages = lImages;
    }

    public void setlImagesKeys(List<String> lImagesKeys) {
	this.lImagesKeys = lImagesKeys;
    }

    public void setUser(Ref<User> user) {
	this.user = user;
    }

    public String getUrl() {
	String url = WebUtils.cleanTildes(this.titulo.replaceAll(" ", "-"));
	if (url.endsWith("-")) {
	    url = url.substring(0, url.length() - 1);
	}
	return url;
    }

    public List<Comentario> getComentariosDeref() {
	return Deref.deref(getlComentarios());
    }

    public int getSumaPuntos() {
	List<Comentario> lComentarios = this.getComentariosDeref();
	int sumaPuntos = 0;
	for (Comentario comentario : lComentarios) {
	    sumaPuntos += comentario.getPuntos();
	}
	return sumaPuntos;
    }

    public int getVotantes() {
	int votantes = 0;
	if (this.getComentariosDeref() != null) {
	    List<Comentario> lComentarios = this.getComentariosDeref();
	    for (Comentario comentario : lComentarios) {
		if (comentario.getPuntos() > 0) {
		    votantes++;
		}
	    }
	}
	return votantes;
    }

    public int getNumComentarios() {
	return lComentarios.size();
    }

    public String getTitulo2() {
	return titulo2;
    }

    public void setTitulo2(String titulo2) {
	this.titulo2 = titulo2;
    }

    public String getScript() {
	return script;
    }

    public void setScript(String script) {
	this.script = script;
    }

    public String getScript2() {
	return script2;
    }

    public void setScript2(String script2) {
	this.script2 = script2;
    }

    public String getDisponible() {
	return disponible;
    }

    public void setDisponible(String disponible) {
	this.disponible = disponible;
    }

    public String getClase3() {
	return clase3;
    }

    public void setClase3(String clase3) {
	this.clase3 = clase3;
    }

    public String getClase4() {
	return clase4;
    }

    public void setClase4(String clase4) {
	this.clase4 = clase4;
    }

    public String getClase5() {
	return clase5;
    }

    public void setClase5(String clase5) {
	this.clase5 = clase5;
    }

    public String getClase6() {
	return clase6;
    }

    public void setClase6(String clase6) {
	this.clase6 = clase6;
    }

    public String getGoogleAutor() {
	return googleAutor;
    }

    public void setGoogleAutor(String googleAutor) {
	this.googleAutor = googleAutor;
    }

    public List<String> getlImagesNames() {
	return lImagesNames;
    }

    public void setlImagesNames(List<String> lImagesNames) {
	this.lImagesNames = lImagesNames;
    }

    public String getPortada() {
	return portada;
    }

    public void setPortada(String portada) {
	this.portada = portada;
    }

    public String getDestacado() {
	return destacado;
    }

    public void setDestacado(String destacado) {
	this.destacado = destacado;
    }

    public String getNumeros() {
	return numeros;
    }

    public void setNumeros(String numeros) {
	this.numeros = numeros;
    }

    public String getDescPortada() {
	return descPortada;
    }

    public void setDescPortada(String descPortada) {
	this.descPortada = descPortada;
    }

    public String getTituloPortada() {
	return tituloPortada;
    }

    public void setTituloPortada(String tituloPortada) {
	this.tituloPortada = tituloPortada;
    }

    public String getClase7() {
	return clase7;
    }

    public void setClase7(String clase7) {
	this.clase7 = clase7;
    }

    public String getClase8() {
	return clase8;
    }

    public void setClase8(String clase8) {
	this.clase8 = clase8;
    }

    public String getScript21() {
	return script21;
    }

    public void setScript21(String script21) {
	this.script21 = script21;
    }

    public String getScript22() {
	return script22;
    }

    public void setScript22(String script22) {
	this.script22 = script22;
    }

    public String getScript31() {
	return script31;
    }

    public void setScript31(String script31) {
	this.script31 = script31;
    }

    public String getScript32() {
	return script32;
    }

    public void setScript32(String script32) {
	this.script32 = script32;
    }

    public String getScript41() {
	return script41;
    }

    public void setScript41(String script41) {
	this.script41 = script41;
    }

    public String getScript42() {
	return script42;
    }

    public void setScript42(String script42) {
	this.script42 = script42;
    }

    public String getScript51() {
	return script51;
    }

    public void setScript51(String script51) {
	this.script51 = script51;
    }

    public String getScript52() {
	return script52;
    }

    public void setScript52(String script52) {
	this.script52 = script52;
    }

    public String getClase10() {
	return clase10;
    }

    public void setClase10(String clase10) {
	this.clase10 = clase10;
    }

    public String getClase11() {
	return clase11;
    }

    public void setClase11(String clase11) {
	this.clase11 = clase11;
    }

    public String getClase12() {
	return clase12;
    }

    public void setClase12(String clase12) {
	this.clase12 = clase12;
    }

    public String getClase13() {
	return clase13;
    }

    public void setClase13(String clase13) {
	this.clase13 = clase13;
    }

    public String getClase14() {
	return clase14;
    }

    public void setClase14(String clase14) {
	this.clase14 = clase14;
    }

    public String getClase15() {
	return clase15;
    }

    public void setClase15(String clase15) {
	this.clase15 = clase15;
    }

    public String getScript61() {
	return script61;
    }

    public void setScript61(String script61) {
	this.script61 = script61;
    }

    public String getScript62() {
	return script62;
    }

    public void setScript62(String script62) {
	this.script62 = script62;
    }

    public String getScript71() {
	return script71;
    }

    public void setScript71(String script71) {
	this.script71 = script71;
    }

    public String getScript72() {
	return script72;
    }

    public void setScript72(String script72) {
	this.script72 = script72;
    }

    public String getScript81() {
	return script81;
    }

    public void setScript81(String script81) {
	this.script81 = script81;
    }

    public String getScript82() {
	return script82;
    }

    public void setScript82(String script82) {
	this.script82 = script82;
    }

    public String getScript91() {
	return script91;
    }

    public void setScript91(String script91) {
	this.script91 = script91;
    }

    public String getScript92() {
	return script92;
    }

    public void setScript92(String script92) {
	this.script92 = script92;
    }

    public String getScript101() {
	return script101;
    }

    public void setScript101(String script101) {
	this.script101 = script101;
    }

    public String getScript102() {
	return script102;
    }

    public void setScript102(String script102) {
	this.script102 = script102;
    }

    public String getScript111() {
	return script111;
    }

    public void setScript111(String script111) {
	this.script111 = script111;
    }

    public String getScript112() {
	return script112;
    }

    public void setScript112(String script112) {
	this.script112 = script112;
    }

    public String getScript121() {
	return script121;
    }

    public void setScript121(String script121) {
	this.script121 = script121;
    }

    public String getScript122() {
	return script122;
    }

    public void setScript122(String script122) {
	this.script122 = script122;
    }

    public String getEstrellas() {
	return estrellas;
    }

    public void setEstrellas(String estrellas) {
	this.estrellas = estrellas;
    }

}
