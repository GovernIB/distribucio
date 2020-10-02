/**
 * 
 */
package es.caib.distribucio.core.helper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

import es.caib.distribucio.core.api.dto.BustiaDto;
import es.caib.distribucio.core.api.dto.ContingutDto;
import es.caib.distribucio.core.api.dto.EntitatDto;
import es.caib.distribucio.core.api.dto.LogTipusEnumDto;
import es.caib.distribucio.core.api.dto.PermisDto;
import es.caib.distribucio.core.api.dto.RegistreAnnexDto;
import es.caib.distribucio.core.api.dto.RegistreDto;
import es.caib.distribucio.core.api.dto.RegistreProcesEstatSimpleEnumDto;
import es.caib.distribucio.core.api.dto.UnitatOrganitzativaDto;
import es.caib.distribucio.core.api.dto.UsuariDto;
import es.caib.distribucio.core.api.dto.UsuariPermisDto;
import es.caib.distribucio.core.api.registre.RegistreInteressat;
import es.caib.distribucio.core.api.registre.RegistreInteressatTipusEnum;
import es.caib.distribucio.core.api.registre.RegistreProcesEstatEnum;
import es.caib.distribucio.core.entity.BustiaEntity;
import es.caib.distribucio.core.entity.ContingutEntity;
import es.caib.distribucio.core.entity.ContingutLogEntity;
import es.caib.distribucio.core.entity.ContingutMovimentEntity;
import es.caib.distribucio.core.entity.EntitatEntity;
import es.caib.distribucio.core.entity.RegistreAnnexEntity;
import es.caib.distribucio.core.entity.RegistreAnnexFirmaEntity;
import es.caib.distribucio.core.entity.RegistreEntity;
import es.caib.distribucio.core.entity.RegistreFirmaDetallEntity;
import es.caib.distribucio.core.entity.RegistreInteressatEntity;
import es.caib.distribucio.core.entity.UnitatOrganitzativaEntity;
import es.caib.distribucio.core.entity.UsuariEntity;
import es.caib.distribucio.core.repository.ContingutComentariRepository;
import es.caib.distribucio.core.repository.ContingutLogRepository;
import es.caib.distribucio.core.repository.ContingutMovimentRepository;
import es.caib.distribucio.core.repository.ContingutRepository;
import es.caib.distribucio.core.repository.RegistreFirmaDetallRepository;
import es.caib.distribucio.core.security.ExtendedPermission;
import es.caib.distribucio.plugin.usuari.DadesUsuari;

/**
 * Utilitat per a gestionar contenidors.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class ContingutHelper {

	@Autowired
	private ContingutRepository contingutRepository;
	@Autowired
	private ContingutLogRepository contingutLogRepository;
	@Autowired
	private ContingutMovimentRepository contenidorMovimentRepository;
	@Autowired
	private ContingutComentariRepository contingutComentariRepository;
	@Autowired
	private ConversioTipusHelper conversioTipusHelper;
	@Autowired
	private PluginHelper pluginHelper;
	@Autowired
	private RegistreHelper registreHelper;
	@Autowired
	private PermisosHelper permisosHelper;
	@Autowired
	private UsuariHelper usuariHelper;
	@Autowired
	private UnitatOrganitzativaHelper unitatOrganitzativaHelper;	
	@Autowired
	private MetricRegistry metricRegistry;
	@Resource
	private CacheHelper cacheHelper;
	@Autowired
	RegistreFirmaDetallRepository registreFirmaDetallRepository;

	public ContingutDto toContingutDto(
			ContingutEntity contingut) {
		return toContingutDto(
				contingut,
				false,
				false,
				false,
				false,
				false,
				false,
				true);
	}
	public ContingutDto toContingutDto(
			ContingutEntity contingut,
			boolean ambPermisos,
			boolean ambFills,
			boolean filtrarFillsSegonsPermisRead,
			boolean ambDades,
			boolean ambPath,
	//		boolean pathNomesFinsExpedientArrel,
			boolean ambVersions,
			boolean ambUnitatOrganitzativa) {
		final Timer timerTotal = metricRegistry.timer(MetricRegistry.name(ContingutHelper.class, "toContingutDto"));
		Timer.Context contextTotal = timerTotal.time();
		
		ContingutDto contingutDto = null;
		ContingutEntity deproxied = HibernateHelper.deproxy(contingut);
		
		// ########################################### BUSTIA ####################################################
		if (deproxied instanceof BustiaEntity) {
			BustiaEntity bustiaEntity = (BustiaEntity)deproxied;
			BustiaDto bustiaDto = new BustiaDto();
			bustiaDto.setUnitatCodi(bustiaEntity.getUnitatOrganitzativa().getCodi());
			bustiaDto.setActiva(bustiaEntity.isActiva());
			bustiaDto.setPerDefecte(bustiaEntity.isPerDefecte());

			if (ambUnitatOrganitzativa) {
				
				final Timer timerToContingutDtoUnitatOrganitzativa = metricRegistry.timer(MetricRegistry.name(ContingutHelper.class, "toContingutDto.UnitatOrganitzativa"));
				Timer.Context contextToContingutDtoUnitatOrganitzativa  = timerToContingutDtoUnitatOrganitzativa.time();
				
				UnitatOrganitzativaDto unitatConselleria = unitatOrganitzativaHelper.findConselleria(
						bustiaEntity.getEntitat().getCodiDir3(),
						bustiaEntity.getUnitatOrganitzativa().getCodi());
				if (unitatConselleria != null) {
					bustiaDto.setUnitatConselleriaCodi(unitatConselleria.getCodi());
				}
				UnitatOrganitzativaEntity unitatEntity = bustiaEntity.getUnitatOrganitzativa();
				UnitatOrganitzativaDto unitatDto = conversioTipusHelper.convertir(unitatEntity,
						UnitatOrganitzativaDto.class);
				unitatDto = UnitatOrganitzativaHelper.assignAltresUnitatsFusionades(unitatEntity,
						unitatDto);

				bustiaDto.setUnitatOrganitzativa(unitatDto);
				bustiaDto.setUnitatCodi(bustiaEntity.getUnitatOrganitzativa().getCodi());
				
				contextToContingutDtoUnitatOrganitzativa.stop();
			}

			contingutDto = bustiaDto;
			
		// ########################################### REGISTRE ####################################################	
		} else if (deproxied instanceof RegistreEntity) {
			RegistreEntity registreEntity = (RegistreEntity)deproxied;
			
			final Timer timerToContingutDtoconvertirToRegistreDto = metricRegistry.timer(MetricRegistry.name(ContingutHelper.class, "toContingutDto.convertirToRegistreDto"));
			Timer.Context contextToContingutDtoconvertirToRegistreDto  = timerToContingutDtoconvertirToRegistreDto.time();
			RegistreDto registreDto = conversioTipusHelper.convertir(
						registreEntity,
						RegistreDto.class);
			registreDto.setLlegida(registreEntity.getLlegida() == null || registreEntity.getLlegida());
			contextToContingutDtoconvertirToRegistreDto.stop();
			
			// Traiem el justificant de la llista d'annexos si té el mateix id o uuid
			for (RegistreAnnexDto annexDto : registreDto.getAnnexos()) {
				if ((registreDto.getJustificant() != null && registreDto.getJustificant().getId().equals(annexDto.getId()))
						|| registreDto.getJustificantArxiuUuid() != null && registreDto.getJustificantArxiuUuid().equals(annexDto.getFitxerArxiuUuid()) ) {
					registreDto.getAnnexos().remove(annexDto);
					break;
				}
			}
			
			// toBustiaContingut 
			if (registreEntity.getPare() != null) {
				registreDto.setPareId(registreEntity.getPare().getId());
				ContingutEntity contingutPareDeproxied = HibernateHelper.deproxy(registreEntity.getPare());
				registreDto.setBustiaActiva(((BustiaEntity)contingutPareDeproxied).isActiva());				
			}
			
			if (registreEntity.getProcesEstat() == RegistreProcesEstatEnum.ARXIU_PENDENT || registreEntity.getProcesEstat() == RegistreProcesEstatEnum.REGLA_PENDENT || registreEntity.getProcesEstat() == RegistreProcesEstatEnum.BUSTIA_PENDENT) {
				registreDto.setProcesEstatSimple(RegistreProcesEstatSimpleEnumDto.PENDENT);
			} else if (registreEntity.getProcesEstat() == RegistreProcesEstatEnum.BUSTIA_PROCESSADA || registreEntity.getProcesEstat() == RegistreProcesEstatEnum.BACK_PENDENT || registreEntity.getProcesEstat() == RegistreProcesEstatEnum.BACK_REBUDA || registreEntity.getProcesEstat() == RegistreProcesEstatEnum.BACK_PROCESSADA || registreEntity.getProcesEstat() == RegistreProcesEstatEnum.BACK_REBUTJADA || registreEntity.getProcesEstat() == RegistreProcesEstatEnum.BACK_ERROR) {
				registreDto.setProcesEstatSimple(RegistreProcesEstatSimpleEnumDto.PROCESSAT);
			}

			registreDto.setNumeroOrigen(registreEntity.getNumeroOrigen());
			registreDto.setNumComentaris(contingutComentariRepository.countByContingut(registreEntity));
			// toBustiaContingut //
			
			// Enviaments via email
			if (registreEntity.isEnviatPerEmail()) {
				List<ContingutLogEntity> logs = contingutLogRepository.findByContingutOrderByCreatedDateAsc(
						contingut);
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
				for(ContingutLogEntity log : logs ) {
					if (LogTipusEnumDto.ENVIAMENT_EMAIL.equals(log.getTipus()) ) {
						// Cadena tipus dd/MM/yyyy HH:mm:ss Destinataris: email@email.com
						registreDto.getEnviamentsPerEmail().add(sdf.format(log.getCreatedDate().toDate()) + " " + log.getParam2());
					}
				}

			}
			
			contingutDto = registreDto;
		}
		
		// ########################################### CONTINGUT ####################################################
		contingutDto.setId(contingut.getId());
		contingutDto.setNom(contingut.getNom());
			contingutDto.setEsborrat(contingut.getEsborrat());
			contingutDto.setArxiuUuid(contingut.getArxiuUuid());
			contingutDto.setArxiuDataActualitzacio(contingut.getArxiuDataActualitzacio());
			
			final Timer timerToContingutDtoEntitatMoviemntAudtioria = metricRegistry.timer(MetricRegistry.name(ContingutHelper.class, "toContingutDto.EntitatMoviemntAudtioria"));
			Timer.Context contextToContingutDtoEntitatMoviemntAudtioria  = timerToContingutDtoEntitatMoviemntAudtioria.time();
			contingutDto.setEntitat(
					conversioTipusHelper.convertir(
							contingut.getEntitat(),
							EntitatDto.class));
			contingutDto.setAlerta(!contingut.getAlertesNoLlegides().isEmpty());
			// DARRER MOVIMENT
			if (contingut.getDarrerMoviment() != null) {
				ContingutMovimentEntity darrerMoviment = contingut.getDarrerMoviment();
				contingutDto.setDarrerMovimentUsuari(
						conversioTipusHelper.convertir(
								darrerMoviment.getRemitent(),
								UsuariDto.class));
				contingutDto.setDarrerMovimentData(darrerMoviment.getCreatedDate().toDate());
				contingutDto.setDarrerMovimentComentari(darrerMoviment.getComentari());
			}
			// AUDITORIA
			contingutDto.setCreatedBy(
					conversioTipusHelper.convertir(
							contingut.getCreatedBy(),
							UsuariDto.class));
			contingutDto.setCreatedDate(contingut.getCreatedDate().toDate());
			contingutDto.setLastModifiedBy(
					conversioTipusHelper.convertir(
							contingut.getLastModifiedBy(),
							UsuariDto.class));
			contingutDto.setLastModifiedDate(contingut.getLastModifiedDate().toDate());
			contextToContingutDtoEntitatMoviemntAudtioria.stop();
			
			// PATH
			if (ambPath) {
				List<ContingutDto> path = getPathContingutComDto(
						contingut,
						ambPermisos);
				contingutDto.setPath(path);
			}
			// FILLS
			if (ambFills) {
				List<ContingutDto> contenidorDtos = new ArrayList<ContingutDto>();
				List<ContingutEntity> fills = contingutRepository.findByPareAndEsborrat(
						contingut,
						0,
						new Sort("createdDate"));
				List<ContingutDto> fillPath = null;
				if (ambPath) {
					fillPath = new ArrayList<ContingutDto>();
					if (contingutDto.getPath() != null)
						fillPath.addAll(contingutDto.getPath());
					fillPath.add(toContingutDto(
							contingut,
							false,
							false,
							false,
							false,
							false,
							false,
							true));
				}
				for (ContingutEntity fill: fills) {
					if (fill.getEsborrat() == 0) {
						ContingutDto fillDto = toContingutDto(
								fill,
								ambPermisos,
								false,
								false,
								false,
								false,
								false,
								true);
						// Configura el pare de cada fill
						fillDto.setPath(fillPath);
						contenidorDtos.add(fillDto);
					}
				}
				contingutDto.setFills(contenidorDtos);
			}

		contextTotal.stop();
		return contingutDto;
	}

	public void comprovarPermisosContingut(
			ContingutEntity contingut,
			boolean comprovarPermisRead,
			boolean comprovarPermisWrite,
			boolean comprovarPermisDelete) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		// Comprova els permisos del contenidor actual
		if (contingut instanceof BustiaEntity) {
			BustiaEntity bustia = (BustiaEntity)contingut;
			if (bustia.getPare() != null && comprovarPermisRead) {
				boolean granted = permisosHelper.isGrantedAll(
						bustia.getId(),
						BustiaEntity.class,
						new Permission[] {ExtendedPermission.READ},
						auth);
				if (!granted) {
					logger.debug("Sense permisos per a accedir a la bústia ("
							+ "id=" + bustia.getId() + ", "
							+ "usuari=" + auth.getName() + ")");
					throw new SecurityException("Sense permisos per accedir a la bústia ("
							+ "id=" + bustia.getId() + ", "
							+ "usuari=" + auth.getName() + ")");
				}
			}
		}
	}
	
	public void comprovarPermisosPathContingut(
			ContingutEntity contingut,
			boolean comprovarPermisRead,
			boolean comprovarPermisWrite,
			boolean comprovarPermisDelete,
			boolean incloureActual) {
		List<ContingutEntity> path = getPathContingut(contingut);
		if (path != null) {
			// Dels contenidors del path només comprova el permis read
			for (ContingutEntity contingutPath: path) {
				// Si el contingut està agafat per un altre usuari no es
				// comproven els permisos de l'escriptori perquè òbviament
				// els altres usuaris no hi tendran accés.
				comprovarPermisosContingut(
						contingutPath,
						true,
						false,
						false);
			}
		}
		if (incloureActual) {
			// Del contenidor en qüestió comprova tots els permisos
			comprovarPermisosContingut(
					contingut,
					comprovarPermisRead,
					comprovarPermisWrite,
					comprovarPermisDelete);
		}
	}
	public void comprovarPermisosFinsExpedientArrel(
			ContingutEntity contingut,
			boolean comprovarPermisRead,
			boolean comprovarPermisWrite,
			boolean comprovarPermisDelete,
			boolean incloureActual) {
		List<ContingutEntity> path = getPathContingut(contingut);
		if (incloureActual || path != null) {
			if (incloureActual) {
				if (path == null)
					path = new ArrayList<ContingutEntity>();
				path.add(contingut);
			}
			boolean expedientArrelTrobat = false;
			for (ContingutEntity contingutPath: path) {
				if (expedientArrelTrobat) {
					comprovarPermisosContingut(
							contingutPath,
							comprovarPermisRead,
							comprovarPermisWrite,
							comprovarPermisDelete);
				}
			}
		}
	}

	public Set<String> findUsuarisCodisAmbPermisReadPerContenidor(
			ContingutEntity contingut) {
		List<PermisDto> permisos = new ArrayList<PermisDto>();
		if (contingut instanceof BustiaEntity) {
			permisos = permisosHelper.findPermisos(
					contingut.getId(),
					BustiaEntity.class);
		}
		Set<String> usuaris = new HashSet<String>();
		for (PermisDto permis: permisos) {
			switch (permis.getPrincipalTipus()) {
			case USUARI:
				usuaris.add(permis.getPrincipalNom());
				break;
			case ROL:
				List<DadesUsuari> usuarisGrup = pluginHelper.dadesUsuariFindAmbGrup(
						permis.getPrincipalNom());
				if (usuarisGrup != null) {
					for (DadesUsuari usuariGrup: usuarisGrup) {
						usuaris.add(usuariGrup.getCodi());
					}
				}
				break;
			}
		}
		return usuaris;
	}
	
	public List<UsuariPermisDto> findUsuarisAmbPermisReadPerContenidor(
			ContingutEntity contingut) {
		List<PermisDto> permisos = new ArrayList<PermisDto>();
		if (contingut instanceof BustiaEntity) {
			permisos = permisosHelper.findPermisos(contingut.getId(),
					BustiaEntity.class);
		}
		List<UsuariPermisDto> usuaris = new ArrayList<UsuariPermisDto>();
		for (PermisDto permis : permisos) {
			switch (permis.getPrincipalTipus()) {
			case USUARI:
				UsuariPermisDto usuariUserPermisDto = null;
				for (UsuariPermisDto usuari : usuaris) {
					if (usuari.getCodi().equals(permis.getPrincipalNom())) {
						usuariUserPermisDto = usuari;
					}
				}
				if (usuariUserPermisDto != null) { // if already exists
					usuariUserPermisDto.setHasUsuariPermission(true);
				} else { // if doesnt exists
					DadesUsuari dadesUsuari = cacheHelper.findUsuariAmbCodi(permis.getPrincipalNom());
					usuariUserPermisDto = new UsuariPermisDto();
					usuariUserPermisDto.setCodi(permis.getPrincipalNom());
					usuariUserPermisDto.setNom(dadesUsuari.getNom());
					usuariUserPermisDto.setHasUsuariPermission(true);
					usuaris.add(usuariUserPermisDto);
				}
				break;
			case ROL:
				List<DadesUsuari> usuarisGrup = pluginHelper.dadesUsuariFindAmbGrup(permis.getPrincipalNom());
				if (usuarisGrup != null) {
					for (DadesUsuari usuariGrup : usuarisGrup) {
						UsuariPermisDto usuariRolPermisDto = null;
						for (UsuariPermisDto usuari : usuaris) {
							if (usuari.getCodi().equals(usuariGrup.getCodi())) {
								usuariRolPermisDto = usuari;
							}
						}
						if (usuariRolPermisDto != null) { // if already exists
							usuariRolPermisDto.getRols().add(permis.getPrincipalNom());
						} else { // if doesnt exists
							usuariRolPermisDto = new UsuariPermisDto();
							usuariRolPermisDto.setCodi(usuariGrup.getCodi());
							usuariRolPermisDto.setNom(usuariGrup.getNom());
							usuariRolPermisDto.getRols().add(permis.getPrincipalNom());
							usuaris.add(usuariRolPermisDto);
						}
					}
				}
				break;
			}
		}
		return usuaris;
	}


	public ContingutMovimentEntity ferIEnregistrarMoviment(
			ContingutEntity contingut,
			ContingutEntity desti,
			String comentari) {
		UsuariEntity usuariAutenticat = usuariHelper.getUsuariAutenticat();
		if (usuariAutenticat == null && contingut.getDarrerMoviment() != null)
			usuariHelper.generarUsuariAutenticat(
					contingut.getDarrerMoviment().getRemitent().getCodi(), 
					true);
		ContingutMovimentEntity contenidorMoviment = ContingutMovimentEntity.getBuilder(
				contingut,
				contingut.getPare(),
				desti,
				usuariHelper.getUsuariAutenticat(),
				comentari).build();
		contingut.updateDarrerMoviment(
				contenidorMovimentRepository.save(contenidorMoviment));
		contingut.updatePare(desti);
		return contenidorMoviment;
	}

	public ContingutEntity findContingutArrel(
			ContingutEntity contingut) {
		ContingutEntity contingutActual = contingut;
		while (contingutActual != null && contingutActual.getPare() != null) {
			contingutActual = contingutActual.getPare();
		}
		return contingutRepository.findOne(contingutActual.getId());
	}

	public boolean isNomValid(String nom) {
		return !nom.startsWith(".");
	}

	public long[] countFillsAmbPermisReadByContinguts(
			EntitatEntity entitat,
			List<? extends ContingutEntity> continguts,
			boolean comprovarPermisos) {
		long[] resposta = new long[continguts.size()];
		if (!continguts.isEmpty()) {
			List<Object[]> countFillsTotals = contingutRepository.countByPares(
					entitat,
					continguts);
			List<Object[]> countNodesTotals = new ArrayList<Object[]>();
			List<Object[]> countNodesByPares;
			
			countNodesByPares = new ArrayList<Object[]>();
			
			for (int i = 0; i < continguts.size(); i++) {
				ContingutEntity contingut = continguts.get(i);
				Long total = getCountByContingut(
						contingut,
						countFillsTotals);
				Long totalNodes = getCountByContingut(
						contingut,
						countNodesTotals);
				Long totalNodesPermisRead = getCountByContingut(
						contingut,
						countNodesByPares);
				resposta[i] = total - totalNodes + totalNodesPermisRead;
				
			}
		}
		return resposta;
	}

	private Long getCountByContingut(
			ContingutEntity contingut,
			List<Object[]> counts) {
		for (Object[] count: counts) {
			Long contingutId = (Long)count[0];
			if (contingutId.equals(contingut.getId())) {
				return (Long)count[1];
			}
		}
		return new Long(0);
	}

	@Transactional
	public ContingutEntity ferCopiaRegistre(
			ContingutEntity contingutOriginal,
			String codiDir3Desti) {
		RegistreEntity registreOriginal = (RegistreEntity)contingutOriginal;
		Integer numeroCopies = registreHelper.getMaxNumeroCopia(registreOriginal);
		RegistreEntity registreCopia = RegistreEntity.getBuilder(
				registreOriginal.getEntitat(), 
				registreOriginal.getRegistreTipus(), 
				registreOriginal.getUnitatAdministrativa(),
				registreOriginal.getUnitatAdministrativaDescripcio(), 
				registreOriginal.getNumero(), 
				registreOriginal.getData(),
				numeroCopies +1,
				registreOriginal.getIdentificador(), 
				registreOriginal.getExtracte(), 
				registreOriginal.getOficinaCodi(), 
				registreOriginal.getLlibreCodi(), 
				registreOriginal.getAssumpteTipusCodi(), 
				registreOriginal.getIdiomaCodi(), 
				registreOriginal.getProcesEstat(),
				registreOriginal.getPare()).
				entitatCodi(registreOriginal.getEntitatCodi()).
				entitatDescripcio(registreOriginal.getEntitatDescripcio()).
				oficinaDescripcio(registreOriginal.getOficinaDescripcio()).
				llibreDescripcio(registreOriginal.getLlibreDescripcio()).
				assumpteTipusDescripcio(registreOriginal.getAssumpteTipusDescripcio()).
				assumpteCodi(registreOriginal.getAssumpteCodi()).
				assumpteDescripcio(registreOriginal.getAssumpteDescripcio()).
				procedimentCodi(registreOriginal.getProcedimentCodi()).
				referencia(registreOriginal.getReferencia()).
				expedientNumero(registreOriginal.getExpedientNumero()).
				numeroOrigen(registreOriginal.getNumeroOrigen()).
				idiomaDescripcio(registreOriginal.getIdiomaDescripcio()).
				transportTipusCodi(registreOriginal.getTransportTipusCodi()).
				transportTipusDescripcio(registreOriginal.getTransportTipusDescripcio()).
				transportNumero(registreOriginal.getTransportNumero()).
				usuariCodi(registreOriginal.getUsuariCodi()).
				usuariNom(registreOriginal.getUsuariNom()).
				usuariContacte(registreOriginal.getUsuariContacte()).
				aplicacioCodi(registreOriginal.getAplicacioCodi()).
				aplicacioVersio(registreOriginal.getAplicacioVersio()).
				documentacioFisicaCodi(registreOriginal.getDocumentacioFisicaCodi()).
				documentacioFisicaDescripcio(registreOriginal.getDocumentacioFisicaDescripcio()).
				observacions(registreOriginal.getObservacions()).
				exposa(registreOriginal.getExposa()).
				solicita(registreOriginal.getSolicita()).
				regla(registreOriginal.getRegla()).
				oficinaOrigen(registreOriginal.getDataOrigen(), registreOriginal.getOficinaOrigenCodi(), registreOriginal.getOficinaOrigenDescripcio()).
				presencial(registreOriginal.getPresencial()).
				build();
		// Copia els interessats
		if (registreOriginal.getInteressats() != null) {
			for (RegistreInteressatEntity registreInteressat: registreOriginal.getInteressats()) {
				// Filtra els representants
				if (registreInteressat.getRepresentat() == null)
					registreCopia.getInteressats().add(this.copiarInteressatEntity(registreInteressat, registreCopia));
			}
		}
		// Copia els annexos
		if (registreOriginal.getAnnexos() != null) {
			for (RegistreAnnexEntity registreAnnex: registreOriginal.getAnnexos()) {

				if ((registreOriginal.getJustificant() != null && registreOriginal.getJustificant().getId().equals(registreAnnex.getId())
						|| (registreOriginal.getJustificantArxiuUuid() != null && registreOriginal.getJustificantArxiuUuid().equals(registreAnnex.getFitxerArxiuUuid())))) {
					// No copia l'annex justificant
					continue;
				}
				
				RegistreAnnexEntity nouAnnex = RegistreAnnexEntity.getBuilder(
						registreAnnex.getTitol(), 
						registreAnnex.getFitxerNom(), 
						registreAnnex.getFitxerTamany(), 
						registreAnnex.getFitxerArxiuUuid(), 
						registreAnnex.getDataCaptura(), 
						registreAnnex.getOrigenCiutadaAdmin(), 
						registreAnnex.getNtiTipusDocument(), 
						registreAnnex.getSicresTipusDocument(), 
						registreCopia).
						ntiElaboracioEstat(registreAnnex.getNtiElaboracioEstat()).
						fitxerTipusMime(registreAnnex.getFitxerTipusMime()).
						localitzacio(registreAnnex.getLocalitzacio()).
						observacions(registreAnnex.getObservacions()).
						firmaMode(registreAnnex.getFirmaMode()).
						timestamp(registreAnnex.getTimestamp()).
						validacioOCSP(registreAnnex.getValidacioOCSP()).
						gesdocDocumentId(registreAnnex.getGesdocDocumentId()).
						build();
				for (RegistreAnnexFirmaEntity firma: registreAnnex.getFirmes()) {
					RegistreAnnexFirmaEntity novaFirma = RegistreAnnexFirmaEntity.getBuilder(
							firma.getTipus(), 
							firma.getPerfil(), 
							firma.getFitxerNom(), 
							firma.getTipusMime(), 
							firma.getCsvRegulacio(), 
							firma.isAutofirma(), 
							nouAnnex).
							gesdocFirmaId(firma.getGesdocFirmaId()).
							build();
					for (RegistreFirmaDetallEntity arxiuFirmaDetallEntity : firma.getDetalls()) {
						RegistreFirmaDetallEntity firmaDetallEntity = RegistreFirmaDetallEntity.getBuilder(
								arxiuFirmaDetallEntity,
								novaFirma).build();
						novaFirma.getDetalls().add(firmaDetallEntity);
					}
					
					
					nouAnnex.getFirmes().add(novaFirma);
				}
				nouAnnex.updateSignaturaDetallsDescarregat(true);
				registreCopia.getAnnexos().add(nouAnnex);
			}
		}
		registreCopia.updateJustificantArxiuUuid(
				registreOriginal.getJustificantArxiuUuid());
		contingutRepository.saveAndFlush(registreCopia);
		boolean duplicarContingutArxiu = PropertiesHelper.getProperties().getAsBoolean("es.caib.distribucio.plugins.distribucio.fitxers.duplicar.contingut.arxiu");
		if (duplicarContingutArxiu) {
			registreHelper.createRegistreAndAnnexosInArxiu(
					registreCopia,
					codiDir3Desti,
					true);
		} else {
			registreCopia.updateExpedientArxiuUuid(registreOriginal.getExpedientArxiuUuid());
		}
  		return registreCopia;
	}
	
	/** Fa una còpia de l'interessat i dels seus representants. Si es passa el registre com a paràmetre llavors s'assigna aquest
	 * a l'interessat i als representants.
	 * 
	 * @param registreInteressat
	 * 			Interessat a copiar.
	 * @param registre
	 * 			Paràmetre per assignar un registre diferent a l'interessat i als seus representants.
	 * @return
	 * 		Retorna una còpia de l'interessat.
	 */
	@Transactional
	public RegistreInteressatEntity copiarInteressatEntity(RegistreInteressatEntity registreInteressat, RegistreEntity registre) {

		RegistreInteressatTipusEnum interessatTipus =registreInteressat.getTipus();
		RegistreInteressatEntity.Builder interessatBuilder;
		switch (interessatTipus) {
		case PERSONA_FIS:
			interessatBuilder = RegistreInteressatEntity.getBuilder(
					interessatTipus,
					registreInteressat.getDocumentTipus(),
					registreInteressat.getDocumentNum(),
					registreInteressat.getNom(),
					registreInteressat.getLlinatge1(),
					registreInteressat.getLlinatge2(),
					registre);
			break;
		default: // PERSONA_JUR o ADMINISTRACIO
			interessatBuilder = RegistreInteressatEntity.getBuilder(
					interessatTipus,
					registreInteressat.getDocumentTipus(),
					registreInteressat.getDocumentNum(),
					registreInteressat.getRaoSocial(),
					registre);
			break;
		}
		interessatBuilder.
			pais(registreInteressat.getPais()).
			paisCodi(registreInteressat.getPaisCodi()).
			provincia(registreInteressat.getProvincia()).
			provinciaCodi(registreInteressat.getProvinciaCodi()).
			municipi(registreInteressat.getMunicipi()).
			municipiCodi(registreInteressat.getMunicipiCodi()).
			adresa(registreInteressat.getAdresa()).
			codiPostal(registreInteressat.getCodiPostal()).
			email(registreInteressat.getEmail()).
			telefon(registreInteressat.getTelefon()).
			emailHabilitat(registreInteressat.getEmailHabilitat()).
			canalPreferent(registreInteressat.getCanalPreferent()).
			observacions(registreInteressat.getObservacions());		
		RegistreInteressatEntity interessatCopiaEntity = interessatBuilder.build();
		
		if (registreInteressat.getRepresentant() != null) {
			RegistreInteressatEntity representant = registreInteressat.getRepresentant();
			interessatCopiaEntity.updateRepresentant(
					representant.getTipus(),
					representant.getDocumentTipus(),
					representant.getDocumentNum(),
					representant.getNom(),
					representant.getLlinatge1(),
					representant.getLlinatge2(),
					representant.getRaoSocial(),
					representant.getPais(),
					representant.getPaisCodi(),
					representant.getProvincia(),
					representant.getProvinciaCodi(),
					representant.getMunicipi(),
					representant.getMunicipiCodi(),
					representant.getAdresa(),
					representant.getCodiPostal(),
					representant.getEmail(),
					representant.getTelefon(),
					representant.getEmailHabilitat(),
					representant.getCanalPreferent(),
					representant.getCodiDire());
		}
		return interessatCopiaEntity;
	}

	private List<ContingutEntity> getPathContingut(
			ContingutEntity contingut) {
		
		final Timer getPathContingutComDtoTimergetPathContingut = metricRegistry.timer(MetricRegistry.name(ContingutHelper.class, "getPathContingut"));
		Timer.Context getPathContingutComDtoContextgetPathContingut = getPathContingutComDtoTimergetPathContingut.time();
		
		List<ContingutEntity> path = null;
		ContingutEntity contingutActual = contingut;
		while (contingutActual != null && contingutActual.getPare() != null) {
			if (path == null)
				path = new ArrayList<ContingutEntity>();
			ContingutEntity c = contingutRepository.findOne(contingutActual.getPare().getId());
			path.add(c);
			contingutActual = c;
		}
		if (path != null) {
			Collections.reverse(path);
		}
		
		getPathContingutComDtoContextgetPathContingut.stop();
		return path;
	}

	public List<ContingutDto> getPathContingutComDto(
			ContingutEntity contingut,
			boolean ambPermisos) {
		final Timer getPathContingutComDtoTimer = metricRegistry.timer(MetricRegistry.name(ContingutHelper.class, "getPathContingutComDto"));
		Timer.Context getPathContingutComDtoContext = getPathContingutComDtoTimer.time();
		
		List<ContingutEntity> path = getPathContingut(contingut);
		List<ContingutDto> pathDto = null;
		if (path != null) {
			pathDto = new ArrayList<ContingutDto>();

			for (ContingutEntity contingutPath: path) {

				final Timer getPathContingutPathToContingutDto = metricRegistry.timer(MetricRegistry.name(ContingutHelper.class, "getPathContingutComDto.getPathContingutPathToContingutDto"));
				Timer.Context getPathContingutComDtoContextPathToContingutDto = getPathContingutPathToContingutDto.time();
					pathDto.add(
						toContingutDto(
								contingutPath,
								ambPermisos,
								false,
								false,
								false,
								false,
								false,
								false));
					getPathContingutComDtoContextPathToContingutDto.stop();

			}
			
		}
		getPathContingutComDtoContext.stop();
		return pathDto;
	}
	
	public void tractarInteressats(List<RegistreInteressat> interessats) {
		ListIterator<RegistreInteressat> iter = interessats.listIterator();
		while(iter.hasNext()){
		    if(iter.next().getRepresentat() != null){
		        iter.remove();
		    }
		}
	}

	private static final Logger logger = LoggerFactory.getLogger(ContingutHelper.class);

}