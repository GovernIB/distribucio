/**
 * 
 */
package es.caib.distribucio.logic.intf.dto;

import java.io.Serializable;
import java.util.Comparator;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Informació d'un permís.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
public class PermisDto implements Serializable {

	private Long id;
	private String principalNom;
	private PrincipalTipusEnumDto principalTipus;
	private boolean read;
	private boolean write;
	private boolean create;
	private boolean delete;
	private boolean administration;
	private boolean adminLectura;

    public TipusPermisEnumDto getTipusPermis(){
        if (this.write){
            return TipusPermisEnumDto.COMPLET;
        } else if (this.read){
            return TipusPermisEnumDto.NOMES_LECTURA;
        }
        return null;
    }

	/** Per completar informació en els llistats de permisos. */
	private String principalDescripcio;
	
	// Ordenació permisos
	
	public static Comparator<PermisDto> decending(final Comparator<PermisDto> other) {
        return new Comparator<PermisDto>() {
            public int compare(PermisDto o1, PermisDto o2) {
                return -1 * other.compare(o1, o2);
            }
        };
    }
	
	private static Comparator<PermisDto> tipusComparator;
	private static Comparator<PermisDto> principalNomComparator;
	private static Comparator<PermisDto> principalDescripcioComparator;
	
	public static Comparator<PermisDto> sortByTipus() {
		return tipusComparator != null ? tipusComparator : new TipusComparator();
	}
	
	public static Comparator<PermisDto> sortByPrincipalNom() {
		return principalNomComparator != null ? principalNomComparator : new PrincipalNomComparator();
	}
	
	public static Comparator<PermisDto> sortByPrincipalDescripcio() {
		return principalDescripcioComparator != null ? principalDescripcioComparator : new PrincipalDescripcioComparator();
	}
	
	private static class TipusComparator implements java.util.Comparator<PermisDto> {
		public int compare(PermisDto p1, PermisDto p2) {
			return p1.getPrincipalTipus().compareTo(p2.getPrincipalTipus());
		}
	}

	private static class PrincipalNomComparator implements java.util.Comparator<PermisDto> {
		public int compare(PermisDto p1, PermisDto p2) {
			return p1.getPrincipalNom().compareTo(p2.getPrincipalNom());
		}
	}
	
	private static class PrincipalDescripcioComparator implements java.util.Comparator<PermisDto> {
		public int compare(PermisDto p1, PermisDto p2) {
			return p1.getPrincipalDescripcio().compareTo(p2.getPrincipalDescripcio());
		}
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private static final long serialVersionUID = -139254994389509932L;

}
