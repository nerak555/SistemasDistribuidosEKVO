/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.nodo1_karen;

import java.net.*;
import java.io.*;
import java.util.Scanner;


/**
 *
 * @author LENOVO
 */
public class Nodo1_Karen {
     public static void main(String[] args) {

        int puertoNodo1 = 5001;
        int puertoNodo2 = 5002;

        Scanner sc = new Scanner(System.in);

        try {
            // Nodo 1 también debe escuchar la respuesta final
            DatagramSocket socketUDP =
                new DatagramSocket(puertoNodo1);

            System.out.print("Introduzca una palabra o frase: ");
            String texto = sc.nextLine();

            // Cuenta los caracteres, incluyendo espacios
            int cantidadCaracteres = texto.length();

            // Formato: texto|cantidadCaracteres
            String datos = texto + "|" + cantidadCaracteres;
            byte[] mensaje = datos.getBytes();

            InetAddress ipNodo2 =
                InetAddress.getByName("192.168.43.176");

            DatagramPacket peticion =
                new DatagramPacket(
                    mensaje,
                    mensaje.length,
                    ipNodo2,
                    puertoNodo2
                );

            socketUDP.send(peticion);

            System.out.println("Informacion enviada al Nodo 2.");

            // Esperar respuesta final del Nodo 3
            byte[] bufer = new byte[2000];

            DatagramPacket respuesta =
                new DatagramPacket(bufer, bufer.length);

            socketUDP.receive(respuesta);

            String resultado = new String(
                respuesta.getData(),
                0,
                respuesta.getLength()
            );

            System.out.println("\n--------------- RESULTADO FINAL ---------------");
            System.out.println(resultado);

            socketUDP.close();

        } catch (IOException e) {
            System.out.println("Error Nodo 1: " + e.getMessage());
        }
    }
}
