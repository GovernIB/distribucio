package es.caib.distribucio.war.controller;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.json.simple.JSONArray;
import org.json.simple.JSONValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.distribucio.core.api.monitor.MonitorTascaInfo;
import es.caib.distribucio.core.api.service.MonitorTasquesService;
import es.caib.distribucio.war.helper.MonitorHelper;

/**
 * Controlador per la gestió d'perfils
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/monitor")
public class MonitorSystemController extends BaseController {
	
	@Autowired
	private MonitorTasquesService monitortasquesService;
	
	@RequestMapping(method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			Model model) {
		return "monitor";
	}

	@RequestMapping(value = "/all", method = RequestMethod.GET)
	@ResponseBody
	public String monitor(HttpServletRequest request, String familia) {
		return JSONValue.toJSONString(ejecutar(request));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map<String,JSONArray> ejecutar(HttpServletRequest request) {
		Map<String, JSONArray> mjson = new LinkedHashMap<String,JSONArray>();
		JSONArray sistema = new JSONArray();
		JSONArray hilo = new JSONArray();
		JSONArray cputime = new JSONArray();
		JSONArray estado = new JSONArray();
		JSONArray espera = new JSONArray();
		JSONArray blockedtime = new JSONArray();
		
		ThreadMXBean bean = ManagementFactory.getThreadMXBean();
		sistema.add(getMessage(request, "monitor.procesadores")+": " + Runtime.getRuntime().availableProcessors());
		sistema.add(getMessage(request, "monitor.memoria_disponible")+": " + MonitorHelper.humanReadableByteCount(Runtime.getRuntime().freeMemory()));
		sistema.add(getMessage(request, "monitor.memoria_maxima")+": " + (Runtime.getRuntime().maxMemory() == Long.MAX_VALUE ? "Ilimitada" : MonitorHelper.humanReadableByteCount(Runtime.getRuntime().maxMemory())));
		sistema.add(getMessage(request, "monitor.memoria_total")+": " + MonitorHelper.humanReadableByteCount(Runtime.getRuntime().totalMemory()));
		sistema.add(getMessage(request, "monitor.os-name")+": " + MonitorHelper.getName());
		sistema.add(getMessage(request, "monitor.os-arch") + ": " + MonitorHelper.getArch());
		sistema.add(getMessage(request, "monitor.os-version") + ": " + MonitorHelper.getVersion());
		sistema.add(getMessage(request, "monitor.carga_cpu") + ": " + MonitorHelper.getCPULoad());
		
		for (File root : File.listRoots()) {
			sistema.add(getMessage(request, "monitor.space.total") + " " + root.getAbsolutePath()+": " + MonitorHelper.humanReadableByteCount(root.getTotalSpace()));
			sistema.add(getMessage(request, "monitor.space.free") + " " + root.getAbsolutePath()+": " + MonitorHelper.humanReadableByteCount(root.getFreeSpace()));
		}
        
		int numDeadlocked = 0; 
		if (bean.findMonitorDeadlockedThreads() != null) {
			numDeadlocked = bean.findMonitorDeadlockedThreads().length;
		}
		sistema.add(getMessage(request, "monitor.deadlocked")+": " + numDeadlocked);
		sistema.add(getMessage(request, "monitor.daemon_thread")+": " + bean.getDaemonThreadCount());
		
		bean.resetPeakThreadCount();
		
		if (bean.isThreadCpuTimeSupported()) {
			long[] ids = bean.getAllThreadIds();
			ThreadInfo[] info = bean.getThreadInfo(ids);
			Set hs = new HashSet();
			for (int a = 0; a < ids.length; ++a) {
				hs.add(bean.getThreadCpuTime(ids[a]));
			}
			long tiempoCPUTotal =  ((Long)Collections.max(hs)).longValue();
			for (int a = 0; a < ids.length; ++a) {
				String nombre = (info[a].getLockName() == null ? info[a].getThreadName() : info[a].getLockName());
				if (!"main".equals(nombre)) {
					hilo.add(nombre);
					long tiempoCPU = (long) ((float)100*((float) bean.getThreadCpuTime(ids[a]) / (float) tiempoCPUTotal));
					cputime.add(((tiempoCPU>100)?100:tiempoCPU) + " %");
					estado.add(getMessage(request, "monitor."+info[a].getThreadState()));
					espera.add(((info[a].getWaitedTime() == -1)? 0:info[a].getWaitedTime()) + " ns");
					blockedtime.add(((info[a].getBlockedTime() == -1)? 0:info[a].getBlockedTime()) + " ns");
				}
			}
		}
		
		mjson.put("sistema", sistema);
		mjson.put("hilo", hilo);
		mjson.put("cputime", cputime);
		mjson.put("estado", estado);
		mjson.put("espera", espera);
		mjson.put("blockedtime", blockedtime);
		
		Map<String, JSONArray> tasques = this.getTasquesJson(request);
		mjson.put("tasca", tasques.get("tasca"));
		mjson.put("estat", tasques.get("estat"));
		mjson.put("iniciExecucio", tasques.get("iniciExecucio"));
		mjson.put("tempsExecucio", tasques.get("tempsExecucio"));
//		mjson.put("fiExecucio", tasques.get("fiExecucio"));
		mjson.put("properaExecucio", tasques.get("properaExecucio"));
		mjson.put("observacions", tasques.get("observacions"));
		
		return mjson;
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value="/tasques", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, JSONArray> getTasquesJson(HttpServletRequest request) {
		Map<String, JSONArray> tasques = new HashMap<>();
		JSONArray tasca = new JSONArray();
		JSONArray estat = new JSONArray();
		JSONArray iniciExecucio = new JSONArray();
		JSONArray tempsExecucio = new JSONArray();
//		JSONArray fiExecucio = new JSONArray();
		JSONArray properaExecucio = new JSONArray();
		JSONArray observacions = new JSONArray();
		List<MonitorTascaInfo> monitorTasques = monitortasquesService.findAll();
		for(MonitorTascaInfo monitorTasca : monitorTasques) {
			tasca.add(getMessage(request, "monitor.tasques.tasca") + ": " + getMessage(request, "monitor.tasques.tasca.codi." + monitorTasca.getCodi()));
			estat.add(getMessage(request, "monitor.tasques.estat") + ": " + getMessage(request, "monitor.tasques.estat." + monitorTasca.getEstat()));
			String strDataInici = "-";
			if (monitorTasca.getDataInici() != null) {
				Date dataInici = monitorTasca.getDataInici();
				DateFormat dateFormatInici = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
				strDataInici = dateFormatInici.format(dataInici);
			}
			iniciExecucio.add(getMessage(request, "monitor.tasques.inici.execucio") + ": " + strDataInici);
			String difDataSegons = "-";
			if (monitorTasca.getDataInici() != null) {
				long difDatas = System.currentTimeMillis() - monitorTasca.getDataInici().getTime();
				difDataSegons = ((int) (difDatas / 1000) % 60) + "s";
			}
			tempsExecucio.add(getMessage(request, "monitor.tasques.temps.execucio") + ": " + (difDataSegons));			
//			String strDataFi = "-";
//			if (monitorTasca.getDataFi() != null) {
//				Date dataFi = monitorTasca.getDataFi();
//				DateFormat dateFormatFi = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
//				strDataFi = dateFormatFi.format(dataFi);
//			}
//			fiExecucio.add(getMessage(request, "monitor.tasques.fi.execucio") + ": " + (strDataFi));
			String strProperaExecucio = "-";
			if (monitorTasca.getProperaExecucio() != null) {
				Date dataProperaExecucio = monitorTasca.getProperaExecucio();
				DateFormat dateFormatProperaExecucio = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
				strProperaExecucio = dateFormatProperaExecucio.format(dataProperaExecucio);
			}
			properaExecucio.add(getMessage(request, "monitor.tasques.propera.execucio") + ": " + strProperaExecucio);
			observacions.add(getMessage(request, "monitor.tasques.observacions") + ": " + monitorTasca.getObservacions());
		}
		tasques.put("tasca", tasca);
		tasques.put("estat", estat);
		tasques.put("iniciExecucio", iniciExecucio);
		tasques.put("tempsExecucio", tempsExecucio);
//		tasques.put("fiExecucio", fiExecucio);
		tasques.put("properaExecucio", properaExecucio);
		tasques.put("observacions", observacions);

		return tasques; 
	}
}
