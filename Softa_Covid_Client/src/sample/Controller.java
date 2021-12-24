package sample;

import Message.Message;
import Message.Message_Time_Graph;
import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;

public class Controller {
    public Button logb;
    public Button logo;
    public Button signb;
    public Button getBT;
    public ChoiceBox TimeBox;
    public Button initBT;
    public Button signup_official_button;
    public LineChart Vaccine_chart;
    public PieChart txtbox;
    public TextArea stateTA;
    public TextField stateTF;
    public Button stateBT;
    APIMain.cases[] ar;
    public void stateOnClick(ActionEvent actionEvent)throws Exception {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(stateTF.getText()!=null){
                    for(int i=0;i<ar.length;i++){
                        if(ar[i].provinceState.equals(stateTF.getText().trim())){
                            stateTA.setText("Province: "+ar[i].provinceState+"\n");
                            stateTA.appendText("Country: "+ar[i].countryRegion+"\n");
                            stateTA.appendText("Last Update: "+ar[i].lastUpdate+"\n");
                            stateTA.appendText("Latitude: "+ar[i].lat+"\n");
                            stateTA.appendText("Longitude: "+ar[i].longi+"\n");
                            stateTA.appendText("Confirmed: "+ar[i].confirmed+"\n");
                            stateTA.appendText("Deaths: "+ar[i].deaths+"\n");
                            stateTA.appendText("Recovered: "+ar[i].recovered+"\n");
                            stateTA.appendText("Active: "+ar[i].active+"\n");
                            stateTA.appendText("Incident Rate: "+ar[i].incidentRate+"\n");
                            stateTA.appendText("People Tested: "+ar[i].peopleTested+"\n");
                            stateTA.appendText("People Hospitalized: "+ar[i].peopleHospitalized+"\n");
                            stateTA.appendText("Cases in 28 days: "+ar[i].cases28Days+"\n");
                            stateTA.appendText("Deaths in 28 days: "+ar[i].deaths28Days+"\n");
                        }
                    }
                }
            }
        }).start();
    }

    public void signup_official(ActionEvent actionEvent) {
        System.out.println("SignUP_official");
        Parent root=null;
        Stage stage = (Stage) logb.getScene().getWindow();
        try{
            root = FXMLLoader.load(getClass().getResource("../signup_official/SignUP.fxml"));
        }
        catch (IOException e){
            e.printStackTrace();
        }
        stage.setScene(new Scene(root,600, 400));
    }

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
    public void Linit(ActionEvent actionEvent)throws  Exception{
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                String g[]={"All Time","30 Days","One Week"};
                TimeBox.setItems(FXCollections.observableArrayList(g));

            }
        });
        new Thread(new Runnable() {

            @Override
            public void run() {
                URL urlForGetRequest = null;
                try {
                    urlForGetRequest = new URL("https://covid19.mathdro.id/api/countries/india/confirmed");
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

                        //System.out.println("JSON String Result " + response.toString());
                        ar = new Gson().fromJson(response.toString(), APIMain.cases[].class);
                        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
                        for(int i =0;i<ar.length;i++){
                            System.out.println("State: "+ar[i].provinceState+" Confirmed: "+ar[i].confirmed);
                            pieChartData.add(new PieChart.Data(ar[i].provinceState,Integer.parseInt(ar[i].confirmed)));
                        }
                        txtbox.setData(pieChartData);
                        //GetAndPost.POSTRequest(response.toString());
                    } else {
                        System.out.println("GET NOT WORKED");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).run();
    }
    public void BTget(ActionEvent actionEvent)throws Exception{
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(TimeBox.getValue().equals("All Time")){
                    Message m = new Message("","","","",0,4);
                    try {
                        Socket socket = new Socket("localhost",5400);
                        ObjectOutputStream op = new ObjectOutputStream(socket.getOutputStream());
                        op.writeObject(m);
                        op.flush();
                        ObjectInputStream oi = new ObjectInputStream(socket.getInputStream());
                        Message_Time_Graph k =(Message_Time_Graph) oi.readObject();
                        XYChart.Series series1 = new XYChart.Series();
                        series1.setName("Series 1");
                        for(int i=0;i<k.num;i++){
                            System.out.println(k.a[i][0]+" "+k.a[i][1]);
                            series1.getData().add(new XYChart.Data<>(Integer.toString(i), Integer.parseInt(k.a[i][1])));
                        }
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                Vaccine_chart.getData().add(series1);
                            }
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                else if(TimeBox.getValue().equals("30 Days")){
                    Message m = new Message("","","","",1,4);
                    try {
                        Socket socket = new Socket("localhost",5400);
                        ObjectOutputStream op = new ObjectOutputStream(socket.getOutputStream());
                        op.writeObject(m);
                        op.flush();
                        ObjectInputStream oi = new ObjectInputStream(socket.getInputStream());
                        Message_Time_Graph k =(Message_Time_Graph) oi.readObject();
                        XYChart.Series series1 = new XYChart.Series();
                        series1.setName("Series 1");
                        for(int i=0;i<k.num;i++){
                            System.out.println(k.a[i][0]+" "+k.a[i][1]);
                            series1.getData().add(new XYChart.Data<>(Integer.toString(i), Integer.parseInt(k.a[i][1])));
                        }
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                Vaccine_chart.getData().add(series1);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                else if(TimeBox.getValue().equals("One Week")){
                    Message m = new Message("","","","",2,4);
                    try {
                        Socket socket = new Socket("localhost",5400);
                        ObjectOutputStream op = new ObjectOutputStream(socket.getOutputStream());
                        op.writeObject(m);
                        op.flush();
                        ObjectInputStream oi = new ObjectInputStream(socket.getInputStream());
                        Message_Time_Graph k =(Message_Time_Graph) oi.readObject();
                        XYChart.Series series1 = new XYChart.Series();
                        series1.setName("Series 1");
                        for(int i=0;i<k.num;i++){
                            System.out.println(k.a[i][0]+" "+k.a[i][1]);
                            series1.getData().add(new XYChart.Data<>(Integer.toString(i), Integer.parseInt(k.a[i][1])));
                        }
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                Vaccine_chart.getData().add(series1);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        }).start();
    }
    public void on_log_in(ActionEvent actionEvent)throws Exception{
        System.out.println("LOGIN");
        Parent root=null;
        Stage stage = (Stage) logb.getScene().getWindow();
        try{
            root = FXMLLoader.load(getClass().getResource("../LOGIN_USER/Login_user.fxml"));
        }
        catch (IOException e){
            e.printStackTrace();
        }
        stage.setScene(new Scene(root,950, 740));
    }
    public void on_log_of(ActionEvent actionEvent)throws Exception{
        System.out.println("LOGIN Official");
        Parent root=null;
        Stage stage = (Stage) logb.getScene().getWindow();
        try{
            root = FXMLLoader.load(getClass().getResource("../LOGIN_OFFICIAL/login.fxml"));
        }
        catch (IOException e){
            e.printStackTrace();
        }
        stage.setScene(new Scene(root,950, 740));
    }
    public void on_sign_in(ActionEvent actionEvent)throws Exception{
        System.out.println("SignIN");
        Parent root=null;
        Stage stage = (Stage) logb.getScene().getWindow();
        try{
            root = FXMLLoader.load(getClass().getResource("../SignUP/SignUP.fxml"));
        }
        catch (IOException e){
            e.printStackTrace();
        }
        stage.setScene(new Scene(root,950, 740));
    }
}
