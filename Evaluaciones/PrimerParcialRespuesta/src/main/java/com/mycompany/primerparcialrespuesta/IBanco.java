package com.mycompany.primerparcialrespuesta;

import java.rmi.Remote;
import java.rmi.RemoteException;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */

/**
 *
 * @author LENOVO
 */
public interface IBanco extends Remote {
    public Pago Debitar(String pasaporte, double montoUSD) throws RemoteException;
}
