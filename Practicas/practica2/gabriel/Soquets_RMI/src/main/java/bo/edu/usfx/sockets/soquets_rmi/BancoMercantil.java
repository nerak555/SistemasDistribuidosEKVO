/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package bo.edu.usfx.sockets.soquets_rmi;

/**
 *
 * @author gabriel
 */
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class BancoMercantil {
    private static final int PUERTO = 5001;
    private static final String CI = "11021654";
    private static final String CUENTA = "1515";
    private static double saldo = 5100.0;

    public static void main(String[] args) throws Exception {
        ServerSocket servidor = new ServerSocket(PUERTO);
        System.out.println("Banco Mercantil TCP activo en el puerto " + PUERTO);

        while (true) {
            try (Socket cliente = servidor.accept();
                 BufferedReader entrada = new BufferedReader(
                         new InputStreamReader(cliente.getInputStream(), "UTF-8"));
                 PrintWriter salida = new PrintWriter(cliente.getOutputStream(), true)) {
                salida.println(procesar(entrada.readLine()));
            }
        }
    }

    private static synchronized String procesar(String peticion) {
        if (peticion == null) {
            return "ERROR";
        }

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
