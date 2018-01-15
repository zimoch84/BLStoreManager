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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
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
    
    

    public OracleAPIs(OracleConnection oracleConnection) {
        this.oracleConnection = oracleConnection;
    }


    void refreshInventoryDatabase(JSONObject json){
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
//                    System.err.println("inventory_id"+inventory_id+" part_id"+part_id+" name"+name+" type"+type+" category_id"+category_id
//                    +" color_name"+color_name+" quantity"+quantity+" newUsed"+newUsed+" unit_price"+unit_price+" bulk"+bulk+
//                    " is_retain"+is_retain+" is_stock_room"+is_stock_room+" my_cost"+my_cost+" sale_rate"+sale_rate);
            

//have to delete all first and then inset new.. later
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
    
    HashMap setPricesByInventory(BLAPIs blapis){
        
        String custom_guide_type= "stock";  //stock or sold
        String custom_new_or_used = "U"; //U-used, N-new
        String custom_country ="PL";
        String custom_region= "EU";
        float vo_PRICE_ID=0;
        JSONObject json;
        int countFailConnection=0;
        int maxAttemptsToConnect=500;
        int count_price=0;
        int count_price_detail=0;
        int count_connections=0;
        //get all data about INVENTORY from Oracle Database
        
        String querySelectAllInventory = "select * from inventory i where \n" +
"not exists \n" +
"(select 1 from price p where p.part_id like i.part_id and p.color_id = i.color_id)\n" ;//+
//"\n" +
//"and rownum < 500";
        
      //  querySelectAllInventory = "select * from inventory where id between 10 and 15";
        
        ResultSet rs=oracleConnection.sendRequest(querySelectAllInventory);
        try {
            while (rs.next()) {
                
                count_price++;
                int invID = rs.getInt("ID");
                int invInventoryID = rs.getInt("INVENTORY_ID");
                String invPartID = rs.getString("PART_ID");
                String invName =  rs.getString("NAME");       
                String invType = rs.getString("TYPE"); 
                int invCategoryID = rs.getInt("CATEGORY_ID"); 
                String invColorName = rs.getString("COLOR_NAME"); 
                int invQuantity = rs.getInt("QUANTITY");       
                String invNewUsed =  rs.getString("NEW_OR_USED");
                String invUnitPrice = rs.getString("UNIT_PRICE");         
                int invBulk = rs.getInt("BULK");  
                String invIsRetain = rs.getString("IS_RETAIN");        
                String invIsStocRoom = rs.getString("IS_STOCK_ROOM");        
                String invMyCost = rs.getString("MY_COST");       
                int invSaleRate = rs.getInt("SALE_RATE");        
                int invColorID = rs.getInt("COLOR_ID");
                
                while(true){
            //get price information from BrickLink based on PartId and other..   
                    json = blapis.brickLinkApiItem(invType, invPartID, String.valueOf(invColorID), custom_guide_type, custom_new_or_used, custom_country, custom_region);
                    count_connections++;
                    if(json.getJSONObject("meta").getInt("code")==200){
                        break;
                    }
                    else {
                       
                        countFailConnection++;
                        System.err.println("ALARM"+countFailConnection); 
                        System.err.println();
                        if(countFailConnection==maxAttemptsToConnect)break; 
                    }
                }
                if(countFailConnection==maxAttemptsToConnect)
                {
                    return null;
                }
//parse json object to readable values  
                
                /*  for(int i=0; i<json.getJSONObject("data").getJSONArray("price_detail").length(); i++){
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
                            int sale_rate = json.getJSONArray("data").getJSONObject(i).getInt("sale_rate");*/

                //                    System.err.println("inventory_id"+inventory_id+" part_id"+part_id+" name"+name+" type"+type+" category_id"+category_id
                //                    +" color_name"+color_name+" quantity"+quantity+" newUsed"+newUsed+" unit_price"+unit_price+" bulk"+bulk+
                //                    " is_retain"+is_retain+" is_stock_room"+is_stock_room+" my_cost"+my_cost+" sale_rate"+sale_rate);

                
                            JSONObject temp = json.getJSONObject("data");
                            String bl_new_or_used = temp.getString("new_or_used");
                            Float bl_max_price = temp.getFloat("max_price");
                            Float bl_min_price = temp.getFloat("min_price");
                            Float bl_qty_avg_price = temp.getFloat("qty_avg_price");
                            int bl_total_quantity = temp.getInt("total_quantity");
                            Float bl_avg_price = temp.getFloat("avg_price");
                            String bl_currency_code  = temp.getString("currency_code");
                            int bl_unit_quantity  = temp.getInt("unit_quantity");
                            
                            
                //make insert to tha PRICE table tru the procedure INSERT_PRICE            
                            CallableStatement cs = oracleConnection.getConnection().prepareCall("{call INSERT_PRICE(?,?,?,?,?,?,?,?,?,?,?,?,?,?,     ?)}");//14xin 1xout
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
                //getPriceArray and set it in PRICE_BY_SHOP
                 
                            JSONArray price_detail = temp.getJSONArray("price_detail");
                            for(int i=0; i<price_detail.length(); i++){
                                count_price_detail++;
                            cs = oracleConnection.getConnection().prepareCall("{call INSERT_PRICE_BY_SHOP(?,?,?,?,?,?,?)}");//7in 
                            cs.setString(1, invPartID);
                            cs.setFloat(2, invColorID);
                            cs.setString(3, custom_country);
                            cs.setString(4, custom_region);
                            cs.setString(5, custom_new_or_used);
                            cs.setString(6, custom_guide_type);
                            cs.setFloat(7, vo_PRICE_ID);
                            
                            cs.execute(); 
                            cs.close();
                            
                            }            
                           
                
                /*
                System.out.println(invID+"\t" +invInventoryID+"\t" +invPartID+invName+"\t" +invType+"\t" +invCategoryID+"\t" +
                        invColorName+"\t" +invQuantity+"\t" +invNewUsed+"\t" +invUnitPrice+invBulk+"\t" +invIsRetain+"\t" +
                        invIsStocRoom+"\t" +invMyCost+"\t" +invSaleRate+"\t" +invColorID + "PRICE_ID"+vo_PRICE_ID);
*/
            }
        } catch (SQLException ex) {
            Logger.getLogger(OracleAPIs.class.getName()).log(Level.SEVERE, null, ex);
        }finally {
        oracleConnection.closeConnection();
        System.out.println("Added:"+count_price+" items to PRICE TABLE and, "+count_price_detail+" items to PRICE_BY_SHOP, connecting to BLink " + count_connections+ " times");
        }
     
    
    return null;
    }
    
}
