package com.bricklink.api.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Piotr
 */
public class MSSQLConnection {
         Connection connection = null;
        Statement stmt = null;
        ResultSet rs=null;

    void connect (){
          System.out.println("-------- MSSQL JDBC Connection Testing ------");
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

        } catch (ClassNotFoundException e) {

            System.out.println("Where is your MSSQL JDBC Driver?");
            e.printStackTrace();
            return;
        }

        System.out.println("MSSQL JDBC Driver Registered!");
         try {

             String connectionUrl = "jdbc:sqlserver://localhost:1433;" +  
                 "databaseName=DBBRICKS;user=javaconnector;password=mulPrt$4;";  
             
            connection = DriverManager.getConnection( connectionUrl);

        } catch (SQLException e) {

            System.out.println("Connection Failed! Check output console");
            e.printStackTrace();
            return;
        }

        if (connection != null) {
            System.out.println("You made it, take control your database now!");
        } else {
            System.out.println("Failed to make connection!");
        }
    }
    
    
    ResultSet sendRequest(String query){
       
        try {
            stmt=connection.createStatement();  
            ////step4 execute query  
            rs=stmt.executeQuery(query);  

        } catch (SQLException e) {

            System.out.println("Connection Failed! Check output console");
            e.printStackTrace();
            

        }//has to be closed with closeConneciton() when used
        return rs;
        
    }
    void disconnect (){
          try {  
              if(stmt != null)
                if(!stmt.isClosed())
                    stmt.close();
              connection.close();
          } catch (SQLException ex) {
              Logger.getLogger(OracleConnection.class.getName()).log(Level.SEVERE, null, ex);
          }
    }
    Connection getConnection(){
        return connection;
    }
}
