package LOGIN_OFFICIAL;

import Message.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import sample.Controller;

import java.io.*;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class user_vac_status {
    public Button status_bt;
    public Button updateBT;
    public TextField UTF;
    public TextArea txtarea;
    public Label lable;
    public Button backBt;
    public String username="";
    Status_Message vac;
    Socket socket;
    ObjectOutputStream op;
    ObjectInputStream oi;
    public ChoiceBox UpStat;
    public void BTupdate(ActionEvent actionEvent)throws Exception {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if(username == ""){
                        lable.setText("Give username");
                    }else{
                        Message m = new Message(username, "", "", "", 0, 3);
                        socket=new Socket("localhost",5400);
                        op= new ObjectOutputStream(socket.getOutputStream());
                        op.writeObject(m);
                        oi = new ObjectInputStream(socket.getInputStream());
                        Status_updated status_update = (Status_updated) oi.readObject();
                        System.out.println(status_update);
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                lable.setText(String.valueOf(status_update));
                            }
                        });
//                        Platform.runLater(new Runnable() {
//                            @Override
//                            public void run() {
//                                if(vac.v.size()>0){
//                                    Platform.runLater(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            for(String a: vac.v){
//                                                txtarea.appendText(a + "\n");
//                                            }
//                                        }
//                                    });
//                                }
//                            }
//
//                        });
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    public void btStatus(ActionEvent actionEvent)throws Exception{

        new Thread(new Runnable() {
            @Override
            public void run() {
                username = UTF.getText();
                Message m = new Message(username,"","","",0, 7);
                try {
                    socket=new Socket("localhost",5400);
                    op= new ObjectOutputStream(socket.getOutputStream());
                    op.writeObject(m);
                    oi = new ObjectInputStream(socket.getInputStream());
                    vac=(Status_Message) oi.readObject();
//                    System.out.println(vac);
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            if(vac.v.size()>0){
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        for(String a: vac.v){
                                            txtarea.appendText(a + "\n");
                                        }
                                    }
                                });
                            }
                        }

                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        }).start();
    }

    public void onclickBack(ActionEvent actionEvent)throws Exception {
        Parent root=null;
        Stage stage = (Stage) backBt.getScene().getWindow();
        try{
            root = FXMLLoader.load(getClass().getResource("./dashBoard_official.fxml"));
        }
        catch (IOException e){
            e.printStackTrace();
        }
        stage.setScene(new Scene(root,950, 740));
    }
}
