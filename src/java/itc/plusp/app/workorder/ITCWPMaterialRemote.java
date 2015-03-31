package itc.plusp.app.workorder;

import java.rmi.RemoteException;
import psdi.mbo.MboValue;
import psdi.plusp.app.workorder.PlusPWPMaterialRemote;
import psdi.util.MXException;

/**
 *
 * @author ekishimoto
 */
public interface ITCWPMaterialRemote extends PlusPWPMaterialRemote {

  public void itcSetValuesFromMarginsPolicies() throws MXException, RemoteException;

  public void itcCalcularMargen() throws MXException, RemoteException;

  public void itcCalcularMargen(MboValue mbovalue) throws MXException, RemoteException;

  public void setITCBaseCurrencyUnitCost(double vendorunitcost) throws MXException, RemoteException;
}
