package bo.edu.usfx.practica2;


import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Scanner;


public class ClienteJuez {
    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("Uso: java ClienteJuez IP_JUSTICIA");
            return;
        }

        Registry registro = LocateRegistry.getRegistry(args[0], 1099);
        ServicioJusticia justicia = (ServicioJusticia) registro.lookup("Justicia");
        Scanner teclado = new Scanner(System.in);

        System.out.print("CI: ");
        String ci = teclado.nextLine();
        System.out.print("Nombres: ");
        String nombres = teclado.nextLine();
        System.out.print("Apellidos: ");
        String apellidos = teclado.nextLine();

        RespuestaCuenta respuesta = justicia.ConsultarCuentas(ci, nombres, apellidos);
        System.out.println(respuesta.getMensaje());
        ArrayList<Cuenta> cuentas = respuesta.getCuentas();

        for (int i = 0; i < cuentas.size(); i++) {
            Cuenta cuenta = cuentas.get(i);
            System.out.printf("%d. %s - Cuenta %s - Saldo %.2f%n",
                    i + 1, cuenta.getBanco(), cuenta.getNrocuenta(), cuenta.getSaldo());
        }

        if (cuentas.isEmpty()) {
            return;
        }

        System.out.print("¿Congelar fondos? (s/n): ");
        if (teclado.nextLine().equalsIgnoreCase("s")) {
            System.out.print("Número de la lista: ");
            int posicion = Integer.parseInt(teclado.nextLine()) - 1;
            System.out.print("Monto: ");
            double monto = Double.parseDouble(teclado.nextLine());

            boolean congelado = posicion >= 0 && posicion < cuentas.size()
                    && justicia.Congelar(cuentas.get(posicion), monto);
            System.out.println(congelado
                    ? "Fondos congelados correctamente"
                    : "No se pudo congelar el monto");
        }
    }
}
