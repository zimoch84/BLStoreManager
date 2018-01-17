/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bricklink.api.example;


import org.json.JSONObject;



/**
 *
 * @author Bartosz
 */
public class ZimochBricks {

      
   public static void main( String[] args ) {
       
     ZimochBricks zb = new ZimochBricks();
    
     //zb.getPricesByInventory();
     //zb.setKnownColors();
     zb.getItem();
   }
    
   void refreshInventory()
   {
       BLAPIs blapis = new BLAPIs();
       JSONObject responseInventory = blapis.brickLinkApiInventory();
       OracleAPIs oracleApis = new OracleAPIs();
       oracleApis.refreshInventoryDatabase(responseInventory);
   }
   
void setPricesByInventory()
{
     //BLAPIs blapis = new BLAPIs();
     //JSONObject responseItem=blapis.brickLinkApiItem("part", "2335", "11", "stock", "U", "PL", "EU");
     //System.err.println(responseItem.toString());
        
String custom_guide_type= "stock";  //stock or sold
String custom_new_or_used = "U"; //U-used, N-new
String custom_country ="PL";
String custom_region= "EU";

String dbQuerry = "select PART_ID, TYPE, COLOR_ID from inventory i where \n" +
                    "not exists \n" +
                        "(select 1 from price p where p.part_id like i.part_id and p.color_id = i.color_id)\n" ;


OracleAPIs oracleApis = new OracleAPIs();
oracleApis.setPricesByQuerry(dbQuerry, custom_guide_type, custom_new_or_used, custom_country, custom_region);
}
   
void getColors()
           
   {
   
        BLAPIs blapis = new BLAPIs();
        OracleAPIs oracleApis = new OracleAPIs();
        
        JSONObject colors = blapis.getColors();
        oracleApis.setColors(colors);
        
        System.out.println(colors.toString());
   }
    
void getCategories()
           
   {
   
        BLAPIs blapis = new BLAPIs();
        OracleAPIs oracleApis = new OracleAPIs();
        
        JSONObject categories = blapis.getCategories();
        oracleApis.setCategories(categories);
        
        System.out.println(categories.toString());
   }
 void setKnownColors()
           
   {
        OracleAPIs oracleApis = new OracleAPIs();
        String querySelectItems = "select distinct part_id, type from items i where rownum < 2"
            + "and not exists (select 1 from knowncolors where part_id = i.part_id) and type != \'SET\'";
        oracleApis.setKnownColors(querySelectItems);
        //JSONObject categories = blapis.getKnownColors("PART","98313");
        //oracleApis.setCategories(categories);
        //System.out.println(categories.toString());
   }
void getItem()
{

    //BLAPIs blapis = new BLAPIs();
    
    //blapis.getItem("4589", "PART");
    
    
     OracleAPIs oracleApis = new OracleAPIs();
        String querySelectItems = "select distinct part_id, type from items i where 1=1"
                +"and type = 'PART'";
              
               
  
        oracleApis.setItems(querySelectItems);
        
        
        
}
        
        
    
}
