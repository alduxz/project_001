package itc.plust.app.workorder.wplabor;

import java.rmi.RemoteException;
import psdi.plust.app.workorder.PlusTWPLaborRemote;
import psdi.util.MXException;

/**
 *
 * @author ekishimoto
 */
public interface ITCWPLaborRemote extends PlusTWPLaborRemote {

    public void itcCalcularMargen() throws MXException, RemoteException;

}
