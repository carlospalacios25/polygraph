package com.polygraph.dao;

import com.polygraph.modelo.Servicio;
import com.polygraph.util.ConexionBD;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ServicioDAO {

    // INSERTAR SERVICIO
    public void insertarServicio(Servicio servicio) throws SQLException {
        String sql = "INSERT INTO servicios (" +
                "Fecha_Solicitud, Hora_Solicitud, Nit_Cliente, Cedula_Candidato, Id_Proceso" +
                ") VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = ConexionBD.getInstancia().getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setObject(1, servicio.getFechaSolicitud());
            pstmt.setObject(2, servicio.getHoraSolicitud());
            pstmt.setLong(3, servicio.getNitCliente());
            pstmt.setLong(4, servicio.getCedulaCandidato());
            pstmt.setInt(5, servicio.getIdProceso());

            int filas = pstmt.executeUpdate();
            if (filas > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        servicio.setIdServicio(rs.getInt(1));
                    }
                }
            }

        } catch (SQLException e) {
            throw new SQLException("Error al insertar servicio: " + e.getMessage(), e);
        }
    }

    public List<Servicio> listarServicios() throws SQLException {
        List<Servicio> servicios = new ArrayList<>();

        String sql = """
            SELECT 
                    s.Id_Servicio,
                    s.Fecha_Solicitud,
                    s.Hora_Solicitud,
                    cli.Nombre_Cliente,
                    can.Nombre_Candidato,
                    can.Apellido_Candidato,
                    p.Nombre_Proceso,
                    s.Estado,
                    s.Resultado
            FROM servicios s
            LEFT JOIN clientes cli     ON s.Nit_Cliente      = cli.Nit_Cliente
            LEFT JOIN candidatos can   ON s.Cedula_Candidato = can.Cedula_Candidato
            LEFT JOIN procesos p       ON s.Id_Proceso       = p.Id_Proceso
            ORDER BY s.Fecha_Solicitud ASC, s.Hora_Solicitud ASC
            """;

        try (Connection conn = ConexionBD.getInstancia().getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Servicio s = new Servicio(
                    rs.getInt("Id_Servicio"),
                    rs.getObject("Fecha_Solicitud", LocalDate.class),
                    rs.getObject("Hora_Solicitud", LocalTime.class),
                    rs.getString("Nombre_Cliente"),
                    rs.getString("Nombre_Candidato"),
                    rs.getString("Apellido_Candidato"),
                    rs.getString("Nombre_Proceso"),
                    rs.getString("Estado"),
                    rs.getString("Resultado")
                );
                servicios.add(s);
            }
        }
        return servicios;
    }

    public List<Servicio> listarServiciosVisita() throws SQLException {
        List<Servicio> servicios = new ArrayList<>();

        String sql = """
            SELECT 
            	s.Id_Servicio,
            	s.Fecha_Solicitud,
            	s.Hora_Solicitud,
            	cli.Nombre_Cliente,
            	can.Nombre_Candidato,
            	can.Apellido_Candidato,
            	p.Nombre_Proceso,
            	s.Estado,
            	s.Resultado
            FROM servicios s
            LEFT JOIN clientes cli     ON s.Nit_Cliente      = cli.Nit_Cliente
            LEFT JOIN candidatos can   ON s.Cedula_Candidato = can.Cedula_Candidato
            LEFT JOIN procesos p       ON s.Id_Proceso       = p.Id_Proceso
            LEFT JOIN proceso_tipos_progreso tp ON s.Id_Proceso  = tp.Id_Proceso
            LEFT JOIN tipos_progreso tpr ON tp.Id_Tipo_Progreso = tpr.Id_Tipo_Progreso
            WHERE  tpr.Nombre_Progreso LIKE '%visita%' and TP.Habilitado = 1
            ORDER BY s.Fecha_Solicitud ASC, s.Hora_Solicitud ASC
            """;

        try (Connection conn = ConexionBD.getInstancia().getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Servicio s = new Servicio(
                    rs.getInt("Id_Servicio"),
                    rs.getObject("Fecha_Solicitud", LocalDate.class),
                    rs.getObject("Hora_Solicitud", LocalTime.class),
                    rs.getString("Nombre_Cliente"),
                    rs.getString("Nombre_Candidato"),
                    rs.getString("Apellido_Candidato"),
                    rs.getString("Nombre_Proceso"),
                    rs.getString("Estado"),
                    rs.getString("Resultado")
                );
                servicios.add(s);
            }
        }
        return servicios;
    }
    // ACTUALIZAR SERVICIO (OPCIONAL)
    public void actualizarServicio(Servicio servicio) throws SQLException {
        String sql = "UPDATE servicios SET " +
                "Fecha_Solicitud = ?, Hora_Solicitud = ?, Nit_Cliente = ?, " +
                "Cedula_Candidato = ?, Id_Proceso = ?, Estado = ?, Resultado = ? " +
                "WHERE Id_Servicio = ?";

        try (Connection conn = ConexionBD.getInstancia().getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setObject(1, servicio.getFechaSolicitud());
            pstmt.setObject(2, servicio.getHoraSolicitud());
            pstmt.setLong(3, servicio.getNitCliente());
            pstmt.setLong(4, servicio.getCedulaCandidato());
            pstmt.setInt(5, servicio.getIdProceso());
            pstmt.setString(6, servicio.getEstado());
            pstmt.setString(7, servicio.getResultado());
            pstmt.setInt(8, servicio.getIdServicio());

            pstmt.executeUpdate();
        }
    }
    
    public boolean servicioRequiereVisita(int idServicio) throws SQLException {
        String sql = """
            SELECT 
                1
            FROM servicios s
            LEFT JOIN proceso_tipos_progreso tp ON s.Id_Proceso  = tp.Id_Proceso
            LEFT JOIN tipos_progreso tpr       ON tp.Id_Tipo_Progreso = tpr.Id_Tipo_Progreso
            WHERE  s.Id_Servicio = ?
               AND tpr.Nombre_Progreso LIKE '%visita%'
               AND tp.Habilitado = 1
            LIMIT 1
            """;

        try (Connection conn = ConexionBD.getInstancia().getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idServicio);

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next(); // true si encontró al menos 1 registro
            }
        }
    }
    
    public static void actualizarEstadoAutomatico(int idServicio) {
        String sql = """
            UPDATE servicios s
            SET Estado = CASE
                WHEN EXISTS (
                    SELECT 1 FROM poligrafias p WHERE p.Id_Servicio = s.Id_Servicio
                    UNION ALL
                    SELECT 1 FROM visitas v WHERE v.Id_Servicio = s.Id_Servicio
                ) AND s.Estado = 'Pendiente' THEN 'Agendado'

                WHEN (
                    SELECT COUNT(*) FROM documentos d 
                    WHERE d.Id_Servicio = s.Id_Servicio 
                      AND d.Estado_Documento = 'Activo'
                      AND d.Tipo_Documento IN ('Informe Poligrafía','Reporte Visita')
                ) >= 2 
                AND EXISTS (SELECT 1 FROM analisis a WHERE a.Id_Servicio = s.Id_Servicio AND a.Tipo_Analisis = 'Final')
                AND s.Estado NOT IN ('Publicado','Cancelado') THEN 'Finalizado'

                WHEN s.Estado = 'Finalizado' THEN 'Publicado'  -- o cuando el cliente lo marque como publicado

                ELSE s.Estado
            END
            WHERE s.Id_Servicio = ?;
            """;

        try (Connection conn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idServicio);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public boolean servicioRequierePoligrafia(int idServicio) throws SQLException {
        String sql = """
            SELECT 
                1
            FROM servicios s
            LEFT JOIN proceso_tipos_progreso tp ON s.Id_Proceso  = tp.Id_Proceso
            LEFT JOIN tipos_progreso tpr       ON tp.Id_Tipo_Progreso = tpr.Id_Tipo_Progreso
            WHERE  s.Id_Servicio = ?
               AND tpr.Nombre_Progreso LIKE '%poligrafia%'
               AND tp.Habilitado = 1
            LIMIT 1
            """;

        try (Connection conn = ConexionBD.getInstancia().getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idServicio);

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next(); // true si encontró al menos 1 registro
            }
        }
    }
    
    public List<Servicio> listarServiciosPoligrafia() throws SQLException {
        List<Servicio> servicios = new ArrayList<>();

        String sql = """
            SELECT 
            	s.Id_Servicio,
            	s.Fecha_Solicitud,
            	s.Hora_Solicitud,
            	cli.Nombre_Cliente,
            	can.Nombre_Candidato,
            	can.Apellido_Candidato,
            	p.Nombre_Proceso,
            	s.Estado,
            	s.Resultado
            FROM servicios s
            LEFT JOIN clientes cli     ON s.Nit_Cliente      = cli.Nit_Cliente
            LEFT JOIN candidatos can   ON s.Cedula_Candidato = can.Cedula_Candidato
            LEFT JOIN procesos p       ON s.Id_Proceso       = p.Id_Proceso
            LEFT JOIN proceso_tipos_progreso tp ON s.Id_Proceso  = tp.Id_Proceso
            LEFT JOIN tipos_progreso tpr ON tp.Id_Tipo_Progreso = tpr.Id_Tipo_Progreso
            WHERE  tpr.Nombre_Progreso LIKE '%poligrafia%' and TP.Habilitado = 1
            ORDER BY s.Fecha_Solicitud ASC, s.Hora_Solicitud ASC
            """;

        try (Connection conn = ConexionBD.getInstancia().getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Servicio s = new Servicio(
                    rs.getInt("Id_Servicio"),
                    rs.getObject("Fecha_Solicitud", LocalDate.class),
                    rs.getObject("Hora_Solicitud", LocalTime.class),
                    rs.getString("Nombre_Cliente"),
                    rs.getString("Nombre_Candidato"),
                    rs.getString("Apellido_Candidato"),
                    rs.getString("Nombre_Proceso"),
                    rs.getString("Estado"),
                    rs.getString("Resultado")
                );
                servicios.add(s);
            }
        }
        return servicios;
    }
    
    public Servicio obtenerServicioCompletoPorId(Long idServicio) {
        String sql = """
            SELECT
                s.Id_Servicio,
                s.Fecha_Solicitud,
                s.Hora_Solicitud,
                s.Estado,
                c.Nit_Cliente,
                c.Nombre_Cliente,
                p.Id_Proceso,
                p.Nombre_Proceso,
                s.Cedula_Candidato,                    -- ← Siempre trae la cédula del servicio
                can.Nombre_Candidato,                  -- ← Puede ser NULL si no existe
                can.Apellido_Candidato,               -- ← Puede ser NULL
                can.Telefono_Candidato,                -- ← Puede ser NULL
                can.Direccion_Candidato,               -- ← Puede ser NULL
                ciu.Nombre_Ciudad                      -- ← Puede ser NULL
            FROM servicios s
            JOIN clientes c ON s.Nit_Cliente = c.Nit_Cliente
            JOIN procesos p ON s.Id_Proceso = p.Id_Proceso
            LEFT JOIN candidatos can ON s.Cedula_Candidato = can.Cedula_Candidato
            LEFT JOIN ciudades ciu ON can.Id_Ciudad = ciu.Id_Ciudad
            WHERE s.Id_Servicio = ?
            """;

        try (Connection conn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, idServicio);  // ← Usa Long, no int (mejor práctica)

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Servicio s = new Servicio();
                    s.setIdServicio(rs.getInt("Id_Servicio"));
                    s.setFechaSolicitud(rs.getObject("Fecha_Solicitud", LocalDate.class));
                    s.setHoraSolicitud(rs.getObject("Hora_Solicitud", LocalTime.class));
                    s.setEstado(rs.getString("Estado"));

                    // Cliente
                    s.setNitCliente(rs.getLong("Nit_Cliente"));
                    s.setNombreCliente(rs.getString("Nombre_Cliente"));

                    // Proceso
                    s.setIdProceso(rs.getInt("Id_Proceso"));
                    s.setNombreProceso(rs.getString("Nombre_Proceso"));

                    // Candidato (puede ser NULL)
                    Long cedula = rs.getObject("Cedula_Candidato", Long.class);
                    s.setCedulaCandidato(cedula);  // ← Siempre trae la cédula del servicio

                    // Estos campos pueden ser NULL si el candidato no existe
                    s.setNombreCandidato(rs.getString("Nombre_Candidato"));
                    s.setApellidoCandidato(rs.getString("Apellido_Candidato"));
                    s.setTelefonoCandidato(rs.getString("Telefono_Candidato"));
                    s.setDireccionCandidato(rs.getString("Direccion_Candidato"));
                    s.setNombreCiudad(rs.getString("Nombre_Ciudad"));

                    return s;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
}