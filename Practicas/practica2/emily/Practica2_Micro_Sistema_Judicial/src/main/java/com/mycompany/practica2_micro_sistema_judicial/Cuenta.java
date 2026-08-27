package com.mycompany.practica2_micro_sistema_judicial;

import java.io.Serializable;

public class Cuenta implements Serializable {
    private static final long serialVersionUID = 1L;

    private Banco banco;
    private String nrocuenta;
    private String ci;
    private String nombres;
    private String apellidos;
    private Double saldo;

    public Cuenta(Banco banco, String nrocuenta, String ci,
                  String nombres, String apellidos, Double saldo) {
        this.banco = banco;
        this.nrocuenta = nrocuenta;
        this.ci = ci;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.saldo = saldo;
    }

    public Banco getBanco() {
        return banco;
    }

    public String getNrocuenta() {
        return nrocuenta;
    }

    public String getCi() {
        return ci;
    }

    public String getNombres() {
        return nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public Double getSaldo() {
        return saldo;
    }
}
