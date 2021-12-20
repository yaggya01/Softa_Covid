package sample;
import com.google.gson.Gson;
import netscape.javascript.JSObject;

import java.io.*;
import java.util.*;
import java.net.*;

public class APIMain {
    static class cases{
        String provinceState;
        String countryRegion;
        String lastUpdate;
        String lat;
        String longi;
        String confirmed;
        String deaths;
        String recovered;
        String active;
        String admin2;
        String fips;
        String combinedKey;
        String incidentRate;
        String peopleTested;
        String peopleHospitalized;
        String uid;
        String iso3;
        String cases28Days;
        String deaths28Days;
        public cases(String a,String b,String c,String d,String e,String f,String g,String h,String i,String j,String k,String l,String m,String n,String o,String p,String q,String r,String s){
            provinceState=a;
            countryRegion=b;
            lastUpdate=c;
            lat=d;
            longi=e;
            confirmed=f;
            deaths=g;
            recovered=h;
            active=i;
            admin2=j;
            fips=k;
            combinedKey=l;
            incidentRate=m;
            peopleTested=n;
            peopleHospitalized=o;
            uid=p;
            iso3=q;
            cases28Days=r;
            deaths28Days=s;
        }
    }
    public static void main(String args[]) throws IOException, InterruptedException {
        URL urlForGetRequest = new URL("https://covid19.mathdro.id/api/countries/india/confirmed");
        String readLine = null;
        HttpURLConnection conection = (HttpURLConnection) urlForGetRequest.openConnection();
        conection.setRequestMethod("GET");
        conection.setRequestProperty("userId", "a1bcdef"); // set userId its a sample here
        int responseCode = conection.getResponseCode();


        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(conection.getInputStream()));
            StringBuffer response = new StringBuffer();
            while ((readLine = in .readLine()) != null) {
                response.append(readLine);
            } in .close();
            // print result

            System.out.println("JSON String Result " + response.toString());
            /*cases[] ar = new Gson().fromJson(response.toString(),cases[].class);
            for(int i =0;i<ar.length;i++){
                System.out.println("State: "+ar[i].provinceState+" Confirmed: "+ar[i].confirmed +" death: "+ar[i].deaths);
            }*/
            //GetAndPost.POSTRequest(response.toString());
        } else {
            System.out.println("GET NOT WORKED");
        }

    }
}