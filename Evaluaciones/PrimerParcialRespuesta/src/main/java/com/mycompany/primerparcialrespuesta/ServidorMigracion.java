package com.mycompany.primerparcialrespuesta;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServidorMigracion {

    public static void main(String[] args) {

        int puerto = 1000;

        try {

            ServerSocket servidor = new ServerSocket(puerto);

            System.out.println(
                    "Servidor Migracion escuchando en puerto " + puerto
            );

            while (true) {

                // Esperar conexión de Operadora
                Socket cliente = servidor.accept();

                System.out.println("Operadora conectada");

                // Flujos de entrada y salida
                DataInputStream recibir =
                        new DataInputStream(cliente.getInputStream());

                DataOutputStream enviar =
                        new DataOutputStream(cliente.getOutputStream());

                // Recibir mensaje
                String mensaje = recibir.readUTF();

                System.out.println("Recibido: " + mensaje);

                // Separar mensaje
                String[] partes = mensaje.split(":");

                String respuesta = "invalido";

                if (partes.length == 2
                        && partes[0].equalsIgnoreCase("pasaporte")) {

                    String pasaporte = partes[1];

                    // SIMULACIÓN DE MIGRACIÓN
                    if (pasaporte.startsWith("BO")) {

                        respuesta = "valido:BOLIVIA";

                    } else if (pasaporte.startsWith("AR")) {

                        respuesta = "valido:ARGENTINA";

                    } else {

                        respuesta = "invalido";
                    }
                }

                // Enviar respuesta a Operadora
                enviar.writeUTF(respuesta);

                System.out.println("Respuesta: " + respuesta);

                // Cerrar conexión con este cliente
                recibir.close();
                enviar.close();
                cliente.close();
            }

        } catch (IOException e) {

            System.out.println(
                    "Error Servidor Migracion: " + e.getMessage()
            );
        }
    }
}