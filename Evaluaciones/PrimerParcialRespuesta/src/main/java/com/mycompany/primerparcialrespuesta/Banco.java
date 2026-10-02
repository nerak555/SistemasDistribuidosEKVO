/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.primerparcialrespuesta;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 *
 * @author LENOVO
 */
public class Banco extends UnicastRemoteObject implements IBanco  {

    public Banco() throws RemoteException {
        super();
    }

    @Override
    public Pago Debitar(String pasaporte, double montoUSD) throws RemoteException {
        try {
            DatagramSocket socket = new DatagramSocket();
            String mensaje = "riesgo:"+pasaporte+":"+montoUSD;
            byte[] msj = mensaje.getBytes();
            InetAddress direccion = InetAddress.getByName("localhost");
            int puerto = 2000;
            DatagramPacket paquete = new DatagramPacket(msj, msj.length,direccion,puerto);
            socket.send(paquete);
            
            //recibir UDP
            byte[] buffer = new byte[124];
            DatagramPacket respuesta = new DatagramPacket(buffer,buffer.length);
            socket.receive(respuesta);
            
            String riesgo = new String(respuesta.getData(),0,respuesta.getLength());
            
            if (riesgo.equalsIgnoreCase("alto")) {
                return new Pago(false,"no existe","riesgo alto");
            }else{
                if(riesgo.equalsIgnoreCase("bajo")){
                    double saldo = 12000;
                    if(saldo>=montoUSD){
                        return new Pago(true,"AUT-001","saldo suficiente");
                    }else {
                        return new Pago(false,"no existe","saldo insuficiente");
                    }
                }
            }
            
        } catch (SocketException ex) {
            System.out.println("Erro UDP"+ex.getMessage());
        } catch (UnknownHostException ex) {
            System.getLogger(Banco.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        } catch (IOException ex) {
            System.getLogger(Banco.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
        return new Pago(false, "no existe", "error en la operacion");
        
    }
    
}
