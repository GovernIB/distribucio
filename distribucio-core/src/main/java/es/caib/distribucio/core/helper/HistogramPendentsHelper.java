/**
 * 
 */
package es.caib.distribucio.core.helper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Component;

import es.caib.distribucio.logic.intf.dto.HistogramPendentsEntryDto;

/**
 * Mètodes per a la gestió d'integracions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class HistogramPendentsHelper {
	
	public static final int MAX_HISTOGRAM = 60;

	private List<HistogramPendentsEntryDto> histogram = new ArrayList<HistogramPendentsEntryDto>();
	
	/**
	 * Only for tests
	 */
	HistogramPendentsHelper(){
		
//		histogram.add(new HistogramPendentsEntryDto(new Date(), 2, 3, 5, 5643));
//		histogram.add(new HistogramPendentsEntryDto(new Date(), 3, 2, 0, 3455));
//		histogram.add(new HistogramPendentsEntryDto(new Date(), 1, 3, 5, 5667));
//		histogram.add(new HistogramPendentsEntryDto(new Date(), 1, 1, 0, 1562));
//		histogram.add(new HistogramPendentsEntryDto(new Date(), 0, 3, 4, 6725));
//		histogram.add(new HistogramPendentsEntryDto(new Date(), 0, 2, 5, 12543));
//		histogram.add(new HistogramPendentsEntryDto(new Date(), 2, 3, 3, 4682));
//		histogram.add(new HistogramPendentsEntryDto(new Date(), 2, 3, 5, 12453));
//		histogram.add(new HistogramPendentsEntryDto(new Date(), 10, 2, 0, 1653));
//		histogram.add(new HistogramPendentsEntryDto(new Date(), 1, 3, 5, 1763));
//		histogram.add(new HistogramPendentsEntryDto(new Date(), 1, 1, 0, 1267));
//		histogram.add(new HistogramPendentsEntryDto(new Date(), 0, 3, 15, 123));
//		histogram.add(new HistogramPendentsEntryDto(new Date(), 0, 2, 5, 1623));
//		histogram.add(new HistogramPendentsEntryDto(new Date(), 2, 3, 3, 1623));
//		histogram.add(new HistogramPendentsEntryDto(new Date(), 2, 3, 5, 12783));
//		histogram.add(new HistogramPendentsEntryDto(new Date(), 3, 2, 0, 1263));
//		histogram.add(new HistogramPendentsEntryDto(new Date(), 1, 3, 5, 1237));
//		histogram.add(new HistogramPendentsEntryDto(new Date(), 1, 1, 0, 1263));
//		histogram.add(new HistogramPendentsEntryDto(new Date(), 0, 3, 4, 123));
//		histogram.add(new HistogramPendentsEntryDto(new Date(), 0, 2, 5, 12783));
//		histogram.add(new HistogramPendentsEntryDto(new Date(), 2, 3, 3, 12783));
//		histogram.add(new HistogramPendentsEntryDto(new Date(), 2, 3, 5, 123));
//		histogram.add(new HistogramPendentsEntryDto(new Date(), 10, 2, 0, 123));
//		histogram.add(new HistogramPendentsEntryDto(new Date(), 1, 3, 5, 123));
//		histogram.add(new HistogramPendentsEntryDto(new Date(), 1, 1, 0, 12789));
//		histogram.add(new HistogramPendentsEntryDto(new Date(), 0, 3, 15, 123));
//		histogram.add(new HistogramPendentsEntryDto(new Date(), 0, 2, 5, 123));
//		histogram.add(new HistogramPendentsEntryDto(new Date(), 2, 3, 3, 15623));
//		histogram.add(new HistogramPendentsEntryDto(new Date(), 2, 3, 5, 123));
//		histogram.add(new HistogramPendentsEntryDto(new Date(), 3, 2, 0, 123));
//		histogram.add(new HistogramPendentsEntryDto(new Date(), 1, 3, 5, 123));
//		histogram.add(new HistogramPendentsEntryDto(new Date(), 1, 1, 0, 123));
//		histogram.add(new HistogramPendentsEntryDto(new Date(), 0, 3, 4, 15623));
//		histogram.add(new HistogramPendentsEntryDto(new Date(), 0, 2, 5, 123));
//		histogram.add(new HistogramPendentsEntryDto(new Date(), 2, 3, 3, 123));
//		histogram.add(new HistogramPendentsEntryDto(new Date(), 2, 3, 5, 123));
//		histogram.add(new HistogramPendentsEntryDto(new Date(), 10, 2, 0, 15623));
//		histogram.add(new HistogramPendentsEntryDto(new Date(), 1, 3, 5, 123));
//		histogram.add(new HistogramPendentsEntryDto(new Date(), 1, 1, 0, 123));
//		histogram.add(new HistogramPendentsEntryDto(new Date(), 0, 3, 15, 123));
//		histogram.add(new HistogramPendentsEntryDto(new Date(), 0, 2, 5, 123));
//		histogram.add(new HistogramPendentsEntryDto(new Date(), 2, 3, 3, 123));
//		histogram.add(new HistogramPendentsEntryDto(new Date(), 2, 3, 5, 123));
//		histogram.add(new HistogramPendentsEntryDto(new Date(), 3, 2, 0, 123));
//		histogram.add(new HistogramPendentsEntryDto(new Date(), 1, 3, 5, 123));
//		histogram.add(new HistogramPendentsEntryDto(new Date(), 1, 1, 0, 123));
//		histogram.add(new HistogramPendentsEntryDto(new Date(), 0, 3, 4, 123));
//		histogram.add(new HistogramPendentsEntryDto(new Date(), 0, 2, 5, 123));
//		histogram.add(new HistogramPendentsEntryDto(new Date(), 2, 3, 3, 123));
//		histogram.add(new HistogramPendentsEntryDto(new Date(), 2, 3, 5, 123));
//		histogram.add(new HistogramPendentsEntryDto(new Date(), 10, 2, 0, 123));
//		histogram.add(new HistogramPendentsEntryDto(new Date(), 1, 3, 5, 123));
//		histogram.add(new HistogramPendentsEntryDto(new Date(), 1, 1, 0, 123));
//		histogram.add(new HistogramPendentsEntryDto(new Date(), 0, 3, 15, 123));
//		histogram.add(new HistogramPendentsEntryDto(new Date(), 0, 2, 5, 123));
//		histogram.add(new HistogramPendentsEntryDto(new Date(), 2, 3, 3, 123));
//		histogram.add(new HistogramPendentsEntryDto(new Date(), 2, 3, 5, 123));
//		histogram.add(new HistogramPendentsEntryDto(new Date(), 3, 2, 0, 123));
//		histogram.add(new HistogramPendentsEntryDto(new Date(), 1, 3, 5, 123));
//		histogram.add(new HistogramPendentsEntryDto(new Date(), 1, 1, 0, 123));
//		histogram.add(new HistogramPendentsEntryDto(new Date(), 0, 3, 4, 123));


	}
	
	
	public List<HistogramPendentsEntryDto> getAll() {
		return this.histogram;
	}

	/** This method shoult to be called from WorkerThread when there is an error. */
	synchronized public void addHistogramError() {
		if (!this.histogram.isEmpty()){
			HistogramPendentsEntryDto lastOne = this.histogram.get(this.histogram.size()-1);
			lastOne.increaseErrorCounter();
		}
			
	}
	
	/** This method shoult to be called from WorkerThread when an annex has been saven into arxiu. */
	synchronized public void addHistogramProcessat(float tempsProcessament) {
		if (!this.histogram.isEmpty()) {			
			HistogramPendentsEntryDto entry = this.histogram.get(histogram.size()-1);
			if (entry.getProcessats() > 0) {
				entry.setProcessTimeAverage((entry.getProcessTimeAverage() * entry.getProcessats() + tempsProcessament) / (entry.getProcessats() + 1));
			} else {
				entry.setProcessTimeAverage(tempsProcessament);
			}
			entry.increaseProcessatsCounter();
		}
	}
	
	
	public void addNewEntryToHistogram(int pendentArxiu) {

		HistogramPendentsEntryDto entry = new HistogramPendentsEntryDto();
		entry.setData(new Date());
		entry.setPendentArxiu(pendentArxiu);
		if (histogram.size() > MAX_HISTOGRAM)
			histogram.remove(0);
		histogram.add(entry);
	}

}
