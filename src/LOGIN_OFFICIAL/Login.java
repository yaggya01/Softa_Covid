package LOGIN_OFFICIAL;


import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import Message.*;
import javafx.stage.Stage;

public class Login{
    public Button lbt;
    public TextField UTfield;
    public TextField ETfield;
    public TextField OTfield;
    public Socket socket;
    Message_otp m;
    ObjectInputStream oi=null;

    public void lb(javafx.event.ActionEvent actionEvent)throws Exception {
        System.out.println("hello");
        socket = new Socket("localhost",5400);
        ObjectOutputStream op = new ObjectOutputStream(socket.getOutputStream());
        String name=UTfield.getText();
        String password = ETfield.getText();
        String email = OTfield.getText();
        op.writeObject(new Message(name,password,email,0,2));
        op.flush();
        try{
            oi = new ObjectInputStream(socket.getInputStream());
            m= (Message_otp) oi.readObject();
            System.out.println("OTP: " + m.otp);
            if(m.otp==0){
                Parent root=null;
                Stage stage = (Stage) lbt.getScene().getWindow();
                try{
                    root = FXMLLoader.load(getClass().getResource("./dashBoard_official.fxml"));
                }
                catch (IOException e){
                    e.printStackTrace();
                }
                stage.setScene(new Scene(root,600, 400));
            }
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }
}
