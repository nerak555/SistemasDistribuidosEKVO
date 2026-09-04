/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package bo.edu.usfx.jgroups;

import java.io.Serializable;

/**
 *
 * @author LENOVO
 */
public class Puja implements Serializable{
    private static final long serialVersionUID = 1L;

    private final String postor;
    private final double monto;
    private final long instante;

    public Puja(String postor, double monto, long instante) {
        this.postor = postor;
        this.monto = monto;
        this.instante = instante;
    }

    public String getPostor() {
        return postor;
    }

    public double getMonto() {
        return monto;
    }

    public long getInstante() {
        return instante;
    }

    @Override
    public String toString() {
        return postor + " -> Bs " + monto;
    }   
}
