package sample;

import Message.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Controller {
    public Button logb;
    public Button logo;
    public Button signb;
    public Button getBT;
    public ChoiceBox TimeBox;
    public Button initBT;
    public LineChart Vaccine_chart;
    public void Linit(ActionEvent actionEvent)throws  Exception{
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                String g[]={"All Time","30 Days","One Week"};
                TimeBox.setItems(FXCollections.observableArrayList(g));
            }
        });
    }
    public void BTget(ActionEvent actionEvent)throws Exception{
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(TimeBox.getValue().equals("All Time")){
                    Message m = new Message("","","",0,4);
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
                    Message m = new Message("","","",1,4);
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
                    Message m = new Message("","","",2,4);
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
        stage.setScene(new Scene(root,600, 400));
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
        stage.setScene(new Scene(root,600, 400));
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
        stage.setScene(new Scene(root,600, 400));
    }
}
