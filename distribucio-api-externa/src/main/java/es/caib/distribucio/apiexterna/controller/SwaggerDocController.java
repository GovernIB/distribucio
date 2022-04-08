package es.caib.distribucio.apiexterna.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequestMapping("/dadesobertes")
public class SwaggerDocController {
	
@RequestMapping(value = "/apidoc", method = RequestMethod.GET)
public String documentacio(HttpServletRequest request) {
	
	return "apidoc";
}

}
