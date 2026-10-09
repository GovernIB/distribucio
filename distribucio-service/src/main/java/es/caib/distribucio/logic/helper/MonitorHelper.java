package es.caib.distribucio.logic.helper;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.List;

/**
 * Monitor.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
public class MonitorHelper {
	
	private static Boolean actiu = null;
	private static long prevUpTime;
	private static long	prevProcessCpuTime;
	private static RuntimeMXBean rmBean;
	private static com.sun.management.OperatingSystemMXBean sunOSMBean;
	private static final String NO_DISPONIBLE = "No disponible";

	private MonitorHelper() {
		throw new IllegalStateException("Utility class");
	}

	public static com.sun.management.OperatingSystemMXBean getSunOSMBean() {
		return sunOSMBean;
	}

	public static String getArch() {

		try {
			return sunOSMBean.getArch();
		} catch (Exception e) {
			return NO_DISPONIBLE;
		}
	}
	
	public static String getName() {

		try {
			return sunOSMBean.getName();
		} catch (Exception e) {
			return NO_DISPONIBLE;
		}
	}
	
	public static String getVersion() {

		try {
			return sunOSMBean.getVersion();
		} catch (Exception e) {
			return NO_DISPONIBLE;
		}
	}

	private static Result result;
	public static Boolean getActiu() {
		return actiu;
	}

	private static class Result {
		long upTime = -1L;
		long processCpuTime = -1L;
		float cpuUsage = 0;
		int nCPUs;
	}

	static {
		try {
			rmBean = ManagementFactory.getRuntimeMXBean();
			// reperisco l'MBean relativo al sunOS
			sunOSMBean = ManagementFactory.newPlatformMXBeanProxy(ManagementFactory.getPlatformMBeanServer(), ManagementFactory.OPERATING_SYSTEM_MXBEAN_NAME, com.sun.management.OperatingSystemMXBean.class);

			result = new Result();
			result.nCPUs = sunOSMBean.getAvailableProcessors();
			result.upTime = rmBean.getUptime();
			result.processCpuTime = 0;
			if (sunOSMBean != null) {
				result.processCpuTime = sunOSMBean.getProcessCpuTime();
			}
		} catch (Exception e) {
			log.error(MonitorHelper.class.getSimpleName() + " exception ", e.getMessage());
		}
	}

	private static ThreadMXBean bean = ManagementFactory.getThreadMXBean();

	public static long[] getThreadsIds() {
		return ManagementFactory.getThreadMXBean().getAllThreadIds();
	}

	public static String humanReadableByteCount(long bytes) {

		var unit = 1000;
		if (bytes < unit) {
			return bytes + " B";
		}
		var exp = (int) (Math.log(bytes) / Math.log(unit));
		var pre = "kMGTPE".charAt(exp - 1);
		return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
	}
	
	public static String getSystemCpuLoad() {
        try {
            double cpuLoad = sunOSMBean.getSystemCpuLoad();

            if (cpuLoad >= 0) {
                return String.format("%.2f%%", cpuLoad * 100);
            } else {
                return NO_DISPONIBLE;
            }
        } catch (Exception e) {
            return NO_DISPONIBLE;
        }
    }

	public static String getProcessCPULoad() {

		try {
			result.upTime = rmBean.getUptime();
			result.processCpuTime = sunOSMBean.getProcessCpuTime();
			if (result.upTime > 0L && result.processCpuTime >= 0L) {
				updateCPUInfo();
			}
			return result.cpuUsage + "%";
		} catch (Exception e) {
			return NO_DISPONIBLE;
		}
	}

	public static void updateCPUInfo() {

		if (prevUpTime > 0L && result.upTime > prevUpTime) {
			var elapsedCpu = result.processCpuTime - prevProcessCpuTime;
			var elapsedTime = result.upTime - prevUpTime;
			result.cpuUsage = Math.round(Math.min(100F, elapsedCpu / (elapsedTime * 10000F * result.nCPUs)));
		}
		prevUpTime = result.upTime;
		prevProcessCpuTime = result.processCpuTime;
	}

	/** Get CPU time in nanoseconds. */
	public static long getCpuTime() {

		if (!bean.isThreadCpuTimeSupported()) {
			return 0L;
		}
		var time = 0L;
		for (long i : getThreadsIds()) {
			long t = bean.getThreadCpuTime(i);
			if (t != -1) {
				time += t;
			}
		}
		return time;
	}

	public static long getCpuTimePercent() {

		if (!bean.isThreadCpuTimeSupported()) {
			return 0L;
		}
		var time = 0L;
		long t;
		for (var i : getThreadsIds()) {
			t = bean.getThreadCpuTime(i);
			if (t != -1) {
				time += t;
			}
		}
		return time;
	}

	/** Get user time in nanoseconds. */
	public static long getUserTime() {

		if (!bean.isThreadCpuTimeSupported()) {
			return 0L;
		}
		var time = 0L;
		long t;
		for (var i : getThreadsIds()) {
			t = bean.getThreadUserTime(i);
			if (t != -1) {
				time += t;
			}
		}
		return time;
	}

	/** Get system time in nanoseconds. */
	public static long getSystemTime() {

		if (!bean.isThreadCpuTimeSupported()) {
			return 0L;
		}
		var time = 0L;
		long tc;
		long tu;
		for (var i : getThreadsIds()) {
			tc = bean.getThreadCpuTime(i);
			tu = bean.getThreadUserTime(i);
			if (tc != -1 && tu != -1)
				time += (tc - tu);
		}
		return time;
	}

	/** Informacio d'un fil d'execucio de la JVM. */
	@Getter
	@AllArgsConstructor
	public static class FilInfo {
		private final long id;
		private final String nom;
		private final Thread.State estat;
		/** Percentatge de temps de CPU respecte del fil amb mes temps de CPU (0-100). */
		private final long tempsCpuPercent;
		/** Temps d'espera en nanosegons. */
		private final long tempsEsperaNs;
		/** Temps de bloqueig en nanosegons. */
		private final long tempsBloqueigNs;
	}

	/**
	 * Retorna els fils d'execucio de la JVM (excepte el fil "main"), amb el nom del lock si n'esperen algun.
	 * Buida si la JVM no permet mesurar el temps de CPU dels fils.
	 */
	public static List<FilInfo> getFils() {

		List<FilInfo> fils = new ArrayList<>();
		if (!bean.isThreadCpuTimeSupported()) {
			return fils;
		}
		long[] ids = getThreadsIds();
		ThreadInfo[] infos = bean.getThreadInfo(ids);
		long cpuMaxim = 0;
		for (long id : ids) {
			cpuMaxim = Math.max(cpuMaxim, bean.getThreadCpuTime(id));
		}
		for (var i = 0; i < ids.length; i++) {
			// Un fil pot haver acabat entre la consulta dels identificadors i la de la seva informacio
			if (infos[i] == null) {
				continue;
			}
			var nom = infos[i].getLockName() != null ? infos[i].getLockName() : infos[i].getThreadName();
			if ("main".equals(nom)) {
				continue;
			}
			var cpuPercent = cpuMaxim > 0 ? (long) (100 * ((float) bean.getThreadCpuTime(ids[i]) / (float) cpuMaxim)) : 0L;
			fils.add(new FilInfo(
					infos[i].getThreadId(),
					nom,
					infos[i].getThreadState(),
					Math.min(cpuPercent, 100L),
					Math.max(infos[i].getWaitedTime(), 0L),
					Math.max(infos[i].getBlockedTime(), 0L)));
		}
		return fils;
	}

	/** Nombre de fils en deadlock (monitors). */
	public static int getFilsDeadlock() {

		var ids = bean.findMonitorDeadlockedThreads();
		return ids != null ? ids.length : 0;
	}

	/** Nombre de fils daemon. */
	public static int getFilsDaemon() {

		return bean.getDaemonThreadCount();
	}

	/** Memoria maxima de la JVM en bytes, o -1 si no te limit. */
	public static long getMemoriaMaxima() {

		var maxima = Runtime.getRuntime().maxMemory();
		return maxima == Long.MAX_VALUE ? -1L : maxima;
	}
}
