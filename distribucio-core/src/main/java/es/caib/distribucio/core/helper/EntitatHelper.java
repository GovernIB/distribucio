/**
 * 
 */
package es.caib.distribucio.core.helper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.caib.distribucio.core.api.exception.PropietatNotFoundException;

/**
 * Mètodes comuns per la gestió del logo de l'entitat
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class EntitatHelper {

	@Autowired
	private ConfigHelper configHelper;
	
	public void createLogo(
			String entitatCodi,
			String logoExtension,
			byte[] logoCapBytes) {
		try {
//			## Genera el nom del nou logo i crea la carpeta pare per entitat
			String fileName =  "logo_" + entitatCodi + (logoExtension != "" ? "." + logoExtension : "");
			File fContent = new File(getLogosDir() + "/" + entitatCodi + "/" + fileName);
			fContent.getParentFile().mkdirs();
			
			if(fContent.getParentFile().listFiles() == null) 
				throw new RuntimeException("No s'ha pogut crear la ruta pel logo. Per favor, reviseu els permisos o canvieu el directori. ");
			
			
//			## Esborrar logos antics
			for(File file: fContent.getParentFile().listFiles()) 
			    if (!file.isDirectory()) 
			        file.delete();
			
//			## Guarda el nou logo
			FileOutputStream outContent = new FileOutputStream(fContent);
			InputStream logoCapIn = new ByteArrayInputStream(logoCapBytes);
			IOUtils.copy(logoCapIn, outContent);
			outContent.close();
		} catch (FileNotFoundException ex) {
			throw new RuntimeException("No s'ha pogut crear el logo a FileSystem", ex);
		} catch (IOException ex) {
			throw new RuntimeException("Hi ha hagut un error tancant el FileOutputStream", ex);
		}
	}
	
	public byte[] getLogo(String entitatCodi) {
		byte[] logoCapBytes = null;
		try {
//			## Recupera el logo de la carpeta pare
			File fFolder = new File(getLogosDir() + "/" + entitatCodi);
			File[] files = fFolder.listFiles();
			for (File file : files) {
				ByteArrayOutputStream streamLogo = new ByteArrayOutputStream();
				FileInputStream contingutIn = new FileInputStream(file);
				IOUtils.copy(contingutIn, streamLogo);
				logoCapBytes = streamLogo.toByteArray();
				break;
			}
			
			if (logoCapBytes == null) {
				throw new RuntimeException(
						"No s'ha trobat cap logo per l'entitat " + entitatCodi);
			}
		} catch (Exception ex) {
			throw new RuntimeException(
					"No s'ha pogut llegir el fitxer del logo (entitatCodi=" + entitatCodi + ")",
					ex);
		}
		return logoCapBytes;
	}
	
	public void removeLogos(String entitatCodi) {
		try {
//			## Esborra tots els logos de la carpeta de logos de l'entitat
			File fFolder = new File(getLogosDir() + "/" + entitatCodi);
//			## Esborrar logos
			if (fFolder.exists()) {
				for(File file: fFolder.listFiles()) 
				    if (!file.isDirectory()) 
				        file.delete();
			}
		} catch (Exception ex) {
			throw new RuntimeException(
					"No s'ha pogut esborrar la ruta de logos (entitatCodi=" + entitatCodi + ")",
					ex);
		}
	}
	
	private String getLogosDir() {
		String propertyNom = "es.caib.distribucio.entitat.logos.base.dir";
		String baseDir = configHelper.getConfig(propertyNom);
		if (baseDir == null || baseDir.isEmpty())
			throw new PropietatNotFoundException(propertyNom);
		return baseDir;
	}
	
}
