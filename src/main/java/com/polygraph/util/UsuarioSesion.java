package com.polygraph.util;

/**
 * Clase singleton para manejar el usuario que inició sesión.
 * Se usa en todo el sistema para saber quién está registrado.
 */
public class UsuarioSesion {

    // Variable estática que guarda el usuario actual
    private static String usuarioActual = null;

    // Constructor privado → nadie puede hacer new UsuarioSesion()
    private UsuarioSesion() {}

    /**
     * Establece el usuario que inició sesión
     * Llámalo justo después del login exitoso
     */
    public static void setUsuarioActual(String usuario) {
        usuarioActual = usuario;
    }

    /**
     * Obtiene el nombre del usuario actual
     * @return nombre de usuario o null si no hay sesión
     */
    public static String getUsuarioActual() {
        return usuarioActual;
    }

    /**
     * Cierra la sesión (opcional, para logout)
     */
    public static void cerrarSesion() {
        usuarioActual = null;
    }

    /**
     * Verifica si hay un usuario logueado
     */
    public static boolean estaLogueado() {
        return usuarioActual != null && !usuarioActual.trim().isEmpty();
    }
}