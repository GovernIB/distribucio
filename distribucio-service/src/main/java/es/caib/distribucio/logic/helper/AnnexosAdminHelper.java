/**
 * 
 */
package es.caib.distribucio.logic.helper;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import es.caib.distribucio.logic.intf.dto.ArxiuDetallDto;
import es.caib.distribucio.logic.intf.dto.ExpedientEstatEnumDto;
import es.caib.distribucio.logic.intf.dto.RegistreAnnexDto;
import es.caib.distribucio.logic.intf.dto.ResultatAnnexDefinitiuDto;
import es.caib.distribucio.logic.intf.registre.ValidacioFirmaEnum;
import es.caib.distribucio.logic.intf.service.RegistreService;
import es.caib.distribucio.logic.intf.service.ws.backoffice.AnnexEstat;
import es.caib.distribucio.persist.entity.RegistreAnnexEntity;
import es.caib.distribucio.persist.entity.RegistreEntity;
import es.caib.distribucio.persist.repository.RegistreAnnexRepository;
import es.caib.distribucio.plugin.distribucio.DistribucioRegistreAnnex;
import es.caib.distribucio.plugin.distribucio.DistribucioRegistreAnotacio;
import es.caib.pluginsib.arxiu.api.Document;
import es.caib.pluginsib.arxiu.api.DocumentEstat;

/**
 * Utilitat per a gestionar annexos.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class AnnexosAdminHelper {

	private static final Logger logger = LoggerFactory.getLogger(AnnexosAdminHelper.class);

	@Autowired
	private ConversioTipusHelper conversioTipusHelper;
	@Autowired
	private RegistreAnnexRepository registreAnnexRepository;
	@Autowired
	private RegistreService registreService;
	@Autowired
	private PluginHelper pluginHelper;
	@Autowired
	private RegistreHelper registreHelper;

	@PersistenceContext
	private EntityManager entityManager;

	public RegistreAnnexDto toRegistreAnnexDto(
			RegistreAnnexEntity registreAnnexEntity) {
		RegistreAnnexDto registreAnnexDto = conversioTipusHelper.convertir(registreAnnexEntity, RegistreAnnexDto.class);
		return registreAnnexDto;
	}

	public List<String> getTitolsAnnexes(RegistreEntity registre) {
		return registreAnnexRepository.findTitolByRegistre(registre);
	}

	/**
	 * Acció "Custòdia": consulta l'estat de l'expedient ENI i del document a l'Arxiu, crea
	 * l'expedient a l'Arxiu si cal, puja/firma l'annex a l'Arxiu i valida el resultat de la
	 * firma. Compartit entre el servei antic ({@code AnnexosServiceImpl}, cridat des de la JSP
	 * i des de l'execució massiva) i el nou {@code RegistreAnnexResourceServiceImpl} (acció
	 * "Custòdia" de la pantalla React).
	 */
	@Transactional(readOnly = false)
	public ResultatAnnexDefinitiuDto guardarComADefinitiu(Long annexId) {

		logger.debug("Guardar com a definitiu l'annex " + annexId);
		RegistreAnnexEntity registreAnnex = registreAnnexRepository.findById(annexId).get();
		RegistreEntity registre = registreAnnex.getRegistre();
		String arxiuDistribucioUuid = null;
		ResultatAnnexDefinitiuDto resultatAnnexDefinitiu = new ResultatAnnexDefinitiuDto();
		resultatAnnexDefinitiu.setAnnexId(annexId);
		resultatAnnexDefinitiu.setAnnexTitol(registreAnnex.getTitol());
		resultatAnnexDefinitiu.setAnotacioNumero(registre.getNumero());

		// Comprovar si a Distribució hi ha l'annex ja marcat com a definitiu:
		AnnexEstat arxiuEstat = registreAnnex.getArxiuEstat();
		if ((arxiuEstat != null) && (arxiuEstat.equals(AnnexEstat.DEFINITIU))) {
			resultatAnnexDefinitiu.setKeyMessage("annex.accio.marcardefinitiu.jaDefinitiu");
			resultatAnnexDefinitiu.setOk(true);
			return resultatAnnexDefinitiu;
		}

		try {

			// Si el registre està tancat ja no cal continuar:
			ArxiuDetallDto arxiuDetall = registreService.getArxiuDetall(registre.getId());
			if (arxiuDetall.getEniEstat() == ExpedientEstatEnumDto.TANCAT) {
				resultatAnnexDefinitiu.setKeyMessage("annex.accio.marcardefinitiu.expedientTancat");
				resultatAnnexDefinitiu.setOk(false);
				return resultatAnnexDefinitiu;
			}

			// Si el document està com a definitiu en l'arxiu posar com a definitiu a distribució:
			Document document = pluginHelper.arxiuDocumentConsultar(
					registreAnnex.getFitxerArxiuUuid(), null, true, false, registre.getNumero());
			if (document.getEstat().equals(DocumentEstat.DEFINITIU)) {
				registreAnnex.setArxiuEstat(AnnexEstat.DEFINITIU);
				registreAnnexRepository.save(registreAnnex);
				resultatAnnexDefinitiu.setKeyMessage("annex.accio.marcardefinitiu.definitiuArxiu");
				resultatAnnexDefinitiu.setOk(false);
				return resultatAnnexDefinitiu;
			}

			// Si el document s'ha mogut a un expedient del backoffice no continuem
			if (document.getExpedientMetadades() != null) {
				String expedientUuid = document.getExpedientMetadades().getIdentificador();
				arxiuDistribucioUuid = registre.getArxiuUuid();
				if (!expedientUuid.equals(arxiuDistribucioUuid)) {
					resultatAnnexDefinitiu.setKeyMessage("annex.accio.marcardefinitiu.mogutBackoffice");
					resultatAnnexDefinitiu.setOk(false);
					return resultatAnnexDefinitiu;
				}
			}
		} catch (Exception ex) {
			resultatAnnexDefinitiu.setKeyMessage("annex.accio.marcardefinitiu.errorArxiu");
			resultatAnnexDefinitiu.setOk(false);
			resultatAnnexDefinitiu.setThrowable(ex);
			return resultatAnnexDefinitiu;
		}

		// Si arribem fins aquí podem reintentar guardar l'annex a l'arxiu i provarà de validar i firmar en cas que sigui necessari

		List<Throwable> exceptions = null;

		DistribucioRegistreAnotacio distribucioRegistreAnotacio =
				registreHelper.getDistribucioRegistreAnotacio(registre.getId());
		String unitatOrganitzativaCodi = distribucioRegistreAnotacio.getUnitatOrganitzativaCodi();

		if (distribucioRegistreAnotacio.getExpedientArxiuUuid() == null)
			exceptions = registreHelper.crearExpedientArxiu(
					distribucioRegistreAnotacio,
					unitatOrganitzativaCodi,
					arxiuDistribucioUuid);

		if (exceptions != null && !exceptions.isEmpty()) {
			resultatAnnexDefinitiu.setKeyMessage("annex.accio.marcardefinitiu.errorUpdate");
			resultatAnnexDefinitiu.setOk(false);
			resultatAnnexDefinitiu.setThrowable(exceptions.get(0));
			return resultatAnnexDefinitiu;
		}

		try {
			DistribucioRegistreAnnex distribucioRegistreAnnex = conversioTipusHelper.convertir(
					registreAnnex,
					DistribucioRegistreAnnex.class);

			registreHelper.crearAnnexInArxiu(
					annexId,
					distribucioRegistreAnnex,
					unitatOrganitzativaCodi,
					distribucioRegistreAnotacio.getExpedientArxiuUuid(),
					distribucioRegistreAnotacio.getProcedimentCodi());

			ValidacioFirmaEnum estatValidacioFirma = distribucioRegistreAnnex.getValidacioFirmaEstat();

			if (estatValidacioFirma != null &&
					(estatValidacioFirma.equals(ValidacioFirmaEnum.FIRMA_INVALIDA) || estatValidacioFirma.equals(ValidacioFirmaEnum.ERROR_VALIDANT))) {
				resultatAnnexDefinitiu.setKeyMessage("annex.accio.marcardefinitiu.errorFirma");
				resultatAnnexDefinitiu.setOk(false);
				return resultatAnnexDefinitiu;
			}

			entityManager.refresh(registreAnnex);

			if (registreAnnex.getArxiuEstat().equals(AnnexEstat.ESBORRANY)) {
				resultatAnnexDefinitiu.setKeyMessage("annex.accio.marcardefinitiu.senseFirma");
				resultatAnnexDefinitiu.setOk(false);
				return resultatAnnexDefinitiu;
			}

			resultatAnnexDefinitiu.setKeyMessage("annex.accio.marcardefinitiu.updated");
			resultatAnnexDefinitiu.setOk(true);
		} catch (Exception ex) {
			resultatAnnexDefinitiu.setKeyMessage("annex.accio.marcardefinitiu.errorUpdate");
			resultatAnnexDefinitiu.setOk(false);
			resultatAnnexDefinitiu.setThrowable(ex);
		}
		return resultatAnnexDefinitiu;
	}

}