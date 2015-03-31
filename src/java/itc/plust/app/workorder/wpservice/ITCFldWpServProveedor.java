package itc.plust.app.workorder.wpservice;

import itc.plust.app.workorder.*;
import java.rmi.RemoteException;
import java.util.Date;
import static psdi.mbo.MboConstants.NOACCESSCHECK;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXException;
import psdi.util.logging.MXLogger;
import psdi.util.logging.MXLoggerFactory;

/**
 *
 * @author ekishimoto
 */
public class ITCFldWpServProveedor extends MboValueAdapter {

    private final MXLogger log = MXLoggerFactory.getLogger("maximo.customization.WORKORDER");
    private final long LOGBEGIN = 0L;
    private final long LOGEND = 1L;
    private final long LOGRUNNING = 2L;

    public ITCFldWpServProveedor(MboValue mbv) throws MXException, RemoteException {
        super(mbv);
    }

    //metodo para agregar validacion de la moneda del proveedor
    @Override
    public void action() throws MXException, RemoteException {
        logdebug(LOGBEGIN, "action()");
        super.action();
        MboRemote thisMbo = getMboValue().getMbo();

        if (thisMbo != null) {

            logdebug("(thisMbo != null) ");

            String currencyFrom = getVendorCurrency();

            logdebug("currencyFrom : ", currencyFrom);

            getMboValue("ITCCURRENCYCODE").setValue(currencyFrom, NOACCESSCHECK);
        }

        logdebug(LOGEND, "action()");
    }

    //metodo para obtener la moneda del proveedor
    private String getVendorCurrency() throws MXException, RemoteException {
        logdebug(LOGBEGIN, "getVendorCurrency()");  //2
        String currency = null;
        MboRemote mbo = getMboValue().getMbo();
        MboSetRemote vendorSet = mbo.getMboSet("ITCCOMPANY");  //relacion ITCOMPANY del objeto wpmaterial // select *

        if (vendorSet != null && !vendorSet.isEmpty()) {   //evaluar nulo o vacio
            logdebug("vendorSet != null && !vendorSet.isEmpty()");
            vendorSet.setFlag(DISCARDABLE, true);
            MboRemote vendor = vendorSet.getMbo(0);  //obtiene el primer registro del conjunto ITCCOMPANY

            if (vendor != null) {
                logdebug("vendor != null");
                currency = vendor.getString("CURRENCYCODE");  //
            }
        }

        logdebug("currency", currency);
        logdebug("return currency;");
        logdebug(LOGEND, "getVendorCurrency()");
        return currency;
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
