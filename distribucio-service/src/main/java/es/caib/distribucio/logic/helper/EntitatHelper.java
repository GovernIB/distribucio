/**
 * 
 */
package es.caib.distribucio.logic.helper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import es.caib.distribucio.logic.intf.dto.EntitatDto;
import es.caib.distribucio.logic.intf.dto.PermisDto;
import es.caib.distribucio.logic.intf.exception.PropietatNotFoundException;
import es.caib.distribucio.persist.entity.EntitatEntity;
import es.caib.distribucio.persist.repository.EntitatRepository;

/**
 * Mètodes comuns per la gestió del logo de l'entitat
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class EntitatHelper {

	@Autowired private ConfigHelper configHelper;
	@Autowired private CacheHelper cacheHelper;
	@Autowired private EntitatRepository entitatRepository;
	@Autowired private EntityComprovarHelper entityComprovarHelper;
	@Autowired private PermisosHelper permisosHelper;

	public void createLogo(
			String entitatCodi,
			String logoExtension,
			byte[] logoCapBytes) {
		try {
			// Genera el nom del nou logo i crea la carpeta pare per entitat
			String fileName =  "logo_" + entitatCodi + (logoExtension != "" ? "." + logoExtension : "");
			File fContent = new File(getLogosDir() + "/" + entitatCodi + "/" + fileName);
			fContent.getParentFile().mkdirs();
			
			if(fContent.getParentFile().listFiles() == null) 
				throw new RuntimeException("No s'ha pogut crear la ruta pel logo. Per favor, reviseu els permisos o canvieu el directori. ");
			
			
			// Esborrar logos antics
			for (File file: fContent.getParentFile().listFiles()) {
				if (!file.isDirectory()) {
					file.delete();
				}
			}
			// Guarda el nou logo
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
			// Recupera el logo de la carpeta pare
			File fFolder = new File(getLogosDir() + "/" + entitatCodi);
			File[] files = fFolder.listFiles();
			if (files != null) {
				for (File file: files) {
					ByteArrayOutputStream streamLogo = new ByteArrayOutputStream();
					FileInputStream contingutIn = new FileInputStream(file);
					IOUtils.copy(contingutIn, streamLogo);
					logoCapBytes = streamLogo.toByteArray();
					break;
				}
			}
			if (logoCapBytes == null) {
				logger.warn("No s'ha trobat cap logo per l'entitat " + entitatCodi);
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
			// Esborra tots els logos de la carpeta de logos de l'entitat
			File fFolder = new File(getLogosDir() + "/" + entitatCodi);
			// Esborrar logos
			if (fFolder.exists()) {
				for (File file: fFolder.listFiles()) {
					if (!file.isDirectory()) {
						file.delete();
					}
				}
			}
		} catch (Exception ex) {
			throw new RuntimeException(
					"No s'ha pogut esborrar la ruta de logos (entitatCodi=" + entitatCodi + ")",
					ex);
		}
	}
	
	/** Consulta el codi d'entitat per un id d'entitat. */
	@Transactional(readOnly = true)
	public String getCodiEntitat(Long entitatId) {
		return entitatRepository.getCodiEntitatPerId(entitatId);
	}

	/** Consulta el codi d'entitat per una anotació concreta. */
	@Transactional(readOnly = true)
	public String getCodiEntitatRegistre(Long anotacioId) {
		return entitatRepository.getCodiEntitatPerAnotacioId(anotacioId);
	}

	public List<EntitatDto> findAccessiblesUsuariActual() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		List<EntitatDto> entitats;
		if (auth != null) {
			String authUserName = auth.getName();
			logger.trace("Consulta les entitats accessibles per l'usuari actual (" +
					"usuari=" + authUserName + ")");
			entitats = cacheHelper.findEntitatsAccessiblesUsuari(
					authUserName,
					getRolsClauCache(auth));
		} else {
			logger.trace("Consulta de les entitats per l'usuari actual sense usuari autenticat.");
			entitats = new ArrayList<EntitatDto>();
		}
		return entitats;
	}
	
	public void updatePermisSuper(
			Long id,
			PermisDto permis) {
		logger.debug("Modificació com a superusuari del permis de l'entitat (" +
				"id=" + id + ", " +
				"permis=" + permis + ")");
		entityComprovarHelper.comprovarEntitat(
				id,
				false,
				false,
				false);
		permisosHelper.updatePermis(
				id,
				EntitatEntity.class,
				permis);
	}
	
	public void deletePermisSuper(
			Long id,
			Long permisId) {
		logger.debug("Eliminació com a superusuari del permis de l'entitat (" +
				"id=" + id + ", " +
				"permisId=" + permisId + ")");
		entityComprovarHelper.comprovarEntitat(
				id,
				false,
				false,
				false);
		permisosHelper.deletePermis(
				id,
				EntitatEntity.class,
				permisId);
	}
	
	public List<PermisDto> findPermisSuper(Long id) {
		logger.debug("Consulta com a superusuari dels permisos de l'entitat (" +
				"id=" + id + ")");
		entityComprovarHelper.comprovarEntitat(
				id,
				false,
				false,
				false);
		return permisosHelper.findPermisos(
				id,
				EntitatEntity.class);
	}
	
	/**
	 * Rols de l'autenticacio, ordenats i concatenats, per a formar part de la clau de la cache
	 * d'entitats accessibles (veure {@code CacheHelper.findEntitatsAccessiblesUsuari}).
	 */
	private String getRolsClauCache(Authentication auth) {
		return auth.getAuthorities().stream().
				map(GrantedAuthority::getAuthority).
				sorted().
				collect(Collectors.joining(","));
	}
	
	private String getLogosDir() {
		String propertyNom = "es.caib.distribucio.entitat.logos.base.dir";
		String baseDir = configHelper.getConfig(propertyNom);
		if (baseDir == null || baseDir.isEmpty())
			throw new PropietatNotFoundException(propertyNom);
		return baseDir;
	}

	private static final Logger logger = LoggerFactory.getLogger(EntitatHelper.class);

}
