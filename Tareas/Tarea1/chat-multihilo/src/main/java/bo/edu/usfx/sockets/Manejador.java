/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package bo.edu.usfx.sockets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

// agregamos esto Paso 9
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 *
 * @author LENOVO
 */
// Manejador extiende de Rubbable porque viajara en la red tambein
public class Manejador implements Runnable {
    
    // Colección ESTÁTICA: compartida por todos los hilos (Paso 9)
    private static final Set<Manejador> CLIENTES = new CopyOnWriteArraySet<>();

    private final Socket cliente;
    private final int id;

    // agregamos
    private PrintWriter salida; // Lo sacamos afuera para poder acceder a él en difundir()

    public Manejador(Socket cliente, int id) {
        this.cliente = cliente;
        this.id = id;
    }

    @Override
    public void run() { // se ejecuta en OTRO hilo
        String hilo = Thread.currentThread().getName();
        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(cliente.getInputStream())); PrintWriter out = new PrintWriter(cliente.getOutputStream(), true)) {

            // Inicializamos la salida
            this.salida = new PrintWriter(cliente.getOutputStream(), true);
            CLIENTES.add(this); // Agregamos este cliente a la lista global

            out.println("Bienvenido. Le atiende el hilo: " + hilo);
            String linea;

            // ECO -> EL SERVER SOLO EL ESCUCHA LOS MENSAJES O ESTADOS. ENTRE HILOS DE CLIENTES, 
//            while ((linea = in.readLine()) != null) {
//                System.out.println("[" + hilo + "] cliente " + id + ": " + linea);
//                out.println("ECO(" + hilo + "): " + linea);
//            }

            // ESTADO COMPARTIDO, LOS ESTADOS EN LOS HILOS DE CLIENTES SE COMPARTES, CLIENTE 1 ENVIA Y CLIENTE 2 ESCUCHA EJM. EN TIEMPO REAL. 
            while ((linea = in.readLine()) != null) {
                System.out.println("[" + hilo + "] cliente " + id + ": " + linea);
                // En lugar de ECO, ahora difundimos a todos (Paso 9) 
                difundir("cliente-" + id + "> " + linea); //-- todos los clientes escucharan el ECO mediante una funcion Difundir()
            }

        } catch (IOException e) {
            System.err.println("Error con el cliente " + id + ": " + e.getMessage());
        } finally {
            try {
                cliente.close();
            } catch (IOException e) {
            }
            System.out.println("Cliente " + id + " desconectado");
        }
    }
    
        // Método para enviar el mensaje a todos los demás clientes
        private void difundir(String mensaje) {
            for (Manejador m : CLIENTES) {
                if (m != this && m.salida != null) {
                    m.salida.println(mensaje);
                }
            }
    }
}
