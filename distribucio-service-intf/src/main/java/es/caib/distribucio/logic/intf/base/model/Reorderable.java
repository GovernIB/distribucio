package es.caib.distribucio.logic.intf.base.model;

/**
 * Interfície que han d'implementar tots els recursos reordenables.
 * 
 * @author Límit Tecnologies
 */
public interface Reorderable {

	public Long getOrder();
	public void setOrder(Long order);

}
