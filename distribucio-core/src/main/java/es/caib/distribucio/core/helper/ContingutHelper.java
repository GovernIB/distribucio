/**
 * 
 */
package es.caib.distribucio.core.helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

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
import es.caib.distribucio.core.api.dto.PermisDto;
import es.caib.distribucio.core.api.dto.RegistreAnotacioDto;
import es.caib.distribucio.core.api.dto.UnitatOrganitzativaDto;
import es.caib.distribucio.core.api.dto.UsuariDto;
import es.caib.distribucio.core.api.registre.RegistreInteressat;
import es.caib.distribucio.core.api.registre.RegistreInteressatTipusEnum;
import es.caib.distribucio.core.entity.BustiaEntity;
import es.caib.distribucio.core.entity.ContingutEntity;
import es.caib.distribucio.core.entity.ContingutMovimentEntity;
import es.caib.distribucio.core.entity.EntitatEntity;
import es.caib.distribucio.core.entity.RegistreAnnexEntity;
import es.caib.distribucio.core.entity.RegistreAnnexFirmaEntity;
import es.caib.distribucio.core.entity.RegistreEntity;
import es.caib.distribucio.core.entity.RegistreInteressatEntity;
import es.caib.distribucio.core.entity.UnitatOrganitzativaEntity;
import es.caib.distribucio.core.entity.UsuariEntity;
import es.caib.distribucio.core.repository.ContingutMovimentRepository;
import es.caib.distribucio.core.repository.ContingutRepository;
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
	private ContingutMovimentRepository contenidorMovimentRepository;

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
				false);
	}
	public ContingutDto toContingutDto(
			ContingutEntity contingut,
			boolean ambPermisos,
			boolean ambFills,
			boolean filtrarFillsSegonsPermisRead,
			boolean ambDades,
			boolean ambPath,
			boolean pathNomesFinsExpedientArrel,
			boolean ambVersions) {
		ContingutDto resposta = null;
		// Crea el contenidor del tipus correcte
		ContingutEntity deproxied = HibernateHelper.deproxy(contingut);
		if (deproxied instanceof BustiaEntity) {
			BustiaEntity bustia = (BustiaEntity)deproxied;
			BustiaDto dto = new BustiaDto();
			dto.setUnitatCodi(bustia.getUnitatCodi());
			dto.setActiva(bustia.isActiva());
			dto.setPerDefecte(bustia.isPerDefecte());
			UnitatOrganitzativaDto unitatConselleria = unitatOrganitzativaHelper.findConselleria(
					bustia.getEntitat().getCodiDir3(),
					bustia.getUnitatCodi());
			if (unitatConselleria != null) {
				dto.setUnitatConselleriaCodi(unitatConselleria.getCodi());
			}
			UnitatOrganitzativaEntity unitatEntity = bustia.getUnitatOrganitzativa();
			UnitatOrganitzativaDto unitatDto = conversioTipusHelper.convertir(
					unitatEntity,
					UnitatOrganitzativaDto.class);
			unitatDto = UnitatOrganitzativaHelper.assignAltresUnitatsFusionades(unitatEntity, unitatDto);
			
			dto.setUnitatOrganitzativa(unitatDto);
			dto.setUnitatCodi(bustia.getUnitatOrganitzativa().getCodi());
			resposta = dto;
		} else if (deproxied instanceof RegistreEntity) {
			RegistreEntity registre = (RegistreEntity)deproxied;
			RegistreAnotacioDto dto = conversioTipusHelper.convertir(
					registre,
					RegistreAnotacioDto.class);
			dto.setLlegida(registre.getLlegida() == null || registre.getLlegida());
			resposta = dto;
		}
		resposta.setId(contingut.getId());
		resposta.setNom(contingut.getNom());
		resposta.setEsborrat(contingut.getEsborrat());
		resposta.setArxiuUuid(contingut.getArxiuUuid());
		resposta.setArxiuDataActualitzacio(contingut.getArxiuDataActualitzacio());
		resposta.setEntitat(
				conversioTipusHelper.convertir(
						contingut.getEntitat(),
							EntitatDto.class));
		resposta.setAlerta(!contingut.getAlertes().isEmpty());
		if (contingut.getDarrerMoviment() != null) {
			ContingutMovimentEntity darrerMoviment = contingut.getDarrerMoviment();
			resposta.setDarrerMovimentUsuari(
					conversioTipusHelper.convertir(
							darrerMoviment.getRemitent(),
							UsuariDto.class));
			resposta.setDarrerMovimentData(darrerMoviment.getCreatedDate().toDate());
			resposta.setDarrerMovimentComentari(darrerMoviment.getComentari());
		}
		if (resposta != null) {
			// Omple la informació d'auditoria
			resposta.setCreatedBy(
					conversioTipusHelper.convertir(
							contingut.getCreatedBy(),
							UsuariDto.class));
			resposta.setCreatedDate(contingut.getCreatedDate().toDate());
			resposta.setLastModifiedBy(
					conversioTipusHelper.convertir(
							contingut.getLastModifiedBy(),
							UsuariDto.class));
			resposta.setLastModifiedDate(contingut.getLastModifiedDate().toDate());
		}
		if (resposta != null) {
			if (ambPath) {
				// TIMER START
				final Timer getPathContingutComDtoTimer = metricRegistry.timer(MetricRegistry.name(ContingutHelper.class, "toContingutDto.getPathContingutComDto"));
				Timer.Context getPathContingutComDtoContext = getPathContingutComDtoTimer.time();
				// Calcula el path
				List<ContingutDto> path = getPathContingutComDto(
						contingut,
						ambPermisos,
						pathNomesFinsExpedientArrel);
				resposta.setPath(path);
				getPathContingutComDtoContext.stop();
				// TIMER STOP
			}
			if (ambFills) {
				// Cerca els nodes fills
				List<ContingutDto> contenidorDtos = new ArrayList<ContingutDto>();
				// TIMER START
				final Timer findByPareAndEsborratTimer = metricRegistry.timer(MetricRegistry.name(ContingutHelper.class, "toContingutDto.getPathContingutComDto"));
				Timer.Context findByPareAndEsborratContext = findByPareAndEsborratTimer.time();
				List<ContingutEntity> fills = contingutRepository.findByPareAndEsborrat(
						contingut,
						0,
						new Sort("createdDate"));
				findByPareAndEsborratContext.stop();
				// TIMER STOP
				List<ContingutDto> fillPath = null;
				if (ambPath) {
					fillPath = new ArrayList<ContingutDto>();
					if (resposta.getPath() != null)
						fillPath.addAll(resposta.getPath());
					fillPath.add(toContingutDto(
							contingut,
							false,
							false,
							false,
							false,
							false,
							false,
							false));
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
								false);
						// Configura el pare de cada fill
						fillDto.setPath(fillPath);
						contenidorDtos.add(fillDto);
					}
				}
				resposta.setFills(contenidorDtos);
			}
		}
		return resposta;
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

	public Set<String> findUsuarisAmbPermisReadPerContenidor(
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
	public ContingutEntity ferCopiaContingut(
			ContingutEntity contingutOriginal,
			BustiaEntity bustiaDesti) {
		RegistreEntity registreOriginal = (RegistreEntity)contingutOriginal;
		Integer numeroCopies = registreHelper.getMaxNumeroCopia(registreOriginal);
		RegistreEntity contingutCopia = RegistreEntity.getBuilder(
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
				oficinaOrigen(registreOriginal.getDataOrigen(), 
				registreOriginal.getOficinaOrigenCodi(), 
				registreOriginal.getOficinaOrigenDescripcio()).
				build();
		// Copia els interessats
		if (registreOriginal.getInteressats() != null) {
			for (RegistreInteressatEntity registreInteressat: registreOriginal.getInteressats()) {
				// Filtra els representants
				if (registreInteressat.getRepresentat() == null)
					contingutCopia.getInteressats().add(this.copiarInteressatEntity(registreInteressat, contingutCopia));
			}
		}
		// Copia els annexos
		if (registreOriginal.getAnnexos() != null) {
			for (RegistreAnnexEntity registreAnnex: registreOriginal.getAnnexos()) {
				RegistreAnnexEntity nouAnnex = RegistreAnnexEntity.getBuilder(
						registreAnnex.getTitol(), 
						registreAnnex.getFitxerNom(), 
						registreAnnex.getFitxerTamany(), 
						registreAnnex.getFitxerArxiuUuid(), 
						registreAnnex.getDataCaptura(), 
						registreAnnex.getOrigenCiutadaAdmin(), 
						registreAnnex.getNtiTipusDocument(), 
						registreAnnex.getSicresTipusDocument(), 
						contingutCopia).
						ntiElaboracioEstat(registreAnnex.getNtiElaboracioEstat()).
						fitxerTipusMime(registreAnnex.getFitxerTipusMime()).
						localitzacio(registreAnnex.getLocalitzacio()).
						observacions(registreAnnex.getObservacions()).
						firmaMode(registreAnnex.getFirmaMode()).
						timestamp(registreAnnex.getTimestamp()).
						validacioOCSP(registreAnnex.getValidacioOCSP()).
						build();
				for (RegistreAnnexFirmaEntity firma: registreAnnex.getFirmes()) {
					RegistreAnnexFirmaEntity novaFirma = RegistreAnnexFirmaEntity.getBuilder(
							firma.getTipus(), 
							firma.getPerfil(), 
							firma.getFitxerNom(), 
							firma.getTipusMime(), 
							firma.getCsvRegulacio(), 
							firma.isAutofirma(), 
							nouAnnex).build();
					nouAnnex.getFirmes().add(novaFirma);
				}
				contingutCopia.getAnnexos().add(nouAnnex);
			}
		}
		contingutCopia.updateJustificantArxiuUuid(
				registreOriginal.getJustificantArxiuUuid());
		contingutRepository.saveAndFlush(contingutCopia);
		boolean duplicarContingut = PropertiesHelper.getProperties().getAsBoolean("es.caib.distribucio.plugins.distribucio.fitxers.duplicar.contingut.arxiu");
		if (duplicarContingut) {
			registreHelper.guardarAnnexosAmbPluginDistribucio(
					contingutCopia,
					bustiaDesti.getEntitat().getCodiDir3(),
					false);
		} else {
			contingutCopia.updateExpedientArxiuUuid(registreOriginal.getExpedientArxiuUuid());
		}
  		return contingutCopia;
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
					representant.getCanalPreferent());
		}
		return interessatCopiaEntity;
	}

	private List<ContingutEntity> getPathContingut(
			ContingutEntity contingut) {
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
		return path;
	}

	public List<ContingutDto> getPathContingutComDto(
			ContingutEntity contingut,
			boolean ambPermisos,
			boolean nomesFinsExpedientArrel) {
		List<ContingutEntity> path = getPathContingut(contingut);
		List<ContingutDto> pathDto = null;
		if (path != null) {
			pathDto = new ArrayList<ContingutDto>();
			boolean expedientArrelTrobat = !nomesFinsExpedientArrel;
			for (ContingutEntity contingutPath: path) {
				if (expedientArrelTrobat) {
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
				}
			}
		}
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