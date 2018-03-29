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
   
 //zb.refreshInventory();
   zb.setPricesByInventory();
    //zb.getItems();
 // zb.setPricesByInventory();
       //BLAPIs bl = new BLAPIs();
               
        //       bl.getSubSets("40178-1", "SET");
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
String custom_new_or_used = ""; //U-used, N-new can be null
String custom_country ="PL"; 
String custom_region= "EU";

String priceInventory = "select PART_ID, TYPE, COLOR_ID from inventory i where  i.DATE_CREATED > sysdate - 1  and i.ISVALID = 'Y' \n" ;
                 //  "not exists (select 1 from price p where p.part_id like i.part_id and p.color_id = i.color_id and country = 'PL' and region = 'EU' and new_or_used='N' )" ;

String pricesByKnownColors = "select k.PART_ID, k.COLOR_ID , i.type "
        + "from KNOWNCOLORS  k join ITEMS i on i.PART_ID = k.PART_ID join CATEGORIES c on c.ID = i.CATEGORY_ID\n" +
"\n" +
"where not exists (select 1 from PRICE p2 where p2.COLOR_ID = k.COLOR_ID and p2.PART_ID = k.PART_ID)\n" +
"\n" +
"and rownum < 20000" ;

String subpartsBySets = "select distinct s.subpart_id as part_id,"
        + " (select type from items i2 where i2.part_id  = s.subpart_id) as type, s.color_id\n" +
"from items i join SUBSETS s on s.PART_ID = i.PART_ID\n " +
"left outer join PRICE p on p.COLOR_ID = s.COLOR_ID  and p.PART_ID = s.SUBPART_ID\n" +
"where i.type like 'SET' and year_released in ('2014')\n" +
"--and p.country  is null\n" +
"and p.isvalid = 'Y'\n" +
"and p.id = (select max(id) from price p2 where p2.part_id = p.part_id and p2.color_id = p.color_id) \n" +
"and p.AVG_PRICE  = 0";

String sqlSets = "select part_id, type, 0 as color_id from items i \n" +
"--join price p on p.part_id = i.part_id\n" +
"where i.type like 'SET' \n" +
"and i.YEAR_RELEASED in ('2017', '2016', '2015', '2014', '2013', '2012', '2011', '2010') \n" +
"and \n" +
"(exists (select 1 from price p2 where p2.part_ID = i.part_id  and p2.avg_price = 0)\n" +
"or not exists(select 1 from price p3 where p3.part_ID = i.part_id))"
        ;
        
String onePart = "select i.part_id, type, kk.color_id color_id from items i join KNOWNCOLORS kk on kk.part_id = i.part_id\n" +
"where i.part_id in ('3005', '3004',  '3010', '2454', '60593')";
        

OracleAPIs oracleApis = new OracleAPIs();
oracleApis.setPricesByQuerry(priceInventory, custom_guide_type, custom_new_or_used, custom_country, custom_region);
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
               
       String itemByInventory = "select DISTINCT INV.PART_ID,'PART' as type from inventory  INV LEFT OUTER JOIN ITEMS ON INV.PART_ID = ITEMS.PART_ID\n" +
                    "WHERE AND ITEMS.PART_ID IS NULL";
  
        oracleApis.setItems(itemByInventory);
        
        
        
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
     String querySelectItems = "select distinct part_id, type from items i where 1=1\n" +
"             and type = 'SET'\n" +
"              and part_id like '%-%'\n" +
"               and part_id not in ('LMI2-DE' , 'LMI1-DE')\n" +
    
"              and i.YEAR_RELEASED in ( '2015', '2014')";
    
     
    oApi.setPartOutSet(querySelectItems);
              
    
}    
    
}
