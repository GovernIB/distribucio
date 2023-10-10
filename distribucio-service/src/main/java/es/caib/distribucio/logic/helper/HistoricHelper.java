package es.caib.distribucio.logic.helper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import es.caib.distribucio.logic.intf.dto.PermisDto;
import es.caib.distribucio.logic.intf.dto.PrincipalTipusEnumDto;
import es.caib.distribucio.logic.intf.dto.UnitatOrganitzativaDto;
import es.caib.distribucio.logic.intf.dto.historic.HistoricAnotacioDto;
import es.caib.distribucio.logic.intf.dto.historic.HistoricBustiaDto;
import es.caib.distribucio.logic.intf.dto.historic.HistoricDadesDto;
import es.caib.distribucio.logic.intf.dto.historic.HistoricDadesMostrarEnumDto;
import es.caib.distribucio.logic.intf.dto.historic.HistoricTipusEnumDto;
import es.caib.distribucio.logic.intf.registre.RegistreProcesEstatEnum;
import es.caib.distribucio.persist.entity.BustiaEntity;
import es.caib.distribucio.persist.entity.EntitatEntity;
import es.caib.distribucio.persist.entity.HistoricAnotacioEntity;
import es.caib.distribucio.persist.entity.HistoricBustiaEntity;
import es.caib.distribucio.persist.entity.HistoricEstatEntity;
import es.caib.distribucio.persist.entity.UnitatOrganitzativaEntity;
import es.caib.distribucio.persist.repository.EntitatRepository;
import es.caib.distribucio.persist.repository.HistoricAnotacioRepository;
import es.caib.distribucio.persist.repository.HistoricBustiaRepository;
import es.caib.distribucio.persist.repository.HistoricEstatRepository;
import es.caib.distribucio.persist.repository.UnitatOrganitzativaRepository;
import es.caib.distribucio.plugin.usuari.DadesUsuari;

/** 
 * Classe helper per a la generació i consulta de dades estadístiques i històriques.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class HistoricHelper {

	@Autowired
	EntitatRepository entitatRepository;
	@Autowired
	UnitatOrganitzativaRepository unitatOrganitzativaRepository;
	@Autowired
	PermisosHelper permisosHelper;
	@Autowired
	PluginHelper pluginHelper;
	@Autowired
	HistoricAnotacioRepository historicAnotacioRepository;
	@Autowired
	HistoricEstatRepository historicEstatRepository;
	@Autowired
	HistoricBustiaRepository historicBustiaRepository;
	@Autowired
	ConversioTipusHelper conversioTipusHelper;

	/** Calcula i guarda les dades històriques de:
	 * - Dades d'anotacions per UO
	 * - Dades per estat i UO
	 * - Dades d'usuaris de bústies per UO
	 * Guarda la dada per la data, actualitza el càlcul mensual i per 
	 * entitat.
	 * @param  
	 */
	@Transactional
	public void calcularDades(Date data) {
		
		logger.debug("Calculant dades històriques per la data " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.sss").format(data));
		
		Map<Long, EntitatEntity> entitats = new HashMap<>();
		Map<Long, UnitatOrganitzativaEntity> unitats = new HashMap<>();
		Map<Long, Set<String>> usuarisUo = new HashMap<>();
		Map<Long, Set<String>> usuarisEntitat = new HashMap<>();
	
		// Treu l'hora al dia
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime(data);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		data = calendar.getTime();
		
		// Invoca els diferents càlculs
		this.calcularDadesBustia(data, entitats, unitats, usuarisUo, usuarisEntitat);
		this.calcularDadesAnotacionsUO(data, entitats, unitats, usuarisUo, usuarisEntitat);
		this.calcularDadesEstats(data, entitats, unitats);
	}

	/** Recalcula les dades totals per un dia a partir de les dades del dia següent o del mes següent.
	 * @param  
	 */
	@Transactional
	public void recalcularTotals(Date data) {
		logger.debug("Recalculant els totals de les dades històriques per la data " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.sss").format(data));
		// Treu l'hora al dia
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime(data);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		data = calendar.getTime();
		
		// Recalcula els totals.
		calendar.add(Calendar.DATE, 1);
		Date diaSeguent = calendar.getTime();
		calendar.add(Calendar.DATE, -1);
		calendar.add(Calendar.MONTH, 1);
		Date mesSeguent = calendar.getTime();
		
		this.historicAnotacioRepository.recalcularTotals(
				data,
				diaSeguent,
				mesSeguent);
		this.historicEstatRepository.recalcularTotals(
				data,
				diaSeguent,
				mesSeguent);
	}

	/** Esborra les dades existents del dia, calcula les dades del dia per UO i entitat
	 * i esborra i calcula dades mensuals per anotacions per UO.
	 * @param data
	 * @param unitats 
	 * @param entitats 
	 * @param usuarisEntitat Map d'usuaris per entitat per informar la dada del volum d'usuaris
	 * @param usuarisUo  Map d'usuaris per UO per informar el volum d'usuaris per UO
	 */
	private void calcularDadesAnotacionsUO(Date data, Map<Long, EntitatEntity> entitats,
			Map<Long, UnitatOrganitzativaEntity> unitats, Map<Long, Set<String>> usuarisUo,
			Map<Long, Set<String>> usuarisEntitat) {

		EntitatEntity entitat;
		UnitatOrganitzativaEntity unitatOrganitzativa;
		
		// Esborra les dades estadístiques de la data
		historicAnotacioRepository.deleteByDataAndTipus(data, HistoricTipusEnumDto.DIARI);
		
		// calcula les dades d'anotacions per UO
		Map<Long, HistoricAnotacioEntity> dadesAnotacions = new HashMap<Long, HistoricAnotacioEntity>();
		HistoricAnotacioEntity dadaAnotacio;
		
		// - Anotacions
		Long anotacions;
		for(Object[] novesAnotacions : historicAnotacioRepository.getAnotacions(data)) {
			entitat  = this.getEntitat(entitats, (Long) novesAnotacions[0]);
			unitatOrganitzativa  = this.getUnitat(unitats, (Long) novesAnotacions[1]);
			anotacions  = (Long) novesAnotacions[2];
			dadaAnotacio = this.getDadaAnotacio(dadesAnotacions, this.getEntitat(entitats, (Long) novesAnotacions[0]), unitatOrganitzativa, data);
			dadaAnotacio.setAnotacions(anotacions);
		}
		
		// - Anotacions totals
		Long anotacionsTotal;
		for(Object[] anotacionsTotals : historicAnotacioRepository.getAnotacionsTotal()) {
			entitat  = this.getEntitat(entitats, (Long) anotacionsTotals[0]);
			unitatOrganitzativa  = this.getUnitat(unitats, (Long) anotacionsTotals[1]);
			anotacionsTotal  = (Long) anotacionsTotals[2];
			dadaAnotacio = this.getDadaAnotacio(dadesAnotacions, entitat, unitatOrganitzativa, data);
			dadaAnotacio.setAnotacionsTotal(anotacionsTotal);
		}
		// - Reenviaments
		Long reenviaments;
		for(Object[] reenviamentsData : historicAnotacioRepository.getReenviaments(data)) {
			entitat  = this.getEntitat(entitats, (Long) reenviamentsData[0]);
			unitatOrganitzativa  = this.getUnitat(unitats, (Long) reenviamentsData[1]);
			reenviaments  = (Long) reenviamentsData[2];
			dadaAnotacio = this.getDadaAnotacio(dadesAnotacions, entitat, unitatOrganitzativa, data);
			dadaAnotacio.setReenviaments(reenviaments);
		}
		
		// - Anotacions Per email
		Long emails;
		for(Object[] emailsData : historicAnotacioRepository.getEmails(data)) {
			entitat  = this.getEntitat(entitats, (Long) emailsData[0]);
			unitatOrganitzativa  = this.getUnitat(unitats, (Long) emailsData[1]);
			emails  = (Long) emailsData[2];
			dadaAnotacio = this.getDadaAnotacio(dadesAnotacions, entitat, unitatOrganitzativa, data);
			dadaAnotacio.setEmails(emails);
		}
		// - Justificants processats
		Long justificants;
		for(Object[] justificantsData : historicAnotacioRepository.getJustificants(data)) {
			entitat  = this.getEntitat(entitats, (Long) justificantsData[0]);
			unitatOrganitzativa  = this.getUnitat(unitats, (Long) justificantsData[1]);
			justificants  = (Long) justificantsData[2];
			dadaAnotacio = this.getDadaAnotacio(dadesAnotacions, entitat, unitatOrganitzativa, data);
			dadaAnotacio.setJustificants(justificants);
		}
		// - Annexos processats
		Long annexos;
		for(Object[] annexosData : historicAnotacioRepository.getAnnexos(data)) {
			entitat  = this.getEntitat(entitats, (Long) annexosData[0]);
			unitatOrganitzativa  = this.getUnitat(unitats, (Long) annexosData[1]);
			annexos  = (Long) annexosData[2];
			dadaAnotacio = this.getDadaAnotacio(dadesAnotacions, entitat, unitatOrganitzativa, data);
			dadaAnotacio.setAnnexos(annexos);
		}
		// - Número de bústies
		Long busties;
		for(Object[] bustiesData : historicAnotacioRepository.getBusties()) {
			entitat  = this.getEntitat(entitats, (Long) bustiesData[0]);
			unitatOrganitzativa  = this.getUnitat(unitats, (Long) bustiesData[1]);
			busties  = (Long) bustiesData[2];
			dadaAnotacio = this.getDadaAnotacio(dadesAnotacions, entitat, unitatOrganitzativa, data);
			dadaAnotacio.setBusties(busties);
		}
		// - Número d'usuaris
		for (Long unitatId : usuarisUo.keySet()) {
			if (usuarisUo.containsKey(unitatId))
				dadesAnotacions.get(unitatId).setUsuaris(Long.valueOf(usuarisUo.get(unitatId).size()));
		}
		usuarisUo.clear();
		
		// Guarda les noves dades
		historicAnotacioRepository.saveAll(dadesAnotacions.values());
		historicAnotacioRepository.flush();
		
		// Afegeix un registre amb els agregats d'anotacions amb codi UO null per cada entitat
		for (Object[] dadesEntitatAnotacions 
				: historicAnotacioRepository.getDadesPerEntitat(data)) {
			HistoricAnotacioEntity anotacioEntitat = new HistoricAnotacioEntity();
			anotacioEntitat.setEntitat(getEntitat(entitats, (Long)dadesEntitatAnotacions[0]));
			anotacioEntitat.setUnitat(null);
			anotacioEntitat.setTipus(HistoricTipusEnumDto.DIARI);
			anotacioEntitat.setData(data);
			anotacioEntitat.setAnotacions((Long) dadesEntitatAnotacions[1]); 
			anotacioEntitat.setAnotacionsTotal((Long) dadesEntitatAnotacions[2]); 
			anotacioEntitat.setReenviaments((Long) dadesEntitatAnotacions[3]); 
			anotacioEntitat.setEmails((Long) dadesEntitatAnotacions[4]); 
			anotacioEntitat.setJustificants((Long) dadesEntitatAnotacions[5]); 
			anotacioEntitat.setAnnexos((Long) dadesEntitatAnotacions[6]); 
			anotacioEntitat.setBusties(((Double) dadesEntitatAnotacions[7]).longValue()); 
			if (usuarisEntitat.containsKey(anotacioEntitat.getEntitat().getId())) {
				anotacioEntitat.setUsuaris(Long.valueOf(usuarisEntitat.get(anotacioEntitat.getEntitat().getId()).size())); 
			}
			historicAnotacioRepository.saveAndFlush(anotacioEntitat);
		}
		
		// Fa el sumatori pel mes i guarda les dades mensuals
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime(data);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		Date mesInici = calendar.getTime();
		calendar.add(Calendar.MONTH, 1);
		Date mesFi = calendar.getTime();
		
		// Esborra les dades mensuals
		historicAnotacioRepository.deleteByDataAndTipus(mesInici, HistoricTipusEnumDto.MENSUAL);
		
		// Les recalcula
		for(Object[] mesAnotacions : historicAnotacioRepository.getDadesPerMes(mesInici, mesFi)) {
			entitat  = getEntitat(entitats, (Long) mesAnotacions[0]);
			unitatOrganitzativa  = getUnitat( unitats, (Long) mesAnotacions[1]);
			HistoricAnotacioEntity historicAnotacioMes = new HistoricAnotacioEntity();
			historicAnotacioMes.setEntitat(entitat);
			historicAnotacioMes.setUnitat(unitatOrganitzativa);
			historicAnotacioMes.setTipus(HistoricTipusEnumDto.MENSUAL);
			historicAnotacioMes.setData(mesInici);
			
			historicAnotacioMes.setAnotacions((Long) mesAnotacions[2]);
			historicAnotacioMes.setAnotacionsTotal((Long) mesAnotacions[3]);
			historicAnotacioMes.setReenviaments((Long) mesAnotacions[4]);
			historicAnotacioMes.setEmails((Long) mesAnotacions[5]);
			historicAnotacioMes.setJustificants((Long) mesAnotacions[6]);
			historicAnotacioMes.setAnnexos((Long) mesAnotacions[7]);
			historicAnotacioMes.setBusties((Long) mesAnotacions[8]);
			historicAnotacioMes.setUsuaris((Long) mesAnotacions[9]);
			
			historicAnotacioRepository.save(historicAnotacioMes);
		}
	}

	/** Esborra les dades existents del dia, calcula les dades del dia per UO i entitat
	 * i esborra i calcula dades mensuals per estats per UO.
	 * @param data
	 * @param unitats 
	 * @param entitats 
	 */
	private void calcularDadesEstats(
			Date data,
			Map<Long, EntitatEntity> entitats,
			Map<Long, UnitatOrganitzativaEntity> unitats) {
		// Esborra les dades estadístiques de la data
		historicEstatRepository.deleteByDataAndTipus(data, HistoricTipusEnumDto.DIARI);
		
		// calcula les dades per estat i UO
		HistoricEstatEntity estat;
		for(Object[] dadesEstat : historicEstatRepository.getEstats(data)) {
			estat = new HistoricEstatEntity();
			estat.setEntitat(this.getEntitat(entitats, (Long) dadesEstat[0]));
			estat.setUnitat(this.getUnitat(unitats, (Long) dadesEstat[1]));
			estat.setTipus(HistoricTipusEnumDto.DIARI);
			estat.setData(data);
			estat.setEstat((RegistreProcesEstatEnum) dadesEstat[2]);
			estat.setCorrecte((Long) dadesEstat[3]);
			estat.setCorrecteTotal((Long) dadesEstat[4]);
			estat.setError((Long) dadesEstat[5]);
			estat.setErrorTotal((Long) dadesEstat[6]);
			estat.setTotal((Long) dadesEstat[7]);
		    historicEstatRepository.save(estat);
		}
		
		// Afegeix un registre amb els agregats d'anotacions amb codi UO null per cada entitat
		for (Object[] dadesEntitatEstats 
				: historicEstatRepository.getDadesPerEntitat(data)) {
			HistoricEstatEntity estatEntitat = new HistoricEstatEntity();
			estatEntitat.setEntitat(getEntitat(entitats, (Long)dadesEntitatEstats[0]));
			estatEntitat.setUnitat(null);
			estatEntitat.setTipus(HistoricTipusEnumDto.DIARI);
			estatEntitat.setData(data);
			estatEntitat.setEstat((RegistreProcesEstatEnum) dadesEntitatEstats[1]); 
			estatEntitat.setCorrecte((Long) dadesEntitatEstats[2]);
			estatEntitat.setCorrecteTotal((Long) dadesEntitatEstats[3]); 
			estatEntitat.setError((Long) dadesEntitatEstats[4]); 
			estatEntitat.setErrorTotal((Long) dadesEntitatEstats[5]); 
			estatEntitat.setTotal((Long) dadesEntitatEstats[6]); 
			historicEstatRepository.save(estatEntitat);
		}
		
		// Fa el sumatori pel mes i guarda les dades mensuals
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime(data);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		Date mesInici = calendar.getTime();
		calendar.add(Calendar.MONTH, 1);
		Date mesFi = calendar.getTime();
		
		// Esborra les dades mensuals
		historicEstatRepository.deleteByDataAndTipus(mesInici, HistoricTipusEnumDto.MENSUAL);
		
		// Les recalcula
		EntitatEntity entitat;
		UnitatOrganitzativaEntity unitatOrganitzativa;
		for(Object[] mesEstat : historicEstatRepository.getDadesPerMes(mesInici, mesFi)) {
			entitat  = getEntitat(entitats, (Long) mesEstat[0]);
			unitatOrganitzativa  = getUnitat( unitats, (Long) mesEstat[1]);
			HistoricEstatEntity historicEstatMes = new HistoricEstatEntity();
			historicEstatMes.setEntitat(entitat);
			historicEstatMes.setUnitat(unitatOrganitzativa);
			historicEstatMes.setTipus(HistoricTipusEnumDto.MENSUAL);
			historicEstatMes.setData(mesInici);
			
			historicEstatMes.setEstat((RegistreProcesEstatEnum) mesEstat[2]);
			historicEstatMes.setCorrecte((Long) mesEstat[3]);
			historicEstatMes.setCorrecteTotal((Long) mesEstat[4]);
			historicEstatMes.setError((Long) mesEstat[5]);
			historicEstatMes.setErrorTotal((Long) mesEstat[6]);
			historicEstatMes.setTotal((long) mesEstat[7]);
			
			historicEstatRepository.save(historicEstatMes);
		}
	}

	/** Esborra les dades existents del dia, calcula les dades del dia per bústies
	 * i esborra i calcula dades mensuals de bústies.
	 * @param data
	 * @param unitats 
	 * @param entitats 
	 * @param usuarisEntitat Map d'usuaris per entitatId per calcular l'agregat i número d'usuaris
	 * @param usuarisUo Map d'usuaris per unitatOrganitzativaId per calcular el volum d'usuaris per UO
	 */	
	private void calcularDadesBustia(
			Date data,
			Map<Long, EntitatEntity> entitats,
			Map<Long, UnitatOrganitzativaEntity> unitats,
			Map<Long, Set<String>> usuarisUo,
			Map<Long, Set<String>> usuarisEntitat) {
		// Esborra les dades estadístiques de la data
		historicBustiaRepository.deleteByDataAndTipus(data, HistoricTipusEnumDto.DIARI);	    
		
		// Calcula les dades d'usuaris per bústies actives, ha d'anar consultant els usuaris per rols
		// També guarda els usuaris per unitat organitzativa per informar les dades d'anotacions per unitat
		HistoricBustiaEntity bustia;
		List<PermisDto> permisosBustia;
		Set<String> usuarisPermis;
		Set<String> usuarisRol;
		Map<String, Set<String>> rols = new HashMap<>();
		
		for(Object[] dadesBustia : historicBustiaRepository.getBusties()) {
			bustia = new HistoricBustiaEntity();
			bustia.setEntitat(this.getEntitat(entitats, (Long) dadesBustia[0]));
			bustia.setUnitat(this.getUnitat(unitats, (Long) dadesBustia[1]));
			bustia.setTipus(HistoricTipusEnumDto.DIARI);
			bustia.setData(data);
			
			bustia.setBustiaId((Long) dadesBustia[2]);
			bustia.setNom((String) dadesBustia[3]);
			
			permisosBustia = permisosHelper.findPermisos(bustia.getBustiaId(), BustiaEntity.class);
			usuarisPermis = new HashSet<>();
			usuarisRol = new HashSet<>();
			for (PermisDto permis : permisosBustia) {
				if (PrincipalTipusEnumDto.ROL.equals(permis.getPrincipalTipus())) {
					// Rol
					usuarisRol.addAll(getUsuarisRol(rols, permis.getPrincipalNom()));
				} else {
					// Usuari
					usuarisPermis.add(permis.getPrincipalNom());
				}
			}
			bustia.setUsuarisPermis(Long.valueOf(usuarisPermis.size()));
			bustia.setUsuarisRol(Long.valueOf(usuarisRol.size()));
			usuarisPermis.addAll(usuarisRol);
			bustia.setUsuaris(Long.valueOf(usuarisPermis.size()));
			
			// Guarda els usuaris per UO
			if (!usuarisUo.containsKey(bustia.getUnitat().getId()))
				usuarisUo.put(bustia.getUnitat().getId(), new HashSet<String>());
			usuarisUo.get(bustia.getUnitat().getId()).addAll(usuarisPermis);
			// Guarda els usuaris per Entitat
			if (!usuarisEntitat.containsKey(bustia.getEntitat().getId()))
				usuarisEntitat.put(bustia.getEntitat().getId(), new HashSet<String>());
			usuarisEntitat.get(bustia.getEntitat().getId()).addAll(usuarisPermis);
			
		    historicBustiaRepository.save(bustia);
		}
		
		// Fa el sumatori pel mes i guarda les dades mensuals
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime(data);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		Date mesInici = calendar.getTime();
		calendar.add(Calendar.MONTH, 1);
		Date mesFi = calendar.getTime();
		
		// Esborra les dades mensuals
		historicBustiaRepository.deleteByDataAndTipus(mesInici, HistoricTipusEnumDto.MENSUAL);
		
		// Les recalcula
		EntitatEntity entitat;
		UnitatOrganitzativaEntity unitatOrganitzativa;
		for(Object[] mesBustia : historicBustiaRepository.getDadesPerMes(mesInici, mesFi)) {
			entitat  = getEntitat(entitats, (Long) mesBustia[0]);
			unitatOrganitzativa  = getUnitat( unitats, (Long) mesBustia[1]);
			HistoricBustiaEntity historicBustiaMes = new HistoricBustiaEntity();
			historicBustiaMes.setEntitat(entitat);
			historicBustiaMes.setUnitat(unitatOrganitzativa);
			historicBustiaMes.setTipus(HistoricTipusEnumDto.MENSUAL);
			historicBustiaMes.setData(mesInici);
			
			historicBustiaMes.setBustiaId((Long) mesBustia[2]);
			historicBustiaMes.setNom((String) mesBustia[3]);
			historicBustiaMes.setUsuaris(((Double) mesBustia[4]).longValue());
			historicBustiaMes.setUsuarisPermis(((Double) mesBustia[5]).longValue());
			historicBustiaMes.setUsuarisRol(((Double) mesBustia[6]).longValue());
			
			historicBustiaRepository.save(historicBustiaMes);
		}
	}

	private HistoricAnotacioEntity getDadaAnotacio(
			Map<Long, HistoricAnotacioEntity> dadesAnotacions, 
			EntitatEntity entitat,
			UnitatOrganitzativaEntity unitatOrganitzativa, 
			Date data) {
		HistoricAnotacioEntity historicAnotacioEntity = dadesAnotacions.get(unitatOrganitzativa.getId()) ;
		if (historicAnotacioEntity == null) {
			historicAnotacioEntity = new HistoricAnotacioEntity();
			historicAnotacioEntity.setEntitat(entitat);
			historicAnotacioEntity.setUnitat(unitatOrganitzativa);
			historicAnotacioEntity.setTipus(HistoricTipusEnumDto.DIARI);
			historicAnotacioEntity.setData(data);
			dadesAnotacions.put(unitatOrganitzativa.getId(), historicAnotacioEntity);
		}
		return historicAnotacioEntity;
	}

	private EntitatEntity getEntitat(Map<Long, EntitatEntity> entitats, Long entitatId) {
		EntitatEntity entitat = entitats.get(entitatId);
		if (entitat == null) {
			entitat = entitatRepository.getReferenceById(entitatId);
			entitats.put(entitatId, entitat);
		}
		return entitat;
	}

	private UnitatOrganitzativaEntity getUnitat(
			Map<Long, UnitatOrganitzativaEntity> unitats, Long unitatId) {
		if (unitatId == null)
			return null;
		UnitatOrganitzativaEntity unitat = unitats.get(unitatId);
		if (unitat == null) {
			unitat = unitatOrganitzativaRepository.getReferenceById(unitatId);
			unitats.put(unitatId, unitat);
		}
		return unitat;
	}

	private Set<String> getUsuarisRol(
			Map<String, Set<String>> rols, 
			String rol) {
		Set<String> usuaris = rols.get(rol);
		if (usuaris == null) {
			usuaris = new HashSet<>();
			List<DadesUsuari> usuarisGrup = pluginHelper.dadesUsuariFindAmbGrup(rol);
			if (usuarisGrup != null) {
				for (DadesUsuari usuariGrup : usuarisGrup)
					usuaris.add(usuariGrup.getCodi());
			}			
		}
		return usuaris;
	}

	/** Mètode per obtenir les dades històriques filtrades.
	 * 
	 * @param entitatId
	 * @param unitatsIds
	 * @param tipus
	 * @param dadesMostrar
	 * @param dataInici
	 * @param dataFi
	 * @return
	 */
	public HistoricDadesDto findDades(
			Long entitatId, 
			List<Long> unitatsIds, 
			HistoricTipusEnumDto tipus,
			List<HistoricDadesMostrarEnumDto> dadesMostrar,
			Date dataInici, 
			Date dataFi) {
		
		HistoricDadesDto dades = new HistoricDadesDto();
		boolean dadesEntitat = unitatsIds == null || unitatsIds.isEmpty();
		if (dadesEntitat) {
			if (unitatsIds == null)
				unitatsIds = new ArrayList<>();
			unitatsIds.add(0L);
			
		}
		if (dadesMostrar.contains(HistoricDadesMostrarEnumDto.UO)) {
			dades.setDadesAnotacions(
					conversioTipusHelper.convertirList(
							historicAnotacioRepository.findByFiltre(
									entitatId,
									dadesEntitat,
									unitatsIds,
									tipus,
									dataInici == null,
									dataInici,
									dataFi == null,
									dataFi), 
							HistoricAnotacioDto.class)					
				);
		}
		if (dadesMostrar.contains(HistoricDadesMostrarEnumDto.ESTAT)) {
			dades.setDadesEstats(
							historicEstatRepository.findAgregatsByFiltre(
									entitatId,
									dadesEntitat,
									unitatsIds,
									tipus,
									dataInici == null,
									dataInici,
									dataFi == null,
									dataFi)
				);
		}
		if (dadesMostrar.contains(HistoricDadesMostrarEnumDto.BUSTIES)) {
			dades.setDadesBusties(
					conversioTipusHelper.convertirList(
							historicBustiaRepository.findByFiltre(
									entitatId,
									dadesEntitat,
									unitatsIds,
									tipus,
									dataInici == null,
									dataInici,
									dataFi == null,
									dataFi), 
							HistoricBustiaDto.class)
				);
		}
		if (dadesEntitat) {
			// Completa la informació de la unitat
			EntitatEntity entitat = entitatRepository.getReferenceById(entitatId);
			UnitatOrganitzativaDto unitat = conversioTipusHelper.convertir(
					unitatOrganitzativaRepository.findByCodi(entitat.getCodiDir3()), 
					UnitatOrganitzativaDto.class);
			if (dades.getDadesAnotacions() != null) {
				for  (HistoricAnotacioDto anotacio : dades.getDadesAnotacions())
					anotacio.setUnitat(unitat);
			}
			// Per les bústies no cal fer res, venen informades correctament
		}
		return dades;
	}

	private static final Logger logger = LoggerFactory.getLogger(HistoricHelper.class);

}
