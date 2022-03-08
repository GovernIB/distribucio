package es.caib.distribucio.ws.client;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MultiThreadingTest {

	public static void main(String[] args) {
		
		int N_THREADS = 10;
		int POOL = 5;

		List<String> errors = new ArrayList<String>();
		ExecutorService executor = Executors.newFixedThreadPool(POOL);
		for (int i = 0; i < N_THREADS; i++) {

				System.out.println("Programant el fil " + i);
				Runnable thread = new TestThread(i, executor, errors);
				executor.execute(thread);
		}

        executor.shutdown();
        
        while (!executor.isTerminated()) {
        	try {
        		executor.awaitTermination(100, TimeUnit.MILLISECONDS);
        	} catch (InterruptedException e) {}
        }
        
        if (!errors.isEmpty()) {
        	System.out.println("Fi ko " + errors.get(0));
        } {
        	System.out.println("Fi ok");
        }

	}
	

}
