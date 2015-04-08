package itc.plust.app.workorder;

import java.rmi.RemoteException;
import java.util.Date;
import psdi.mbo.MboSet;
import psdi.plust.app.workorder.PlusTWOActivity;
import psdi.util.MXException;
import psdi.util.MXMath;
import psdi.util.logging.MXLogger;
import psdi.util.logging.MXLoggerFactory;

/**
 *
 * @author aescobar
 */
public class ITCWOActivity extends PlusTWOActivity implements ITCWOActivityRemote, ITCWORemote {

    private final MXLogger log = MXLoggerFactory.getLogger("maximo.customization.WORKORDER");
    private final long LOGBEGIN = 0L;
    private final long LOGEND = 1L;
    private final long LOGRUNNING = 2L;

    /* needed */
    public ITCWOActivity(MboSet ms) throws MXException, RemoteException {
        super(ms);
    }

    /**
     * Metodo para incrementar el total de los materiales en la woactivity
     *
     * @param incrAmount monto a incrementar
     * @throws MXException
     * @throws RemoteException
     */
    @Override
    public void itcIncrTotalMateriales(double incrAmount) throws MXException, RemoteException {
        logdebug(LOGBEGIN, "itcIncrTotalMateriales(double incrAmount)");
        logdebug("incrAmount", incrAmount);
        double newValue = MXMath.add(getDouble("ITCTOTALMATERIALES"), incrAmount);
        logdebug("newValue", newValue);

        if (MXMath.compareTo(newValue, 0.0D) > 0) {
            logdebug("if (MXMath.compareTo(newValue, 0.0D) > 0)");
            setValue("ITCTOTALMATERIALES", newValue, NOACCESSCHECK);
        } else {
            logdebug("if (MXMath.compareTo(newValue, 0.0D) <= 0)");
            setValue("ITCTOTALMATERIALES", 0.0D, NOACCESSCHECK);
        }
        logdebug(LOGEND, "itcIncrTotalMateriales(double incrAmount)");
    }

    /**
     * Metodo para incrementar el monto total de la mano de obra en la
     * woactivity
     *
     * @param incrAmount monto a incrementar
     * @throws MXException
     * @throws RemoteException
     */
    @Override
    public void itcIncrTotalManoObra(double incrAmount) throws MXException, RemoteException {
        logdebug(LOGBEGIN, "itcIncrTotalManoObra(double incrAmount)");
        logdebug("incrAmount", incrAmount);
        double newValue = MXMath.add(getDouble("ITCTOTALMANOOBRA"), incrAmount);
        logdebug("newValue", newValue);

        if (MXMath.compareTo(newValue, 0.0D) > 0) {
            logdebug("if (MXMath.compareTo(newValue, 0.0D) > 0)");
            setValue("ITCTOTALMANOOBRA", newValue, NOACCESSCHECK);
        } else {
            logdebug("if (MXMath.compareTo(newValue, 0.0D) <= 0)");
            setValue("ITCTOTALMANOOBRA", 0.0D, NOACCESSCHECK);
        }
        logdebug(LOGEND, "itcIncrTotalManoObra(double incrAmount)");
    }

    /**
     * Metodo para incrementar el monto total de los servicios en la woactivity
     *
     * @param incrAmount monto a incrementar
     * @throws MXException
     * @throws RemoteException
     */
    @Override
    public void itcIncrTotalServicios(double incrAmount) throws MXException, RemoteException {
        logdebug(LOGBEGIN, "itcIncrTotalServicios(double incrAmount)");
        logdebug("incrAmount", incrAmount);
        double newValue = MXMath.add(getDouble("ITCTOTALSERVICIOS"), incrAmount);
        logdebug("newValue", newValue);

        if (MXMath.compareTo(newValue, 0.0D) > 0) {
            logdebug("if (MXMath.compareTo(newValue, 0.0D) > 0)");
            setValue("ITCTOTALSERVICIOS", newValue, NOACCESSCHECK);
        } else {
            logdebug("if (MXMath.compareTo(newValue, 0.0D) <= 0)");
            setValue("ITCTOTALSERVICIOS", 0.0D, NOACCESSCHECK);
        }
        logdebug(LOGEND, "itcIncrTotalServicios(double incrAmount)");
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
