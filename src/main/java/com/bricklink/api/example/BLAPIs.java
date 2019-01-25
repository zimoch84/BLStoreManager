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
import javax.net.ssl.HttpsURLConnection;
import org.json.JSONObject;

/**
 *
 * @author Piotr
 */
public class BLAPIs {
    
 boolean debug = false;
 BLAuth auth ;

 
    public BLAPIs() {
        auth= new BLAuth();   
    }

    public BLAPIs(boolean debug) {
         auth= new BLAuth();   
         this.debug = debug;
    }
    
 
JSONObject doRequest()    {

    
    System.out.println(auth.getBaseUrlWIthParams());
    
    int countFailConnection=0;
    int maxAttemptsToConnect=500;
    HttpURLConnection httpConnection ;  
    try {
        while(true){
        httpConnection = (HttpsURLConnection) auth.getUrl().openConnection();
        httpConnection.setRequestMethod("GET");
        InputStream is = httpConnection.getInputStream();
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
        StringBuilder response = new StringBuilder(); 
        String line;
            while ((line = rd.readLine()) != null) {
              response.append(line);
              response.append('\r');
            }
        rd.close();
        JSONObject json = new JSONObject(response.toString());
        
        if(debug)
                System.out.println(json);
        
        httpConnection.disconnect();

        if(json.getJSONObject("meta").getInt("code")==200){
           return json;
        }
        if(json.getJSONObject("meta").getString("message").equals("PARAMETER_MISSING_OR_INVALID"))
        {
            System.err.println("PARAMETER_MISSING_OR_INVALID");
            break;
        }

        if(json.getJSONObject("meta").getString("message").equals("RESOURCE_NOT_FOUND"))
        {
            System.err.println("RESOURCE_NOT_FOUND");
            break;
        }
        else {
            countFailConnection++;
            if(countFailConnection==maxAttemptsToConnect)
            { 
                System.err.println("maxAttemptsToConnect exceeded");
                break; 
            }
        }
    }
    } 
    catch (Exception e) {
        e.printStackTrace();
          } 

    return new JSONObject();

}
    
    
JSONObject brickLinkApiInventory()    {
        auth.setBaseUrl("https://api.bricklink.com/api/store/v1/inventories");
return doRequest();
    }      

JSONObject getColors(){
    auth.setBaseUrl("https://api.bricklink.com/api/store/v1/colors");
    return doRequest();
}

JSONObject getCategories()        
{
        auth.setBaseUrl("https://api.bricklink.com/api/store/v1/categories");
        return doRequest();
}

JSONObject getKnownColors(String type, String item_id)        
{
    auth.setBaseUrl("https://api.bricklink.com/api/store/v1/items/" + type+ "/" + item_id + "/colors");
    return doRequest();
    
    
}

JSONObject getItem(String part_id, String type)
        
{
    auth.setBaseUrl("https://api.bricklink.com/api/store/v1/items/" +  type + "/" + part_id );
    return doRequest();
}

JSONObject getSuperSets(String part_id, String type)
{
    auth.setBaseUrl("https://api.bricklink.com/api/store/v1/items/" + type + "/" +  part_id + "/supersets" );
    return doRequest();

}
JSONObject getSubSets(String part_id, String type)
{
    auth.setBaseUrl("https://api.bricklink.com/api/store/v1/items/"+ type+  "/" +  part_id + "/subsets" );
    return doRequest();

}
 
JSONObject getItemPrice(String type, String partNo, String color_id, String guide_type, String new_or_used, String country_code, String region ){
     
    auth.clearParameters();
    auth.setBaseUrl("https://api.bricklink.com/api/store/v1/items/"+type+"/"+partNo+"/price");//+"?"+"color_id="+color_id+"&guide_type="+guide_type+"&new_or_used="+new_or_used+"&country_code="+country_code+"&region="+region);
    auth.parameters.put("color_id", color_id);
    auth.parameters.put("guide_type", guide_type);
    auth.parameters.put("new_or_used", new_or_used);
    auth.parameters.put("vat", "Y");
    if(!country_code.equals(""))
    auth.parameters.put("country_code", country_code);
    if(!region.equals(""))
    auth.parameters.put("region", region);
    return doRequest();
}
}
 
 
