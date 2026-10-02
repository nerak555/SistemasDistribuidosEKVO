package com.mycompany.primerparcialrespuesta;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class ServidorOperadora {

    public static void main(String[] args) {

        try {
            LocateRegistry.createRegistry(1099);

            Operadora operadora = new Operadora();

            Naming.rebind(
                    "rmi://localhost:1099/Operadora",
                    operadora
            );

            System.out.println(
                    "Servidor Operadora listo en puerto 1099"
            );

        } catch (Exception e) {
            System.out.println(
                    "Error Servidor Operadora: " + e.getMessage()
            );
        }
    }
}