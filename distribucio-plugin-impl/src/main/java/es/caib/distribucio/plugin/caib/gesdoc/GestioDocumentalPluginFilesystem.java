/**
 * 
 */
package es.caib.distribucio.plugin.caib.gesdoc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.caib.distribucio.plugin.DistribucioAbstractPluginProperties;
import es.caib.distribucio.plugin.SistemaExternException;
import es.caib.distribucio.plugin.gesdoc.GestioDocumentalPlugin;

/**
 * Implementació del plugin de gestió documental que
 * emmagatzema els arxius a un directori del sistema
 * de fitxers.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class GestioDocumentalPluginFilesystem extends DistribucioAbstractPluginProperties implements GestioDocumentalPlugin {
	
	public GestioDocumentalPluginFilesystem() {
		super();
	}
	
	public GestioDocumentalPluginFilesystem(Properties properties) {
		super(properties);
	}
	
	@Override
	public synchronized String create(
			String agrupacio,
			InputStream contingut) throws SistemaExternException {
		try {
			String id = new Long(System.currentTimeMillis()).toString();
			File fContent = new File(getBaseDir(agrupacio) + "/" + id);
			fContent.getParentFile().mkdirs();
			while (fContent.exists()) {
				try {
					Thread.sleep(1);
				} catch (Exception ignored) {}
				id = new Long(System.currentTimeMillis()).toString();
				fContent = new File(getBaseDir(agrupacio) + "/" + id);
			}
			FileOutputStream outContent = new FileOutputStream(fContent);
			IOUtils.copy(contingut, outContent);
			outContent.close();
			return id;
		} catch (Exception ex) {
			logger.error("No s'ha pogut crear l'arxiu", ex);
			throw new SistemaExternException(
					"No s'ha pogut crear l'arxiu",
					ex);
		}
	}

	@Override
	public void update(
			String id,
			String agrupacio,
			InputStream contingut) throws SistemaExternException {
		try {
			File fContent = new File(getBaseDir(agrupacio) + "/" + id);
			fContent.getParentFile().mkdirs();
			if (fContent.exists()) {
				FileOutputStream outContent = new FileOutputStream(fContent, false);
				IOUtils.copy(contingut, outContent);
				outContent.close();
			} else {
				throw new SistemaExternException(
						"No s'ha trobat l'arxiu per actualitzar (id=" + id + ")");
			}
		} catch (Exception ex) {
			logger.error("No s'ha pogut actualitzar l'arxiu (id=" + id + ")", ex);
			throw new SistemaExternException(
					"No s'ha pogut actualitzar l'arxiu (id=" + id + ")",
					ex);
		}
	}

	@Override
	public void delete(
			String id,
			String agrupacio) throws SistemaExternException {
		try {
			File fContent = new File(getBaseDir(agrupacio) + "/" + id);
			fContent.getParentFile().mkdirs();
			if (fContent.exists()) {
				fContent.delete();
			} else {
				logger.error("No s'ha trobat l'arxiu per esborrar (id=" + id + ")");
				throw new SistemaExternException(
						"No s'ha trobat l'arxiu per esborrar (id=" + id + ")");
			}
		} catch (Exception ex) {
			logger.error("No s'ha pogut esborrar l'arxiu (id=" + id + ")", ex);
			throw new SistemaExternException(
					"No s'ha pogut esborrar l'arxiu (id=" + id + ")",
					ex);
		}
	}

	@Override
	public void get(
			String id,
			String agrupacio,
			OutputStream contingutOut) throws SistemaExternException {
		try {
			File fContent = new File(getBaseDir(agrupacio) + "/" + id);
			fContent.getParentFile().mkdirs();
			if (fContent.exists()) {
				FileInputStream contingutIn = new FileInputStream(fContent);
				IOUtils.copy(contingutIn, contingutOut);
			} else {
				logger.error("No s'ha trobat l'arxiu per consultar (id=" + id + ")");
				throw new SistemaExternException(
						"No s'ha trobat l'arxiu per consultar (id=" + id + ")");
			}
		} catch (Exception ex) {
			logger.error("No s'ha pogut llegir l'arxiu (id=" + id + ")", ex);
			throw new SistemaExternException(
					"No s'ha pogut llegir l'arxiu (id=" + id + ")",
					ex);
		}
	}



	private String getBaseDir(String agrupacio) {
		String baseDir = getProperty(
				"es.caib.distribucio.plugin.gesdoc.filesystem.base.dir");
		if (baseDir != null) {
			if (baseDir.endsWith("/")) {
				return baseDir + agrupacio;
			} else {
				return baseDir + "/" + agrupacio;
			}
		}
		return baseDir;
	}


	private static final Logger logger = LoggerFactory.getLogger(GestioDocumentalPluginFilesystem.class);
}
