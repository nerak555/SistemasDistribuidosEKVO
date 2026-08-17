/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.sis258.server.operacion;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author Dell
 */
public class ServerOperacion {
//2Interactivo
    public static void main(String[] args) {
         int port = 5002;
        ServerSocket server = null;
        try {
            server = new ServerSocket(port);
            System.out.println("Se inicio el servidor interactivo con éxito en el puerto " + port);
            
            while (true) {
                Socket client = server.accept(); //conexion entre cliente y servidor para comunicacion bidireccional
                System.out.println("Cliente se conecto");
                
                BufferedReader fromClient = new BufferedReader(new InputStreamReader(client.getInputStream())); // el lector
                PrintStream toClient = new PrintStream(client.getOutputStream());
                
                // Mensaje de bienvenida para el cliente interactivo
                toClient.println("CONECTADO AL SERVIDOR INTERACTIVO. Formato: operacion numero1 numero2 (Ej: suma 10 5) o 'salir'");
                
                String recibido;
                // Bucle para mantener la sesión interactiva con el mismo cliente
                while ((recibido = fromClient.readLine()) != null) {
                    System.out.println("El cliente envio el mensaje: " + recibido);
                    
                    if (recibido.trim().equalsIgnoreCase("salir")) {
                        toClient.println("Conexion cerrada. Adios!");
                        break;
                    }
                    
                    String respuesta = procesarSolicitud(recibido);
                    toClient.println(respuesta);
                }
                
                client.close();
                System.out.println("Cliente desconectado. Esperando nuevo cliente...\n");
            }

        } catch (IOException ex) {
            System.out.print(ex.getMessage());
        } finally {
            if (server != null) {
                try {
                    server.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String procesarSolicitud(String cadena) {
        try {
            String[] partes = cadena.trim().split("\\s+");
            if (partes.length < 3) {
                return "Formato incorrecto. Use: [operacion] [num1] [num2] (Ej: suma 10 5)";
            }

            String operacion = partes[0].toLowerCase();
            double numero1 = Double.parseDouble(partes[1]);
            double numero2 = Double.parseDouble(partes[2]);
            double resultado = 0;

            switch (operacion) {
                case "suma":
                case "1":
                    resultado = numero1 + numero2;
                    break;
                case "resta":
                case "restar":
                case "2":
                    resultado = numero1 - numero2;
                    break;
                case "multiplicacion":
                case "3":
                    resultado = numero1 * numero2;
                    break;
                case "division":
                case "4":
                    if (numero2 != 0) {
                        resultado = numero1 / numero2;
                    } else {
                        return "Error: Division entre cero.";
                    }
                    break;
                default:
                    return "Operacion no valida. Use: suma, restar, multiplicacion, division.";
            }

            return "Resultado: " + resultado;
        } catch (Exception e) {
            return "Error al procesar los datos. Verifique los numeros ingresados.";
        }
    }
}