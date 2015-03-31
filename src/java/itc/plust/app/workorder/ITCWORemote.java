package itc.plust.app.workorder;

import java.rmi.RemoteException;
import psdi.plust.app.workorder.PlusTWORemote;
import psdi.util.MXException;

/**
 *
 * @author ekishimoto
 */
public abstract interface ITCWORemote extends PlusTWORemote {

    public abstract void itcIncrTotalMateriales(double incrAmount) throws MXException, RemoteException;

    public abstract void itcIncrTotalManoObra(double incrAmount) throws MXException, RemoteException;

    public abstract void itcIncrTotalServicios(double incrAmount) throws MXException, RemoteException;
}
