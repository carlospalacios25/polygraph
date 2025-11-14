// src/main/java/com/polygraph/dao/ProcesoTipoProgresoDAO.java
package com.polygraph.dao;

import com.polygraph.modelo.ProcesoTipoProgreso;
import com.polygraph.util.ConexionBD;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProcesoTipoProgresoDAO {

    public void insertar(ProcesoTipoProgreso relacion) throws SQLException {
        String sql = "INSERT INTO proceso_tipos_progreso (Id_Proceso, Id_Tipo_Progreso, Habilitado) VALUES (?, ?, ?)";
        try (Connection conn = ConexionBD.getInstancia().getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, relacion.getIdProceso());
            pstmt.setInt(2, relacion.getIdTipoProgreso());
            pstmt.setBoolean(3, relacion.isHabilitado());
            pstmt.executeUpdate();
        }
    }

    public List<ProcesoTipoProgreso> obtenerPorProceso(int idProceso) throws SQLException {
        List<ProcesoTipoProgreso> lista = new ArrayList<>();
        String sql = """
            SELECT Id, Id_Proceso, Id_Tipo_Progreso, Habilitado
            FROM proceso_tipos_progreso
            WHERE Id_Proceso = ?
            ORDER BY Id_Tipo_Progreso
            """;
        try (Connection conn = ConexionBD.getInstancia().getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idProceso);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(new ProcesoTipoProgreso(
                        rs.getInt("Id"),
                        rs.getInt("Id_Proceso"),
                        rs.getInt("Id_Tipo_Progreso"),
                        rs.getBoolean("Habilitado")
                    ));
                }
            }
        }
        return lista;
    }

    public void actualizarHabilitado(int idRelacion, boolean habilitado) throws SQLException {
        String sql = "UPDATE proceso_tipos_progreso SET Habilitado = ? WHERE Id = ?";
        try (Connection conn = ConexionBD.getInstancia().getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setBoolean(1, habilitado);
            pstmt.setInt(2, idRelacion);
            pstmt.executeUpdate();
        }
    }

    public void eliminar(int idRelacion) throws SQLException {
        String sql = "DELETE FROM proceso_tipos_progreso WHERE Id = ?";
        try (Connection conn = ConexionBD.getInstancia().getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idRelacion);
            pstmt.executeUpdate();
        }
    }
}