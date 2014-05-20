package com.amatic.ch.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.amatic.ch.constants.WebConstants;
import com.amatic.ch.dto.Contacto;
import com.amatic.ch.dto.Email;
import com.amatic.ch.dto.User;
import com.amatic.ch.service.PublicacionService;
import com.amatic.ch.service.UserService;
import com.amatic.ch.utils.WebUtils;
import com.dyuproject.openid.OpenIdUser;

@Controller
public class OtherController {
    private static final Logger log = LoggerFactory
	    .getLogger(OtherController.class);
    List<Integer> sessions = new ArrayList<Integer>();

    @Autowired
    private PublicacionService publicacionService;

    @Autowired
    private UserService userService;

    @Value("#{application['domain']}")
    String DOMAIN;

    @Value("#{application['emailmail']}")
    String EMAILMAIL;

    @Value("#{application['emailcontact']}")
    String EMAILCONTACT;

    @RequestMapping(value = { "/contacto" }, method = { RequestMethod.GET,
	    RequestMethod.POST })
    public String getContacto(ModelMap model, HttpServletRequest request,
	    HttpServletResponse response) throws IOException {

	return "contacta";
    }

    @RequestMapping(value = { "/sobre" }, method = { RequestMethod.GET,
	    RequestMethod.POST })
    public String getSobre(ModelMap model, HttpServletRequest request,
	    HttpServletResponse response) throws IOException {

	return "sobre";
    }

    @RequestMapping(value = { "/politica-de-privacidad" }, method = {
	    RequestMethod.GET, RequestMethod.POST })
    public String getPoliticaPrivacidad(ModelMap model,
	    HttpServletRequest request, HttpServletResponse response)
	    throws IOException {

	return "privacidad";
    }

    @RequestMapping(value = { "/suscribir" }, method = { RequestMethod.POST })
    public void getSubscripcion(ModelMap model, HttpServletRequest request,
	    @RequestParam("email") String email, HttpServletResponse response)
	    throws IOException {
	Email emailObj = new Email(email);
	publicacionService.saveEmail(emailObj);
	try {
	    this.sendMail("From: " + email, "Nueva Suscripcion " + DOMAIN);
	} catch (Exception e) {
	    // TODO Auto-generated catch block
	    log.error("Mail no mandado pero suscripcion realizada");
	}
	return;
    }

    @RequestMapping(value = { "/contactar" }, method = { RequestMethod.POST })
    public void getContacto(ModelMap model, HttpServletRequest request,
	    @RequestParam("email") String email,
	    @RequestParam("name") String name,
	    @RequestParam("message") String message,
	    HttpServletResponse response) throws Exception {
	Contacto contacto = new Contacto();
	contacto.setEmail(email);
	contacto.setMessage(message);
	contacto.setName(name);
	publicacionService.saveContacto(contacto);
	this.sendMail("From: " + email + "\nName: " + name + "\nMessage:"
		+ message, "Nueva consulta " + DOMAIN);
	return;
    }

    @RequestMapping(value = { "/editar" }, method = { RequestMethod.GET,
	    RequestMethod.POST })
    public String getLogEditar(ModelMap model, HttpServletRequest request,
	    HttpServletResponse response) throws IOException {
	HttpSession session = request.getSession();

	User user = (User) session
		.getAttribute(WebConstants.SessionConstants.RC_USER);
	if (user != null) {
	    response.sendRedirect("/edicion/nuevo");
	    return null;
	} else {
	    return "logEditar";
	}
	// //Ref<?> value has not been initialized
	// if (user != null) {
	// user = this.userService.findUser(user.getMail());
	// }
    }

    @RequestMapping(value = "/preAuth", method = RequestMethod.GET)
    public void getPreAuth(ModelMap model, HttpServletRequest request,
	    HttpServletResponse response) throws IOException {

	User userA = (User) request.getSession().getAttribute(
		WebConstants.SessionConstants.RC_USER);
	if (userA == null) {
	    OpenIdUser userOId = (OpenIdUser) request.getSession()
		    .getAttribute(OpenIdUser.ATTR_NAME);
	    Map<String, Object> mUserOId = (Map<String, Object>) userOId
		    .getAttributes().get("info");
	    String mail;
	    if (mUserOId.get("email") != null) {
		mail = (String) mUserOId.get("email");
	    } else {
		mail = userOId.getIdentity();
	    }
	    try {
		userA = this.userService.findUser(mail);
		userA.setIpAddress(WebUtils.getClienAddress(request));
		userA = this.userService.updateUserIp(userA);
	    } catch (com.googlecode.objectify.NotFoundException nf) {
		userA = new User();
		userA.setMail(mail);
		userA.setIpAddress(WebUtils.getClienAddress(request));
		this.userService.create(userA, false);
		User.setCont(0);
		userA.setNewUser(true);
	    }

	    request.getSession().setAttribute(
		    WebConstants.SessionConstants.RC_USER, userA);
	}

	response.sendRedirect("/edicion/nuevo");
    }

    static Properties props = new Properties();
    static Session session = Session.getDefaultInstance(props, null);

    public void sendMail(String msgBody, String subject) throws Exception {
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
	    throw e;
	}
    }

}
