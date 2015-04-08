package itc.plust.app.workorder;

import java.rmi.RemoteException;
import psdi.app.workorder.WOActivityRemote;
import psdi.util.MXException;

/**
 *
 * @author aescobar
 */
public abstract interface ITCWOActivityRemote extends WOActivityRemote {

    public abstract void itcIncrTotalMateriales(double incrAmount) throws MXException, RemoteException;

    public abstract void itcIncrTotalManoObra(double incrAmount) throws MXException, RemoteException;

    public abstract void itcIncrTotalServicios(double incrAmount) throws MXException, RemoteException;
}
