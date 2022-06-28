/**
 * 
 */
package es.caib.distribucio.plugin.caib.unitat;

import java.net.MalformedURLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import es.caib.distribucio.plugin.DistribucioAbstractPluginProperties;
import es.caib.distribucio.plugin.SistemaExternException;
import es.caib.distribucio.plugin.unitat.UnitatOrganitzativa;
import es.caib.distribucio.plugin.unitat.UnitatsOrganitzativesPlugin;

/**
 * Implementació de proves del plugin d'unitats organitzatives.
 * La estructura d'unitats és la següent:
 *   arrel: Limit Tecnologies (00000000T)
 *   filla: Departament de programari (12345678Z)
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class UnitatsOrganitzativesPluginMock extends DistribucioAbstractPluginProperties implements UnitatsOrganitzativesPlugin {
	
	public UnitatsOrganitzativesPluginMock() {
		super();
	}
	
	public UnitatsOrganitzativesPluginMock(Properties properties) {
		super(properties);
	}
	
	@Override
	public List<UnitatOrganitzativa> findAmbPare(String pareCodi, Timestamp fechaActualizacion, Timestamp fechaSincronizacion) throws SistemaExternException{
		
		List<UnitatOrganitzativa> unitats = new ArrayList<>();
		
		
		final String CODI_UNITAT_ARREL = "A04019281";
		final String CODI_UNITAT_SUPERIOR = "A04019281";
		
		final String CODI_UNITAT_TO_SPLIT = "A04032338";
		final String CODI_UNITAT_TO_MERGE1 = "A04032340";
		final String CODI_UNITAT_TO_MERGE2 = "A04031575";
		final String CODI_UNITAT_TO_SUBSTITUTE = "A04031579";
		final String CODI_UNITAT_TO_CUMULATIVE_CHANGES = "A04046344";
		final String CODI_UNITAT_TO_PROPS_CHANGED = "A04031605";		
		
		//SPLIT
		unitats.add(new UnitatOrganitzativa(CODI_UNITAT_TO_SPLIT, "denominacio", CODI_UNITAT_SUPERIOR, CODI_UNITAT_ARREL,"E", new ArrayList<>(Arrays.asList("A99999901", "A99999902"))));
		unitats.add(new UnitatOrganitzativa("A99999901", "denominacio", CODI_UNITAT_SUPERIOR, CODI_UNITAT_ARREL,"V", null));
		unitats.add(new UnitatOrganitzativa("A99999902", "denominacio", CODI_UNITAT_SUPERIOR, CODI_UNITAT_ARREL,"V", null));
		
		//MERGE
		unitats.add(new UnitatOrganitzativa(CODI_UNITAT_TO_MERGE1, "denominacio", CODI_UNITAT_SUPERIOR, CODI_UNITAT_ARREL,"E", new ArrayList<>(Arrays.asList("A99999903"))));
		unitats.add(new UnitatOrganitzativa(CODI_UNITAT_TO_MERGE2, "denominacio", CODI_UNITAT_SUPERIOR, CODI_UNITAT_ARREL,"E", new ArrayList<>(Arrays.asList("A99999903"))));
		unitats.add(new UnitatOrganitzativa("A99999903", "denominacio", CODI_UNITAT_SUPERIOR, CODI_UNITAT_ARREL,"V", null));
		
		//SUBSTITUTION
		unitats.add(new UnitatOrganitzativa(CODI_UNITAT_TO_SUBSTITUTE, "denominacio", CODI_UNITAT_SUPERIOR, CODI_UNITAT_ARREL,"E", new ArrayList<>(Arrays.asList("A99999904"))));
		unitats.add(new UnitatOrganitzativa("A99999904", "denominacio", CODI_UNITAT_SUPERIOR, CODI_UNITAT_ARREL,"V", null));
		
		//CUMULATIVE CHANGES
		unitats.add(new UnitatOrganitzativa(CODI_UNITAT_TO_CUMULATIVE_CHANGES, "denominacio", CODI_UNITAT_SUPERIOR, CODI_UNITAT_ARREL,"E", new ArrayList<>(Arrays.asList("A99999905"))));
		unitats.add(new UnitatOrganitzativa("A99999905", "denominacio", CODI_UNITAT_SUPERIOR, CODI_UNITAT_ARREL,"E", new ArrayList<>(Arrays.asList("A99999906"))));
		unitats.add(new UnitatOrganitzativa("A99999906", "denominacio", CODI_UNITAT_SUPERIOR, CODI_UNITAT_ARREL,"V", null));
		
		//PROPS CHANGED
		unitats.add(new UnitatOrganitzativa(CODI_UNITAT_TO_PROPS_CHANGED, "denominacio", CODI_UNITAT_SUPERIOR, CODI_UNITAT_ARREL,"V", new ArrayList<String>()));
		
		//NEW
		unitats.add(new UnitatOrganitzativa("A99999907", "denominacio", CODI_UNITAT_SUPERIOR, CODI_UNITAT_ARREL,"V", null));
		unitats.add(new UnitatOrganitzativa("A99999908", "denominacio", CODI_UNITAT_SUPERIOR, CODI_UNITAT_ARREL,"V", null));
		
		return unitats;
	}



	@Override
	public List<UnitatOrganitzativa> cercaUnitats(String codiUnitat, String denominacioUnitat,
			Long codiNivellAdministracio, Long codiComunitat, Boolean ambOficines, Boolean esUnitatArrel,
			Long codiProvincia, String codiLocalitat) throws SistemaExternException {
		return null;
	}
	
	@Override
	public UnitatOrganitzativa findUnidad(String pareCodi, Timestamp fechaActualizacion, Timestamp fechaSincronizacion) throws MalformedURLException{
		return new UnitatOrganitzativa("E00003601", "Ministerio de Fomento","EA9999999", "E00003601","V", null);
	}



	@Override
	public String getUsuariIntegracio() {
		return "Mock";
	}
	

}
