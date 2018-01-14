/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bricklink.api.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import static java.util.Collections.list;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.HttpsURLConnection;
import org.json.JSONArray;

import org.json.JSONObject;
import org.json.simple.parser.JSONParser;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;



/**
 *
 * @author Bartosz
 */
public class ZimochBricks {

    HashMap<Integer, Object> Inventory = new HashMap<Integer, Object>();
    
   public static void main( String[] args ) {
       OracleConnection oracleConnection = new OracleConnection();
          
       BLAPIs blapis = new BLAPIs();
       
      // JSONObject responseItem=blapis.brickLinkApiItem("part", "2335", "11", "stock", "U", "PL", "EU");
     JSONObject responseInventory = blapis.brickLinkApiInventory();
       
      // System.err.println(responseInventory.toString());
       
       oracleConnection.connect();
       OracleAPIs oracleApis = new OracleAPIs(oracleConnection);
       //oracleApis.refreshInventoryDatabase(responseInventory);
        //oracleApis.getAllInventory();
       oracleApis.setPricesByInventory(blapis);
   }
    
    

    
}
