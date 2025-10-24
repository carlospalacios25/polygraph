package com.polygraph.dao;

import com.polygraph.modelo.Clientes;
import com.polygraph.util.ConexionBD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {

    public void insertarUsuario(Clientes cliente) throws SQLException {
        Connection conn = ConexionBD.getInstancia().getConexion();
        String sql = "INSERT INTO clientes (Nit_Cliente, Nombre_Cliente, Telefono_Cliente, Direccion_Cliente, Id_Ciudad) " +
                     "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, cliente.getNitCliente());
            pstmt.setString(2, cliente.getNombreCliente());
            pstmt.setString(3, cliente.getTelefonoCliente());
            pstmt.setString(4, cliente.getDireccionCliente());
            pstmt.setInt(5, cliente.getIdCiudad());
            pstmt.executeUpdate();
        }
    }
    
    public List<Clientes> listadoClientes() {
        List<Clientes> clientes = new ArrayList<>();
        Connection conn = ConexionBD.getInstancia().getConexion();
        if (conn == null) {
            return clientes;
        }
        String sql = "SELECT cli.Nit_Cliente, cli.Nombre_Cliente, cli.Telefono_Cliente, ciu.Nombre_Ciudad " +
                     "FROM clientes AS cli JOIN ciudades AS ciu ON cli.Id_Ciudad = ciu.Id_Ciudad LIMIT 0, 1000;";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Clientes cliente = new Clientes(
                    rs.getLong("Nit_Cliente"),
                    rs.getString("Nombre_Cliente"),
                    rs.getString("Telefono_Cliente"),
                    rs.getString("Nombre_Ciudad")
                );
                clientes.add(cliente);
            }
           
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clientes;
    }
    
    public int actualizarCliente(Clientes cliente) throws SQLException {  // Cambiado a int
        Connection conn = ConexionBD.getInstancia().getConexion();
        String sql = "UPDATE clientes SET Nombre_Cliente = ?, Telefono_Cliente = ?, Direccion_Cliente = ?, Id_Ciudad = ? WHERE Nit_Cliente = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, cliente.getNombreCliente());
            pstmt.setString(2, cliente.getTelefonoCliente());
            pstmt.setString(3, cliente.getDireccionCliente());
            pstmt.setInt(4, cliente.getIdCiudad());
            pstmt.setLong(5, cliente.getNitCliente());
            int filasAfectadas = pstmt.executeUpdate();  // Esto devuelve el número de filas afectadas (int)
            return filasAfectadas;  // Retorna el número de filas actualizadas
        }
    }
    
    public Clientes obtenerClienteCompleto(long nitCliente) {
        Clientes cliente = null;
        Connection conn = ConexionBD.getInstancia().getConexion();
        if (conn == null) {
            return null;
        }
        
        String sql = "SELECT cli.Nit_Cliente, cli.Nombre_Cliente, cli.Telefono_Cliente, " +
                     "cli.Direccion_Cliente, cli.Id_Ciudad, ciu.Nombre_Ciudad " +
                     "FROM clientes AS cli JOIN ciudades AS ciu ON cli.Id_Ciudad = ciu.Id_Ciudad " +
                     "WHERE cli.Nit_Cliente = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, nitCliente);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    cliente = new Clientes(
                        rs.getLong("Nit_Cliente"),
                        rs.getString("Nombre_Cliente"),
                        rs.getString("Telefono_Cliente"),
                        rs.getString("Direccion_Cliente"),
                        rs.getInt("Id_Ciudad")
                    );
                    cliente.setNombreCiudad(rs.getString("Nombre_Ciudad"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cliente;
    }
}