package es.caib.distribucio.logic.helper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.springframework.stereotype.Component;

import es.caib.distribucio.logic.intf.base.util.I18nUtil;
import es.caib.distribucio.logic.intf.dto.ReglaTipusEnumDto;
import es.caib.distribucio.persist.entity.ReglaEntity;
import es.caib.distribucio.persist.repository.ReglaRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Validacions d'una regla que necessiten consultar les altres regles (codis SIA de les regles de tipus
 * backoffice i unicitat de nom+tipus+assumpte). Compartit entre la UI antiga
 * ({@code ReglaServiceImpl.findReglesByCodisSiaAndTramits}) i el recurs React
 * ({@code ReglaResourceServiceImpl}).
 *
 * @author Límit Tecnologies
 */
@Component
@RequiredArgsConstructor
public class ReglaValidacioHelper {

	private static final String MSG_PREFIX = "regla.validacio.";

	private final ReglaRepository reglaRepository;

	/** Coincidència d'una regla amb un codi SIA i un tràmit concrets. */
	@Getter
	@RequiredArgsConstructor
	public static class Match {
		private final ReglaEntity regla;
		private final String sia;
		private final String tramit;
	}

	/** Cerca les regles que contenen algun dels codis SIA (procediment o servei) i tràmits indicats. */
	public List<Match> findReglesByCodisSiaAndTramits(List<String> sias, List<String> tramits) {
		if (sias == null || sias.isEmpty()) {
			return Collections.emptyList();
		}
		List<ReglaEntity> resultats = new ArrayList<>();
		for (String sia : sias) {
			if (tramits != null && !tramits.isEmpty()) {
				for (String tramit : tramits) {
					resultats.addAll(reglaRepository.findReglaByCodiSiaAndTramit(sia, false, tramit));
				}
			} else {
				resultats.addAll(reglaRepository.findReglaByCodiSiaAndTramit(sia, true, null));
			}
		}
		List<Match> result = new ArrayList<>();
		Set<String> alreadyAdded = new HashSet<>();
		for (ReglaEntity regla : resultats) {
			List<String> siasRegla = new ArrayList<>();
			siasRegla.addAll(split(regla.getProcedimentCodiFiltre()));
			siasRegla.addAll(split(regla.getServeiCodiFiltre()));
			List<String> tramitsRegla = split(regla.getTramitCodiFiltre());
			for (String sia : sias) {
				if (!siasRegla.contains(sia)) {
					continue;
				}
				if (regla.getTramitCodiFiltre() == null) {
					if (alreadyAdded.add(regla.getId() + "|" + sia + "|null")) {
						result.add(new Match(regla, sia, null));
					}
				} else {
					for (String tramit : tramits) {
						if (tramitsRegla.contains(tramit) && alreadyAdded.add(regla.getId() + "|" + sia + "|" + tramit)) {
							result.add(new Match(regla, sia, tramit));
						}
					}
				}
			}
		}
		return result;
	}

	/**
	 * Comprova que cap altra regla de tipus BACKOFFICE tingui els mateixos codis SIA (procediment o servei)
	 * i tràmits amb la mateixa unitat organitzativa de filtre (o també sense unitat).
	 *
	 * @return missatges d'error ja traduïts (buit si tot és correcte).
	 */
	public List<String> validarCodisSiaBackoffice(
			Long reglaId,
			Long entitatId,
			Long unitatFiltreId,
			String procedimentCodiFiltre,
			String serveiCodiFiltre,
			String tramitCodiFiltre) {
		List<String> errors = new ArrayList<>();
		List<String> tramits = split(tramitCodiFiltre);
		for (String codis : Arrays.asList(procedimentCodiFiltre, serveiCodiFiltre)) {
			List<String> sias = split(codis);
			if (sias.isEmpty()) {
				continue;
			}
			// Un missatge per cada parella SIA-tràmit amb les regles que hi coincideixen.
			Map<String, List<String>> reglesPerCodi = new LinkedHashMap<>();
			Map<String, String> unitatPerCodi = new LinkedHashMap<>();
			for (Match match : findReglesByCodisSiaAndTramits(sias, tramits)) {
				ReglaEntity regla = match.getRegla();
				if (Objects.equals(regla.getId(), reglaId) || !ReglaTipusEnumDto.BACKOFFICE.equals(regla.getTipus())) {
					continue;
				}
				Long unitatReglaId = regla.getUnitatOrganitzativaFiltre() != null
						? regla.getUnitatOrganitzativaFiltre().getId()
						: null;
				if (!Objects.equals(unitatFiltreId, unitatReglaId)) {
					continue;
				}
				String codi = match.getSia() + (match.getTramit() != null ? " - " + match.getTramit() : "");
				String nomRegla = regla.getNom();
				if (regla.getEntitat() != null && !regla.getEntitat().getId().equals(entitatId)) {
					nomRegla += " (" + regla.getEntitat().getNom() + ")";
				}
				reglesPerCodi.computeIfAbsent(codi, k -> new ArrayList<>()).add(nomRegla);
				unitatPerCodi.put(codi, regla.getUnitatOrganitzativaFiltre() != null
						? regla.getUnitatOrganitzativaFiltre().getDenominacio()
						: I18nUtil.getInstance().getI18nMessage(MSG_PREFIX + "unitat.buida"));
			}
			for (Map.Entry<String, List<String>> entry : reglesPerCodi.entrySet()) {
				errors.add(I18nUtil.getInstance().getI18nMessage(
						MSG_PREFIX + "backoffice.codisia.igualUnitat.existent",
						String.join(", ", entry.getValue()),
						entry.getKey(),
						unitatPerCodi.get(entry.getKey())));
			}
		}
		return errors;
	}

	/** Indica si ja existeix una altra regla a l'entitat amb el mateix nom, tipus i codi d'assumpte. */
	public boolean existeixNomTipusAssumpte(
			Long entitatId,
			String nom,
			ReglaTipusEnumDto tipus,
			String assumpteCodi,
			Long excloureId) {
		boolean assumpteBuit = assumpteCodi == null || assumpteCodi.trim().isEmpty();
		return reglaRepository.countByNomTipusAssumpte(
				entitatId,
				nom.trim(),
				tipus,
				assumpteBuit,
				assumpteBuit ? "" : assumpteCodi,
				excloureId != null ? excloureId : -1L) > 0;
	}

	public String missatgeNomDuplicat() {
		return I18nUtil.getInstance().getI18nMessage(MSG_PREFIX + "nom.mult.uk");
	}

	private static List<String> split(String value) {
		if (value == null || value.trim().isEmpty()) {
			return Collections.emptyList();
		}
		return Arrays.asList(value.trim().split("\\s+"));
	}

}
