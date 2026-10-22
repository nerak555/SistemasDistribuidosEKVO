<?php
require_once __DIR__ . '/config.php';
require_once __DIR__ . '/vendor/autoload.php'; // Lo crea Composer.

use GuzzleHttp\Client;

// Misma función pública que en cURL, pero las peticiones las hace Guzzle.
function api(string $metodo, string $ruta, array $datos = [], ?string $token = null): array
{
    $cliente = new Client(['base_uri' => API_URL, 'timeout' => 15]);

    $opciones = [
        'http_errors' => false, // Permite leer errores 400, 401, 403, etc.
        'headers' => ['Accept' => 'application/json'],
    ];

    if ($token) {
        $opciones['headers']['Authorization'] = 'Bearer ' . $token;
    }
    if ($metodo === 'POST' || $metodo === 'PUT') {
        $opciones['json'] = $datos; // Guzzle convierte el array a JSON.
    }

    try {
        $respuesta = $cliente->request($metodo, $ruta, $opciones);
        return [
            'codigo' => $respuesta->getStatusCode(),
            'datos' => json_decode((string) $respuesta->getBody(), true) ?? [],
        ];
    } catch (\GuzzleHttp\Exception\GuzzleException $e) {
        return ['codigo' => 0, 'datos' => ['message' => 'No se pudo conectar: ' . $e->getMessage()]];
    }
}
