package com.mycompany.primerparcialrespuesta;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class ServidorBanco {

    public static void main(String[] args) {

        try {
            LocateRegistry.createRegistry(1100);

            Banco banco = new Banco();

            Naming.rebind("rmi://localhost:1100/Banco", banco);

            System.out.println("Servidor Banco listo en puerto 1100");

        } catch (Exception e) {
            System.out.println("Error servidor Banco: " + e.getMessage());
        }
    }
}