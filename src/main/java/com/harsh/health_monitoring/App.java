package com.harsh.health_monitoring;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Date;
import com.mongodb.client.MongoDatabase;

import org.bson.BSONObject;
import org.bson.Document;
import org.json.*;
import com.mongodb.MongoClient; 
import com.mongodb.MongoCredential;  
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;


public class App 
{
    public static void main( String[] args ) throws Exception
    {
    	SimpleDateFormat dateformat = new SimpleDateFormat("HH");
    	String hour = dateformat.format(new Date()) + "00";
    	
    	StringBuffer nw = new StringBuffer();
    	 // Creating a Mongo client 
       MongoClient mongo = new MongoClient( "localhost" , 27017 ); 
     
        // Creating Credentials 
        MongoCredential credential; 
        
        
        credential = MongoCredential.createCredential("sampleUser", "server", 
           "password".toCharArray()); 
        System.out.println("Connected to the database successfully");  
        
        // Accessing the database 
        MongoDatabase database = mongo.getDatabase("server"); 
        System.out.println("Credentials ::"+ credential);     
        
        
        
    	FileReader fr= new FileReader("./serverlist.txt");
        BufferedReader bufferedReader = new BufferedReader(fr);
        String line;
         
        while ((line = bufferedReader.readLine()) != null) {
            JSONObject json = new JSONObject();
        	
        	//System.out.println("reading");
            String[] words=line.split("\\s");
            //System.out.println(words[2]);
            if(words[1].equals("available")) {
            	database.createCollection(words[2]);

                URL myURL = new URL("http://jcp.jioconnect.com/collectServer?application="+words[2]+"&format=json");
                URLConnection c = myURL.openConnection();
                FileWriter fw=new FileWriter("./logs/log"+words[2]+".json");


                String authStr = Base64.getEncoder()
                        .encodeToString("jcp:Jcp$1234".getBytes());
                c.setRequestProperty("Authorization", "Basic " + authStr);
                
                
                Map<String, List<String>> headers = c.getHeaderFields();
            //    System.out.println(c.getInputStream());
                
                
//			      System.out.println("-- Response headers --");
//			      headers.entrySet()
//			             .forEach(e -> System.out.printf("%s: %s%n", e.getKey(), e.getValue()));
//			      System.out.println("-- Response body --");

                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(c.getInputStream()))) {
                    reader.lines().forEach(t -> {
                     try {
                          // System.out.println(t);
                          fw.write(t+"\n");
                         nw.append(t + "\n");
                       } catch (IOException e1) {
                            // TODO Auto-generated catch block
                           e1.printStackTrace();
                        }
                    });
                    fw.close();
                    //System.out.println(nw);
                    json.put(hour, nw);
                    Document djson = Document.parse(json.toString());
                    database.getCollection(words[2]).insertOne(djson); 
						
								
                    System.out.println(words[2]+" data has been written");
               
                    Thread.sleep(500);

                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
    }
}
    
}
}
    

    		
    		
    		