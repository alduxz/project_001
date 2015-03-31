package itc.plust.app.workorder;

import java.rmi.RemoteException;
import psdi.plust.app.workorder.PlusTWPMaterialRemote;
import psdi.util.MXException;

/**
 *
 * @author ekishimoto
 */
public interface ITCWPMaterialRemote extends PlusTWPMaterialRemote {

    public void itcCalcularMargen() throws MXException, RemoteException;

    public void itcCalculaPrecioOT() throws MXException, RemoteException;
}
