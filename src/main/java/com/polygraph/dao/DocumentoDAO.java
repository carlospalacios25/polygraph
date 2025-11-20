package com.polygraph.dao;

import com.polygraph.modelo.Documentos;
import com.polygraph.util.ConexionBD;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DocumentoDAO {

    public List<Documentos> listarPorServicio(int idServicio) throws SQLException {
        List<Documentos> lista = new ArrayList<>();
        String sql = """
            SELECT Id_Documento, Tipo_Documento, Nombre_Archivo, Fecha_Carga,
                   Estado_Documento, Tamaño_Archivo, Descripcion, Fecha_Recibido
            FROM documentos
            WHERE Id_Servicio = ?
            ORDER BY Fecha_Carga DESC
            """;

        try (Connection c = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, idServicio);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Documentos d = new Documentos();
                    d.setIdDocumento(rs.getInt("Id_Documento"));
                    d.setTipoDocumento(rs.getString("Tipo_Documento"));
                    d.setNombreArchivo(rs.getString("Nombre_Archivo"));

                    // CORREGIDO: Fecha_Carga es DATETIME → usar Timestamp → LocalDateTime
                    Timestamp timestamp = rs.getTimestamp("Fecha_Carga");
                    if (timestamp != null) {
                        d.setFechaCarga(timestamp.toLocalDateTime());  // AHORA SÍ COMPATIBLE
                    }

                    d.setEstadoDocumento(rs.getString("Estado_Documento"));
                    d.setTamanoArchivo(rs.getLong("Tamaño_Archivo"));
                    d.setDescripcion(rs.getString("Descripcion"));

                    // Fecha_Recibido es DATE → LocalDate
                    java.sql.Date fechaRec = rs.getDate("Fecha_Recibido");
                    d.setFechaRecibido(fechaRec != null ? fechaRec.toLocalDate() : null);

                    d.setIdServicio(idServicio);
                    lista.add(d);
                }
            }
        }
        return lista;
    }

    public void insertar(Documentos d) throws SQLException {
        String sql = """
            INSERT INTO documentos 
            (Id_Servicio, Tipo_Documento, Nombre_Archivo, Fecha_Carga, 
             Descripcion, Tamaño_Archivo, Estado_Documento, 
             Fecha_Solicitud, Fecha_Recibido, Habes_Data, Comunicados)
            VALUES (?, ?, ?, NOW(), ?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection c = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, d.getIdServicio());
            ps.setString(2, d.getTipoDocumento());
            ps.setString(3, d.getNombreArchivo());
            ps.setString(4, d.getDescripcion());
            ps.setLong(5, d.getTamanoArchivo());
            ps.setString(6, d.getEstadoDocumento());
            ps.setDate(7, java.sql.Date.valueOf(d.getFechaSolicitud()));

            if (d.getFechaRecibido() != null) ps.setDate(8, java.sql.Date.valueOf(d.getFechaRecibido()));
            else ps.setNull(8, java.sql.Types.DATE);

            ps.setString(9, d.getHabesData());
            ps.setString(10, d.getComunicados());

            ps.executeUpdate();
        }
    }
}
