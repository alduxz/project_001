package cust.psdi.app.item;

import java.rmi.RemoteException;
import psdi.mbo.*;
import psdi.util.*;


public class ItemSet extends psdi.plusg.app.item.PlusGItemSet implements psdi.plusg.app.item.PlusGItemSetRemote
{
  public ItemSet(MboServerInterface ms) throws MXException, RemoteException
  {
    super(ms);
  }

  @Override
  protected Mbo getMboInstance(MboSet ms) throws MXException, RemoteException
  {
    return new Item(ms);
  }
}