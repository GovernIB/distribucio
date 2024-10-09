/**
 * 
 */
package es.caib.distribucio.logic.service;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.distribucio.logic.helper.BustiaHelper;
import es.caib.distribucio.logic.helper.CacheHelper;
import es.caib.distribucio.logic.helper.ConfigHelper;
import es.caib.distribucio.logic.helper.ConversioTipusHelper;
import es.caib.distribucio.logic.helper.EntityComprovarHelper;
import es.caib.distribucio.logic.helper.ExcepcioLogHelper;
import es.caib.distribucio.logic.helper.PluginHelper;
import es.caib.distribucio.logic.intf.dto.BustiaDto;
import es.caib.distribucio.logic.intf.dto.ExcepcioLogDto;
import es.caib.distribucio.logic.intf.dto.UsuariDto;
import es.caib.distribucio.logic.intf.service.AplicacioService;
import es.caib.distribucio.persist.entity.BustiaDefaultEntity;
import es.caib.distribucio.persist.entity.BustiaEntity;
import es.caib.distribucio.persist.entity.EntitatEntity;
import es.caib.distribucio.persist.entity.UsuariEntity;
import es.caib.distribucio.persist.repository.AclSidRepository;
import es.caib.distribucio.persist.repository.BustiaDefaultRepository;
import es.caib.distribucio.persist.repository.UsuariRepository;
import es.caib.distribucio.plugin.usuari.DadesUsuari;

/**
 * Implementació dels mètodes per a gestionar la versió de l'aplicació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class AplicacioServiceImpl implements AplicacioService {

	private Properties versionProperties;

	@Resource
	private UsuariRepository usuariRepository;
	@Resource
	private AclSidRepository aclSidRepository;

	@Resource
	private CacheHelper cacheHelper;
	@Resource
	private PluginHelper pluginHelper;
	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	@Resource
	private ExcepcioLogHelper excepcioLogHelper;
	@Autowired
	private ConfigHelper configHelper;
	@Autowired
	private EntityComprovarHelper entityComprovarHelper;
	@Autowired
	private BustiaDefaultRepository bustiaDefaultRepository;
	@Autowired
	private BustiaHelper bustiaHelper;
	
	@Override
	public String getVersioActual() {
		logger.trace("Obtenint versió actual de l'aplicació");
		try {
			return getVersionProperties().getProperty("app.version");
		} catch (IOException ex) {
			logger.error("No s'ha pogut llegir el fitxer version.properties", ex);
			return "???";
		}
	}
	
	@Override
	public String getVersioData() {
		logger.trace("Obtenint data de l'aplicació");
		try {
			return getVersionProperties().getProperty("app.date");
		} catch (IOException ex) {
			logger.error("No s'ha pogut llegir el fitxer version.properties", ex);
			return "???";
		}
	}

	@Transactional
	@Override
	public void processarAutenticacioUsuari() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		logger.trace("Processant autenticació (usuariCodi=" + auth.getName() + ")");
		UsuariEntity usuari = usuariRepository.findById(auth.getName()).orElse(null);
		if (usuari == null) {
			logger.trace("Consultant plugin de dades d'usuari (" +
					"usuariCodi=" + auth.getName() + ")");
			String idioma = configHelper.getConfig("es.caib.distribucio.default.user.language");
			if (idioma == null || idioma.trim().isEmpty()) {
				idioma = "ca";
			}
			DadesUsuari dadesUsuari = cacheHelper.findUsuariAmbCodi(auth.getName());
			if (dadesUsuari != null) {
				usuari = usuariRepository.save(
						UsuariEntity.getBuilder(
								dadesUsuari.getCodi(),
								dadesUsuari.getNomSencer(),
								dadesUsuari.getNif(),
								dadesUsuari.getEmail(),
								null,
								idioma).build());
			} else {
				// Pot ser que sigui un usuari d'integració sense dades d'usuari
				usuari = usuariRepository.save(
						UsuariEntity.getBuilder(
								auth.getName(),
								auth.getName(),
								null,
								null,
								null,
								idioma).build());
			}
		} else {
			logger.trace("Consultant plugin de dades d'usuari (" +
					"usuariCodi=" + auth.getName() + ")");
			DadesUsuari dadesUsuari = cacheHelper.findUsuariAmbCodi(auth.getName());
			if (dadesUsuari == null) {
				// Pot ser que sigui un usuari d'integracio
				dadesUsuari = new DadesUsuari(
						usuari.getCodi(), 
						usuari.getNom(),
						usuari.getNom(), 
						null, 
						usuari.getNif(),
						usuari.getEmailAlternatiu() != null ? usuari.getEmailAlternatiu() : usuari.getEmail(),
						true);
			}
			if (dadesUsuari.getNomSencer() != null) {
				usuari.update(
						dadesUsuari.getNomSencer(), 
						dadesUsuari.getNif(), 
						dadesUsuari.getEmail());
			} else {
				usuari.update(
						dadesUsuari.getNom() + " " + dadesUsuari.getLlinatges(),
						dadesUsuari.getNif(),
						dadesUsuari.getEmail());
			}
			cacheHelper.evictUsuariByCodi(dadesUsuari.getCodi());
		}
	}

	@Transactional(readOnly = true)
	@Override
	public UsuariDto getUsuariActual() {
		UsuariDto usuari = null;
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null) {
			logger.trace("Obtenint usuari actual \"" + auth.getName() + "\"");
			return toUsuariDtoAmbRols(
					usuariRepository.findByCodi(auth.getName()));
		}
		return usuari;
	}
	
	@Transactional
	@Override
	public UsuariDto updateUsuariActual(UsuariDto dto, Long entitatId) {
		logger.trace("Actualitzant configuració de usuari actual");
		EntitatEntity entitatActual = entityComprovarHelper.comprovarEntitat(
				entitatId, 
				false, 
				false, 
				false);
		UsuariEntity usuari = usuariRepository.getReferenceById(dto.getCodi());
		
		usuari.updateEmailAlternatiu(dto.getEmailAlternatiu());
		
		usuari.update(
				dto.getRebreEmailsBustia(), 
				dto.getRebreEmailsAgrupats(),
				dto.getIdioma());
		BustiaDefaultEntity bustiaDefaultEntity = bustiaDefaultRepository.findByEntitatAndUsuari(
				entitatActual, 
				usuari);
		
		if (dto.getBustiaPerDefecte() != null) {
			BustiaEntity bustiaPerDefecte = entityComprovarHelper.comprovarBustia(
					entitatActual, 
					dto.getBustiaPerDefecte(), 
					true);
//			Crear o actualitzar
			if (bustiaDefaultEntity != null) {
				bustiaDefaultEntity.updateBustiaDefault(bustiaPerDefecte);
			} else {
//				Guarda la bústia per defecte per entitat
				BustiaDefaultEntity bustiaPerDefecteEntity = BustiaDefaultEntity.getBuilder(
						entitatActual, 
						bustiaPerDefecte, 
						usuari).build();
				bustiaDefaultRepository.save(bustiaPerDefecteEntity);
			}
		} else if (bustiaDefaultEntity != null) {
			bustiaDefaultRepository.delete(bustiaDefaultEntity);
		}
		return toUsuariDtoAmbRols(usuari);
	}

	@Transactional(readOnly = true)
	@Override
	public UsuariDto findUsuariAmbCodi(String codi) {
		logger.trace("Obtenint usuari amb codi (codi=" + codi + ")");
		return conversioTipusHelper.convertir(
				usuariRepository.findById(codi).orElse(null),
				UsuariDto.class);
	}

	@Transactional(readOnly = true)
	@Override
	public List<UsuariDto> findUsuariAmbText(String text) {
		logger.trace("Consultant usuaris amb text (text=" + text + ")");
		return conversioTipusHelper.convertirList(
				usuariRepository.findByText(text != null? text : ""),
				UsuariDto.class);
	}
	
	@Transactional(readOnly = true)
	@Override
	public List<UsuariDto> findUsuariAmbCodiAndNom(String text) {
		logger.trace("Consultant usuaris per codi o nom amb text (text=" + text + ")");
		return conversioTipusHelper.convertirList(
				usuariRepository.findByCodiAndNom(text != null? text : ""),
				UsuariDto.class);
	}

	@Override
	public void excepcioSave(Throwable exception) {
		logger.trace("Emmagatzemant excepció (" +
				"exception=" + exception + ")");
		excepcioLogHelper.addExcepcio(exception);
	}

	@Override
	public ExcepcioLogDto excepcioFindOne(Long index) {
		logger.trace("Consulta d'una excepció (index=" + index + ")");
		return excepcioLogHelper.findAll().get(index.intValue());
	}

	@Override
	public List<ExcepcioLogDto> excepcioFindAll() {
		logger.trace("Consulta de les excepcions disponibles");
		return excepcioLogHelper.findAll();
	}

	@Override
	public List<String> permisosFindRolsDistinctAll() {
		logger.trace("Consulta dels rols definits a les ACLs");
		return aclSidRepository.findSidByPrincipalFalse();
	}

	@Override
	public boolean isPluginArxiuActiu() {
		logger.trace("Consulta si el plugin d'arxiu està actiu");
		return pluginHelper.isArxiuPluginActiu();
	}

	@Override
	public String propertyBaseUrl() {
		logger.trace("Consulta de la propietat base URL");
		return configHelper.getConfig("es.caib.distribucio.base.url");
	}

	@Override
	public String propertyPluginPassarelaFirmaIgnorarModalIds() {
		logger.trace("Consulta de la propietat amb les ids pels plugins de passarela de firma");
		return configHelper.getConfig("plugin.passarelafirma.ignorar.modal.ids");
	}

	@Override
	public Properties propertyFindByPrefix(String prefix) {
		logger.trace("Consulta del valor dels properties amb prefix (" +
				"prefix=" + prefix + ")");
		Properties environmentProperties = configHelper.getAllEnvironmentProperties();
		Properties filteredProperties = new Properties();
		for (Object key: environmentProperties.keySet()) {
			if (key instanceof String && ((String)key).startsWith(prefix)) {
				filteredProperties.put(key, environmentProperties.get(key));
			}
		}
		return filteredProperties;
	}

	@Override
	public String propertyFindByNom(String nom) {
		logger.trace("Consulta del valor del propertat amb nom");
		return configHelper.getConfig(nom);
	}
	
	@Transactional(readOnly = true)
	@Override
	public BustiaDto getBustiaPerDefecte(UsuariDto dto, Long entitatId) {
		logger.trace("Recuperant la bústia per defecte de l'usuari actual (" + 
						"entitatId=" + entitatId + 
						"usuariCodi=" + dto.getCodi() + ")");
		EntitatEntity entitatActual = entityComprovarHelper.comprovarEntitat(
				entitatId, 
				false, 
				false, 
				false);
		BustiaDto bustia = null;
		UsuariEntity usuari = usuariRepository.getReferenceById(dto.getCodi());
		BustiaDefaultEntity bustiaDefaultEntity = bustiaDefaultRepository.findByEntitatAndUsuari(
				entitatActual, 
				usuari);
		if (bustiaDefaultEntity != null)
			bustia = bustiaHelper.toBustiaDto(
						bustiaDefaultEntity.getBustia(),
						false,
						false,
						false);
		return bustia;
	}

	@Transactional
	@Override
	public void setRolUsuariActual(String rolActual) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		logger.debug("Actualitzant rol de usuari actual");
		UsuariEntity usuari = usuariRepository.getReferenceById(auth.getName());
		usuari.updateRolActual(rolActual);
	}

	@Override
	public Authentication getAuthentication() {
		return SecurityContextHolder.getContext().getAuthentication();
	}

	private Properties getVersionProperties() throws IOException {
		if (versionProperties == null) {
			versionProperties = new Properties();
			versionProperties.load(
					getClass().getResourceAsStream(
							"/es/caib/distribucio/core/version/version.properties"));
		}
		return versionProperties;
	}

	private UsuariDto toUsuariDtoAmbRols(
			UsuariEntity usuari) {
		UsuariDto dto = conversioTipusHelper.convertir(
				usuari,
				UsuariDto.class);
		if (dto != null) {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			if (auth.getAuthorities() != null) {
				String[] rols = new String[auth.getAuthorities().size()];
				int index = 0;
				for (GrantedAuthority grantedAuthority: auth.getAuthorities()) {
					rols[index++] = grantedAuthority.getAuthority();
				}
				dto.setRols(rols);
			}
		}
		return dto;
	}

	@Override
	@Transactional
	public UsuariDto updateUsuari(String codi) {
		UsuariDto usuari = null;
		if (codi != null && !codi.isEmpty()) {
			logger.trace("Actualitzant dades de l'usuari (codi=" + codi + ")");
			UsuariEntity usuariEntity = usuariRepository.findById(codi).orElse(null);
			if (usuariEntity != null) {
				logger.trace("Consultant plugin de dades d'usuari (" +
						"usuariCodi=" + codi + ")");
				DadesUsuari dadesUsuari = pluginHelper.dadesUsuariFindAmbCodi(codi);
				if (dadesUsuari != null) {
					usuariEntity.update(
							dadesUsuari.getNomSencer(), 
							dadesUsuari.getNif(), 
							dadesUsuari.getEmail());
				} else {
					// No hi ha dades d'usuari, pot ser un usuari d'integració
					usuariEntity.update(
							codi, 
							null, 
							null);
				}
				usuari = toUsuariDtoAmbRols(usuariEntity);
				cacheHelper.evictUsuariByCodi(dadesUsuari.getCodi());
			}
		}
		return usuari;
	}

	private static final Logger logger = LoggerFactory.getLogger(AplicacioServiceImpl.class);
}
