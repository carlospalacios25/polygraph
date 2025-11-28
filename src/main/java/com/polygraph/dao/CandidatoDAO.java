package com.polygraph.dao;

import com.polygraph.modelo.Candidatos;
import com.polygraph.modelo.Clientes;
import com.polygraph.util.ConexionBD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CandidatoDAO {

   public void insertarCandidato(Candidatos candidatos) throws SQLException {
        Connection conn = ConexionBD.getInstancia().getConexion();
        String sql = "INSERT INTO candidatos (Cedula_Candidato, "
                + "Nombre_Candidato, "
                + "Apellido_Candidato,"
                + "Telefono_Candidato, "
                + "Direccion_Candidato,"
                + "Id_Ciudad) "
                + "VALUES (?, ?, ?, ?, ?,?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, candidatos.getCedulaCandidato());
            pstmt.setString(2, candidatos.getNombreCandidato());
            pstmt.setString(3, candidatos.getApellidoCandidato());
            pstmt.setString(4, candidatos.getTelefonoCandidato());
            pstmt.setString(5, candidatos.getDireccionCandidato());
            pstmt.setInt(6, candidatos.getIdCiudad());
            pstmt.executeUpdate();
        }
    }
    
    public List<Candidatos> listadoCandidatos() {
        List<Candidatos> candidatos = new ArrayList<>();
        Connection conn = ConexionBD.getInstancia().getConexion();
        if (conn == null) {
            return candidatos;
        }

        String sql = "SELECT can.Cedula_Candidato, " +
                     "CONCAT(can.Nombre_Candidato, ' ', can.Apellido_Candidato) AS nombre_completo, " +
                     "can.Telefono_Candidato, " +
                     "can.Direccion_Candidato, " +
                     "ciu.Nombre_Ciudad " +
                     "FROM candidatos AS can " +
                     "JOIN ciudades AS ciu ON can.Id_Ciudad = ciu.Id_Ciudad";

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                candidatos.add(new Candidatos(
                    rs.getLong("Cedula_Candidato"),
                    rs.getString("nombre_completo"),
                    rs.getString("Telefono_Candidato"),
                    rs.getString("Direccion_Candidato"),
                    rs.getString("Nombre_Ciudad")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return candidatos;
    }
    
    public int actualizarCandidato(Candidatos candidatos) throws SQLException { 
        Connection conn = ConexionBD.getInstancia().getConexion();
        String sql = "UPDATE candidatos SET Nombre_Candidato = ?, "
                + "Apellido_Candidato = ?, "
                + "Telefono_Candidato = ?, "
                + "Direccion_Candidato = ?, "
                + "Id_Ciudad = ? "
                + "WHERE Cedula_Candidato = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, candidatos.getNombreCandidato());
            pstmt.setString(2, candidatos.getApellidoCandidato());
            pstmt.setString(3, candidatos.getTelefonoCandidato());
            pstmt.setString(4, candidatos.getDireccionCandidato());
            pstmt.setInt(5, candidatos.getIdCiudad());
            pstmt.setLong(6, candidatos.getCedulaCandidato());
            int filasAfectadas = pstmt.executeUpdate();  // Esto devuelve el número de filas afectadas (int)
            return filasAfectadas;  // Retorna el número de filas actualizadas
        }
    }
    
    public Candidatos obtenerCandidatoCompleto(long cedulaCandidato) {
        String sql = "SELECT can.Cedula_Candidato, can.Nombre_Candidato, can.Apellido_Candidato, " +
                     "can.Telefono_Candidato, can.Direccion_Candidato, can.Id_Ciudad, ciu.Nombre_Ciudad " +
                     "FROM candidatos can " +
                     "JOIN ciudades ciu ON can.Id_Ciudad = ciu.Id_Ciudad " +
                     "WHERE can.Cedula_Candidato = ?";

        try (Connection conn = ConexionBD.getInstancia().getConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, cedulaCandidato);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Candidatos c = new Candidatos(
                        rs.getLong("Cedula_Candidato"),
                        rs.getString("Nombre_Candidato"),
                        rs.getString("Apellido_Candidato"),
                        rs.getString("Telefono_Candidato"),
                        rs.getString("Direccion_Candidato"),
                        rs.getInt("Id_Ciudad")
                    );
                    c.setNombreCiudad(rs.getString("Nombre_Ciudad"));
                    return c;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Candidatos> obtenerCandidatosBox() throws SQLException {
        List<Candidatos> candidatos = new ArrayList<>();
        Connection conn = ConexionBD.getInstancia().getConexion();
        String sql = "SELECT Cedula_Candidato, CONCAT(Nombre_Candidato, ' ', Apellido_Candidato) AS nombre_completo"
                + " FROM Candidatos";

        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                candidatos.add(new Candidatos(
                    rs.getInt("Cedula_Candidato"),
                    rs.getString("nombre_completo")
                ));
            }
        }
        return candidatos;
    }
    

        /**
     * Obtiene TODOS los datos del candidato desde el servicio (con LEFT JOIN)
     * Incluye: cédula, nombre, apellido, teléfono, dirección y NOMBRE de ciudad
     * Funciona aunque el candidato NO exista aún (trae datos del servicio)
     */
    public Candidatos obtenerCandidatoDesdeServicio(long idServicio) {
        String sql = """
            SELECT 
                can.Cedula_Candidato,
                can.Nombre_Candidato,
                can.Apellido_Candidato,
                can.Telefono_Candidato,
                can.Direccion_Candidato,
                ciu.Nombre_Ciudad,
                can.Id_Ciudad
            FROM servicios ser
            LEFT JOIN candidatos can ON can.Cedula_Candidato = ser.Cedula_Candidato
            LEFT JOIN ciudades ciu ON ciu.Id_Ciudad = can.Id_Ciudad
            WHERE ser.Id_Servicio = ?
            """;

        try (Connection conn = ConexionBD.getInstancia().getConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, idServicio);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Siempre habrá cédula (del servicio)
                    long cedula = rs.getLong("Cedula_Candidato");

                    // Nombre y apellido: si existe en candidatos → usa esos, si no → del servicio
                    String nombre = rs.getString("Nombre_Candidato");
                    String apellido = rs.getString("Apellido_Candidato");

                    // Estos pueden ser NULL si no existe candidato
                    String telefono = rs.getString("Telefono_Candidato");
                    String direccion = rs.getString("Direccion_Candidato");
                    String nombreCiudad = rs.getString("Nombre_Ciudad");
                    int idCiudad = rs.getInt("Id_Ciudad");

                    // Si no hay candidato, idCiudad será 0
                    if (rs.wasNull()) {
                        idCiudad = -1;
                        nombreCiudad = null;
                    }

                    Candidatos c = new Candidatos(cedula, nombre, apellido, telefono, direccion, idCiudad);
                    c.setNombreCiudad(nombreCiudad); // ← importante
                    return c;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}