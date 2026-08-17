package bo.edu.usfx.sockets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

// Manejador implementa Runnable porque viajará en la red / se ejecutará en un hilo
public class Manejador implements Runnable {

    // Colección ESTÁTICA: compartida por todos los hilos (Paso 9)
    private static final Set<Manejador> CLIENTES = new CopyOnWriteArraySet<>();

    private final Socket cliente;
    private final int id;
    private PrintWriter salida; // Lo sacamos afuera para poder acceder a él en difundir()

    public Manejador(Socket cliente, int id) {
        this.cliente = cliente;
        this.id = id;
    }

    @Override
    public void run() { // se ejecuta en OTRO hilo
        String hilo = Thread.currentThread().getName();
        
        // CORRECCIÓN 1: Solo abrimos el InputStream aquí, la salida la manejamos abajo
        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(cliente.getInputStream()))) {

            // CORRECCIÓN 2: Inicializamos la salida de forma limpia y única
            this.salida = new PrintWriter(cliente.getOutputStream(), true);
            CLIENTES.add(this); // Agregamos este cliente a la lista global

            salida.println("Bienvenido. Le atiende el hilo: " + hilo);
            String linea;

            // ESTADO COMPARTIDO: Difundimos a todos los clientes en tiempo real
            while ((linea = in.readLine()) != null) {
                System.out.println("[" + hilo + "] cliente " + id + ": " + linea);
                // Difundir a todos (Paso 9) 
                difundir("cliente-" + id + "> " + linea); 
            }

        } catch (IOException e) {
            System.err.println("Error con el cliente " + id + ": " + e.getMessage());
        } finally {
            // Sacar al cliente de la lista cuando se va
            CLIENTES.remove(this); 
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