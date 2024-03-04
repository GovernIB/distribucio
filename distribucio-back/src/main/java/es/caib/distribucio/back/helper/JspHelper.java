/**
 * 
 */
package es.caib.distribucio.back.helper;

import java.io.IOException;
import java.io.InputStream;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import javax.servlet.http.HttpServletRequest;

/**
 * Helper per a obtenir informació des de les pàgines JSP.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class JspHelper {

	private static Attributes manifestMainAttributes;

	public static String getImplementationVersion(HttpServletRequest request) throws IOException {
		return getManifestAttributeValue(request, "Implementation-Version");
	}

	public static String getBuildTimestamp(HttpServletRequest request) throws IOException {
		return getManifestAttributeValue(request, "Build-Timestamp");
	}

	public static String getImplementationScmBranch(HttpServletRequest request) throws IOException {
		return getManifestAttributeValue(request, "Implementation-SCM-Branch");
	}

	public static String getImplementationScmRevision(HttpServletRequest request) throws IOException {
		return getManifestAttributeValue(request, "Implementation-SCM-Revision");
	}

	private static String getManifestAttributeValue(
			HttpServletRequest request,
			String attributeName) throws IOException {
		if (manifestMainAttributes == null) {
			InputStream is = request.getServletContext().getResourceAsStream("/META-INF/MANIFEST.MF");
			if (is != null) {
				Manifest manifest = new Manifest(is);
				Attributes attributes = manifest.getMainAttributes();
				return attributes.getValue(attributeName);
			} else {
				manifestMainAttributes = new Attributes();
			}
		}
		String value = manifestMainAttributes.getValue(attributeName);
		return (value != null) ? value : "";
	}

}
