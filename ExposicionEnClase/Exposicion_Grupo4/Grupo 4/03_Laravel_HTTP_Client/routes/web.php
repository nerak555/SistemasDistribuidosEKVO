<?php

use Illuminate\Http\Request;
use Illuminate\Support\Facades\Http;
use Illuminate\Support\Facades\Route;
use Illuminate\Http\Client\ConnectionException;

// ¡ESTA ES LA PARTE PRINCIPAL DE LARAVEL HTTP CLIENT!
// Envía HTTP y devuelve código de respuesta y datos JSON.
function api(string $metodo, string $ruta, array $datos = []): array
{
    $cliente = Http::acceptJson()->timeout(15);

    if (session('token')) {
        $cliente = $cliente->withToken(session('token'));
    }

    $url = rtrim(config('services.api.url'), '/') . '/' . $ruta;

    try {
        $respuesta = match ($metodo) {
            'GET'    => $cliente->get($url),
            'POST'   => $cliente->post($url, $datos),
            'PUT'    => $cliente->put($url, $datos),
            'DELETE' => $cliente->delete($url),
        };
        return ['codigo' => $respuesta->status(), 'datos' => $respuesta->json() ?? []];
    } catch (ConnectionException $e) {
        return ['codigo' => 0, 'datos' => ['message' => 'No se pudo conectar: ' . $e->getMessage()]];
    }
}

Route::get('/', fn () => session('token') ? redirect('/panel') : view('login'));

Route::post('/login', function (Request $request) {
    $request->validate(['email' => 'required|email', 'password' => 'required']);
    $r = api('POST', 'login', $request->only('email', 'password'));

    if ($r['codigo'] !== 200 || empty($r['datos']['token'])) {
        return back()->with('error', $r['datos']['mensaje'] ?? $r['datos']['message'] ?? 'Login incorrecto');
    }

    $request->session()->regenerate();
    session([
        'token' => $r['datos']['token'],
        'nombre' => $r['datos']['usuario']['name'] ?? 'Usuario',
        'rol' => $r['datos']['usuario']['role'] ?? 'user',
        'id_usuario' => $r['datos']['usuario']['id'] ?? 0,
    ]);
    return redirect('/panel');
});

Route::post('/salir', function (Request $request) {
    $request->session()->invalidate();
    $request->session()->regenerateToken();
    return redirect('/');
});

Route::get('/panel', function () {
    if (!session('token')) return redirect('/');

    $candidatos = api('GET', 'candidatos');
    $usuarios = session('rol') === 'admin' ? api('GET', 'usuarios') : null;

    return view('panel', compact('candidatos', 'usuarios'));
});

// Un formulario sirve tanto para POST (crear) como PUT (editar).
Route::post('/guardar/{tipo}', function (Request $request, string $tipo) {
    if (!session('token')) return redirect('/');
    if (session('rol') !== 'admin') abort(403);
    if (!in_array($tipo, ['candidatos', 'usuarios'], true)) abort(404);

    $campos = $tipo === 'candidatos'
        ? ['nombres', 'apellidos', 'cargo', 'ambito']
        : ['name', 'email', 'role', 'password'];

    $datos = $request->only($campos);
    if ($tipo === 'usuarios' && empty($datos['password'])) unset($datos['password']);

    // ID vacío = crear. ID con valor = editar.
    $id = (int) $request->input('id', 0);
    $metodo = $id ? 'PUT' : 'POST';
    $ruta = $tipo . ($id ? '/' . $id : '');
    $r = api($metodo, $ruta, $datos);

    if ($r['codigo'] < 200 || $r['codigo'] >= 300) {
        return back()->with('error', 'HTTP ' . $r['codigo'] . ': ' . ($r['datos']['message'] ?? $r['datos']['mensaje'] ?? 'Error del servidor'));
    }
    return redirect('/panel')->with('ok', 'Guardado correctamente');
});

Route::post('/eliminar/{tipo}/{id}', function (string $tipo, int $id) {
    if (!session('token')) return redirect('/');
    if (session('rol') !== 'admin') abort(403);
    if (!in_array($tipo, ['candidatos', 'usuarios'], true)) abort(404);
    if ($tipo === 'usuarios' && $id === (int) session('id_usuario')) {
        return back()->with('error', 'No puedes eliminar tu propia cuenta desde este formulario.');
    }

    $r = api('DELETE', "$tipo/$id");
    if ($r['codigo'] < 200 || $r['codigo'] >= 300) {
        return back()->with('error', 'HTTP ' . $r['codigo'] . ': ' . ($r['datos']['message'] ?? 'Error al eliminar'));
    }
    return redirect('/panel')->with('ok', 'Eliminado correctamente');
});
