/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.nodo3_jhonny;

import java.net.*;
import java.io.*;

/**
 *
 * @author cdk04
 */
public class Nodo3_Jhonny {

     public static void main(String[] args) {

        int puertoNodo3 = 5003;
        int puertoNodo1 = 5001;

        try {
            DatagramSocket socketUDP =
                new DatagramSocket(puertoNodo3);

            System.out.println(
                "Nodo 3 escuchando en el puerto " + puertoNodo3
            );

            while (true) {

                byte[] bufer = new byte[2000];

                DatagramPacket peticion = new DatagramPacket(bufer, bufer.length);

                socketUDP.receive(peticion);

                String recibido = new String(peticion.getData(), 0, peticion.getLength());

                // Recibe: texto|caracteres|palabras|tipo
                String[] partes = recibido.split("\\|", 4);

                String texto = partes[0];
                int caracteres = Integer.parseInt(partes[1]);
                int palabras = Integer.parseInt(partes[2]);
                String tipo = partes[3];

                String textoMayuscula = texto.toUpperCase();
                int cantidadVocales = contarVocales(texto);

                String resumen =
                    "Texto original: " + texto +
                    "\nTexto en mayusculas: " + textoMayuscula +
                    "\nCantidad de caracteres: " + caracteres +
                    "\nCantidad de palabras: " + palabras +
                    "\nCantidad de caracteres par o impar: " + tipo +
                    "\nCantidad de vocales: " + cantidadVocales;

                byte[] mensaje = resumen.getBytes();

                InetAddress ipNodo1 = InetAddress.getByName("192.168.43.95");

                DatagramPacket respuesta =
                    new DatagramPacket(
                        mensaje,
                        mensaje.length,
                        ipNodo1,
                        puertoNodo1
                    );

                socketUDP.send(respuesta);

                System.out.println("Nodo 3 envio el resultado final al Nodo 1.");
            }

        } catch (IOException e) {
            System.out.println("Error Nodo 3: " + e.getMessage());
        }
    }

    public static int contarVocales(String texto) {

        int contador = 0;
        String textoMinuscula = texto.toLowerCase();

        for (int i = 0; i < textoMinuscula.length(); i++) {

            char letra = textoMinuscula.charAt(i);

            if (letra == 'a' || letra == 'e' ||
                letra == 'i' || letra == 'o' ||
                letra == 'u') {

                contador++;
            }
        }
        
        return contador;
    }
}
