package com.polygraph.dao;

import com.polygraph.modelo.Documentos;
import com.polygraph.util.ConexionBD;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DocumentoDAO {

    // Listar documentos por servicio
    public List<Documentos> listarPorServicio(int idServicio) throws SQLException {
        List<Documentos> lista = new ArrayList<>();

        String sql = """
            SELECT Id_Documento,
                   Id_Servicio,
                   Tipo_Documento,
                   Nombre_Archivo,
                   Fecha_Carga,
                   Descripcion,
                   Tamaño_Archivo,
                   Estado_Documento,
                   Fecha_Solicitud,
                   Fecha_Recibido,
                   Habes_Data,
                   Comunicados
            FROM documentos
            WHERE Id_Servicio = ?
            ORDER BY Fecha_Carga DESC
            """;

        try (Connection conn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idServicio);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Documentos d = new Documentos();

                    d.setIdDocumento(rs.getInt("Id_Documento"));
                    d.setIdServicio(rs.getInt("Id_Servicio"));
                    d.setTipoDocumento(rs.getString("Tipo_Documento"));
                    d.setNombreArchivo(rs.getString("Nombre_Archivo"));

                    Timestamp ts = rs.getTimestamp("Fecha_Carga");
                    d.setFechaCarga(ts != null ? ts.toLocalDateTime().toLocalDate() : null);

                    d.setDescripcion(rs.getString("Descripcion"));

                    long size = rs.getLong("Tamaño_Archivo");
                    if (rs.wasNull()) {
                        d.setTamanoArchivo(0);
                    } else {
                        d.setTamanoArchivo(size);
                    }

                    d.setEstadoDocumento(rs.getString("Estado_Documento"));

                    Date fSol = rs.getDate("Fecha_Solicitud");
                    d.setFechaSolicitud(fSol != null ? fSol.toLocalDate() : null);

                    Date fRec = rs.getDate("Fecha_Recibido");
                    d.setFechaRecibido(fRec != null ? fRec.toLocalDate() : null);

                    d.setHabesData(rs.getString("Habes_Data"));
                    d.setComunicados(rs.getString("Comunicados"));

                    lista.add(d);
                }
            }
        }
        return lista;
    }

    // Insertar documento (desde subida de archivo)
    public void insertar(Documentos d) throws SQLException {
        String sql = """
            INSERT INTO documentos
              (Id_Servicio,
               Tipo_Documento,
               Nombre_Archivo,
               Fecha_Carga,
               Descripcion,
               Tamaño_Archivo,
               Estado_Documento,
               Fecha_Solicitud,
               Fecha_Recibido,
               Habes_Data,
               Comunicados)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection conn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, d.getIdServicio());
            ps.setString(2, d.getTipoDocumento());      // 'Informe Poligrafía', 'Otro', etc.
            ps.setString(3, d.getNombreArchivo());

            // Usamos LocalDate y lo convertimos a LocalDateTime a medianoche
            LocalDate fechaCarga = d.getFechaCarga() != null ? d.getFechaCarga() : LocalDate.now();
            ps.setTimestamp(4, Timestamp.valueOf(fechaCarga.atStartOfDay()));

            ps.setString(5, d.getDescripcion());

            if (d.getTamanoArchivo() > 0) {
                ps.setLong(6, d.getTamanoArchivo());
            } else {
                ps.setNull(6, Types.BIGINT);
            }

            ps.setString(7, d.getEstadoDocumento() != null ? d.getEstadoDocumento() : "Activo");

            if (d.getFechaSolicitud() != null) {
                ps.setDate(8, Date.valueOf(d.getFechaSolicitud()));
            } else {
                ps.setNull(8, Types.DATE);
            }

            if (d.getFechaRecibido() != null) {
                ps.setDate(9, Date.valueOf(d.getFechaRecibido()));
            } else {
                ps.setNull(9, Types.DATE);
            }

            ps.setString(10, d.getHabesData());
            ps.setString(11, d.getComunicados());

            ps.executeUpdate();
        }
    }
}
