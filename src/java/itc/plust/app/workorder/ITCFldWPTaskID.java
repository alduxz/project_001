/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package itc.plust.app.workorder;

import java.rmi.RemoteException;
import java.util.Date;
import psdi.app.workorder.WO;
import psdi.mbo.MboRemote;
import psdi.mbo.MboValue;
import psdi.plust.app.workorder.PlusTFldWPTaskID;
import psdi.util.MXException;
import psdi.util.logging.MXLogger;
import psdi.util.logging.MXLoggerFactory;

/**
 *
 * @author TOSHIBA
 */
public class ITCFldWPTaskID extends PlusTFldWPTaskID {

    private final MXLogger log = MXLoggerFactory.getLogger("maximo.customization.WORKORDER");
    private final long LOGBEGIN = 0L;
    private final long LOGEND = 1L;
    private final long LOGRUNNING = 2L;

    public ITCFldWPTaskID(MboValue mbv) throws MXException {
        super(mbv);
    }

    @Override
    public void action() throws MXException, RemoteException {
        logdebug(LOGBEGIN, "action()");
        super.action();

        if (!getMboValue().isNull()) {
            String str = getMboValue().getString(); //TASKID
            MboRemote localMbo = getMboValue().getMbo(); //WPMATERIAL -
            MboRemote localMboRemote1 = getMboValue().getMbo().getOwner();  //WOKRORDER PADRE
            ITCWORemote localWO = (ITCWORemote) ((WO) localMboRemote1).getParentMbo();

            MboRemote localMboRemote2 = localWO.getWOforTask(str);

            if (localMboRemote2 != null) {

                localMbo.setValue("ITCCOMPRA", localMboRemote2.getBoolean("ITCCOMPRA"), 2L);
                localMbo.setValue("ITCCOMPANY", localMboRemote2.getString("ITCCOMPANY"), 2L);
                localMbo.setValue("ITCCURRENCYCODE", localMboRemote2.getString("ITCCURRENCYCODE"), 2L);
                localMbo.setValue("ITCTIPOCOMPRA", localMboRemote2.getString("ITCTIPOCOMPRA"), 2L);
                localMbo.setValue("ITCPULISTAPROV", localMboRemote2.getDouble("ITCPULISTAPROV"), 2L);
                localMbo.setValue("ITCPULISTAOT", localMboRemote2.getDouble("ITCPULISTAOT"), 2L);
                localMbo.setValue("ITCUIMP", localMboRemote2.getDouble("ITCUIMP"), 2L);
                localMbo.setValue("ITCMARGEN", localMboRemote2.getDouble("ITCMARGEN"), 2L);
                localMbo.setValue("ITCDCTOCLIENTE", localMboRemote2.getDouble("ITCDCTOCLIENTE"), 2L);
                localMbo.setValue("ITCVVUNITARIO", localMboRemote2.getDouble("ITCVVUNITARIO"), 2L);
                localMbo.setValue("ITCVVLINEA", localMboRemote2.getDouble("ITCVVLINEA"), 2L);
                localMbo.setValue("ITCTAXCODE", localMboRemote2.getDouble("ITCTAXCODE"), 2L);
                localMbo.setValue("ITCIGV", localMboRemote2.getDouble("ITCIGV"), 2L);
                localMbo.setValue("ITCPVUNITARIO", localMboRemote2.getDouble("ITCPVUNITARIO"), 2L);
                localMbo.setValue("ITCPVLINEA", localMboRemote2.getDouble("ITCPVLINEA"), 2L);

            }
        }

        MboRemote thisMbo = getMboValue().getMbo();

        if (thisMbo != null) {
            logdebug("thisMbo != null");
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
