package com.backend.test;

import com.backend.user.Usuario;
import org.h2.Driver;

import java.sql.*;

public class Test {
    private static final String SQL_TABLE_CREATE = "DROP TABLE IF EXISTS USUARIO; CREATE TABLE USUARIO"
            +"("
            +"ID INT PRIMARY KEY,"
            +"NOMBRE varchar(100) NOT NULL,"
            +"EMAIL varchar(100) NOT NULL,"
            +"SUELDO numeric(15, 2) NOT NULL,"
            +")";
    private static final String SQL_INSERT ="INSERT INTO USUARIO(ID, NOMBRE, EMAIL, SUELDO) VALUES (?,?,?,?)";
    private static final String SQL_UPDATE = "UPDATE USUARIO SET SUELDO=? WHERE EMAIL=?";

    public static void main(String[] args) throws Exception {
        Usuario usuario = new Usuario("Jaimito", "jaimito@perez.com", 10d);

        Connection connection = null;

        try{

            connection = getConnection();
            Statement statement = connection.createStatement();
            statement.execute(SQL_TABLE_CREATE);

            PreparedStatement psInsert = connection.prepareStatement(SQL_INSERT);

            //Empiezo a insertar en la base de datos

            psInsert.setInt(1, 1);
            psInsert.setString(2, usuario.getNombre());
            psInsert.setString(3, usuario.getEmail());
            psInsert.setDouble(4, usuario.getSueldo());

            psInsert.execute(); // Hasta aqui insertamos en la base de datos al usuario pedro con un sueldo de 10

            //Empezamos la transacción
            connection.setAutoCommit(false);

            PreparedStatement psUpdate = connection.prepareStatement(SQL_UPDATE);

            psUpdate.setDouble(1, usuario.subirSueldo(10d));
            psUpdate.setString(2, usuario.getEmail());

            psUpdate.execute();
            int a = 4/0; //Hacemos esto para generar error

            connection.commit();
            connection.setAutoCommit(true);

            String sql = "SELECT * FROM USUARIO";
            Statement stmt = connection.createStatement();
            ResultSet rd = stmt.executeQuery(sql);
            while(rd.next()){
                System.out.println(rd.getInt(1) + rd.getString(2) + rd.getString(3) + rd.getDouble(4));
            }

        } catch (Exception e){

            e.printStackTrace();
            connection.rollback();
        } finally {

            connection.close();
        }

        Connection connection1 = getConnection();
        String sql = "SELECT * FROM USUARIO";
        Statement stmt = connection1.createStatement();
        ResultSet rd = stmt.executeQuery(sql);
        while(rd.next()){
            System.out.println(rd.getInt(1) + rd.getString(2) + rd.getString(3) + rd.getDouble(4));
        }
    }
    public static Connection getConnection() throws Exception{
        Class.forName("org.h2.Driver").newInstance();
        return DriverManager.getConnection("jdbc:h2:"+"./Database/my"+"root"+"myPassword");
    }
}
