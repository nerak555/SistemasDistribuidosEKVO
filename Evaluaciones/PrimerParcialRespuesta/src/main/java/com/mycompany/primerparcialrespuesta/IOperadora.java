/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.mycompany.primerparcialrespuesta;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author LENOVO
 */
public interface IOperadora extends Remote{
    Voucher ComprarTour(String pasaporte,String codigoTour,int personas) throws RemoteException;
}
