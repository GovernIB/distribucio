package es.caib.distribucio.core.helper;

public class WorkerThread implements Runnable {
	  

	private RegistreHelper registreHelper;
	
    private Long registreId;

    
	public WorkerThread() {
	}
	
	public WorkerThread(Long registreId, RegistreHelper registreHelper) {
		this.registreId = registreId;
		this.registreHelper = registreHelper;
	}

    @Override
    public void run() {

		registreHelper.processarAnotacioPendentArxiuInThreadExecuto(registreId);

    }


    @Override
    public String toString(){
		return this.registreId.toString();
    }

}