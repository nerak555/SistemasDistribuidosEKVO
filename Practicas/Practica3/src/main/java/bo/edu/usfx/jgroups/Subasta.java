/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package bo.edu.usfx.jgroups;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author LENOVO
 */
public class Subasta implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String articulo;
    private final double precioBase;
    private final String creador;
    private final long instanteCierre;

    private double mejorPuja;
    private String mejorPostor;
    private boolean cerrada;

    private final List<Puja> historial;

    public Subasta(String articulo,
                   double precioBase,
                   String creador,
                   long instanteCierre) {

        this.articulo = articulo;
        this.precioBase = precioBase;
        this.creador = creador;
        this.instanteCierre = instanteCierre;

        this.mejorPuja = precioBase;
        this.mejorPostor = null;
        this.cerrada = false;

        this.historial = new ArrayList<>();
    }

    public String getArticulo() {
        return articulo;
    }

    public double getPrecioBase() {
        return precioBase;
    }

    public String getCreador() {
        return creador;
    }

    public long getInstanteCierre() {
        return instanteCierre;
    }

    public double getMejorPuja() {
        return mejorPuja;
    }

    public String getMejorPostor() {
        return mejorPostor;
    }

    public boolean isCerrada() {
        return cerrada;
    }

    public List<Puja> getHistorial() {
        return historial;
    }

    public void registrarPuja(Puja puja) {
        historial.add(puja);
        mejorPuja = puja.getMonto();
        mejorPostor = puja.getPostor();
    }

    public void cerrar() {
        cerrada = true;
    }

    public long segundosRestantes() {

        long restante =
                (instanteCierre - System.currentTimeMillis()) / 1000;

        return Math.max(restante, 0);
    }
}
