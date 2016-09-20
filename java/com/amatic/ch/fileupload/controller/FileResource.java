package com.amatic.ch.fileupload.controller;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.cache.CacheException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amatic.ch.dao.impl.PublicacionDaoImpl;
import com.amatic.ch.dto.Publicacion;
import com.amatic.ch.fileupload.dto.Entity;
import com.amatic.ch.fileupload.dto.FileMeta;
import com.amatic.ch.fileupload.dto.FileUrl;
import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreFailureException;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ServingUrlOptions;
import com.google.appengine.labs.repackaged.com.google.common.collect.Lists;

@Path("/file")
public class FileResource {

    private static final Logger log = LoggerFactory
	    .getLogger(FileResource.class);

    private final BlobstoreService blobstoreService = BlobstoreServiceFactory
	    .getBlobstoreService();
    private final BlobInfoFactory blobInfoFactory = new BlobInfoFactory();

    // @Autowired
    // private PublicacionService publicacionService;
    PublicacionDaoImpl pdi = new PublicacionDaoImpl();

    // properties
    String logo;
    String photoheight;
    String photowidth;

    /* step 1. get a unique url */

    @DELETE
    @Path("/{key}")
    public Response delete(@PathParam("key") String key,
	    @Context HttpServletRequest req, @Context HttpServletResponse res)
	    throws CacheException {
	Status status;
	try {
	    blobstoreService.delete(new BlobKey(key));
	    status = Status.OK;
	} catch (BlobstoreFailureException bfe) {
	    status = Status.NOT_FOUND;
	}
	if (pdi != null) {
	    HttpSession session = req.getSession();
	    Publicacion publicacion = pdi.getPublicacion(
		    (String) session.getAttribute("tituloNuevaPublicacion"),
		    (String) session.getAttribute("tipoNuevaPublicacion"));
	    List<String> lImages = publicacion.getlImages();
	    lImages.remove(key);
	    pdi.update(publicacion);
	}
	return Response.status(status).build();
    }

    /* step 2. post a file */

    @GET
    @Path("/url")
    public Response getCallbackUrl() {
	/* this is /_ah/upload and it redirects to its given path */
	String url = blobstoreService.createUploadUrl("/rest/file");
	return Response.ok(new FileUrl(url)).build();
    }

    /* step 3. redirected to the meta info */

    @SuppressWarnings("null")
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response post(@Context HttpServletRequest req,
	    @Context HttpServletResponse res,
	    @Context ServletContext servletContext) throws IOException,
	    URISyntaxException {
	try {
	    Map<String, BlobKey> blobs = blobstoreService.getUploadedBlobs(req);
	    BlobKey blobKey = blobs.get("files[]");
	    // res.sendRedirect(blobKey.getKeyString() + "/meta");
	    Properties properties = null;

	    InputStream propertiesIS = servletContext
		    .getResourceAsStream("/WEB-INF/application.properties");

	    byte[] contents = new byte[1024];

	    int bytesRead = 0;
	    String strFileContents = "";
	    while ((bytesRead = propertiesIS.read(contents)) != -1) {
		strFileContents = new String(contents, 0, bytesRead);
	    }
	    String[] propertiesvalues = strFileContents.split("\\r\\n");
	    int indexlogo = 0;
	    int indexphotoheight = 0;
	    int indexphotowidth = 0;
	    for (int i = 0; i < propertiesvalues.length; i++) {
		if (propertiesvalues[i].contains("logo")) {
		    indexlogo = i;
		}
		if (propertiesvalues[i].contains("photoheight")) {
		    indexphotoheight = i;
		}
		if (propertiesvalues[i].contains("photowidth")) {
		    indexphotowidth = i;
		}
	    }
	    String[] logoprop = propertiesvalues[indexlogo].split("=");
	    logo = logoprop[1];

	    String[] photoheightprop = propertiesvalues[indexphotoheight]
		    .split("=");
	    photoheight = photoheightprop[1];

	    String[] photowidthprop = propertiesvalues[indexphotowidth]
		    .split("=");
	    photowidth = photowidthprop[1];

	    BlobInfo info = blobInfoFactory.loadBlobInfo(blobKey);
	    HttpSession session = req.getSession();

	    String name = info.getFilename();
	    long size = info.getSize();

	    ImagesService imagesService = ImagesServiceFactory
		    .getImagesService();

	    int sizeImage = ImagesService.SERVING_SIZES_LIMIT;
	    String url = imagesService.getServingUrl(ServingUrlOptions.Builder
		    .withBlobKey(blobKey).crop(false).imageSize(sizeImage)
		    .secureUrl(true));

	    Publicacion publicacion = pdi.getPublicacion(
		    (String) session.getAttribute("tituloNuevaPublicacion"),
		    (String) session.getAttribute("tipoNuevaPublicacion"));
	    List<String> lImages = publicacion.getlImages();
	    lImages.add(url);
	    List<String> lImagesKeys = publicacion.getlImagesKeys();
	    lImagesKeys.add(blobKey.getKeyString());
	    List<String> lImagesNames = publicacion.getlImagesNames();
	    lImagesNames.add(name);

	    String articulo = publicacion.getArticulo();

	    String replaceimg = "<br>";
	    // if (!publicacion.getScript().equals("#") && lImages.size() == 3)
	    // {
	    // replaceimg += "<a target=\"_blank\" href=\"/venta/principal/"
	    // + publicacion.getUrl() + "\">";
	    // }
	    String lazysrc = "data-original";
	    String lazyclass = "imageContextual lazy";
	    // if (logo.equals("CMovsH")) {
	    // lazysrc = "src";
	    // lazyclass = "imageContextual";
	    // }

	    replaceimg += "<img id=\"_image6\" itemprop=\"image\"  " + lazysrc
		    + "=\"" + url + "\" alt=\"" + publicacion.getDescripcion()
		    + "\" style=\"width:" + photowidth + "px; height:"
		    + photoheight + "px; margin-left: 22%;\"/>";
	    // if (!publicacion.getScript().equals("#") && lImages.size() == 3)
	    // {
	    // replaceimg += "</a>";
	    // }
	    replaceimg += "<br> ";

	    // if (lImages.size() == 3) {
	    // articulo = articulo.replaceAll("<img>", replaceimg);
	    //
	    // publicacion.setArticulo(articulo);
	    // }
	    if (lImages.size() == 1) {
		replaceimg = replaceimg.replace(
			"alt=\"" + publicacion.getDescripcion() + "\"",
			"alt=\"primera foto del texto\"");
		replaceimg = replaceimg.replace(
			"id=\"_image6\" itemprop=\"image\" ", "class=\""
				+ lazyclass + "\" ");
		articulo = articulo.replaceAll("<img1>", replaceimg);

		publicacion.setArticulo(articulo);
	    }
	    if (lImages.size() == 2) {
		replaceimg = replaceimg.replace(
			"alt=\"" + publicacion.getDescripcion() + "\"",
			"alt=\"foto de detalle del artículo\"");
		replaceimg = replaceimg.replace(
			"id=\"_image6\" itemprop=\"image\" ", "class=\""
				+ lazyclass + "\" ");
		articulo = articulo.replaceAll("<img2>", replaceimg);

		publicacion.setArticulo(articulo);
	    }
	    if (lImages.size() == 3) {
		replaceimg = replaceimg.replace(
			"alt=\"" + publicacion.getDescripcion() + "\"",
			"alt=\"imagen de " + publicacion.getTitulo() + "\"");
		replaceimg = replaceimg.replace(
			"id=\"_image6\" itemprop=\"image\" ", "class=\""
				+ lazyclass + "\" ");
		articulo = articulo.replaceAll("<img3>", replaceimg);

		publicacion.setArticulo(articulo);
	    }
	    if (lImages.size() == 4) {
		replaceimg = replaceimg.replace(
			"alt=\"" + publicacion.getDescripcion() + "\"", "");
		replaceimg = replaceimg.replace(
			"id=\"_image6\" itemprop=\"image\" ", "class=\""
				+ lazyclass + "\" ");
		articulo = articulo.replaceAll("<img4>", replaceimg);

		publicacion.setArticulo(articulo);
	    }
	    if (lImages.size() == 5) {
		replaceimg = replaceimg.replace(
			"alt=\"" + publicacion.getDescripcion() + "\"", "");
		replaceimg = replaceimg.replace(
			"id=\"_image6\" itemprop=\"image\" ", "class=\""
				+ lazyclass + "\" ");
		articulo = articulo.replaceAll("<img5>", replaceimg);

		publicacion.setArticulo(articulo);
	    }
	    if (lImages.size() == 6) {
		replaceimg = replaceimg.replace(
			"alt=\"" + publicacion.getDescripcion() + "\"", "");
		replaceimg = replaceimg.replace(
			"id=\"_image6\" itemprop=\"image\" ", "class=\""
				+ lazyclass + "\" ");
		articulo = articulo.replaceAll("<img6>", replaceimg);

		publicacion.setArticulo(articulo);
	    }
	    if (lImages.size() == 7) {
		replaceimg = replaceimg.replace(
			"alt=\"" + publicacion.getDescripcion() + "\"", "");
		replaceimg = replaceimg.replace(
			"id=\"_image6\" itemprop=\"image\" ", "class=\""
				+ lazyclass + "\" ");
		articulo = articulo.replaceAll("<img7>", replaceimg);

		publicacion.setArticulo(articulo);
	    }
	    if (lImages.size() == 8) {
		replaceimg = replaceimg.replace(
			"alt=\"" + publicacion.getDescripcion() + "\"", "");
		replaceimg = replaceimg.replace(
			"id=\"_image6\" itemprop=\"image\" ", "class=\""
				+ lazyclass + "\" ");
		articulo = articulo.replaceAll("<img8>", replaceimg);

		publicacion.setArticulo(articulo);
	    }
	    if (lImages.size() == 9) {
		replaceimg = replaceimg.replace(
			"alt=\"" + publicacion.getDescripcion() + "\"", "");
		replaceimg = replaceimg.replace(
			"id=\"_image6\" itemprop=\"image\" ", "class=\""
				+ lazyclass + "\" ");
		articulo = articulo.replaceAll("<img9>", replaceimg);

		publicacion.setArticulo(articulo);
	    }
	    if (lImages.size() == 10) {
		replaceimg = replaceimg.replace(
			"alt=\"" + publicacion.getDescripcion() + "\"", "");
		replaceimg = replaceimg.replace(
			"id=\"_image6\" itemprop=\"image\" ", "class=\""
				+ lazyclass + "\" ");
		articulo = articulo.replaceAll("<img10>", replaceimg);

		publicacion.setArticulo(articulo);
	    }
	    if (lImages.size() == 11) {
		replaceimg = replaceimg.replace(
			"alt=\"" + publicacion.getDescripcion() + "\"", "");
		replaceimg = replaceimg.replace(
			"id=\"_image6\" itemprop=\"image\" ", "class=\""
				+ lazyclass + "\" ");
		articulo = articulo.replaceAll("<img11>", replaceimg);

		publicacion.setArticulo(articulo);
	    }
	    if (lImages.size() == 12) {
		replaceimg = replaceimg.replace(
			"alt=\"" + publicacion.getDescripcion() + "\"", "");
		replaceimg = replaceimg.replace(
			"id=\"_image6\" itemprop=\"image\" ", "class=\""
				+ lazyclass + "\" ");
		articulo = articulo.replaceAll("<img12>", replaceimg);

		publicacion.setArticulo(articulo);
	    }

	    pdi.update(publicacion);
	    int sizePreview = 80;
	    String urlPreview = imagesService
		    .getServingUrl(ServingUrlOptions.Builder
			    .withBlobKey(blobKey).crop(false)
			    .imageSize(sizePreview).secureUrl(true));

	    FileMeta meta = new FileMeta(name, size, url, urlPreview);

	    List<FileMeta> metas = Lists.newArrayList(meta);
	    Entity entity = new Entity(metas);

	    return Response.ok(entity, MediaType.APPLICATION_JSON).build();
	} catch (Exception e) {
	    log.error("error en fileResource al subir imagen", e);
	    return null;
	}
    }

    /* step 4. download the file */

    @GET
    @Path("/{key}/meta")
    public Response redirect(@PathParam("key") String key) throws IOException {
	BlobKey blobKey = new BlobKey(key);
	BlobInfo info = blobInfoFactory.loadBlobInfo(blobKey);

	String name = info.getFilename();
	long size = info.getSize();
	String url = "/rest/file/" + key;

	ImagesService imagesService = ImagesServiceFactory.getImagesService();
	ServingUrlOptions.Builder.withBlobKey(blobKey).crop(false)
		.imageSize(80);

	String urlPreview = imagesService
		.getServingUrl(ServingUrlOptions.Builder.withBlobKey(blobKey)
			.crop(false).imageSize(80));

	FileMeta meta = new FileMeta(name, size, url, urlPreview);

	List<FileMeta> metas = Lists.newArrayList(meta);
	Entity entity = new Entity(metas);
	return Response.ok(entity, MediaType.APPLICATION_JSON).build();
    }

    /* step 5. delete the file */

    @GET
    @Path("/{key}")
    public Response serve(@PathParam("key") String key,
	    @Context HttpServletResponse response) throws IOException {
	BlobKey blobKey = new BlobKey(key);
	final BlobInfo blobInfo = blobInfoFactory.loadBlobInfo(blobKey);
	response.setHeader("Content-Disposition", "attachment; filename="
		+ blobInfo.getFilename());
	BlobstoreServiceFactory.getBlobstoreService().serve(blobKey, response);
	return Response.ok().build();
    }
}