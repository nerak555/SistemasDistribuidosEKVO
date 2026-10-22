<?php
require_once __DIR__ . '/config.php';

// Esta es la ÚNICA parte que cambia entre los clientes.
// Recibe método, ruta, datos y token; devuelve código HTTP y JSON.
function api(string $metodo, string $ruta, array $datos = [], ?string $token = null): array
{
    $curl = curl_init(API_URL . $ruta);

    $cabeceras = ['Accept: application/json', 'Content-Type: application/json'];
    if ($token) {
        $cabeceras[] = 'Authorization: Bearer ' . $token;
    }

    curl_setopt_array($curl, [
        CURLOPT_RETURNTRANSFER => true,
        CURLOPT_CUSTOMREQUEST  => $metodo,
        CURLOPT_HTTPHEADER     => $cabeceras,
        CURLOPT_TIMEOUT        => 15,
    ]);

    if ($metodo === 'POST' || $metodo === 'PUT') {
        curl_setopt($curl, CURLOPT_POSTFIELDS, json_encode($datos));
    }

    $respuesta = curl_exec($curl);
    $codigo = curl_getinfo($curl, CURLINFO_HTTP_CODE);
    $error = curl_error($curl);
    curl_close($curl);

    if ($respuesta === false) {
        return ['codigo' => 0, 'datos' => ['message' => 'No se pudo conectar: ' . $error]];
    }

    return ['codigo' => $codigo, 'datos' => json_decode($respuesta, true) ?? []];
}
