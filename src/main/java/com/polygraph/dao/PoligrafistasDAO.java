package com.polygraph.dao;

import com.polygraph.modelo.Poligrafistas;
import com.polygraph.util.ConexionBD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PoligrafistasDAO {

    /**
     * Lista todos los poligrafistas.
     */
    public List<Poligrafistas> obtenerPoligrafistas() throws SQLException {
        List<Poligrafistas> lista = new ArrayList<>();

        String sql = "SELECT Id_Poligrafista, Nombre_Poligrafista, Sala_Encargada " +
                     "FROM poligrafistas " +
                     "ORDER BY Nombre_Poligrafista ASC";

        try (Connection c = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Poligrafistas p = new Poligrafistas(
                        rs.getInt("Id_Poligrafista"),
                        rs.getString("Nombre_Poligrafista"),
                        rs.getString("Sala_Encargada")
                );
                lista.add(p);
            }
        }
        return lista;
    }

    /**
     * Inserta un poligrafista nuevo.
     * Asigna el ID generado al objeto recibido.
     */
    public void insertarPoligrafista(Poligrafistas p) throws SQLException {
        String sql = "INSERT INTO poligrafistas " +
                     "(Nombre_Poligrafista, Sala_Encargada) " +
                     "VALUES (?, ?)";

        try (Connection c = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, p.getNombrePoligrafista());
            ps.setString(2, p.getSalaEncargada());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    p.setIdPoligrafista(rs.getInt(1));
                }
            }
        }
    }

    /**
     * Actualiza un poligrafista existente.
     */
    public void actualizarPoligrafista(Poligrafistas p) throws SQLException {
        String sql = "UPDATE poligrafistas SET " +
                     "Nombre_Poligrafista = ?, " +
                     "Sala_Encargada = ? " +
                     "WHERE Id_Poligrafista = ?";

        try (Connection c = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, p.getNombrePoligrafista());
            ps.setString(2, p.getSalaEncargada());
            ps.setInt(3, p.getIdPoligrafista());

            ps.executeUpdate();
        }
    }

    /**
     * Elimina un poligrafista.
     * OJO: si tiene poligrafías asociadas, por el FK RESTRICT MySQL lanzará una SQLException.
     */
    public void eliminarPoligrafista(int idPoligrafista) throws SQLException {
        String sql = "DELETE FROM poligrafistas WHERE Id_Poligrafista = ?";

        try (Connection c = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, idPoligrafista);
            ps.executeUpdate();
        }
    }

    /**
     * Obtiene un poligrafista por su ID.
     */
    public Poligrafistas obtenerPorId(int idPoligrafista) throws SQLException {
        String sql = "SELECT Id_Poligrafista, Nombre_Poligrafista, Sala_Encargada " +
                     "FROM poligrafistas " +
                     "WHERE Id_Poligrafista = ?";

        try (Connection c = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, idPoligrafista);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Poligrafistas(
                            rs.getInt("Id_Poligrafista"),
                            rs.getString("Nombre_Poligrafista"),
                            rs.getString("Sala_Encargada")
                    );
                }
            }
        }
        return null;
    }
}
