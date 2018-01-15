package com.bricklink.api.example;

import java.net.MalformedURLException;

import java.util.Collections;
import java.util.Map;
import java.net.URL;

import java.util.HashMap;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONObject;

public class BLAuth {
 public Map<String, String> parameters;
    public BLAuth() {
        parameters=new HashMap();
        
    }
        
        
    
       
    
        String baseUrl= "";

        public String getBaseUrl() {
            return  baseUrl;
        }

        public void setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
        }
   	
        
        
	       
        String getAuth10()
                
        {
        
                String consumerKey = "E5850ABFAC43423E80FADEB085F2329B";
		String consumerSecret = "4C6D415EBFE64FA78E8C12077C29ACD9";
		String tokenValue = "CDD4770F393A4088818560C5D842774E";
		String tokenSecret = "489FDDE9FD8C4DB0840846D245E0D054";

		BLAuthSigner signer = new BLAuthSigner( consumerKey, consumerSecret );
		signer.setToken( tokenValue, tokenSecret );
		signer.setVerb( "GET" );
                signer.setURL( baseUrl );
                for (Map.Entry<String, String> entry : parameters.entrySet())
                    {
                        signer.addParameter(entry.getKey() , entry.getValue());
                    }
		Map<String, String> params = Collections.emptyMap();
		try {
			params = signer.getFinalOAuthParams();
		} catch( Exception e ) {
			e.printStackTrace();
		}
		JSONObject obj = new JSONObject();
		obj.putAll( params );
		return obj.toJSONString();
        }
        URL getUrl()
        {
            String paramUrl = "";
            for (Map.Entry<String, String> entry : parameters.entrySet())
                    {
                        paramUrl += entry.getKey() +"="+ entry.getValue()+"&";
                    }
            URL url;
            try {
                url = new URL(baseUrl + "?"+paramUrl+"Authorization=" + getAuth10() );
                return url;
            } catch (MalformedURLException ex) {
                Logger.getLogger(BLAuth.class.getName()).log(Level.SEVERE, null, ex);
            }
            return null;
            
        }
}
