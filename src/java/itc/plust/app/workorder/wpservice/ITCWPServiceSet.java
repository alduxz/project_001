package itc.plust.app.workorder.wpservice;

import java.rmi.RemoteException;
import java.util.Date;
import psdi.mbo.Mbo;
import psdi.mbo.MboServerInterface;
import psdi.mbo.MboSet;
import psdi.plust.app.workorder.PlusTWPServiceSet;
import psdi.util.MXException;
import psdi.util.logging.MXLogger;
import psdi.util.logging.MXLoggerFactory;

/**
 *
 * @author TOSHIBA
 */
public class ITCWPServiceSet extends PlusTWPServiceSet implements ITCWPServiceSetRemote {

    private final MXLogger log = MXLoggerFactory.getLogger("maximo.customization.WORKORDER");
    private final long LOGBEGIN = 0L;
    private final long LOGEND = 1L;
    private final long LOGRUNNING = 2L;

    public ITCWPServiceSet(MboServerInterface ms) throws MXException, RemoteException {
        super(ms);
    }

    @Override
    protected Mbo getMboInstance(MboSet mboset) throws MXException, RemoteException {
        return new ITCWPService(mboset);
    }

    @Override
    public void itcCalculaPrecioOT() throws MXException, RemoteException {
        logdebug(LOGBEGIN, "FOR itcCalculaPrecioOT()");

        for (ITCWPServiceRemote wpmaterialMbo = (ITCWPServiceRemote) this.moveFirst(); wpmaterialMbo != null; wpmaterialMbo = (ITCWPServiceRemote) this.moveNext()) {
            wpmaterialMbo.itcCalculaPrecioOT();
        }

        logdebug(LOGEND, "END FOR itcCalculaPrecioOT()");
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
