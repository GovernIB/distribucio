package es.caib.distribucio.war.controller;

import es.caib.distribucio.war.helper.MonitorHelper;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.*;

/**
 * Controlador per la gestió d'perfils
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/monitor")
public class MonitorSystemController extends BaseController {
	
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
		return mjson;
	}
}
