# Comparación de las tres maneras de consumir REST

En los tres clientes ocurre lo mismo:

1. `POST /api/login` → enviar correo y contraseña.
2. Obtener la propiedad `token` de la respuesta JSON.
3. Guardar el token en la sesión web.
4. Para el CRUD, enviar `Authorization: Bearer <token>`.
5. Mostrar las listas JSON en HTML.

## 01: PHP cURL

Archivo **`01_PHP_cURL/api.php`**:

```php
$curl = curl_init($url);
curl_setopt($curl, CURLOPT_RETURNTRANSFER, true);
$respuesta = curl_exec($curl);
curl_close($curl);
```

## 02: PHP Guzzle

Archivo **`02_PHP_Guzzle/api.php`**:

```php
$cliente = new Client();
$respuesta = $cliente->request('GET', $url);
```

## 03: Laravel HTTP Client

Archivo **`03_Laravel_HTTP_Client/routes/web.php`**:

```php
$respuesta = Http::withToken(session('token'))
    ->get($url);
```

La API y los métodos HTTP son los mismos; cambia **la librería cliente**.
