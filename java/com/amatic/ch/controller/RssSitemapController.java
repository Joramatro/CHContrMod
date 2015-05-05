package com.amatic.ch.controller;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.amatic.ch.constants.WebConstants;
import com.amatic.ch.dto.Publicacion;
import com.amatic.ch.dto.SampleContent;
import com.amatic.ch.service.PublicacionService;
import com.amatic.ch.utils.WebUtils;
import com.sun.syndication.feed.rss.Category;

@Controller
public class RssSitemapController {

    @Autowired
    private PublicacionService publicacionService;

    @Value("#{application['domain']}")
    String DOMAIN;

    @Value("#{application['logo']}")
    String logo;

    @Value("#{application['photoheight']}")
    String photoheight;

    @Value("#{application['photowidth']}")
    String photowidth;

    @RequestMapping(value = "/rssfeed", method = RequestMethod.GET)
    public ModelAndView getFeedInRss() throws UnsupportedEncodingException {

	List<SampleContent> items = new ArrayList<SampleContent>();

	List<Publicacion> publicacionesEbooks = null;
	// recordar que las nuevas webs usan ebooks pero no son artículos de ahí
	// este if
	if (logo.startsWith("C") && !logo.equals("CSMG")) {
	    publicacionesEbooks = publicacionService
		    .getUltimasPublicaciones(WebConstants.SessionConstants.EBOOK);

	    for (Publicacion publicacionEbook : publicacionesEbooks) {
		SampleContent content = new SampleContent();
		content.setTitle(publicacionEbook.getTitulo());
		content.setAuthor(publicacionEbook.getAutor());
		String subfolder = "";
		if (logo.equals("CCE")) {
		    subfolder = "cafeteras/";
		} else if (logo.equals("CEH")) {
		    subfolder = "ebooks/";
		} else if (logo.equals("CMH")) {
		    subfolder = "microondas/";
		}
		content.setUrl("http://www." + DOMAIN + "/" + subfolder
			+ publicacionEbook.getUrl());

		String img0 = "";
		if (publicacionEbook.getlImages() != null
			&& publicacionEbook.getlImages().size() > 0) {
		    img0 = "<img src=\"" + publicacionEbook.getlImages().get(0)
			    + "\" width=\"" + photowidth + "\" height=\""
			    + photoheight
			    + "\" alt=\"Foto principal\"/><br/><br/>";
		}

		content.setSummary(img0 + publicacionEbook.getResumen());
		Category category = new Category();
		category.setValue(publicacionEbook.getClase1());
		content.getCategories().add(category);
		if (publicacionEbook.getClase2() != null
			&& !publicacionEbook.getClase2().equals("")) {
		    Category category2 = new Category();
		    category2.setValue(publicacionEbook.getClase2());
		    content.getCategories().add(category2);
		}
		content.setDescription(publicacionEbook.getDescripcion());
		content.setComments("http://www." + DOMAIN + "/" + subfolder
			+ publicacionEbook.getUrl() + "/#comments");
		content.setCreatedDate(publicacionEbook.getFechaCreacion());
		try {
		    content.setGuid(WebUtils.SHA1(String
			    .valueOf(publicacionEbook.getId())));
		} catch (NoSuchAlgorithmException e) {
		    content.setGuid(publicacionEbook.getUrl());
		}
		items.add(content);
	    }
	}
	List<Publicacion> publicacionesBlog = publicacionService
		.getUltimasPublicaciones(WebConstants.SessionConstants.ARTICULO);

	for (Publicacion publicacionArticulo : publicacionesBlog) {
	    SampleContent content = new SampleContent();
	    content.setTitle(publicacionArticulo.getTitulo());
	    content.setAuthor(publicacionArticulo.getAutor());
	    if (logo.startsWith("C") && !logo.equals("CMovsH")) {
		content.setUrl("http://www." + DOMAIN + "/blog/"
			+ publicacionArticulo.getUrl());
	    } else {
		content.setUrl("http://www." + DOMAIN + "/"
			+ publicacionArticulo.getUrl());
	    }

	    String img0 = "";
	    if (publicacionArticulo.getlImages() != null
		    && publicacionArticulo.getlImages().size() > 0) {
		img0 = "<img src=\"" + publicacionArticulo.getlImages().get(0)
			+ "\" width=\"" + photowidth + "\" height=\""
			+ photoheight + "\" alt=\"Foto principal\"/><br/><br/>";
	    }

	    content.setSummary(img0 + publicacionArticulo.getResumen());
	    Category category = new Category();
	    category.setValue(publicacionArticulo.getClase1());
	    content.getCategories().add(category);
	    if (publicacionArticulo.getClase2() != null
		    && !publicacionArticulo.getClase2().equals("")) {
		Category category2 = new Category();
		category2.setValue(publicacionArticulo.getClase2());
		content.getCategories().add(category2);
	    }
	    content.setDescription(publicacionArticulo.getDescripcion());
	    if (logo.startsWith("C") && !logo.equals("CMovsH")) {
		content.setComments("http://www." + DOMAIN + "/blog/"
			+ publicacionArticulo.getUrl() + "/#comments");
	    } else {
		content.setComments("http://www." + DOMAIN + "/"
			+ publicacionArticulo.getUrl() + "/#comments");
	    }

	    content.setCreatedDate(publicacionArticulo.getFechaCreacion());
	    try {
		content.setGuid(WebUtils.SHA1(String
			.valueOf(publicacionArticulo.getId())));
	    } catch (NoSuchAlgorithmException e) {
		content.setGuid(publicacionArticulo.getUrl());
	    }
	    items.add(content);
	}

	Collections.sort(items, new Comparator<SampleContent>() {
	    @Override
	    public int compare(SampleContent o1, SampleContent o2) {
		return o2.getCreatedDate().compareTo(o1.getCreatedDate());
	    }
	});

	ModelAndView mav = new ModelAndView();
	mav.setViewName("rssViewer");
	mav.addObject("feedContent", items);

	return mav;

    }

    @RequestMapping(value = "/sitemap.xml", method = RequestMethod.GET)
    public String getMainScreen(ModelMap model, HttpServletRequest request,
	    HttpServletResponse response) {
	List<Publicacion> publicacionesBlog = publicacionService
		.getPublicaciones(WebConstants.SessionConstants.ARTICULO);

	List<Publicacion> publicacionesEbooks = null;
	if (logo.startsWith("C") && !logo.equals("CSMG")) {
	    publicacionesEbooks = publicacionService
		    .getUltimasPublicaciones(WebConstants.SessionConstants.EBOOK);

	    model.addAttribute("publicacionesEbooks", publicacionesEbooks);
	}

	model.addAttribute("publicacionesBlog", publicacionesBlog);

	return "sitemap";

    }
}
