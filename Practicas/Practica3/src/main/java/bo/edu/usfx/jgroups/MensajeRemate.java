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
public class MensajeRemate implements Serializable{
    private static final long serialVersionUID = 1L;

    private final TipoMensaje tipo;
    private final String articulo;
    private final double monto;
    private final String participante;
    private final long instanteCierre;
    private final String detalle;

    public MensajeRemate(
            TipoMensaje tipo,
            String articulo,
            double monto,
            String participante,
            long instanteCierre,
            String detalle) {

        this.tipo = tipo;
        this.articulo = articulo;
        this.monto = monto;
        this.participante = participante;
        this.instanteCierre = instanteCierre;
        this.detalle = detalle;
    }

    public TipoMensaje getTipo() {
        return tipo;
    }

    public String getArticulo() {
        return articulo;
    }

    public double getMonto() {
        return monto;
    }

    public String getParticipante() {
        return participante;
    }

    public long getInstanteCierre() {
        return instanteCierre;
    }

    public String getDetalle() {
        return detalle;
    }

    @Override
    public String toString() {
        return "MensajeRemate{"
                + "tipo=" + tipo
                + ", articulo=" + articulo
                + ", monto=" + monto
                + ", participante=" + participante
                + '}';
    }
}
