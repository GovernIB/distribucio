package es.caib.distribucio.apiexterna.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/** Controlador per mostrar la informació swagger de les diferents apis rest */
@Controller
@RequestMapping("/")
public class SwaggerDocController {
	
@RequestMapping(
		value = {"", "/apidoc"}, 
		method = RequestMethod.GET)
public String documentacio(HttpServletRequest request) {
	
	return "apidoc";
}

}
