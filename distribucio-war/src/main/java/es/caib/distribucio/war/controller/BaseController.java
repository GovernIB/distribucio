/**
 * 
 */
package es.caib.distribucio.war.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.servlet.support.RequestContext;

import es.caib.distribucio.core.api.dto.EntitatDto;
import es.caib.distribucio.core.api.dto.RegistreAnnexDto;
import es.caib.distribucio.core.api.dto.RegistreDto;
import es.caib.distribucio.core.api.registre.ValidacioFirmaEnum;
import es.caib.distribucio.core.api.service.ws.backoffice.AnnexEstat;
import es.caib.distribucio.war.helper.AjaxHelper;
import es.caib.distribucio.war.helper.EntitatHelper;
import es.caib.distribucio.war.helper.MissatgesHelper;
import es.caib.distribucio.war.helper.ModalHelper;
import es.caib.distribucio.war.helper.RequestSessionHelper;

/**
 * Controlador base que implementa funcionalitats comunes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class BaseController implements MessageSourceAware {

	MessageSource messageSource;

	protected String modalUrlTancar() {
		//return "redirect:/nodeco/util/modalTancar";
		return "redirect:" + ModalHelper.ACCIO_MODAL_TANCAR;
	}
	protected String ajaxUrlOk() {
		//return "redirect:/nodeco/util/ajaxOk";
		return "redirect:" + AjaxHelper.ACCIO_AJAX_OK;
	}

	protected String getAjaxControllerReturnValueSuccess(
			HttpServletRequest request,
			String url,
			String messageKey) {
		return getAjaxControllerReturnValueSuccess(
				request,
				url,
				messageKey,
				null);
	}
	protected String getAjaxControllerReturnValueSuccess(
			HttpServletRequest request,
			String url,
			String messageKey,
			Object[] messageArgs) {
		if (messageKey != null) {
			MissatgesHelper.success(
					request, 
					getMessage(
							request, 
							messageKey,
							messageArgs));
		}
		if (AjaxHelper.isAjax(request)) {
			return ajaxUrlOk();
		} else {
			return url;
		}
	}
	protected String getAjaxControllerReturnValueError(
			HttpServletRequest request,
			String url,
			String messageKey) {
		return getAjaxControllerReturnValueError(
				request,
				url,
				messageKey,
				null);
	}
	protected String getAjaxControllerReturnValueError(
			HttpServletRequest request,
			String url,
			String messageKey,
			Object[] messageArgs) {
		if (messageKey != null) {
			MissatgesHelper.error(
					request, 
					getMessage(
							request, 
							messageKey,
							messageArgs));
		}
		if (AjaxHelper.isAjax(request)) {
			return ajaxUrlOk();
		} else {
			return url;
		}
	}

	protected String getModalControllerReturnValueSuccess(
			HttpServletRequest request,
			String url,
			String messageKey) {
		return getModalControllerReturnValueSuccess(
				request,
				url,
				messageKey,
				null);
	}
	protected String getModalControllerReturnValueSuccess(
			HttpServletRequest request,
			String url,
			String messageKey,
			Object[] messageArgs) {
		if (messageKey != null) {
			MissatgesHelper.success(
					request, 
					getMessage(
							request, 
							messageKey,
							messageArgs));
		}
		if (ModalHelper.isModal(request)) {
			return modalUrlTancar();
		} else {
			return url;
		}
	}

	protected String getModalControllerReturnValueError(
			HttpServletRequest request,
			String url,
			String messageKey) {
		return getModalControllerReturnValueError(
				request,
				url,
				messageKey,
				null);
	}
	protected String getModalControllerReturnValueError(
			HttpServletRequest request,
			String url,
			String messageKey,
			Object[] messageArgs) {
		if (messageKey != null) {
			MissatgesHelper.error(
					request, 
					getMessage(
							request, 
							messageKey,
							messageArgs));
		}
		if (ModalHelper.isModal(request)) {
			return modalUrlTancar();
		} else {
			return url;
		}
	}
	
	protected String getModalControllerReturnValueErrorNoKey(
			HttpServletRequest request,
			String url,
			String message) {
		if (message != null) {
			MissatgesHelper.error(
					request, 
					message);
		}
		if (ModalHelper.isModal(request)) {
			return modalUrlTancar();
		} else {
			return url;
		}
	}
	
	
	protected String getAjaxControllerReturnValueErrorMessage(
			HttpServletRequest request,
			String url,
			String message) {
		if (message != null) {
			MissatgesHelper.error(
					request, 
					message);
		}
		if (AjaxHelper.isAjax(request)) {
			return ajaxUrlOk();
		} else {
			return url;
		}
	}

	protected void writeFileToResponse(
			String fileName,
			byte[] fileContent,
			HttpServletResponse response) throws IOException {
		response.setHeader("Pragma", "");
		response.setHeader("Expires", "");
		response.setHeader("Cache-Control", "");
		response.setHeader("Content-Disposition","attachment; filename=\"" + fileName + "\"");
		if (fileName != null && !fileName.isEmpty())
			response.setContentType(new MimetypesFileTypeMap().getContentType(fileName));
		
		if (fileContent == null) {
			throw new RuntimeException("El contingut que voleu descarregar és nul");
		}
		
		response.getOutputStream().write(fileContent);
	}

	protected String getMessage(
			HttpServletRequest request,
			String key,
			Object[] args) {
		String message = messageSource.getMessage(
				key,
				args,
				"???" + key + "???",
				new RequestContext(request).getLocale());
		return message;
	}
	protected String getMessage(
			HttpServletRequest request,
			String key) {
		return getMessage(request, key, null);
	}



	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}
	
	/** Per afegir el trim de les dades provinents dels formularis per evitar espais en blanc davant i darrera
	 * els valors.
	 * 
	 * @param binder
	 */
    @InitBinder
    public void initBinderBaseController ( WebDataBinder binder )
    {
        StringTrimmerEditor stringtrimmer = new StringTrimmerEditor(true);
        binder.registerCustomEditor(String.class, stringtrimmer);
    }

	/** Mètode per consultar els registres seleccionats pel processament múltiple.
	 * @param request Request
	 * @param sessionName Objecte de sessió que conté la selecció
	 * @return
	 */
	protected List<Long> getRegistresSeleccionats(HttpServletRequest request, String sessionName) {
		@SuppressWarnings("unchecked")
		Set<Long> seleccio = (Set<Long>)RequestSessionHelper.obtenirObjecteSessio(
				request,
				sessionName);
		List<Long> registres;
		if (seleccio != null) {
			registres = new ArrayList<Long>(seleccio);
		} else {
			registres = new ArrayList<Long>();
		}
		return registres;

	}

	
	public EntitatDto getEntitatActual(
			HttpServletRequest request) {
		EntitatDto entitat = EntitatHelper.getEntitatActual(request);
		if (entitat == null)
			throw new SecurityException(getMessage(request, "entitat.actual.error.no.assignada"));
		if (!entitat.isUsuariActualRead() && !entitat.isUsuariActualAdministration())
			throw new SecurityException(getMessage(request, "entitat.actual.error.permis.acces"));
		return entitat;
	}

	/** Compta el número d'annexos pendents d'Arxiu */
	public int numeroAnnexosPendentsArxiu(RegistreDto registre) {
		int numeroAnnexosPendentsArxiu = 0;						
		for (RegistreAnnexDto registreAnnexDto:registre.getAnnexos()) {
			if (registreAnnexDto.getFitxerArxiuUuid()==null) {
				numeroAnnexosPendentsArxiu++;
			}
		}
		return numeroAnnexosPendentsArxiu;
	}
	
	/** Compta el número d'annexos amb firma invàlida */
	public int numeroAnnexosFirmaInvalida(RegistreDto registre) {
		int numeroAnnexosFirmaInvalida = 0;						
		for (RegistreAnnexDto registreAnnexDto:registre.getAnnexos()) {
			if (registreAnnexDto.getValidacioFirmaEstat() == ValidacioFirmaEnum.FIRMA_INVALIDA 
					|| registreAnnexDto.getValidacioFirmaEstat() == ValidacioFirmaEnum.ERROR_VALIDANT ) {
				numeroAnnexosFirmaInvalida++;
			}
		}
		return numeroAnnexosFirmaInvalida;
	}

	/** Compta el número d'annexos en estat esborrany */
	public int numeroAnnexosEstatEsborrany(RegistreDto registre) {
		int numeroAnnexosEstatEsborrany = 0;						
		for (RegistreAnnexDto registreAnnexDto:registre.getAnnexos()) {
			if (registreAnnexDto.getArxiuEstat() == AnnexEstat.ESBORRANY) {
				
				numeroAnnexosEstatEsborrany++;
			}
		}
		return numeroAnnexosEstatEsborrany;
	}
	
	
	/** Obté l'entitat actual segons el rol que té l'usuari
	 * s'utilitza només per les accions comuns d'admin i usuari */	
	public EntitatDto getEntitatActualComprovantPermis(
			HttpServletRequest request, 
			String rol) {
		EntitatDto entitat = this.getEntitatActual(request);
		if ("admin".equals(rol)) {
			if (!entitat.isUsuariActualAdministration() && !entitat.isUsuariActualAdminLectura())
				throw new SecurityException(getMessage(request, "entitat.actual.error.permis.admin"));
			return entitat;
		}else {
			if (!entitat.isUsuariActualRead() && !entitat.isUsuariActualAdminLectura())
				throw new SecurityException(getMessage(request, "entitat.actual.error.permis.admin"));
			return entitat;			
		}
	}
	

}
