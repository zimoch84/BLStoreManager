/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bricklink.api.example;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Bartosz
 */
public class OracleAPIs {
    OracleConnection oracleConnection;
    BLAPIs blAPI;
    boolean debug = false;
    
public OracleAPIs() {
    this.oracleConnection =   new OracleConnection();
    this.blAPI = new BLAPIs();
}

    public OracleAPIs(boolean debug) {
        this.debug = debug;
        this.oracleConnection =   new OracleConnection();
        this.blAPI = new BLAPIs(debug);
    }



void refreshInventoryDatabase(){

    JSONObject json = blAPI.brickLinkApiInventory();
    oracleConnection.connect();
    String updatequery = "UPDATE BARTEK.INVENTORY set isvalid = 'N' , dto=sysdate where isvalid = 'Y'"   ;
        try {
            PreparedStatement ps = oracleConnection.getConnection().prepareStatement(updatequery);
            System.out.println("com.bricklink.api.example.OracleAPIs.refreshInventoryDatabase() update inventory" );
            ps.execute();
            ps.close();
        }
        catch(SQLException s){
            System.out.println("com.bricklink.api.example.OracleAPIs.refreshInventoryDatabase()" + s) ;
        }

        for(int i=0; i<json.getJSONArray("data").length(); i++){
            int inventory_id = json.getJSONArray("data").getJSONObject(i).getInt("inventory_id");
                String part_id = json.getJSONArray("data").getJSONObject(i).getJSONObject("item").getString("no");
                String name = json.getJSONArray("data").getJSONObject(i).getJSONObject("item").getString("name");
                String type = json.getJSONArray("data").getJSONObject(i).getJSONObject("item").getString("type");
                int category_id = json.getJSONArray("data").getJSONObject(i).getJSONObject("item").getInt("category_id");
                  
            int color_id = json.getJSONArray("data").getJSONObject(i).getInt("color_id");
            String color_name = json.getJSONArray("data").getJSONObject(i).getString("color_name");
            int quantity = json.getJSONArray("data").getJSONObject(i).getInt("quantity");
            String newUsed = json.getJSONArray("data").getJSONObject(i).getString("new_or_used");
            float unit_price = json.getJSONArray("data").getJSONObject(i).getFloat("unit_price");
            int bulk = json.getJSONArray("data").getJSONObject(i).getInt("bulk");
            boolean is_retain = json.getJSONArray("data").getJSONObject(i).getBoolean("is_retain");
            boolean is_stock_room = json.getJSONArray("data").getJSONObject(i).getBoolean("is_stock_room");
            float my_cost = json.getJSONArray("data").getJSONObject(i).getFloat("my_cost");
            int sale_rate = json.getJSONArray("data").getJSONObject(i).getInt("sale_rate");
             String date_created = json.getJSONArray("data").getJSONObject(i).getString("date_created");   

            String query = "INSERT INTO BARTEK.INVENTORY "
                + "(INVENTORY_ID, PART_ID, NAME, TYPE, CATEGORY_ID, COLOR_NAME, QUANTITY, NEW_OR_USED, UNIT_PRICE, BULK, IS_RETAIN, IS_STOCK_ROOM, MY_COST, SALE_RATE, COLOR_ID, DATE_CREATED)"
                + " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
   
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date convertedCurrentDate = sdf.parse(date_created.substring(0,10));
          
                PreparedStatement ps = oracleConnection.getConnection().prepareStatement(query);
                ps.setInt(1, inventory_id);
                ps.setString(2, part_id);
                ps.setString(3, name);  
                ps.setString(4, type);  
                ps.setInt(5, category_id);  
                ps.setString(6, color_name);  
                ps.setInt(7, quantity);  
                ps.setString(8, newUsed);  
                ps.setFloat(9, unit_price);  
                ps.setInt(10, bulk);  
                ps.setString(11, String.valueOf(is_retain));  
                ps.setString(12, String.valueOf(is_stock_room));                      
                ps.setFloat(13, my_cost); 
                ps.setInt(14, sale_rate);
                ps.setInt(15, color_id);
                ps.setDate(16, new java.sql.Date(convertedCurrentDate.getTime()));
                
                ps.execute();
                ps.close();
            } catch (SQLException ex) {
                Logger.getLogger(ZimochBricks.class.getName()).log(Level.SEVERE, null, ex);
            } catch (java.text.ParseException ex) {
                Logger.getLogger(OracleAPIs.class.getName()).log(Level.SEVERE, null, ex);
            }
        
        }
    }
    
void setPricesByQuerry(String dbQuerry,String custom_guide_type,String custom_new_or_used,
        String custom_country, String custom_region  ){
    

oracleConnection.connect();
float vo_PRICE_ID=0;
JSONObject json;
int count_price=0;
int count_price_detail=0;
int count_connections=0;
ResultSet rs=oracleConnection.sendRequest(dbQuerry);
try {
    while (rs.next()) {

        count_price++;
        String invPartID = rs.getString("PART_ID");
        String invType = rs.getString("TYPE"); 
        int invColorID = rs.getInt("COLOR_ID");

        json = blAPI.getItemPrice(invType, invPartID, String.valueOf(invColorID), custom_guide_type, custom_new_or_used, custom_country, custom_region);
       
        JSONObject temp ;
        try{
            temp = json.getJSONObject("data");
        }
        catch (Exception ex )
        {
            System.err.println("Blad w wyciagabniu data " + json.toString());
            continue;
        }
        String bl_new_or_used = temp.getString("new_or_used");
        Float bl_max_price = temp.getFloat("max_price");
        Float bl_min_price = temp.getFloat("min_price");
        Float bl_qty_avg_price = temp.getFloat("qty_avg_price");
        int bl_total_quantity = temp.getInt("total_quantity");
        Float bl_avg_price = temp.getFloat("avg_price");
        String bl_currency_code  = temp.getString("currency_code");
        int bl_unit_quantity  = temp.getInt("unit_quantity");
        
        
            if(bl_avg_price > 0 )
            {
            
                CallableStatement cs = oracleConnection.getConnection().prepareCall("{call INSERT_PRICE(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");//14xin 1xout
                cs.setString(1, invPartID);
                cs.setString(2, invType);
                cs.setString(3, bl_new_or_used);
                cs.setFloat(4, bl_max_price);
                cs.setFloat(5, bl_min_price);
                cs.setFloat(6, bl_qty_avg_price);
                cs.setInt(7, bl_total_quantity);
                cs.setFloat(8, bl_avg_price);
                cs.setString(9, bl_currency_code);
                cs.setInt(10, bl_unit_quantity);
                cs.setFloat(11, invColorID);
                cs.setString(12, custom_country);
                cs.setString(13, custom_region);
                cs.setString(14, custom_guide_type);
                cs.registerOutParameter(15, java.sql.Types.FLOAT);

                cs.execute();

                vo_PRICE_ID =   cs.getFloat(15);
                cs.close();

               
                
                JSONArray price_detail = temp.getJSONArray("price_detail");
                int detailQuantity;
                float unit_price;
                String date_ordered = "";
                String seller_country_code = "";
                String buyer_country_code = "";
                java.sql.Date date_ordered_date;

                for(int i=0; i<price_detail.length(); i++){
                    count_price_detail++;

                 detailQuantity = price_detail.getJSONObject(i).getInt("quantity");
                 unit_price = price_detail.getJSONObject(i).getFloat("unit_price");
                 if(custom_guide_type.equals("sold") ){
                    try {
                    date_ordered = price_detail.getJSONObject(i).getString("date_ordered");    
                    date_ordered_date = new java.sql.Date(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(date_ordered).getTime());
                    seller_country_code = price_detail.getJSONObject(i).getString("seller_country_code");
                    buyer_country_code = price_detail.getJSONObject(i).getString("buyer_country_code");
                    
                    cs = oracleConnection.getConnection().prepareCall("{call INSERT_PRICE_BY_SHOP(?,?,?,?,?,?,?,?,?,?,?,?)}");//7in 
                    cs.setString(1, invPartID);
                    cs.setFloat(2, invColorID);
                    cs.setString(3, custom_country);
                    cs.setString(4, custom_region);
                    cs.setString(5, custom_new_or_used);
                    cs.setString(6, custom_guide_type);
                    cs.setInt(7, detailQuantity);
                    cs.setFloat(8, unit_price);
                    cs.setDate(9, date_ordered_date);
                    cs.setString(10, seller_country_code);
                    cs.setString(11, buyer_country_code);
                    cs.setFloat(12, vo_PRICE_ID);

                    cs.execute(); 
                    cs.close();
                    
                } 
                catch (ParseException ex) {
                    Logger.getLogger(OracleAPIs.class.getName()).log(Level.SEVERE, null, ex);
                }
               
                }
                else{
                    cs = oracleConnection.getConnection().prepareCall("{call INSERT_PRICE_BY_SHOP(?,?,?,?,?,?,?,?,?,?,?,?)}");//7in 
                    cs.setString(1, invPartID);
                    cs.setFloat(2, invColorID);
                    cs.setString(3, custom_country);
                    cs.setString(4, custom_region);
                    cs.setString(5, custom_new_or_used);
                    cs.setString(6, custom_guide_type);
                    cs.setInt(7, detailQuantity);
                    cs.setFloat(8, unit_price);
                    cs.setDate(9, null);
                    cs.setString(10, null);
                    cs.setString(11, null);
                    cs.setFloat(12, vo_PRICE_ID);

                    cs.execute(); 
                    cs.close();
                }
                 
                }
        }            
}
}
catch (SQLException ex) {
    Logger.getLogger(OracleAPIs.class.getName()).log(Level.SEVERE, null, ex);
}
finally {
    oracleConnection.closeConnection();
     System.out.println("Added:"+count_price+" items to PRICE TABLE and, "
             + count_price_detail+ " items to PRICE_BY_SHOP, connecting to BLink " 
             + count_connections+ " times");
}

}
    
void setColors(JSONObject json){
    
oracleConnection.connect();
for(int i=0; i<json.getJSONArray("data").length(); i++){
    int color_id = json.getJSONArray("data").getJSONObject(i).getInt("color_id");
    String color_name = json.getJSONArray("data").getJSONObject(i).getString("color_name");
    String color_type = json.getJSONArray("data").getJSONObject(i).getString("color_type");
    String query = "INSERT INTO BARTEK.COLORS "
        + "(ID, NAME, TYPE)"
        + " VALUES (?,?,?)";
    try {
        PreparedStatement ps = oracleConnection.getConnection().prepareStatement(query);
        ps.setInt(1, color_id);
        ps.setString(2, color_name);
        ps.setString(3, color_type);  
        ps.execute();
        ps.close();
    } catch (SQLException ex) {
        Logger.getLogger(ZimochBricks.class.getName()).log(Level.SEVERE, null, ex);
    }
}
}
void setCategories(JSONObject json){
    
oracleConnection.connect();
for(int i=0; i<json.getJSONArray("data").length(); i++){
    int category_id = json.getJSONArray("data").getJSONObject(i).getInt("category_id");
    String category_name = json.getJSONArray("data").getJSONObject(i).getString("category_name");
    int parent_id = json.getJSONArray("data").getJSONObject(i).getInt("parent_id");
    String query = "INSERT INTO BARTEK.CATEGORIES"
        + "(ID, NAME, PARENT_ID)"
        + " VALUES (?,?,?)";
    try {
        PreparedStatement ps = oracleConnection.getConnection().prepareStatement(query);
        ps.setInt(1, category_id);
        ps.setString(2, category_name);
        ps.setInt(3, parent_id);  
        ps.execute();
        ps.close();
    } catch (SQLException ex) {
        Logger.getLogger(ZimochBricks.class.getName()).log(Level.SEVERE, null, ex);
    }
}
}
void setKnownColors(String dbQuerryFromItems){

oracleConnection.connect();

int countFailConnection=0;
int maxAttemptsToConnect=500;
int count_price=0;
int count_price_detail=0;
int count_connections=0;

ResultSet rs=oracleConnection.sendRequest(dbQuerryFromItems);
try {
    while (rs.next()) {
    count_price++;
    String invPartID = rs.getString("PART_ID");
    String invType = rs.getString("TYPE"); 
    JSONObject json;
    while(true){
        json = blAPI.getKnownColors(invType, invPartID);
        count_connections++;
        if(json.getJSONObject("meta").getInt("code")==200){
            break;
        }
        else if (json.getJSONObject("meta").getInt("code")==404)
        {
         System.err.println(json.getJSONObject("meta").toString());
        }
        
        else {
            countFailConnection++;
            System.err.println("ALARM"+countFailConnection); 
            if(countFailConnection==maxAttemptsToConnect) return; 
        }
        }

    for(int i=0; i<json.getJSONArray("data").length(); i++){
       int color_id = json.getJSONArray("data").getJSONObject(i).getInt("color_id");
       int quantity = json.getJSONArray("data").getJSONObject(i).getInt("quantity");

    String query = "INSERT INTO BARTEK.KNOWNCOLORS "
        + "(PART_ID, COLOR_ID, QUANTITY)"
        + " VALUES (?,?,?)";
    try {
        PreparedStatement ps = oracleConnection.getConnection().prepareStatement(query);
        ps.setString(1,invPartID ); 
        ps.setInt(2, color_id);
        ps.setInt(3, quantity);
        ps.execute();
        ps.close();
    } catch (SQLException ex) {
        Logger.getLogger(ZimochBricks.class.getName()).log(Level.SEVERE, null, ex);
    }
    
 }
}
} catch (SQLException ex) {
    Logger.getLogger(OracleAPIs.class.getName()).log(Level.SEVERE, null, ex);
}finally {
    oracleConnection.closeConnection();
     System.out.println("Added:"+count_price+" items to PRICE TABLE and, "
             + count_price_detail+ " items to PRICE_BY_SHOP, connecting to BLink " 
             + count_connections+ " times");
}
}
void setItems(String dbQuerryFromItems){

oracleConnection.connect();
int count_price=0;
int count_price_detail=0;
int count_connections=0;

JSONObject json;
ResultSet rs=oracleConnection.sendRequest(dbQuerryFromItems);
try {
    while (rs.next()) {
    count_price++;
    String invPartID = rs.getString("PART_ID");
    String invType = rs.getString("TYPE"); 
    
    json = blAPI.getItem(invPartID, invType);
    json = json.getJSONObject("data");
    
    String name ;
    int category_id;
    String image_url ;
    String thumbnail_url ;
    float weight  ;
    float dim_x  ;
    float dim_y  ;
    float dim_z ;
    String invType2;

    try{
    name = json.getString("name");
    }
    catch (Exception ex){
        System.err.println("Error przy czytaniu name ");
        continue;
    }
    category_id = json.getInt("category_id");
     
    weight = json.getFloat("weight");
    try{
    dim_x = json.getFloat("dim_x");
    dim_y = json.getFloat("dim_y");
    dim_z = json.getFloat("dim_z");
    }
    catch (Exception ex){
       dim_x = 0;
        dim_y = 0;
        dim_z = 0;
    }
    invType2 = json.getString("type");
    try{
        image_url = json.getString("image_url");
     }
    catch (Exception ex){
        System.err.println("Error przy czytaniu image_url ");
        image_url = null;
    }
    try{
        thumbnail_url = json.getString("thumbnail_url");
        
    }
    catch (Exception ex){
        System.err.println("Error przy czytaniu thumbnail_url");
        thumbnail_url = null;
    }
    
    Integer year = new Integer(0);
    if(invType.equals("SET")  )
    {
         year = json.getInt("year_released");
    } 
            
        CallableStatement cs = oracleConnection.getConnection().
        prepareCall("{call ITEMS_TAPI.INS(?,?,?,?,?,?,?,?,?,?,?)}");
        
        cs.setInt(1, category_id);
        cs.setFloat(2, dim_x);
        cs.setFloat(3, dim_y);
        cs.setFloat(4, dim_z);
        cs.setString(5, thumbnail_url);
        cs.setString(6, image_url);
        cs.setFloat(7, weight);
        cs.setString(8, invType2);
        cs.setString(9, invPartID);
        cs.setString(10, name);
        cs.setString(11, year.toString());
        cs.execute();
        cs.close();
        
    
    }
} catch (SQLException ex) {
    Logger.getLogger(OracleAPIs.class.getName()).log(Level.SEVERE, null, ex);
}finally {
    oracleConnection.closeConnection();
     System.out.println("Added:"+count_price+" items to PRICE TABLE and, "
             + count_price_detail+ " items to PRICE_BY_SHOP, connecting to BLink " 
             + count_connections+ " times");
}
}

void setSetsFromParts(String dbQuerryFromItems){

oracleConnection.connect();
BLAPIs blAPI = new BLAPIs();
ResultSet rs=oracleConnection.sendRequest(dbQuerryFromItems);
try {
    while (rs.next()) {
    String invPartID = rs.getString("PART_ID");
    String invType = rs.getString("TYPE"); 
    
    JSONObject json = blAPI.getSuperSets(invPartID, invType );
    JSONArray  data = json.getJSONArray("data");
    
    for(int i=0; i<data.length(); i++){
        JSONArray sets = data.getJSONObject(i).getJSONArray("entries");
            for (int j=0; j< sets.length() ; j++){

            JSONObject set = sets.getJSONObject(j).getJSONObject("item");
            String  invSetID = set.getString("no");
            String  invSetType = set.getString("type");
            String name = set.getString("name");
            int category_id = set.getInt("category_id");
            CallableStatement cs = oracleConnection.getConnection().
            prepareCall("{call ITEMS_TAPI.INS(?,?,?,?,?,?,?,?,?,?,?)}");
            cs.setInt(1, category_id);
            cs.setString(2,null);
            cs.setString(3,null);
            cs.setString(4,null);
            cs.setString(5,null);
            cs.setString(6,null);
            cs.setString(7,null);
            cs.setString(8, invSetType);
            cs.setString(9, invSetID);
            cs.setString(10, name);
            cs.setString(11, null);
            cs.execute();
            cs.close();
            }
    }    
    }
} 
catch (SQLException ex) {
    Logger.getLogger(OracleAPIs.class.getName()).log(Level.SEVERE, null, ex);
}finally {
    oracleConnection.closeConnection();
   
}
}
void setPartOutSet(String dbQuerryFromItems){

oracleConnection.connect();
   
int count_price=0;


ResultSet rs=oracleConnection.sendRequest(dbQuerryFromItems);
try {
    while (rs.next()) {
    count_price++;
    String invPartID = rs.getString("PART_ID");
    String invType = rs.getString("TYPE"); 
    JSONObject json;
    json = blAPI.getSubSets(invPartID, invType );
    JSONArray  data = json.getJSONArray("data");
    
    for(int i=0; i<data.length(); i++){
            JSONArray entries = data.getJSONObject(i).getJSONArray("entries");
            for (int j=0; j< entries.length() ; j++)
            {
                JSONObject part = entries.getJSONObject(j).getJSONObject("item");
                    String  invSetID = part.getString("no");
                    String type = part.getString("type"); 
                int colorID = entries.getJSONObject(j).getInt("color_id");
                int quantity = entries.getJSONObject(j).getInt("quantity");
              
                boolean isAlter = entries.getJSONObject(j).getBoolean("is_alternate");
                boolean isCounterpart =entries.getJSONObject(j).getBoolean("is_counterpart");
                int extra_quantity =entries.getJSONObject(j).getInt("extra_quantity");
                CallableStatement cs = oracleConnection.getConnection().
                prepareCall("{call SUBSETS_tapi.INS(?,?,?,?,?,?,?,?)}");
                cs.setString(1,invPartID);
                cs.setString(2,invSetID);
                cs.setInt(3,colorID);
                cs.setInt(4,quantity);
                cs.setString(5, type);
                cs.setString(6, (isAlter ? "Y" : "N"));
                cs.setString(7, (isCounterpart ? "Y" : "N"));
                cs.setInt(8, extra_quantity);
                cs.execute();
                cs.close();
            }
      
    }    
   
    }
} 
catch (SQLException ex) {
    Logger.getLogger(OracleAPIs.class.getName()).log(Level.SEVERE, null, ex);
}
finally {
    oracleConnection.closeConnection();
    System.out.println("Added:"+count_price);
}


}
}