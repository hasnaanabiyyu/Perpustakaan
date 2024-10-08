/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package dbl_uts;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author M. HASNAN AL ABIYYU
 */
public class ConnectDb {

    private Connection connection;
    private static final ConnectDb connectionHelper = new ConnectDb();

    public static ConnectDb getConnectDb() {
        return connectionHelper;
    }

    public Connection getConnection () {
        if (connection ==  null) {
            try {
                final String URL = "jdbc:postgresql://localhost:5432/DBL_UTS";
                final String USER = "postgres";
                final String PASSWORD = "Hasnan270206*";
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Connection established");
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
        return connection;
    }
    
}
