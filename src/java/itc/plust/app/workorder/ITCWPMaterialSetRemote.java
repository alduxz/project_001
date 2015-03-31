package itc.plust.app.workorder;

import java.rmi.RemoteException;
import psdi.plust.app.workorder.PlusTWPMaterialSetRemote;
import psdi.util.MXException;

/**
 *
 * @author ekishimoto
 */
public interface ITCWPMaterialSetRemote extends PlusTWPMaterialSetRemote {

    public abstract void itcCalculaPrecioOT() throws MXException, RemoteException;
}
