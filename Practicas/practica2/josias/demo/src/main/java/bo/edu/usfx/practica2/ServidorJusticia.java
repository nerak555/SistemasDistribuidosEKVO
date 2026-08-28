package bo.edu.usfx.practica2;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class ServidorJusticia extends UnicastRemoteObject implements ServicioJusticia {
    private static final int PUERTO_MERCANTIL = 5001;
    private static final int PUERTO_BCP = 5002;
    private final String ipBancos;

    public ServidorJusticia(String ipBancos) throws Exception {
        super(1100);
        this.ipBancos = ipBancos;
    }

    @Override
    public RespuestaCuenta ConsultarCuentas(String ci, String nombres, String apellidos) {
        ArrayList<Cuenta> cuentas = new ArrayList<Cuenta>();
        ArrayList<String> errores = new ArrayList<String>();

        try {
            agregarCuentas(cuentas, Banco.MERCANTIL,
                    enviarTcp("BUSCAR:" + ci), ci, nombres, apellidos);
        } catch (Exception e) {
            errores.add("Mercantil: " + e.getMessage());
        }

        try {
            agregarCuentas(cuentas, Banco.BCP,
                    enviarUdp("BUSCAR:" + ci), ci, nombres, apellidos);
        } catch (Exception e) {
            errores.add("BCP: " + e.getMessage());
        }

        boolean error = !errores.isEmpty();
        String mensaje = error ? String.join(" | ", errores) : "Consulta correcta";
        return new RespuestaCuenta(error, mensaje, cuentas);
    }

    @Override
    public boolean Congelar(Cuenta cuenta, double monto) {
        if (cuenta == null || monto <= 0) {
            return false;
        }

        String peticion = "CONGELAR:" + cuenta.getNrocuenta() + ":" + monto;
        try {
            String respuesta = cuenta.getBanco() == Banco.MERCANTIL
                    ? enviarTcp(peticion)
                    : enviarUdp(peticion);
            return respuesta.equals("OK");
        } catch (Exception e) {
            return false;
        }
    }

    private String enviarTcp(String peticion) throws Exception {
        try (Socket socket = new Socket(ipBancos, PUERTO_MERCANTIL);
             PrintWriter salida = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader entrada = new BufferedReader(
                     new InputStreamReader(socket.getInputStream(), "UTF-8"))) {
            salida.println(peticion);
            return entrada.readLine();
        }
    }

    private String enviarUdp(String peticion) throws Exception {
        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setSoTimeout(3000);
            byte[] datos = peticion.getBytes(StandardCharsets.UTF_8);
            DatagramPacket envio = new DatagramPacket(datos, datos.length,
                    InetAddress.getByName(ipBancos), PUERTO_BCP);
            socket.send(envio);

            byte[] buffer = new byte[1024];
            DatagramPacket respuesta = new DatagramPacket(buffer, buffer.length);
            socket.receive(respuesta);
            return new String(respuesta.getData(), 0,
                    respuesta.getLength(), StandardCharsets.UTF_8);
        }
    }

    private void agregarCuentas(ArrayList<Cuenta> cuentas, Banco banco,
                                String respuesta, String ci,
                                String nombres, String apellidos) {
        if (respuesta == null || respuesta.isEmpty()) {
            return;
        }

        String[] registros = respuesta.split(":");
        for (String registro : registros) {
            String[] datos = registro.split("-");
            cuentas.add(new Cuenta(banco, datos[0], ci, nombres,
                    apellidos, Double.parseDouble(datos[1])));
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.out.println("Uso: java ServidorJusticia IP_JUSTICIA IP_BANCOS");
            return;
        }

        System.setProperty("java.rmi.server.hostname", args[0]);
        Registry registro = LocateRegistry.createRegistry(1099);
        registro.rebind("Justicia", new ServidorJusticia(args[1]));
        System.out.println("Servidor Justicia RMI activo en " + args[0]);
    }
}
