package es.caib.distribucio.logic.service;

import es.caib.comanda.model.v1.log.FitxerContingut;
import es.caib.comanda.model.v1.log.FitxerInfo;
import es.caib.comanda.ms.log.helper.LogHelper;
import es.caib.distribucio.logic.helper.ConfigHelper;
import es.caib.distribucio.logic.intf.service.LogService;
import joptsimple.internal.Strings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class LogServiceImpl implements LogService {

    private final ConfigHelper configHelper;

    private final String logPath = "es.caib.distribucio.plugin.fitxer.logs.path";
    private String getLogPath() {
        return configHelper.getConfig(logPath);
    }

    @Override
    public List<FitxerInfo> llistarFitxers() {
        return LogHelper.llistarFitxers(this.getLogPath(), "");
    }

    @Override
    public FitxerContingut getFitxerByNom(String nom) {
        return LogHelper.getFitxerByNom(this.getLogPath(), nom);
    }

    private final BlockingQueue<String> queue = new ArrayBlockingQueue<>(100);

    @Override
    public BlockingQueue<String> getQueue() {
        return queue;
    }

    @Override
    public void tailLogFile(String filePath) {
        var directoriPath = this.getLogPath();
        if (Strings.isNullOrEmpty(directoriPath)) {
            log.error("[LogService.tailLogFile] No s'ha especificat valor a la propietat \"" + logPath + "\"");
            return;
        }
        var path = Paths.get(directoriPath, filePath);
        new Thread(() -> {
            try (BufferedReader reader = Files.newBufferedReader(path)) {
                reader.skip(Files.size(path));
                while (true) {
                    String line = reader.readLine();
                    if (line != null) {
                        queue.put(line);
                    } else {
                        // Sleep for a short time to avoid busy waiting
                        TimeUnit.MILLISECONDS.sleep(500);
                    }
                }
            } catch (IOException e) {
                log.error("[LogService.tailLogFile] IOException llegint el fitxer de log: " + e.getMessage(), e);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("[LogService.tailLogFile] Thread interrupted: " + e.getMessage());
            }
        }).start();
    }

    @Override
    public List<String> readLastNLines(String nomFitxer, Long nLinies) {
        return LogHelper.readLastNLines(this.getLogPath(), nomFitxer, nLinies);
    }
}
