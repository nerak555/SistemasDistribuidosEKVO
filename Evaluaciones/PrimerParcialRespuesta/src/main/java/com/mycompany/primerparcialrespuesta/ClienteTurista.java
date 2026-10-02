package com.mycompany.primerparcialrespuesta;

import java.rmi.Naming;
import java.util.Scanner;

public class ClienteTurista {

    public static void main(String[] args) {

        try {
            // Buscar el servicio remoto de Operadora
            IOperadora operadora =
                    (IOperadora) Naming.lookup(
                            "rmi://localhost:1099/Operadora"
                    );

            Scanner scanner = new Scanner(System.in);

            System.out.print("Ingrese pasaporte: ");
            String pasaporte = scanner.nextLine();

            System.out.print("Ingrese codigo de tour: ");
            String codigoTour = scanner.nextLine();

            System.out.print("Ingrese cantidad de personas: ");
            int personas = scanner.nextInt();

            // Llamada remota
            Voucher voucher =
                    operadora.ComprarTour(
                            pasaporte,
                            codigoTour,
                            personas
                    );

            System.out.println("\n--- RESULTADO ---");
            System.out.println(voucher);

        } catch (Exception e) {
            System.out.println(
                    "Error Cliente Turista: " + e.getMessage()
            );
        }
    }
}