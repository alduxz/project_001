/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cust.psdi.app.item;

import java.rmi.RemoteException;
import psdi.mbo.*;
import psdi.util.*;



public class Item extends psdi.plusg.app.item.PlusGItem implements psdi.plusg.app.item.PlusGItemRemote
{
  public Item(MboSet ms) throws MXException, RemoteException
  {
    super(ms);
  }

  @Override
  public void add() throws MXException, RemoteException
  {
    setValue("description", "AJUA");
    super.add();
  }
}