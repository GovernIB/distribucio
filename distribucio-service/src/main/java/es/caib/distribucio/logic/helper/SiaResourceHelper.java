/**
 *
 */
package es.caib.distribucio.logic.helper;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import es.caib.distribucio.logic.intf.dto.UpdateProgressDto;
import es.caib.distribucio.logic.intf.dto.RegistreClassificarTipusEnum;
import es.caib.distribucio.persist.resourceentity.EntitatResourceEntity;
import es.caib.distribucio.persist.resourceentity.SiaResourceEntity;
import es.caib.distribucio.persist.resourceentity.UnitatOrganitzativaResourceEntity;
import es.caib.distribucio.persist.resourcerepository.EntitatResourceRepository;
import es.caib.distribucio.persist.resourcerepository.SiaResourceRepository;
import es.caib.distribucio.persist.resourcerepository.UnitatOrganitzativaResourceRepository;
import es.caib.distribucio.plugin.procediment.Procediment;
import es.caib.distribucio.plugin.servei.Servei;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import es.caib.distribucio.logic.intf.dto.ProcedimentEstatEnumDto;
import es.caib.distribucio.plugin.Link;
import es.caib.distribucio.plugin.procediment.UnitatAdministrativa;

/**
 * Helper per operar amb procediments.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
@RequiredArgsConstructor
public class SiaResourceHelper {

    private final EntitatResourceRepository entitatResourceRepository;
    private final PluginHelper pluginHelper;
    private final SiaResourceRepository siaResourceRepository;

    /** Progrés d'acualització actual.*/
    public static Map<Long, UpdateProgressDto> progressosActualitzacio = new ConcurrentHashMap<Long, UpdateProgressDto>();
    public static Map<Long, UpdateProgressDto> serveisActualitzacio = new ConcurrentHashMap<Long, UpdateProgressDto>();
    private final UnitatOrganitzativaResourceRepository unitatOrganitzativaResourceRepository;

    @Getter
    @Setter
    @Builder
    public static class Sia {
        private String codigo;
        private String codiSia;
        private String nombre;
        private Link unidadAdministrativa;
        private Link organoInstructor;
        private boolean comun;
    }

    /** Troba la unitat organitzativa de la BBDD a partir de les dades del procediment de Distribucio. Si no troba
     * la UO amb codi SIA del procediments afegeix un avís al progrés per a que s'actualitzi l'arbre d'unitats.
     * @param unitatsOranitzatives
     * @param sia
     * @param codiDir3
     * @return
     */
    private UnitatOrganitzativaResourceEntity resoldreUnitatOrganitzativa(
            Map<String, UnitatOrganitzativaResourceEntity> unitatsOranitzatives,
            Sia sia,
            String codiDir3) {

        UnitatOrganitzativaResourceEntity uo = null;
        Link unidadAdministrativaLink = sia.getUnidadAdministrativa();
        Link organoInstructorLink = sia.getOrganoInstructor();
        String unidadAdministrativaCodigo = null;
        if (unidadAdministrativaLink!=null) {
            unidadAdministrativaCodigo = unidadAdministrativaLink.getCodigo();
        } else if (organoInstructorLink != null) {
            unidadAdministrativaCodigo = organoInstructorLink.getCodigo();
        }
        if (unidadAdministrativaCodigo != null) {
            if (!unitatsOranitzatives.containsKey(unidadAdministrativaCodigo)) {
                String codi = null;
                // Cerca a rolsac el codi dir3 de la unitat organitzativa del procediment, si no en té va cercant en els pares
                UnitatAdministrativa unitatAdministrativa = null;
                String codiUnitatArrel = unidadAdministrativaCodigo;
                boolean error = false;
                do {
                    // Consulta de la unitat administrativa per codi a Rolsac amb 5 reintents
                    int reintents = 0;
                    do {
                        try {
                            unitatAdministrativa =
                                    pluginHelper.procedimentGetUnitatAdministrativa(codi);
                        } catch (Exception e) {
                            logger.error("Error consultant la unitat organitzativa amb codi " + codi + " a Rolsac: pel procediment " +
                                    sia.getCodiSia()  + e.toString());
                        }
                        error = reintents++ >= 5;
                    } while (unitatAdministrativa == null && !error);

                    if (unitatAdministrativa != null) {
                        if (unitatAdministrativa.getCodiDir3() != null ) {
                            codiDir3 = unitatAdministrativa.getCodiDir3();
                            uo = unitatOrganitzativaResourceRepository.findByEntitatCodiDir3AndCodi(codiDir3, codiUnitatArrel);
                            if (uo == null && unitatAdministrativa.getPareCodi() != null) {
                                codi = unitatAdministrativa.getPareCodi();
                            } else {
                                codi = null;
                            }
                        } else if (unitatAdministrativa.getPareCodi() != null) {
                            // Cerca el codi Dir3 en la unitat administrativa pare
                            codi = unitatAdministrativa.getPareCodi();
                        } else {
                            codi = null;
                        }
                    } else {
                        codi = null;
                    }
                }
                while(uo == null
                        && codi != null);

                if (uo == null) {
                    try {
                        uo = unitatOrganitzativaResourceRepository.findByEntitatCodiDir3AndCodi(codiDir3, codiDir3);
                    }catch(Exception e) {
                        logger.debug("No s'ha trobat el paràmetre amb codi "+ codiDir3);
                    }

                }
                unitatsOranitzatives.put(unidadAdministrativaCodigo, uo);
            } else {
                uo = unitatsOranitzatives.get(unidadAdministrativaCodigo);
            }
        }
        return uo;
    }

    @Async
    @Transactional
    public void findAndUpdateProcediments(Long entitatId) throws Exception {

        String msgInfo;
        UpdateProgressDto progres = null;
        // Comprova si hi ha una altre instància del procés en execució
        if (isUpdatingProcediments(entitatId)) {
            logger.debug("Ja existeix un altre procés que està executant l'actualització de procediments per l'entitat " + entitatId + ".");
            return;	// S'està executant l'actualitzacio
        } else {
            progres = new UpdateProgressDto();
            progressosActualitzacio.put(entitatId, progres);
        }

        EntitatResourceEntity entitat = entitatResourceRepository.findById(entitatId).orElse(null);
        ConfigHelper.setEntitatActualCodi(entitat.getCodi());
        msgInfo = "Inici del procés d'actualització de procediments de l'entitat " + entitat.getCodi() + " " + entitat.getNom();
        progres.setEstat(UpdateProgressDto.Estat.INICIALITZANT);
        logger.info(msgInfo);

        List<Sia> procedimentList = null;
        int reintents = 1;
        boolean errorConsultaProcediments = false;
        Exception exConsultaProcediments = null;
        String errMsg = "-";
        do {
            try {
                msgInfo = "Obtenint el llistat de procediments per a l'entitat " + entitat.getCodiDir3();
                logger.info(msgInfo);
                procedimentList = pluginHelper.procedimentFindByCodiDir3(entitat.getCodiDir3()).stream()
                        .map(this::toSia)
                        .collect(Collectors.toList());
            } catch (Exception e) {
                exConsultaProcediments = e;
                errMsg = "Error consultant els procediments per l'entitat: " + entitat.getCodiDir3();
                errorConsultaProcediments = reintents++ >= 3;
            }
        }
        while (procedimentList == null && !errorConsultaProcediments);

        try {
            // Comprova si hi ha hagut errors consultant els procediments
            if (errorConsultaProcediments) {
                String errorMessage = exConsultaProcediments.getMessage() != null ? exConsultaProcediments.getMessage() : errMsg;
                throw new Exception(errorMessage, exConsultaProcediments);
            }

            if (procedimentList == null || procedimentList.isEmpty()) {
                throw new Exception(
                        "No s'ha obtingut cap llista o resultat per la consulta de procediments: (llista " + (procedimentList == null? "nul·la" :  "buida") + ")"
                );
            }

            // Processa els procediments consultats
            msgInfo="S'han obtingut " + procedimentList.size() + " procediments vigents a Distribucio.";
            logger.info(msgInfo);
            progres.setEstat(UpdateProgressDto.Estat.ACTUALITZANT);
            progres.setTotal(procedimentList.size());

            // Crea un Map amb els procediments de Distribucio per codi
            Map<String, Sia> procedimentMap = new HashMap<String, Sia>();
            for (Sia procediment : procedimentList) {
                procedimentMap.put(procediment.getCodigo(), procediment);
            }

            // Deshabilita els procediments que no hagi retornat Distribucio
            actualtizarSiaNoVigents(entitatId, procedimentMap, RegistreClassificarTipusEnum.PROCEDIMENT);

            // Processa tots els procediments, actualitza-ne la informació, donant-los d'alta i revisant la seva UO
            msgInfo = "Es procedeix a processar els " + procedimentList.size() + " procediments consultats a Distribucio.";
            logger.info(msgInfo);

            // Map<codi unitat rolsac, unitatOrganitzativa> per no haver de consultar la UO de totes les unitats per codi rolsac
            Map<String, UnitatOrganitzativaResourceEntity> unitatsOrganitzatives = new HashMap<String, UnitatOrganitzativaResourceEntity>();
            for (Sia procediment : procedimentList) {
                // Tracta el procediment en una transacció a part.
                actualitzaSia(procediment, unitatsOrganitzatives, entitat, RegistreClassificarTipusEnum.PROCEDIMENT);
                progres.incProcessats();
            }

            progres.setEstat(UpdateProgressDto.Estat.FINALITZAT);
        } catch (Exception e) {
            progres.setEstat(UpdateProgressDto.Estat.ERROR);
            progres.setErrorMsg(e.getMessage());
        }
    }

    public SiaResourceEntity findAndUpdateProcediment(Long entitatId, String procedimentCodi) throws Exception {
        EntitatResourceEntity entitat = entitatResourceRepository.findById(entitatId).orElse(null);

        Sia procediment = null;
        int reintents = 1;
        boolean errorConsultaServeis = false;
        Exception exConsultaServeis = null;
        String errMsg = "-";
        do {
            try {
                procediment = this.toSia(pluginHelper.procedimentGetByCodi(procedimentCodi));
            } catch (Exception e) {
                exConsultaServeis = e;
                errMsg = "Error consultant el procediment per codi: " + procedimentCodi;
            }
            errorConsultaServeis = reintents++ >= 3;
        }
        while (procediment == null && !errorConsultaServeis);

        // Comprova si hi ha hagut errors consultant els procediments
        if (errorConsultaServeis && exConsultaServeis != null) {
            String errorMessage = exConsultaServeis.getMessage() != null ? exConsultaServeis.getMessage() : errMsg;
            throw new Exception(errorMessage, exConsultaServeis);
        }

        if (procediment == null) {
            throw new Exception(
                    "No s'ha obtingut cap resultat per la consulta de procediment: (" + procedimentCodi + ")"
            );
        }

        // Map<codi unitat rolsac, unitatOrganitzativa> per no haver de consultar la UO de totes les unitats per codi rolsac
        Map<String, UnitatOrganitzativaResourceEntity> unitatsOrganitzatives = new HashMap<String, UnitatOrganitzativaResourceEntity>();

        return actualitzaSia(procediment, unitatsOrganitzatives, entitat, RegistreClassificarTipusEnum.PROCEDIMENT);
    }

    @Async
    @Transactional
    public void findAndUpdateServeis(Long entitatId) throws Exception {

        String msgInfo;
        UpdateProgressDto progres = null;
        // Comprova si hi ha una altre instància del procés en execució
        if (isUpdatingServeis(entitatId)) {
            logger.debug("Ja existeix un altre procés que està executant l'actualització de serveis per l'entitat " + entitatId + ".");
            return;    // S'està executant l'actualitzacio
        } else {
            progres = new UpdateProgressDto();
            serveisActualitzacio.put(entitatId, progres);
        }

        EntitatResourceEntity entitat = entitatResourceRepository.getReferenceById(entitatId);
        // Els plugins s'instancien per entitat i llegeixen el codi de l'entitat actual del
        // ThreadLocal de ConfigHelper. A la interfície JSP l'hi deixa LlistaEntitatsInterceptor,
        // però les peticions de la interfície REACT (/api/**) estan excloses dels interceptors
        // (veure INTERCEPTOR_EXCLUSIONS a WebMvcConfig), així que el fixam aquí a partir de
        // l'entitat que rep el mètode -- mateix patró que RegistreServiceImpl i SegonPlaServiceImpl.
        ConfigHelper.setEntitatActualCodi(entitat.getCodi());
        msgInfo = "Inici del procés d'actualització de serveis de l'entitat " + entitat.getCodi() + " " + entitat.getNom();
        progres.setEstat(UpdateProgressDto.Estat.INICIALITZANT);
        logger.info(msgInfo);

        List<Sia> serveiList = null;
        int reintents = 1;
        boolean errorConsultaServeis = false;
        Exception exConsultaServeis = null;
        String errMsg = "-";
        do {
            try {
                msgInfo = "Obtenint el llistat de serveis per a l'entitat " + entitat.getCodiDir3();
                logger.info(msgInfo);
                serveiList = pluginHelper.serveiFindByCodiDir3(entitat.getCodiDir3()).stream()
                        .map(this::toSia)
                        .collect(Collectors.toList());
            } catch (Exception e) {
                exConsultaServeis = e;
                errMsg = "Error consultant els serveis per l'entitat: " + entitat.getCodiDir3();
                errorConsultaServeis = reintents++ >= 3;
            }
        }
        while (serveiList == null && !errorConsultaServeis);

        try {
            // Comprova si hi ha hagut errors consultant els serveis
            if (errorConsultaServeis) {
                String errorMessage = exConsultaServeis.getMessage() != null ? exConsultaServeis.getMessage() : errMsg;
                throw new Exception(errorMessage, exConsultaServeis);
            }

            if (serveiList == null || serveiList.isEmpty()) {
                throw new Exception(
                        "No s'ha obtingut cap llista o resultat per la consulta de serveis: (llista " + (serveiList == null ? "nul·la" : "buida") + ")"
                );
            }

            // Processa els serveis consultats
            msgInfo = "S'han obtingut " + serveiList.size() + " serveis vigents a Rolsac.";
            logger.info(msgInfo);
            progres.setEstat(UpdateProgressDto.Estat.ACTUALITZANT);
            progres.setTotal(serveiList.size());

            // Crea un Map amb els serveis de Distribucio per codi
            Map<String, Sia> serveiMap = new HashMap<String, Sia>();
            for (Sia servei : serveiList) {
                serveiMap.put(servei.getCodigo(), servei);
            }

            // Deshabilita els serveis que no hagi retornat Distribucio
            actualtizarSiaNoVigents(entitatId, serveiMap, RegistreClassificarTipusEnum.SERVEI);

            // Processa tots els serveis, actualitza-ne la informació, donant-los d'alta i revisant la seva UO
            msgInfo = "Es procedeix a processar els " + serveiList.size() + " serveis consultats a Rolsac.";
            logger.info(msgInfo);

            // Map<codi unitat rolsac, unitatOrganitzativa> per no haver de consultar la UO de totes les unitats per codi rolsac
            Map<String, UnitatOrganitzativaResourceEntity> unitatsOrganitzatives = new HashMap<String, UnitatOrganitzativaResourceEntity>();
            for (Sia servei : serveiList) {
                // Tracta el servei en una transacció a part.
                actualitzaSia(servei, unitatsOrganitzatives, entitat, RegistreClassificarTipusEnum.SERVEI);
                progres.incProcessats();
            }

            progres.setEstat(UpdateProgressDto.Estat.FINALITZAT);
        } catch (Exception e) {
            progres.setEstat(UpdateProgressDto.Estat.ERROR);
            progres.setErrorMsg(e.getMessage());
        }
    }

    public SiaResourceEntity findAndUpdateServei(Long entitatId, String serveiCodi) throws Exception {
        EntitatResourceEntity entitat = entitatResourceRepository.getReferenceById(entitatId);
        // Els plugins s'instancien per entitat i llegeixen el codi de l'entitat actual del
        // ThreadLocal de ConfigHelper. A la interfície JSP l'hi deixa LlistaEntitatsInterceptor,
        // però les peticions de la interfície REACT (/api/**) estan excloses dels interceptors
        // (veure INTERCEPTOR_EXCLUSIONS a WebMvcConfig), així que el fixam aquí a partir de
        // l'entitat que rep el mètode -- mateix patró que RegistreServiceImpl i SegonPlaServiceImpl.
        ConfigHelper.setEntitatActualCodi(entitat.getCodi());

        Sia servei = null;
        int reintents = 1;
        boolean errorConsultaServeis = false;
        Exception exConsultaServeis = null;
        String errMsg = "-";
        do {
            try {
                servei = this.toSia(pluginHelper.serveiGetByCodi(serveiCodi));
            } catch (Exception e) {
                exConsultaServeis = e;
                errMsg = "Error consultant el servei per codi: " + serveiCodi;
            }
            errorConsultaServeis = reintents++ >= 3;
        }
        while (servei == null && !errorConsultaServeis);

        // Comprova si hi ha hagut errors consultant els serveis
        if (errorConsultaServeis && exConsultaServeis != null) {
            String errorMessage = exConsultaServeis.getMessage() != null ? exConsultaServeis.getMessage() : errMsg;
            throw new Exception(errorMessage, exConsultaServeis);
        }

        if (servei == null) {
            throw new Exception(
                    "No s'ha obtingut cap resultat per la consulta de servei: (" + serveiCodi + ")"
            );
        }

        // Map<codi unitat rolsac, unitatOrganitzativa> per no haver de consultar la UO de totes les unitats per codi rolsac
        Map<String, UnitatOrganitzativaResourceEntity> unitatsOrganitzatives = new HashMap<String, UnitatOrganitzativaResourceEntity>();

        return actualitzaSia(servei, unitatsOrganitzatives, entitat, RegistreClassificarTipusEnum.SERVEI);
    }

    /** Mètode per tractar per separat un servei vigent de Distribucio. Es consultarà la seva UO i es determinarà si s'ha d'actualtizar, crear o deixar tal
     * com està.
     *
     * @param sia
     * 			Servei consultat a Distribucio.
     * @param unitatsOrganitzatives
     * 			Map amb les unitats organitzatives per codi per no haver-les de consultar per cada servei.
     * @param entitatEntity
     * 			La entitat per a actualitzar el servei
     */
    @Transactional( propagation = Propagation.REQUIRES_NEW)
    public SiaResourceEntity actualitzaSia(
            Sia sia,
            Map<String, UnitatOrganitzativaResourceEntity> unitatsOrganitzatives,
            EntitatResourceEntity entitatEntity,
            RegistreClassificarTipusEnum tipus) {

        String msgInfo;
        msgInfo = sia.getCodiSia() + " - " + sia.getNombre();
        logger.info(msgInfo);

        SiaResourceEntity siaResourceEntity = null;
        try {
            // Determina la unitat organitzativa
            UnitatOrganitzativaResourceEntity unitatOrganitzativa = this.resoldreUnitatOrganitzativa(
                    unitatsOrganitzatives,
                    sia,
                    entitatEntity.getCodiDir3());
            // Consulta el sia a la BBDD
            siaResourceEntity = siaResourceRepository.findByEntitatIdAndCodi(entitatEntity.getId(), sia.getCodigo()).orElse(null);
            if (siaResourceEntity == null) {
                // Crea el nou procediment
                siaResourceEntity = SiaResourceEntity.builder()
                        .codi(sia.getCodigo())
                        .nom(sia.getNombre())
                        .codiSia(sia.getCodiSia())
                        .unitatOrganitzativa(unitatOrganitzativa)
                        .entitat(entitatEntity)
                        .estat(ProcedimentEstatEnumDto.VIGENT)
                        .tipus(tipus)
                        .comu(sia.isComun())
                        .build();
                siaResourceRepository.save(siaResourceEntity);
            } else {
                // Procediment existent. Comprova si s'ha d'actualitzar el sia
                if (!sia.getCodiSia().equals(siaResourceEntity.getCodiSia())) {
                    siaResourceEntity.setCodiSia(sia.getCodiSia());
                }
                if (sia.getNombre() != null && !sia.getNombre().equals(siaResourceEntity.getNom())) {
                    siaResourceEntity.setNom(sia.getNombre());
                }
                if (!siaResourceEntity.getEstat().equals(ProcedimentEstatEnumDto.VIGENT)) {
                    siaResourceEntity.setEstat(ProcedimentEstatEnumDto.VIGENT);
                }
                if (!Objects.equals(unitatOrganitzativa.getId(), siaResourceEntity.getUnitatOrganitzativa().getId())) {
                    siaResourceEntity.setUnitatOrganitzativa(unitatOrganitzativa);
                }
                if (sia.isComun() != siaResourceEntity.isComu()) {
                    siaResourceEntity.setComu(sia.isComun());
                }
            }
        } catch(Exception e) {
            logger.error("Error actualitzant el sia: " + e.toString());
        }

        return siaResourceEntity;
    }

    /** Consutla la llista de sias de BBDD i marca com a extingits els que no hagi retornat la consulta a Rolsac.
     *
     * @param siaMap Map amb els sias de Distribucio.
     */
    @Transactional( propagation = Propagation.REQUIRES_NEW)
    public void actualtizarSiaNoVigents(
            Long entitatId,
            Map<String, Sia> siaMap,
            RegistreClassificarTipusEnum tipus) {

        String msgInfo;
        msgInfo = "Actualització de sias no vigents";
        logger.info(msgInfo);

        // Consulta els sias vigents
        List<SiaResourceEntity> siaVigents = siaResourceRepository.findAllByEntitatIdAndEstatAndTipus(entitatId, ProcedimentEstatEnumDto.VIGENT, tipus);
        msgInfo = "Actualment a la BBDD hi ha " + siaVigents.size() + " sias vigents.";
        logger.info(msgInfo);

        List<String> siasExtingits = new ArrayList<String>();
        for (SiaResourceEntity siaEntity : siaVigents) {
            if (!siaMap.containsKey(siaEntity.getCodi())) {
                siaEntity.setEstat(ProcedimentEstatEnumDto.EXTINGIT);
                siasExtingits.add(siaEntity.getCodiSia() + " - " + siaEntity.getNom());
            }
        }

        if (!siasExtingits.isEmpty()) {
            msgInfo="S'han marcat com a extingits " + siasExtingits.size() + " sias";
            logger.info(msgInfo);
        } else {
            msgInfo="No s'ha marcat cap sia com a extingit";
            logger.info(msgInfo);
        }
        msgInfo = " dels " + siaVigents.size() + " sias que estaven vigents.";
        logger.info(msgInfo);
    }

    public RegistreClassificarTipusEnum getTipusSiaByCodi(Long entitatId, String codiSia) {
        return siaResourceRepository.findByEntitatIdAndCodiSia(entitatId, codiSia)
                .map(SiaResourceEntity::getTipus).orElse(null);
    }


    public boolean isUpdatingProcediments(Long entitatId) {
        UpdateProgressDto progres = progressosActualitzacio.get(entitatId);
        return progres != null
                && progres.getEstat() != UpdateProgressDto.Estat.FINALITZAT
                && progres.getEstat() != UpdateProgressDto.Estat.ERROR;
    }

    public boolean isUpdatingServeis(Long entitatId) {
        UpdateProgressDto progres = serveisActualitzacio.get(entitatId);
        return progres != null
                && progres.getEstat() != UpdateProgressDto.Estat.FINALITZAT
                && progres.getEstat() != UpdateProgressDto.Estat.ERROR;
    }

    private Sia toSia(Procediment procediment) {
        return Sia.builder()
                .codigo(procediment.getCodigo())
                .nombre(procediment.getNombre())
                .codiSia(procediment.getCodigoSIA() != null ?procediment.getCodigoSIA() :procediment.getCodigoSia())
                .unidadAdministrativa(procediment.getUnidadAdministrativa())
                .comun(procediment.isComun())
                .build();
    }

    private Sia toSia(Servei servei) {
        return Sia.builder()
                .codigo(servei.getCodigo())
                .nombre(servei.getNombre())
                .codiSia(servei.getCodigoSIA() != null ?servei.getCodigoSIA() :servei.getCodigoSia())
                .unidadAdministrativa(servei.getUnidadAdministrativa())
                .organoInstructor(servei.getOrganoInstructor())
                .comun(servei.isComun())
                .build();
    }

    private static final Logger logger = LoggerFactory.getLogger(ProcedimentHelper.class);
}