package itc.plusp.app.workorder;

import java.rmi.RemoteException;
import java.util.Date;
import psdi.app.currency.CurrencyService;
import psdi.mbo.Mbo;
import static psdi.mbo.MboConstants.NOACCESSCHECK;
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
 */
public class ITCFldWpMatProveedor extends MboValueAdapter {

  private final MXLogger log = MXLoggerFactory.getLogger("maximo.customization.WORKORDER");
  private final long LOGBEGIN = 0L;
  private final long LOGEND = 1L;
  private final long LOGRUNNING = 2L;

  public ITCFldWpMatProveedor(MboValue mbv) throws MXException, RemoteException {
    super(mbv);
  }

  //metodo para agregar validacion de la moneda del proveedor
  @Override
  public void action() throws MXException, RemoteException {
    logdebug(LOGBEGIN, "action()");
    super.action();
    MboRemote thisMbo = getMboValue().getMbo();

    if (thisMbo != null) {
        
      if (!getMboValue().isNull() && !getMboValue("ITCTIPOCOMPRA").isNull()) {
        logdebug("!getMboValue().isNull() && !getMboValue(\"ITCTIPOCOMPRA\").isNull()");

        if (!getMboValue("ITCTIPOCOMPRA").getString().equals("LOC")) {
          logdebug("!getMboValue(\"ITCTIPOCOMPRA\").getString().equals(\"STOCK\")");
          
//?   - regresa 1 registro
          MboSetRemote companiesSet = thisMbo.getMboSet("$COMPANIES", "COMPANIES", "parentcompany=:itccompany and orgid=:orgid and itctipocompra=:itctipocompra");

          if (companiesSet != null && !companiesSet.isEmpty()) {
            logdebug("companiesSet != null && !companiesSet.isEmpty()");
// flag discardable??
            companiesSet.setFlag(DISCARDABLE, true);
// se obtiene el primer registro
            MboRemote companies = companiesSet.getMbo(0);

            if (companies != null) {
              logdebug("companies != null");
              String vendor = companies.getString("COMPANY");
              logdebug("vendor", vendor);  
              
//setea el codigo de compañia
              thisMbo.setValue("VENDOR", vendor, NOACCESSCHECK | NOVALIDATION);
            }
          }
        }
      }  //opc
      
//WPMATERIAL.ITCPULISTAPROV - ingresado por el usuario
      if (!getMboValue("ITCPULISTAPROV").isNull()) {
        logdebug("!getMboValue(\"ITCPULISTAPROV\").isNull()");  //debug
        Mbo mbo = getMboValue().getMbo();       //
        MboRemote mboOwner = mbo.getOwner();    //workorder

        if (!mboOwner.isNull("ITCCURRENCYCODE")) {   //WORKORDER.ITCCURRENCYCODE
          logdebug("!mboOwner.isNull(\"ITCCURRENCYCODE\")");
          CurrencyService curService = (CurrencyService) ((AppService) mbo.getMboServer()).getMXServer().lookup("CURRENCY");

          String currencyFrom = getVendorCurrency(); //obtener moneda del proveedor
          String currencyTo = mboOwner.getString("ITCCURRENCYCODE"); //moneda de la OT
          double preciolistaProv = getMboValue("ITCPULISTAPROV").getDouble();  //precio de moneda del proveedor, ingresado por el usuario
          Date exchangeDate = new Date();  //variable fecha
          String orgid = mboOwner.getString("ORGID");  //valor de la organización de la OT
          double listPrice;

          logdebug("currencyFrom", currencyFrom);
          logdebug("currencyTo", currencyTo);
          logdebug("preciolistaProv", Double.toString(preciolistaProv));
          logdebug("exchangeDate", exchangeDate.toString());
          logdebug("orgid", orgid);

          if (currencyFrom != null && !currencyFrom.isEmpty()) {  
            logdebug("currencyFrom != null && !currencyFrom.isEmpty()");
            //convertir a la moneda de la OT
            listPrice = curService.calculateCurrencyCost(mbo.getUserInfo(), currencyFrom, currencyTo, preciolistaProv, exchangeDate, orgid);
          } else {
            logdebug("currencyFrom == null || currencyFrom.isEmpty()");
            // la moneda destino es null o vacio
            listPrice = preciolistaProv;
          }

          logdebug("listPrice", Double.toString(listPrice));
          //setear el valor de precio de lista ACTUALIZADO, itcpulistaot
          getMboValue("ITCPULISTAOT").setValue(listPrice, NOACCESSCHECK);  
        }
      }
    }

    logdebug(LOGEND, "action()");
  }

  //metodo para obtener la moneda del proveedor
  private String getVendorCurrency() throws MXException, RemoteException {
    logdebug(LOGBEGIN, "getVendorCurrency()");
    String currency = null;
    MboRemote mbo = getMboValue().getMbo();
    MboSetRemote vendorSet = mbo.getMboSet("ITCCOMPANY");  //relacion ITCOMPANY del objeto wpmaterial

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
