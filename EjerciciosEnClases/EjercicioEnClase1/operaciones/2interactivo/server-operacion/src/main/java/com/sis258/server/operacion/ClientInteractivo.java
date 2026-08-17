package com.sis258.server.operacion;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClientInteractivo {
    public static void main(String[] args) {
        // IP de Tailscale de la máquina que está corriendo el servidor
        //100.70.39.66
        String host = "localhost"; 
        int port = 5002;

        try (Socket socket = new Socket(host, port);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
             Scanner scanner = new Scanner(System.in)) {

            System.out.println("Conectado al servidor interactivo.");

            // Leer el mensaje de bienvenida inicial del servidor
            String bienvenida = reader.readLine();
            if (bienvenida != null) {
                System.out.println("Servidor: " + bienvenida);
            }

            // Bucle interactivo para enviar comandos al servidor
            while (true) {
                System.out.print("Escribe tu comando (ej: suma 10 5 o 'salir'): ");
                String comando = scanner.nextLine();
                
                writer.println(comando); // Enviar comando al servidor
                
                String respuesta = reader.readLine();
                if (respuesta == null) break;
                System.out.println("Servidor: " + respuesta);

                // Si el usuario escribe salir, cerramos el bucle del cliente
                if (comando.trim().equalsIgnoreCase("salir")) {
                    break;
                }
            }

        } catch (Exception e) {
            System.out.println("Error en el cliente: " + e.getMessage());
        }
    }
}