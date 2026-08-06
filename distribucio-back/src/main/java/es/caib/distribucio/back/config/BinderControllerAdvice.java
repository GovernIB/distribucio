package es.caib.distribucio.back.config;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

/**
 * Bloqueja la vinculació de camps de formulari que permetrien manipular el classloader via
 * property binding (p.ex. {@code class.classLoader.URLs[0]}).
 *
 * @author Limit Tecnologies
 */
@ControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE)
public class BinderControllerAdvice {

	@InitBinder
	public void setAllowedFields(WebDataBinder dataBinder) {
		String[] denyList = new String[] {"class.", "Class.", ".class", ".Class"};
		dataBinder.setDisallowedFields(denyList);
	}
}
