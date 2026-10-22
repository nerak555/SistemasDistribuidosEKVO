# Cliente PHP con cURL

1. Asegúrate de que la API Laravel esté encendida en `http://127.0.0.1:8000`.
2. Si el puerto cambia, modifica `config.php`.
3. Habilita la extensión PHP `curl`.
4. Dentro de esta carpeta ejecuta `php -S 127.0.0.1:8001`.
5. Abre la dirección indicada en el navegador.

Archivos:
- `config.php`: URL de la API.
- `api.php`: aquí está **la forma de consumir REST**.
- `index.php`: login, sesiones y formularios CRUD.

La API de usuarios procede de tu otro proyecto. Este cliente supone que están disponibles `GET/POST /api/usuarios` y `PUT/DELETE /api/usuarios/{id}`. Datos esperados: `name`, `email`, `role`, `password`. Si las rutas o campos son distintos, ajústalos en `index.php`.
