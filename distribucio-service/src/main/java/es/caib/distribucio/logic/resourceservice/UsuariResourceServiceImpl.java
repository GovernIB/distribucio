package es.caib.distribucio.logic.resourceservice;

import es.caib.distribucio.logic.base.helper.AuthenticationHelper;
import es.caib.distribucio.logic.base.service.BaseMutableResourceService;
import es.caib.distribucio.logic.helper.CacheHelper;
import es.caib.distribucio.logic.helper.UsuariCodiHelper;
import es.caib.distribucio.logic.intf.base.exception.ActionExecutionException;
import es.caib.distribucio.logic.intf.base.exception.AnswerRequiredException;
import es.caib.distribucio.logic.intf.base.exception.ArtifactNotFoundException;
import es.caib.distribucio.logic.intf.base.model.FieldOption;
import es.caib.distribucio.logic.intf.base.util.I18nUtil;
import es.caib.distribucio.logic.intf.dto.IdiomaEnumDto;
import es.caib.distribucio.logic.intf.model.UsuariResource;
import es.caib.distribucio.logic.intf.resourceservice.UsuariResourceService;
import es.caib.distribucio.persist.resourceentity.BustiaDefaultResourceEntity;
import es.caib.distribucio.persist.resourceentity.BustiaResourceEntity;
import es.caib.distribucio.persist.resourceentity.EntitatResourceEntity;
import es.caib.distribucio.persist.resourceentity.UsuariResourceEntity;
import es.caib.distribucio.persist.resourcerepository.BustiaDefaultResourceRepository;
import es.caib.distribucio.persist.resourcerepository.BustiaResourceRepository;
import es.caib.distribucio.persist.resourcerepository.EntitatResourceRepository;
import es.caib.distribucio.persist.resourcerepository.UsuariResourceRepository;
import es.caib.distribucio.plugin.usuari.DadesUsuari;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Implementació del servei de consulta i modificació del perfil de l'usuari autenticat actual.
 * <p>
 * Restringeix sempre l'accés al propi usuari: independentment de l'id sol·licitat, la consulta
 * només pot retornar (o modificar) el registre l'usuari autenticat.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UsuariResourceServiceImpl extends BaseMutableResourceService<UsuariResource, String, UsuariResourceEntity> implements UsuariResourceService {

	private static final String ROLE_DISPLAY_PREFIX = "DIS_";
	/** Línia "codiActual=codiNou": dos codis no buits, sense espais i separats per un únic "=". */
	private static final Pattern LINIA_CANVI_CODI = Pattern.compile("^([^\\s=]+)=([^\\s=]+)$");

	private final AuthenticationHelper authenticationHelper;
	private final CacheHelper cacheHelper;
	private final EntitatResourceRepository entitatResourceRepository;
	private final UsuariResourceRepository usuariResourceRepository;

	private final BustiaResourceRepository bustiaResourceRepository;
	private final BustiaDefaultResourceRepository bustiaDefaultResourceRepository;
	private final UsuariCodiHelper usuariCodiHelper;

	@PostConstruct
	public void init() {
		register(UsuariResource.Fields.idioma, new IdiomaFieldOptionsProvider());
		register(UsuariResource.ACTION_CANVI_CODIS_CODE, new CanviCodisActionExecutor());
	}

	/**
	 * Les accions d'aquest recurs no s'executen dins una transacció: el canvi de codi d'un usuari ja obre la seva
	 * ({@link UsuariCodiHelper#updateUsuariCodi}, amb timeout propi de 1200 s). Amb la transacció exterior per
	 * defecte de {@code BaseMutableResourceService} el temps màxim del servidor d'aplicacions (uns 300 s a JBoss)
	 * podria caducar mentre una línia lenta encara s'està executant: el canvi es confirmaria, però la resposta
	 * seria un error 500 i el front el mostraria com a fallit.
	 */
	@Override
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public <P extends Serializable> Serializable artifactActionExec(
			String id,
			String code,
			P params) throws ArtifactNotFoundException, ActionExecutionException {
		return super.artifactActionExec(id, code, params);
	}

	/**
	 * Canvi de codis d'usuari: una línia "codiActual=codiNou" per usuari, sense espais. Mateix treball que
	 * {@code UsuariController.setCanviCodis} de la interfície JSP, però amb totes les línies en una sola acció.
	 * <p>
	 * Cada línia es processa en ordre (es poden encadenar "a=b" i "b=c") i en la seva pròpia transacció:
	 * l'error d'una línia no atura ni desfà les altres.
	 */
	private class CanviCodisActionExecutor implements
			ActionExecutor<UsuariResourceEntity, UsuariResource.CanviCodisForm, UsuariResource.CanviCodisResultat> {

		@Override
		public UsuariResource.CanviCodisResultat exec(
				String code,
				UsuariResourceEntity entity,
				UsuariResource.CanviCodisForm params) throws ActionExecutionException {
			long t0 = System.currentTimeMillis();
			boolean unifica = Boolean.TRUE.equals(params.getUnificaUsuarisExistents());
			List<UsuariResource.CanviCodisLinia> linies = new ArrayList<>();
			Set<String> codisAntics = new HashSet<>();
			String[] entrades = params.getMapeig().split("\\R", -1);
			for (int i = 0; i < entrades.length; i++) {
				String entrada = entrades[i].trim();
				if (entrada.isEmpty()) {
					continue;
				}
				UsuariResource.CanviCodisLinia linia = new UsuariResource.CanviCodisLinia();
				linia.setNumLinia(i + 1);
				linies.add(linia);
				Matcher matcher = LINIA_CANVI_CODI.matcher(entrada);
				if (!matcher.matches() || matcher.group(1).equals(matcher.group(2))) {
					linia.setEstat(UsuariResource.CanviCodisEstat.FORMAT_INCORRECTE);
					linia.setMissatge(entrada);
					continue;
				}
				String codiAntic = matcher.group(1);
				String codiNou = matcher.group(2);
				linia.setCodiAntic(codiAntic);
				linia.setCodiNou(codiNou);
				if (!codisAntics.add(codiAntic)) {
					linia.setEstat(UsuariResource.CanviCodisEstat.DUPLICAT);
				} else if (!usuariCodiHelper.existeixUsuari(codiAntic)) {
					linia.setEstat(UsuariResource.CanviCodisEstat.ANTIC_NO_EXISTEIX);
				} else if (!unifica && usuariCodiHelper.existeixUsuari(codiNou)) {
					linia.setEstat(UsuariResource.CanviCodisEstat.NOU_EXISTEIX_SALTAT);
				} else {
					long tLinia = System.currentTimeMillis();
					try {
						linia.setRegistresModificats(usuariCodiHelper.updateUsuariCodi(codiAntic, codiNou));
						linia.setEstat(UsuariResource.CanviCodisEstat.OK);
					} catch (Exception ex) {
						log.error("Error modificant el codi de l'usuari (codiAntic=" + codiAntic + ", codiNou=" + codiNou + ")", ex);
						linia.setEstat(UsuariResource.CanviCodisEstat.ERROR);
						linia.setMissatge(ex.getMessage());
					}
					linia.setDurada(System.currentTimeMillis() - tLinia);
				}
			}
			return new UsuariResource.CanviCodisResultat(linies, System.currentTimeMillis() - t0);
		}

		@Override
		public void onChange(
				Serializable id,
				UsuariResource.CanviCodisForm previous,
				String fieldName,
				Object fieldValue,
				Map<String, AnswerRequiredException.AnswerValue> answers,
				String[] previousFieldNames,
				UsuariResource.CanviCodisForm target) {
		}

	}

	/**
	 * Valors del desplegable d'idioma: els mateixos que ofereix la interfície JSP, que els treu
	 * d'{@link IdiomaEnumDto} (veure {@code UsuariController} i {@code usuariForm.jsp}). El valor
	 * de l'opció és el nom de la constant, que és el que la JSP desa a la columna.
	 */
	private static class IdiomaFieldOptionsProvider implements FieldOptionsProvider {
		@Override
		public List<FieldOption> getOptions(String fieldName, Map<String, String[]> requestParameterMap) {
			return Arrays.stream(IdiomaEnumDto.values()).
					map(idioma -> new FieldOption(
							idioma.name(),
							I18nUtil.getInstance().getI18nMessage(
									IdiomaEnumDto.class.getName() + "." + idioma.name()))).
					collect(Collectors.toList());
		}
	}

	/**
	 * Deixa l'idioma en majúscules, que és el format de les opcions i el que desa la interfície
	 * JSP. L'alta automàtica d'usuaris hi posa "ca" en minúscules, i sense normalitzar-ho el
	 * desplegable no trobaria cap opció que hi encaixés i sortiria buit. La fila es queda com
	 * està fins que l'usuari desa el perfil.
	 */
	private void normalitzarIdioma(UsuariResource resource) {
		if (resource.getIdioma() != null) {
			resource.setIdioma(resource.getIdioma().toUpperCase());
		}
	}

	@Override
	protected Specification<UsuariResourceEntity> additionalSpecification(String[] namedQueries) {
		String currentUserName = authenticationHelper.getCurrentUserName();
		return (root, query, cb) -> cb.equal(root.get("id"), currentUserName);
	}

	@Override
	protected void completeResource(UsuariResource resource) {
		String[] roles = authenticationHelper.getCurrentUserRoles();
		resource.setRols(Arrays.stream(roles).
				filter(r -> r.startsWith(ROLE_DISPLAY_PREFIX)).
				toArray(String[]::new));
		normalitzarIdioma(resource);
		// bustiaPerDefecte es guarda a una taula apart (dis_bustia_default), per parella
		// entitat+usuari -- s'utilitza entitatPerDefecteId com a "entitat de context" ja que la
		// interfície REACT encara no disposa d'un selector d'entitat independent (veure
		// AplicacioServiceImpl.getBustiaPerDefecte/updateUsuariActual per a l'equivalent JSP).
		if (resource.getEntitatPerDefecteId() != null) {
			EntitatResourceEntity entitat = entitatResourceRepository.getReferenceById(resource.getEntitatPerDefecteId());
			UsuariResourceEntity usuari = usuariResourceRepository.getReferenceById(resource.getId());
			BustiaDefaultResourceEntity bustiaDefault = bustiaDefaultResourceRepository.findByEntitatAndUsuari(entitat, usuari);
			if (bustiaDefault != null) {
				resource.setBustiaPerDefecte(bustiaDefault.getBustia().getId());
			}
		}
	}

	@Override
	protected void afterConversion(UsuariResourceEntity entity, UsuariResource resource) {
		// completeResource() només es crida des de create()/update() (sobre el resource
		// d'entrada, abans de desar) -- getOne() no el crida mai, per tant sense això els camps
		// derivats (rols, bustiaPerDefecte) no es mostraven en obrir el diàleg de perfil, només
		// després de guardar. afterConversion() es crida sempre (getOne() i com a resposta final
		// de create()/update()), per tant és el punt correcte per garantir-ho en tots els casos.
		completeResource(resource);
	}

	@Override
	protected void beforeUpdateSave(
			UsuariResourceEntity entity,
			UsuariResource resource,
			Map<String, AnswerRequiredException.AnswerValue> answers) {
		if (resource.getEntitatPerDefecteId() == null) {
			return;
		}
		EntitatResourceEntity entitat = entitatResourceRepository.getReferenceById(resource.getEntitatPerDefecteId());
		UsuariResourceEntity usuari = usuariResourceRepository.getReferenceById(resource.getId());
		BustiaDefaultResourceEntity bustiaDefault = bustiaDefaultResourceRepository.findByEntitatAndUsuari(entitat, usuari);
		if (resource.getBustiaPerDefecte() != null) {
			BustiaResourceEntity bustia = bustiaResourceRepository.getReferenceById(resource.getBustiaPerDefecte());
			if (bustiaDefault != null) {
				bustiaDefault.updateBustiaDefault(bustia);
			} else {
				bustiaDefaultResourceRepository.save(BustiaDefaultResourceEntity.getBuilder(entitat, bustia, usuari).build());
			}
		} else if (bustiaDefault != null) {
			bustiaDefaultResourceRepository.delete(bustiaDefault);
		}
	}

    @Override
    public void refresh() {
		UsuariResource usuariFromAuth = getUsuariResourceFromAuth();
		if (usuariFromAuth != null) {
			Optional<UsuariResourceEntity> usuariOptional = usuariResourceRepository.findById(authenticationHelper.getCurrentUserName());
			if (usuariOptional.isPresent()) {
				UsuariResourceEntity usuariFromDb = usuariOptional.get();
				if (hasToUpdateUsuari(usuariFromDb, usuariFromAuth)) {
					usuariFromDb.setNom(usuariFromAuth.getNom());
					usuariFromDb.setNif(usuariFromAuth.getNif());
					usuariFromDb.setEmail(usuariFromAuth.getEmail());
					usuariResourceRepository.save(usuariFromDb);
				}
			} else {
				UsuariResourceEntity usuari = new UsuariResourceEntity();
				usuari.setId(usuariFromAuth.getId());
				usuari.setNom(usuariFromAuth.getNom());
				usuari.setNif(usuariFromAuth.getNif());
				usuari.setEmail(usuariFromAuth.getEmail());
				usuari.setEstilMenu(usuariFromAuth.getEstilMenu());
				usuariResourceRepository.save(usuari);
			}
		}
    }

	private UsuariResource getUsuariResourceFromAuth() {
		String codi = authenticationHelper.getCurrentUserName();
		DadesUsuari dadesUsuari = cacheHelper.findUsuariAmbCodi(codi);
		if (dadesUsuari == null) {
			return null;
		}
		UsuariResource usuari = new UsuariResource();
		usuari.setId(codi);
		usuari.setNom(dadesUsuari.getNomSencer());
		usuari.setNif(dadesUsuari.getNif());
		usuari.setEmail(dadesUsuari.getEmail());
		return usuari;
	}

	private boolean hasToUpdateUsuari(UsuariResourceEntity usuariFromDb, UsuariResource usuariFromAuth) {
		return !Objects.equals(usuariFromDb.getNom(), usuariFromAuth.getNom()) ||
				!Objects.equals(usuariFromDb.getNif(), usuariFromAuth.getNif()) ||
				!Objects.equals(usuariFromDb.getEmail(), usuariFromAuth.getEmail());
	}
}
