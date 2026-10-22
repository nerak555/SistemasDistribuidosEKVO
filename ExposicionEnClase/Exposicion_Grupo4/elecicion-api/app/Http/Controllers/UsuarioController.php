<?php

namespace App\Http\Controllers;

use App\Models\User;
use Illuminate\Http\Request;

class UsuarioController extends Controller
{
    public function index()
    {
        $usuarios = User::all();
        return response()->json($usuarios, 200);
    }

    public function store(Request $request)
    {
        if ($request->attributes->get('user')->role !== 'admin') {
            return response()->json(['message' => 'Acceso denegado'], 403);
        }

        $usuario = User::create($request->all());
        return response()->json($usuario, 201);
    }

    public function show(User $usuario)
    {
        return response()->json($usuario, 200);
    }

    public function update(Request $request, User $usuario)
    {
        if ($request->attributes->get('user')->role !== 'admin') {
            return response()->json(['message' => 'Acceso denegado'], 403);
        }

        $usuario->update($request->all());
        return response()->json($usuario, 200);
    }

    public function destroy(Request $request, User $usuario)
    {
        if ($request->attributes->get('user')->role !== 'admin') {
            return response()->json(['message' => 'Acceso denegado'], 403);
        }

        $usuario->delete();
        return response()->json($usuario, 204);
    }
}
