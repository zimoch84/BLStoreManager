/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bricklink.api.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Bartosz
 */
public class OracleConnection {
    
        Connection connection = null;
        Statement stmt = null;
        ResultSet rs=null;

    void connect (){
          System.out.println("-------- Oracle JDBC Connection Testing ------");
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");

        } catch (ClassNotFoundException e) {

            System.out.println("Where is your Oracle JDBC Driver?");
            e.printStackTrace();
            return;
        }

        System.out.println("Oracle JDBC Driver Registered!");
         try {

            connection = DriverManager.getConnection(
                    "jdbc:oracle:thin:@localhost:1521:xe", "BARTEK", "Sabina17");

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
    void closeConnection (){
          try {  
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
