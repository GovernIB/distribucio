package es.caib.distribucio.persist.resourcerepository;

import java.util.List;

import es.caib.distribucio.persist.base.repository.BaseRepository;
import es.caib.distribucio.persist.resourceentity.EntitatResourceEntity;
import es.caib.distribucio.persist.resourceentity.ReglaResourceEntity;

/**
 * Repositori per a la gestió de regles.
 *
 * @author Límit Tecnologies
 */
public interface ReglaResourceRepository extends BaseRepository<ReglaResourceEntity, Long> {

    /** Nombre de regles de l'entitat, usat per calcular l'ordre d'una regla nova (s'afegeix al final). */
    int countByEntitat(EntitatResourceEntity entitat);

    /** Totes les regles de l'entitat ordenades per <code>ordre</code>, usat per recalcular l'ordre en moure'n una (accions AMUNT/AVALL/MOURE). */
    List<ReglaResourceEntity> findByEntitatOrderByOrdreAsc(EntitatResourceEntity entitat);

}
