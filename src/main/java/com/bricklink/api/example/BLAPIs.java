/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bricklink.api.example;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;
import org.json.JSONObject;

/**
 *
 * @author Bartosz
 */
public class BLAPIs {
    
 BLAuth auth ;

    public BLAPIs() {
        auth= new BLAuth();   
    }
  
 
 
 
 JSONObject brickLinkApiInventory()
    {
        auth.setBaseUrl("https://api.bricklink.com/api/store/v1/inventories");
       
        try {
            HttpURLConnection httpConnection ;
            System.out.println( auth.getUrl() );
            httpConnection = (HttpsURLConnection)auth.getUrl().openConnection();
            
            
            httpConnection.setRequestMethod("GET");
            
             System.out.println("Response: "  + httpConnection.getResponseMessage());
             
             InputStream is = httpConnection.getInputStream();
             
                BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                StringBuilder response = new StringBuilder(); // or StringBuffer if Java version 5+
                String line;
                while ((line = rd.readLine()) != null) {
                  response.append(line);
                  response.append('\r');
                }
                rd.close();
                //response from BrickLink
                JSONObject json = new JSONObject(response.toString());
                httpConnection.disconnect();
                 return json;
                } 
        catch (Exception e) {
                e.printStackTrace();
               
              } 
        return null;
    }      
    
 
JSONObject brickLinkApiItem(String type, String partNo, String color_id, String guide_type, String new_or_used, String country_code, String region ){
     
        auth.setBaseUrl("https://api.bricklink.com/api/store/v1/items/"+type+"/"+partNo+"/price");//+"?"+"color_id="+color_id+"&guide_type="+guide_type+"&new_or_used="+new_or_used+"&country_code="+country_code+"&region="+region);
        auth.parameters.put("color_id", color_id);
        auth.parameters.put("guide_type", guide_type);
        auth.parameters.put("new_or_used", new_or_used);
        auth.parameters.put("country_code", country_code);
        auth.parameters.put("region", region);
        
        try {
            HttpURLConnection httpConnection ;
            System.out.println("Url: "+ auth.getUrl() );
            System.out.println();
            httpConnection = (HttpsURLConnection)auth.getUrl().openConnection();
            httpConnection.setRequestMethod("GET");
            InputStream is = httpConnection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            StringBuilder response = new StringBuilder(); // or StringBuffer if Java version 5+
            String line;
            while ((line = rd.readLine()) != null) {
                  response.append(line);
                  response.append('\r');
            }
            rd.close();
                //response from BrickLink
                JSONObject json = new JSONObject(response.toString());
               System.out.println("JSON Response : " + response.toString()); 
                 System.out.println();
                
             httpConnection.disconnect();
             return json;
                } 
        catch (Exception e) {
                e.printStackTrace();
                //httpConnection.disconnect();
         
              } 
            
      return null;
    }

}
 
 
 
