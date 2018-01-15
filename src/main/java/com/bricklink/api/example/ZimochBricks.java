/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bricklink.api.example;


import org.json.JSONObject;
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
       oracleApis.refreshInventoryDatabase(responseInventory);
        //oracleApis.getAllInventory();
       //oracleApis.setPricesByInventory(blapis);
   }
    
    

    
}
