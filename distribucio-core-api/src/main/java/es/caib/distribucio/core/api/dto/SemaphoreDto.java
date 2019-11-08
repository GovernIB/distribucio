package es.caib.distribucio.core.api.dto;

public class SemaphoreDto {

	public static Object semaphore = new Object();
	
	public static Object getSemaphore() {
		return semaphore;
	}
}
