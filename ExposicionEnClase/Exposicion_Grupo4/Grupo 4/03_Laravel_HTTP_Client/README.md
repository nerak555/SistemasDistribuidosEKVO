# Cliente 03 - Laravel HTTP Client

Este es un proyecto Laravel **mínimo para aprendizaje**, sin `vendor/` (las dependencias se instalan con Composer).

## Instalación

Necesitas PHP 8.2 o superior y Composer.

1. Entra en esta carpeta.
2. Ejecuta `composer install`.
3. Copia `.env.example` a `.env` (Windows: `copy .env.example .env`).
4. Ejecuta `php artisan key:generate`.
5. Ejecuta `php artisan serve --host=127.0.0.1 --port=8003`.
6. Abre `http://127.0.0.1:8003`.

La API debe estar ejecutándose por separado en `http://127.0.0.1:8000`. Si cambia, edita `API_URL` en `.env`.

## Lo que debes estudiar

- **`routes/web.php`, función `api()`**: `Http::get()`, `Http::post()`, `Http::put()`, `Http::delete()` y `withToken()`.
- `resources/views/login.blade.php`: formulario de login.
- `resources/views/panel.blade.php`: CRUD de candidatos y usuarios.

`/api/usuarios` procede de tu segundo proyecto. El cliente supone estas rutas REST: `GET/POST /api/usuarios`, `PUT/DELETE /api/usuarios/{id}` y campos `name`, `email`, `role`, `password`. Adáptalos si tu implementación difiere.
