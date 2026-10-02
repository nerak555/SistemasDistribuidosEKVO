# Errores cometidos en el Primer Parcial
## Sistemas Distribuidos - SIS258

**Estudiante:** Emily Karen Villarpando Olmedo

## Lo que me faltó en el examen

- No separé correctamente la interfaz RMI de la clase que la implementa.
- Coloqué elementos de `Pago` dentro de una interfaz y llegué a usar `IPago` incorrectamente.
- No implementé correctamente la estructura RMI de Operadora y Banco.
- No conecté los servidores entre sí.
- No realicé correctamente el `Naming.lookup()` y el registro de los servicios RMI.
- No completé la comunicación TCP entre Operadora y Migración.
- No sabía cómo enviar ni recibir datagramas UDP entre Banco y Antifraude.
- No completé la lógica de `Debitar()`.
- No completé la lógica de `ComprarTour()`.
- No logré conectar todo el flujo:

```text
ClienteTurista
→ Operadora
→ Migración
→ Banco
→ Antifraude