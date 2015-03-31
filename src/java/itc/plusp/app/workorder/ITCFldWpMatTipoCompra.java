package itc.plusp.app.workorder;

import java.rmi.RemoteException;
import java.util.Date;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.util.MXException;
import psdi.util.logging.MXLogger;
import psdi.util.logging.MXLoggerFactory;

/**
 *
 * @author TOSHIBA
 */
public class ITCFldWpMatTipoCompra extends MboValueAdapter {

  private final MXLogger log = MXLoggerFactory.getLogger("maximo.customization.WORKORDER");
  private final long LOGBEGIN = 0L;
  private final long LOGEND = 1L;
  private final long LOGRUNNING = 2L;

  public ITCFldWpMatTipoCompra(MboValue mbv) throws MXException, RemoteException {
    super(mbv);
  }

  /**
   * Metodo para inicializa
   * 
   * @throws MXException
   * @throws RemoteException 
   */
  @Override
  public void init() throws MXException, RemoteException {
    logdebug(LOGBEGIN, "init()");
    super.init();

    MboRemote mbo = getMboValue().getMbo();
    if (mbo.toBeAdded()) {
      logdebug("mbo.toBeAdded()");
      boolean directreq = getMboValue("DIRECTREQ").getBoolean();

      if (directreq) {
        logdebug("directreq");
        getMboValue().setFlag(READONLY, false);
        getMboValue().setFlag(REQUIRED, true);

        //getMboValue("ITCPROVEEDOR").setReadOnly(false);
        //getMboValue("ITCPROVEEDOR").setRequired(true);
        //getMboValue("ITCPROVEEDOR").setValueNull(NOACCESSCHECK | NOVALIDATION_AND_NOACTION);
      } else {
        logdebug("!directreq");
        getMboValue().setFlag(REQUIRED, false);
        getMboValue().setFlag(READONLY, true);

        //getMboValue("ITCPROVEEDOR").setValueNull(NOACCESSCHECK | NOVALIDATION_AND_NOACTION);
        //getMboValue("ITCPROVEEDOR").setRequired(false);
        //getMboValue("ITCPROVEEDOR").setReadOnly(true);
      }
    }
    logdebug(LOGEND, "init()");
  }

  @Override
  public void action() throws MXException, RemoteException {
    logdebug(LOGBEGIN, "action()");
    super.action();
    MboRemote thisMbo = getMboValue().getMbo();

    if (thisMbo != null) {
      ITCWPMaterialRemote wpmaterialMbo = (ITCWPMaterialRemote) thisMbo;

      logdebug(LOGRUNNING, "ITCWPMaterialRemote.itcSetValuesFromMarginsPolicies()");
      wpmaterialMbo.itcSetValuesFromMarginsPolicies();

      logdebug(LOGRUNNING, "ITCWPMaterialRemote.itcCalcularMargen()");
      wpmaterialMbo.itcCalcularMargen();

      if (!getMboValue().isNull() && !getMboValue("ITCPROVEEDOR").isNull()) {
        logdebug("!getMboValue().isNull() && !getMboValue(\"ITCPROVEEDOR\").isNull()");

        if (!getMboValue().getString().equals("STOCK")) {
          logdebug("!getMboValue().getString().equals(\"STOCK\")");
          MboSetRemote companiesSet = thisMbo.getMboSet("$COMPANIES", "COMPANIES", "parentcompany=:itcproveedor and orgid=:orgid and itctipocompra=:itctipocompra");

          if (companiesSet != null && !companiesSet.isEmpty()) {
            logdebug("companiesSet != null && !companiesSet.isEmpty()");
            companiesSet.setFlag(DISCARDABLE, true);
            MboRemote companies = companiesSet.getMbo(0);

            if (companies != null) {
              logdebug("companies != null");
              String vendor = companies.getString("COMPANY");
              logdebug("vendor", vendor);
              thisMbo.setValue("VENDOR", vendor, NOACCESSCHECK | NOVALIDATION);
            }
          }
        }
      }
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
