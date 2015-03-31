/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package itc.plusp.app.workorder;

import java.rmi.RemoteException;
import java.util.Date;
import psdi.app.workorder.FldWpMatItemNum;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.util.MXException;
import psdi.util.logging.MXLogger;
import psdi.util.logging.MXLoggerFactory;

/**
 *
 * @author TOSHIBA
 */
public class ITCFldWpMatItemNum extends FldWpMatItemNum {

  private final MXLogger log = MXLoggerFactory.getLogger("maximo.customization.WORKORDER");
  private final long LOGBEGIN = 0L;
  private final long LOGEND = 1L;
  private final long LOGRUNNING = 2L;

  public ITCFldWpMatItemNum(MboValue mbv) throws MXException {
    super(mbv);
  }

  @Override
  public void action() throws MXException, RemoteException {
    logdebug(LOGBEGIN, "action()");
    super.action();
    MboRemote thisMbo = getMboValue().getMbo();

    if (thisMbo != null) {
      logdebug("thisMbo != null");
      ITCWPMaterialRemote wpmaterialMbo = (ITCWPMaterialRemote) thisMbo;
      MboRemote ownerMbo = wpmaterialMbo.getOwner();

      if (ownerMbo != null) {
        logdebug("ownerMbo != null");
        MboSetRemote ownerMboSet = ownerMbo.getThisMboSet();

        if (ownerMboSet != null) {
          logdebug("ownerMboSet != null");

          if (ownerMboSet.getApp() != null) {
            logdebug("ownerMboSet.getApp() != null");

            if (ownerMboSet.getApp().equals("PLUSPWO")) {
              logdebug("ownerMboSet.getApp().equals(\"PLUSPWO\")");
              MboSetRemote itemSet = thisMbo.getMboSet("ITEM");

              if (itemSet != null && !itemSet.isEmpty()) {
                logdebug("itemSet != null && !itemSet.isEmpty()");
                itemSet.setFlag(DISCARDABLE, true);
                MboRemote item = itemSet.getMbo(0);

                if (item != null) {
                  logdebug("item != null");
                  double itclistprice = item.getDouble("ITCLISTPRICE");
                  thisMbo.setValue("ITCPRECIOLISTAPROV", itclistprice, NOACCESSCHECK);
                }
              }

              if (hasAvailableBalance()) {
                logdebug("hasAvailableBalance()");
                thisMbo.setValue("DIRECTREQ", false, NOACCESSCHECK);
              } else {
                logdebug("!hasAvailableBalance()");
                thisMbo.setValue("DIRECTREQ", true, NOACCESSCHECK);
              }
            }
          }
        }
      }

      logdebug(LOGRUNNING, "ITCWPMaterialRemote.itcSetValuesFromMarginsPolicies()");
      wpmaterialMbo.itcSetValuesFromMarginsPolicies();

      logdebug(LOGRUNNING, "ITCWPMaterialRemote.itcCalcularMargen()");
      wpmaterialMbo.itcCalcularMargen();
    }

    logdebug(LOGEND, "action()");
  }

  private boolean hasAvailableBalance() throws MXException, RemoteException {
    logdebug(LOGBEGIN, "hasAvailableBalance()");
    MboRemote thisMbo = getMboValue().getMbo();
    MboSetRemote inventorySet = thisMbo.getMboSet("INVENTORYSTATUS");

    if (inventorySet != null && !inventorySet.isEmpty()) {
      logdebug("inventorySet != null && !inventorySet.isEmpty()");
      inventorySet.setFlag(DISCARDABLE, true);
      MboRemote inventory = inventorySet.getMbo(0);

      if (inventory != null) {
        logdebug("inventory != null");
        double avblbalance = inventory.getDouble("AVBLBALANCE");
        logdebug("avblbalance", Double.toString(avblbalance));

        if (avblbalance > 0.0D) {
          logdebug("return true;");
          logdebug(LOGEND, "hasAvailableBalance()");
          return true;
        } else {
          logdebug("return false;");
          logdebug(LOGEND, "hasAvailableBalance()");
          return false;
        }
      }
    }

    logdebug("return false;");
    logdebug(LOGEND, "hasAvailableBalance()");
    return false;
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
