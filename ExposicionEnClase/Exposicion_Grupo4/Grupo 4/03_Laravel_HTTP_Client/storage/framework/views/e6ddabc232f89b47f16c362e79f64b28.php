<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8"><title>Grupo 4 - Laravel</title>
    <style>
        body { font-family: Arial; margin: 25px; }
        table { border-collapse: collapse; width: 100%; margin: 12px 0; }
        td, th { border: 1px solid #aaa; padding: 7px; text-align: left; }
        input, select, button { padding: 5px; margin: 3px; }
        section { margin-top: 35px; }
        .error { color: red; }
    </style>
</head>
<body>
<h1>Grupo 4 - Cliente Laravel</h1>
<p>Hola, <?php echo e(session('nombre')); ?> (<?php echo e(session('rol')); ?>)</p>
<form method="POST" action="/salir"><?php echo csrf_field(); ?> <button>Cerrar sesión</button></form>
<?php if(session('error')): ?> <p class="error"><?php echo e(session('error')); ?></p> <?php endif; ?>
<?php if(session('ok')): ?> <p><?php echo e(session('ok')); ?></p> <?php endif; ?>

<section>
<h2>Candidatos — GET /api/candidatos</h2>
<?php if($candidatos['codigo'] !== 200): ?>
    <p class="error">Error HTTP <?php echo e($candidatos['codigo']); ?>. Revisa la API y el token.</p>
<?php else: ?>
    <?php if(session('rol') === 'admin'): ?>
    <h3>Nuevo candidato (POST)</h3>
    <form method="POST" action="/guardar/candidatos">
        <?php echo csrf_field(); ?>
        <input name="nombres" placeholder="Nombres" required>
        <input name="apellidos" placeholder="Apellidos" required>
        <input name="cargo" placeholder="Cargo" required>
        <input name="ambito" placeholder="Ámbito" required>
        <button>Crear</button>
    </form>
    <?php endif; ?>
    <table>
        <tr><th>ID</th><th>Nombres</th><th>Apellidos</th><th>Cargo</th><th>Ámbito</th><?php if(session('rol') === 'admin'): ?><th>Acciones</th><?php endif; ?></tr>
        <?php $__currentLoopData = $candidatos['datos']; $__env->addLoop($__currentLoopData); foreach($__currentLoopData as $c): $__env->incrementLoopIndices(); $loop = $__env->getLastLoop(); ?>
        <tr>
            <td><?php echo e($c['id']); ?></td><td><?php echo e($c['nombres']); ?></td><td><?php echo e($c['apellidos']); ?></td>
            <td><?php echo e($c['cargo']); ?></td><td><?php echo e($c['ambito']); ?></td>
            <?php if(session('rol') === 'admin'): ?>
            <td>
                <details><summary>Editar (PUT)</summary>
                    <form method="POST" action="/guardar/candidatos">
                        <?php echo csrf_field(); ?>
                        <input type="hidden" name="id" value="<?php echo e($c['id']); ?>">
                        <input name="nombres" value="<?php echo e($c['nombres']); ?>" required>
                        <input name="apellidos" value="<?php echo e($c['apellidos']); ?>" required>
                        <input name="cargo" value="<?php echo e($c['cargo']); ?>" required>
                        <input name="ambito" value="<?php echo e($c['ambito']); ?>" required>
                        <button>Guardar</button>
                    </form>
                </details>
                <form method="POST" action="/eliminar/candidatos/<?php echo e($c['id']); ?>" onsubmit="return confirm('¿Eliminar candidato?')">
                    <?php echo csrf_field(); ?> <button>Eliminar (DELETE)</button>
                </form>
            </td>
            <?php endif; ?>
        </tr>
        <?php endforeach; $__env->popLoop(); $loop = $__env->getLastLoop(); ?>
    </table>
<?php endif; ?>
</section>

<?php if(session('rol') === 'admin'): ?>
<section>
<h2>Usuarios — GET /api/usuarios</h2>
<?php if($usuarios['codigo'] !== 200): ?>
    <p class="error">Error HTTP <?php echo e($usuarios['codigo']); ?>. Revisa el endpoint de usuarios.</p>
<?php else: ?>
    <h3>Nuevo usuario (POST)</h3>
    <form method="POST" action="/guardar/usuarios">
        <?php echo csrf_field(); ?>
        <input name="name" placeholder="Nombre" required>
        <input type="email" name="email" placeholder="Correo" required>
        <input type="password" name="password" placeholder="Contraseña" required>
        <select name="role"><option value="user">user</option><option value="admin">admin</option></select>
        <button>Crear</button>
    </form>
    <table>
        <tr><th>ID</th><th>Nombre</th><th>Correo</th><th>Rol</th><th>Acciones</th></tr>
        <?php $__currentLoopData = $usuarios['datos']; $__env->addLoop($__currentLoopData); foreach($__currentLoopData as $u): $__env->incrementLoopIndices(); $loop = $__env->getLastLoop(); ?>
        <tr>
            <td><?php echo e($u['id']); ?></td><td><?php echo e($u['name']); ?></td><td><?php echo e($u['email']); ?></td><td><?php echo e($u['role']); ?></td>
            <td>
                <details><summary>Editar (PUT)</summary>
                    <form method="POST" action="/guardar/usuarios">
                        <?php echo csrf_field(); ?>
                        <input type="hidden" name="id" value="<?php echo e($u['id']); ?>">
                        <input name="name" value="<?php echo e($u['name']); ?>" required>
                        <input type="email" name="email" value="<?php echo e($u['email']); ?>" required>
                        <input type="password" name="password" placeholder="Nueva contraseña (opcional)">
                        <select name="role">
                            <option value="user" <?php if($u['role'] === 'user'): echo 'selected'; endif; ?>>user</option>
                            <option value="admin" <?php if($u['role'] === 'admin'): echo 'selected'; endif; ?>>admin</option>
                        </select>
                        <button>Guardar</button>
                    </form>
                </details>
                <?php if((int)$u['id'] !== (int)session('id_usuario')): ?>
                <form method="POST" action="/eliminar/usuarios/<?php echo e($u['id']); ?>" onsubmit="return confirm('¿Eliminar usuario?')">
                    <?php echo csrf_field(); ?> <button>Eliminar (DELETE)</button>
                </form>
                <?php endif; ?>
            </td>
        </tr>
        <?php endforeach; $__env->popLoop(); $loop = $__env->getLastLoop(); ?>
    </table>
<?php endif; ?>
</section>
<?php else: ?>
    <p>Tu rol es user: puedes consultar candidatos, pero no editarlos.</p>
<?php endif; ?>
</body>
</html>
<?php /**PATH C:\Users\aaran\Documents\Semestre 2-26\SIS258\SistemasDistribuidos-API\Grupo 4\03_Laravel_HTTP_Client\resources\views/panel.blade.php ENDPATH**/ ?>