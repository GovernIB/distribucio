package es.caib.distribucio.logic.helper;

import es.caib.distribucio.logic.base.helper.AuthenticationHelper;
import es.caib.distribucio.logic.intf.base.model.DownloadableFile;
import es.caib.distribucio.logic.intf.base.model.ReportFileType;
import es.caib.distribucio.logic.intf.base.util.I18nUtil;
import es.caib.distribucio.logic.intf.dto.RegistreAnnexDto;
import es.caib.distribucio.logic.intf.dto.RegistreDto;
import es.caib.distribucio.logic.intf.registre.RegistreAnnexSicresTipusDocumentEnum;
import es.caib.distribucio.persist.repository.ExecucioMassivaRepository;
import es.caib.distribucio.persist.resourceentity.RegistreAnnexResourceEntity;
import es.caib.distribucio.persist.resourceentity.RegistreInteressatResourceEntity;
import es.caib.distribucio.persist.resourceentity.RegistreResourceEntity;
import es.caib.distribucio.persist.resourcerepository.RegistreInteressatResourceRepository;
import lombok.RequiredArgsConstructor;
import org.jopendocument.dom.spreadsheet.SpreadSheet;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RegistreResourceHelper {

    private static final DateTimeFormatter FORMAT_DATA = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
    private static final String[] COLUMNES_RAW = {
            "numero", "titol", "data", "data.presentacio", "oficina",
            "presencial", "documentacio.fisica", "numero.annexes", "procediment",
            "observacions", "origenNum", "origenData", "origenOficina",
            "interessats", "estat", "uo", "bustia"
    };
    private final AuthenticationHelper authenticationHelper;
    private final RegistreInteressatResourceRepository registreInteressatResourceRepository;
    private final ExecucioMassivaRepository execucioMassivaRepository;
    private final ConfigHelper configHelper;

    public DownloadableFile exportarAnotacions(List<RegistreResourceEntity> registres, ReportFileType fileType) throws IOException {
        String[] columnes = Arrays.stream(COLUMNES_RAW)
                .map(c -> I18nUtil.getInstance().getI18nMessage("registre.user.exportar.columna." + c))
                .toArray(String[]::new);

        List<String[]> files = registres.stream()
                .map(this::construirFila)
                .collect(Collectors.toList());

        switch (fileType) {
            case ODS: return generarOds(files, columnes);
            case CSV: return generarCsv(files, columnes);
            default: throw new IllegalArgumentException("Format no suportat: " + fileType);
        }
    }

    private String[] construirFila(RegistreResourceEntity r) {
        String[] fila = new String[COLUMNES_RAW.length];
        List<String> rols = List.of(authenticationHelper.getCurrentUserRoles());

        List<RegistreInteressatResourceEntity> interessats = registreInteressatResourceRepository.findByRegistreId(r.getId());

        fila[0] = r.getNumero();
        fila[1] = r.getExtracte();
        fila[2] = formatarData(r.getData());
        fila[3] = formatarData(r.getDataOrigen());
        fila[4] = r.getOficinaDescripcio();
        fila[5] = Boolean.TRUE.equals(r.getPresencial()) ? "Sí" : "No";
        fila[6] = r.getDocumentacioFisicaDescripcio();
        fila[7] = String.valueOf(r.getAnnexos().stream()
                .filter(p ->
                        (r.getJustificant() == null || !r.getJustificant().getId().equals(p.getId()))
                        && (r.getJustificantArxiuUuid() == null || !r.getJustificantArxiuUuid().equals(p.getFitxerArxiuUuid()))
                        && (!rols.contains("tothom") || p.getSicresTipusDocument() == null || !RegistreAnnexSicresTipusDocumentEnum.INTERN.getValor().equals(p.getSicresTipusDocument())) )
                .count());
        fila[8] = r.getProcedimentCodi();
        fila[9] = r.getObservacions();
        fila[10] = r.getNumeroOrigen();
        fila[11] = formatarData(r.getDataOrigen());
        fila[12] = r.getOficinaOrigenCodi();
        fila[13] = construirInteressats(interessats);
        fila[14] = r.getProcesEstat() != null
                ? I18nUtil.getInstance().getI18nMessage("registre.proces.estat.enum." + r.getProcesEstat())
                : "";
        fila[15] = r.getPare() != null ?r.getPare().getPare() != null ?r.getPare().getPare().getNom() :null :null;
        fila[16] = r.getPare() != null ?r.getPare().getNom() :null;

        return fila;
    }

    private String formatarData(Date data) {
        if (data == null) return "";
        return data.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
                .format(FORMAT_DATA);
    }

    private String construirInteressats(List<RegistreInteressatResourceEntity> interessats) {
        if (interessats == null || interessats.isEmpty()) return "";

        return interessats.stream()
                .map(i -> {
                    String nom = i.getNom() != null
                            ? i.getNom() + " " + i.getLlinatge1() + " " + i.getLlinatge2()
                            : i.getRaoSocial();
                    return nom + " (" + i.getDocumentNum() + ")";
                })
                .collect(Collectors.joining(" | "));
    }

    private DownloadableFile generarOds(List<String[]> files, String[] columnes) throws IOException {
        Object[][] filesArray = files.toArray(new Object[0][0]);
        TableModel model = new DefaultTableModel(filesArray, columnes);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        SpreadSheet.createEmpty(model).getPackage().save(baos);

        return new DownloadableFile(
                "exportacio.ods",
                "application/vnd.oasis.opendocument.spreadsheet",
                baos.toByteArray()
        );
    }

    private DownloadableFile generarCsv(List<String[]> files, String[] columnes) throws IOException {
        String nomFitxer = I18nUtil.getInstance().getI18nMessage("registre.user.exportar.nomFitxer") + ".csv";

        StringBuilder sb = new StringBuilder();
        sb.append('\ufeff');
        this.afegirLinia(sb, columnes, ';', ' ');
        for (String[] fila : files) {
            this.afegirLinia(sb, fila, ';', ' ');
        }

        return new DownloadableFile(nomFitxer, "text/csv; charset=UTF-8", sb.toString().getBytes(StandardCharsets.UTF_8));
    }

    private void afegirLinia(StringBuilder sb, String[] valors, char separador, char cometa) throws IOException {
        if (separador == ' ') {
            separador = ',';
        }
        boolean first = true;
        for (String valor: valors) {
            if (!first) {
                sb.append(separador);
            }
            if (cometa == ' ') {
                sb.append(seguirFormatCsv(valor));
            } else {
                sb.append(cometa).append(seguirFormatCsv(valor)).append(cometa);
            }
            first = false;
        }
        sb.append("\n");
    }
    private static String seguirFormatCsv(String valor) {
        if (valor == null) {
            return "";
        } else {
            String result = valor;
            if (result.contains("\"")) {
                result = result.replace("\"", "\"\"");
            }
            return result;
        }
    }


    public List<String> validarDescargaMassivaForm(List<RegistreResourceEntity> registres) {
        List<String> errors = new ArrayList<>();
        int maxExec = Integer.parseInt(configHelper.getConfig("es.caib.distribucio.exportar.annex.zip.exec.max", "5"));
        int maxSize = Integer.parseInt(configHelper.getConfig("es.caib.distribucio.exportar.annex.zip.mida.max", "10"));

        boolean enabled = "true".equals(configHelper.getConfig("es.caib.distribucio.exportar.annex.zip.enabled"));
        int exec = this.getNumeroExecucioMasiva();
        double midaAproximada = this.getMidaAproximada(registres);

        if (exec >= maxExec) {
            errors.add(I18nUtil.getInstance().getI18nMessage("registre.annex.descarregar.zip.exec.max", maxExec));
        }
        if (midaAproximada > (maxSize * 1024 * 1024)) {
            errors.add(I18nUtil.getInstance().getI18nMessage("registre.annex.descarregar.zip.size.max", String.format("%.2f", midaAproximada / 1024 / 1024), maxSize));
        }
        if (!enabled) {
            errors.add(I18nUtil.getInstance().getI18nMessage("registre.annex.descarregar.zip.disabled"));
        }

        return errors;
    }

    private double getMidaAproximada(List<RegistreResourceEntity> registresSeleccionats) {
        double tamany = 0;
        for (RegistreResourceEntity registre : registresSeleccionats) {
            for (RegistreAnnexResourceEntity annexos : registre.getAnnexos()) {
                tamany += annexos.getFitxerTamany() * obtenirRatioCompresio(annexos.getFitxerTipusMime());
            }
        }
        return tamany;
    }

    private double obtenirRatioCompresio(String mime) {
        if (mime == null) return 0.80; // Fallback seguro
        String m = mime.toLowerCase();

        //  Textos y datos (alta compresión)
        if (m.startsWith("text/") || m.contains("json") || m.contains("xml") || m.contains("csv") || m.contains("sql")) {
            return 0.30; // ~70% reducción
        }
        // PDF
        if (m.equals("application/pdf")) return 0.70;
        // Imágenes (ya están comprimidas)
        if (m.startsWith("image/")) return 0.95;
        //  Office legacy (.doc, .xls, .ppt)
        if (m.contains("msword") || m.contains("ms-excel") || m.contains("ms-powerpoint")) return 0.40;
        // Office moderno (.docx, .xlsx, .pptx) → ya son ZIP internamente
        if (m.contains("openxmlformats")) return 0.95;
        // Archivos comprimidos / Multimedia
        if (m.contains("zip") || m.contains("rar") || m.contains("7z") ||
                m.startsWith("video/") || m.startsWith("audio/")) {
            return 0.99; // Prácticamente 0% compresión
        }
        // Fallback genérico
        return 0.80;
    }

    private int getNumeroExecucioMasiva() {
        String user = SecurityContextHolder.getContext().getAuthentication().getName();
        Date date = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
        return execucioMassivaRepository.countNombreAccionsMassives(user, date);
    }

}
