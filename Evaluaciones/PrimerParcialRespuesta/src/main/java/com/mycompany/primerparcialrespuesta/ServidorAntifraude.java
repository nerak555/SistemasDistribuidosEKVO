package com.mycompany.primerparcialrespuesta;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class ServidorAntifraude {

    public static void main(String[] args) {

        int puerto = 2000;

        try {
            DatagramSocket socket = new DatagramSocket(puerto);

            System.out.println("Servidor Antifraude escuchando en puerto " + puerto);

            while (true) {

                // recibir mensaje del Banco
                byte[] buffer = new byte[124];

                DatagramPacket paqueteRecibido =
                        new DatagramPacket(buffer, buffer.length);

                socket.receive(paqueteRecibido);

                String mensaje = new String(
                        paqueteRecibido.getData(),
                        0,
                        paqueteRecibido.getLength()
                );

                System.out.println("Recibido: " + mensaje);

                // mensaje esperado:
                // riesgo:BO123:900.0

                String[] partes = mensaje.split(":");

                double monto = Double.parseDouble(partes[2]);

                String respuesta;

                if (monto > 1000) {
                    respuesta = "alto";
                } else {
                    respuesta = "bajo";
                }

                // convertir respuesta a bytes
                byte[] datosRespuesta = respuesta.getBytes();

                // responder al mismo Banco que hizo la petición
                DatagramPacket paqueteRespuesta =
                        new DatagramPacket(
                                datosRespuesta,
                                datosRespuesta.length,
                                paqueteRecibido.getAddress(),
                                paqueteRecibido.getPort()
                        );

                socket.send(paqueteRespuesta);

                System.out.println("Respuesta enviada: " + respuesta);
            }

        } catch (IOException e) {
            System.out.println("Error UDP: " + e.getMessage());
        }
    }
}