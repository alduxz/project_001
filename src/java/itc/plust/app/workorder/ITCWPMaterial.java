package itc.plust.app.workorder;

import java.rmi.RemoteException;
import java.util.Date;
import psdi.app.currency.CurrencyServiceRemote;
import static psdi.mbo.MboConstants.COUNT_AFTERSAVE;
import static psdi.mbo.MboConstants.NOACCESSCHECK;
import static psdi.mbo.MboConstants.NOVALIDATION_AND_NOACTION;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.mbo.MboSetRemote;
import psdi.plust.app.workorder.PlusTWPMaterial;
import psdi.server.MXServer;
import psdi.util.MXApplicationException;
import psdi.util.MXException;
import psdi.util.MXMath;
import psdi.util.logging.MXLogger;
import psdi.util.logging.MXLoggerFactory;

/**
 * Clase extendida para el calculo de cotización en la pestaña Materiales de la
 * aplicacion WorkOrder
 *
 * @author AESCOBAR psdi.plust.app.workorder.PlusTWPMaterialSet
 */
public class ITCWPMaterial extends PlusTWPMaterial implements ITCWPMaterialRemote {

    private final MXLogger log = MXLoggerFactory.getLogger("maximo.customization.WORKORDER");
    private final long LOGBEGIN = 0L;
    private final long LOGEND = 1L;
    private final long LOGRUNNING = 2L;

    /**
     *
     * @param ms
     * @throws MXException
     * @throws RemoteException
     */
    public ITCWPMaterial(MboSet ms) throws MXException, RemoteException {
        super(ms);
    }

    /**
     *
     * @throws MXException
     * @throws RemoteException
     */
    @Override
    public void add() throws MXException, RemoteException {
        super.add();
    }

    /**
     * Metodo sobreescrito para validar los campos ITCCURRENCYCODE, ITCCOMPANY,
     * ITCTIPOCOMPRA, si el check requiere compra esta marcado.
     *
     * @throws MXException
     * @throws RemoteException
     */
    @Override
    public void appValidate() throws MXException, RemoteException {

        logdebug(LOGBEGIN, "appValidate()");

        String param2 = "";
        logdebug("param2", param2);

        MboRemote thisMbo = getMboValue("ITCCOMPRA").getMbo();

        if (thisMbo != null) {
            if (getMboValue("ITCCOMPRA").getBoolean()) {
                logdebug("getMboValue(\"ITCCOMPRA\").getBoolean()");
                if (isNull("ITCCURRENCYCODE") || isNull("ITCCOMPANY") || isNull("ITCTIPOCOMPRA")) {
                    logdebug(" if (isNull(\"ITCCURRENCYCODE\") || isNull(\"ITCCOMPANY\") || isNull(\"ITCTIPOCOMPRA\")) ");
                    if (isNull("ITCCURRENCYCODE")) {
                        logdebug(" isNull(\"ITCCURRENCYCODE\") ");
                        param2 = getMboValue("ITCCURRENCYCODE").getColumnTitle() + ", ";
                    }
                    if (isNull("ITCCOMPANY")) {
                        logdebug(" isNull(\"ITCCOMPANY\") ");
                        param2 = param2 + getMboValue("ITCCOMPANY").getColumnTitle() + ", ";
                    }
                    if (isNull("ITCTIPOCOMPRA")) {
                        logdebug(" isNull(\"ITCTIPOCOMPRA\") ");
                        param2 = param2 + getMboValue("ITCTIPOCOMPRA").getColumnTitle() + ", ";
                    }

                    Object[] params = {param2};
                    throw new MXApplicationException("workorder", "itcvalornulo", params);
                }
                if ("IMP".equals(getMboValue("ITCTIPOCOMPRA").getString())) {
                    logdebug("\"IMP\".equals(getMboValue(\"ITCTIPOCOMPRA\").getString() ");
                    if (isNull("ITCUIMP")) {
                        logdebug(" isNull(\"ITCUIMP\") ");
                        param2 = param2 + ", " + getMboValue("ITCUIMP").getColumnTitle();

                        Object[] params = {param2};
                        throw new MXApplicationException("workorder", "itcvalornulo", params);
                    }
                }

            }
        }

        super.appValidate();
        logdebug(LOGEND, "appValidate()");
    }

    /**
     *
     * @throws MXException
     * @throws RemoteException
     */
    @Override
    public void save() throws MXException, RemoteException {
        logdebug(LOGBEGIN, "save()");
        super.save();
        logdebug(LOGEND, "save()");
    }

    /**
     * Metodo que calcula el importe de venta unitario y de linea, el valor del
     * impuesto y los precios de venta unitario y de linea. Invoca el metodo
     * action del campo ITCVVLINEA el cual actualizara el importe total cotizado
     * para materiales
     *
     * @throws MXException
     * @throws RemoteException
     */
    @Override
    public void itcCalcularMargen() throws MXException, RemoteException {
        logdebug(LOGBEGIN, "itcCalcularMargen()");
        double itemqty = getDouble("ITEMQTY");
        double itcpreciolistaprov = getDouble("ITCPULISTAPROV");  //PRECIO PROVEEDOR
        double itclistprice = getDouble("ITCPULISTAOT");  //PRECIO OT
        double itcuimp = getDouble("ITCUIMP");  //costo unitario de importación
        double itcmargen = getDouble("ITCMARGEN");   //margen
        double itcdctocliente = getDouble("ITCDCTOCLIENTE");  //descuento

        logdebug("WPMATERIAL.ITEMQTY: " + Double.toString(itemqty));
        logdebug("WPMATERIAL.ITCPRECIOLISTAPROV: " + Double.toString(itcpreciolistaprov));
        logdebug("WPMATERIAL.ITCPULISTAOT: " + Double.toString(itclistprice));
        logdebug("WPMATERIAL.ITCUIMP: " + Double.toString(itcuimp));
        logdebug("WPMATERIAL.ITCMARGEN: " + Double.toString(itcmargen));
        logdebug("WPMATERIAL.ITCDCTOCLIENTE: " + Double.toString(itcdctocliente));

        double itcvalorventa_sindscto = (itcuimp + itclistprice) * (1D + (itcmargen / 100D));  //importe unitario con margen de ganancia
        double itcvalorventaunitario = itcvalorventa_sindscto * (1D - (itcdctocliente / 100D));  //importe unitario con descuento
        double itcvalorventalinea = itcvalorventaunitario * itemqty;
        double igvrate = 0D;

        MboSetRemote igvTaxSet = getMboSet("$IGVTAX", "TAX", "taxcode=:ITCTAXCODE and orgid=:orgid");  //buscar relación -crear el campo en el wpmaterial

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

        logdebug("WPMATERIAL.ITCVVUNITARIO: " + Double.toString(getDouble("ITCVVUNITARIO")));
        logdebug("WPMATERIAL.ITCVVLINEA: " + Double.toString(getDouble("ITCVVLINEA")));

        logdebug("WPMATERIAL.ITCIGV: " + Double.toString(getDouble("ITCIGV")));
        logdebug("WPMATERIAL.ITCPVLINEA: " + Double.toString(getDouble("ITCPVLINEA")));
        logdebug("WPMATERIAL.ITCPVUNITARIO: " + Double.toString(getDouble("ITCPVUNITARIO")));

        logdebug(LOGEND, "itcCalcularMargen()");
    }

    @Override
    /**
     * Metodo que calcula el Precio unitario de lista en función a la moneda de
     * la Orden de Trabajo. Invoca el metodo action de campo ITCPULISTAOT el
     * cual llama al metodo itcCalcularMargen()
     *
     */
    public void itcCalculaPrecioOT() throws MXException, RemoteException {
        logdebug(LOGBEGIN, "itcCalculaPrecioOT()");

        MboRemote mboOwner = getOwner();    //workorder

        if (!mboOwner.isNull("ITCCURRENCYCODE") || !mboOwner.isNull("ITCFECHACOTIZACION")) {
            logdebug("!mboOwner.isNull(\"ITCCURRENCYCODE\") || !mboOwner.isNull(\"ITCFECHACOTIZACION\")");

            CurrencyServiceRemote curService = (CurrencyServiceRemote) MXServer.getMXServer().lookup("CURRENCY");

            String currencyFrom = getMboValue("ITCCURRENCYCODE").getString();
            String currencyTo = mboOwner.getString("ITCCURRENCYCODE");
            double preciolistaProv = getMboValue("ITCPULISTAPROV").getDouble();
            Date exchangeDate = mboOwner.getDate("ITCFECHACOTIZACION");
            //Date exchangeDate = MXServer.getMXServer().getDate(getClientLocale(), getClientTimeZone());
            //Date exchangeDate = new Date();
            String orgid = mboOwner.getString("ORGID");
            double listPrice;
            double exchangeRate = 0;

            logdebug("currencyFrom", currencyFrom);
            logdebug("currencyTo", currencyTo);
            logdebug("preciolistaProv", Double.toString(preciolistaProv));
            logdebug("exchangeDate", exchangeDate.toString());
            logdebug("orgid", orgid);

            if (currencyFrom != null && !currencyFrom.isEmpty()) {
                logdebug("currencyFrom != null && !currencyFrom.isEmpty()");
                listPrice = curService.calculateCurrencyCost(getUserInfo(), currencyFrom, currencyTo, preciolistaProv, exchangeDate, orgid);

                exchangeRate = curService.getCurrencyExchangeRate(getUserInfo(), currencyFrom, currencyTo, exchangeDate, getString("ORGID"));
                logdebug("exchangeRate", exchangeRate);
            } else {
                logdebug("currencyFrom == null || currencyFrom.isEmpty()");
                listPrice = preciolistaProv;
            }

            logdebug("ITCPULISTAOT", Double.toString(listPrice));
            getMboValue("ITCPULISTAOT").setValue(listPrice, NOACCESSCHECK);
            getMboValue("ITCEXCHANGERATE").setValue(exchangeRate, NOACCESSCHECK | NOVALIDATION_AND_NOACTION);

        }

        logdebug(LOGEND, "itcCalculaPrecioOT()");
    }

    /**
     *
     * @param accessModifier
     * @throws MXException
     * @throws RemoteException
     */
    @Override
    public void delete(long accessModifier) throws MXException, RemoteException {
        logdebug(LOGBEGIN, "delete(long accessModifier)");
        super.delete(accessModifier);

        MboRemote refWo = getOwner();
        logdebug(LOGRUNNING, "this.itcUpdateTotals(refWo, false);");
        this.itcUpdateTotals(refWo, false);
        logdebug(LOGEND, "delete(long accessModifier)");
    }

    /**
     *
     * @throws MXException
     * @throws RemoteException
     */
    @Override
    public void undelete() throws MXException, RemoteException {
        logdebug(LOGBEGIN, "undelete()");
        super.undelete();
        MboRemote refWo = getOwner();

        if (getThisMboSet().count(COUNT_AFTERSAVE) == 0) {
            logdebug("if (getThisMboSet().count(COUNT_AFTERSAVE) == 0)");
            refWo.setValue("ITCTOTALMATERIALES", 0.0D, NOACCESSCHECK);
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
            logdebug(LOGRUNNING, "((ITCWORemote) wo).itcIncrTotalMateriales(itcvvlinea);");
            ((ITCWORemote) wo).itcIncrTotalMateriales(itcvvlinea);
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
