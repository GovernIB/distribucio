/**
 * 
 */
package es.caib.distribucio.logic.intf.dto;

import java.io.Serializable;
import java.util.Comparator;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Informació d'un permís.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
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
	
	/** Per completar informació en els llistats de permisos. */
	private String principalDescripcio;

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getPrincipalNom() {
		return principalNom;
	}
	public void setPrincipalNom(String principalNom) {
		this.principalNom = principalNom;
	}
	public PrincipalTipusEnumDto getPrincipalTipus() {
		return principalTipus;
	}
	public void setPrincipalTipus(PrincipalTipusEnumDto principalTipus) {
		this.principalTipus = principalTipus;
	}
	public boolean isRead() {
		return read;
	}
	public void setRead(boolean read) {
		this.read = read;
	}
	public boolean isWrite() {
		return write;
	}
	public void setWrite(boolean write) {
		this.write = write;
	}
	public boolean isCreate() {
		return create;
	}
	public void setCreate(boolean create) {
		this.create = create;
	}
	public boolean isDelete() {
		return delete;
	}
	public void setDelete(boolean delete) {
		this.delete = delete;
	}
	public boolean isAdministration() {
		return administration;
	}
	public void setAdministration(boolean administration) {
		this.administration = administration;
	}
	public boolean isAdminLectura() {
		return adminLectura;
	}
	public void setAdminLectura(boolean adminLectura) {
		this.adminLectura = adminLectura;
	}
	public String getPrincipalDescripcio() {
		return principalDescripcio;
	}
	public void setPrincipalDescripcio(String principalDescripcio) {
		this.principalDescripcio = principalDescripcio;
	}
	
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
