package LOGIN_OFFICIAL;


import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.awt.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import Message.*;
import javafx.stage.Stage;
import sample.Controller;

public class Login{
    public Button lbt;
    public Button backBt;
    public TextField UTF;
    public TextField PASSTF;
    public TextField ETF;
    public Socket socket;
//    public Label lb;
    Message_otp m;
    ObjectInputStream oi=null;

    public void lbt(javafx.event.ActionEvent actionEvent)throws Exception {
        System.out.println("official is trying to log in");
        socket = new Socket("localhost",5400);
        ObjectOutputStream op = new ObjectOutputStream(socket.getOutputStream());
        String username=UTF.getText();
        String password = PASSTF.getText();
        String email = ETF.getText();
        op.writeObject(new Message(username,"Name",email,password,0,2));
        System.out.println("official is trying to log in next");
        op.flush();
        try{
            oi = new ObjectInputStream(socket.getInputStream());
            m= (Message_otp) oi.readObject();
            System.out.println("OTP: " + m.otp);
            if(m.otp>2){
                Parent root=null;
                Stage stage = (Stage) lbt.getScene().getWindow();
                try{
                    root = FXMLLoader.load(getClass().getResource("./dashBoard_official.fxml"));
                }
                catch (IOException e){
                    e.printStackTrace();
                }
                stage.setScene(new Scene(root,950, 740));

            }else if(m.otp == 2){
//                lable.setText("Password is not correct");
            }else{
//                lable.setText("username not found");
            }
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }
    public void onClickBack(ActionEvent actionEvent)throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource(
                        "../sample/sample.fxml"
                )
        );

        Stage stage = (Stage) backBt.getScene().getWindow();
//            stage.hide();
        stage.setScene(
                new Scene(loader.load(),950, 740)
        );
        Controller controller = loader.getController();
        controller.initSample();
    }
}
