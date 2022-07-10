/**
 * 
 */
package es.caib.distribucio.core.helper;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.caib.distribucio.core.api.dto.AlertaDto;
import es.caib.distribucio.core.api.dto.ContingutComentariDto;
import es.caib.distribucio.core.api.dto.RegistreAnnexDto;
import es.caib.distribucio.core.api.dto.ReglaDto;
import es.caib.distribucio.core.api.dto.UsuariDto;
import es.caib.distribucio.core.entity.AlertaEntity;
import es.caib.distribucio.core.entity.ContingutComentariEntity;
import es.caib.distribucio.core.entity.RegistreAnnexEntity;
import es.caib.distribucio.core.entity.ReglaEntity;
import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.metadata.ClassMapBuilder;
import ma.glasnost.orika.metadata.Type;
/**
 * Helper per a convertir entre diferents formats de documents.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class ConversioTipusHelper {

	private MapperFactory mapperFactory;

	public ConversioTipusHelper() {
		mapperFactory = new DefaultMapperFactory.Builder().build();
		mapperFactory.getConverterFactory().registerConverter(
				new CustomConverter<DateTime, Date>() {
					public Date convert(DateTime source, Type<? extends Date> destinationClass) {
						return source.toDate();
					}
				});
		mapperFactory.getConverterFactory().registerConverter(
				new CustomConverter<AlertaEntity, AlertaDto>() {
					public AlertaDto convert(AlertaEntity source, Type<? extends AlertaDto> destinationClass) {
						AlertaDto target = new AlertaDto();
						target.setId(source.getId());
						target.setText(source.getText());
						target.setError(source.getError());
						target.setLlegida(source.getLlegida().booleanValue());
						target.setContingutId(source.getContingut().getId());
						return target;
					}
				});
		
		mapperFactory.registerClassMap(
				ClassMapBuilder.map(ReglaEntity.class, ReglaDto.class)
				.field("backofficeDesti.nom", "backofficeDestiNom")
				.field("backofficeDesti.id", "backofficeDestiId")
				.field("bustiaDesti.nom", "bustiaDestiNom")
				.field("unitatDesti.codiAndNom", "unitatDestiNom")
				.field("bustiaFiltre.nom", "bustiaFiltreNom")
				.field("entitat.id", "entitatId")
				.field("entitat.nom", "entitatNom")
				.byDefault().toClassMap());
		
		mapperFactory.getConverterFactory().registerConverter(
				new CustomConverter<ContingutComentariEntity, ContingutComentariDto>() {
					public ContingutComentariDto convert(ContingutComentariEntity source, Type<? extends ContingutComentariDto> destinationClass) {
						ContingutComentariDto target = new ContingutComentariDto();
						target.setId(source.getId());
						target.setText(source.getText());
						target.setCreatedBy(convertir(source.getCreatedBy(), UsuariDto.class));
						target.setCreatedDate(source.getCreatedDate().toDate());
						target.setLastModifiedBy(convertir(source.getLastModifiedBy(), UsuariDto.class));
						target.setLastModifiedDate(source.getLastModifiedDate().toDate());
						return target;
					}
				});		
		mapperFactory.getConverterFactory().registerConverter(
				new CustomConverter<RegistreAnnexEntity, RegistreAnnexDto>() {
					public RegistreAnnexDto convert(RegistreAnnexEntity source, Type<? extends RegistreAnnexDto> destinationClass) {
						RegistreAnnexDto target = new RegistreAnnexDto();
						target.setId(source.getId());
						target.setTitol(source.getTitol());
						target.setFitxerNom(source.getFitxerNom());
						target.setFitxerTamany(source.getFitxerTamany());
						target.setFitxerTipusMime(source.getFitxerTipusMime());
						target.setDataCaptura(source.getDataCaptura());
						target.setLocalitzacio(source.getLocalitzacio());
						if (source.getOrigenCiutadaAdmin() != null)
							target.setOrigenCiutadaAdmin(source.getOrigenCiutadaAdmin().toString());
						if (source.getNtiTipusDocument() != null)
							target.setNtiTipusDocument(source.getNtiTipusDocument().toString());
						if (source.getSicresTipusDocument() != null)
							target.setSicresTipusDocument(source.getSicresTipusDocument().toString());
						if (source.getNtiElaboracioEstat() != null)
							target.setNtiElaboracioEstat(source.getNtiElaboracioEstat().toString());
						target.setObservacions(source.getObservacions());
						target.setFirmaMode(source.getFirmaMode());
						target.setTimestamp(source.getTimestamp());
						target.setValidacioOCSP(source.getValidacioOCSP());
						target.setFitxerArxiuUuid(source.getFitxerArxiuUuid());
						target.setFirmaCsv(source.getFirmaCsv());
						if(source.getFirmes()!=null && !source.getFirmes().isEmpty()){
							target.setAmbFirma(true);
						} else {
							target.setAmbFirma(false);
						}
						
						if (source.getMetaDades() != null) {
							try {
								Map<String, String> metaDadesMap = new ObjectMapper().readValue(source.getMetaDades(), new TypeReference<Map<String, String>>(){});
								target.setMetaDadesMap(metaDadesMap);
							} catch (Exception e) {
								throw new RuntimeException(e);
							}
						}
						target.setValidacioFirmaEstat(source.getValidacioFirmaEstat());
						target.setValidacioFirmaError(source.getValidacioFirmaError());
						target.setArxiuEstat(source.getArxiuEstat());

						return target;
					}
				});
	}

	public <T> T convertir(Object source, Class<T> targetType) {
		if (source == null)
			return null;
		return getMapperFacade().map(source, targetType);
	}
	public <T> List<T> convertirList(List<?> items, Class<T> targetType) {
		if (items == null)
			return null;
		return getMapperFacade().mapAsList(items, targetType);
	}
	public <T> Set<T> convertirSet(Set<?> items, Class<T> targetType) {
		if (items == null)
			return null;
		return getMapperFacade().mapAsSet(items, targetType);
	}



	private MapperFacade getMapperFacade() {
		return mapperFactory.getMapperFacade();
	}

}
