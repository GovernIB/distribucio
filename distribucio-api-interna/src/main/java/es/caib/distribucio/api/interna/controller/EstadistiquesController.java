package es.caib.distribucio.api.interna.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import es.caib.comanda.ms.estadistica.model.DimensioDesc;
import es.caib.comanda.ms.estadistica.model.EstadistiquesInfo;
import es.caib.comanda.ms.estadistica.model.IndicadorDesc;
import es.caib.comanda.ms.estadistica.model.RegistresEstadistics;
import es.caib.distribucio.logic.intf.service.EstadisticaService;

@RestController
public class EstadistiquesController {

	@Autowired
	private EstadisticaService estadisticaService;
	
	@GetMapping("/estadistiquesInfo")
    public EstadistiquesInfo statsInfo() throws IOException {
		List<DimensioDesc> dimensions = estadisticaService.getDimensions();
        List<IndicadorDesc> indicadors = estadisticaService.getIndicadors();
        return EstadistiquesInfo.
        		builder().
        		codi("DIS").
        		dimensions(dimensions).
        		indicadors(indicadors).
        		build();
	}	
	
	@GetMapping("/estadistiques")
    public RegistresEstadistics estadistiques(HttpServletRequest request) throws IOException {
        return estadisticaService.consultaUltimesEstadistiques();
    }
	
	@GetMapping("/estadistiques/of/{data}")
    public RegistresEstadistics estadistiques(HttpServletRequest request, @PathVariable String data) throws Exception {

        LocalDate localData = LocalDate.parse(data, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        return estadisticaService.consultaEstadistiques(localData);
    }

    @GetMapping("/estadistiques/from/{dataInici}/to/{dataFi}")
    public List<RegistresEstadistics> estadistiques(HttpServletRequest request, @PathVariable String dataInici, @PathVariable String dataFi) throws Exception {
        LocalDate dataFrom = LocalDate.parse(dataInici, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        LocalDate dataTo = LocalDate.parse(dataFi, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        LocalDate startLocalData = dataFrom.isBefore(dataTo) ? dataFrom : dataTo;
        LocalDate endLocalData = dataFrom.isBefore(dataTo) ? dataTo : dataFrom;
        LocalDate ahir = LocalDate.now().minusDays(1);
        if (endLocalData.isAfter(ahir)) {
        	endLocalData = ahir;
        }
        return estadisticaService.consultaEstadistiques(startLocalData, endLocalData);
    }
    
}
