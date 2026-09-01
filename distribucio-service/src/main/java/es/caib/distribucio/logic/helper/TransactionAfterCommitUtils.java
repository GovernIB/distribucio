package es.caib.distribucio.logic.helper;

import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * Utilitat per a executar una tasca quan la transacció actual ja s'ha confirmat.
 * <p/>
 * Es fa servir per a publicar els missatges que només són un senyal ("això ha canviat, torna-ho a
 * llegir"): si es publicassin amb la transacció encara oberta, qui els rep podria llegir l'estat
 * anterior. Si no hi ha cap transacció activa la tasca s'executa immediatament.
 * <p/>
 * La tasca es protegeix amb un indicador perquè s'executi exactament una vegada: amb la
 * combinació d'un EJB que obre la transacció JTA i un {@code @Transactional} de Spring que hi
 * participa, {@code afterCommit()} es pot arribar a invocar més d'un cop sobre la mateixa
 * sincronització (comprovat a RIPEA, d'on ve aquesta utilitat).
 *
 * @author Límit Tecnologies
 */
public final class TransactionAfterCommitUtils {

	private TransactionAfterCommitUtils() {
	}

	public static void run(final Runnable task) {
		if (TransactionSynchronizationManager.isSynchronizationActive()) {
			TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
				private final AtomicBoolean executat = new AtomicBoolean(false);
				@Override
				public void afterCommit() {
					if (executat.compareAndSet(false, true)) {
						task.run();
					}
				}
			});
		} else {
			task.run();
		}
	}

}
