/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package bo.edu.usfx.sockets;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author LENOVO
 */
public class ServidorMultihilo {

    public static void main(String[] args) throws IOException {
        int puerto = args.length > 0 ? Integer.parseInt(args[0]) : 5000;
        int hilos = args.length > 1 ? Integer.parseInt(args[1]) : 4;
        ServerSocket servidor = new ServerSocket(puerto);
        ExecutorService pool = Executors.newFixedThreadPool(hilos);
        System.out.println("Servidor multihilo en el puerto " + puerto);
        int contador = 0;
        while (true) {
            Socket cliente = servidor.accept(); // solo acepta clientes 
            contador++;
            System.out.println("Conexion #" + contador + " desde "
                    + cliente.getInetAddress().getHostAddress());
            pool.execute(new Manejador(cliente, contador)); // la logica lo delega al Manajeador
        }
    }
}
