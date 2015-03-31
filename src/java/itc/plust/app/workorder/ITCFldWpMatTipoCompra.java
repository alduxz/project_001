/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package itc.plust.app.workorder;

import java.rmi.RemoteException;
import java.util.Date;
import psdi.mbo.MboRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXException;
import psdi.util.logging.MXLogger;
import psdi.util.logging.MXLoggerFactory;

/**
 * Clase para el campo Tipo de Compra: LOC o IMP
 *
 * @author aescobar
 */
public class ITCFldWpMatTipoCompra extends MboValueAdapter {

    private final MXLogger log = MXLoggerFactory.getLogger("maximo.customization.WORKORDER");
    private final long LOGBEGIN = 0L;
    private final long LOGEND = 1L;
    private final long LOGRUNNING = 2L;

    public ITCFldWpMatTipoCompra(MboValue mbv) throws MXException {
        super(mbv);
    }

    @Override
    public void action() throws MXException, RemoteException {
        logdebug(LOGBEGIN, "action()");
        super.action();
        MboRemote thisMbo = getMboValue().getMbo();

        if (thisMbo != null) {

            String tipoCompra = getMboValue().getString();
            logdebug("tipoCompra", tipoCompra);

            //revisar
            if ("LOC".equals(tipoCompra)) {
                getMboValue("ITCUIMP").setReadOnly(true);
                getMboValue("ITCUIMP").setValueNull(NOACCESSCHECK);
            } else {
                getMboValue("ITCUIMP").setReadOnly(false);
                getMboValue("ITCUIMP").setRequired(true);
            }

            ITCWPMaterialRemote wpmaterialMbo = (ITCWPMaterialRemote) thisMbo;

            logdebug(LOGRUNNING, "ITCWPMaterialRemote.itcCalcularMargen(getMboValue())");
            wpmaterialMbo.itcCalcularMargen();
        }
        logdebug(LOGEND, "action()");
    }

    /**
     * Este método imprime mensajes en log de MAXIMO
     *
     * @param type
     * @param message
     */
    private void logdebug(String message) {
        if (log.isDebugEnabled()) {
            log.debug(">>>> MESSAGE " + getClass().getSimpleName() + " -> " + message);
        }
    }

    /**
     * Este método imprime mensajes en log de MAXIMO
     *
     * @param type
     * @param message
     */
    private void logdebug(long type, String method) {
        if (log.isDebugEnabled()) {
            if (type == 0L) {
                log.debug("<<<< BEGIN " + getClass().getName() + ":" + method + " >>>>");
            } else if (type == 1L) {
                log.debug("<<<< ==> END " + getClass().getName() + ":" + method + " >>>>");
            } else if (type == 2L) {
                log.debug(">>>> RUNNING " + getClass().getSimpleName() + " -> " + method);
            }
        }
    }

    /**
     * Este método imprime mensajes en log de MAXIMO
     *
     * @param varname
     * @param var
     */
    private void logdebug(String varname, Object var) {
        if (log.isDebugEnabled()) {
            String value = "";

            if (var != null) {
                if (var instanceof String) {
                    value = (String) var;
                } else if (var instanceof Integer) {
                    value = Integer.toString((Integer) var);
                } else if (var instanceof Long) {
                    value = Long.toString((Long) var);
                } else if (var instanceof Double) {
                    value = Double.toString((Double) var);
                } else if (var instanceof Boolean) {
                    value = Boolean.toString((Boolean) var);
                } else if (var instanceof Date) {
                    value = ((Date) var).toString();
                }
            } else {
                value = "null";
            }
            log.debug(">>>> VALUE " + getClass().getSimpleName() + " -> " + varname + ": " + value);
        }
    }
}
