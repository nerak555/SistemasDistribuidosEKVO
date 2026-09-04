/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package bo.edu.usfx.jgroups;

import java.util.concurrent.ScheduledExecutorService;
import org.jgroups.Message;
import org.jgroups.View;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.ObjectMessage;
import org.jgroups.Receiver;

/**
 *
 * @author LENOVO
 */
public class RemateUSFX implements Receiver {

    private JChannel canal;
    private final String nombre;

    private final Map<String, Subasta> subastas = new HashMap<>();

    private final ScheduledExecutorService reloj
            = Executors.newSingleThreadScheduledExecutor();

    private View vistaActual;

    public RemateUSFX(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public void viewAccepted(View vista) {

        vistaActual = vista;

        System.out.println(
                "** Vista: " + vista.getMembers()
                + " | coordinador: " + vista.getCoord()
        );
    }

    public void iniciar() throws Exception {

        canal = new JChannel(
                System.getProperty("config", "udp.xml")
        );

        canal.name(nombre);
        canal.setReceiver(this);

        canal.connect("RemateSIS258");

        System.out.println(
                "Conectado como " + canal.getAddress()
        );

        leerTeclado();

        reloj.shutdownNow();
        canal.close();
    }

    private void leerTeclado() throws Exception {
        BufferedReader teclado
                = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("""
        Comandos:
        /crear <articulo> <precio_base> <segundos>
        /subastas
        /pujar <articulo> <monto>
        /estado <articulo>
        /quien
        /salir
        """);

        String linea;

        while ((linea = teclado.readLine()) != null) {

            linea = linea.trim();

            if (linea.equals("/salir")) {
                break;

            } else if (linea.equals("/quien")) {

                System.out.println(
                        "Miembros: " + canal.getView().getMembers()
                        + " | coordinador: " + canal.getView().getCoord()
                        + " | yo: " + canal.getAddress()
                );

            } else if (linea.equals("/subastas")) {

                mostrarSubastas();

            } else if (linea.startsWith("/crear ")) {

                crearSubasta(linea);

            } else if (linea.startsWith("/pujar ")) {

                proponerPuja(linea);

            } else if (linea.startsWith("/estado ")) {

                mostrarEstado(linea);

            } else {

                System.out.println("Comando no reconocido.");
            }
        }

    }

    private void mostrarSubastas() {
        synchronized (subastas) {

            if (subastas.isEmpty()) {
                System.out.println("No existen subastas.");
                return;
            }

            for (Subasta s : subastas.values()) {

                String postor = s.getMejorPostor() == null
                        ? "sin pujas"
                        : s.getMejorPostor();

                System.out.println(
                        s.getArticulo()
                        + " | Bs " + s.getMejorPuja()
                        + " | postor: " + postor
                        + " | quedan: " + s.segundosRestantes() + " s"
                        + " | " + (s.isCerrada() ? "CERRADA" : "ABIERTA")
                );
            }
        }

    }

    private void crearSubasta(String linea) throws Exception {
        String[] p = linea.split("\\s+");

        if (p.length != 4) {
            System.out.println(
                    "Uso: /crear <articulo> <precio_base> <segundos>"
            );
            return;
        }

        String articulo = p[1];
        double precioBase = Double.parseDouble(p[2]);
        long segundos = Long.parseLong(p[3]);

        long cierre
                = System.currentTimeMillis() + segundos * 1000;

        MensajeRemate mensaje = new MensajeRemate(
                TipoMensaje.NUEVA_SUBASTA,
                articulo,
                precioBase,
                nombre,
                cierre,
                ""
        );

        canal.send(new ObjectMessage(null, mensaje));

    }

    private void proponerPuja(String linea) throws Exception {
        String[] p = linea.split("\\s+");

        if (p.length != 3) {
            System.out.println(
                    "Uso: /pujar <articulo> <monto>"
            );
            return;
        }

        String articulo = p[1];
        double monto = Double.parseDouble(p[2]);

        Address coordinador = canal.getView().getCoord();

        MensajeRemate mensaje = new MensajeRemate(
                TipoMensaje.PROPUESTA_PUJA,
                articulo,
                monto,
                nombre,
                0,
                ""
        );

        canal.send(new ObjectMessage(coordinador, mensaje));
    }

    private void mostrarEstado(String linea) {

        String[] p = linea.split("\\s+");

        if (p.length != 2) {
            System.out.println("Uso: /estado <articulo>");
            return;
        }

        synchronized (subastas) {

            Subasta s = subastas.get(p[1]);

            if (s == null) {
                System.out.println("No existe esa subasta.");
                return;
            }

            System.out.println("Articulo: " + s.getArticulo());
            System.out.println("Precio base: Bs " + s.getPrecioBase());
            System.out.println("Mejor puja: Bs " + s.getMejorPuja());
            System.out.println("Mejor postor: " + s.getMejorPostor());
            System.out.println("Historial:");

            for (Puja puja : s.getHistorial()) {
                System.out.println("  " + puja);
            }
        }
    }

    @Override
    public void receive(Message msg) {
        
    Object obj = msg.getObject();

    if (!(obj instanceof MensajeRemate)) {
        return;
    }

    MensajeRemate m = (MensajeRemate) obj;

    if (m.getTipo() == TipoMensaje.NUEVA_SUBASTA) {

        synchronized (subastas) {

            if (subastas.containsKey(m.getArticulo())) {
                System.out.println(
                        "** Ya existe la subasta: " + m.getArticulo()
                );
                return;
            }

            Subasta nueva = new Subasta(
                    m.getArticulo(),
                    m.getMonto(),
                    m.getParticipante(),
                    m.getInstanteCierre()
            );

            subastas.put(m.getArticulo(), nueva);
        }

        System.out.println(
                "** NUEVA SUBASTA: "
                + m.getArticulo()
                + " | precio base: Bs " + m.getMonto()
                + " | creador: " + m.getParticipante()
        );
    }

    }

    public static void main(String[] args) throws Exception {

        String nombre = args.length > 0
                ? args[0]
                : "anonimo";

        new RemateUSFX(nombre).iniciar();
    }

}
