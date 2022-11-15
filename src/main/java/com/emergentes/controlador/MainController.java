package com.emergentes.controlador;

import com.emergentes.modelo.Libro;
import com.emergentes.utiles.ConexionDB;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "MainController", urlPatterns = {"/MainController"})
public class MainController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String op;
        op = (request.getParameter("op") != null) ? request.getParameter("op") : "list";
        ArrayList<Libro> lista = new ArrayList<Libro>();
        ConexionDB canal = new ConexionDB();
        Connection conn = canal.conectar();
        PreparedStatement ps;
        ResultSet rs;
        if (op.equals("list")) {
            try {
                //Para listar los datos
                String sql = "select * from libros";
                //consulta de seleccion y almacenamiento en una coleccion
                ps = conn.prepareStatement(sql);
                rs = ps.executeQuery();

                while (rs.next()) {
                    Libro lib = new Libro();
                    lib.setId(rs.getInt("id"));
                    lib.setIsbn(rs.getString("isbn"));
                    lib.setTitulo(rs.getString("titulo"));
                    lib.setCategoria(rs.getString("categoria"));
                    lista.add(lib);
                }
                request.setAttribute("lista", lista);
                //enviar al index para mostrar la informacion
                request.getRequestDispatcher("index.jsp").forward(request, response);
            } catch (SQLException ex) {
                System.out.println("error en sql" + ex.getMessage());
            } finally {
                canal.desconectar();
            }

        }
        if (op.equals("nuevo")) {

            //instanciar un objeto de la calse libro
            Libro lib = new Libro();
            System.out.println(lib.toString());
            //el objeto se pone como atributo de request
            request.setAttribute("libro", lib);
            //redireccionar a editar
            request.getRequestDispatcher("editar.jsp").forward(request, response);

        }
        if (op.equals("editar")) {
            try {
                int id = Integer.parseInt(request.getParameter("id"));
                String sql = "select * from libros where id = ?";

                ps = conn.prepareStatement(sql);
                ps.setInt(1, id);
                rs = ps.executeQuery();
                 Libro li = new Libro();
                while (rs.next()) {
                   
                    li.setId(rs.getInt("id"));
                    li.setIsbn(rs.getString("isbn"));
                    li.setTitulo(rs.getString("titulo"));
                    li.setCategoria(rs.getString("categoria"));
                    

                }
                request.setAttribute("libro", li); 

                request.getRequestDispatcher("editar.jsp").forward(request, response);

            } catch (SQLException ex) {
                System.out.println("error en sql" + ex.getMessage());
            } finally {
                canal.desconectar();
            }

        }
        if (op.equals("eliminar")) {
            try {
                //obtener el id
                int id = Integer.parseInt(request.getParameter("id"));
                //realizar la eliminacion en la base de datos
                String sql = "delete from libros where id = ?";
                ps = conn.prepareStatement(sql);
                ps.setInt(1, id);
                ps.executeUpdate();

            } catch (SQLException ex) {
                System.out.println("error de sql " + ex.getMessage());
            } finally {
                canal.desconectar();
            }
            //redireccionar a maincontroller
            response.sendRedirect("MainController");

        }

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int id = Integer.parseInt(request.getParameter("id"));

        String isbn = request.getParameter("isbn");
        String titulo = request.getParameter("titulo");
        String categoria = request.getParameter("categoria");

        Libro lib = new Libro();
        lib.setId(id);
        lib.setIsbn(isbn);
        lib.setTitulo(titulo);
        lib.setCategoria(categoria);

        ConexionDB canal = new ConexionDB();
        Connection conn = canal.conectar();
        PreparedStatement ps;
        ResultSet rs;

        if (id == 0) {

            //nuevo registro
            String sql = "insert into libros (isbn, titulo, categoria) values (?,?,?)";
            try {
                ps = conn.prepareStatement(sql);
                ps.setString(1, lib.getIsbn());
                ps.setString(2, lib.getTitulo());
                ps.setString(3, lib.getCategoria());
                ps.executeUpdate();

            } catch (SQLException ex) {
                System.out.println("error de sql" + ex.getMessage());
            } finally {
                canal.desconectar();
            }
            response.sendRedirect("MainController");

        } else {

            //edicion de registro
            String sql = "update libros set isbn = ?, titulo = ?, categoria = ? where id = ?";
            try {
                ps = conn.prepareStatement(sql);

                ps.setString(1, lib.getIsbn());
                ps.setString(2, lib.getTitulo());
                ps.setString(3, lib.getCategoria());
                ps.setInt(4, lib.getId());
                ps.executeUpdate();

            } catch (SQLException ex) {
                System.out.println("error en sql " + ex.getMessage());
            } finally {
                canal.desconectar();
            }
            response.sendRedirect("MainController");
        }

    }
}
