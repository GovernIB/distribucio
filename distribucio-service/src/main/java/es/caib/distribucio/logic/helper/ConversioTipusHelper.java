/**
 * 
 */
package es.caib.distribucio.logic.helper;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.caib.distribucio.logic.intf.dto.AlertaDto;
import es.caib.distribucio.logic.intf.dto.ArxiuFirmaTipusEnumDto;
import es.caib.distribucio.logic.intf.dto.ContingutComentariDto;
import es.caib.distribucio.logic.intf.dto.MetaDadaDto;
import es.caib.distribucio.logic.intf.dto.MetaDadaTipusEnumDto;
import es.caib.distribucio.logic.intf.dto.RegistreAnnexDto;
import es.caib.distribucio.logic.intf.dto.ReglaDto;
import es.caib.distribucio.logic.intf.dto.UsuariDto;
import es.caib.distribucio.logic.intf.helper.ArxiuConversions;
import es.caib.distribucio.persist.entity.AlertaEntity;
import es.caib.distribucio.persist.entity.ContingutComentariEntity;
import es.caib.distribucio.persist.entity.DadaEntity;
import es.caib.distribucio.persist.entity.MetaDadaEntity;
import es.caib.distribucio.persist.entity.RegistreAnnexEntity;
import es.caib.distribucio.persist.entity.RegistreAnnexFirmaEntity;
import es.caib.distribucio.persist.entity.ReglaEntity;
import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
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

	/**
	 * 
	 */
	@SuppressWarnings("deprecation")
	public ConversioTipusHelper() {
		// mapperFactory = new DefaultMapperFactory.Builder().build();
		MappingContext.Factory mappingContextFactory = new MappingContext.Factory();
		mapperFactory= new DefaultMapperFactory.Builder().mappingContextFactory(mappingContextFactory).build();
		mapperFactory.getConverterFactory().registerConverter(
				new CustomConverter<DateTime, Date>() {
					public Date convert(
							DateTime source,
							Type<? extends Date> destinationClass,
							MappingContext mappingContext) {
						return source.toDate();
					}
				});
		mapperFactory.getConverterFactory().registerConverter(
				new CustomConverter<AlertaEntity, AlertaDto>() {
					public AlertaDto convert(
							AlertaEntity source,
							Type<? extends AlertaDto> destinationClass,
							MappingContext mappingContext) {
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
					public ContingutComentariDto convert(
							ContingutComentariEntity source,
							Type<? extends ContingutComentariDto> destinationClass,
							MappingContext mappingContext) {
						ContingutComentariDto target = new ContingutComentariDto();
						target.setId(source.getId());
						target.setText(source.getText());
						target.setCreatedBy(convertir(source.getCreatedBy().orElse(null), UsuariDto.class));
						if (source.getCreatedDate().isPresent()) {
							target.setCreatedDate(
									java.sql.Timestamp.valueOf(source.getCreatedDate().get()));
						}
						target.setLastModifiedBy(convertir(source.getLastModifiedBy().orElse(null), UsuariDto.class));
						if (source.getLastModifiedDate().isPresent()) {
							target.setLastModifiedDate(
									java.sql.Timestamp.valueOf(source.getLastModifiedDate().get()));
						}
						return target;
					}
				});
		mapperFactory.getConverterFactory().registerConverter(
				new CustomConverter<RegistreAnnexEntity, RegistreAnnexDto>() {
					public RegistreAnnexDto convert(
							RegistreAnnexEntity source,
							Type<? extends RegistreAnnexDto> destinationClass,
							MappingContext mappingContext) {
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
						target.setGesdocDocumentId(source.getGesdocDocumentId());
						target.setRegistreNumero(source.getRegistre() != null ? source.getRegistre().getNumero() : null);						
						target.setSignaturaInfo(getSignaturaInfo(source));
						target.setTipusFirma(getTipusFirma(source));
						target.setRegistreId(source.getRegistre() != null ? source.getRegistre().getId() : null);
						
						if (source.getFitxerNom() != null && source.getFitxerNom().contains(".")) {
							target.setFitxerExtension(source.getFitxerNom().substring(source.getFitxerNom().lastIndexOf('.') +1, source.getFitxerNom().length()));
						} else {
							target.setFitxerExtension(null);
						}						
						
						return target;
					}
				});
		mapperFactory.getConverterFactory().registerConverter(
				new CustomConverter<MetaDadaEntity, MetaDadaDto>() {
					public MetaDadaDto convert(
							MetaDadaEntity source,
							Type<? extends MetaDadaDto> destinationClass,
							MappingContext mappingContext) {
						MetaDadaDto target = new MetaDadaDto();
						target.setId(source.getId());
						target.setCodi(source.getCodi());
						target.setNom(source.getNom());
						target.setTipus(source.getTipus());
						target.setDescripcio(source.getDescripcio());
						target.setMultiplicitat(source.getMultiplicitat());
						target.setReadOnly(source.isReadOnly());
						target.setOrdre(source.getOrdre());
						target.setActiva(source.isActiva());
						target.setNoAplica(source.isNoAplica());
						if (source.getTipus()==MetaDadaTipusEnumDto.BOOLEA) {
							target.setValorBoolea((Boolean) DadaEntity.getDadaValorPerRetornar(source, source.getValor()));
						} else if (source.getTipus()==MetaDadaTipusEnumDto.DATA) {
							target.setValorData((Date) DadaEntity.getDadaValorPerRetornar(source, source.getValor()));
						} else if (source.getTipus()==MetaDadaTipusEnumDto.FLOTANT) {
							target.setValorFlotant((Double) DadaEntity.getDadaValorPerRetornar(source, source.getValor()));
						} else if (source.getTipus()==MetaDadaTipusEnumDto.IMPORT) {
							target.setValorImport((BigDecimal)DadaEntity.getDadaValorPerRetornar(source, source.getValor()));
						} else if (source.getTipus()==MetaDadaTipusEnumDto.SENCER) {
							target.setValorSencer((Long) DadaEntity.getDadaValorPerRetornar(source, source.getValor()));
						}  else if (source.getTipus()==MetaDadaTipusEnumDto.TEXT) {
							target.setValorString((String) DadaEntity.getDadaValorPerRetornar(source, source.getValor()));
						}
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
	
	private String getSignaturaInfo(RegistreAnnexEntity source) {
		String signaturaInfo = "";
		List<RegistreAnnexFirmaEntity> firmes = source.getFirmes();
		if (firmes != null && firmes.size() > 0) {
			RegistreAnnexFirmaEntity firma = firmes.get(0);
			String tipusFirma = firma.getTipus();
			String perfilFirma = firma.getPerfil();
			ArxiuFirmaTipusEnumDto arxiuFirmaTipus = ArxiuConversions.toArxiuFirmaTipus(tipusFirma);		
			signaturaInfo = tipusFirma + " " + arxiuFirmaTipus.name() + " " + perfilFirma;		
		}
		return signaturaInfo;
	}
	
	private ArxiuFirmaTipusEnumDto getTipusFirma(RegistreAnnexEntity source) {
		String tipusFirmaSt = "";
		ArxiuFirmaTipusEnumDto tipusFirma = null;
		List<RegistreAnnexFirmaEntity> firmes = source.getFirmes();
		if (firmes != null && firmes.size() > 0) {
			RegistreAnnexFirmaEntity firma = firmes.get(0);
			tipusFirmaSt = firma.getTipus();	
			tipusFirma = ArxiuConversions.toArxiuFirmaTipus(tipusFirmaSt);			
		}
		return tipusFirma;
	}

}
