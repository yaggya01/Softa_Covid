package LOGIN_OFFICIAL;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class dashBoard_official {
    public Button user;

    public void lbUser(ActionEvent actionEvent)throws Exception {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                System.out.println("BACK");
                Parent root=null;
                Stage stage = (Stage) user.getScene().getWindow();
                try{
                    root = FXMLLoader.load(getClass().getResource("./user_vac_status.fxml"));
                }
                catch (IOException e){
                    e.printStackTrace();
                }
                stage.setScene(new Scene(root,300, 275));
            }
        });
    }
}
