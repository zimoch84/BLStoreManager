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
   zb.setPricesByInventory();
   }
    
   void refreshInventory()
   {
       BLAPIs blapis = new BLAPIs();
       JSONObject responseInventory = blapis.brickLinkApiInventory();
       System.out.println("com.bricklink.api.example.ZimochBricks.refreshInventory() " + responseInventory.toString());
       OracleAPIs oracleApis = new OracleAPIs();
       oracleApis.refreshInventoryDatabase(responseInventory);
   }
   
void setPricesByInventory()
{
     //BLAPIs blapis = new BLAPIs();
     //JSONObject responseItem=blapis.brickLinkApiItem("part", "2335", "11", "stock", "U", "PL", "EU");
     //System.err.println(responseItem.toString());
        
String custom_guide_type= "stock";  //stock or sold
String custom_new_or_used = "N"; //U-used, N-new
String custom_country ="";
String custom_region= "EU";

String dbQuerry1 = "select PART_ID, TYPE, COLOR_ID from inventory i where \n" +
                    "not exists \n" +
                        "(select 1 from price p where p.part_id like i.part_id and p.color_id = i.color_id)\n" ;

String dbQuerry = "select k.PART_ID, k.COLOR_ID , i.type "
        + "from KNOWNCOLORS  k join ITEMS i on i.PART_ID = k.PART_ID join CATEGORIES c on c.ID = i.CATEGORY_ID\n" +
"\n" +
"where not exists (select 1 from PRICE p2 where p2.COLOR_ID = k.COLOR_ID and p2.PART_ID = k.PART_ID)\n" +
"\n" +
"and rownum < 20000" ;

String dbQString2 = "select distinct  s.SUBPART_ID as part_id, s.COLOR_ID , \n" +
"(select type from items i2 where i2.part_id  = s.subpart_id) as type\n" +
"\n" +
"\n" +
"from items i join SUBSETS s on s.PART_ID = i.PART_ID\n" +
"left outer join PRICE p on p.COLOR_ID = s.COLOR_ID  and p.PART_ID = s.SUBPART_ID\n" +
"where i.type like 'SET' and year_released in ('2017')\n" +
"and p.AVG_PRICE =0";

OracleAPIs oracleApis = new OracleAPIs();
oracleApis.setPricesByQuerry(dbQString2, custom_guide_type, custom_new_or_used, custom_country, custom_region);
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
        String querySelectItems = "select distinct part_id, type from items i where rownum < 2";
          //  + "and not exists (select 1 from knowncolors where part_id = i.part_id) and type != \'SET\'";
        oracleApis.setKnownColors(querySelectItems);
        //JSONObject categories = blapis.getKnownColors("PART","98313");
        //oracleApis.setCategories(categories);
        //System.out.println(categories.toString());
   }
void getItems()
{


    
    BLAPIs bl = new BLAPIs();
    
    //bl.getItem("rac060", "SET");
    
    
     OracleAPIs oracleApis = new OracleAPIs();
      /*  String querySelectItems = "select distinct part_id, type from items i where 1=1"
                +"and type = 'SET' and part_id  like '%-%' and year_released is null and part_id not in ('LMI2-DE', 'LMI1-DE') ";
        */      
       String querySelectItems = "select distinct part_id, 'SET' as type from items i where 1=1"
                +"and type is null";
               
  
        oracleApis.setItems(querySelectItems);
        
        
        
}

void getSuperSets(){

    //BLAPIs api = new BLAPIs();
  //  api.getSuperSets("4856a", null);
OracleAPIs oApi = new OracleAPIs();
String querySelectItems = "select distinct part_id, type from items i where 1=1"
                +"and type = 'PART'";
     
     oApi.setSetsFromParts(querySelectItems);
              
    
}
    void getSubSets(){

    BLAPIs api = new BLAPIs();
   api.getSubSets("6392-1", "SET");
   OracleAPIs oApi = new OracleAPIs();
     String querySelectItems = "select distinct part_id, type from items i where 1=1"
             +"and type = 'SET'"
             + "and part_id like '%-%'"
               +"and part_id not in ('LMI2-DE' , 'LMI1-DE')"
             + "and rownum < 5000"
             + "and not exists (select 1 from subsets s where s.part_id = i.part_id)";
    
     
    oApi.setPartOutSet(querySelectItems);
              
    
}    
    
}
