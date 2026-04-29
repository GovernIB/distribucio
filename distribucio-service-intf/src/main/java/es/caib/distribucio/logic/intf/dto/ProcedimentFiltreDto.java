package es.caib.distribucio.logic.intf.dto;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.builder.ToStringBuilder;

@Getter
@Setter
public class ProcedimentFiltreDto implements Serializable{
	
	private String codi;
	private String nom;
	private String codiSia;
	private ProcedimentEstatEnumDto estat;
	private Long unitatOrganitzativa;
	private EntitatDto entitat;
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}


	private static final long serialVersionUID = -5749404179903245757L;	

}
