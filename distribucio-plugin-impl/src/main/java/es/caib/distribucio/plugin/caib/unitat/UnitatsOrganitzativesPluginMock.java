/**
 * 
 */
package es.caib.distribucio.plugin.caib.unitat;

import java.net.MalformedURLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
public class UnitatsOrganitzativesPluginMock implements UnitatsOrganitzativesPlugin {

	/*private static final String CODI_UNITAT_ARREL = "00000000T";
	private static final String CODI_UNITAT_FILLA = "12345678Z";

	private List<UnitatOrganitzativa> unitats;*/
	
	
	@Override
	public UnitatOrganitzativa findUnidad(String pareCodi, Timestamp fechaActualizacion, Timestamp fechaSincronizacion) throws MalformedURLException{
		return new UnitatOrganitzativa("E00003601", "Ministerio de Fomento","EA9999999", "E00003601","V", null);
	}
	
	@Override
	public List<UnitatOrganitzativa> findAmbPare(String pareCodi, Timestamp fechaActualizacion, Timestamp fechaSincronizacion) throws SistemaExternException{
		
		List<UnitatOrganitzativa> unitats = new ArrayList<>();
		
		// unitat arrel: A04019281  
		// unitat superior: A04017954
		
		//SPLIT
		unitats.add(new UnitatOrganitzativa("A04017955", "denominacio", "A04017954", "A04019281","E", new ArrayList<>(Arrays.asList("A99999901", "A99999902"))));
		unitats.add(new UnitatOrganitzativa("A99999901", "denominacio", "A04017954", "A04019281","V", null));
		unitats.add(new UnitatOrganitzativa("A99999902", "denominacio", "A04017954", "A04019281","V", null));
		
		//MERGE
		unitats.add(new UnitatOrganitzativa("A04017960", "denominacio", "A04017954", "A04019281","E", new ArrayList<>(Arrays.asList("A99999903"))));
		unitats.add(new UnitatOrganitzativa("A04017956", "denominacio", "A04017954", "A04019281","E", new ArrayList<>(Arrays.asList("A99999903"))));
		unitats.add(new UnitatOrganitzativa("A99999903", "denominacio", "A04017954", "A04019281","V", null));
		
		//SUBSTITUTION
		unitats.add(new UnitatOrganitzativa("A04017957", "denominacio", "A04017954", "A04019281","E", new ArrayList<>(Arrays.asList("A99999904"))));
		unitats.add(new UnitatOrganitzativa("A99999904", "denominacio", "A04017954", "A04019281","V", null));
		
		//PROPS CHANGED
		unitats.add(new UnitatOrganitzativa("A04017958", "denominacio", "A04017954", "A04019281","V", null));
		
		//NEW
		unitats.add(new UnitatOrganitzativa("A99999905", "denominacio", "A04017954", "A04019281","V", null));
		unitats.add(new UnitatOrganitzativa("A99999906", "denominacio", "A04017954", "A04019281","V", null));
		
		return unitats;
	}





	/*@Override
	public List<UnitatOrganitzativaD3> cercaUnitatsD3(
			String codiUnitat, 
			String denominacioUnitat,
			Long codiNivellAdministracio, 
			Long codiComunitat, 
			Boolean ambOficines, 
			Boolean esUnitatArrel,
			Long codiProvincia, 
			String codiLocalitat) throws SistemaExternException {
		throw new SistemaExternException("Mètode no implementat");
	}*/

	/*private List<UnitatOrganitzativa> getUnitats() {
		if (unitats == null) {
			unitats = new ArrayList<UnitatOrganitzativa>();
			UnitatOrganitzativa pare = new UnitatOrganitzativa();
			pare.setCodi(CODI_UNITAT_ARREL);
			pare.setDenominacio("Límit Tecnologies");
			unitats.add(pare);
			UnitatOrganitzativa fill = new UnitatOrganitzativa();
			fill.setCodi(CODI_UNITAT_FILLA);
			fill.setDenominacio("Departament de programari");
			fill.setCodiUnitatArrel(CODI_UNITAT_ARREL);
			fill.setCodiUnitatSuperior(CODI_UNITAT_ARREL);
			unitats.add(fill);
		}
		return unitats;
	}*/

	@Override
	public List<UnitatOrganitzativa> cercaUnitats(String codiUnitat, String denominacioUnitat,
			Long codiNivellAdministracio, Long codiComunitat, Boolean ambOficines, Boolean esUnitatArrel,
			Long codiProvincia, String codiLocalitat) throws SistemaExternException {
		// TODO Auto-generated method stub
		return null;
	}

}
