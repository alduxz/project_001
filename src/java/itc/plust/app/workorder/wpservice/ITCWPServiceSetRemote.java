package itc.plust.app.workorder.wpservice;

import java.rmi.RemoteException;
import psdi.plust.app.workorder.PlusTWPServiceSetRemote;
import psdi.util.MXException;

/**
 *
 * @author ekishimoto
 */
public interface ITCWPServiceSetRemote extends PlusTWPServiceSetRemote {

    public abstract void itcCalculaPrecioOT() throws MXException, RemoteException;
}
