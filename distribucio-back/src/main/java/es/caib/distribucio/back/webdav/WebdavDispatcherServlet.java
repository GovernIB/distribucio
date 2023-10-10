/**
 * 
 */
package es.caib.distribucio.back.webdav;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.DispatcherServlet;

/**
 * Servlet que intercepta totes les peticions HTTP i les
 * redirecciona cap al servidor WEBDAV o les deixa passar
 * cap al DispatcherServlet de Spring.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class WebdavDispatcherServlet extends DispatcherServlet {

    @Override
    protected void service(
    		HttpServletRequest request,
    		HttpServletResponse response) throws ServletException, IOException {
    	String webdavPrefix = getInitParameter("webdav-prefix");
    	String uriSenseContext = request.getRequestURI().substring(
    			request.getContextPath().length());
    	if (uriSenseContext.startsWith(webdavPrefix)) {
    		try {
                doService(request, response);
            } catch(ServletException e) {
                throw e;
            } catch(IOException e) {
                throw e;
            } catch (Exception ex) {
                throw new ServletException(ex);
            }
    	} else {
    		super.service(request, response);
    	}
    }

}
