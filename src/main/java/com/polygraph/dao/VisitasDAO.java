package com.polygraph.dao;

import com.polygraph.modelo.Visitas;
import com.polygraph.util.ConexionBD;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class VisitasDAO {

    public void insertarVisita(Visitas v) throws SQLException {
        String sql = "INSERT INTO visitas (Id_Servicio, Id_Visitador, Tipo_Prueba, Tipo_Visita, " +
                     "Fecha_Solicitud, Fecha_Visita, Hora_Visita, Fecha_Envio_Informe, Novedad_Visita) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection c = ConexionBD.getInstancia().getConexion();
             PreparedStatement p = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            p.setInt(1, v.getIdServicio());
            p.setInt(2, v.getIdVisitador());
            p.setString(3, v.getTipo_Prueba());
            p.setString(4, v.getTipo_Visita());
            p.setDate(5, Date.valueOf(v.getFechaSolicitud()));
            p.setDate(6, Date.valueOf(v.getFechaVisita()));
            p.setTime(7, Time.valueOf(v.getHoraVisita()));
            p.setDate(8, Date.valueOf(v.getFechaeInforme()));
            p.setString(9, v.getNovedadVisita());
            p.executeUpdate();

            try (ResultSet rs = p.getGeneratedKeys()) {
                if (rs.next()) {
                    v.setIdVisita(rs.getInt(1));
                }
            }
        }
    }
    
    public void actualizarVisita(Visitas v) throws SQLException {
        String sql = "UPDATE visitas SET " +
                     "Id_Servicio = ?, Id_Visitador = ?, Tipo_Prueba = ?, Tipo_Visita = ?, " +
                     "Fecha_Solicitud = ?, Fecha_Visita = ?, Hora_Visita = ?, " +
                     "Fecha_Envio_Informe = ?, Novedad_Visita = ? " +
                     "WHERE Id_Visita = ?";

        try (Connection c = ConexionBD.getInstancia().getConexion();
             PreparedStatement p = c.prepareStatement(sql)) {

            p.setInt(1, v.getIdServicio());
            p.setInt(2, v.getIdVisitador());
            p.setString(3, v.getTipo_Prueba());
            p.setString(4, v.getTipo_Visita());
            p.setDate(5, Date.valueOf(v.getFechaSolicitud()));
            p.setDate(6, Date.valueOf(v.getFechaVisita()));
            p.setTime(7, Time.valueOf(v.getHoraVisita()));
            p.setDate(8, Date.valueOf(v.getFechaeInforme()));
            p.setString(9, v.getNovedadVisita());
            p.setInt(10, v.getIdVisita());

            p.executeUpdate();
        }
    }
    
    public List<Visitas> listarVisitas() throws SQLException {
        List<Visitas> lista = new ArrayList<>();
        String sql = """
            SELECT v.*, s.Nombre_Servicio, vis.Nombre_Visitador 
            FROM visitas v
            LEFT JOIN servicios s ON v.Id_Servicio = s.Id_Servicio
            LEFT JOIN visitadores vis ON v.Id_Visitador = vis.Id_Visitador
            ORDER BY v.Fecha_Visita DESC
            """;
        try (Connection c = ConexionBD.getInstancia().getConexion();
             PreparedStatement p = c.prepareStatement(sql);
             ResultSet rs = p.executeQuery()) {
            while (rs.next()) {
                Visitas v = new Visitas(
                    rs.getInt("Id_Visita"),
                    rs.getInt("Id_Servicio"),
                    rs.getInt("Id_Visitador"),
                    rs.getString("Tipo_Prueba"),
                    rs.getString("Tipo_Visita"),
                    rs.getDate("Fecha_Solicitud").toLocalDate(),
                    rs.getDate("Fecha_Visita").toLocalDate(),
                    rs.getTime("Hora_Visita").toLocalTime(),
                    rs.getDate("Fecha_Envio_Informe").toLocalDate(),
                    rs.getString("Novedad_Visita")
                );
                v.setNombreVisitador(rs.getString("Nombre_Visitador"));
                lista.add(v);
            }
        }
        return lista;
    }
    
    public Visitas obtenerVisitaPorServicio(int idServicio) throws SQLException {
        String sql = "SELECT v.Id_Visita, v.Id_Servicio, v.Id_Visitador, " +
                     "       v.Tipo_Prueba, v.Tipo_Visita, " +
                     "       v.Fecha_Solicitud, v.Fecha_Visita, v.Hora_Visita, " +
                     "       v.Fecha_Envio_Informe, v.Novedad_Visita, " +
                     "       vis.Nombre_Visitador " +
                     "FROM visitas AS v " +
                     "JOIN visitadores AS vis ON v.Id_Visitador = vis.Id_Visitador " +
                     "WHERE v.Id_Servicio = ? " +
                     "ORDER BY v.Fecha_Visita DESC " +
                     "LIMIT 1";

        try (Connection c = ConexionBD.getInstancia().getConexion();
             PreparedStatement p = c.prepareStatement(sql)) {

            p.setInt(1, idServicio);

            try (ResultSet rs = p.executeQuery()) {
                if (rs.next()) {
                    Visitas v = new Visitas(
                            rs.getInt("Id_Visita"),
                            rs.getInt("Id_Servicio"),
                            rs.getInt("Id_Visitador"),
                            rs.getString("Tipo_Prueba"),
                            rs.getString("Tipo_Visita"),
                            rs.getDate("Fecha_Solicitud").toLocalDate(),
                            rs.getDate("Fecha_Visita").toLocalDate(),
                            rs.getTime("Hora_Visita").toLocalTime(),
                            rs.getDate("Fecha_Envio_Informe").toLocalDate(),
                            rs.getString("Novedad_Visita")
                    );
                    v.setNombreVisitador(rs.getString("Nombre_Visitador"));
                    return v;
                }
            }
        }

        return null;
    }

}