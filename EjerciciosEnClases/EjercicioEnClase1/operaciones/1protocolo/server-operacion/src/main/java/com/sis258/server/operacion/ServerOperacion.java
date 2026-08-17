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
//1 protocolo
    public static void main(String[] args) {
        int port = 5002;
        ServerSocket server = null;
        try {
            server = new ServerSocket(port);
            System.out.println("Se inicio el servidor con éxito en el puerto " + port);
            
            while (true) {
                Socket client = server.accept(); //conexion entre cliente y servidor para comunicacion bidireccional
                System.out.println("Cliente se conecto");
                
                BufferedReader fromClient = new BufferedReader(new InputStreamReader(client.getInputStream())); // el lector
                PrintStream toClient = new PrintStream(client.getOutputStream());
                
                // Paso 1: Pedir y leer el primer número
                toClient.println("Introduzca el primer numero:");
                String recibido1 = fromClient.readLine();
                if (recibido1 == null) continue;
                System.out.println("El cliente envio el primer numero: " + recibido1);
                
                // Paso 2: Pedir y leer el segundo número
                toClient.println("Introduzca el segundo numero:");
                String recibido2 = fromClient.readLine();
                if (recibido2 == null) continue;
                System.out.println("El cliente envio el segundo numero: " + recibido2);
                
                // Paso 3: Pedir y leer la operación
                toClient.println("1. suma | 2. resta | 3. multiplicacion | 4. division. Introduzca la operacion:");
                String recibido3 = fromClient.readLine();
                if (recibido3 == null) continue;
                System.out.println("El cliente envio la operacion: " + recibido3);
                
                // Construimos una cadena con los datos para procesarlos en el método del ingeniero
                String paqueteDatos = recibido1 + "," + recibido2 + "," + recibido3;
                
                String respuesta = procesarSolicitud(paqueteDatos);
                toClient.println(respuesta);
                
                client.close();
                System.out.println("Conexión cerrada con el cliente.\n");
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
            String[] partes = cadena.split(",");
            if (partes.length < 3) {
                return "Error en los datos recibidos.";
            }
            
            double num1 = Double.parseDouble(partes[0].trim());
            double num2 = Double.parseDouble(partes[1].trim());
            String op = partes[2].trim().toLowerCase();
            double resultado = 0;
            
            switch (op) {
                case "1":
                case "suma":
                    resultado = num1 + num2;
                    break;
                case "2":
                case "resta":
                case "restar":
                    resultado = num1 - num2;
                    break;
                case "3":
                case "multiplicacion":
                    resultado = num1 * num2;
                    break;
                case "4":
                case "division":
                    if (num2 != 0) {
                        resultado = num1 / num2;
                    } else {
                        return "Error: Division entre cero.";
                    }
                    break;
                default:
                    return "Operacion no valida.";
            }
            
            return "El resultado es: " + resultado;
        } catch (Exception e) {
            return "Error procesando la solicitud.";
        }
    }
}