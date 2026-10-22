# Grupo 4 - Consumo de una API REST con PHP

Tres clientes web para la MISMA API REST. La diferencia está en **cómo envían las peticiones HTTP**.

| Carpeta | Biblioteca | Archivo principal para aprender |
| --- | --- | --- |
| `01_PHP_cURL` | cURL nativo | `api.php` |
| `02_PHP_Guzzle` | Guzzle | `api.php` |
| `03_Laravel_HTTP_Client` | Laravel HTTP Client | `routes/web.php` (función `api`) |

## Antes de empezar

1. Levanta **tu API REST Laravel** primero, normalmente en `http://127.0.0.1:8000`, ejecutando `php artisan serve` en el proyecto del servidor.
2. Comprueba el login en Insomnia: `POST http://127.0.0.1:8000/api/login`. Ejemplo de datos sembrados: `test@example.com` / `password`.
3. Comprueba `GET /api/candidatos` y `GET /api/usuarios` con el token JWT.
4. Inicia los clientes, cada uno en **otro puerto**.

## Cómo ejecutar los clientes

### 01 - cURL

```bash
cd "01_PHP_cURL"
php -S 127.0.0.1:8001
```

Abre `http://127.0.0.1:8001`.

### 02 - Guzzle

```bash
cd "02_PHP_Guzzle"
composer install
php -S 127.0.0.1:8002
```

Abre `http://127.0.0.1:8002`.

### 03 - Laravel HTTP Client

```bash
cd "03_Laravel_HTTP_Client"
composer install
```

Copia `.env.example` a `.env`:

- En Windows: `copy .env.example .env`
- En macOS/Linux: `cp .env.example .env`

```bash
php artisan key:generate
php artisan serve --host=127.0.0.1 --port=8003
```

Abre `http://127.0.0.1:8003`.

**Requisitos:** PHP 8.2+, Composer, extensión cURL activa para el cliente 01 y una API funcionando en 8000. Para el cliente Laravel, Composer descargará las dependencias. Se requiere internet al instalarlas si no están en caché.

## Qué hace cada cliente

- Login con `POST /api/login`, recibe `token` y lo almacena en una sesión del cliente web.
- Añade `Authorization: Bearer TOKEN` a sus solicitudes protegidas.
- CRUD de candidatos (`/api/candidatos`): `nombres`, `apellidos`, `cargo`, `ambito`.
- CRUD de usuarios (`/api/usuarios`): `name`, `email`, `role`, `password`.
- El rol `admin` puede usar los formularios de crear/editar/eliminar candidatos. Los clientes muestran también la gestión de usuarios para `admin`.
- Al cerrar sesión se borra la sesión web del cliente.

## Importante sobre usuarios

En la copia pública del repositorio `cmontellanob/elecicion-api` no existen endpoints de usuarios. **Tú has confirmado con Insomnia que tu otro proyecto sí responde a `GET /api/usuarios`**. Por eso NO se modifica el servidor ni se incluye una ampliación.

Los endpoints **POST**, **PUT** y **DELETE** de usuarios no se pueden confirmar solo con esa captura. Los clientes suponen las rutas REST convencionales `POST /api/usuarios`, `PUT /api/usuarios/{id}` y `DELETE /api/usuarios/{id}`. Si tu API utiliza otros métodos o URLs, modifica la ruta del formulario correspondiente.

También se asume que los campos que espera tu API de usuarios son `name`, `email`, `role`, `password`. La captura confirma los 3 primeros al listar, pero el formato exacto requerido para crear/editar depende de tu implementación.

## Consejos para la presentación

1. Demuestra login en cada cliente.
2. Lista candidatos y usuarios (GET).
3. Crea un candidato y un usuario (POST).
4. Edita (PUT) y elimina (DELETE) algún registro de prueba.
5. Abre `api.php` de cURL y Guzzle, y la función `api` de Laravel, para explicar la diferencia.

**No se incluye la API del docente; estos son clientes HTTP, no servidores REST.**
