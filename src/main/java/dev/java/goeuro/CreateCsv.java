package dev.java.goeuro;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Logger;


public class CreateCsv {
    private static final String BASE_URL="http://api.goeuro.com/api/v2/position/suggest/en/";
    private static Logger logger = Logger.getLogger(CreateCsv.class.getName());
    private static final File outCsvFile=new File(System.getProperty("user.dir")+"/OutCsvFile.csv");
    private static final String headerCsv="id,name,type,latitude,longitude";


   public static void main(String[] args){
       try{
           if (args.length>1 || args.length==0){
               logger.severe("Invalid, please enter one city name");
               System.exit(-1);
           }
           String city=args[0];
           System.out.println("City entered is: " +city);
           boolean valid=isValid(city);
           if (!valid){
               logger.severe("Only city name allowed, please enter proper city name");
               System.exit(-1);
           }
           String urlstring=makeUrl(city);
           URL url=new URL(urlstring);
           HttpURLConnection request=(HttpURLConnection)url.openConnection();
           JsonParser jp=new JsonParser();
           JsonElement root=jp.parse(new InputStreamReader((InputStream)request.getContent()));
           JsonArray jsonArray=root.getAsJsonArray();
           writeToFile(jsonArray);
       }
       catch(Exception e){
           e.printStackTrace();
           logger.severe("Error in main");
       }
    }

    private static boolean isValid(String city){
        return city.matches("[a-zA-Z]+");
    }

    private static String makeUrl(String city){
        StringBuilder sb=new StringBuilder();
        sb.append(BASE_URL);
        sb.append(city);
        return sb.toString();
    }

    private static void writeToFile(JsonArray jsonArray){
        try{
            String id;
            String name;
            String type;
            String latitude;
            String longitude;
            FileWriter fw=new FileWriter(outCsvFile.getAbsoluteFile());
            BufferedWriter bw=new BufferedWriter(fw);
            bw.write(String.valueOf(headerCsv));
            bw.newLine();
            for(int i=0;i<jsonArray.size();i++){
                id=jsonArray.get(i).getAsJsonObject().get("_id").getAsString();
                name=jsonArray.get(i).getAsJsonObject().get("name").getAsString();
                type=jsonArray.get(i).getAsJsonObject().get("type").getAsString();
                JsonObject position=jsonArray.get(i).getAsJsonObject().getAsJsonObject("geo_position");
                latitude=position.get("latitude").getAsString();
                longitude=position.get("longitude").getAsString();
                bw.write(String.valueOf(id));
                bw.write(",");
                bw.write(String.valueOf(name));
                bw.write(",");
                bw.write(String.valueOf(type));
                bw.write(",");
                bw.write(String.valueOf(latitude));
                bw.write(",");
                bw.write(String.valueOf(longitude));
                bw.flush();
                bw.newLine();
            }
        }
        catch (Exception e){
            e.printStackTrace();
            logger.info("Unable to write to file");
        }
    }


}
