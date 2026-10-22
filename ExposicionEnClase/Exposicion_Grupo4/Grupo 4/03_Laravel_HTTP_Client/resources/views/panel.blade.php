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
<p>Hola, {{ session('nombre') }} ({{ session('rol') }})</p>
<form method="POST" action="/salir">@csrf <button>Cerrar sesión</button></form>
@if(session('error')) <p class="error">{{ session('error') }}</p> @endif
@if(session('ok')) <p>{{ session('ok') }}</p> @endif

<section>
<h2>Candidatos — GET /api/candidatos</h2>
@if($candidatos['codigo'] !== 200)
    <p class="error">Error HTTP {{ $candidatos['codigo'] }}. Revisa la API y el token.</p>
@else
    @if(session('rol') === 'admin')
    <h3>Nuevo candidato (POST)</h3>
    <form method="POST" action="/guardar/candidatos">
        @csrf
        <input name="nombres" placeholder="Nombres" required>
        <input name="apellidos" placeholder="Apellidos" required>
        <input name="cargo" placeholder="Cargo" required>
        <input name="ambito" placeholder="Ámbito" required>
        <button>Crear</button>
    </form>
    @endif
    <table>
        <tr><th>ID</th><th>Nombres</th><th>Apellidos</th><th>Cargo</th><th>Ámbito</th>@if(session('rol') === 'admin')<th>Acciones</th>@endif</tr>
        @foreach($candidatos['datos'] as $c)
        <tr>
            <td>{{ $c['id'] }}</td><td>{{ $c['nombres'] }}</td><td>{{ $c['apellidos'] }}</td>
            <td>{{ $c['cargo'] }}</td><td>{{ $c['ambito'] }}</td>
            @if(session('rol') === 'admin')
            <td>
                <details><summary>Editar (PUT)</summary>
                    <form method="POST" action="/guardar/candidatos">
                        @csrf
                        <input type="hidden" name="id" value="{{ $c['id'] }}">
                        <input name="nombres" value="{{ $c['nombres'] }}" required>
                        <input name="apellidos" value="{{ $c['apellidos'] }}" required>
                        <input name="cargo" value="{{ $c['cargo'] }}" required>
                        <input name="ambito" value="{{ $c['ambito'] }}" required>
                        <button>Guardar</button>
                    </form>
                </details>
                <form method="POST" action="/eliminar/candidatos/{{ $c['id'] }}" onsubmit="return confirm('¿Eliminar candidato?')">
                    @csrf <button>Eliminar (DELETE)</button>
                </form>
            </td>
            @endif
        </tr>
        @endforeach
    </table>
@endif
</section>

@if(session('rol') === 'admin')
<section>
<h2>Usuarios — GET /api/usuarios</h2>
@if($usuarios['codigo'] !== 200)
    <p class="error">Error HTTP {{ $usuarios['codigo'] }}. Revisa el endpoint de usuarios.</p>
@else
    <h3>Nuevo usuario (POST)</h3>
    <form method="POST" action="/guardar/usuarios">
        @csrf
        <input name="name" placeholder="Nombre" required>
        <input type="email" name="email" placeholder="Correo" required>
        <input type="password" name="password" placeholder="Contraseña" required>
        <select name="role"><option value="user">user</option><option value="admin">admin</option></select>
        <button>Crear</button>
    </form>
    <table>
        <tr><th>ID</th><th>Nombre</th><th>Correo</th><th>Rol</th><th>Acciones</th></tr>
        @foreach($usuarios['datos'] as $u)
        <tr>
            <td>{{ $u['id'] }}</td><td>{{ $u['name'] }}</td><td>{{ $u['email'] }}</td><td>{{ $u['role'] }}</td>
            <td>
                <details><summary>Editar (PUT)</summary>
                    <form method="POST" action="/guardar/usuarios">
                        @csrf
                        <input type="hidden" name="id" value="{{ $u['id'] }}">
                        <input name="name" value="{{ $u['name'] }}" required>
                        <input type="email" name="email" value="{{ $u['email'] }}" required>
                        <input type="password" name="password" placeholder="Nueva contraseña (opcional)">
                        <select name="role">
                            <option value="user" @selected($u['role'] === 'user')>user</option>
                            <option value="admin" @selected($u['role'] === 'admin')>admin</option>
                        </select>
                        <button>Guardar</button>
                    </form>
                </details>
                @if((int)$u['id'] !== (int)session('id_usuario'))
                <form method="POST" action="/eliminar/usuarios/{{ $u['id'] }}" onsubmit="return confirm('¿Eliminar usuario?')">
                    @csrf <button>Eliminar (DELETE)</button>
                </form>
                @endif
            </td>
        </tr>
        @endforeach
    </table>
@endif
</section>
@else
    <p>Tu rol es user: puedes consultar candidatos, pero no editarlos.</p>
@endif
</body>
</html>
