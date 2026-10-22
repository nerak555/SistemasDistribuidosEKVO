<!DOCTYPE html>
<html lang="es">
<head><meta charset="UTF-8"><title>Grupo 4 - Laravel</title></head>
<body style="font-family:Arial; margin:30px">
    <h1>Login (Laravel HTTP Client)</h1>
    <?php if(session('error')): ?> <p style="color:red"><?php echo e(session('error')); ?></p> <?php endif; ?>
    <?php if($errors->any()): ?> <p style="color:red"><?php echo e($errors->first()); ?></p> <?php endif; ?>
    <form method="POST" action="/login">
        <?php echo csrf_field(); ?>
        <input type="email" name="email" placeholder="Correo" required>
        <input type="password" name="password" placeholder="Contraseña" required>
        <button>Entrar</button>
    </form>
    <p>Datos de prueba: test@example.com / password</p>
</body>
</html>
<?php /**PATH C:\Users\aaran\Documents\Semestre 2-26\SIS258\SistemasDistribuidos-API\Grupo 4\03_Laravel_HTTP_Client\resources\views/login.blade.php ENDPATH**/ ?>