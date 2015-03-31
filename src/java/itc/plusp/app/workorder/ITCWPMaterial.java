package itc.plusp.app.workorder;

import java.rmi.RemoteException;
import java.util.Date;
import psdi.app.currency.CurrencyServiceRemote;
import static psdi.mbo.MboConstants.NOACCESSCHECK;
import static psdi.mbo.MboConstants.NOVALIDATION_AND_NOACTION;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.mbo.MboSetRemote;
import psdi.mbo.MboValue;
import psdi.mbo.SqlFormat;
import psdi.plusp.app.workorder.PlusPWPMaterial;
import psdi.server.MXServer;
import psdi.util.MXException;
import psdi.util.MXMath;
import psdi.util.logging.MXLogger;
import psdi.util.logging.MXLoggerFactory;

/**
 *
 * @author TOSHIBA psdi.plust.app.workorder.PlusTWPMaterialSet
 */
public class ITCWPMaterial extends PlusPWPMaterial implements ITCWPMaterialRemote {

    private final MXLogger log = MXLoggerFactory.getLogger("maximo.customization.WORKORDER");
    private final long LOGBEGIN = 0L;
    private final long LOGEND = 1L;
    private final long LOGRUNNING = 2L;

    public ITCWPMaterial(MboSet ms) throws MXException, RemoteException {
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
        MboRemote ownerMbo = getOwner();

        if (ownerMbo != null) {
            logdebug("ownerMbo != null");
            if (ownerMbo.isBasedOn("WORKORDER")) {
                logdebug("ownerMbo.isBasedOn(\"WORKORDER\")");

                if (!isNull("ITCTIPOCOMPRA")) {
                    logdebug("!isNull(\"ITCTIPOCOMPRA\")");
                    String tipocompra = getString("ITCTIPOCOMPRA");

                    if (!tipocompra.equalsIgnoreCase("STOCK")) {
                        logdebug("!tipocompra.equalsIgnoreCase(\"STOCK\")");

                        if (!isNull("ITCPROVEEDOR")) {
                            logdebug("!isNull(\"ITCPROVEEDOR\")");
                            MboSetRemote companiesSet = getMboSet("$COMPANIES", "COMPANIES", "parentcompany=:itcproveedor and orgid=:orgid and itctipocompra=:itctipocompra");

                            if (companiesSet != null && !companiesSet.isEmpty()) {
                                logdebug("companiesSet != null && !companiesSet.isEmpty()");
                                companiesSet.setFlag(DISCARDABLE, true);
                                MboRemote companies = companiesSet.getMbo(0);

                                if (companies != null) {
                                    logdebug("companies != null");
                                    String vendor = companies.getString("COMPANY");
                                    logdebug("vendor: " + vendor);
                                    setValue("VENDOR", vendor, NOACCESSCHECK | NOVALIDATION);
                                }
                            }
                        }
                    }

                    if (!tipocompra.equalsIgnoreCase("STOCK")) {
                        logdebug("!tipocompra.equalsIgnoreCase(\"STOCK\")");
                        double valorfobprov = getDouble("ITCVALORFOBPROV");

                        logdebug("valorfobprov: " + Double.toString(valorfobprov));
                        logdebug(LOGRUNNING, "setITCBaseCurrencyUnitCost(valorfobprov)");
                        setITCBaseCurrencyUnitCost(valorfobprov);
                    }
                }
            }
        }
        logdebug(LOGEND, "save()");
    }

    @Override
    public void itcSetValuesFromMarginsPolicies() throws MXException, RemoteException {
        logdebug(LOGBEGIN, "itcSetValuesFromMarginsPolicies()");
        MboSetRemote itemSet = getMboSet("ITEM");

        double itcdctocliente = 0D;
        double itcfactorimp = 0D;
        double itcdctoprov = 0D;
        double itcmargen = 0D;
        int itctiempoimp = 0;

        if (itemSet != null && !itemSet.isEmpty()) {
            logdebug("itemSet != null && !itemSet.isEmpty()");
            itemSet.setFlag(DISCARDABLE, true);
            MboRemote item = itemSet.getMbo(0);

            if (item != null) {
                logdebug("item != null");

                String[] query = new String[3];

                if (!item.isNull("COMMODITYGROUP") && !item.isNull("COMMODITY") && !isNull("ITCTIPOCOMPRA")) {
                    String commoditygroup = item.getString("COMMODITYGROUP");
                    String commodity = item.getString("COMMODITY");
                    String tipocompra = getString("ITCTIPOCOMPRA");
                    SqlFormat sqf0 = new SqlFormat(this, "orgid=:orgid and commoditygroup=:1 and commodity=:2 and itctipocompra=:3 and pluspcustomer is null and assetnum is null");
                    sqf0.setObject(1, "ITCMARGINSPOLICY", "COMMODITYGROUP", commoditygroup);
                    sqf0.setObject(2, "ITCMARGINSPOLICY", "COMMODITY", commodity);
                    sqf0.setObject(3, "ITCMARGINSPOLICY", "ITCTIPOCOMPRA", tipocompra);

                    query[0] = sqf0.format();

                    MboRemote owner = getOwner();

                    if (!owner.isNull("PLUSPCUSTOMER")) {
                        logdebug("!owner.isNull(\"PLUSPCUSTOMER\")");
                        String pluspcustomer = owner.getString("PLUSPCUSTOMER");
                        SqlFormat sqf1 = new SqlFormat(this, "orgid=:orgid and commoditygroup=:1 and commodity=:2 and itctipocompra=:3 and pluspcustomer=:4 and assetnum is null");
                        sqf1.setObject(1, "ITCMARGINSPOLICY", "COMMODITYGROUP", commoditygroup);
                        sqf1.setObject(2, "ITCMARGINSPOLICY", "COMMODITY", commodity);
                        sqf1.setObject(3, "ITCMARGINSPOLICY", "ITCTIPOCOMPRA", tipocompra);
                        sqf1.setObject(4, "ITCMARGINSPOLICY", "PLUSPCUSTOMER", pluspcustomer);

                        query[1] = sqf1.format();

                        if (!owner.isNull("ASSETNUM")) {
                            logdebug("!iowner.sNull(\"ASSETNUM\")");
                            String assetnum = owner.getString("ASSETNUM");
                            SqlFormat sqf2 = new SqlFormat(this, "orgid=:orgid and commoditygroup=:1 and commodity=:2 and itctipocompra=:3 and pluspcustomer=:4 and assetnum=:5");
                            sqf2.setObject(1, "ITCMARGINSPOLICY", "COMMODITYGROUP", commoditygroup);
                            sqf2.setObject(2, "ITCMARGINSPOLICY", "COMMODITY", commodity);
                            sqf2.setObject(3, "ITCMARGINSPOLICY", "ITCTIPOCOMPRA", tipocompra);
                            sqf2.setObject(4, "ITCMARGINSPOLICY", "PLUSPCUSTOMER", pluspcustomer);
                            sqf2.setObject(5, "ITCMARGINSPOLICY", "ASSETNUM", assetnum);

                            query[2] = sqf2.format();
                        } else {
                            logdebug("owner.isNull(\"ASSETNUM\")");
                            query[2] = null;
                        }
                    } else {
                        logdebug("owner.isNull(\"PLUSPCUSTOMER\")");
                        query[1] = null;
                    }

                    logdebug("query[0]: " + query[0]);
                    logdebug("query[1]: " + query[1]);
                    logdebug("query[2]: " + query[2]);

                    for (int i = query.length - 1; i >= 0; i--) {
                        logdebug("for iteration");

                        if (query[i] != null) {
                            MboSetRemote marginspolicySet = getMboSet("$ITCMARGINSPOLICY", "ITCMARGINSPOLICY", query[i]);

                            if (marginspolicySet != null && !marginspolicySet.isEmpty()) {
                                logdebug("marginspolicySet != null && !marginspolicySet.isEmpty()");
                                marginspolicySet.setFlag(DISCARDABLE, true);
                                MboRemote marginspolicy = marginspolicySet.getMbo(0);

                                if (marginspolicy != null) {
                                    logdebug("marginspolicy != null");

                                    itcdctocliente = marginspolicy.getDouble("ITCDCTOCLIENTE");
                                    itcfactorimp = marginspolicy.getDouble("ITCFACTORIMP");
                                    itcdctoprov = marginspolicy.getDouble("ITCDCTOPROV");
                                    itcmargen = marginspolicy.getDouble("ITCMARGEN");
                                    itctiempoimp = marginspolicy.getInt("ITCTIEMPOIMP");

                                    logdebug("itcdctocliente: " + Double.toString(itcdctocliente));
                                    logdebug("itcfactorimp: " + Double.toString(itcfactorimp));
                                    logdebug("itcdctoprov: " + Double.toString(itcdctoprov));
                                    logdebug("itcmargen: " + Double.toString(itcmargen));
                                    logdebug("itctiempoimp: " + Integer.toString(itctiempoimp));
                                }
                                logdebug("break;");
                                break;
                            }
                        }
                    }
                }
            }
        }

        setValue("ITCDCTOCLIENTE", itcdctocliente, NOACCESSCHECK);
        setValue("ITCFACTORIMP", itcfactorimp, NOACCESSCHECK);
        setValue("ITCDCTOPROV", itcdctoprov, NOACCESSCHECK);
        setValue("ITCMARGEN", itcmargen, NOACCESSCHECK);
        setValue("ITCTIEMPOIMP", itctiempoimp, NOACCESSCHECK);

        logdebug("WPMATERIAL.ITCDCTOCLIENTE: " + Double.toString(getDouble("ITCDCTOCLIENTE")));
        logdebug("WPMATERIAL.ITCFACTORIMP: " + Double.toString(getDouble("ITCFACTORIMP")));
        logdebug("WPMATERIAL.ITCDCTOPROV: " + Double.toString(getDouble("ITCDCTOPROV")));
        logdebug("WPMATERIAL.ITCMARGEN: " + Double.toString(getDouble("ITCMARGEN")));
        logdebug("WPMATERIAL.ITCTIEMPOIMP: " + Integer.toString(getInt("ITCTIEMPOIMP")));
        logdebug(LOGEND, "itcSetValuesFromMarginsPolicies()");
    }

    @Override
    public void itcCalcularMargen() throws MXException, RemoteException {
        logdebug(LOGBEGIN, "itcCalcularMargen()");
        double itemqty = getDouble("ITEMQTY");
        double itcpreciolistaprov = getDouble("ITCPRECIOLISTAPROV");
        double itclistprice = getDouble("ITCLISTPRICE");
        double itcdctoprov = getDouble("ITCDCTOPROV");
        double itcfactorimp = getDouble("ITCFACTORIMP");
        double itcmargen = getDouble("ITCMARGEN");
        double itcdctocliente = getDouble("ITCDCTOCLIENTE");

        logdebug("WPMATERIAL.ITEMQTY: " + Double.toString(itemqty));
        logdebug("WPMATERIAL.ITCPRECIOLISTAPROV: " + Double.toString(itcpreciolistaprov));
        logdebug("WPMATERIAL.ITCLISTPRICE: " + Double.toString(itclistprice));
        logdebug("WPMATERIAL.ITCDCTOPROV: " + Double.toString(itcdctoprov));
        logdebug("WPMATERIAL.ITCFACTORIMP: " + Double.toString(itcfactorimp));
        logdebug("WPMATERIAL.ITCMARGEN: " + Double.toString(itcmargen));
        logdebug("WPMATERIAL.ITCDCTOCLIENTE: " + Double.toString(itcdctocliente));

        double itcvalorfobprov = itcpreciolistaprov * (1D - (itcdctoprov / 100D));
        double itcvalorfob = itclistprice * (1D - (itcdctoprov / 100D));
        double itcvaloralm = itcvalorfob * itcfactorimp;
        double itcvalorventa = itcvaloralm * (1D + (itcmargen / 100D));
        double itcpreciounitario = itcvalorventa * (1D - (itcdctocliente / 100D));
        double itcprecioventa = itcpreciounitario * itemqty;

        double igvrate = 0D;
        MboSetRemote igvTaxSet = getMboSet("$IGVTAX", "TAX", "taxcode='IGV' and orgid=:orgid");

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

        double itcigv = itcprecioventa * igvrate / 100D;
        double itctotal = itcprecioventa + itcigv;

        logdebug("itcvalorfobprov: " + Double.toString(itcvalorfobprov));
        logdebug("itcvalorfob: " + Double.toString(itcvalorfob));
        logdebug("itcvaloralm: " + Double.toString(itcvaloralm));
        logdebug("itcvalorventa: " + Double.toString(itcvalorventa));
        logdebug("itcpreciounitario: " + Double.toString(itcpreciounitario));
        logdebug("itcprecioventa: " + Double.toString(itcprecioventa));
        logdebug("itcigv: " + Double.toString(itcigv));
        logdebug("itctotal: " + Double.toString(itctotal));

        setValue("ITCVALORFOBPROV", itcvalorfobprov, NOACCESSCHECK | NOVALIDATION_AND_NOACTION);
        setValue("ITCVALORFOB", itcvalorfob, NOACCESSCHECK | NOVALIDATION_AND_NOACTION);
        setValue("ITCVALORALM", itcvaloralm, NOACCESSCHECK | NOVALIDATION_AND_NOACTION);
        setValue("ITCVALORVENTA", itcvalorventa, NOACCESSCHECK | NOVALIDATION_AND_NOACTION);
        setValue("ITCPRECIOUNITARIO", itcpreciounitario, NOACCESSCHECK | NOVALIDATION_AND_NOACTION);
        setValue("ITCPRECIOVENTA", itcprecioventa, NOACCESSCHECK | NOVALIDATION_AND_NOACTION);
        setValue("ITCIGV", itcigv, NOACCESSCHECK | NOVALIDATION_AND_NOACTION);
        setValue("ITCTOTAL", itctotal, NOACCESSCHECK | NOVALIDATION_AND_NOACTION);

        logdebug("WPMATERIAL.ITCVALORFOBPROV: " + Double.toString(getDouble("ITCVALORFOBPROV")));
        logdebug("WPMATERIAL.ITCVALORFOB: " + Double.toString(getDouble("ITCVALORFOB")));
        logdebug("WPMATERIAL.ITCVALORALM: " + Double.toString(getDouble("ITCVALORALM")));
        logdebug("WPMATERIAL.ITCVALORVENTA: " + Double.toString(getDouble("ITCVALORVENTA")));
        logdebug("WPMATERIAL.ITCPRECIOUNITARIO: " + Double.toString(getDouble("ITCPRECIOUNITARIO")));
        logdebug("WPMATERIAL.ITCPRECIOVENTA: " + Double.toString(getDouble("ITCPRECIOVENTA")));
        logdebug("WPMATERIAL.ITCIGV: " + Double.toString(getDouble("ITCIGV")));
        logdebug("WPMATERIAL.ITCTOTAL: " + Double.toString(getDouble("ITCTOTAL")));

        logdebug(LOGRUNNING, "setBaseCurrencyUnitCost(itcvalorfobprov)");
        setBaseCurrencyUnitCost(itcvalorfobprov);
        logdebug(LOGRUNNING, "itcSetTaxesAndTotal()");
        this.itcSetTaxesAndTotal();

        logdebug(LOGEND, "itcCalcularMargen()");
    }

    @Override
    public void itcCalcularMargen(MboValue mbovalue) throws MXException, RemoteException {
        logdebug(LOGBEGIN, "itcCalcularMargen(MboValue mbovalue)");
        String mbovalueName = mbovalue.getName();
        logdebug("mbovalueName: " + mbovalueName);
        double itemqty = getDouble("ITEMQTY");
        double itcpreciolistaprov = getDouble("ITCPRECIOLISTAPROV");
        double itclistprice = getDouble("ITCLISTPRICE");
        double itcdctoprov = getDouble("ITCDCTOPROV");
        double itcfactorimp = getDouble("ITCFACTORIMP");
        double itcmargen = getDouble("ITCMARGEN");
        double itcdctocliente = getDouble("ITCDCTOCLIENTE");

        logdebug("WPMATERIAL.ITEMQTY: " + Double.toString(itemqty));
        logdebug("WPMATERIAL.ITCPRECIOLISTAPROV: " + Double.toString(itcpreciolistaprov));
        logdebug("WPMATERIAL.ITCLISTPRICE: " + Double.toString(itclistprice));
        logdebug("WPMATERIAL.ITCDCTOPROV: " + Double.toString(itcdctoprov));
        logdebug("WPMATERIAL.ITCFACTORIMP: " + Double.toString(itcfactorimp));
        logdebug("WPMATERIAL.ITCMARGEN: " + Double.toString(itcmargen));
        logdebug("WPMATERIAL.ITCDCTOCLIENTE: " + Double.toString(itcdctocliente));

        if (mbovalueName.equalsIgnoreCase("ITCLISTPRICE")) {
            logdebug("mbovalueName.equalsIgnoreCase(\"ITCLISTPRICE\")");
            itclistprice = mbovalue.getDouble();
            logdebug("itclistprice: " + Double.toString(itclistprice));
        } else if (mbovalueName.equalsIgnoreCase("ITCDCTOPROV")) {
            logdebug("mbovalueName.equalsIgnoreCase(\"ITCDCTOPROV\")");
            itcdctoprov = mbovalue.getDouble();
            logdebug("itcdctoprov: " + Double.toString(itcdctoprov));
        } else if (mbovalueName.equalsIgnoreCase("ITCFACTORIMP")) {
            logdebug("mbovalueName.equalsIgnoreCase(\"ITCFACTORIMP\")");
            itcfactorimp = mbovalue.getDouble();
            logdebug("itcfactorimp: " + Double.toString(itcfactorimp));
        } else if (mbovalueName.equalsIgnoreCase("ITCMARGEN")) {
            logdebug("mbovalueName.equalsIgnoreCase(\"ITCMARGEN\")");
            itcmargen = mbovalue.getDouble();
            logdebug("itcmargen: " + Double.toString(itcmargen));
        } else if (mbovalueName.equalsIgnoreCase("ITCDCTOCLIENTE")) {
            logdebug("mbovalueName.equalsIgnoreCase(\"ITCDCTOCLIENTE\")");
            itcdctocliente = mbovalue.getDouble();
            logdebug("itcdctocliente: " + Double.toString(itcdctocliente));
        } else if (mbovalueName.equalsIgnoreCase("ITEMQTY")) {
            logdebug("mbovalueName.equalsIgnoreCase(\"ITEMQTY\")");
            itemqty = mbovalue.getDouble();
            logdebug("itemqty: " + Double.toString(itemqty));
        }

        double itcvalorfobprov = itcpreciolistaprov * (1D - (itcdctoprov / 100D));
        double itcvalorfob = itclistprice * (1D - (itcdctoprov / 100D));
        double itcvaloralm = itcvalorfob * itcfactorimp;
        double itcvalorventa = itcvaloralm * (1D + (itcmargen / 100D));
        double itcpreciounitario = itcvalorventa * (1D - (itcdctocliente / 100D));
        double itcprecioventa = itcpreciounitario * itemqty;

        double igvrate = 0.0D;
        MboSetRemote igvTaxSet = getMboSet("$IGVTAX", "TAX", "taxcode='IGV' and orgid=:orgid");

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

        double itcigv = itcprecioventa * igvrate / 100D;
        double itctotal = itcprecioventa + itcigv;

        logdebug("itcvalorfobprov: " + Double.toString(itcvalorfobprov));
        logdebug("itcvalorfob: " + Double.toString(itcvalorfob));
        logdebug("itcvaloralm: " + Double.toString(itcvaloralm));
        logdebug("itcvalorventa: " + Double.toString(itcvalorventa));
        logdebug("itcpreciounitario: " + Double.toString(itcpreciounitario));
        logdebug("itcprecioventa: " + Double.toString(itcprecioventa));
        logdebug("itcigv: " + Double.toString(itcigv));
        logdebug("itctotal: " + Double.toString(itctotal));

        setValue("ITCVALORFOBPROV", itcvalorfobprov, NOACCESSCHECK | NOVALIDATION_AND_NOACTION);
        setValue("ITCVALORFOB", itcvalorfob, NOACCESSCHECK | NOVALIDATION_AND_NOACTION);
        setValue("ITCVALORALM", itcvaloralm, NOACCESSCHECK | NOVALIDATION_AND_NOACTION);
        setValue("ITCVALORVENTA", itcvalorventa, NOACCESSCHECK | NOVALIDATION_AND_NOACTION);
        setValue("ITCPRECIOUNITARIO", itcpreciounitario, NOACCESSCHECK | NOVALIDATION_AND_NOACTION);
        setValue("ITCPRECIOVENTA", itcprecioventa, NOACCESSCHECK | NOVALIDATION_AND_NOACTION);
        setValue("ITCIGV", itcigv, NOACCESSCHECK | NOVALIDATION_AND_NOACTION);
        setValue("ITCTOTAL", itctotal, NOACCESSCHECK | NOVALIDATION_AND_NOACTION);

        logdebug("WPMATERIAL.ITCVALORFOBPROV: " + Double.toString(getDouble("ITCVALORFOBPROV")));
        logdebug("WPMATERIAL.ITCVALORFOB: " + Double.toString(getDouble("ITCVALORFOB")));
        logdebug("WPMATERIAL.ITCVALORALM: " + Double.toString(getDouble("ITCVALORALM")));
        logdebug("WPMATERIAL.ITCVALORVENTA: " + Double.toString(getDouble("ITCVALORVENTA")));
        logdebug("WPMATERIAL.ITCPRECIOUNITARIO: " + Double.toString(getDouble("ITCPRECIOUNITARIO")));
        logdebug("WPMATERIAL.ITCPRECIOVENTA: " + Double.toString(getDouble("ITCPRECIOVENTA")));
        logdebug("WPMATERIAL.ITCIGV: " + Double.toString(getDouble("ITCIGV")));
        logdebug("WPMATERIAL.ITCTOTAL: " + Double.toString(getDouble("ITCTOTAL")));

        logdebug(LOGRUNNING, "setBaseCurrencyUnitCost(itcvalorfobprov)");
        setBaseCurrencyUnitCost(itcvalorfobprov);
        logdebug(LOGRUNNING, "itcSetTaxesAndTotal()");
        this.itcSetTaxesAndTotal();

        logdebug(LOGEND, "itcCalcularMargen(MboValue mbovalue)");
    }

    private void itcSetTaxesAndTotal() throws MXException, RemoteException {
        logdebug(LOGBEGIN, "itcSetTaxesAndTotal()");
        MboRemote workorder = getOwner();
        MboSetRemote itemSet = getMboSet("ITEM");

        if (itemSet != null && !itemSet.isEmpty()) {
            logdebug("itemSet != null && !itemSet.isEmpty()");
            itemSet.setFlag(DISCARDABLE, true);
            MboRemote item = itemSet.getMbo(0);

            if (item != null) {
                logdebug("item != null");
                MboSetRemote pluspcustSet = workorder.getMboSet("PLUSPCUSTOMER");

                if (pluspcustSet != null && !pluspcustSet.isEmpty()) {
                    logdebug("pluspcustSet != null && !pluspcustSet.isEmpty()");
                    pluspcustSet.setFlag(DISCARDABLE, true);
                    MboRemote pluspcust = pluspcustSet.getMbo(0);

                    if (pluspcust != null) {
                        logdebug("pluspcust != null");

                        String commoditygroup = item.getString("COMMODITYGROUP");
                        String commodity = item.getString("COMMODITY");
                        boolean isrotating = item.getBoolean("ROTATING");
                        boolean ispercepcion = pluspcust.getBoolean("ITCPERCEPCION");
                        boolean isretencion = pluspcust.getBoolean("ITCRETENCION");

                        logdebug("ITEM.COMMODITYGROUP: " + commoditygroup);
                        logdebug("ITEM.COMMODITY: " + commodity);
                        logdebug("PLUSPCUSTOMER.ITCPERCEPCION: " + Boolean.toString(ispercepcion));
                        logdebug("PLUSPCUSTOMER.ITCRETENCION: " + Boolean.toString(isretencion));

                        if (isrotating) {
                            logdebug("isrotating is true");
                            setValue("ITCIMPUESTO", "IGV", NOACCESSCHECK | NOVALIDATION_AND_NOACTION);
                        } else if (isretencion) {
                            logdebug("itcretencion is true");
                            setValue("ITCIMPUESTO", "IGV", NOACCESSCHECK | NOVALIDATION_AND_NOACTION);
                        } else {
                            logdebug("isrotating is false && itcretencion is false");
                            MboSetRemote commoditiesSet = item.getMboSet("$COMMODITYLUB", "COMMODITIES", "commodity=:commodity and parent=:commoditygroup and itemsetid=:itemsetid and itcislubricant=1");

                            if (commoditiesSet != null && !commoditiesSet.isEmpty()) {
                                logdebug("commoditiesSet != null && !commoditiesSet.isEmpty()");
                                commoditiesSet.setFlag(DISCARDABLE, true);
                                setValue("ITCIMPUESTO", "IGV", NOACCESSCHECK | NOVALIDATION_AND_NOACTION);
                            } else {
                                logdebug("commoditiesSet == null || commoditiesSet.isEmpty()");
                                if (ispercepcion) {
                                    logdebug("itcpercepcion is true");
                                    setValue("ITCIMPUESTO", "IGV5", NOACCESSCHECK | NOVALIDATION_AND_NOACTION);
                                } else {
                                    logdebug("itcpercepcion is false");
                                    setValue("ITCIMPUESTO", "IGV2", NOACCESSCHECK | NOVALIDATION_AND_NOACTION);
                                }
                            }
                        }
                        logdebug("WPMATERIAL.ITCIMPUESTO: " + getString("ITCIMPUESTO"));
                    }

                    double itcppercepcion = 0.0D;

                    if (!isNull("ITCIMPUESTO")) {
                        logdebug("!isNull(\"ITCIMPUESTO\")");
                        String itcimpuesto = getString("ITCIMPUESTO");

                        if (!itcimpuesto.equalsIgnoreCase("IGV") && !itcimpuesto.equalsIgnoreCase("IGV_EXE")) {
                            MboSetRemote taxSet = getMboSet("$ITCIMPUESTO", "TAX", "taxcode=:itcimpuesto and orgid=:orgid");

                            if (taxSet != null && !taxSet.isEmpty()) {
                                logdebug("taxSet != null && !taxSet.isEmpty()");
                                taxSet.setFlag(DISCARDABLE, true);
                                MboRemote tax = taxSet.getMbo(0);

                                if (tax != null) {
                                    logdebug("tax != null");
                                    itcppercepcion = tax.getDouble("TAXRATE");
                                }
                            }
                        }
                    }
                    setValue("ITCPPERCEPCION", itcppercepcion, NOACCESSCHECK | NOVALIDATION_AND_NOACTION);

                    double itctotal = getDouble("ITCTOTAL");
                    double itcpercepcion = itctotal * itcppercepcion / 100D;
                    double itctotalincper = itcpercepcion + itctotal;
                    double itemqty = getDouble("ITEMQTY");
                    double itcunitprice = 0.0D;

                    if (itemqty != 0.0D) {
                        logdebug("itemqty != 0.0D");
                        itcunitprice = itctotalincper / itemqty;
                    }
                    if (itcunitprice == 0.0D) {
                        logdebug("itcunitprice == 0.0D");
                        itcunitprice = getDouble("UNITCOST");
                    }

                    logdebug("WPMATERIAL.ITCUNITPRICE: " + Double.toString(itcunitprice));
                    logdebug("WPMATERIAL.ITCPERCEPCION: " + Double.toString(itcpercepcion));
                    logdebug("WPMATERIAL.ITCLINEPRICE: " + Double.toString(itctotalincper));

                    setValue("ITCUNITPRICE", itcunitprice, NOACCESSCHECK | NOVALIDATION_AND_NOACTION);
                    setValue("ITCPERCEPCION", itcpercepcion, NOACCESSCHECK | NOVALIDATION_AND_NOACTION);
                    setValue("ITCTOTALINCPER", itctotalincper, NOACCESSCHECK | NOVALIDATION_AND_NOACTION);
                    setValue("ITCLINEPRICE", itctotalincper, NOACCESSCHECK | NOVALIDATION_AND_NOACTION);
                    setValue("PLUSPLINEPRICE", itctotalincper, NOACCESSCHECK | NOVALIDATION_AND_NOACTION);

                    logdebug("WPMATERIAL.ITCUNITPRICE: " + Double.toString(getDouble("ITCUNITPRICE")));
                    logdebug("WPMATERIAL.ITCPERCEPCION: " + Double.toString(getDouble("ITCPERCEPCION")));
                    logdebug("WPMATERIAL.ITCTOTALINCPER: " + Double.toString(getDouble("ITCTOTALINCPER")));
                    logdebug("WPMATERIAL.ITCLINEPRICE: " + Double.toString(getDouble("ITCLINEPRICE")));
                    logdebug("WPMATERIAL.PLUSPLINEPRICE: " + Double.toString(getDouble("PLUSPLINEPRICE")));
                }
            }
        }
        logdebug(LOGEND, "itcSetTaxesAndTotal()");
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
            refWo.setValue("ITCTOTALMATERIALES", 0.0D, NOACCESSCHECK);
        }

        logdebug(LOGRUNNING, "this.itcUpdateTotals(refWo, true);");
        this.itcUpdateTotals(refWo, true);
        logdebug(LOGEND, "undelete()");
    }

    private void itcUpdateTotals(MboRemote wo, boolean isGain) throws MXException, RemoteException {
        if ((toBeUpdated()) || (toBeDeleted())) {
            double itcvvlinea = this.getDouble("ITCVVLINEA");

            if (MXMath.compareTo(itcvvlinea, 0.0D) != 0) {
                if (!isGain) {
                    itcvvlinea *= -1.0D ;
                }
                ((ITCWORemote) wo).itcIncrTotalMateriales(itcvvlinea);
            }
        }
    }

    @Override
    public void setITCBaseCurrencyUnitCost(double vendorunitcost) throws MXException, RemoteException {
        logdebug(LOGBEGIN, "setITCBaseCurrencyUnitCost(vendorunitcost)");
        logdebug("vendorunitcost: " + Double.toString(vendorunitcost));
        double unitCost = 0.0D;
        Date dateToday = MXServer.getMXServer().getDate(getClientLocale(), getClientTimeZone());
        CurrencyServiceRemote currService = (CurrencyServiceRemote) MXServer.getMXServer().lookup("CURRENCY");

        if (!getMboValue("vendor").isNull()) {
            logdebug("!getMboValue(\"vendor\").isNull()");

            if (!getMboValue("itcproveedor").isNull()) {
                logdebug("!getMboValue(\"itcproveedor\").isNull()");
                String currencyCode = getMboSet("ITCCOMPANY").getMbo(0).getString("CURRENCYCODE");
                String baseCurrency = currService.getBaseCurrency1(getString("ORGID"), getUserInfo());

                if (baseCurrency != null && !baseCurrency.equals("")) {
                    logdebug("baseCurrency != null && !baseCurrency.equals(\"\")");
                    double exchangeRate = currService.getCurrencyExchangeRate(getUserInfo(), currencyCode, baseCurrency, dateToday, getString("ORGID"));
                    logdebug("exchangeRate: " + Double.toString(exchangeRate));
                    unitCost = exchangeRate * vendorunitcost;
                }
                logdebug("unitCost: " + Double.toString(unitCost));
                setValue("UNITCOST", unitCost, NOACCESSCHECK);
                logdebug("WPMATERIAL.UNITCOST: " + Double.toString(getDouble("UNITCOST")));
            }
        }
        logdebug(LOGEND, "setITCBaseCurrencyUnitCost(vendorunitcost)");
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
