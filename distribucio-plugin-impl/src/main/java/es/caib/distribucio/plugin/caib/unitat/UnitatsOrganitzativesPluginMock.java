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
		unitats.add(new UnitatOrganitzativa("A04026201", "B3 Prueba Marilen","A04025121", "A04025121","E", new ArrayList<>(Arrays.asList("A04026203", "A04026205"))));
		unitats.add(new UnitatOrganitzativa("A04026203", "Administracion del Estado en el Exterior - Representacion Permanente de Transportes","A04025121", "A04025121","V", null));
		unitats.add(new UnitatOrganitzativa("A04026205", "AENA Aeropuertos, S.A.","A04025121", "A04025121","V", null));
		unitats.add(new UnitatOrganitzativa("A04026185", "Agencia Estatal de Seguridad Aerea","A04025121", "A04025121","E", new ArrayList<>(Arrays.asList("A04026187"))));
		unitats.add(new UnitatOrganitzativa("A04026163", "Agencia Estatal de Seguridad Ferroviaria","A04025121", "A04025121","E", new ArrayList<>(Arrays.asList("A04026187"))));
		unitats.add(new UnitatOrganitzativa("A04026187", "AENA Desarrollo Internacional, S.A.","A04025121", "A04025121","V",null));

		
//		unitats.add(new UnitatOrganitzativa("A04026161", "B1 Prueba Marilen","A04025121", "A04025121","E", new ArrayList<>(Arrays.asList("A04026191"))));
//		unitats.add(new UnitatOrganitzativa("A04026191", "Denom Completa 1.1.","A04026163", "A04025121","V", null));
		unitats.add(new UnitatOrganitzativa("A04026202", "B3 Prueba Marilen 1.1.11111111111111111","A04026201", "A04025121","V", null));
		
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
