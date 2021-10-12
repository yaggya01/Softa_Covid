package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class Controller {
    public Button logb;
    public Button logo;
    public Button signb;
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
