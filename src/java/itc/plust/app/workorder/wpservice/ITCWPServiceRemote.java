package itc.plust.app.workorder.wpservice;

import java.rmi.RemoteException;
import psdi.plust.app.workorder.PlusTWPServiceRemote;
import psdi.util.MXException;

/**
 *
 * @author ekishimoto
 */
public interface ITCWPServiceRemote extends PlusTWPServiceRemote {

    public void itcCalcularMargen() throws MXException, RemoteException;

    public void itcCalculaPrecioOT() throws MXException, RemoteException;
}
