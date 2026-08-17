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

/**
 *
 * @author LENOVO
 */
public class ClienteBasico {
    
       public static void main(String[] args) throws IOException {
           //26.125.87.253 
        String host = args.length > 0 ? args[0] : "26.125.87.253";

        Socket socket = new Socket(host, 5000);

        System.out.println(
                "Conectado. Puerto local: " + socket.getLocalPort());

        PrintWriter out = new PrintWriter(
                socket.getOutputStream(), true);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));

        BufferedReader teclado = new BufferedReader(
                new InputStreamReader(System.in));

        String texto;

        while ((texto = teclado.readLine()) != null) {

            out.println(texto);

            System.out.println("Servidor: " + in.readLine());
        }

        socket.close();
    }
}
