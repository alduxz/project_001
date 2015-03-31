package itc.plust.app.workorder.wplabor;

import itc.plust.app.workorder.ITCWORemote;
import java.rmi.RemoteException;
import java.util.Date;
import static psdi.mbo.MboConstants.COUNT_AFTERSAVE;
import static psdi.mbo.MboConstants.NOACCESSCHECK;
import static psdi.mbo.MboConstants.NOVALIDATION_AND_NOACTION;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.mbo.MboSetRemote;
import psdi.plust.app.workorder.PlusTWPLabor;
import psdi.util.MXException;
import psdi.util.MXMath;
import psdi.util.logging.MXLogger;
import psdi.util.logging.MXLoggerFactory;

/**
 *
 * @author TOSHIBA psdi.plust.app.workorder.PlusTWPMaterialSet
 */
public class ITCWPLabor extends PlusTWPLabor implements ITCWPLaborRemote {

    private final MXLogger log = MXLoggerFactory.getLogger("maximo.customization.WORKORDER");
    private final long LOGBEGIN = 0L;
    private final long LOGEND = 1L;
    private final long LOGRUNNING = 2L;

    public ITCWPLabor(MboSet ms) throws MXException, RemoteException {
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
        logdebug(LOGBEGIN, "itcCalcularMargen()");

        double quantity = getDouble("QUANTITY");
        double linecost = getDouble("LINECOST");  //PRECIO DE LINEA
        double itcmargen = getDouble("ITCMARGEN");   //margen de ganancia %

        logdebug("WPLABOR.ITEMQTY: " + Double.toString(quantity));
        logdebug("WPLABOR.LINECOST: " + Double.toString(linecost));
        logdebug("WPLABOR.ITCMARGEN: " + Double.toString(itcmargen));

        double itcvalorventalinea = (linecost) * (1D + (itcmargen / 100D));  //importe unitario con margen de ganancia

        double itcvalorventaunitario = itcvalorventalinea / quantity;

        double igvrate = 0D;

        //MboSetRemote igvTaxSet = getMboSet("$IGVTAX", "TAX", "taxcode='IGV' and orgid=:orgid");  //buscar relación -crear el campo en el wpmaterial
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
        double itctotalunitario = itctotal / quantity;          //precio final de venta unitario

        logdebug("itcigv: " + Double.toString(itcigv));
        logdebug("itctotal: " + Double.toString(itctotal));
        logdebug("itctotalunitario: " + Double.toString(itctotalunitario));

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
            refWo.setValue("ITCTOTALMANOOBRA", 0.0D, NOACCESSCHECK);
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
            logdebug(LOGRUNNING, "((ITCWORemote) wo).itcIncrTotalManoObra(itcvvlinea);");
            ((ITCWORemote) wo).itcIncrTotalManoObra(itcvvlinea);
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
