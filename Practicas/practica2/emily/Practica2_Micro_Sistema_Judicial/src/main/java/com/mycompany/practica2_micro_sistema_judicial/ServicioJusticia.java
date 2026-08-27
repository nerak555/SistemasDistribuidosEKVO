package com.mycompany.practica2_micro_sistema_judicial;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServicioJusticia extends Remote {
    RespuestaCuenta ConsultarCuentas(String ci, String nombres, String apellidos)
            throws RemoteException;

    boolean Congelar(Cuenta cuenta, double monto) throws RemoteException;
}
