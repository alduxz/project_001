package itc.plust.app.workorder.wpservice;

import itc.plust.app.workorder.ITCWORemote;
import java.rmi.RemoteException;
import java.util.Date;
import psdi.app.currency.CurrencyServiceRemote;
import static psdi.mbo.MboConstants.COUNT_AFTERSAVE;
import static psdi.mbo.MboConstants.NOACCESSCHECK;
import static psdi.mbo.MboConstants.NOVALIDATION_AND_NOACTION;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.mbo.MboSetRemote;
import psdi.plust.app.workorder.PlusTWPService;
import psdi.server.MXServer;
import psdi.util.MXException;
import psdi.util.MXMath;
import psdi.util.logging.MXLogger;
import psdi.util.logging.MXLoggerFactory;

/**
 *
 * @author TOSHIBA
 */
public class ITCWPService extends PlusTWPService implements ITCWPServiceRemote {

    private final MXLogger log = MXLoggerFactory.getLogger("maximo.customization.WORKORDER");
    private final long LOGBEGIN = 0L;
    private final long LOGEND = 1L;
    private final long LOGRUNNING = 2L;

    public ITCWPService(MboSet ms) throws MXException, RemoteException {
        super(ms);
    }

    @Override
    public void add() throws MXException, RemoteException {
        super.add();
    }

    @Override
    public void save() throws MXException, RemoteException {
        logdebug(LOGBEGIN, "save()");
        super.save();
        logdebug(LOGEND, "save()");
    }

    @Override
    public void itcCalcularMargen() throws MXException, RemoteException {
        logdebug(LOGBEGIN, "ITCWPService.itcCalcularMargen()");
        double itemqty = getDouble("ITEMQTY");
        double itcpreciolistaprov = getDouble("ITCPULISTAPROV");  //PRECIO PROVEEDOR
        double itclistprice = getDouble("ITCPULISTAOT");  //PRECIO OT

        double itcmargen = getDouble("ITCMARGEN");   //margen

        logdebug("WPSERVICE.ITEMQTY: " + Double.toString(itemqty));
        logdebug("WPSERVICE.ITCPRECIOLISTAPROV: " + Double.toString(itcpreciolistaprov));
        logdebug("WPSERVICE.ITCPULISTAOT: " + Double.toString(itclistprice));

        logdebug("WPSERVICE.ITCMARGEN: " + Double.toString(itcmargen));

        double itcvalorventaunitario = (itclistprice) * (1D + (itcmargen / 100D));  //importe unitario con margen de ganancia

        double itcvalorventalinea = itcvalorventaunitario * itemqty;
        double igvrate = 0D;

        MboSetRemote igvTaxSet = getMboSet("$IGVTAX", "TAX", "taxcode=:ITCTAXCODE and orgid=:orgid");

        if (igvTaxSet != null && !igvTaxSet.isEmpty()) {
            logdebug("igvTaxSet != null && !igvTaxSet.isEmpty()");
            igvTaxSet.setFlag(DISCARDABLE, true);
            MboRemote igvTax = igvTaxSet.getMbo(0);

            if (igvTax != null) {
                logdebug("igvTax != null");
                igvrate = igvTax.getDouble("TAXRATE");
            }
        }

        logdebug("igvrate: " + Double.toString(igvrate));

        double itcigv = itcvalorventalinea * (igvrate / 100D); //importe del impuesto para la linea
        double itctotal = itcvalorventalinea + itcigv;         //precio final de venta de linea
        double itctotalunitario = itctotal / itemqty;          //precio final de venta unitario

        logdebug("itcigv: " + Double.toString(itcigv));
        logdebug("itctotal: " + Double.toString(itctotal));

        setValue("ITCVVUNITARIO", itcvalorventaunitario, NOACCESSCHECK | NOVALIDATION_AND_NOACTION);
        setValue("ITCVVLINEA", itcvalorventalinea, NOACCESSCHECK);

        setValue("ITCIGV", itcigv, NOACCESSCHECK | NOVALIDATION_AND_NOACTION);
        setValue("ITCPVLINEA", itctotal, NOACCESSCHECK | NOVALIDATION_AND_NOACTION);
        setValue("ITCPVUNITARIO", itctotalunitario, NOACCESSCHECK | NOVALIDATION_AND_NOACTION);

        logdebug("WPSERVICE.ITCVVUNITARIO: " + Double.toString(getDouble("ITCVVUNITARIO")));
        logdebug("WPSERVICE.ITCVVLINEA: " + Double.toString(getDouble("ITCVVLINEA")));

        logdebug("WPSERVICE.ITCIGV: " + Double.toString(getDouble("ITCIGV")));
        logdebug("WPSERVICE.ITCPVLINEA: " + Double.toString(getDouble("ITCPVLINEA")));
        logdebug("WPSERVICE.ITCPVUNITARIO: " + Double.toString(getDouble("ITCPVUNITARIO")));

        logdebug(LOGEND, "ITCWPService.itcCalcularMargen()");
    }

    @Override
    public void itcCalculaPrecioOT() throws MXException, RemoteException {
        logdebug(LOGBEGIN, "ITCWPService.itcCalculaPrecioOT()");

        MboRemote mboOwner = getOwner();    //workorder

        if (!mboOwner.isNull("ITCCURRENCYCODE")) {   //WORKORDER.ITCCURRENCYCODE
            logdebug("!mboOwner.isNull(\"ITCCURRENCYCODE\")");

            CurrencyServiceRemote curService = (CurrencyServiceRemote) MXServer.getMXServer().lookup("CURRENCY");

            String currencyFrom = getMboValue("ITCCURRENCYCODE").getString();
            String currencyTo = mboOwner.getString("ITCCURRENCYCODE");
            double preciolistaProv = getMboValue("ITCPULISTAPROV").getDouble();
            Date exchangeDate = MXServer.getMXServer().getDate(getClientLocale(), getClientTimeZone());
            //Date exchangeDate = new Date();
            String orgid = mboOwner.getString("ORGID");
            double listPrice;

            logdebug("currencyFrom", currencyFrom);
            logdebug("currencyTo", currencyTo);
            logdebug("preciolistaProv", Double.toString(preciolistaProv));
            logdebug("exchangeDate", exchangeDate.toString());
            logdebug("orgid", orgid);

            if (currencyFrom != null && !currencyFrom.isEmpty()) {
                logdebug("currencyFrom != null && !currencyFrom.isEmpty()");
                listPrice = curService.calculateCurrencyCost(getUserInfo(), currencyFrom, currencyTo, preciolistaProv, exchangeDate, orgid);  //ok

                double exchangeRate = curService.getCurrencyExchangeRate(getUserInfo(), currencyFrom, currencyTo, exchangeDate, getString("ORGID"));
                logdebug("exchangeRate", exchangeRate);
            } else {
                logdebug("currencyFrom == null || currencyFrom.isEmpty()");
                listPrice = preciolistaProv;
            }

            logdebug("ITCPULISTAOT", Double.toString(listPrice));
            getMboValue("ITCPULISTAOT").setValue(listPrice, NOACCESSCHECK);

        }

        logdebug(LOGEND, "ITCWPService.itcCalculaPrecioOT()");
    }

    @Override
    public void delete(long accessModifier) throws MXException, RemoteException {
        logdebug(LOGBEGIN, "delete(long accessModifier)");
        super.delete(accessModifier);

        MboRemote refWo = getOwner();
        logdebug(LOGRUNNING, "this.itcUpdateTotals(refWo, false);");
        this.itcUpdateTotals(refWo, false);
        logdebug(LOGEND, "delete(long accessModifier)");
    }

    @Override
    public void undelete() throws MXException, RemoteException {
        logdebug(LOGBEGIN, "undelete()");
        super.undelete();
        MboRemote refWo = getOwner();

        if (getThisMboSet().count(COUNT_AFTERSAVE) == 0) {
            logdebug("if (getThisMboSet().count(COUNT_AFTERSAVE) == 0)");
            refWo.setValue("ITCTOTALSERVICIOS", 0.0D, NOACCESSCHECK);
        }

        logdebug(LOGRUNNING, "this.itcUpdateTotals(refWo, true);");
        this.itcUpdateTotals(refWo, true);

        logdebug(LOGEND, "undelete()");
    }

    private void itcUpdateTotals(MboRemote wo, boolean isGain) throws MXException, RemoteException {
        logdebug(LOGBEGIN, "itcUpdateTotals(MboRemote wo, boolean isGain)");
        logdebug("isGain", isGain);
        double itcvvlinea = this.getDouble("ITCVVLINEA");
        logdebug("itcvvlinea", itcvvlinea);

        if (MXMath.compareTo(itcvvlinea, 0.0D) != 0) {
            logdebug("if (MXMath.compareTo(itcvvlinea, 0.0D) != 0)");
            if (!isGain) {
                logdebug("if (!isGain)");
                itcvvlinea *= -1.0D;
            }
            logdebug("itcvvlinea", itcvvlinea);
            logdebug(LOGRUNNING, "((ITCWORemote) wo).itcIncrTotalServicios(itcvvlinea);");
            ((ITCWORemote) wo).itcIncrTotalServicios(itcvvlinea);
        }
        logdebug(LOGEND, "itcUpdateTotals(MboRemote wo, boolean isGain)");
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
