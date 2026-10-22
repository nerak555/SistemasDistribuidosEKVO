<!DOCTYPE html>
<html lang="es">
<head><meta charset="UTF-8"><title>Grupo 4 - Laravel</title></head>
<body style="font-family:Arial; margin:30px">
    <h1>Login (Laravel HTTP Client)</h1>
    @if(session('error')) <p style="color:red">{{ session('error') }}</p> @endif
    @if($errors->any()) <p style="color:red">{{ $errors->first() }}</p> @endif
    <form method="POST" action="/login">
        @csrf
        <input type="email" name="email" placeholder="Correo" required>
        <input type="password" name="password" placeholder="Contraseña" required>
        <button>Entrar</button>
    </form>
    <p>Datos de prueba: test@example.com / password</p>
</body>
</html>
