/**
 *
 */
package es.caib.distribucio.plugin.caib.usuari;

import es.caib.comanda.model.server.monitoring.EstatSalut;
import es.caib.comanda.model.server.monitoring.IntegracioPeticions;
import es.caib.distribucio.plugin.AbstractSalutPlugin;
import es.caib.distribucio.plugin.SistemaExternException;
import es.caib.distribucio.plugin.usuari.DadesUsuari;
import es.caib.distribucio.plugin.usuari.DadesUsuariPlugin;
import io.micrometer.core.instrument.MeterRegistry;
import org.fundaciobit.pluginsib.userinformation.RolesInfo;
import org.fundaciobit.pluginsib.userinformation.UserInfo;
import org.fundaciobit.pluginsib.userinformation.soffid.SoffidUserInformationPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class DadesUsuariPluginSoffid extends SoffidUserInformationPlugin implements DadesUsuariPlugin {

    public DadesUsuariPluginSoffid(String propertyKeyBase, Properties properties, boolean configuracioEspecifica) {
        super(propertyKeyBase, properties);
        salutPluginComponent.setConfiguracioEspecifica(configuracioEspecifica);
    }

    public DadesUsuariPluginSoffid(String propertyKeyBase) {
        super(propertyKeyBase);
    }

    @Override
    public DadesUsuari findAmbCodi(String usuariCodi) throws SistemaExternException {
        LOGGER.debug("Consulta de les dades de l'usuari (usuariCodi=" + usuariCodi + ")");
        try {
            long start = System.currentTimeMillis();
            UserInfo userInfo = getUserInfoByUserName(usuariCodi);
            DadesUsuari dadesUsuari = toDadesUsuari(userInfo);
            salutPluginComponent.incrementarOperacioOk(System.currentTimeMillis() - start);
            return dadesUsuari;
        } catch (Exception ex) {
            salutPluginComponent.incrementarOperacioError();
            throw new SistemaExternException("Error al consultar l'usuari amb codi " + usuariCodi, ex);
        }
    }

    @Override
    public List<DadesUsuari> findAmbGrup(String grupCodi) throws SistemaExternException {
        LOGGER.debug("Consulta dels usuaris del grup (grupCodi=" + grupCodi + ")");
        try {
            long start = System.currentTimeMillis();

            UserInfo[] users = super.getUserInfoByRol(grupCodi);
            List<DadesUsuari> dadesUsuaris = Arrays.stream(users)
                    .map(this::toDadesUsuari)
                    .collect(Collectors.toList());

            salutPluginComponent.incrementarOperacioOk(System.currentTimeMillis() - start);
            return dadesUsuaris;
        } catch (Exception ex) {
            salutPluginComponent.incrementarOperacioError();
            throw new SistemaExternException("Error al consultar els usuaris del grup " + grupCodi, ex);
        }
    }

    @Override
    public String[] getUsernamesByRol(String rol) throws Exception {
        try {
            long start = System.currentTimeMillis();
            String[] usuaris = super.getUsernamesByRol(rol);

            salutPluginComponent.incrementarOperacioOk(System.currentTimeMillis() - start);
            return usuaris;
        } catch (Exception ex) {
            salutPluginComponent.incrementarOperacioError();
            throw new SistemaExternException("Error al consultar els usuaris del rol " + rol, ex);
        }
    }

    @Override
    public List<String> findRolsPerUsuari(String usuariCodi) throws SistemaExternException {
        try {
            RolesInfo ri = getRolesByUsername(usuariCodi);
            if (ri != null && ri.getRoles() != null) {
                return eliminarDuplicados(Arrays.asList(ri.getRoles()));
            }
        } catch (Exception ex) {
            log.error("Error al consultar els rols del usuari " + usuariCodi, ex);
        }

        return new ArrayList<String>();
    }

    private List<String> eliminarDuplicados(List<String> lista) {
        if (lista == null) return null;
        return new ArrayList<>(new LinkedHashSet<>(lista)); // Set elimina duplicados y mantiene el orden
    }

    private DadesUsuari toDadesUsuari(UserInfo userInfo) {
        if (userInfo != null) {
            DadesUsuari dadesUsuari = new DadesUsuari();
            dadesUsuari.setCodi(userInfo.getUsername());
            dadesUsuari.setNomSencer(userInfo.getFullName());
            dadesUsuari.setNom(userInfo.getName());
            dadesUsuari.setLlinatges(userInfo.getSurname1() + (userInfo.getSurname2() != null ? " " + userInfo.getSurname2() : ""));
            dadesUsuari.setNif(userInfo.getAdministrationID());
            dadesUsuari.setEmail(userInfo.getEmail());
            dadesUsuari.setActiu(true);
            return dadesUsuari;
        } else {
            return null;
        }
    }

    // Mètodes de SALUT
    // /////////////////////////////////////////////////////////////////////////////////////////////
    private AbstractSalutPlugin salutPluginComponent = new AbstractSalutPlugin();

    public void init(MeterRegistry registry, String codiPlugin) {
        salutPluginComponent.init(registry, codiPlugin);
    }

    @Override
    public boolean teConfiguracioEspecifica() {
        return salutPluginComponent.teConfiguracioEspecifica();
    }

    @Override
    public EstatSalut getEstatPlugin() {
        return salutPluginComponent.getEstatPlugin();
    }

    @Override
    public IntegracioPeticions getPeticionsPlugin() {
        return salutPluginComponent.getPeticionsPlugin();
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(DadesUsuariPluginSoffid.class);

}
