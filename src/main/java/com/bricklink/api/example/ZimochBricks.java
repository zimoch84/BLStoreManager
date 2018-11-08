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

    OracleAPIs oApi;
    BLAPIs blApi;

    public ZimochBricks() {
    
    oApi = new OracleAPIs();
    blApi = new BLAPIs();
    }
    
    
      
public static void main( String[] args ) {
       
ZimochBricks zb = new ZimochBricks();
   //
//9984

//zb.getNewParts();
zb.setPrices();
//zb.refreshInventory();
//zb.setPricesFromSubsets();
//zb.setPrices();
//zb.setPricesByNewInventory();
    //zb.getItems();
 // zb.setPricesByInventory();
 
 
 
  //  BLAPIs bl = new BLAPIs();
               
    
   //  System.out.println( bl.getItemPrice("PART", "48729b", "86", "sold", "U", "PL", "EU"));
   }
    
void refreshInventory()
{
    OracleAPIs oracleApis = new OracleAPIs();
    oracleApis.refreshInventoryDatabase();
}

void setPricesFromSubsets(){
       
String subpartsBySets = "select distinct s.subpart_id as part_id, s.color_id , i.type\n" +
"from items i join SUBSETS s on s.subpart_id = i.PART_ID and s.iscounterpart = 'N' \n" +
"and s.isalternate = 'N' \n" +
"where 1=1\n" +
"and s.part_id in ('21136-1',\n" +
"'21310-1',\n" +
"'10255-1',\n" +
"'79018-1',\n" +
"'10251-1',\n" +
"'10241-1',\n" +
"'70612-1',\n" +
"'21030-1',\n" +
"'76085-1',\n" +
"'75060-1',\n" +
"'70620-1',\n" +
"'21028-1')";
setAllPrices(subpartsBySets);
}
   
   
void setPrices()
{
     //BLAPIs blapis = new BLAPIs();
     //JSONObject responseItem=blapis.brickLinkApiItem("part", "2335", "11", "stock", "U", "PL", "EU");
     //System.err.println(responseItem.toString());
        
String custom_guide_type= "stock";  //stock or sold
String custom_new_or_used = "N"; //U-used, N-new can be null
String custom_country ="PL"; 
String custom_region= "EU";

String priceInventory  = "select PART_ID, TYPE, COLOR_ID from inventory i where  i.DATE_CREATED >= sysdate - 2  and i.ISVALID = 'Y' ";

String priceInventoryStockRoom  = "select PART_ID, TYPE, COLOR_ID from inventory i where IS_STOCK_ROOM like 'true' and isvalid = 'Y'";


String priceInventory2 = "select PART_ID, TYPE, COLOR_ID from inventory i where  i.DATE_CREATED >= sysdate - 1  and i.ISVALID = 'Y' and unit_price = 4 \n"
              + "OR \n" +
"(\n" +
"INV_HASCHANGEDQTY(i.PART_ID, i.COLOR_ID,i.QUANTITY) =1  \n" +
"and i.isvalid = 'Y'\n" +

")"
        + "" ;
                 //  "not exists (select 1 from price p where p.part_id like i.part_id and p.color_id = i.color_id and country = 'PL' and region = 'EU' and new_or_used='N' )" ;

String pricesByKnownColors = "select k.PART_ID, k.COLOR_ID , i.type "
        + "from KNOWNCOLORS  k join ITEMS i on i.PART_ID = k.PART_ID join CATEGORIES c on c.ID = i.CATEGORY_ID\n" +
"\n" +
"where not exists (select 1 from PRICE p2 where p2.COLOR_ID = k.COLOR_ID and p2.PART_ID = k.PART_ID)\n" +
"\n" +
"and rownum < 20000" ;

String subpartsBySets = "select  distinct subs.subpart_id as part_id ,subs.type , subs.color_id\n" +
"from items sts \n" +
"join subsets subs on subs.PART_ID = sts.part_id\n" +
"where sts.type = 'SET' \n" +
"and sts.YEAR_RELEASED in ( '2015', '2016', '2017', '2018' ) \n" +
"and subs.ISCOUNTERPART = 'N'\n" +
"and not exists(select 1 from price p where p.part_id = subs.subpart_id and p.color_id = subs.color_id)";

String sqlSets = "select part_id, type, 0 as color_id from items i  \n" +
"where i.type like 'SET'  \n" +
"and not exists (select 1 from price p3 where p3.part_ID = i.part_id)"
 ;
     
String sqlByType = "select i.part_id , i.type , kc.COLOR_ID from items i join CATEGORIES c on c.ID = i.category_id join KNOWNCOLORS kc on kc.part_id = i.part_id where c.name in ('Brick','Plate')";

String onePart = "select i.part_id, type, kk.color_id color_id from items i join KNOWNCOLORS kk on kk.part_id = i.part_id\n" +
"where i.part_id in ('3005', '3004',  '3010', '2454', '60593')";
      

String updateInventory = "select i.part_id, type, i.color_id color_id from inventory i join categories c on c.id = i.CATEGORY_ID \n" +
"where c.name in (\n" +
"'Brick','Brick, Arch','Brick, Modified','Brick, Round','Plate','Plate, Modified','Plate, Round','Slope','Slope, Inverted','Slope, Decorated','Wedge')\n" +
"and i.isvalid = 'Y'";

//OracleAPIs oracleApis = new OracleAPIs();
//oracleApis.setPricesByQuerry(updateInventory, custom_guide_type, custom_new_or_used, custom_country, custom_region);

setAllPrices(subpartsBySets);
}


void setAllPrices(String querry){
String custom_guide_type= "stock";  //stock or sold
String custom_new_or_used = "U"; //U-used, N-new can be null
String custom_country ="PL"; 
String custom_region= "EU";



OracleAPIs oracleApis = new OracleAPIs();
oracleApis.setPricesByQuerry(querry, custom_guide_type, custom_new_or_used, custom_country, custom_region);

custom_new_or_used = "U";
oracleApis.setPricesByQuerry(querry, custom_guide_type, custom_new_or_used, custom_country, custom_region);

custom_new_or_used = "U"; 
custom_country =""; 
custom_region= "EU";
oracleApis.setPricesByQuerry(querry, custom_guide_type, custom_new_or_used, custom_country, custom_region);

custom_new_or_used = "N"; 
custom_country =""; 
custom_region= "EU";
oracleApis.setPricesByQuerry(querry, custom_guide_type, custom_new_or_used, custom_country, custom_region);


}

void setPricesByNewInventory()
{
        
String custom_guide_type= "stock";  //stock or sold
String custom_new_or_used = "N"; //U-used, N-new can be null
String custom_country ="PL"; 
String custom_region= "EU";

String priceInventory = "select PART_ID, TYPE, COLOR_ID from inventory i where  i.DATE_CREATED >= sysdate - 4  and i.ISVALID = 'Y' and unit_price = 4 \n" ;

OracleAPIs oracleApis = new OracleAPIs();
oracleApis.setPricesByQuerry(priceInventory, custom_guide_type, custom_new_or_used, custom_country, custom_region);

custom_new_or_used = "U";
oracleApis.setPricesByQuerry(priceInventory, custom_guide_type, custom_new_or_used, custom_country, custom_region);

custom_new_or_used = "U"; 
custom_country =""; 
custom_region= "EU";
oracleApis.setPricesByQuerry(priceInventory, custom_guide_type, custom_new_or_used, custom_country, custom_region);

custom_new_or_used = "N"; 
custom_country =""; 
custom_region= "EU";
oracleApis.setPricesByQuerry(priceInventory, custom_guide_type, custom_new_or_used, custom_country, custom_region);


}

void getColors() {
    JSONObject colors = blApi.getColors();
    oApi.setColors(colors);
    System.out.println(colors.toString());
}
    
void getCategories(){
    JSONObject categories = blApi.getCategories();
    oApi.setCategories(categories);
}
 void setKnownColors(){
     String querySelectItems = "select distinct part_id, type from items i where rownum < 2";
     oApi.setKnownColors(querySelectItems);
}
void getItems()
{
/*  String querySelectItems = "select distinct part_id, type from items i where 1=1"
          +"and type = 'SET' and part_id  like '%-%' and year_released is null and part_id not in ('LMI2-DE', 'LMI1-DE') ";
  */      
String querySelectItems = "select distinct part_id, 'SET' as type from items i where 1=1"
         +"and type is null";

String itemByInventory = "select DISTINCT INV.PART_ID,'PART' as type from inventory  INV LEFT OUTER JOIN ITEMS ON INV.PART_ID = ITEMS.PART_ID\n" +
             "WHERE AND ITEMS.PART_ID IS NULL";

 oApi.setItems(itemByInventory);
        
}

void getSuperSets(){

String querySelectItems = "select distinct part_id, type from items i  join categories c on c.ID = i.CATEGORY_ID where 1=1 and type in 'PART'\n" +
"and c.name in ('Plate' ,'Tile', 'Brick', 'Slope') \n" +
"and not regexp_like(part_id, '[A-Z]')";
     
oApi.setSetsFromParts(querySelectItems);
    
}
void getSubSets(){
String querySelectItems = "select distinct part_id, type from items i where 1=1\n" +
 "             and type = 'SET'\n" +
"              and part_id like '%-%'\n" +
"              and part_id not in ('LMI2-DE' , 'LMI1-DE')\n" +
"              and i.YEAR_RELEASED in ( '2017', '2018')";
oApi.setPartOutSet(querySelectItems);
}

void getNewParts(){

String querySelectItems = "select distinct part_id, type from items i  join categories c on c.ID = i.CATEGORY_ID where 1=1 and type in 'PART'\n" +
"and c.name in ('Plate' ,'Tile', 'Brick', 'Slope') \n" +
"and not regexp_like(part_id, '[A-Z]')";

System.out.println("getting parts from sets");     
oApi.setSetsFromParts(querySelectItems);

String newSets = "select distinct part_id, type from items i where 1=1"
          +"and type = 'SET' and part_id  like '%-%' and year_released is null and part_id not in ('LMI2-DE', 'LMI1-DE') ";
System.out.println("Setting items for new sets");     
oApi.setItems(newSets);

querySelectItems = "select distinct part_id, type from items i where 1=1\n" +
 "             and type = 'SET'\n" +
"              and part_id like '%-%'\n" +
"              and part_id not in ('LMI2-DE' , 'LMI1-DE')\n" +
"              and i.YEAR_RELEASED in ( '2017', '2018')";
System.out.println("Parting out new sets");     
oApi.setPartOutSet(querySelectItems);

String newItems = "select distinct part_id from subsets s\n" +
"where not exists (select part_ID from items i where i.part_id = s.part_id) ";
System.out.println("Setting new known colors");   
oApi.setKnownColors(newItems);
System.out.println("Setting new items");   
oApi.setItems(newItems);


}}
