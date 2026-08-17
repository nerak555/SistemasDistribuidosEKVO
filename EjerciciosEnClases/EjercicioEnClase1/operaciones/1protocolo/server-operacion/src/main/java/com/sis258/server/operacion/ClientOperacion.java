package com.sis258.server.operacion;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClientOperacion {
    public static void main(String[] args) {
        String host = "localhost";
        int port = 5002;

        try (Socket socket = new Socket(host, port);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
             Scanner scanner = new Scanner(System.in)) {

            System.out.println("Conectado al servidor de operaciones (1protocolo).");

            // El servidor del 1protocolo pide exactamente 3 entradas paso a paso
            for (int i = 0; i < 3; i++) {
                String mensajeServidor = reader.readLine();
                if (mensajeServidor == null) break;
                System.out.println("Servidor: " + mensajeServidor);
                
                System.out.print("Tu respuesta: ");
                String respuesta = scanner.nextLine();
                writer.println(respuesta);
            }

            // Leer la respuesta final del resultado calculada por el servidor
            String resultadoFinal = reader.readLine();
            if (resultadoFinal != null) {
                System.out.println("Servidor: " + resultadoFinal);
            }

        } catch (Exception e) {
            System.out.println("Error en el cliente: " + e.getMessage());
        }
    }
}