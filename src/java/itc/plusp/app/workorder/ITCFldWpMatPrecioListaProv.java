package itc.plusp.app.workorder;

import java.rmi.RemoteException;
import java.util.Date;
import psdi.app.currency.CurrencyService;
import psdi.mbo.Mbo;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.mbo.MboValueAdapter;
import psdi.server.AppService;
import psdi.util.MXException;

import psdi.util.logging.MXLogger;
import psdi.util.logging.MXLoggerFactory;

/**
 *
 * @author ekishimoto
 * obtener el precio de lista segun la moneda del proveedor
 */
public class ITCFldWpMatPrecioListaProv extends MboValueAdapter {

  private final MXLogger log = MXLoggerFactory.getLogger("maximo.customization.WORKORDER");
  private final long LOGBEGIN = 0L;
  private final long LOGEND = 1L;
  private final long LOGRUNNING = 2L;

  public ITCFldWpMatPrecioListaProv(MboValue mbv) {
    super(mbv);
  }

  @Override
  public void action() throws MXException, RemoteException {
    logdebug(LOGBEGIN, "action()");
    super.action();

    Mbo mbo = getMboValue().getMbo();
    MboRemote mboOwner = mbo.getOwner();

    if (!mboOwner.isNull("ITCCURRENCYCODE")) {
      CurrencyService curService = (CurrencyService) ((AppService) mbo.getMboServer()).getMXServer().lookup("CURRENCY");

      logdebug(LOGRUNNING, "getVendorCurrency();");
      String currencyFrom = getVendorCurrency();
      String currencyTo = mboOwner.getString("ITCCURRENCYCODE");
      double preciolistaProv = getMboValue().getDouble();
      Date exchangeDate = new Date();
      String orgid = mboOwner.getString("ORGID");
      double listPrice;

      logdebug("currencyFrom: " + currencyFrom);
      logdebug("currencyTo: " + currencyTo);
      logdebug("preciolistaProv: " + Double.toString(preciolistaProv));
      logdebug("exchangeDate: " + exchangeDate.toString());
      logdebug("orgid: " + orgid);

      if (currencyFrom != null && !currencyFrom.isEmpty()) {
        logdebug("currencyFrom != null && !currencyFrom.isEmpty()");
        listPrice = curService.calculateCurrencyCost(mbo.getUserInfo(), currencyFrom, currencyTo, preciolistaProv, exchangeDate, orgid);
      } else {
        logdebug("currencyFrom == null || currencyFrom.isEmpty()");
        listPrice = preciolistaProv;
      }

      logdebug("listPrice", Double.toString(listPrice));
      getMboValue("ITCLISTPRICE").setValue(listPrice, NOACCESSCHECK);

    }
    logdebug(LOGEND, "action()");
  }

  private String getVendorCurrency() throws MXException, RemoteException {
    logdebug(LOGBEGIN, "getVendorCurrency()");
    String currency = null;
                        
    MboRemote mbo = getMboValue().getMbo();  // dominio?  --row
    MboSetRemote vendorSet = mbo.getMboSet("ITCCOMPANY");  //conjunto vendor con la relacion ITCCOMPANY || wpmaterial
  //la relación devuelve 1 solo registro
    if (vendorSet != null && !vendorSet.isEmpty()) {
      logdebug("vendorSet != null && !vendorSet.isEmpty()");
      vendorSet.setFlag(DISCARDABLE, true);
      MboRemote vendor = vendorSet.getMbo(0);   //obtiene el primer registro

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
