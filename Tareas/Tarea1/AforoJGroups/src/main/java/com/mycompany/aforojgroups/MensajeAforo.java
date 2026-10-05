import java.io.Serializable;

public class MensajeAforo implements Serializable {

    private static final long serialVersionUID = 1L;

    public enum Tipo {
        SOLICITUD,
        ACEPTADO,
        RECHAZADO,
        SALIDA
    }

    private Tipo tipo;
    private String puerta;
    private int personas;

    public MensajeAforo(Tipo tipo, String puerta, int personas) {
        this.tipo = tipo;
        this.puerta = puerta;
        this.personas = personas;
    }

    public Tipo getTipo() {
        return tipo;
    }

    public String getPuerta() {
        return puerta;
    }

    public int getPersonas() {
        return personas;
    }

    @Override
    public String toString() {
        return tipo + " - " + puerta + " - " + personas;
    }
}

