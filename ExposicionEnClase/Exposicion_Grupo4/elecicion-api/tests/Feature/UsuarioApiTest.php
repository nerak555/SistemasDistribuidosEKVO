<?php

use App\Models\User;
use Illuminate\Foundation\Testing\RefreshDatabase;
use Illuminate\Support\Facades\Hash;

uses(RefreshDatabase::class);

beforeEach(function () {
    $_ENV['JWT_SECRET'] = $_SERVER['JWT_SECRET'] = str_repeat('test', 8);
    $_ENV['JWT_ALGORITHM'] = $_SERVER['JWT_ALGORITHM'] = 'HS256';
});

test('an administrator can manage users without exposing passwords', function () {
    $admin = User::factory()->create(['role' => 'admin']);
    $token = $this->postJson('/api/login', [
        'email' => $admin->email,
        'password' => 'password',
    ])->assertOk()->json('token');

    $this->withToken($token);

    $created = $this->postJson('/api/usuarios', [
        'name' => 'Nuevo Usuario',
        'email' => 'nuevo@example.com',
        'password' => 'clave-inicial',
        'role' => 'user',
    ])->assertCreated()
        ->assertJsonPath('role', 'user')
        ->assertJsonMissingPath('password')
        ->assertJsonMissingPath('remember_token');

    $id = $created->json('id');
    expect(Hash::check('clave-inicial', User::findOrFail($id)->password))->toBeTrue();

    $this->getJson('/api/usuarios')->assertOk()
        ->assertJsonCount(2)
        ->assertJsonMissingPath('0.password')
        ->assertJsonMissingPath('1.password');

    $this->getJson("/api/usuarios/{$id}")->assertOk()
        ->assertJsonPath('email', 'nuevo@example.com')
        ->assertJsonMissingPath('password');

    $this->putJson("/api/usuarios/{$id}", [
        'name' => 'Usuario Editado',
        'email' => 'editado@example.com',
        'password' => 'clave-nueva',
        'role' => 'admin',
    ])->assertOk()->assertJsonPath('role', 'admin')->assertJsonMissingPath('password');

    $this->assertDatabaseHas('users', [
        'id' => $id,
        'name' => 'Usuario Editado',
        'email' => 'editado@example.com',
        'role' => 'admin',
    ]);
    expect(Hash::check('clave-nueva', User::findOrFail($id)->password))->toBeTrue();

    $this->patchJson("/api/usuarios/{$id}", ['name' => 'Nombre Actualizado'])
        ->assertOk()->assertJsonPath('name', 'Nombre Actualizado');

    $this->deleteJson("/api/usuarios/{$id}")->assertNoContent();
    $this->assertDatabaseMissing('users', ['id' => $id]);
    $this->getJson("/api/usuarios/{$id}")->assertNotFound();
});

test('a regular user can read users but cannot create update or delete them', function () {
    $user = User::factory()->create(['role' => 'user']);
    $token = $this->postJson('/api/login', [
        'email' => $user->email,
        'password' => 'password',
    ])->assertOk()->json('token');

    $this->withToken($token);
    $this->getJson('/api/usuarios')->assertOk();
    $this->getJson("/api/usuarios/{$user->id}")->assertOk();

    $this->postJson('/api/usuarios', [
        'name' => 'No permitido',
        'email' => 'denegado@example.com',
        'password' => 'password',
        'role' => 'admin',
    ])->assertForbidden()->assertJsonPath('message', 'Acceso denegado');

    $this->patchJson("/api/usuarios/{$user->id}", ['role' => 'admin'])->assertForbidden();
    $this->putJson("/api/usuarios/{$user->id}", ['role' => 'admin'])->assertForbidden();
    $this->deleteJson("/api/usuarios/{$user->id}")->assertForbidden();

    $this->assertDatabaseCount('users', 1);
    $this->assertDatabaseHas('users', ['id' => $user->id, 'role' => 'user']);
});

test('user endpoints require a valid token', function (string $authorization) {
    $user = User::factory()->create();
    $this->withHeader('Authorization', $authorization);

    $this->getJson('/api/usuarios')->assertUnauthorized();
    $this->getJson("/api/usuarios/{$user->id}")->assertUnauthorized();
    $this->postJson('/api/usuarios', [])->assertUnauthorized();
    $this->putJson("/api/usuarios/{$user->id}", [])->assertUnauthorized();
    $this->patchJson("/api/usuarios/{$user->id}", [])->assertUnauthorized();
    $this->deleteJson("/api/usuarios/{$user->id}")->assertUnauthorized();
})->with(['missing' => '', 'invalid' => 'Bearer invalid-token']);
