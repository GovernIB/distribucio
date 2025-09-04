package es.caib.distribucio.plugin.caib.arxiu;

import java.time.Duration;
import java.time.Instant;

import es.caib.comanda.ms.salut.model.EstatSalut;
import es.caib.comanda.ms.salut.model.EstatSalutEnum;
import es.caib.comanda.ms.salut.model.IntegracioPeticions;
import es.caib.distribucio.plugin.arxiu.ArxiuPlugin;
import es.caib.pluginsib.arxiu.api.ArxiuException;
import es.caib.pluginsib.arxiu.api.ContingutArxiu;
import es.caib.pluginsib.arxiu.api.Expedient;
import es.caib.pluginsib.arxiu.caib.ArxiuPluginCaib;
import lombok.Synchronized;

public class ArxiuPluginSalutCaib extends ArxiuPluginCaib implements ArxiuPlugin {
	
	@Override
	public ContingutArxiu expedientCrear(
			final Expedient expedient) throws ArxiuException {
		try {
			ContingutArxiu resposta = super.expedientCrear(expedient);
			incrementarOperacioOk();
			
			return resposta;
		} catch (Exception e) {
			incrementarOperacioError();
			throw e;
		}
	}
	@Override
	public void expedientEsborrar(
			final String identificador) throws ArxiuException {
		try {
			super.expedientEsborrar(identificador);
			incrementarOperacioOk();
		} catch (Exception e) {
			incrementarOperacioError();
			throw e;
		}
	}
	
	@Override
	public String expedientReobrir(
			final String identificador) throws ArxiuException {
		try {
			String resposta = super.expedientReobrir(identificador);
			incrementarOperacioOk();
			
			return resposta;
		} catch (Exception e) {
			incrementarOperacioError();
			throw e;
		}
	}
	
	@Override
	public String expedientTancar(
			final String identificador) throws ArxiuException {
		try {
			String resposta = super.expedientTancar(identificador);
			incrementarOperacioOk();
			
			return resposta;
		} catch (Exception e) {
			incrementarOperacioError();
			throw e;
		}
	}
	
	@Override
	public Expedient expedientDetalls(
			final String identificador,
			final String versio) throws ArxiuException {
		try {
			Expedient resposta = super.expedientDetalls(identificador, versio);
			incrementarOperacioOk();
			
			return resposta;
		} catch (Exception e) {
			incrementarOperacioError();
			throw e;
		}
	}
	
	
	// Mètodes de SALUT
	// /////////////////////////////////////////////////////////////////////////////////////////////

	private boolean configuracioEspecifica = false;
	private int operacionsOk = 0;
	private int operacionsError = 0;

	@Synchronized
	private void incrementarOperacioOk() {
		operacionsOk++;
	}

	@Synchronized
	private void incrementarOperacioError() {
		operacionsError++;
	}

	@Synchronized
	private void resetComptadors() {
		operacionsOk = 0;
		operacionsError = 0;
	}

	@Override
	public boolean teConfiguracioEspecifica() {
		return this.configuracioEspecifica;
	}

	@Override
	public EstatSalut getEstatPlugin() {
		try {
			Instant start = Instant.now();
			String identificador = "00000000-0000-0000-0000-000000000000";
			documentDetalls(identificador, null, false);

			return EstatSalut.builder()
					.latencia((int) Duration.between(start, Instant.now()).toMillis())
					.estat(EstatSalutEnum.UP)
					.build();
		} catch (Exception ex) {
		}
		return EstatSalut.builder().estat(EstatSalutEnum.DOWN).build();
	}

	@Override
	public IntegracioPeticions getPeticionsPlugin() {
		IntegracioPeticions integracioPeticions = IntegracioPeticions.builder()
				.totalOk(operacionsOk)
				.totalError(operacionsError)
				.build();
		resetComptadors();
		return integracioPeticions;
	}
}
