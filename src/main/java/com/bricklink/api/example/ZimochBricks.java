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
       
    /*
SimpleDateFormat ss =   new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
 try{

System.out.println(ss.parse("2018-05-17T17:04:19.920Z").getTime());
 }
 catch(ParseException x){
 
     System.out.println("blad");
 }
    */
    MSSQLConnection connection = new MSSQLConnection();
    
    connection.connect();
    connection.disconnect();
    
    
//ZimochBricks zb = new ZimochBricks();
   //
//9984

//zb.getNewParts();
//zb.setPrices();
//zb.refreshInventory();
//zb.setPricesFromSubsets();
//zb.setPrices();
//zb.setPricesByNewInventory();
 // zb.setPricesFromSubsets();
 // zb.setPricesByInventory();
 
 /*
 
BLAPIs bl = new BLAPIs(true);
    
bl.getItemPrice("SET", "76067-1", String.valueOf(0), "stock", "N", "", "EU");
   */            
    
//System.out.println( zb.blApi.getItemPrice("PART", "71040stk01a", "0", "stock", "N", "", "EU"));
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
"and s.part_id in ('8448-1',\n" +
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
"and subs.ISCOUNTERPART = 'N'\n" ;
//+"and not exists(select 1 from price p where p.part_id = subs.subpart_id and p.color_id = subs.color_id and p.country = 'PL')";

String sqlSets = "select part_id, type, 0 as color_id from items i   \n" +
"where i.type like 'SET' \n" +
"and TO_NUMBER(I.YEAR_RELEASED) >  2000"
 ;
     
String sqlByType = "select i.part_id , i.type , kc.COLOR_ID from items i join CATEGORIES c on c.ID = i.category_id join KNOWNCOLORS kc on kc.part_id = i.part_id where c.name in ('Brick','Plate')";

String onePart = "select i.part_id, type, kk.color_id color_id from items i join KNOWNCOLORS kk on kk.part_id = i.part_id\n" +
"where i.part_id in ('3005', '3004',  '3010', '2454', '60593')";
      

String updateInventory = "select i.part_id, type, i.color_id color_id from inventory i join categories c on c.id = i.CATEGORY_ID \n" +
"where c.name in (\n" +
"'Brick','Brick, Arch','Brick, Modified','Brick, Round','Plate','Plate, Modified','Plate, Round','Slope','Slope, Inverted','Slope, Decorated','Wedge')\n" +
"and i.isvalid = 'Y'";

String missingPrices = "select  distinct s.subpart_id as part_id, s.type \n" +
", s.color_id\n" +
"from subsets s \n" +
"join items i on s.part_id = i.part_id \n" +
"\n" +
"where \n" +
"i.type like 'SET' \n" +
"and to_number(i.YEAR_RELEASED) between 2010 and 2014 \n" +
"and not exists(\n" +
"select 1 from price p where p.part_id = s.subpart_id and p.color_id = s.COLOR_ID \n" +
"and p.new_or_used = 'N'\n" +
"and p.isvalid = 'Y'\n" +
"and p.guide_type= 'stock'\n" +
"\n" +
") ";

//OracleAPIs oracleApis = new OracleAPIs();
//oracleApis.setPricesByQuerry(updateInventory, custom_guide_type, custom_new_or_used, custom_country, custom_region);

//setAllPrices(sqlSets);

setPricesByCondition(sqlSets, "stock", "N", "EU", "");

}


void setAllPrices(String querry){
String custom_guide_type= "stock";  //stock or sold
String custom_new_or_used = "U"; //U-used, N-new can be null
String custom_country ="PL"; 
String custom_region= "EU";



OracleAPIs oracleApis = new OracleAPIs();
//oracleApis.setPricesByQuerry(querry, custom_guide_type, custom_new_or_used, custom_country, custom_region);

custom_new_or_used = "N";
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


void setPricesByCondition(String querry, String guide_type, String new_or_used, String region , String country){
    OracleAPIs oracleApis = new OracleAPIs();
    oracleApis.setPricesByQuerry(querry, guide_type, new_or_used, region, country);
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
"             and part_id not in ('LMI2-DE' , 'LMI1-DE')\n" +
"and not exists (select 1 from subsets s where s.part_id = i.part_id) ";
oApi.setPartOutSet(querySelectItems);
}

void getNewPartsDefinition(){

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
"              and i.YEAR_RELEASED in ( '2017', '2018', '2019')";
System.out.println("Parting out new sets");     
oApi.setPartOutSet(querySelectItems);

String newItems = "select distinct part_id from subsets s\n" +
"where not exists (select part_ID from items i where i.part_id = s.part_id) ";
System.out.println("Setting new known colors");   
oApi.setKnownColors(newItems);
System.out.println("Setting new items");   
oApi.setItems(newItems);


}}
