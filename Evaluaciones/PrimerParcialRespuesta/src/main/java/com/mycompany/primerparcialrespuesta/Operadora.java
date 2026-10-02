/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.primerparcialrespuesta;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 *
 * @author LENOVO
 */
public class Operadora extends UnicastRemoteObject implements IOperadora{

    public Operadora() throws RemoteException {
        super();
    }
    

    @Override
    public Voucher ComprarTour(String pasaporte, String codigoTour, int personas) throws RemoteException {
        // consultar migracion tcp
        int puerto =1000;
        try {
            
        Socket socket = new Socket("localhost",puerto);
        DataInputStream recibir = new DataInputStream(socket.getInputStream());
        DataOutputStream enviar = new DataOutputStream(socket.getOutputStream());
        enviar.writeUTF("pasaporte:"+pasaporte);
        String isValido = recibir.readUTF();
        String[] partes = isValido.split(":");
        double montoUSD;
        // ver pasaporte valido
            if (partes[0].equalsIgnoreCase("valido")) {
                String pais = partes[1];
                // valido calcular el monto
                montoUSD = 180 *personas;
                
                // aplicar descuenta si corresponde

                if(pais.equalsIgnoreCase("Bolivia")){
                    // aplicar descuenta si corresponde
                    montoUSD = montoUSD * 0.5;
                }
                
                // consultar banco por RMI
                IBanco banco = (IBanco) Naming.lookup("rmi://localhost:1100/Banco");
                Pago pago = banco.Debitar(pasaporte, montoUSD);
                if (pago.isAprobado()) {
                   return new Voucher(true, "C-0001", "compra confirmada", montoUSD);
                } else {
                    return new Voucher(false, "no existe compra", pago.getMotivo(), montoUSD);
                }
                
            }
            // si es invalido voucher rechazado
            if(isValido.equalsIgnoreCase("invalido")){
             return new Voucher(false,"no existe compra","pasaporte invalido",0);
            }
        
        
        // Recibir un pago
        // Segun pago devolver voucher aprobado o rechazado

        
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (NotBoundException e) {
            System.out.println("Banco no encontrado: " + e.getMessage());
        }
        return new Voucher(false, "no existe compra", "error en la operacion", 0);
        
    }
    
}
