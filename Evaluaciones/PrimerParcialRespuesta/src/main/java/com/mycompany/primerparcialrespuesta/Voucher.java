package com.mycompany.primerparcialrespuesta;

import java.io.Serializable;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


/**
 *
 * @author LENOVO
 */
public class Voucher implements Serializable {
    private boolean confirmado;
    private String codigoCompra;
    private String motivo;
    private double montoUSD;

    public Voucher(boolean confirmado, String codigoCompra, String motivo, double montoUSD) {
        this.confirmado = confirmado;
        this.codigoCompra = codigoCompra;
        this.motivo = motivo;
        this.montoUSD = montoUSD;
    }

    public boolean isConfirmado() {
        return confirmado;
    }

    public String getCodigoCompra() {
        return codigoCompra;
    }

    public String getMotivo() {
        return motivo;
    }

    public double getMontoUSD() {
        return montoUSD;
    }

    @Override
    public String toString() {
        return "Voucher{" + "confirmado=" + confirmado + ", codigoCompra=" + codigoCompra + ", motivo=" + motivo + ", montoUSD=" + montoUSD + '}';
    }
    
    
}
