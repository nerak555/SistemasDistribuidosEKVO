/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package bo.edu.usfx.sockets.soquets_rmi;

/**
 *
 * @author gabriel
 */
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;

public class BancoBCP {
    private static final int PUERTO = 5002;
    private static final String CI = "11021654";
    private static final String CUENTA = "6576";
    private static double saldo = 6500.0;

    public static void main(String[] args) throws Exception {
        DatagramSocket servidor = new DatagramSocket(PUERTO);
        System.out.println("Banco BCP UDP activo en el puerto " + PUERTO);

        while (true) {
            byte[] buffer = new byte[1024];
            DatagramPacket solicitud = new DatagramPacket(buffer, buffer.length);
            servidor.receive(solicitud);

            String peticion = new String(solicitud.getData(), 0,
                    solicitud.getLength(), StandardCharsets.UTF_8);
            byte[] respuesta = procesar(peticion).getBytes(StandardCharsets.UTF_8);
            DatagramPacket envio = new DatagramPacket(respuesta, respuesta.length,
                    solicitud.getAddress(), solicitud.getPort());
            servidor.send(envio);
        }
    }

    private static synchronized String procesar(String peticion) {
        String[] partes = peticion.split(":");
        if (partes.length == 2 && partes[0].equalsIgnoreCase("BUSCAR")) {
            return partes[1].equals(CI) ? CUENTA + "-" + saldo : "";
        }

        if (partes.length == 3 && partes[0].equalsIgnoreCase("CONGELAR")) {
            double monto = Double.parseDouble(partes[2]);
            if (partes[1].equals(CUENTA) && monto > 0 && monto <= saldo) {
                saldo -= monto;
                return "OK";
            }
        }
        return "ERROR";
    }
}
