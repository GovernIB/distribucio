package es.caib.distribucio.ws.client;

import java.util.List;
import java.util.concurrent.ExecutorService;

public class TestThread implements Runnable {
	
	private int nThread = 0;
	private ExecutorService executor;
	private List<String> errors;
	public TestThread(int nThread, ExecutorService executor, List<String> errors) {
		this.nThread = nThread;
		this.executor = executor;
		this.errors = errors;
	}
	
	@Override
	public void run() {
		
		int N_THREAD_ERROR = 4;
		
		System.out.println("Iniciant thread " + this.nThread);
		try {
			if (this.nThread==N_THREAD_ERROR) {
				System.out.println("Interrumpint tots els threads des del fil " + this.nThread + "!");
				this.errors.add("Prova error en el fil " + this.nThread);
				executor.shutdownNow();
			}
			Thread.sleep(1500);
		} catch (Exception e) {
			System.err.println("Thread " + this.nThread + " error : " + e.getMessage());
		}
		System.out.println("Finalitzat thread " + this.nThread);
	}
	
	
}
