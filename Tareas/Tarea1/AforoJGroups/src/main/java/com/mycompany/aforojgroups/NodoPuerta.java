import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Scanner;
import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;

public class NodoPuerta extends ReceiverAdapter {

    private JChannel canal;
    private String nombre;
    private int aforoMaximo;
    private volatile int ocupacion = 0;
    private View vistaAnterior;

    public NodoPuerta(String nombre, int aforoMaximo) {
        this.nombre = nombre;
        this.aforoMaximo = aforoMaximo;
    }

    public void iniciar() throws Exception {
        canal = new JChannel();

        // Nombre logico de la puerta dentro de JGroups
        canal.name(nombre);

        // Esta clase recibira mensajes, cambios de vista y estado
        canal.setReceiver(this);

        // Todos los nodos se unen al mismo grupo
        canal.connect("AforoSIS258");

        // Si ya existen otras puertas, recuperar la ocupacion actual
        if (canal.getView().getMembers().size() > 1) {
            canal.getState(null, 10000);
        }

        System.out.println(nombre + " conectada al grupo AforoSIS258");
        System.out.println("Aforo maximo: " + aforoMaximo);
        System.out.println("Comandos: /entrar n, /salir n, /estado");

        menu();
    }

    private void menu() throws Exception {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.print("> ");
            String comando = sc.nextLine().trim();

            if (comando.equals("/estado")) {
                mostrarEstado();

            } else if (comando.startsWith("/entrar ")) {
                String[] partes = comando.split("\\s+");

                if (partes.length != 2) {
                    System.out.println("Uso: /entrar n");
                    continue;
                }

                try {
                    int personas = Integer.parseInt(partes[1]);

                    if (personas <= 0) {
                        System.out.println("La cantidad debe ser mayor a 0.");
                        continue;
                    }

                    Address coordinador = canal.getView().getMembers().get(0);

                    MensajeAforo solicitud = new MensajeAforo(
                            MensajeAforo.Tipo.SOLICITUD,
                            nombre,
                            personas
                    );

                    // Unicast: solo al coordinador
                    canal.send(new Message(coordinador, solicitud));
                    System.out.println("Solicitud enviada al coordinador: " + coordinador);

                } catch (NumberFormatException e) {
                    System.out.println("Ingrese un numero valido. Ejemplo: /entrar 5");
                }

            } else if (comando.startsWith("/salir ")) {
                String[] partes = comando.split("\\s+");

                if (partes.length != 2) {
                    System.out.println("Uso: /salir n");
                    continue;
                }

                try {
                    int personas = Integer.parseInt(partes[1]);

                    if (personas <= 0) {
                        System.out.println("La cantidad debe ser mayor a 0.");
                        continue;
                    }

                    if (personas > ocupacion) {
                        System.out.println("No pueden salir " + personas
                                + " personas. Ocupacion actual: " + ocupacion);
                        continue;
                    }

                    MensajeAforo salida = new MensajeAforo(
                            MensajeAforo.Tipo.SALIDA,
                            nombre,
                            personas
                    );

                    // Multicast: destino null significa enviar a todo el grupo
                    canal.send(new Message(null, salida));

                } catch (NumberFormatException e) {
                    System.out.println("Ingrese un numero valido. Ejemplo: /salir 3");
                }

            } else {
                System.out.println("Comando no valido.");
                System.out.println("Use: /entrar n, /salir n o /estado");
            }
        }
    }

    @Override
    public synchronized void receive(Message msg) {
        Object objeto = msg.getObject();

        if (!(objeto instanceof MensajeAforo)) {
            return;
        }

        MensajeAforo mensaje = (MensajeAforo) objeto;

        try {
            switch (mensaje.getTipo()) {

                case SOLICITUD:
                    // Solo el coordinador decide si acepta o rechaza
                    if (!esCoordinador()) {
                        return;
                    }

                    System.out.println("\nSolicitud de " + mensaje.getPuerta()
                            + ": entrar " + mensaje.getPersonas() + " personas.");

                    if (ocupacion + mensaje.getPersonas() <= aforoMaximo) {
                        MensajeAforo aceptado = new MensajeAforo(
                                MensajeAforo.Tipo.ACEPTADO,
                                mensaje.getPuerta(),
                                mensaje.getPersonas()
                        );

                        // Multicast: todos deben sumar
                        canal.send(new Message(null, aceptado));

                    } else {
                        MensajeAforo rechazado = new MensajeAforo(
                                MensajeAforo.Tipo.RECHAZADO,
                                mensaje.getPuerta(),
                                mensaje.getPersonas()
                        );

                        // Unicast: solo responde a la puerta solicitante
                        canal.send(new Message(msg.getSrc(), rechazado));
                    }
                    break;

                case ACEPTADO:
                    ocupacion += mensaje.getPersonas();

                    System.out.println("\n[ACEPTADO] " + mensaje.getPuerta()
                            + " ingreso " + mensaje.getPersonas() + " persona(s).");
                    mostrarEstado();
                    break;

                case RECHAZADO:
                    System.out.println("\n[RECHAZADO] No pueden ingresar "
                            + mensaje.getPersonas() + " persona(s) por "
                            + mensaje.getPuerta() + ".");
                    mostrarEstado();
                    break;

                case SALIDA:
                    ocupacion -= mensaje.getPersonas();

                    if (ocupacion < 0) {
                        ocupacion = 0;
                    }

                    System.out.println("\n[SALIDA] " + mensaje.getPuerta()
                            + " registro la salida de "
                            + mensaje.getPersonas() + " persona(s).");
                    mostrarEstado();
                    break;
            }

        } catch (Exception e) {
            System.out.println("Error procesando mensaje: " + e.getMessage());
        }
    }

    private boolean esCoordinador() {
        Address coordinador = canal.getView().getMembers().get(0);
        return canal.getAddress().equals(coordinador);
    }

    private synchronized void mostrarEstado() {
        System.out.println("Ocupacion: " + ocupacion + "/" + aforoMaximo);

        if (ocupacion >= aforoMaximo) {
            System.out.println("AFORO COMPLETO");
        }
    }

    @Override
    public synchronized void getState(OutputStream output) throws Exception {
        DataOutputStream dos = new DataOutputStream(output);
        dos.writeInt(ocupacion);
        dos.flush();
    }

    @Override
    public synchronized void setState(InputStream input) throws Exception {
        DataInputStream dis = new DataInputStream(input);
        ocupacion = dis.readInt();

        System.out.println("Estado recibido. Ocupacion actual: "
                + ocupacion + "/" + aforoMaximo);
    }

    @Override
    public synchronized void viewAccepted(View vista) {
        List<Address> miembrosActuales = vista.getMembers();

        if (vistaAnterior == null) {
            for (Address miembro : miembrosActuales) {
                System.out.println("[+] " + miembro + " entro al grupo.");
            }
        } else {
            List<Address> miembrosAnteriores = vistaAnterior.getMembers();

            for (Address miembro : miembrosActuales) {
                if (!miembrosAnteriores.contains(miembro)) {
                    System.out.println("[+] " + miembro + " entro al grupo.");
                }
            }

            for (Address miembro : miembrosAnteriores) {
                if (!miembrosActuales.contains(miembro)) {
                    System.out.println("[-] " + miembro + " salio del grupo.");
                }
            }
        }

        if (!miembrosActuales.isEmpty()) {
            System.out.println("Miembros conectados: " + miembrosActuales);
            System.out.println("Coordinador actual: " + miembrosActuales.get(0));
        }

        vistaAnterior = vista;
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.out.println("Uso: java NodoPuerta <nombre> <aforoMaximo>");
            return;
        }

        String nombre = args[0];
        int aforoMaximo;

        try {
            aforoMaximo = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.out.println("El aforoMaximo debe ser un numero entero.");
            return;
        }

        if (aforoMaximo <= 0) {
            System.out.println("El aforoMaximo debe ser mayor a 0.");
            return;
        }

        NodoPuerta puerta = new NodoPuerta(nombre, aforoMaximo);
        puerta.iniciar();
    }
}
