<?php
session_start();
require_once __DIR__ . '/api.php';

// Escapar texto antes de mostrarlo en HTML.
function h($valor): string
{
    return htmlspecialchars((string) $valor, ENT_QUOTES, 'UTF-8');
}

// Token para proteger los formularios de esta aplicación.
$_SESSION['csrf'] ??= bin2hex(random_bytes(16));
$mensaje = $_SESSION['mensaje'] ?? '';
unset($_SESSION['mensaje']);

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    if (!hash_equals($_SESSION['csrf'], $_POST['csrf'] ?? '')) {
        $mensaje = 'Formulario no válido. Actualiza la página.';
    } else {
        $accion = $_POST['accion'] ?? '';

        if ($accion === 'salir') {
            session_destroy();
            header('Location: index.php');
            exit;
        }

        if ($accion === 'login') {
            $r = api('POST', 'login', [
                'email' => $_POST['email'] ?? '',
                'password' => $_POST['password'] ?? '',
            ]);

            if ($r['codigo'] === 200 && !empty($r['datos']['token'])) {
                session_regenerate_id(true);
                $_SESSION['token'] = $r['datos']['token'];
                $_SESSION['nombre'] = $r['datos']['usuario']['name'] ?? 'Usuario';
                $_SESSION['rol'] = $r['datos']['usuario']['role'] ?? 'user';
                $_SESSION['id_usuario'] = $r['datos']['usuario']['id'] ?? null;
                header('Location: index.php');
                exit;
            }
            $mensaje = $r['datos']['mensaje'] ?? $r['datos']['message'] ?? 'Error al iniciar sesión.';
        } elseif (!empty($_SESSION['token'])) {
            $token = $_SESSION['token'];
            $r = null;

            // CRUD de candidatos: exactamente /api/candidatos del proyecto.
            if ($accion === 'crear_candidato' || $accion === 'editar_candidato') {
                $datos = [
                    'nombres' => $_POST['nombres'] ?? '',
                    'apellidos' => $_POST['apellidos'] ?? '',
                    'cargo' => $_POST['cargo'] ?? '',
                    'ambito' => $_POST['ambito'] ?? '',
                ];
                $metodo = $accion === 'crear_candidato' ? 'POST' : 'PUT';
                $ruta = 'candidatos' . ($metodo === 'PUT' ? '/' . (int) ($_POST['id'] ?? 0) : '');
                $r = api($metodo, $ruta, $datos, $token);
            } elseif ($accion === 'eliminar_candidato') {
                $r = api('DELETE', 'candidatos/' . (int) ($_POST['id'] ?? 0), [], $token);
            }

            // CRUD de usuarios: utiliza la API de usuarios que ya tienes.
            if ($accion === 'crear_usuario' || $accion === 'editar_usuario') {
                $datos = [
                    'name' => $_POST['name'] ?? '',
                    'email' => $_POST['email'] ?? '',
                    'role' => $_POST['role'] ?? 'user',
                ];
                // Contraseña obligatoria al crear; al editar es opcional.
                if ($accion === 'crear_usuario' || !empty($_POST['password'])) {
                    $datos['password'] = $_POST['password'] ?? '';
                }
                $metodo = $accion === 'crear_usuario' ? 'POST' : 'PUT';
                $ruta = 'usuarios' . ($metodo === 'PUT' ? '/' . (int) ($_POST['id'] ?? 0) : '');
                $r = api($metodo, $ruta, $datos, $token);
            } elseif ($accion === 'eliminar_usuario') {
                if ((int) ($_POST['id'] ?? 0) === (int) ($_SESSION['id_usuario'] ?? -1)) {
                    $mensaje = 'No puedes eliminar tu propia cuenta desde este formulario.';
                } else {
                    $r = api('DELETE', 'usuarios/' . (int) ($_POST['id'] ?? 0), [], $token);
                }
            }

            if ($r !== null) {
                if ($r['codigo'] >= 200 && $r['codigo'] < 300) {
                    $_SESSION['mensaje'] = 'Operación realizada correctamente.';
                    header('Location: index.php');
                    exit;
                }
                $detalle = $r['datos']['message'] ?? $r['datos']['mensaje'] ?? json_encode($r['datos']);
                $mensaje = 'Error HTTP ' . $r['codigo'] . ': ' . $detalle;
            }
        }
    }
}

$token = $_SESSION['token'] ?? null;
$admin = ($_SESSION['rol'] ?? '') === 'admin';
$candidatos = $token ? api('GET', 'candidatos', [], $token) : null;
$usuarios = ($token && $admin) ? api('GET', 'usuarios', [], $token) : null;
$csrf = $_SESSION['csrf'];
?>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Grupo 4 - Cliente API REST</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 24px; }
        input, select, button { padding: 6px; margin: 3px; }
        table { border-collapse: collapse; width: 100%; margin: 12px 0; }
        th, td { border: 1px solid #aaa; padding: 7px; text-align: left; }
        details { margin: 6px 0; }
        section { margin-top: 30px; }
        .error { color: #ae1414; }
        .ayuda { color: #555; }
    </style>
</head>
<body>
<h1>Grupo 4 - Cliente PHP</h1>
<?php if ($mensaje): ?><p class="error"><?= h($mensaje) ?></p><?php endif; ?>

<?php if (!$token): ?>
    <h2>Iniciar sesión</h2>
    <form method="post">
        <input type="hidden" name="csrf" value="<?= h($csrf) ?>">
        <input type="hidden" name="accion" value="login">
        <input type="email" name="email" placeholder="Correo" required>
        <input type="password" name="password" placeholder="Contraseña" required>
        <button>Entrar</button>
    </form>
    <p class="ayuda">Para los datos sembrados por el proyecto: test@example.com / password</p>
<?php else: ?>
    <p>Sesión: <strong><?= h($_SESSION['nombre']) ?></strong> (<?= h($_SESSION['rol']) ?>)</p>
    <form method="post">
        <input type="hidden" name="csrf" value="<?= h($csrf) ?>">
        <input type="hidden" name="accion" value="salir">
        <button>Cerrar sesión</button>
    </form>

    <section>
        <h2>Candidatos (GET /api/candidatos)</h2>
        <?php if ($candidatos['codigo'] !== 200): ?>
            <p class="error">No se pudo consultar candidatos. HTTP <?= h($candidatos['codigo']) ?>. Verifica el servidor y el token.</p>
        <?php else: ?>
            <?php if ($admin): ?>
                <h3>Crear candidato (POST)</h3>
                <form method="post">
                    <input type="hidden" name="csrf" value="<?= h($csrf) ?>">
                    <input type="hidden" name="accion" value="crear_candidato">
                    <input name="nombres" placeholder="Nombres" required>
                    <input name="apellidos" placeholder="Apellidos" required>
                    <input name="cargo" placeholder="Cargo" required>
                    <input name="ambito" placeholder="Ámbito" required>
                    <button>Crear</button>
                </form>
            <?php endif; ?>
            <table>
                <tr><th>ID</th><th>Nombres</th><th>Apellidos</th><th>Cargo</th><th>Ámbito</th><?php if ($admin): ?><th>Acciones</th><?php endif; ?></tr>
                <?php foreach ($candidatos['datos'] as $c): ?>
                    <tr>
                        <td><?= h($c['id']) ?></td><td><?= h($c['nombres']) ?></td>
                        <td><?= h($c['apellidos']) ?></td><td><?= h($c['cargo']) ?></td><td><?= h($c['ambito']) ?></td>
                        <?php if ($admin): ?><td>
                            <details><summary>Editar (PUT)</summary>
                                <form method="post">
                                    <input type="hidden" name="csrf" value="<?= h($csrf) ?>">
                                    <input type="hidden" name="accion" value="editar_candidato">
                                    <input type="hidden" name="id" value="<?= h($c['id']) ?>">
                                    <input name="nombres" value="<?= h($c['nombres']) ?>" required>
                                    <input name="apellidos" value="<?= h($c['apellidos']) ?>" required>
                                    <input name="cargo" value="<?= h($c['cargo']) ?>" required>
                                    <input name="ambito" value="<?= h($c['ambito']) ?>" required>
                                    <button>Guardar</button>
                                </form>
                            </details>
                            <form method="post" onsubmit="return confirm('¿Eliminar candidato?')">
                                <input type="hidden" name="csrf" value="<?= h($csrf) ?>">
                                <input type="hidden" name="accion" value="eliminar_candidato">
                                <input type="hidden" name="id" value="<?= h($c['id']) ?>">
                                <button>Eliminar (DELETE)</button>
                            </form>
                        </td><?php endif; ?>
                    </tr>
                <?php endforeach; ?>
            </table>
        <?php endif; ?>
    </section>

    <?php if ($admin): ?>
    <section>
        <h2>Usuarios (GET /api/usuarios)</h2>
        <?php if ($usuarios['codigo'] !== 200): ?>
            <p class="error">No se pudo consultar /api/usuarios (HTTP <?= h($usuarios['codigo']) ?>). Revisa el servidor, el token y las rutas de tu proyecto.</p>
        <?php else: ?>
            <h3>Crear usuario (POST)</h3>
            <form method="post">
                <input type="hidden" name="csrf" value="<?= h($csrf) ?>">
                <input type="hidden" name="accion" value="crear_usuario">
                <input name="name" placeholder="Nombre" required>
                <input type="email" name="email" placeholder="Correo" required>
                <input type="password" name="password" placeholder="Contraseña" required>
                <select name="role"><option value="user">user</option><option value="admin">admin</option></select>
                <button>Crear</button>
            </form>
            <table>
                <tr><th>ID</th><th>Nombre</th><th>Correo</th><th>Rol</th><th>Acciones</th></tr>
                <?php foreach ($usuarios['datos'] as $u): ?>
                    <tr>
                        <td><?= h($u['id']) ?></td><td><?= h($u['name']) ?></td>
                        <td><?= h($u['email']) ?></td><td><?= h($u['role']) ?></td>
                        <td>
                            <details><summary>Editar (PUT)</summary>
                                <form method="post">
                                    <input type="hidden" name="csrf" value="<?= h($csrf) ?>">
                                    <input type="hidden" name="accion" value="editar_usuario">
                                    <input type="hidden" name="id" value="<?= h($u['id']) ?>">
                                    <input name="name" value="<?= h($u['name']) ?>" required>
                                    <input type="email" name="email" value="<?= h($u['email']) ?>" required>
                                    <input type="password" name="password" placeholder="Nueva contraseña (opcional)">
                                    <select name="role">
                                        <option value="user" <?= ($u['role'] === 'user') ? 'selected' : '' ?>>user</option>
                                        <option value="admin" <?= ($u['role'] === 'admin') ? 'selected' : '' ?>>admin</option>
                                    </select>
                                    <button>Guardar</button>
                                </form>
                            </details>
                            <form method="post" onsubmit="return confirm('¿Eliminar usuario?')">
                                <input type="hidden" name="csrf" value="<?= h($csrf) ?>">
                                <input type="hidden" name="accion" value="eliminar_usuario">
                                <input type="hidden" name="id" value="<?= h($u['id']) ?>">
                                <button>Eliminar (DELETE)</button>
                            </form>
                        </td>
                    </tr>
                <?php endforeach; ?>
            </table>
            <p class="ayuda">Evita borrar la cuenta administradora con la que estás conectado.</p>
        <?php endif; ?>
    </section>
    <?php else: ?>
        <p class="ayuda">Tu rol es «user»: puedes consultar candidatos, pero no realizar cambios.</p>
    <?php endif; ?>
<?php endif; ?>
</body>
</html>
