package es.caib.distribucio.logic.helper;

import java.util.function.IntSupplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.model.AclCache;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import es.caib.distribucio.logic.intf.exception.NotFoundException;
import es.caib.distribucio.persist.entity.UsuariEntity;
import es.caib.distribucio.persist.repository.AclSidRepository;
import es.caib.distribucio.persist.repository.AlertaRepository;
import es.caib.distribucio.persist.repository.AvisRepository;
import es.caib.distribucio.persist.repository.BackofficeRepository;
import es.caib.distribucio.persist.repository.BustiaDefaultRepository;
import es.caib.distribucio.persist.repository.ConfigRepository;
import es.caib.distribucio.persist.repository.ContingutComentariRepository;
import es.caib.distribucio.persist.repository.ContingutLogParamRepository;
import es.caib.distribucio.persist.repository.ContingutLogRepository;
import es.caib.distribucio.persist.repository.ContingutMovimentEmailRepository;
import es.caib.distribucio.persist.repository.ContingutMovimentRepository;
import es.caib.distribucio.persist.repository.ContingutRepository;
import es.caib.distribucio.persist.repository.DadaRepository;
import es.caib.distribucio.persist.repository.DominiRepository;
import es.caib.distribucio.persist.repository.EntitatRepository;
import es.caib.distribucio.persist.repository.MetaDadaRepository;
import es.caib.distribucio.persist.repository.MonitorIntegracioRepository;
import es.caib.distribucio.persist.repository.ProcedimentRepository;
import es.caib.distribucio.persist.repository.RegistreAnnexFirmaRepository;
import es.caib.distribucio.persist.repository.RegistreAnnexRepository;
import es.caib.distribucio.persist.repository.RegistreFirmaDetallRepository;
import es.caib.distribucio.persist.repository.RegistreInteressatRepository;
import es.caib.distribucio.persist.repository.RegistreRepository;
import es.caib.distribucio.persist.repository.ReglaRepository;
import es.caib.distribucio.persist.repository.ServeiRepository;
import es.caib.distribucio.persist.repository.UsuariBustiaFavoritRepository;
import es.caib.distribucio.persist.repository.UsuariRepository;

/**
 * Helper per canviar el codi d'un usuari: re-apunta al codi nou totes les referències a l'antic
 * (auditoria, permisos ACL i taules amb referència a l'usuari) i n'elimina l'usuari.
 * <p>
 * El comparteixen el servei antic ({@code AplicacioServiceImpl.updateUsuariCodi}, per a la interfície
 * JSP) i {@code UsuariResourceServiceImpl} (acció CANVI_CODIS de la interfície REACT).
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class UsuariCodiHelper {

	@Autowired
	private UsuariRepository usuariRepository;
	@Autowired
	private AclSidRepository aclSidRepository;
	@Autowired
	private CacheHelper cacheHelper;
	@Autowired
	private AclCache aclCache;

	@Autowired
	private BustiaDefaultRepository bustiaDefaultRepository;
	@Autowired
	private ContingutMovimentEmailRepository contingutMovimentEmailRepository;
	@Autowired
	private ContingutMovimentRepository contingutMovimentRepository;
	@Autowired
	private MonitorIntegracioRepository monitorIntegracioRepository;
	@Autowired
	private RegistreRepository registreRepository;
	@Autowired
	private AlertaRepository alertaRepository;
	@Autowired
	private AvisRepository avisRepository;
	@Autowired
	private BackofficeRepository backofficeRepository;
	@Autowired
	private ConfigRepository configRepository;
	@Autowired
	private ContingutRepository contingutRepository;
	@Autowired
	private ContingutComentariRepository contingutComentariRepository;
	@Autowired
	private ContingutLogRepository contingutLogRepository;
	@Autowired
	private ContingutLogParamRepository contingutLogParamRepository;
	@Autowired
	private DadaRepository dadaRepository;
	@Autowired
	private DominiRepository dominiRepository;
	@Autowired
	private EntitatRepository entitatRepository;
	@Autowired
	private MetaDadaRepository metaDadaRepository;
	@Autowired
	private ProcedimentRepository procedimentRepository;
	@Autowired
	private RegistreAnnexRepository registreAnnexRepository;
	@Autowired
	private RegistreAnnexFirmaRepository registreAnnexFirmaRepository;
	@Autowired
	private RegistreFirmaDetallRepository registreFirmaDetallRepository;
	@Autowired
	private RegistreInteressatRepository registreInteressatRepository;
	@Autowired
	private ReglaRepository reglaRepository;
	@Autowired
	private ServeiRepository serveiRepository;
	@Autowired
	private UsuariBustiaFavoritRepository usuariBustiaFavoritRepository;

	/** Indica si existeix un usuari amb el codi indicat. */
	@Transactional(readOnly = true)
	public boolean existeixUsuari(String codi) {
		return usuariRepository.findByCodi(codi) != null;
	}

	/**
	 * Canvia el codi d'un usuari. Cada crida s'executa en una transacció pròpia perquè, quan es
	 * processen diversos usuaris seguits, l'error d'un no desfaci els ja canviats.
	 *
	 * @return el nombre de registres modificats.
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW, timeout = 1200)
	public Long updateUsuariCodi(String codiAntic, String codiNou) {
		logger.trace("Actualitzant dades de l'usuari (codiAntic={}, codiNou={})", codiAntic, codiNou);
		UsuariEntity usuariAntic = usuariRepository.findByCodi(codiAntic);
		if (usuariAntic == null) {
			throw new NotFoundException(codiAntic, UsuariEntity.class);
		}

		UsuariEntity usuariNou = usuariRepository.findByCodi(codiNou);
		if (usuariNou == null) {
			cloneUsuari(codiNou, usuariAntic);
		}

		long registresModificats = 0L;

		// Actualitzam la informació de auditoria de les taules (createdby i lastmodifiedby):
		registresModificats += updateUsuariAuditoria(codiAntic, codiNou);

		// Actualitzam els permisos assignats per ACL
		registresModificats += updateUsuariPermisos(codiAntic, codiNou);

		// Actualitzam les referencis a l'usuari a taules:
		registresModificats += updateUsuariReferencies(codiAntic, codiNou);

		cacheHelper.evictUsuariByCodi(codiAntic);
		cacheHelper.evictUsuariByCodi(codiNou);
		cacheHelper.evictEntitatsAccessiblesUsuari(codiAntic);
		cacheHelper.evictEntitatsAccessiblesUsuari(codiNou);
		aclCache.clearCache();

		usuariRepository.delete(usuariAntic);

		return registresModificats;
	}

	private UsuariEntity cloneUsuari(
			String codiNou,
			UsuariEntity usuariAntic) {
		UsuariEntity usuariNou = UsuariEntity.getBuilder(
				codiNou,
				usuariAntic.getNom(),
				usuariAntic.getNif(),
				usuariAntic.getEmail(),
				usuariAntic.getEmailAlternatiu(),
				usuariAntic.getIdioma()).build();

		return usuariRepository.saveAndFlush(usuariNou);
	}

	private long updateUsuariAuditoria(String codiAntic, String codiNou) {
		logger.debug(">>> UPDATE USUARIS AUDITORIA:");
		long registresModificats = 0L;
		registresModificats += timed("Llista alertes", () -> alertaRepository.updateUsuariAuditoria(codiAntic, codiNou));
		registresModificats += timed("Llista avisos", () -> avisRepository.updateUsuariAuditoria(codiAntic, codiNou));
		registresModificats += timed("Llista backoffices", () -> backofficeRepository.updateUsuariAuditoria(codiAntic, codiNou));
		registresModificats += timed("Busties favorites", () -> usuariBustiaFavoritRepository.updateUsuariAuditoria(codiAntic, codiNou));
		registresModificats += timed("Llista configuracio", () -> configRepository.updateUsuariAuditoria(codiAntic, codiNou));
		registresModificats += timed("Taula contingut", () -> contingutRepository.updateUsuariAuditoria(codiAntic, codiNou));
		registresModificats += timed("Comentaris", () -> contingutComentariRepository.updateUsuariAuditoria(codiAntic, codiNou));
		registresModificats += timed("Historic contingut", () -> contingutLogRepository.updateUsuariAuditoria(codiAntic, codiNou));
		registresModificats += timed("Parametres historic contingut", () -> contingutLogParamRepository.updateUsuariAuditoria(codiAntic, codiNou));
		registresModificats += timed("Moviments registres", () -> contingutMovimentRepository.updateUsuariAuditoria(codiAntic, codiNou));
		registresModificats += timed("Email moviments registres", () -> contingutMovimentEmailRepository.updateUsuariAuditoria(codiAntic, codiNou));
		registresModificats += timed("Dades anotacions", () -> dadaRepository.updateUsuariAuditoria(codiAntic, codiNou));
		registresModificats += timed("Llista dominis", () -> dominiRepository.updateUsuariAuditoria(codiAntic, codiNou));
		registresModificats += timed("Llista entitats", () -> entitatRepository.updateUsuariAuditoria(codiAntic, codiNou));
		registresModificats += timed("Llista metadades", () -> metaDadaRepository.updateUsuariAuditoria(codiAntic, codiNou));
		registresModificats += timed("Llista anotacions", () -> registreRepository.updateUsuariAuditoria(codiAntic, codiNou));
		registresModificats += timed("Llista procediments", () -> procedimentRepository.updateUsuariAuditoria(codiAntic, codiNou));
		registresModificats += timed("Annexos anotacions", () -> registreAnnexRepository.updateUsuariAuditoria(codiAntic, codiNou));
		registresModificats += timed("Firma annexos", () -> registreAnnexFirmaRepository.updateUsuariAuditoria(codiAntic, codiNou));
		registresModificats += timed("Detall firma annexos", () -> registreFirmaDetallRepository.updateUsuariAuditoria(codiAntic, codiNou));
		registresModificats += timed("Interessats anotacions", () -> registreInteressatRepository.updateUsuariAuditoria(codiAntic, codiNou));
		registresModificats += timed("Llista regles", () -> reglaRepository.updateUsuariAuditoria(codiAntic, codiNou));
		registresModificats += timed("Llista serveis", () -> serveiRepository.updateUsuariAuditoria(codiAntic, codiNou));
		return registresModificats;
	}

	/**
	 * Passa els permisos ACL de l'usuari antic al nou. Si el nou encara no té SID n'hi basta amb canviar el
	 * codi del SID antic; si ja en té (unificació de dos usuaris) no es pot, per la restricció única
	 * sid + principal, i es repunten al SID del nou les entrades i els objectes de l'antic abans d'esborrar-lo.
	 */
	private long updateUsuariPermisos(String codiAntic, String codiNou) {
		logger.debug(">>> UPDATE USUARIS PERMISOS ACL:");
		Long sidAntic = aclSidRepository.findIdPrincipalBySid(codiAntic);
		if (sidAntic == null) {
			return 0L;
		}
		Long sidNou = aclSidRepository.findIdPrincipalBySid(codiNou);
		if (sidNou == null) {
			return timed("Permisos ACL", () -> aclSidRepository.updateUsuariPermis(codiAntic, codiNou));
		}
		long registresModificats = 0L;
		registresModificats += timed("Permisos ACL duplicats", () -> aclSidRepository.deleteEntriesDuplicades(sidAntic, sidNou));
		registresModificats += timed("Permisos ACL entrades", () -> aclSidRepository.updateEntriesSid(sidAntic, sidNou));
		registresModificats += timed("Permisos ACL propietari", () -> aclSidRepository.updateOwnerObjectIdentity(sidAntic, sidNou));
		registresModificats += timed("Permisos ACL SID antic", () -> aclSidRepository.deleteSidById(sidAntic));
		return registresModificats;
	}

	private long updateUsuariReferencies(String codiAntic, String codiNou) {
		logger.debug(">>> UPDATE USUARIS TAULES AMB REFERENCIES:");
		long registresModificats = 0L;
		// Si el nou ja té la mateixa bústia per defecte o favorita, la de l'antic sobra (restricció única).
		registresModificats += timed("Busties per defecte duplicades", () -> bustiaDefaultRepository.deleteDuplicatsUsuariCodi(codiAntic, codiNou));
		registresModificats += timed("Busties favorites duplicades", () -> usuariBustiaFavoritRepository.deleteDuplicatsUsuariCodi(codiAntic, codiNou));
		registresModificats += timed("Busties per defecte", () -> bustiaDefaultRepository.updateUsuariCodi(codiAntic, codiNou));
		registresModificats += timed("Busties favorites", () -> usuariBustiaFavoritRepository.updateUsuariCodi(codiAntic, codiNou));
		registresModificats += timed("Historic email agrupat", () -> contingutMovimentEmailRepository.updateUsuariCodi(codiAntic, codiNou));
		registresModificats += timed("Historic moviments", () -> contingutMovimentRepository.updateUsuariCodi(codiAntic, codiNou));
		registresModificats += timed("Monitor integracions", () -> monitorIntegracioRepository.updateUsuariCodi(codiAntic, codiNou));
		registresModificats += timed("Llista anotacions de registre", () -> registreRepository.updateUsuariCodi(codiAntic, codiNou));
		return registresModificats;
	}

	/** Executa l'actualització, en registra el temps i en retorna el nombre de registres modificats. */
	private int timed(String etiqueta, IntSupplier actualitzacio) {
		long t0 = System.currentTimeMillis();
		int registres = actualitzacio.getAsInt();
		logger.info("> {}: {} registres, {} ms", etiqueta, registres, System.currentTimeMillis() - t0);
		return registres;
	}

	private static final Logger logger = LoggerFactory.getLogger(UsuariCodiHelper.class);

}
